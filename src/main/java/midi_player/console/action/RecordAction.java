package midi_player.console.action;

import java.beans.PropertyChangeEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;

import jswingshell.AbstractJssController;
import jswingshell.IJssController;
import jswingshell.action.AbstractThreadedJssAction;
import jswingshell.action.IJssAction;
import midi_player.console.resources.ResourceUtils;
import midi_player.resources.LocaleChangeListener;

/**
 * Action to print the shell screen and save it to a file.
 *
 * <p>
 * This action's stop and start arguments are used as markers to define the recording scope. When
 * saving the recording to a file, the action will look for the <em>nearest</em> start/stop markers.
 * If none are found, the recording will simply go from the start of the application to the current
 * save, even if save actions were done in the meantime.
 * </p>
 *
 * <p>
 * All successful record actions in the recording scope are omitted.
 * </p>
 *
 * @author Mathieu Brunot
 */
public final class RecordAction extends jswingshell.action.AbstractThreadedJssAction
    implements LocaleChangeListener {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = -5401816901198699982L;

  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger.getLogger(RecordAction.class.getName());

  /**
   * This action default identifier.
   */
  public static final String DEFAULT_IDENTIFIER = "record";

  private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

  private static final String ACTION_LABEL = "Record";

  private static final String ACTION_LABEL_KEY = "midi_player.console.action.record.name";

  private static final String COMMAND_BRIEF_HELP = "Record the executed shell commands to a file.";

  private static final String COMMAND_BRIEF_HELP_KEY =
      "midi_player.console.action.record.help.short";

  private static final String COMMAND_HELP_KEY = "midi_player.console.action.record.help.long";

  private static final String COMMAND_RUN_FILE_WRITING_ERROR_KEY =
      "midi_player.console.action.record.run.file_writing_error";

  private static final String COMMAND_RUN_BUFFERED_FILE_CLOSING_ERROR_KEY =
      "midi_player.console.action.record.run.buffered_file_closing_error";

  private static final String COMMAND_RUN_FILE_CLOSING_ERROR_KEY =
      "midi_player.console.action.record.run.file_closing_error";

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
  public static final String getHelp(RecordAction action) {
    if (!commandHelpInitialized && action != null) {
      StringBuilder stringBuilder = new StringBuilder();

      String commandIdsAsString = action.getCommandIdentifiersAsString(),
          startArgument = RECORD_START, stopArgument = RECORD_STOP, saveArgument = RECORD_SAVE;
      stringBuilder.append(action.getBriefHelp()).append("\n");
      stringBuilder.append("\n");
      try {
        stringBuilder.append(ResourceUtils.getMessage(COMMAND_HELP_KEY, commandIdsAsString,
            startArgument, stopArgument, saveArgument));
      } catch (MissingResourceException e) {
        LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_HELP_KEY + "\"", e);
        stringBuilder.append("Start recording commands:").append("\n");
        stringBuilder.append("\t").append(commandIdsAsString).append(" ").append(startArgument)
            .append("\n");
        stringBuilder.append("If a start was already entered, ")
            .append("only the last one before a stop or save command ")
            .append("will be taken into account.").append("\n");
        stringBuilder.append("\n");
        stringBuilder.append("Stop recording commands:").append("\n");
        stringBuilder.append("\t").append(commandIdsAsString).append(" ").append(stopArgument)
            .append("\n");
        stringBuilder.append("If a stop was already entered, ")
            .append("only the first one after a start command ")
            .append("will be taken into account.").append("\n");
        stringBuilder.append("\n");
        stringBuilder.append("Save recording commands:").append("\n");
        stringBuilder.append("\t").append(commandIdsAsString).append(" ").append(saveArgument)
            .append(" file_path").append("\n");
        stringBuilder.append("If no stop was entered, ")
            .append("the record will go from the last start command ")
            .append("to the current save command.").append("\n");
        stringBuilder.append("The recording will not stop until a stop command is entered, ")
            .append("or until the application exits, ")
            .append("meaning that a new save command will go from the last start command ")
            .append("to the current save command, even if there were save done in between.");
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
  public static final String getBriefHelp(RecordAction action) {
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

  protected static final String RECORD_START = "START";

  protected static final String RECORD_STOP = "STOP";

  protected static final String RECORD_SAVE = "SAVE";

  // #########################################################################
  public RecordAction(AbstractJssController shellController) {
    this(shellController, (String[]) null);
  }

  public RecordAction(AbstractJssController shellController, String... args) {
    super(ACTION_LABEL, shellController, args);
    if (shellController == null) {
      throw new IllegalArgumentException("Shell controller is null");
    }
    putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
    localeChanged();
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
    int commandReturnStatus;

    AbstractJssActionWorker worker = this.prepareWorker(shellController, args);
    if (worker != null) {
      commandReturnStatus = IJssAction.IN_PROGRESS;
      if (shellController != null) {
        shellController.lockCommandLine();
      }
      worker.execute();
    } else {
      commandReturnStatus = IJssAction.SUCCESS;
    }

    return commandReturnStatus;
  }

  @Override
  protected AbstractJssActionWorker prepareWorker(IJssController shellController, String... args) {
    RecordWorker worker = null;

    if (shellController != null && shellController instanceof AbstractJssController) {
      if (args == null || args.length == 1 || args.length > 3) {
        shellController.publish(IJssController.PublicationLevel.WARNING, getHelp(shellController));
      } else {
        String recordAction = args[1].toUpperCase();
        switch (recordAction) {
          case RECORD_START:
          case RECORD_STOP:
            if (args.length == 3) {
              shellController.publish(IJssController.PublicationLevel.WARNING,
                  getHelp(shellController));
            } else {
              // Do nothing on start or stop
              // Start/Stop commands will be used as markers for save
            }
            break;
          case RECORD_SAVE:
            if (args.length == 3) {
              String filePath = args[2];
              String[] recordedCommands =
                  extractRecordedCommands((AbstractJssController) shellController);
              worker = new RecordWorker(shellController, filePath, recordedCommands);
            } else {
              shellController.publish(IJssController.PublicationLevel.WARNING,
                  getHelp(shellController));
            }
            break;
          default:
            shellController.publish(IJssController.PublicationLevel.WARNING,
                getHelp(shellController));
            break;
        }
      }
    }

    return worker;
  }

  private String[] extractRecordedCommands(AbstractJssController shellController) {
    Deque<String> commands = new ArrayDeque<>();

    AbstractJssController.CommandHistory commandHistory = shellController.getCommandHistory();
    int endIndex = -1;

    for (int i = commandHistory.size() - 1; i >= 0; i--) {
      String command = commandHistory.previous();
      String[] args = shellController.getCommandLineParser().extractCommandArguments(command);

      if (args != null && args.length > 0) {

        if ((command.startsWith(IDENTIFIERS[0]) || command.startsWith("\"" + IDENTIFIERS[0]))
            && args.length > 1) {
          String recordAction = args[1].toUpperCase().replace("\"", "");

          switch (recordAction) {
            case RECORD_START:
              // If start found with or without stop
              if (endIndex <= -1 || endIndex > i) {
                // Force break of loop
                i = -1;
              }
              break;
            case RECORD_STOP:
              commands.clear();
              endIndex = i;
              break;
            case RECORD_SAVE:
              // Ignore record save commands
              break;
          }
        } else {
          commands.addFirst(command);
        }

      }
    }

    // Store commands in array
    String[] commandsArray = new String[commands.size()];
    commandsArray = commands.toArray(commandsArray);

    return commandsArray;
  }

  // #########################################################################
  protected class RecordWorker extends AbstractJssActionWorker {

    final String filePath;

    final String[] commands;

    public RecordWorker(IJssController shellController, String filePath, String[] commands) {
      super(shellController);
      this.filePath = filePath;
      this.commands = commands;
    }

    @Override
    protected Integer doInBackground() throws Exception {
      int workerCommandReturnStatus = AbstractThreadedJssAction.SUCCESS;

      // Start wrting file
      FileWriter fw = null;
      BufferedWriter bw = null;
      try {
        fw = new FileWriter(filePath);
        bw = new BufferedWriter(fw);

        for (String command : commands) {
          bw.write(command);
          bw.write("\n");
        }
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "Error occured while writing file.", e);

        String msg;
        try {
          msg =
              ResourceUtils.getMessage(COMMAND_RUN_FILE_WRITING_ERROR_KEY, e.getLocalizedMessage());
        } catch (MissingResourceException e1) {
          LOGGER.log(Level.SEVERE,
              "Resource not found: \"" + COMMAND_RUN_FILE_WRITING_ERROR_KEY + "\"", e1);
          msg = "Error occured while reading file: " + e.getMessage();
        }
        this.publish(new JssActionWorkerChunk(IJssController.PublicationLevel.FATAL_ERROR, msg));

        workerCommandReturnStatus = AbstractThreadedJssAction.ERROR;
      } finally {
        if (bw != null) {
          try {
            bw.close();
          } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error occured while closing buffered file writer.", ex);
            String msg;
            try {
              msg = ResourceUtils.getMessage(COMMAND_RUN_BUFFERED_FILE_CLOSING_ERROR_KEY,
                  ex.getLocalizedMessage());
            } catch (MissingResourceException e1) {
              LOGGER.log(Level.SEVERE,
                  "Resource not found: \"" + COMMAND_RUN_BUFFERED_FILE_CLOSING_ERROR_KEY + "\"",
                  e1);
              msg = "Error occured while closing buffered file writer: " + ex.getMessage();
            }
            this.publish(
                new JssActionWorkerChunk(IJssController.PublicationLevel.FATAL_ERROR, msg));
            workerCommandReturnStatus = AbstractThreadedJssAction.ERROR;
          }
        }
        if (fw != null) {
          try {
            fw.close();
          } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error occured while closing file writer.", ex);
            String msg;
            try {
              msg = ResourceUtils.getMessage(COMMAND_RUN_FILE_CLOSING_ERROR_KEY,
                  ex.getLocalizedMessage());
            } catch (MissingResourceException e1) {
              LOGGER.log(Level.SEVERE,
                  "Resource not found: \"" + COMMAND_RUN_FILE_CLOSING_ERROR_KEY + "\"", e1);
              msg = "Error occured while closing file writer: " + ex.getMessage();
            }
            this.publish(
                new JssActionWorkerChunk(IJssController.PublicationLevel.FATAL_ERROR, msg));
            workerCommandReturnStatus = AbstractThreadedJssAction.ERROR;
          }
        }
      }

      return workerCommandReturnStatus;
    }

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
