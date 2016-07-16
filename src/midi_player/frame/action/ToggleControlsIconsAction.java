package midi_player.frame.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JPanel;
import jswingshell.IJssController;
import jswingshell.gui.JssTextAreaController;
import midi_player.resources.LocaleChangeListener;
import midi_player.frame.MidiPlayerFrame;
import midi_player.resources.ResourceUtils;

/**
 * Action to toggle the frame's controls' icons display.
 *
 * <p>
 * This action is deigned for a {@link MidiPlayerFrame} and initializes GUI related
 * properties:</p>
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
public final class ToggleControlsIconsAction extends jswingshell.action.AbstractJssSwitchAction implements LocaleChangeListener {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ToggleControlsIconsAction.class.getName());

    /**
     * This action default identifier.
     */
    public static final String DEFAULT_IDENTIFIER = "toggleControlsIcons";

    private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

    private static final String ACTION_LABEL = "Toggle controls icons";

    private static final String ACTION_LABEL_KEY = "midi_player.action.toggle_controls_icons.name";

    private static final String COMMAND_BRIEF_HELP = "Toggle the frame's controls's icons display.";

    private static final String COMMAND_BRIEF_HELP_KEY = "midi_player.action.toggle_controls_icons.help.short";

    private static final String COMMAND_HELP_KEY = "midi_player.action.toggle_controls_icons.help.long";

    private static String commandHelp;

    private static boolean commandHelpInitialized = false;

    private static String commandBriefHelp;

    private static boolean commandBriefHelpInitialized = false;

    /**
     * Construct the action's command help.
     *
     * @param action the action reference
     *
     * @param shellController The shell controller for which we should retrieve
     * the action's help. This is useful for contextual actions.
     *
     * @return the action's command help.
     */
    public static final String getHelp(ToggleControlsIconsAction action, IJssController shellController) {
        if (!commandHelpInitialized && action != null) {
            StringBuilder stringBuilder = new StringBuilder();

            String commandIdsAsString = action.getCommandIdentifiersAsString();
            stringBuilder.append(action.getBriefHelp()).append("\n");
            stringBuilder.append("\n");
            try {
                stringBuilder.append(ResourceUtils.getMessage(COMMAND_HELP_KEY, commandIdsAsString, action.getOnArgumentsAsString(), action.getOffArgumentsAsString()));
            } catch (MissingResourceException e) {
                LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_HELP_KEY + "\"", e);
                stringBuilder.append("You can switch display mode of controls icons as follow:").append("\n");
                stringBuilder.append("\t").append(commandIdsAsString).append(" ").append(action.getOnArgumentsAsString()).append("\n");
                stringBuilder.append("\t").append(commandIdsAsString).append(" ").append(action.getOffArgumentsAsString());
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
    public static final String getBriefHelp(ToggleControlsIconsAction action) {
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
    private transient MidiPlayerFrame frame;

    public ToggleControlsIconsAction(boolean selected, MidiPlayerFrame frame, JssTextAreaController shellController, String... args) {
        super(selected, ACTION_LABEL, shellController, args);
        if (frame == null) {
            throw new IllegalArgumentException("Frame is null");
        }
        this.frame = frame;
        localeChanged();
    }

    public ToggleControlsIconsAction(MidiPlayerFrame frame, JssTextAreaController shellController, String... args) {
        this(false, frame, shellController, args);
    }

    public ToggleControlsIconsAction(MidiPlayerFrame frame, JssTextAreaController shellController) {
        this(false, frame, shellController, (String[]) null);
    }

    public ToggleControlsIconsAction(MidiPlayerFrame frame) {
        this(false, frame, null, (String[]) null);
    }

    public MidiPlayerFrame getFrame() {
        return frame;
    }

    public void setFrame(MidiPlayerFrame frame) {
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
        JPanel controls = frame.getPanelControl();

        if (controls != null) {
            Component[] controlsButtons = controls.getComponents();

            if (controlsButtons != null) {
                
                for (Component controlsComponent : controlsButtons) {
                    if (controlsComponent instanceof AbstractButton) {
                        AbstractButton controlsButton = (AbstractButton) controlsComponent;
                        if (controlsButton.getAction() instanceof ActionWrapper) {
                            ActionWrapper action = (ActionWrapper) controlsButton.getAction();
                            action.setIconAllowed(switchValue);
                        }
                    }
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public void setDefaultShellController(IJssController shellController) {
        super.setDefaultShellController(shellController);
        // Apply fullscreen mode to new shell controller
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
