package midiplayer.console.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;

import jswingshell.IJssController;
import midiplayer.console.resources.ResourceUtils;
import midiplayer.resources.LocaleChangeListener;

/**
 * Action to mark the end of a shell recording session.
 *
 * <p>
 * This action encapsulates a {@link RecordAction} and initializes GUI related properties:
 * </p>
 * <ul>
 * <li>{@code Action.NAME}</li>
 * <li>{@code Action.SMALL_ICON}</li>
 * <li>{@code Action.LARGE_ICON_KEY}</li>
 * <li>{@code Action.SHORT_DESCRIPTION}</li>
 * <li>{@code Action.LONG_DESCRIPTION}</li>
 * </ul>
 *
 * @author Mathieu Brunot
 */
public final class RecordStopAction extends AbstractAction implements LocaleChangeListener {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = 3426851641761542144L;

  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger.getLogger(RecordStopAction.class.getName());

  private static final String ACTION_LABEL = "Stop recording";

  private static final String ACTION_LABEL_KEY = "midi_player.console.action.record_stop.name";

  private static final String ICON_KEY = "control_stop_blue.png";

  private static final String COMMAND_BRIEF_HELP = "Mark the end of a shell recording session.";

  private static final String COMMAND_BRIEF_HELP_KEY =
      "midi_player.console.action.record_stop.help.short";

  private static String commandBriefHelp;

  private static boolean commandBriefHelpInitialized = false;

  /**
   * Construct the static command brief help.
   *
   * @param action the action reference
   *
   * @return the static command brief help.
   */
  public static final String getBriefHelp(RecordStopAction action) {
    if (!commandBriefHelpInitialized && action != null) {
      try {
        commandBriefHelp = ResourceUtils.getMessage(COMMAND_BRIEF_HELP_KEY);
      } catch (MissingResourceException e) {
        LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_BRIEF_HELP_KEY + "\"", e);
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
    commandBriefHelpInitialized = false;
    commandBriefHelp = null;
  }

  // #########################################################################
  private final RecordAction recordAction;
  private final String[] defaultArgs;

  public RecordStopAction(RecordAction recordAction) {
    super(ACTION_LABEL, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL));
    if (recordAction == null) {
      throw new IllegalArgumentException("Record action is null");
    }
    this.recordAction = recordAction;
    this.defaultArgs =
        new String[] {recordAction.getDefaultCommandIdentifier(), RecordAction.RECORD_STOP};

    putValue(Action.LARGE_ICON_KEY, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL, true));
    localeChanged();
  }

  // #########################################################################
  @Override
  public void actionPerformed(ActionEvent e) {
    IJssController defaultShellController = this.recordAction.getDefaultShellController();

    // Run action
    if (defaultShellController != null) {
      // Construct the command line
      StringBuilder commandBuilder = new StringBuilder();
      if (defaultArgs != null && defaultArgs.length > 0) {
        for (String arg : defaultArgs) {
          commandBuilder.append("\"").append(arg).append("\"");
          commandBuilder.append(defaultShellController.getCommandParameterSeparator());
        }
      } else {
        String[] commandIdentifiers = this.recordAction.getCommandIdentifiers();
        if (commandIdentifiers != null && commandIdentifiers.length > 0) {
          commandBuilder.append(commandIdentifiers[0]);
        }
      }

      defaultShellController.setCommandLine(commandBuilder.toString());
      defaultShellController.interpret();
    } else {
      this.recordAction.run(defaultArgs);
    }

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
      LOGGER.log(Level.SEVERE, "Resource not found: \"" + ACTION_LABEL_KEY + "\"", e);
      putValue(Action.NAME, ACTION_LABEL);
    }
    putValue(Action.SHORT_DESCRIPTION, getBriefHelp(this));
    putValue(Action.LONG_DESCRIPTION, this.recordAction.getHelp());
  }

  // #########################################################################
  @Override
  public final void putValue(String key, Object newValue) {
    super.putValue(key, newValue);
  }

}
