package midiplayer.console.action;

import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;

import jswingshell.IJssController;
import jswingshell.action.AbstractJssAction;
import jswingshell.gui.JssTextArea;
import jswingshell.gui.JssTextAreaController;
import midiplayer.console.resources.ResourceUtils;
import midiplayer.resources.LocaleChangeListener;

/**
 *
 * @author Mathieu Brunot
 */
public final class ZoomAction extends jswingshell.action.AbstractJssAction
    implements LocaleChangeListener {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = -4383123330658694790L;

  /**
   * Logger.
   */
  private static final Logger LOGGER =
      Logger.getLogger(ZoomAction.class.getName());

  /**
   * This action default identifier.
   */
  public static final String DEFAULT_IDENTIFIER = "zoom";

  private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

  private static final String ACTION_LABEL = "Zoom";

  private static final String ACTION_LABEL_KEY =
      "midiplayer.console.action.zoom.name";

  private static final String COMMAND_BRIEF_HELP =
      "Change the size of the shell text.";

  private static final String COMMAND_BRIEF_HELP_KEY =
      "midiplayer.console.action.zoom.help.short";

  private static final String COMMAND_HELP_KEY =
      "midiplayer.console.action.zoom.help.long";

  private static final String COMMAND_RUN_INVALID_ARGUMENT_ERROR_KEY =
      "midiplayer.console.action.zoom.run.invalid_argument_error";

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
  public static final String getHelp(ZoomAction action) {
    if (!commandHelpInitialized && action != null) {
      StringBuilder stringBuilder = new StringBuilder();

      String commandIdsAsString = action.getCommandIdentifiersAsString(),
          zoomInArgument = ZOOM_IN, zoomFitArgument = ZOOM_FIT,
          zoomOutArgument = ZOOM_OUT,
          commandIdentifier = action.getDefaultCommandIdentifier(),
          sampleZoomInArgument = "+42", sampleZoomOutArgument = "-0.5";
      stringBuilder.append(action.getBriefHelp()).append("\n");
      stringBuilder.append("\n");
      try {
        stringBuilder.append(ResourceUtils.getMessage(COMMAND_HELP_KEY,
            commandIdsAsString, zoomInArgument, zoomFitArgument,
            zoomOutArgument, commandIdentifier, sampleZoomInArgument,
            sampleZoomOutArgument));
      } catch (MissingResourceException e) {
        LOGGER.log(Level.SEVERE,
            "Resource not found: \"" + COMMAND_HELP_KEY + "\"", e);
        stringBuilder.append("Shell text size can be increased by one point:")
            .append("\n");
        stringBuilder.append("\t").append(commandIdsAsString).append(" ")
            .append(zoomInArgument).append("\n");
        stringBuilder.append("\n");
        stringBuilder
            .append("Shell text size can be reset to its default values:")
            .append("\n");
        stringBuilder.append("\t").append(commandIdsAsString).append(" ")
            .append(zoomFitArgument).append("\n");
        stringBuilder.append("\n");
        stringBuilder.append("Shell text size can be decreased by one point:")
            .append("\n");
        stringBuilder.append("\t").append(commandIdsAsString).append(" ")
            .append(zoomOutArgument).append("\n");
        stringBuilder.append("\n");
        stringBuilder
            .append("Shell text size can also be derived by a given value:")
            .append("\n");
        stringBuilder.append("\t").append(commandIdsAsString).append(" number")
            .append("\n");
        stringBuilder.append("Example: ").append("\n");
        stringBuilder.append(commandIdentifier).append(sampleZoomInArgument)
            .append("\n");
        stringBuilder.append("Example: ").append("\n");
        stringBuilder.append(commandIdentifier).append(sampleZoomOutArgument);
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
  public static final String getBriefHelp(ZoomAction action) {
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

  protected static final String ZOOM_IN = "+";

  protected static final String ZOOM_FIT = "0";

  protected static final String ZOOM_OUT = "-";

  // #########################################################################
  private transient Font defaultFont;

  public ZoomAction(JssTextAreaController shellController) {
    this(shellController, (String[]) null);
  }

  public ZoomAction(JssTextAreaController shellController, String... args) {
    super(ACTION_LABEL, shellController, args);
    if (shellController == null) {
      throw new IllegalArgumentException("Shell controller is null");
    }
    if (shellController.getView() == null) {
      throw new IllegalArgumentException("Shell view is null");
    }
    JssTextArea view = (JssTextArea) shellController.getView();
    this.defaultFont = view.getFont();

    putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
    localeChanged();
  }

  public Font getDefaultFont() {
    return defaultFont;
  }

  public void setDefaultFont(Font defaultFont) {
    this.defaultFont = defaultFont;
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

    if (shellController == null
        || !(shellController.getView() instanceof JssTextArea)) {
      commandReturnStatus = AbstractJssAction.ERROR;
    } else {
      JssTextArea view = (JssTextArea) shellController.getView();
      if (args == null || args.length == 1 || args.length > 2) {
        shellController.publish(IJssController.PublicationLevel.WARNING,
            getHelp(shellController));
      } else if (args.length == 0) {
        shellController.publish(IJssController.PublicationLevel.WARNING,
            getHelp(shellController));
      } else {
        float size = -1;
        switch (args[1]) {
          case ZOOM_IN:
            size = view.getFont().getSize2D() + 1;
            break;
          case ZOOM_FIT:
            size = defaultFont.getSize2D();
            break;
          case ZOOM_OUT:
            size = view.getFont().getSize2D() - 1;
            break;
          default:
            try {
              size = view.getFont().getSize2D() + Float.parseFloat(args[1]);
            } catch (NumberFormatException e) {
              LOGGER.log(Level.SEVERE, "Invalid zoom argument: {0}", args[1]);

              String msg;
              try {
                msg = ResourceUtils.getMessage(
                    COMMAND_RUN_INVALID_ARGUMENT_ERROR_KEY, args[1]);
              } catch (MissingResourceException e1) {
                LOGGER.log(Level.SEVERE, "Resource not found: \""
                    + COMMAND_RUN_INVALID_ARGUMENT_ERROR_KEY + "\"", e1);
                msg = "Invalid zoom argument: " + args[1];
              }
              shellController.publish(IJssController.PublicationLevel.WARNING,
                  msg);

              shellController.publish(IJssController.PublicationLevel.WARNING,
                  getHelp(shellController));
            }
            break;
        }
        if (size > 0) {
          view.setFont(defaultFont.deriveFont(size));
        }
      }
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
    try {
      ResourceUtils.setTextAndMnemonic(this, ACTION_LABEL_KEY);
    } catch (MissingResourceException e) {
      LOGGER.log(Level.SEVERE,
          "Resource not found: \"" + ACTION_LABEL_KEY + "\"", e);
      putValue(Action.NAME, ACTION_LABEL);
    }
    putValue(Action.SHORT_DESCRIPTION, this.getBriefHelp());
    putValue(Action.LONG_DESCRIPTION,
        this.getHelp(this.getDefaultShellController()));
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
