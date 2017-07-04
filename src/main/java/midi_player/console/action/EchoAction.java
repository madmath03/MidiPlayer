package midi_player.console.action;

import java.beans.PropertyChangeEvent;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.Icon;

import jswingshell.IJssController;
import jswingshell.action.AbstractJssAction;
import jswingshell.gui.JssTextAreaController;
import midi_player.console.resources.ResourceUtils;
import midi_player.resources.LocaleChangeListener;

/**
 * Action to display a message in a shell.
 *
 * @author Mathieu Brunot
 */
public final class EchoAction extends AbstractJssAction implements LocaleChangeListener {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = -4625543245135138703L;

  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger.getLogger(EchoAction.class.getName());

  /**
   * This action default identifier.
   *
   * @since 1.2
   */
  public static final String DEFAULT_IDENTIFIER = "echo";

  private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

  private static final String COMMAND_BRIEF_HELP = "Display a message in the shell.";

  private static final String COMMAND_BRIEF_HELP_KEY = "midi_player.console.action.echo.help.short";

  private static final String COMMAND_HELP_KEY = "midi_player.console.action.echo.help.long";

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
  public static final String getHelp(EchoAction action) {
    if (!commandHelpInitialized && action != null) {
      StringBuilder stringBuilder = new StringBuilder();

      String commandIdsAsString = action.getCommandIdentifiersAsString(),
          defaultCommandId = action.getDefaultCommandIdentifier();
      stringBuilder.append(action.getBriefHelp()).append("\n");
      stringBuilder.append("\n");
      try {
        stringBuilder.append(
            ResourceUtils.getMessage(COMMAND_HELP_KEY, commandIdsAsString, defaultCommandId));
      } catch (MissingResourceException e) {
        LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_HELP_KEY + "\"", e);
        stringBuilder.append("\n").append("Displays everything after the command on a new line:");
        stringBuilder.append("\n\t").append(commandIdsAsString).append(" [message] ");

        if (defaultCommandId != null) {
          stringBuilder.append("\n");
          stringBuilder.append("\n").append("Example: ");
          stringBuilder.append("\n").append(defaultCommandId).append(" Hello world!");
          stringBuilder.append("\n").append("Hello world!");

          stringBuilder.append("\n");
          stringBuilder.append("\n").append("Example: ");
          stringBuilder.append("\n").append(defaultCommandId).append("             Hello world!");
          stringBuilder.append("\n").append("Hello world!");

          stringBuilder.append("\n");
          stringBuilder.append("\n").append("Example: ");
          stringBuilder.append("\n").append(defaultCommandId)
              .append(" \"            Hello world!\"");
          stringBuilder.append("\n").append("            Hello world!");
        }

        stringBuilder.append("\n");
        stringBuilder.append("\n")
            .append("If no message is provided, an empty line will be displayed.");
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
  public static final String getBriefHelp(EchoAction action) {
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
  public EchoAction(String name, Icon icon, IJssController shellController, String... args) {
    super(name, icon, shellController, args);
    putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
    localeChanged();
  }

  public EchoAction(String name, IJssController shellController, String... args) {
    super(name, shellController, args);
    putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
    localeChanged();
  }

  public EchoAction(IJssController shellController, String... args) {
    super(shellController, args);
    putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
    localeChanged();
  }

  public EchoAction(IJssController shellController) {
    this(shellController, (String[]) null);
  }

  public EchoAction() {
    this(null, (String[]) null);
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
    int commandReturnStatus = AbstractJssAction.SUCCESS;

    if (shellController == null) {
      commandReturnStatus = AbstractJssAction.ERROR;
    } else {
      if (args != null && args.length > 1) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 1, n = args.length; i < n; i++) {
          stringBuilder.append(args[i]).append(JssTextAreaController.COMMAND_PARAMETER_SEPARATOR);
        }

        shellController.publish(IJssController.PublicationLevel.SUCCESS, stringBuilder.toString());
      }
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

}
