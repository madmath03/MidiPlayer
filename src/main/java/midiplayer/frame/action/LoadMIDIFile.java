package midiplayer.frame.action;

import java.beans.PropertyChangeEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import jswingshell.IJssController;
import jswingshell.action.AbstractJssAction;
import midiplayer.MidiPlayer;
import midiplayer.resources.LocaleChangeListener;
import midiplayer.resources.ResourceUtils;

/**
 * Action to load and execute a shell file.
 *
 * @author Mathieu Brunot
 */
public class LoadMIDIFile extends AbstractJssAction implements LocaleChangeListener {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = -4812239616899955075L;

  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger.getLogger(LoadMIDIFile.class.getName());

  /**
   * This action default identifier.
   *
   * @since 1.2
   */
  public static final String DEFAULT_IDENTIFIER = "loadMidiFile";

  private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

  private static final String COMMAND_BRIEF_HELP = "Load a MIDI file.";

  private static final String COMMAND_BRIEF_HELP_KEY =
      "midi_player.action.load_midi_file.help.short";

  private static final String COMMAND_HELP_KEY = "midi_player.action.load_midi_file.help.long";

  private static final String COMMAND_RUN_FILE_MANDATORY_WARNING_KEY =
      "midi_player.action.load_midi_file.run.file_mandatory";

  private static final String COMMAND_RUN_FILE_NOT_READABLE_KEY =
      "midi_player.action.load_midi_file.run.file_not_readable";

  private static final String COMMAND_RUN_FILE_PATH_INVALID_KEY =
      "midi_player.action.load_midi_file.run.file_path_invalid";

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
  public static final String getHelp(LoadMIDIFile action) {
    if (!commandHelpInitialized && action != null) {
      StringBuilder stringBuilder = new StringBuilder();

      String commandIdsAsString = action.getCommandIdentifiersAsString();
      stringBuilder.append(action.getBriefHelp());
      stringBuilder.append("\n");
      try {
        stringBuilder.append(ResourceUtils.getMessage(COMMAND_HELP_KEY, commandIdsAsString));
      } catch (MissingResourceException e) {
        LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_HELP_KEY + "\"", e);
        stringBuilder.append("\n")
            .append("Loads and adds the file at the given path to the playlist:");
        stringBuilder.append("\n\t").append(commandIdsAsString).append(" file_path ");
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
  public static final String getBriefHelp(LoadMIDIFile action) {
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

  public LoadMIDIFile(MidiPlayer midiPlayer, String name, Icon icon, IJssController shellController,
      String... args) {
    super(name, icon, shellController, args);
    if (midiPlayer == null) {
      throw new IllegalArgumentException("Midi player is null");
    }
    this.midiPlayer = midiPlayer;
  }

  public LoadMIDIFile(MidiPlayer midiPlayer, String name, IJssController shellController,
      String... args) {
    super(name, shellController, args);
    if (midiPlayer == null) {
      throw new IllegalArgumentException("Midi player is null");
    }
    this.midiPlayer = midiPlayer;
  }

  public LoadMIDIFile(MidiPlayer midiPlayer, IJssController shellController, String... args) {
    super(shellController, args);
    if (midiPlayer == null) {
      throw new IllegalArgumentException("Midi player is null");
    }
    this.midiPlayer = midiPlayer;
  }

  public LoadMIDIFile(IJssController shellController) {
    this(null, shellController, (String[]) null);
  }

  public LoadMIDIFile() {
    this(null, null, (String[]) null);
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
  public int run(IJssController shellController, String... args) {
    int commandReturnStatus = AbstractJssAction.ERROR;

    // Extract file path from parameters
    if (args != null && args.length > 1) {
      if (args.length >= 2) {
        List<Path> filesToLoad = getFilesToLoad(shellController, args);

        // Load all valid files at once
        if (!filesToLoad.isEmpty() && midiPlayer.addAll(filesToLoad)) {
          commandReturnStatus = AbstractJssAction.SUCCESS;
        }
      } else if (shellController != null) {
        shellController.publish(IJssController.PublicationLevel.WARNING, getHelp(shellController));
      }
    } else if (shellController != null) {
      LOGGER.log(Level.WARNING, "File path is mandatory!");
      String msg;
      try {
        msg = ResourceUtils.getMessage(COMMAND_RUN_FILE_MANDATORY_WARNING_KEY);
      } catch (MissingResourceException e) {
        LOGGER.log(Level.SEVERE,
            "Resource not found: \"" + COMMAND_RUN_FILE_MANDATORY_WARNING_KEY + "\"", e);
        msg = "File path is mandatory!";
      }
      shellController.publish(IJssController.PublicationLevel.ERROR, msg);
    }

    return commandReturnStatus;
  }

  private List<Path> getFilesToLoad(IJssController shellController, String... args) {
    List<Path> filesToLoad = new ArrayList<>(args.length);

    for (int i = 1, n = args.length; i < n; i++) {
      String filePath = args[i];
      Path path = Paths.get(filePath).toAbsolutePath().normalize();
      if (!Files.isReadable(path) || !filesToLoad.add(path)) {
        String msg;
        if (Files.exists(path)) {
          // File is not readable
          try {
            msg = ResourceUtils.getMessage(COMMAND_RUN_FILE_NOT_READABLE_KEY, path);
          } catch (MissingResourceException e) {
            LOGGER.log(Level.SEVERE,
                "Resource not found: \"" + COMMAND_RUN_FILE_NOT_READABLE_KEY + "\"", e);
            msg = "File is not readable: " + path;
          }
          LOGGER.log(Level.WARNING, msg);
        } else {
          // File does not exists
          try {
            msg = ResourceUtils.getMessage(COMMAND_RUN_FILE_PATH_INVALID_KEY, path);
          } catch (MissingResourceException e) {
            LOGGER.log(Level.SEVERE,
                "Resource not found: \"" + COMMAND_RUN_FILE_PATH_INVALID_KEY + "\"", e);
            msg = "No file found at path " + path;
          }
        }
        if (shellController != null) {
          shellController.publish(IJssController.PublicationLevel.ERROR, msg);
        }
      }
    }

    return filesToLoad;
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
