package midi_player.console.action;

import java.beans.PropertyChangeEvent;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.Icon;
import jswingshell.IJssController;
import jswingshell.action.AbstractJssAction;
import midi_player.resources.LocaleChangeListener;
import midi_player.console.resources.ResourceUtils;

/**
 * Action to put the current thread to sleep.
 *
 * @author Mathieu Brunot
 */
public final class SleepAction extends AbstractJssAction implements LocaleChangeListener {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(SleepAction.class.getName());

    /**
     * This action default identifier.
     *
     * @since 1.2
     */
    public static final String DEFAULT_IDENTIFIER = "sleep";

    private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

    private static final String COMMAND_BRIEF_HELP = "Put the current thread to sleep.";

    private static final String COMMAND_BRIEF_HELP_KEY = "midi_player.console.action.sleep.help.short";

    private static final String COMMAND_HELP_KEY = "midi_player.console.action.sleep.help.long";

    private static final String COMMAND_RUN_INVALID_NUMBER_FORMAT_KEY = "midi_player.console.action.sleep.run.invalid_number_format";

    private static final String COMMAND_RUN_NEGATIVE_TIME_KEY = "midi_player.console.action.sleep.run.negative_time";

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
    public static final String getHelp(SleepAction action) {
        if (!commandHelpInitialized && action != null) {
            StringBuilder stringBuilder = new StringBuilder();

            String commandIdsAsString = action.getCommandIdentifiersAsString();
            stringBuilder.append(action.getBriefHelp()).append("\n");
            stringBuilder.append("\n");
            try {
                stringBuilder.append(ResourceUtils.getMessage(COMMAND_HELP_KEY, commandIdsAsString));
            } catch (MissingResourceException e) {
                LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_HELP_KEY + "\"", e);
                stringBuilder.append("Put the current thread to sleep:").append("\n");
                stringBuilder.append("\t").append(commandIdsAsString).append("\n");
                stringBuilder.append("The default time for sleep is 1000 milliseconds (1 second).").append("\n");
                stringBuilder.append("You can define the time for the sleep (in milliseconds) as follow:").append("\n");
                stringBuilder.append("\t").append(commandIdsAsString).append(" [time] ");
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
    public static final String getBriefHelp(SleepAction action) {
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
    public SleepAction(String name, Icon icon, IJssController shellController, String... args) {
        super(name, icon, shellController, args);
        putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
        localeChanged();
    }

    public SleepAction(String name, IJssController shellController, String... args) {
        super(name, shellController, args);
        putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
        localeChanged();
    }

    public SleepAction(IJssController shellController, String... args) {
        super(shellController, args);
        putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
        localeChanged();
    }

    public SleepAction(IJssController shellController) {
        this(shellController, (String[]) null);
    }

    public SleepAction() {
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
                    msg = ResourceUtils.getMessage(COMMAND_RUN_INVALID_NUMBER_FORMAT_KEY, args[1], e.getLocalizedMessage());
                } catch (MissingResourceException e1) {
                    LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_RUN_INVALID_NUMBER_FORMAT_KEY + "\"", e1);
                    msg = "Invalid number format: " + args[1] + " (" + e.getMessage() + ")";
                }
                shellController.publish(IJssController.PublicationLevel.ERROR, msg);
            }
        }

        // Put the current thread to sleep
        final long millis = sleepTime;
        if (millis >= 0l) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
                shellController.publish(IJssController.PublicationLevel.FATAL_ERROR, ex.getMessage());
                commandReturnStatus = AbstractJssAction.ERROR;
            }
        } else {
            LOGGER.log(Level.WARNING, "Sleep time cannot be negative: {0}", millis);
            String msg;
            try {
                msg = ResourceUtils.getMessage(COMMAND_RUN_NEGATIVE_TIME_KEY, millis);
            } catch (MissingResourceException e1) {
                LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_RUN_NEGATIVE_TIME_KEY + "\"", e1);
                msg = "Sleep time cannot be negative: " + millis;
            }
            shellController.publish(IJssController.PublicationLevel.ERROR, msg);
            commandReturnStatus = AbstractJssAction.ERROR;
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
