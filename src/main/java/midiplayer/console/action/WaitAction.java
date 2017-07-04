package midiplayer.console.action;

import java.beans.PropertyChangeEvent;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import jswingshell.IJssController;
import jswingshell.action.AbstractThreadedJssAction;
import midiplayer.console.resources.ResourceUtils;
import midiplayer.resources.LocaleChangeListener;

/**
 * Action to put the current shell to sleep.
 *
 * @author Mathieu Brunot
 */
public final class WaitAction extends AbstractThreadedJssAction implements LocaleChangeListener {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = 6169075222747959323L;

  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger.getLogger(WaitAction.class.getName());

  /**
   * This action default identifier.
   *
   * @since 1.2
   */
  public static final String DEFAULT_IDENTIFIER = "wait";

  private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

  private static final String COMMAND_BRIEF_HELP = "Wait for a given time in a separate thread.";

  private static final String COMMAND_BRIEF_HELP_KEY = "midi_player.console.action.wait.help.short";

  private static final String COMMAND_HELP_KEY = "midi_player.console.action.wait.help.long";

  private static final String COMMAND_RUN_INVALID_NUMBER_FORMAT_KEY =
      "midi_player.console.action.wait.run.invalid_number_format";

  private static final String COMMAND_RUN_NEGATIVE_TIME_KEY =
      "midi_player.console.action.wait.run.negative_time";

  private static final String COMMAND_RUN_INTERRUPTED_KEY =
      "midi_player.console.action.wait.run.interrupted";

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
  public static final String getHelp(WaitAction action) {
    if (!commandHelpInitialized && action != null) {
      StringBuilder stringBuilder = new StringBuilder();

      String commandIdsAsString = action.getCommandIdentifiersAsString();
      stringBuilder.append(action.getBriefHelp()).append("\n");
      stringBuilder.append("\n");
      try {
        stringBuilder.append(ResourceUtils.getMessage(COMMAND_HELP_KEY, commandIdsAsString));
      } catch (MissingResourceException e) {
        LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_HELP_KEY + "\"", e);
        stringBuilder.append("Wait for a given time in a separate thread:").append("\n");
        stringBuilder.append("\t").append(commandIdsAsString).append("\n");
        stringBuilder.append("The default time for waiting is 1000 milliseconds (1 second).")
            .append("\n");
        stringBuilder.append("You can define the time for the wait (in milliseconds) as follow:")
            .append("\n");
        stringBuilder.append("\t").append(commandIdsAsString).append(" [time] ").append("\n");
        stringBuilder
            .append(
                "This action is suited for manual use since it will wait a given time without blocking the current thread.")
            .append("\n");
        stringBuilder.append("This action should not be called outside of the EDT.");
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
  public static final String getBriefHelp(WaitAction action) {
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
    commandHelpInitialized = false;
    commandHelp = null;
    commandBriefHelpInitialized = false;
    commandBriefHelp = null;
  }

  // #########################################################################
  public WaitAction(String name, Icon icon, IJssController shellController, String... args) {
    super(name, icon, shellController, args);
    localeChanged();
  }

  public WaitAction(String name, IJssController shellController, String... args) {
    super(name, shellController, args);
    localeChanged();
  }

  public WaitAction(IJssController shellController, String... args) {
    super(shellController, args);
    localeChanged();
  }

  public WaitAction(IJssController shellController) {
    super(shellController);
    localeChanged();
  }

  public WaitAction() {
    super();
    localeChanged();
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
  protected AbstractJssActionWorker prepareWorker(IJssController shellController, String... args) {
    SleepWorker worker = null;

    // Sleep default value
    long sleepTime = 1000;
    // Extract time from parameters
    if (args != null && args.length > 1) {
      try {
        sleepTime = Long.valueOf(args[1]);
      } catch (NumberFormatException e) {
        LOGGER.log(Level.WARNING, "Invalid number format: " + args[1], e);
        String msg;
        try {
          msg = ResourceUtils.getMessage(COMMAND_RUN_INVALID_NUMBER_FORMAT_KEY, args[1],
              e.getLocalizedMessage());
        } catch (MissingResourceException e1) {
          LOGGER.log(Level.SEVERE,
              "Resource not found: \"" + COMMAND_RUN_INVALID_NUMBER_FORMAT_KEY + "\"", e1);
          msg = "Invalid number format: " + args[1] + " (" + e.getMessage() + ")";
        }
        shellController.publish(IJssController.PublicationLevel.ERROR, msg);
      }
    }

    final long millis = sleepTime;
    if (millis >= 0l) {
      worker = new SleepWorker(shellController, millis);
    } else {
      LOGGER.log(Level.WARNING, "Wait time cannot be negative: {0}", millis);
      String msg;
      try {
        msg = ResourceUtils.getMessage(COMMAND_RUN_NEGATIVE_TIME_KEY, millis);
      } catch (MissingResourceException e1) {
        LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_RUN_NEGATIVE_TIME_KEY + "\"",
            e1);
        msg = "Sleep time cannot be negative: " + millis;
      }
      shellController.publish(IJssController.PublicationLevel.ERROR, msg);
    }

    return worker;
  }

  // #########################################################################
  protected class SleepWorker extends AbstractJssActionWorker {

    final long millis;

    public SleepWorker(IJssController shellController, long millis) {
      super(shellController);
      this.millis = millis;
    }

    @Override
    protected Integer doInBackground() throws Exception {
      int workerCommandReturnStatus = AbstractThreadedJssAction.SUCCESS;

      getShellController().lockCommandLine();
      try {
        Thread.sleep(millis);
      } catch (InterruptedException ex) {
        LOGGER.log(Level.WARNING, "Wait action interrupted.", ex);
        String msg;
        try {
          msg = ResourceUtils.getMessage(COMMAND_RUN_INTERRUPTED_KEY, ex.getLocalizedMessage());
        } catch (MissingResourceException e1) {
          LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_RUN_INTERRUPTED_KEY + "\"",
              e1);
          msg = "Wait action interrupted: " + ex.getMessage();
        }
        this.publish(
            WaitAction.this.new JssActionWorkerChunk(IJssController.PublicationLevel.WARNING, msg));
        workerCommandReturnStatus = AbstractThreadedJssAction.ERROR;
      }

      return workerCommandReturnStatus;
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
  }

}
