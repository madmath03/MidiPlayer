package midi_player.frame.action;

import java.beans.PropertyChangeEvent;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import jswingshell.IJssController;
import jswingshell.action.AbstractJssSwitchAction;
import midi_player.MidiPlayer;
import midi_player.resources.LocaleChangeListener;
import midi_player.resources.ResourceUtils;

/**
 * Action to loop a MIDI song.
 *
 * @author Mathieu Brunot
 */
public final class LoopAction extends AbstractJssSwitchAction implements LocaleChangeListener {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LoopAction.class.getName());

    /**
     * This action default identifier.
     *
     * @since 1.2
     */
    public static final String DEFAULT_IDENTIFIER = "loopSong";

    private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

    private static final String ACTION_LABEL = "Repeat";

    private static final String ACTION_LABEL_KEY = "midi_player.action.loop.name";

    private static final String COMMAND_BRIEF_HELP = "Repeat the current MIDI song.";

    private static final String COMMAND_BRIEF_HELP_KEY = "midi_player.action.loop.help.short";

    private static final String COMMAND_HELP_KEY = "midi_player.action.loop.help.long";

    private static final String ICON_KEY = "control_repeat_blue.png";

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
    public static final String getHelp(LoopAction action) {
        if (!commandHelpInitialized && action != null) {
            StringBuilder stringBuilder = new StringBuilder();

            String commandIdsAsString = action.getCommandIdentifiersAsString();
            stringBuilder.append(action.getBriefHelp());
            stringBuilder.append("\n");
            try {
                stringBuilder.append(ResourceUtils.getMessage(COMMAND_HELP_KEY, commandIdsAsString, action.getOnArgumentsAsString(), action.getOffArgumentsAsString()));
            } catch (MissingResourceException e) {
                LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_HELP_KEY + "\"", e);
                stringBuilder.append("\n").append("Repeats the current song in the playlist:");
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
    public static final String getBriefHelp(LoopAction action) {
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
    private transient MidiPlayer midiPlayer;
    
    public LoopAction(MidiPlayer midiPlayer, Boolean isSelected, IJssController shellController, String... args) {
        super(isSelected, ACTION_LABEL, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL), shellController, args);
        if (midiPlayer == null) {
            throw new IllegalArgumentException("Midi player is null");
        }
        this.midiPlayer = midiPlayer;
        putValue(Action.LARGE_ICON_KEY, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL, true));
        putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
        localeChanged();
    }

    public LoopAction(MidiPlayer midiPlayer, IJssController shellController) {
        this(midiPlayer, false, shellController, (String[]) null);
    }

    public LoopAction(MidiPlayer midiPlayer) {
        this(midiPlayer, false, null, (String[]) null);
    }

    public MidiPlayer getMidiPlayer() {
        return midiPlayer;
    }

    public void setMidiPlayer(MidiPlayer midiPlayer) {
        this.midiPlayer = midiPlayer;
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
    protected boolean doSwitch(IJssController shellController, Boolean switchValue) {
        if (switchValue == null || MidiPlayer.getInstance() == null) {
            return false;
        }

        midiPlayer.setLooping(switchValue);
        return true;
    }

    @Override
    public void setDefaultShellController(IJssController shellController) {
        super.setDefaultShellController(shellController);
        this.doSwitch(shellController, this.isSelected());
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
