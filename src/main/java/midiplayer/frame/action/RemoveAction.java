package midiplayer.frame.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JTable;

import jswingshell.IJssController;
import jswingshell.action.AbstractJssAction;
import midiplayer.MidiPlayer;
import midiplayer.frame.MidiPlayerFrame;
import midiplayer.frame.MidiPlayerWithListener;
import midiplayer.resources.LocaleChangeListener;
import midiplayer.resources.ResourceUtils;

/**
 * Action to remove a MIDI song from playlist.
 *
 * @author Mathieu Brunot
 */
public final class RemoveAction extends AbstractJssAction
    implements LocaleChangeListener, PropertyChangeListener {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = 6810665401981415588L;

  /**
   * Logger.
   */
  private static final Logger LOGGER =
      Logger.getLogger(RemoveAction.class.getName());

  /**
   * This action default identifier.
   *
   * @since 1.2
   */
  public static final String DEFAULT_IDENTIFIER = "removeSong";

  private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

  private static final String ACTION_LABEL = "Remove";

  private static final String ACTION_LABEL_KEY =
      "midiplayer.action.remove.name";

  private static final String COMMAND_BRIEF_HELP =
      "Remove the MIDI song from playlist.";

  private static final String COMMAND_BRIEF_HELP_KEY =
      "midiplayer.action.remove.help.short";

  private static final String COMMAND_HELP_KEY =
      "midiplayer.action.remove.help.long";

  private static final String ICON_KEY = "delete.png";

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
  public static final String getHelp(RemoveAction action) {
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
            .append("Remove the song at given index from the playlist:");
        stringBuilder.append("\n\t").append(commandIdsAsString)
            .append(" [integer] ");
        stringBuilder.append("\n")
            .append("You can also specify several songs to remove:");
        stringBuilder.append("\n\t").append(commandIdsAsString)
            .append(" [integer1] [integer2] [integer3] [integer4] ... ");
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
  public static final String getBriefHelp(RemoveAction action) {
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

  private transient MidiPlayerFrame midiPlayerFrame;

  public RemoveAction(MidiPlayer midiPlayer, IJssController shellController,
      String... args) {
    super(ACTION_LABEL, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL),
        shellController, args);
    if (midiPlayer == null) {
      throw new IllegalArgumentException("Midi player is null");
    }
    this.midiPlayer = midiPlayer;
    putValue(Action.LARGE_ICON_KEY,
        ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL, true));
    putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
    localeChanged();
  }

  public RemoveAction(MidiPlayer midiPlayer, IJssController shellController) {
    this(midiPlayer, shellController, (String[]) null);
  }

  public RemoveAction(MidiPlayer midiPlayer) {
    this(midiPlayer, null, (String[]) null);
  }

  public MidiPlayer getMidiPlayer() {
    return midiPlayer;
  }

  public void setMidiPlayer(MidiPlayer midiPlayer) {
    this.midiPlayer = midiPlayer;
  }

  public MidiPlayerFrame getMidiPlayerFrame() {
    return midiPlayerFrame;
  }

  public void setMidiPlayerFrame(MidiPlayerFrame midiPlayerFrame) {
    this.midiPlayerFrame = midiPlayerFrame;
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

    if (args == null || args.length <= 1) {
      if (shellController != null) {
        shellController.publish(IJssController.PublicationLevel.WARNING,
            getHelp(shellController));
      }
    } else {
      String[] songIndexes = Arrays.copyOfRange(args, 1, args.length);
      int[] indexes = new int[songIndexes.length];
      for (int i = 0, n = songIndexes.length; i < n; i++) {
        String indexStr = songIndexes[i];
        try {
          indexes[i] = Integer.parseInt(indexStr);
        } catch (NumberFormatException nfex) {
          LOGGER.log(Level.SEVERE, "Not a valid integer: \"" + indexStr + "\"",
              nfex);
        }
      }

      if (midiPlayer.removeAll(indexes)) {
        commandReturnStatus = AbstractJssAction.SUCCESS;
      }
    }

    return commandReturnStatus;
  }

  @Override
  protected String[] extractArgumentsFromEvent(ActionEvent e) {
    String[] eventArgs = null;

    String commandIdentifier = getDefaultCommandIdentifier();
    if (commandIdentifier != null && e != null) {
      JTable tablePlaylist = null;
      if (e.getSource() instanceof JTable) {
        tablePlaylist = (JTable) e.getSource();
      } else if (midiPlayerFrame != null) {
        tablePlaylist = midiPlayerFrame.getTablePlaylist();
      }

      if (tablePlaylist != null) {
        // Remove songs starting from the last ones selected
        int[] selectedRows = tablePlaylist.getSelectedRows();
        if (selectedRows != null && selectedRows.length > 0) {
          eventArgs = new String[1 + selectedRows.length];
          eventArgs[0] = commandIdentifier;
          for (int rowIndex = selectedRows.length - 1, argIndex =
              1; rowIndex >= 0; rowIndex--, argIndex++) {
            eventArgs[argIndex] = Integer.toString(selectedRows[rowIndex]);
          }
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
            this.setEnabled(newSize > 0);
          }
        }
        break;
    }
  }

}
