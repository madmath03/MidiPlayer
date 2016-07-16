package midi_player.console.action;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import jswingshell.IJssController;
import midi_player.console.resources.ResourceUtils;
import jswingshell.gui.JssTextAreaController;
import midi_player.resources.LocaleChangeListener;

/**
 * Action to toggle the frame's full screen mode.
 *
 * <p>
 * This action uses the
 * {@code java.​awt.​GraphicsDevice.setFullScreenWindow(Window)} and initializes
 * GUI related properties:</p>
 * <ul>
 * <li>{@code Action.NAME}</li>
 * <li>{@code Action.SMALL_ICON}</li>
 * <li>{@code Action.LARGE_ICON_KEY}</li>
 * <li>{@code Action.SHORT_DESCRIPTION}</li>
 * <li>{@code Action.LONG_DESCRIPTION}</li>
 * <li>{@code Action.SELECTED_KEY}</li>
 * <li>{@code Action.ACCELERATOR_KEY}</li>
 * <li>{@code Action.ACTION_COMMAND_KEY}</li>
 * </ul>
 *
 * @author Mathieu Brunot
 */
public final class FullScreenAction extends jswingshell.action.AbstractJssSwitchAction implements LocaleChangeListener {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(FullScreenAction.class.getName());

    /**
     * This action default identifier.
     */
    public static final String DEFAULT_IDENTIFIER = "fullscreen";

    private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

    private static final String ACTION_LABEL = "Full Screen";

    private static final String ACTION_LABEL_KEY = "midi_player.console.action.fullscreen.name";

    private static final String COMMAND_BRIEF_HELP = "Toggle the frame's full screen mode.";

    private static final String COMMAND_BRIEF_HELP_KEY = "midi_player.console.action.fullscreen.help.short";

    private static final String COMMAND_HELP_KEY = "midi_player.console.action.fullscreen.help.long";

    private static final String COMMAND_RUN_NOT_SUPPORTED_WARNING_KEY = "midi_player.console.action.fullscreen.run.not_supported";

    private static final String ICON_KEY = "slideshow_full_screen.png";

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
    public static final String getHelp(FullScreenAction action, IJssController shellController) {
        if (!commandHelpInitialized && action != null) {
            StringBuilder stringBuilder = new StringBuilder();

            String commandIdsAsString = action.getCommandIdentifiersAsString();
            stringBuilder.append(action.getBriefHelp()).append("\n");
            stringBuilder.append("\n");
            try {
                stringBuilder.append(ResourceUtils.getMessage(COMMAND_HELP_KEY, commandIdsAsString, action.getOnArgumentsAsString(), action.getOffArgumentsAsString()));
            } catch (MissingResourceException e) {
                LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_HELP_KEY + "\"", e);
                stringBuilder.append("You can switch full screen mode as follow:").append("\n");
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
    public static final String getBriefHelp(FullScreenAction action) {
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
    private transient JFrame frame;

    public FullScreenAction(Boolean selected, JFrame frame, JssTextAreaController shellController, String... args) {
        super(selected, ACTION_LABEL, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL), shellController, args);
        if (frame == null) {
            throw new IllegalArgumentException("Frame is null");
        }
        this.frame = frame;
        putValue(Action.LARGE_ICON_KEY, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL, true));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_F11, 0));
        putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
        localeChanged();
    }

    public FullScreenAction(JFrame frame, JssTextAreaController shellController, String... args) {
        this(false, frame, shellController, args);
    }

    public FullScreenAction(JFrame frame, JssTextAreaController shellController) {
        this(false, frame, shellController, (String[]) null);
    }

    public FullScreenAction(JFrame frame) {
        this(false, frame, null, (String[]) null);
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
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
        // Do not display in the shell even if one was provided
    }

    @Override
    protected boolean doSwitch(IJssController shellController, Boolean switchValue) {
        // Get the screen device based on the frame's location
        GraphicsDevice gd = getGraphicsDeviceForFrame();

        return switchToFullscreen(shellController, switchValue, gd);
    }

    private GraphicsDevice getGraphicsDeviceForFrame() {
        GraphicsDevice gd = null;

        // Can a display, keyboard, and mouse can be supported in this graphics environment
        if (!GraphicsEnvironment.isHeadless() || frame == null) {
            // Get all the screen devices available
            GraphicsDevice[] gds = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
            if (gds.length == 0) {
                return null;
            }

            // Get the screen device based on the frame's center location
            Point frameCenterLocation = new Point(frame.getLocation());
            frameCenterLocation.move(frameCenterLocation.x + (frame.getWidth() / 2), 
                    frameCenterLocation.y + (frame.getHeight() / 2));

            for (GraphicsDevice tempGd : gds) {
                Rectangle gdBounds = tempGd.getDefaultConfiguration().getBounds();
                if (gdBounds.contains(frameCenterLocation)) {
                    gd = tempGd;
                    break;
                }
            }

            // If no device contains the frame
            if (gd == null) {
                // Use the default screen device
                gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            }

        }

        return gd;
    }

    private boolean switchToFullscreen(IJssController shellController, Boolean switchValue, GraphicsDevice gd) {
        boolean switchDone = false;

        // If there is a screen device and it supports fullscreen
        if (gd != null) {
            // If device supports fullscreen and fullscreen requested
            if (switchValue && gd.isFullScreenSupported()) {
                frame.dispose();
                frame.setUndecorated(switchValue);
                gd.setFullScreenWindow(frame);
                putValue(Action.SELECTED_KEY, switchValue);

                switchDone = true;
            } else {
                // If device does not support fullscreen and while requested
                if (switchValue && !gd.isFullScreenSupported()) {
                    LOGGER.log(Level.WARNING, "Full screen not supported");
                    String msg;
                    try {
                        msg = ResourceUtils.getMessage(COMMAND_RUN_NOT_SUPPORTED_WARNING_KEY);
                    } catch (MissingResourceException e) {
                        LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_RUN_NOT_SUPPORTED_WARNING_KEY + "\"", e);
                        msg = "Full screen not supported";
                    }
                    shellController.publish(IJssController.PublicationLevel.WARNING, msg);
                } else {
                    switchDone = true;
                }

                frame.dispose();
                gd.setFullScreenWindow(null);
                frame.setUndecorated(switchValue);
                frame.setVisible(true);
                putValue(Action.SELECTED_KEY, false);
            }
        }

        return switchDone;
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
