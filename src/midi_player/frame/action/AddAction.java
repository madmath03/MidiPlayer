package midi_player.frame.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;
import jswingshell.IJssController;
import midi_player.MidiPlayer;
import midi_player.resources.ResourceUtils;

/**
 * Action to add a MIDI file to playlist.
 *
 * <p>
 * This action inherits from the standard {@code LoadMIDIFile} and initializes
 * GUI related properties:</p>
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
public final class AddAction extends LoadMIDIFile {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AddAction.class.getName());

    private static final String ACTION_LABEL = "Add";

    private static final String ACTION_LABEL_KEY = "midi_player.action.add_midi_file.name";

    private static final String COMMAND_RUN_FILE_FILTER_DESCRIPTION_KEY = "midi_player.action.add_midi_file.run.file_filter_description";

    private static final String ICON_KEY = "file_extension_mid.png";

    // #########################################################################
    private transient JFileChooser fileChooser;

    private transient Component parent;

    public AddAction(JFileChooser fileChooser, Component parent, MidiPlayer midiPlayer, IJssController shellController, String... args) {
        super(midiPlayer, ACTION_LABEL, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL), shellController, args);
//        if (shellController == null) {
//            throw new IllegalArgumentException("Shell controller is null");
//        }
        this.setFileChooser(fileChooser);
        this.setParent(parent);
        putValue(Action.LARGE_ICON_KEY, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL, true));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
        localeChanged();
    }

    public AddAction(JFileChooser fileChooser, Component parent, MidiPlayer midiPlayer, IJssController shellController) {
        this(fileChooser, parent, midiPlayer, shellController, (String[]) null);
    }

    public AddAction(JFileChooser fileChooser, Component parent, MidiPlayer midiPlayer) {
        this(fileChooser, parent, midiPlayer, null, (String[]) null);
    }

    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    public void setFileChooser(JFileChooser fileChooser) {
        // Set the file chooser
        if (fileChooser == null) {
            throw new IllegalArgumentException("File chooser is null");
        }
        this.fileChooser = fileChooser;
    }

    public Component getParent() {
        return parent;
    }

    public void setParent(Component parent) {
        // Set the parent
        if (parent == null) {
            throw new IllegalArgumentException("Parent is null");
        }
        this.parent = parent;
    }

    // #########################################################################
    @Override
    protected String[] extractArgumentsFromEvent(ActionEvent e) {
        String[] eventArgs = null;

        String commandIdentifier = getDefaultCommandIdentifier();
        if (commandIdentifier != null) {
            // Construct file extension filter
            String description;
            try {
                description = ResourceUtils.getMessage(COMMAND_RUN_FILE_FILTER_DESCRIPTION_KEY);
            } catch (MissingResourceException e1) {
                LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_RUN_FILE_FILTER_DESCRIPTION_KEY + "\"", e1);
                description = "MIDI file";
            }
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    description, "midi", "mid");

            // Open file chooser and select file
            fileChooser.setFileFilter(filter);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(true);
            int returnVal = fileChooser.showOpenDialog(parent);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File[] selectedFiles = fileChooser.getSelectedFiles();
                eventArgs = new String[1 + selectedFiles.length];
                eventArgs[0] = commandIdentifier;
                for (int i = 0, n = selectedFiles.length; i < n; i++) {
                    File file = selectedFiles[i];
                    eventArgs[i + 1] = file.getAbsolutePath();
                }
            }
        }

        return eventArgs;
    }

    // #########################################################################
    @Override
    public void localeChanged() {
        localeChanged(null);
    }

    @Override
    public void localeChanged(PropertyChangeEvent evt) {
        super.localeChanged(evt);
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
