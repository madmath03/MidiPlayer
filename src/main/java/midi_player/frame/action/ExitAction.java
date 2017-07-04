package midi_player.frame.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.KeyStroke;

import jswingshell.IJssController;
import jswingshell.action.AbstractJssAction;
import midi_player.console.action.util.Serialization;
import midi_player.resources.LocaleChangeListener;
import midi_player.resources.ResourceUtils;

/**
 * Action to exit application.
 *
 * <p>
 * This action inherits from the standard {@code jswingshell.action.ExitAction} and initializes GUI
 * related properties:
 * </p>
 * <ul>
 * <li>{@code Action.NAME}</li>
 * <li>{@code Action.SMALL_ICON}</li>
 * <li>{@code Action.LARGE_ICON_KEY}</li>
 * <li>{@code Action.SHORT_DESCRIPTION}</li>
 * <li>{@code Action.LONG_DESCRIPTION}</li>
 * <li>{@code Action.ACTION_COMMAND_KEY}</li>
 * </ul>
 *
 * @author Mathieu Brunot
 */
public final class ExitAction extends AbstractJssAction implements LocaleChangeListener {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = 5884447322379323328L;

  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger.getLogger(ExitAction.class.getName());

  /**
   * This action default identifier.
   *
   * @since 1.2
   */
  public static final String DEFAULT_IDENTIFIER = "exit";

  private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER, "quit"};

  private static final String ACTION_LABEL = "Exit";

  private static final String ACTION_LABEL_KEY = "midi_player.action.exit.name";

  private static final String COMMAND_BRIEF_HELP = "Exits application.";

  private static final String COMMAND_BRIEF_HELP_KEY = "midi_player.action.exit.help.short";

  private static final String COMMAND_HELP_KEY = "midi_player.action.exit.help.long";

  private static final String COMMAND_RUN_INVALID_RETURN_CODE_WARNING_KEY =
      "midi_player.action.exit.run.invalid_return_code";

  private static final String ICON_KEY = "door_in.png";

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
  public static final String getHelp(ExitAction action) {
    if (!commandHelpInitialized && action != null) {
      StringBuilder stringBuilder = new StringBuilder();

      String commandIdsAsString = action.getCommandIdentifiersAsString();
      stringBuilder.append(action.getBriefHelp()).append("\n");
      stringBuilder.append("\n");
      try {
        stringBuilder.append(ResourceUtils.getMessage(COMMAND_HELP_KEY, commandIdsAsString));
      } catch (MissingResourceException e) {
        LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_HELP_KEY + "\"", e);
        stringBuilder.append("\n").append("Exits the application with default return code (0):");
        stringBuilder.append("\n\t").append(commandIdsAsString);
        stringBuilder.append("\n").append("You can define exit status code as follow:");
        stringBuilder.append("\n\t").append(commandIdsAsString).append(" [integer] ");
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
  public static final String getBriefHelp(ExitAction action) {
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
  private transient List<? extends AutoCloseable> closableResources = null;

  public ExitAction(IJssController shellController, String... args) {
    super(ACTION_LABEL, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL), shellController,
        args);
    putValue(Action.LARGE_ICON_KEY, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL, true));
    putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
    putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
    localeChanged();
  }

  public ExitAction(IJssController shellController) {
    this(shellController, (String[]) null);
  }

  public ExitAction() {
    this(null, (String[]) null);
  }

  public List<? extends AutoCloseable> getClosableResources() {
    return closableResources;
  }

  public void setClosableResources(List<? extends AutoCloseable> closableResources) {
    this.closableResources = closableResources;
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

    // Save model before quitting
    if (shellController != null) {
      Serialization.saveSerializedModel(shellController.getModel());
    }

    // Close resources before exit
    if (closableResources != null && !closableResources.isEmpty()) {
      for (AutoCloseable closableResource : closableResources) {
        try {
          closableResource.close();
        } catch (Exception ex) {
          LOGGER.log(Level.SEVERE, "Error occurred while closing resource " + closableResource, ex);
        }
      }
    }

    // Exit application
    if (args == null || args.length <= 1) {
      System.exit(commandReturnStatus);
    } else if (args.length > 1) {
      try {
        commandReturnStatus = Integer.valueOf(args[1]);
        System.exit(commandReturnStatus);
      } catch (NumberFormatException e) {
        LOGGER.log(Level.SEVERE, "Invalid number format: " + args[1], e);
        if (shellController != null) {
          String msg;
          try {
            msg = ResourceUtils.getMessage(COMMAND_RUN_INVALID_RETURN_CODE_WARNING_KEY, args[1]);
          } catch (MissingResourceException e1) {
            LOGGER.log(Level.SEVERE,
                "Resource not found: \"" + COMMAND_RUN_INVALID_RETURN_CODE_WARNING_KEY + "\"", e1);
            msg = "Invalid number format: " + args[1];
          }
          shellController.publish(IJssController.PublicationLevel.ERROR, msg);
        }
        System.exit(commandReturnStatus);
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
      LOGGER.log(Level.SEVERE, "Resource not found: \"" + ACTION_LABEL_KEY + "\"", e);
      putValue(Action.NAME, ACTION_LABEL);
    }
    putValue(Action.SHORT_DESCRIPTION, this.getBriefHelp());
    putValue(Action.LONG_DESCRIPTION, this.getHelp(this.getDefaultShellController()));
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
