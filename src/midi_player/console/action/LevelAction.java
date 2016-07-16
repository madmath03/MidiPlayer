package midi_player.console.action;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import jswingshell.IJssController;
import jswingshell.action.AbstractJssComboAction;
import jswingshell.action.ActionGroup;
import midi_player.resources.LocaleChangeListener;
import midi_player.console.resources.ResourceUtils;

/**
 * Action to switch a shell's publication level.
 *
 * @author Mathieu Brunot
 */
public final class LevelAction extends jswingshell.action.AbstractJssComboAction<IJssController.PublicationLevel> implements LocaleChangeListener {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LevelAction.class.getName());

    /**
     * This action default identifier.
     */
    public static final String DEFAULT_IDENTIFIER = "level";

    private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

    private static final String ACTION_LABEL = "Level";

    private static final String ACTION_LABEL_KEY = "midi_player.console.action.level.name";

    private static final String COMMAND_BRIEF_HELP = "Define the shell's log level.";

    private static final String COMMAND_BRIEF_HELP_KEY = "midi_player.console.action.level.help.short";

    private static final String COMMAND_HELP_KEY = "midi_player.console.action.level.help.long";

    private static final String COMMAND_RUN_NEW_LEVEL_MESSAGE = "Shell level set to ";

    private static final String COMMAND_RUN_NEW_LEVEL_MESSAGE_KEY = "midi_player.console.action.level.run.new_level";

    private static final String INTERNAL_COMMAND_BRIEF_HELP = "Sets the shell's log level to ";

    private static final String INTERNAL_COMMAND_BRIEF_HELP_KEY = "midi_player.console.action.level.internal_command.help.short";

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
    public static final String getHelp(LevelAction action) {
        if (!commandHelpInitialized && action != null) {
            StringBuilder stringBuilder = new StringBuilder();

            String commandIdsAsString = action.getCommandIdentifiersAsString(),
                    commandIdentifier = action.getDefaultCommandIdentifier();
            stringBuilder.append(action.getBriefHelp()).append("\n");
            stringBuilder.append("\t").append(commandIdsAsString).append("\n");
            stringBuilder.append("\n");

            try {
                stringBuilder.append(ResourceUtils.getMessage(COMMAND_HELP_KEY,
                        commandIdsAsString, commandIdentifier));
            } catch (MissingResourceException e) {
                LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_HELP_KEY + "\"", e);

                stringBuilder.append("You can set the value as follow:");
            }

            Map<IJssController.PublicationLevel, Collection<String>> argumentsByValue = action.getSwitchArgumentsByValue();
            if (argumentsByValue != null) {
                for (Map.Entry<IJssController.PublicationLevel, Collection<String>> entry : argumentsByValue.entrySet()) {
                    stringBuilder.append("\n");
                    stringBuilder.append("\t").append(commandIdentifier).append(" ").append(entry.getValue());
                }
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
    public static final String getBriefHelp(LevelAction action) {
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
    public LevelAction() {
        super();
        putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
        localeChanged();
    }

    public LevelAction(IJssController.PublicationLevel[] items) {
        this(items, null, (String[]) null);
    }

    public LevelAction(ComboBoxModel<IJssController.PublicationLevel> aModel) {
        this(aModel, null, (String[]) null);
    }

    public LevelAction(IJssController shellController) {
        this(shellController, (String[]) null);
    }

    public LevelAction(IJssController.PublicationLevel[] items, IJssController shellController) {
        this(items, shellController, (String[]) null);
    }

    public LevelAction(ComboBoxModel<IJssController.PublicationLevel> aModel, IJssController shellController) {
        this(aModel, shellController, (String[]) null);
    }

    public LevelAction(IJssController shellController, String... args) {
        super(ACTION_LABEL, shellController, args);
        putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
        localeChanged();
    }

    public LevelAction(IJssController.PublicationLevel[] items, IJssController shellController, String... args) {
        super(items, ACTION_LABEL, shellController, args);
        putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
        localeChanged();
    }

    public LevelAction(ComboBoxModel<IJssController.PublicationLevel> aModel, IJssController shellController, String... args) {
        super(aModel, ACTION_LABEL, shellController, args);
        putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
        localeChanged();
    }

    // #########################################################################
    @Override
    protected boolean doSwitch(IJssController shellController, IJssController.PublicationLevel switchValue) {
        if (shellController != null && switchValue != null) {
            if (shellController.getPublicationLevel() != switchValue) {
                shellController.setPublicationLevel(switchValue);
                String msg;
                try {
                    msg = ResourceUtils.getMessage(COMMAND_RUN_NEW_LEVEL_MESSAGE_KEY, switchValue);
                } catch (MissingResourceException e1) {
                    LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_RUN_NEW_LEVEL_MESSAGE_KEY + "\"", e1);
                    msg = COMMAND_RUN_NEW_LEVEL_MESSAGE + switchValue;
                }
                shellController.publish(IJssController.PublicationLevel.INFO, msg);
            }
            return true;
        }
        return false;
    }

    @Override
    public String[] getCommandIdentifiers() {
        return IDENTIFIERS;
    }

    @Override
    public final String getDefaultCommandIdentifier() {
        return super.getDefaultCommandIdentifier();
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
    public void setDefaultShellController(IJssController shellController) {
        super.setDefaultShellController(shellController);
        // Apply selected level to new shell controller
        doSwitch(shellController, (IJssController.PublicationLevel) this.getSelectedItem());
    }

    @Override
    protected Collection<ComboElementAction<IJssController.PublicationLevel>> initInnerElements() {
        ActionGroup innerGroup = new ActionGroup();
        ArrayList<ComboElementAction<IJssController.PublicationLevel>> innerElementActions = new ArrayList<>(dataModel.getSize());

        for (int i = 0, n = dataModel.getSize(); i < n; i++) {
            LevelComboElementAction elementAction = this.new LevelComboElementAction(this, dataModel.getElementAt(i));
            elementAction.setEnabled(this.isEnabled());
            ResourceUtils.addLocaleChangeListener(elementAction);
            innerElementActions.add(elementAction);
            innerGroup.add(elementAction);
        }
        setInnerGroup(innerGroup);

        return innerElementActions;
    }

    @Override
    public void resetInnerElements() {
        // Remove from Locale change notifiers internal actions
        if (this.hasInnerElementActions()) {
            for (ComboElementAction<IJssController.PublicationLevel> internalAction : this.getInnerElementActions()) {
                if (internalAction instanceof LocaleChangeListener) {
                    ResourceUtils.removeLocaleChangeListener((LocaleChangeListener) internalAction);
                }
            }
        }
        super.resetInnerElements();
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
    public final class LevelComboElementAction extends ComboElementAction<IJssController.PublicationLevel> implements LocaleChangeListener {

        protected LevelComboElementAction(AbstractJssComboAction<IJssController.PublicationLevel> parentAction, IJssController.PublicationLevel dataItem) {
            super(parentAction, dataItem);
            putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
            localeChanged();
        }

        @Override
        protected String initBriefHelp() {
            String briefHelp;
            try {
                briefHelp = ResourceUtils.getMessage(INTERNAL_COMMAND_BRIEF_HELP_KEY, getDataItem().toString());
            } catch (MissingResourceException e) {
                LOGGER.log(Level.SEVERE, "Resource not found: \"" + INTERNAL_COMMAND_BRIEF_HELP_KEY + "\"", e);
                briefHelp = INTERNAL_COMMAND_BRIEF_HELP + getDataItem().toString();
            }
            return briefHelp;
        }

        // #####################################################################
        @Override
        public void localeChanged() {
            localeChanged(null);
        }

        @Override
        public void localeChanged(PropertyChangeEvent evt) {
            resetBriefHelp();
            putValue(Action.SHORT_DESCRIPTION, this.getBriefHelp());
        }

        // #####################################################################
        @Override
        public final void putValue(String key, Object newValue) {
            super.putValue(key, newValue);
        }

        @Override
        public final String getDefaultCommandIdentifier() {
            return super.getDefaultCommandIdentifier();
        }
    }

}
