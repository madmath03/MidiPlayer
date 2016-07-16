package midi_player.console.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import midi_player.resources.LocaleChangeListener;
import midi_player.console.resources.ResourceUtils;
import midi_player.frame.action.DisplayConsoleAction;

/**
 * Action to close the console screen.
 *
 * <p>
 * This action encapsulates a {@link Disl} and initializes GUI related
 * properties:</p>
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
public final class CloseAction extends AbstractAction implements LocaleChangeListener {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CloseAction.class.getName());

    private static final String ACTION_LABEL = "Close";

    private static final String ACTION_LABEL_KEY = "midi_player.console.action.close.name";

    private static final String ICON_KEY = "door_in.png";

    private static final String COMMAND_BRIEF_HELP = "Closes the console.";

    private static final String COMMAND_BRIEF_HELP_KEY = "midi_player.console.action.close.help.short";

    private static String commandBriefHelp;

    private static boolean commandBriefHelpInitialized = false;

    /**
     * Construct the static command brief help.
     *
     * @param action the action reference
     *
     * @return the static command brief help.
     */
    public static final String getBriefHelp(CloseAction action) {
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
    private final DisplayConsoleAction displayConsoleAction;

    public CloseAction(DisplayConsoleAction displayConsoleAction) {
        super(ACTION_LABEL, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL));
        if (displayConsoleAction == null) {
            throw new IllegalArgumentException("Display action is null");
        }
        this.displayConsoleAction = displayConsoleAction;

        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_F12, 0));
        putValue(Action.LARGE_ICON_KEY, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL, true));
        localeChanged();
    }

    // #########################################################################
    @Override
    public void actionPerformed(ActionEvent e) {
        this.displayConsoleAction.actionPerformed(e);
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
        putValue(Action.LONG_DESCRIPTION, this.displayConsoleAction.getHelp());
    }

    // #########################################################################
    @Override
    public final void putValue(String key, Object newValue) {
        super.putValue(key, newValue);
    }

}
