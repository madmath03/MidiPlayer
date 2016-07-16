package midi_player.console.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import midi_player.console.resources.ResourceUtils;
import javax.swing.Action;
import javax.swing.KeyStroke;
import jswingshell.IJssController;
import jswingshell.action.AbstractJssAction;
import jswingshell.gui.JssTextArea;
import jswingshell.gui.JssTextAreaController;
import midi_player.resources.LocaleChangeListener;

/**
 * Action to copy the current selection in a shell to the clipboard.
 *
 * <p>
 * This action uses the {@code javax.​swing.​text.​JTextComponent.copy()} method
 * and initializes GUI related properties:</p>
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
public final class CopyAction extends jswingshell.action.AbstractJssAction implements LocaleChangeListener {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CopyAction.class.getName());

    /**
     * This action default identifier.
     */
    public static final String DEFAULT_IDENTIFIER = "copy";

    private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

    private static final String ACTION_LABEL = "Copy";

    private static final String ACTION_LABEL_KEY = "midi_player.console.action.copy.name";

    private static final String COMMAND_BRIEF_HELP = "Copy the current selection to the clipboard.";

    private static final String COMMAND_BRIEF_HELP_KEY = "midi_player.console.action.copy.help.short";

    private static final String COMMAND_HELP_KEY = "midi_player.console.action.copy.help.long";

    private static final String ICON_KEY = "page_copy.png";

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
    public static final String getHelp(CopyAction action) {
        if (!commandHelpInitialized && action != null) {
            StringBuilder stringBuilder = new StringBuilder();

            String commandIdsAsString = action.getCommandIdentifiersAsString();
            stringBuilder.append(action.getBriefHelp()).append("\n");
            stringBuilder.append("\n");
            try {
                stringBuilder.append(ResourceUtils.getMessage(COMMAND_HELP_KEY, commandIdsAsString));
            } catch (MissingResourceException e) {
                LOGGER.log(Level.SEVERE, "Resource not found: \""+COMMAND_HELP_KEY+"\"", e);
                stringBuilder.append("Copies the selection in the shell:");
                stringBuilder.append("\n\t").append(commandIdsAsString);
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
    public static final String getBriefHelp(CopyAction action) {
        if (!commandBriefHelpInitialized && action != null) {
            try {
                commandBriefHelp = ResourceUtils.getMessage(COMMAND_BRIEF_HELP_KEY);
            } catch (MissingResourceException e) {
                LOGGER.log(Level.SEVERE, "Resource not found: \""+COMMAND_BRIEF_HELP_KEY+"\"", e);
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
    private transient JssTextAreaController textAreaController;
    
    public CopyAction(JssTextAreaController textAreaController, IJssController shellController, String... args) {
        super(ACTION_LABEL, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL), shellController, args);
        if (textAreaController == null) {
            throw new IllegalArgumentException("Text area controller cannot be null");
        }
        this.textAreaController = textAreaController;
        putValue(Action.LARGE_ICON_KEY, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL, true));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
        localeChanged();
    }

    public CopyAction(JssTextAreaController textAreaController, IJssController shellController) {
        this(textAreaController, shellController, (String[]) null);
    }

    public CopyAction(JssTextAreaController textAreaController) {
        this(textAreaController, null, (String[]) null);
    }

    public JssTextAreaController getTextAreaController() {
        return textAreaController;
    }

    public void setTextAreaController(JssTextAreaController textAreaController) {
        this.textAreaController = textAreaController;
    }

    @Override
    public void setDefaultShellController(IJssController shellController) {
        super.setDefaultShellController(shellController);
        if (textAreaController == null && shellController instanceof JssTextAreaController) {
            this.textAreaController = (JssTextAreaController) shellController;
        }
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

        JssTextAreaController textController = getTextAreaController();
        if (textController == null) {
            commandReturnStatus = AbstractJssAction.ERROR;
        } else {
            JssTextArea view = textController.getView();
            if (args == null || args.length == 1) {
                view.copy();
            } else if (shellController != null) {
                shellController.publish(IJssController.PublicationLevel.WARNING, getHelp(shellController));
            }
        }

        return commandReturnStatus;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        run();
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
            LOGGER.log(Level.SEVERE, "Resource not found: \""+ACTION_LABEL_KEY+"\"", e);
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
