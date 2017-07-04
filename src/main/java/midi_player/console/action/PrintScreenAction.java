package midi_player.console.action;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.Icon;

import jswingshell.IJssController;
import jswingshell.action.AbstractThreadedJssAction;
import jswingshell.gui.JssTextAreaController;
import midi_player.console.resources.ResourceUtils;
import midi_player.resources.LocaleChangeListener;

/**
 * Action to print the shell screen and save it to a file.
 *
 * <p>
 * This action uses the {@code avax.​imageio.​ImageIO.write(RenderedImage, String, File)} and
 * {@code java.​awt.​GraphicsConfiguration.createCompatibleImage(int, int)}, to print a [@code
 * Component} and save it to a file.
 * </p>
 *
 * @author Mathieu Brunot
 */
public class PrintScreenAction extends jswingshell.action.AbstractThreadedJssAction
    implements LocaleChangeListener {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = 624623845126964677L;

  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger.getLogger(PrintScreenAction.class.getName());

  /**
   * This action default identifier.
   */
  public static final String DEFAULT_IDENTIFIER = "printscreen";

  private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

  private static final String COMMAND_BRIEF_HELP = "Print the shell screen and save it to a file.";

  private static final String COMMAND_BRIEF_HELP_KEY =
      "midi_player.console.action.print_screen.help.short";

  private static final String COMMAND_HELP_KEY =
      "midi_player.console.action.print_screen.help.long";

  private static final String COMMAND_RUN_FILE_MANDATORY_WARNING_KEY =
      "midi_player.console.action.print_screen.run.file_mandatory";

  private static final String COMMAND_RUN_FILE_PATH_INVALID_KEY =
      "midi_player.console.action.print_screen.run.file_path_invalid";

  private static final String COMMAND_RUN_FILE_WRITING_ERROR_KEY =
      "midi_player.console.action.print_screen.run.file_writing_error";

  private static final String COMMAND_RUN_FILE_CLOSING_ERROR_KEY =
      "midi_player.console.action.print_screen.run.file_closing_error";

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
  public static final String getHelp(PrintScreenAction action) {
    if (!commandHelpInitialized && action != null) {
      StringBuilder stringBuilder = new StringBuilder();

      String commandIdsAsString = action.getCommandIdentifiersAsString(),
          defaultCommandId = action.getDefaultCommandIdentifier();
      stringBuilder.append(action.getBriefHelp()).append("\n");
      stringBuilder.append("\n");
      try {
        stringBuilder.append(
            ResourceUtils.getMessage(COMMAND_HELP_KEY, commandIdsAsString, defaultCommandId));
      } catch (MissingResourceException e) {
        LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_HELP_KEY + "\"", e);
        stringBuilder.append("\n").append("Print the shell screen and save it to a file:");
        stringBuilder.append("\n\t").append(defaultCommandId).append(" file_path ");
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
  public static final String getBriefHelp(PrintScreenAction action) {
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
  private transient Component printComponent;

  public PrintScreenAction(Component comp, String name, Icon icon, IJssController shellController,
      String... args) {
    super(name, icon, shellController, args);
    if (comp == null) {
      throw new IllegalArgumentException("Component is null");
    }
    this.printComponent = comp;
  }

  public PrintScreenAction(Component comp, String name, IJssController shellController,
      String... args) {
    super(name, shellController, args);
    if (comp == null) {
      throw new IllegalArgumentException("Component is null");
    }
    this.printComponent = comp;
  }

  public PrintScreenAction(Component comp, JssTextAreaController shellController, String... args) {
    super(shellController, args);
    if (comp == null) {
      throw new IllegalArgumentException("Component is null");
    }
    this.printComponent = comp;
  }

  public PrintScreenAction(Component comp, JssTextAreaController shellController) {
    this(comp, shellController, (String[]) null);
  }

  public PrintScreenAction(Component comp) {
    this(comp, null, (String[]) null);
  }

  public Component getPrintComponent() {
    return printComponent;
  }

  public void setPrintComponent(Component printComponent) {
    this.printComponent = printComponent;
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
  protected AbstractJssActionWorker prepareWorker(IJssController shellController, String... args) {
    PrintScreenWorker worker = null;

    if (shellController != null) {
      // Extract file path from parameters
      if (args != null && args.length > 1) {
        if (args.length == 2) {
          String filePath = args[1];

          // Create a BufferedImage and create its Graphics
          BufferedImage image = GraphicsEnvironment.getLocalGraphicsEnvironment()
              .getDefaultScreenDevice().getDefaultConfiguration()
              .createCompatibleImage(printComponent.getWidth(), printComponent.getHeight());
          Graphics graphics = image.createGraphics();

          // Print to BufferedImage
          printComponent.paint(graphics);
          graphics.dispose();

          worker = new PrintScreenWorker(shellController, filePath, image);
        } else {
          shellController.publish(IJssController.PublicationLevel.WARNING,
              getHelp(shellController));
        }
      } else {
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
    }

    return worker;
  }

  // #########################################################################
  protected class PrintScreenWorker extends AbstractJssActionWorker {

    final String filePath;

    final BufferedImage image;

    public PrintScreenWorker(IJssController shellController, String filePath, BufferedImage image) {
      super(shellController);
      this.filePath = filePath;
      this.image = image;
    }

    @Override
    protected Integer doInBackground() throws Exception {
      int workerCommandReturnStatus = AbstractThreadedJssAction.SUCCESS;

      // Output the BufferedImage via ImageIO
      FileOutputStream fos = null;
      try {
        File f = new File(filePath);
        fos = new FileOutputStream(f);
        ImageIO.write(image, "png", fos);
      } catch (FileNotFoundException e) {
        LOGGER.log(Level.WARNING, "File path was not found or is not writable: " + filePath, e);
        String msg;
        try {
          msg = ResourceUtils.getMessage(COMMAND_RUN_FILE_PATH_INVALID_KEY, filePath);
        } catch (MissingResourceException e1) {
          LOGGER.log(Level.SEVERE,
              "Resource not found: \"" + COMMAND_RUN_FILE_PATH_INVALID_KEY + "\"", e1);
          msg = "File path was not found or is not writable: " + filePath;
        }
        this.publish(new JssActionWorkerChunk(IJssController.PublicationLevel.ERROR, msg));
        workerCommandReturnStatus = AbstractThreadedJssAction.ERROR;
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "Error occured while writing image file.", e);
        String msg;
        try {
          msg =
              ResourceUtils.getMessage(COMMAND_RUN_FILE_WRITING_ERROR_KEY, e.getLocalizedMessage());
        } catch (MissingResourceException e1) {
          LOGGER.log(Level.SEVERE,
              "Resource not found: \"" + COMMAND_RUN_FILE_WRITING_ERROR_KEY + "\"", e1);
          msg = "Error occured while writing image file: " + e.getMessage();
        }
        this.publish(new JssActionWorkerChunk(IJssController.PublicationLevel.FATAL_ERROR, msg));
        workerCommandReturnStatus = AbstractThreadedJssAction.ERROR;
      } finally {
        if (fos != null) {
          try {
            fos.close();
          } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error occured while closing file output stream.", ex);
            String msg;
            try {
              msg = ResourceUtils.getMessage(COMMAND_RUN_FILE_CLOSING_ERROR_KEY,
                  ex.getLocalizedMessage());
            } catch (MissingResourceException e1) {
              LOGGER.log(Level.SEVERE,
                  "Resource not found: \"" + COMMAND_RUN_FILE_CLOSING_ERROR_KEY + "\"", e1);
              msg = "Error occured while closing file output stream: " + ex.getMessage();
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
  }

}
