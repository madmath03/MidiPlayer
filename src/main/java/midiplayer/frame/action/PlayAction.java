package midiplayer.frame.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import jswingshell.IJssController;
import jswingshell.action.AbstractJssAction;
import midiplayer.MidiPlayer;
import midiplayer.frame.MidiPlayerWithListener;
import midiplayer.resources.LocaleChangeListener;
import midiplayer.resources.ResourceUtils;

/**
 * Action to play a MIDI song.
 *
 * @author Mathieu Brunot
 */
public final class PlayAction extends AbstractJssAction
    implements LocaleChangeListener, PropertyChangeListener {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = -1587535528814856246L;

  /**
   * Logger.
   */
  private static final Logger LOGGER =
      Logger.getLogger(PlayAction.class.getName());

  /**
   * This action default identifier.
   *
   * @since 1.2
   */
  public static final String DEFAULT_IDENTIFIER = "playSong";

  private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

  private static final String ACTION_LABEL = "Play";

  private static final String ACTION_LABEL_KEY = "midiplayer.action.play.name";

  private static final String COMMAND_BRIEF_HELP =
      "Play the current MIDI song.";

  private static final String COMMAND_BRIEF_HELP_KEY =
      "midiplayer.action.play.help.short";

  private static final String COMMAND_HELP_KEY =
      "midiplayer.action.play.help.long";

  private static final String COMMAND_RUN_FILE_PATH_INVALID_KEY =
      "midiplayer.action.play.run.file_path_invalid";

  private static final String ICON_KEY = "control_play_blue.png";

  private static String commandHelp;

  private static boolean commandHelpInitialized = false;

  private static String commandBriefHelp;

  private static boolean commandBriefHelpInitialized = false;

  /**
   * Construct the static command help.
   *
   * @param action the action reference
   *
   * @return the static command help.
   */
  public static final String getHelp(PlayAction action) {
    if (!commandHelpInitialized && action != null) {
      StringBuilder stringBuilder = new StringBuilder();

      String commandIdsAsString = action.getCommandIdentifiersAsString();
      stringBuilder.append(action.getBriefHelp());
      stringBuilder.append("\n");
      try {
        stringBuilder.append(
            ResourceUtils.getMessage(COMMAND_HELP_KEY, commandIdsAsString));
      } catch (MissingResourceException e) {
        LOGGER.log(Level.SEVERE,
            "Resource not found: \"" + COMMAND_HELP_KEY + "\"", e);
        stringBuilder.append("\n")
            .append("Plays the current song in the playlist:");
        stringBuilder.append("\n\t").append(commandIdsAsString);
        stringBuilder.append("\n").append(
            "You can specify the song to play by giving the song index in playlist as follow:");
        stringBuilder.append("\n\t").append(commandIdsAsString)
            .append(" [integer] ");
        stringBuilder.append("\n").append(
            "You can also specify the song to play by giving its path as follow:");
        stringBuilder.append("\n\t").append(commandIdsAsString)
            .append(" [path] ");
      }

      commandHelp = stringBuilder.toString();
      commandHelpInitialized = true;
    }
    return commandHelp;
  }

  /**
   * Construct the static command brief help.
   *
   * @param action the action reference
   *
   * @return the static command brief help.
   */
  public static final String getBriefHelp(PlayAction action) {
    if (!commandBriefHelpInitialized && action != null) {
      try {
        commandBriefHelp = ResourceUtils.getMessage(COMMAND_BRIEF_HELP_KEY);
      } catch (MissingResourceException e) {
        LOGGER.log(Level.SEVERE,
            "Resource not found: \"" + COMMAND_BRIEF_HELP_KEY + "\"", e);
        commandBriefHelp = COMMAND_BRIEF_HELP;
      }
      commandBriefHelpInitialized = true;
    }
    return commandBriefHelp;
  }

  /**
   * Reset the static help to force reconstruction on next call.
   *
   * @since 1.4
   */
  public static final void resetHelp() {
    commandHelpInitialized = false;
    commandHelp = null;
    commandBriefHelpInitialized = false;
    commandBriefHelp = null;
  }

  // #########################################################################
  private transient MidiPlayer midiPlayer;

  public PlayAction(MidiPlayer midiPlayer, IJssController shellController,
      String... args) {
    super(ACTION_LABEL, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL),
        shellController, args);
    if (midiPlayer == null) {
      throw new IllegalArgumentException("Midi player is null");
    }
    this.midiPlayer = midiPlayer;
    putValue(Action.ACCELERATOR_KEY,
        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD5, 0));
    putValue(Action.LARGE_ICON_KEY,
        ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL, true));
    putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
    localeChanged();
  }

  public PlayAction(MidiPlayer midiPlayer, IJssController shellController) {
    this(midiPlayer, shellController, (String[]) null);
  }

  public PlayAction(MidiPlayer midiPlayer) {
    this(midiPlayer, null, (String[]) null);
  }

  public MidiPlayer getMidiPlayer() {
    return midiPlayer;
  }

  public void setMidiPlayer(MidiPlayer midiPlayer) {
    this.midiPlayer = midiPlayer;
  }

  // #########################################################################
  @Override
  public String[] getCommandIdentifiers() {
    return IDENTIFIERS;
  }

  @Override
  public String getBriefHelp() {
    return getBriefHelp(this);
  }

  @Override
  public String getHelp(IJssController shellController) {
    return getHelp(this);
  }

  @Override
  public int run(IJssController shellController, String... args) {
    int commandReturnStatus = AbstractJssAction.ERROR;

    // If no parameter given, just start playing from current position
    if (args == null || args.length <= 1) {
      if (midiPlayer.startPlaying()) {
        commandReturnStatus = AbstractJssAction.SUCCESS;
      }
    } else if (args.length == 2) {
      // Try to start playing song at index given
      try {
        int index = Integer.parseInt(args[1]);
        if (midiPlayer.startPlaying(index)) {
          commandReturnStatus = AbstractJssAction.SUCCESS;
        }
      } catch (NumberFormatException e) {
        LOGGER.log(Level.WARNING, "Invalid number format: " + args[1], e);

        // Let's assume the first argument is a path and try again...
        Path songPath = Paths.get(args[1]);
        if (Files.exists(songPath, LinkOption.NOFOLLOW_LINKS)) {
          if (midiPlayer.startPlaying(songPath)) {
            commandReturnStatus = AbstractJssAction.SUCCESS;
          }
        } else {
          LOGGER.log(Level.WARNING, "No file was found at path: " + songPath,
              e);
          String msg;
          try {
            msg = ResourceUtils.getMessage(COMMAND_RUN_FILE_PATH_INVALID_KEY,
                songPath);
          } catch (MissingResourceException e1) {
            LOGGER.log(Level.SEVERE, "Resource not found: \""
                + COMMAND_RUN_FILE_PATH_INVALID_KEY + "\"", e1);
            msg = "No file was found at path: " + songPath;
          }
          shellController.publish(IJssController.PublicationLevel.ERROR, msg);
        }
      }
    } else if (args.length > 2 && shellController != null) {
      shellController.publish(IJssController.PublicationLevel.WARNING,
          getHelp(shellController));
    }

    return commandReturnStatus;
  }

  @Override
  protected String[] extractArgumentsFromEvent(ActionEvent e) {
    String[] eventArgs = null;

    if (e != null) {
      if (e.getSource() instanceof JTable) {
        String commandIdentifier = getDefaultCommandIdentifier();
        String command = e.getActionCommand();
        try {
          Integer row = Integer.parseInt(command);
          eventArgs = new String[] {commandIdentifier, row.toString()};
        } catch (NumberFormatException nfex) {
          LOGGER.log(Level.SEVERE, "Not a valid integer: \"" + command + "\"",
              nfex);
        }
      }
    }

    return eventArgs;
  }

  // #########################################################################
  @Override
  public void localeChanged() {
    localeChanged(null);
  }

  @Override
  public void localeChanged(PropertyChangeEvent evt) {
    resetHelp();
    try {
      ResourceUtils.setTextAndMnemonic(this, ACTION_LABEL_KEY);
    } catch (MissingResourceException e) {
      LOGGER.log(Level.SEVERE,
          "Resource not found: \"" + ACTION_LABEL_KEY + "\"", e);
      putValue(Action.NAME, ACTION_LABEL);
    }
    putValue(Action.SHORT_DESCRIPTION, this.getBriefHelp());
    putValue(Action.LONG_DESCRIPTION,
        this.getHelp(this.getDefaultShellController()));
  }

  // #########################################################################
  @Override
  public final void putValue(String key, Object newValue) {
    super.putValue(key, newValue);
  }

  @Override
  public final String getDefaultCommandIdentifier() {
    return super.getDefaultCommandIdentifier();
  }

  // #########################################################################
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt == null) {
      return;
    }

    Object newValue = evt.getNewValue();
    Object oldValue = evt.getOldValue();
    switch (evt.getPropertyName()) {
      case MidiPlayerWithListener.PLAYLIST_SIZE_CHANGE:
        if (newValue instanceof Integer && oldValue instanceof Integer) {
          Integer newSize = (Integer) newValue;
          Integer oldSize = (Integer) oldValue;
          if (oldSize == 0 || newSize == 0) {
            this.setEnabled(newSize > 0 && !midiPlayer.isPlaying());
          }
        }
        break;
      case MidiPlayerWithListener.PLAYING_STOP_CHANGE:
        if (newValue instanceof Boolean) {
          Boolean isStopped = (Boolean) newValue;
          this.setEnabled(isStopped);
        }
        break;
      case MidiPlayerWithListener.PLAYING_START_CHANGE:
        if (newValue instanceof Boolean) {
          Boolean isPlaying = (Boolean) newValue;
          this.setEnabled(!isPlaying);
        }
        break;
    }
  }

}
