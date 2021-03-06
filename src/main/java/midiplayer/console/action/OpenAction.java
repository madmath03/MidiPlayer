package midiplayer.console.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import jswingshell.IJssController;
import midiplayer.console.resources.ResourceUtils;

/**
 * Action to open and execute a shell file.
 *
 * <p>
 * This action inherits from the {@code LoadCommandFile} and initializes GUI related properties:
 * </p>
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
public final class OpenAction extends LoadCommandFile {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = 5349678540847280344L;

  /**
   * Logger.
   */
  private static final Logger LOGGER =
      Logger.getLogger(OpenAction.class.getName());

  private static final String ACTION_LABEL = "Open";

  private static final String ACTION_LABEL_KEY =
      "midiplayer.console.action.open_command_file.name";

  private static final String COMMAND_RUN_FILE_FILTER_DESCRIPTION_KEY =
      "midiplayer.console.action.open_command_file.run.file_filter_description";

  private static final String ICON_KEY = "page.png";

  // #########################################################################
  private transient JFileChooser fileChooser;

  private transient Component parent;

  public OpenAction(JFileChooser fileChooser, Component parent,
      IJssController shellController, String... args) {
    super(ACTION_LABEL, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL),
        shellController, args);
    if (shellController == null) {
      throw new IllegalArgumentException("Shell controller is null");
    }
    this.setFileChooser(fileChooser);
    this.setParent(parent);
    putValue(Action.LARGE_ICON_KEY,
        ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL, true));
    putValue(Action.ACCELERATOR_KEY,
        KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
    localeChanged();
  }

  public OpenAction(JFileChooser fileChooser, Component parent,
      IJssController shellController) {
    this(fileChooser, parent, shellController, (String[]) null);
  }

  public OpenAction(JFileChooser fileChooser, Component parent) {
    this(fileChooser, parent, null, (String[]) null);
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
        description =
            ResourceUtils.getMessage(COMMAND_RUN_FILE_FILTER_DESCRIPTION_KEY);
      } catch (MissingResourceException e1) {
        LOGGER.log(Level.SEVERE, "Resource not found: \""
            + COMMAND_RUN_FILE_FILTER_DESCRIPTION_KEY + "\"", e1);
        description = "Shell file";
      }
      FileNameExtensionFilter filter =
          new FileNameExtensionFilter(description, "shell", "txt");

      // Open file chooser and select file
      fileChooser.setFileFilter(filter);
      fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      int returnVal = fileChooser.showOpenDialog(parent);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        eventArgs = new String[] {commandIdentifier,
            fileChooser.getSelectedFile().getAbsolutePath()};
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
