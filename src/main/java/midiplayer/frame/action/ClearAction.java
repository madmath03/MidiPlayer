package midiplayer.frame.action;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;

import jswingshell.IJssController;
import jswingshell.action.AbstractJssAction;
import midiplayer.MidiPlayer;
import midiplayer.frame.MidiPlayerWithListener;
import midiplayer.resources.LocaleChangeListener;
import midiplayer.resources.ResourceUtils;

/**
 * Action to clear the playlist.
 *
 * @author Mathieu Brunot
 */
public final class ClearAction extends AbstractJssAction
    implements LocaleChangeListener, PropertyChangeListener {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = -3485681133544567875L;

  /**
   * Logger.
   */
  private static final Logger LOGGER =
      Logger.getLogger(ClearAction.class.getName());

  /**
   * This action default identifier.
   *
   * @since 1.2
   */
  public static final String DEFAULT_IDENTIFIER = "clearPlaylist";

  private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

  private static final String ACTION_LABEL = "Clear";

  private static final String ACTION_LABEL_KEY = "midiplayer.action.clear.name";

  private static final String COMMAND_BRIEF_HELP = "Clear the playlist.";

  private static final String COMMAND_BRIEF_HELP_KEY =
      "midiplayer.action.clear.help.short";

  private static final String COMMAND_HELP_KEY =
      "midiplayer.action.clear.help.long";

  private static final String ICON_KEY = "broom.png";

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
  public static final String getHelp(ClearAction action) {
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
        stringBuilder.append("\n").append("Clears the playlist:");
        stringBuilder.append("\n\t").append(commandIdsAsString);
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
  public static final String getBriefHelp(ClearAction action) {
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

  public ClearAction(MidiPlayer midiPlayer, IJssController shellController,
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

  public ClearAction(MidiPlayer midiPlayer, IJssController shellController) {
    this(midiPlayer, shellController, (String[]) null);
  }

  public ClearAction(MidiPlayer midiPlayer) {
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

    if (midiPlayer.clear()) {
      commandReturnStatus = AbstractJssAction.SUCCESS;
    } else if (shellController != null) {
      shellController.publish(IJssController.PublicationLevel.WARNING,
          getHelp(shellController));
    }

    return commandReturnStatus;
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
