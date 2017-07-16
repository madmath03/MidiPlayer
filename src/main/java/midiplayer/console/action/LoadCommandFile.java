package midiplayer.console.action;

import java.beans.PropertyChangeEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import jswingshell.AbstractJssController;
import jswingshell.IJssController;
import jswingshell.action.AbstractThreadedJssAction;
import midiplayer.console.resources.ResourceUtils;
import midiplayer.resources.LocaleChangeListener;

/**
 * Action to load and execute a shell file.
 *
 * @author Mathieu Brunot
 */
public class LoadCommandFile extends AbstractThreadedJssAction
    implements LocaleChangeListener {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = -3467996280036789616L;

  /**
   * Logger.
   */
  private static final Logger LOGGER =
      Logger.getLogger(LoadCommandFile.class.getName());

  /**
   * Prefix for start of lines to indicate that a command should not display itself before
   * execution.
   */
  private static final String MUTE_PREFIX = "@";

  /**
   * Prefix for start of lines to indicate a comment.
   */
  private static final String COMMENT_PREFIX = "//";

  /**
   * This action default identifier.
   *
   * @since 1.2
   */
  public static final String DEFAULT_IDENTIFIER = "loadCommandFile";

  private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

  private static final String COMMAND_BRIEF_HELP =
      "Load and execute a shell file.";

  private static final String COMMAND_BRIEF_HELP_KEY =
      "midiplayer.console.action.load_command_file.help.short";

  private static final String COMMAND_HELP_KEY =
      "midiplayer.console.action.load_command_file.help.long";

  private static final String COMMAND_RUN_FILE_MANDATORY_WARNING_KEY =
      "midiplayer.console.action.load_command_file.run.file_mandatory";

  private static final String COMMAND_RUN_FILE_PATH_INVALID_KEY =
      "midiplayer.console.action.load_command_file.run.file_path_invalid";

  private static final String COMMAND_RUN_FILE_READING_ERROR_KEY =
      "midiplayer.console.action.load_command_file.run.file_reading_error";

  private static final String COMMAND_RUN_BUFFERED_FILE_CLOSING_ERROR_KEY =
      "midiplayer.console.action.load_command_file.run.buffered_file_closing_error";

  private static final String COMMAND_RUN_FILE_CLOSING_ERROR_KEY =
      "midiplayer.console.action.load_command_file.run.file_closing_error";

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
  public static final String getHelp(LoadCommandFile action) {
    if (!commandHelpInitialized && action != null) {
      StringBuilder stringBuilder = new StringBuilder();

      String commandIdsAsString = action.getCommandIdentifiersAsString();
      stringBuilder.append(action.getBriefHelp());
      stringBuilder.append("\n");
      try {
        stringBuilder.append(
            ResourceUtils.getMessage(COMMAND_HELP_KEY, commandIdsAsString));
      } catch (MissingResourceException e) {
        LOGGER.log(Level.SEVERE,
            "Resource not found: \"" + COMMAND_HELP_KEY + "\"", e);
        stringBuilder.append("\n")
            .append("Loads and executes the file at the given path:");
        stringBuilder.append("\n\t").append(commandIdsAsString)
            .append(" file_path ");
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
  public static final String getBriefHelp(LoadCommandFile action) {
    if (!commandBriefHelpInitialized && action != null) {
      try {
        commandBriefHelp = ResourceUtils.getMessage(COMMAND_BRIEF_HELP_KEY);
      } catch (MissingResourceException e) {
        LOGGER.log(Level.SEVERE,
            "Resource not found: \"" + COMMAND_BRIEF_HELP_KEY + "\"", e);
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
  public LoadCommandFile(String name, Icon icon, IJssController shellController,
      String... args) {
    super(name, icon, shellController, args);
  }

  public LoadCommandFile(String name, IJssController shellController,
      String... args) {
    super(name, shellController, args);
  }

  public LoadCommandFile(IJssController shellController, String... args) {
    super(shellController, args);
  }

  public LoadCommandFile(IJssController shellController) {
    this(shellController, (String[]) null);
  }

  public LoadCommandFile() {
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
  protected AbstractJssActionWorker prepareWorker(
      IJssController shellController, String... args) {
    LoadCommandFileWorker worker = null;

    if (shellController != null) {
      // Extract file path from parameters
      if (args != null && args.length > 1) {
        if (args.length == 2) {
          String filePath = args[1];
          Path path = Paths.get(filePath);
          worker = new LoadCommandFileWorker(shellController, filePath);
          if (Files.isReadable(path)) {
            worker = new LoadCommandFileWorker(shellController, filePath);
          } else {
            String msg;
            try {
              msg = ResourceUtils
                  .getMessage(COMMAND_RUN_FILE_MANDATORY_WARNING_KEY);
            } catch (MissingResourceException e) {
              LOGGER.log(Level.SEVERE, "Resource not found: \""
                  + COMMAND_RUN_FILE_MANDATORY_WARNING_KEY + "\"", e);
              msg = "File path is mandatory!";
            }
            shellController.publish(IJssController.PublicationLevel.ERROR, msg);
          }
        } else {
          shellController.publish(IJssController.PublicationLevel.WARNING,
              getHelp(shellController));
        }
      } else {
        LOGGER.log(Level.WARNING, "File path is mandatory!");
        String msg;
        try {
          msg =
              ResourceUtils.getMessage(COMMAND_RUN_FILE_MANDATORY_WARNING_KEY);
        } catch (MissingResourceException e) {
          LOGGER.log(Level.SEVERE, "Resource not found: \""
              + COMMAND_RUN_FILE_MANDATORY_WARNING_KEY + "\"", e);
          msg = "File path is mandatory!";
        }
        shellController.publish(IJssController.PublicationLevel.ERROR, msg);
      }
    }

    return worker;
  }

  // #########################################################################
  protected class LoadCommandFileWorker extends AbstractJssActionWorker {

    final String filePath;

    public LoadCommandFileWorker(IJssController shellController,
        String filePath) {
      super(shellController);
      this.filePath = filePath;
    }

    @Override
    protected Integer doInBackground() throws Exception {
      int workerCommandReturnStatus = AbstractThreadedJssAction.SUCCESS;

      // Start reading file
      FileReader fr = null;
      BufferedReader br = null;
      try {
        fr = new FileReader(filePath);
        br = new BufferedReader(fr);

        int lineCount = 0;
        for (String line; (line = br.readLine()) != null;) {
          lineCount++;
          String command = line != null ? line.trim() : "";

          IJssController shell = getShellController();
          if (command != null && !command.isEmpty()) {
            // Add line to shell
            if (command.startsWith(MUTE_PREFIX)) {
              command = command.substring(MUTE_PREFIX.length());
            } else if (command.startsWith(COMMENT_PREFIX)) {
              continue;
            } else {
              shell.addNewLineToShell(command);
            }

            if (shell instanceof AbstractJssController) {
              // Execute command without adding it to history
              workerCommandReturnStatus |= ((AbstractJssController) shell)
                  .interpretCommand(command, false);
            } else {
              workerCommandReturnStatus |= shell.interpretCommand(command);
            }
          }

          // Stop reading the file if an error occurs
          if (AbstractThreadedJssAction.SUCCESS != workerCommandReturnStatus) {
            this.publish(new JssActionWorkerChunk(
                IJssController.PublicationLevel.WARNING,
                "Stop reading file due to internal command file error: command \""
                    + command + "\" at line " + lineCount
                    + " returned status code " + workerCommandReturnStatus));
            break;
          }
        }
      } catch (FileNotFoundException e) {
        LOGGER.log(Level.WARNING, "File path was not found: " + filePath, e);
        String msg;
        try {
          msg = ResourceUtils.getMessage(COMMAND_RUN_FILE_PATH_INVALID_KEY,
              filePath);
        } catch (MissingResourceException e1) {
          LOGGER.log(Level.SEVERE, "Resource not found: \""
              + COMMAND_RUN_FILE_PATH_INVALID_KEY + "\"", e1);
          msg = "File path was not found: " + filePath;
        }
        this.publish(new JssActionWorkerChunk(
            IJssController.PublicationLevel.ERROR, msg));
        workerCommandReturnStatus = AbstractThreadedJssAction.ERROR;
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "Error occured while reading file.", e);
        String msg;
        try {
          msg = ResourceUtils.getMessage(COMMAND_RUN_FILE_READING_ERROR_KEY,
              e.getLocalizedMessage());
        } catch (MissingResourceException e1) {
          LOGGER.log(Level.SEVERE, "Resource not found: \""
              + COMMAND_RUN_FILE_READING_ERROR_KEY + "\"", e1);
          msg = "Error occured while reading file: " + e.getMessage();
        }
        this.publish(new JssActionWorkerChunk(
            IJssController.PublicationLevel.FATAL_ERROR, msg));
        workerCommandReturnStatus = AbstractThreadedJssAction.ERROR;
      } finally {
        if (br != null) {
          try {
            br.close();
          } catch (IOException ex) {
            LOGGER.log(Level.SEVERE,
                "Error occured while closing buffered file reader.", ex);
            String msg;
            try {
              msg = ResourceUtils.getMessage(
                  COMMAND_RUN_BUFFERED_FILE_CLOSING_ERROR_KEY,
                  ex.getLocalizedMessage());
            } catch (MissingResourceException e1) {
              LOGGER.log(Level.SEVERE, "Resource not found: \""
                  + COMMAND_RUN_BUFFERED_FILE_CLOSING_ERROR_KEY + "\"", e1);
              msg = "Error occured while closing buffered file reader: "
                  + ex.getMessage();
            }
            this.publish(new JssActionWorkerChunk(
                IJssController.PublicationLevel.FATAL_ERROR, msg));
            workerCommandReturnStatus = AbstractThreadedJssAction.ERROR;
          }
        }
        if (fr != null) {
          try {
            fr.close();
          } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error occured while closing file reader.",
                ex);
            String msg;
            try {
              msg = ResourceUtils.getMessage(COMMAND_RUN_FILE_CLOSING_ERROR_KEY,
                  ex.getLocalizedMessage());
            } catch (MissingResourceException e1) {
              LOGGER.log(Level.SEVERE, "Resource not found: \""
                  + COMMAND_RUN_FILE_CLOSING_ERROR_KEY + "\"", e1);
              msg =
                  "Error occured while closing file reader: " + ex.getMessage();
            }
            this.publish(new JssActionWorkerChunk(
                IJssController.PublicationLevel.FATAL_ERROR, msg));
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
  }

}
