package midiplayer.console.action;

import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.KeyStroke;

import jswingshell.IJssController;
import jswingshell.action.AbstractJssAction;
import jswingshell.action.IJssAction;
import midiplayer.console.resources.ResourceUtils;
import midiplayer.resources.LocaleChangeListener;

/**
 * Action to display available commands.
 *
 * <p>
 * This action displays available commands and initializes GUI related properties:
 * </p>
 * <ul>
 * <li>{@code Action.NAME}</li>
 * <li>{@code Action.SMALL_ICON}</li>
 * <li>{@code Action.LARGE_ICON_KEY}</li>
 * <li>{@code Action.SHORT_DESCRIPTION}</li>
 * <li>{@code Action.LONG_DESCRIPTION}</li>
 * <li>{@code Action.ACCELERATOR_KEY}</li>
 * <li>{@code Action.ACTION_COMMAND_KEY}</li>
 * </ul>
 *
 * @author Mathieu Brunot
 */
public final class HelpAction extends AbstractJssAction
    implements LocaleChangeListener {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = -7214034364144694123L;

  /**
   * Logger.
   */
  private static final Logger LOGGER =
      Logger.getLogger(HelpAction.class.getName());

  /**
   * This action default identifier.
   *
   * @since 1.2
   */
  public static final String DEFAULT_IDENTIFIER = "help";

  private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER, "man"};

  private static final String ACTION_LABEL = "Help";

  private static final String ACTION_LABEL_KEY =
      "midiplayer.console.action.help.name";

  private static final String COMMAND_BRIEF_HELP =
      "Display available commands.";

  private static final String COMMAND_BRIEF_HELP_KEY =
      "midiplayer.console.action.help.help.short";

  private static final String COMMAND_HELP_KEY =
      "midiplayer.console.action.help.help.long";

  private static final String COMMAND_RUN_KEY =
      "midiplayer.console.action.help.run";

  private static final String COMMAND_RUN_NOT_FOUND_WARNING_KEY =
      "midiplayer.console.action.help.run.command_not_found";

  private static final String ICON_KEY = "help.png";

  private static String commandHelp;

  private static boolean commandHelpInitialized = false;

  private static String commandBriefHelp;

  private static boolean commandBriefHelpInitialized = false;

  /**
   * Construct the action's command help.
   *
   * @param action the action reference
   *
   * @return the action's command help.
   */
  public static final String getHelp(HelpAction action) {
    if (!commandHelpInitialized && action != null) {
      StringBuilder stringBuilder = new StringBuilder();

      String commandIdsAsString = action.getCommandIdentifiersAsString(),
          defaultCommandId = action.getDefaultCommandIdentifier();
      stringBuilder.append(action.getBriefHelp()).append("\n");
      stringBuilder.append("\n");
      try {
        stringBuilder.append(ResourceUtils.getMessage(COMMAND_HELP_KEY,
            commandIdsAsString, defaultCommandId));
      } catch (MissingResourceException e) {
        LOGGER.log(Level.SEVERE,
            "Resource not found: \"" + COMMAND_HELP_KEY + "\"", e);
        stringBuilder.append("For more information on a command, enter ")
            .append(commandIdsAsString).append(" followed by the command:");
        stringBuilder.append("\n\t").append(defaultCommandId)
            .append(" [command] ");
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
  public static final String getBriefHelp(HelpAction action) {
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
  public HelpAction(IJssController shellController, String... args) {
    super(ACTION_LABEL, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL),
        shellController, args);
    putValue(Action.LARGE_ICON_KEY,
        ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL, true));
    putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
    putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
    localeChanged();
  }

  public HelpAction(IJssController shellController) {
    this(shellController, (String[]) null);
  }

  public HelpAction() {
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
  public String getHelp() {
    return getHelp(this);
  }

  @Override
  public String getHelp(IJssController shellController) {
    return getHelp(this);
  }

  @Override
  public int run(IJssController shellController, String... args) {
    int commandReturnStatus = AbstractJssAction.SUCCESS;

    if (shellController == null) {
      commandReturnStatus = 1;
    } else {
      if (args == null || args.length <= 1) {
        StringBuilder stringBuilder = new StringBuilder();

        String commandIdsAsString = this.getCommandIdentifiersAsString();

        try {
          stringBuilder.append(
              ResourceUtils.getMessage(COMMAND_RUN_KEY, commandIdsAsString));
        } catch (MissingResourceException e) {
          LOGGER.log(Level.SEVERE,
              "Resource not found: \"" + COMMAND_RUN_KEY + "\"", e);
          stringBuilder.append("For more information on a command, enter ")
              .append(commandIdsAsString).append(" followed by the command.");
          stringBuilder.append("\n");
          stringBuilder.append("\n").append("Available commands:");
        }

        Collection<IJssAction> shellAvailableActions =
            shellController.getAvailableActions();
        if (shellAvailableActions != null) {
          List<IJssAction> availableActions =
              new ArrayList<>(shellAvailableActions);
          Collections.sort(availableActions);
          for (IJssAction availableAction : availableActions) {
            if (availableAction != null) {
              stringBuilder.append("\n\t")
                  .append(availableAction.getCommandIdentifiersAsString())
                  .append(" ").append(availableAction.getBriefHelp());
            }
          }
        }

        shellController.publish(IJssController.PublicationLevel.SUCCESS,
            stringBuilder.toString());
      } else if (args.length == 2) {
        IJssAction action =
            shellController.getActionForCommandIdentifier(args[1]);
        if (action != null) {
          shellController.publish(IJssController.PublicationLevel.SUCCESS,
              action.getHelp(shellController));
        } else {
          String msg;
          try {
            msg = ResourceUtils.getMessage(COMMAND_RUN_NOT_FOUND_WARNING_KEY,
                args[1]);
          } catch (MissingResourceException e) {
            LOGGER.log(Level.SEVERE, "Resource not found: \""
                + COMMAND_RUN_NOT_FOUND_WARNING_KEY + "\"", e);
            msg = "Command not found: " + args[1];
          }
          shellController.publish(IJssController.PublicationLevel.WARNING, msg);
        }
      } else {
        shellController.publish(IJssController.PublicationLevel.WARNING,
            getHelp(shellController));
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

}
