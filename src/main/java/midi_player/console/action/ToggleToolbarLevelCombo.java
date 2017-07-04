package midi_player.console.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JToolBar;

import jswingshell.IJssController;
import jswingshell.gui.JssTextAreaController;
import midi_player.console.ConsoleFrame;
import midi_player.console.resources.ResourceUtils;
import midi_player.resources.LocaleChangeListener;

/**
 * Action to toggle the frame's toolbar's level combo box display.
 *
 * <p>
 * This action is deigned for a {@link ConsoleFrame} and initializes GUI related properties:
 * </p>
 * <ul>
 * <li>{@code Action.NAME}</li>
 * <li>{@code Action.SMALL_ICON}</li>
 * <li>{@code Action.LARGE_ICON_KEY}</li>
 * <li>{@code Action.SHORT_DESCRIPTION}</li>
 * <li>{@code Action.LONG_DESCRIPTION}</li>
 * <li>{@code Action.SELECTED_KEY}</li>
 * </ul>
 *
 * @author Mathieu Brunot
 */
public final class ToggleToolbarLevelCombo extends jswingshell.action.AbstractJssSwitchAction
    implements LocaleChangeListener {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = -7267864497676642532L;

  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger.getLogger(ToggleToolbarLevelCombo.class.getName());

  /**
   * This action default identifier.
   */
  public static final String DEFAULT_IDENTIFIER = "toggleToolbarLevelCombo";

  private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

  private static final String ACTION_LABEL = "Toggle toolbar level combo box";

  private static final String ACTION_LABEL_KEY =
      "midi_player.console.action.toggle_toolbar_level_combo.name";

  private static final String COMMAND_BRIEF_HELP =
      "Toggle the frame's toolbar's level combo box display.";

  private static final String COMMAND_BRIEF_HELP_KEY =
      "midi_player.console.action.toggle_toolbar_level_combo.help.short";

  private static final String COMMAND_HELP_KEY =
      "midi_player.console.action.toggle_toolbar_level_combo.help.long";

  private static String commandHelp;

  private static boolean commandHelpInitialized = false;

  private static String commandBriefHelp;

  private static boolean commandBriefHelpInitialized = false;

  /**
   * Construct the action's command help.
   *
   * @param action the action reference
   *
   * @param shellController The shell controller for which we should retrieve the action's help.
   *        This is useful for contextual actions.
   *
   * @return the action's command help.
   */
  public static final String getHelp(ToggleToolbarLevelCombo action,
      IJssController shellController) {
    if (!commandHelpInitialized && action != null) {
      StringBuilder stringBuilder = new StringBuilder();

      String commandIdsAsString = action.getCommandIdentifiersAsString();
      stringBuilder.append(action.getBriefHelp()).append("\n");
      stringBuilder.append("\n");
      try {
        stringBuilder.append(ResourceUtils.getMessage(COMMAND_HELP_KEY, commandIdsAsString,
            action.getOnArgumentsAsString(), action.getOffArgumentsAsString()));
      } catch (MissingResourceException e) {
        LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_HELP_KEY + "\"", e);
        stringBuilder.append("You can switch display mode of toolbar level combo box as follow:")
            .append("\n");
        stringBuilder.append("\t").append(commandIdsAsString).append(" ")
            .append(action.getOnArgumentsAsString()).append("\n");
        stringBuilder.append("\t").append(commandIdsAsString).append(" ")
            .append(action.getOffArgumentsAsString());
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
  public static final String getBriefHelp(ToggleToolbarLevelCombo action) {
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
  private transient ConsoleFrame frame;

  public ToggleToolbarLevelCombo(boolean selected, ConsoleFrame frame,
      JssTextAreaController shellController, String... args) {
    super(selected, ACTION_LABEL, shellController, args);
    if (frame == null) {
      throw new IllegalArgumentException("Frame is null");
    }
    this.frame = frame;
    localeChanged();
  }

  public ToggleToolbarLevelCombo(ConsoleFrame frame, JssTextAreaController shellController,
      String... args) {
    this(false, frame, shellController, args);
  }

  public ToggleToolbarLevelCombo(ConsoleFrame frame, JssTextAreaController shellController) {
    this(false, frame, shellController, (String[]) null);
  }

  public ToggleToolbarLevelCombo(ConsoleFrame frame) {
    this(false, frame, null, (String[]) null);
  }

  public ConsoleFrame getFrame() {
    return frame;
  }

  public void setFrame(ConsoleFrame frame) {
    this.frame = frame;
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
    return getHelp(this, this.getDefaultShellController());
  }

  @Override
  public String getHelp(IJssController shellController) {
    return getHelp(this, shellController);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // Extract some information from the event to construct arguments
    String[] eventArgs = extractArgumentsFromEvent(e);
    // Run action
    if (eventArgs != null) {
      this.run(getDefaultShellController(), eventArgs);
    } else {
      this.run();
    }
  }

  @Override
  protected boolean doSwitch(IJssController shellController, Boolean switchValue) {
    JToolBar.Separator levelSeparator = frame.getjToolbarSeparatorLevel();
    JComboBox<IJssController.PublicationLevel> levelCombo = frame.getjToolbarLevelComboBox();

    if (levelSeparator != null && levelCombo != null) {
      levelSeparator.setVisible(switchValue);
      levelCombo.setVisible(switchValue);

      return true;
    }

    return false;
  }

  @Override
  public void setDefaultShellController(IJssController shellController) {
    super.setDefaultShellController(shellController);
    // Apply mode to new shell controller
    doSwitch(shellController, this.isSelected());
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

}
