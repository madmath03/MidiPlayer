package midi_player.console.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import jswingshell.IJssController;
import jswingshell.action.AbstractJssAction;
import midi_player.console.resources.ResourceUtils;
import midi_player.resources.LocaleChangeListener;

/**
 * Action to save to a file a shell recording session.
 *
 * <p>
 * This action encapsulates a {@link RecordAction} and initializes GUI related properties:
 * </p>
 * <ul>
 * <li>{@code Action.NAME}</li>
 * <li>{@code Action.SMALL_ICON}</li>
 * <li>{@code Action.LARGE_ICON_KEY}</li>
 * <li>{@code Action.SHORT_DESCRIPTION}</li>
 * <li>{@code Action.LONG_DESCRIPTION}</li>
 * <li>{@code Action.ACTION_COMMAND_KEY}</li>
 * </ul>
 *
 * @author Mathieu Brunot
 */
public final class RecordSaveAction extends AbstractJssAction implements LocaleChangeListener {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = 5644101320682209480L;

  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger.getLogger(RecordSaveAction.class.getName());

  /**
   * This action default identifier.
   */
  public static final String DEFAULT_IDENTIFIER = "recordSave";

  private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

  private static final String ACTION_LABEL = "Save recording";

  private static final String ACTION_LABEL_KEY = "midi_player.console.action.record_save.name";

  private static final String ICON_KEY = "page_save.png";

  private static final String COMMAND_BRIEF_HELP = "Save to a file a shell recording session.";

  private static final String COMMAND_BRIEF_HELP_KEY =
      "midi_player.console.action.record_save.help.short";

  private static String commandBriefHelp;

  private static boolean commandBriefHelpInitialized = false;

  /**
   * Construct the static command brief help.
   *
   * @param action the action reference
   *
   * @return the static command brief help.
   */
  public static final String getBriefHelp(RecordSaveAction action) {
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
    commandBriefHelpInitialized = false;
    commandBriefHelp = null;
  }

  // #########################################################################
  private final RecordAction recordAction;

  private transient JFileChooser fileChooser;

  private transient Component parent;

  public RecordSaveAction(RecordAction recordAction, JFileChooser fileChooser, Component parent) {
    super(ACTION_LABEL, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL), null,
        new String[] {recordAction.getDefaultCommandIdentifier(), RecordAction.RECORD_SAVE});
    this.recordAction = recordAction;
    // Set the file chooser
    if (fileChooser == null) {
      throw new IllegalArgumentException("File chooser is null");
    }
    this.fileChooser = fileChooser;
    // Set the parent
    if (parent == null) {
      throw new IllegalArgumentException("Parent is null");
    }
    this.parent = parent;

    putValue(Action.LARGE_ICON_KEY, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL, true));
    putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
    localeChanged();
  }

  public JFileChooser getFileChooser() {
    return fileChooser;
  }

  public void setFileChooser(JFileChooser fileChooser) {
    this.fileChooser = fileChooser;
  }

  public Component getParent() {
    return parent;
  }

  public void setParent(Component parent) {
    this.parent = parent;
  }

  // #########################################################################
  @Override
  protected String[] extractArgumentsFromEvent(ActionEvent e) {
    String[] eventArgs = null;

    // Open file chooser and select file
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Shell file", "shell", "shell");
    fileChooser.setFileFilter(filter);
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    int returnVal = fileChooser.showSaveDialog(parent);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      eventArgs = Arrays.copyOf(getDefaultArguments(), getDefaultArguments().length + 1);
      eventArgs[2] = fileChooser.getSelectedFile().getAbsolutePath();
    }

    return eventArgs;
  }

  @Override
  public String[] getCommandIdentifiers() {
    return IDENTIFIERS;
  }

  @Override
  public int run(IJssController shellController, String... args) {
    return this.recordAction.run(args);
  }

  @Override
  public String getHelp(IJssController shellController) {
    return this.recordAction.getHelp(shellController);
  }

  @Override
  public String getBriefHelp() {
    return getBriefHelp(this);
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
