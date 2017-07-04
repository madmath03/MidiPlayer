package midiplayer.console.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import midiplayer.console.resources.ResourceUtils;
import midiplayer.resources.LocaleChangeListener;

/**
 * Action to reset the size of the shell text.
 *
 * <p>
 * This action encapsulates a {@link ZoomAction} and initializes GUI related properties:
 * </p>
 * <ul>
 * <li>{@code Action.NAME}</li>
 * <li>{@code Action.SMALL_ICON}</li>
 * <li>{@code Action.LARGE_ICON_KEY}</li>
 * <li>{@code Action.SHORT_DESCRIPTION}</li>
 * <li>{@code Action.LONG_DESCRIPTION}</li>
 * </ul>
 *
 * @author Mathieu Brunot
 */
public final class ZoomFitAction extends AbstractAction implements LocaleChangeListener {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = -8099471651359676969L;

  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger.getLogger(ZoomFitAction.class.getName());

  private static final String ACTION_LABEL = "Zoom reset";

  private static final String ACTION_LABEL_KEY = "midi_player.console.action.zoom_fit.name";

  private static final String ICON_KEY = "zoom_fit.png";

  private static final String COMMAND_BRIEF_HELP = "Reset the size of the shell text.";

  private static final String COMMAND_BRIEF_HELP_KEY =
      "midi_player.console.action.zoom_fit.help.short";

  private static String commandBriefHelp;

  private static boolean commandBriefHelpInitialized = false;

  /**
   * Construct the static command brief help.
   *
   * @param action the action reference
   *
   * @return the static command brief help.
   */
  public static final String getBriefHelp(ZoomFitAction action) {
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
  private final ZoomAction zoomAction;
  private final String[] defaultArgs;

  public ZoomFitAction(ZoomAction zoomAction) {
    super(ACTION_LABEL, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL));
    if (zoomAction == null) {
      throw new IllegalArgumentException("Zoom action is null");
    }
    this.zoomAction = zoomAction;
    this.defaultArgs = new String[] {zoomAction.getDefaultCommandIdentifier(), ZoomAction.ZOOM_FIT};

    putValue(Action.LARGE_ICON_KEY, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL, true));
    // putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
    // KeyEvent.VK_0, ActionEvent.CTRL_MASK));
    putValue(Action.ACCELERATOR_KEY,
        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0, ActionEvent.CTRL_MASK));
    localeChanged();
  }

  // #########################################################################
  @Override
  public void actionPerformed(ActionEvent e) {
    this.zoomAction.run(defaultArgs);
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
    putValue(Action.SHORT_DESCRIPTION, getBriefHelp(this));
    putValue(Action.LONG_DESCRIPTION, zoomAction.getHelp());
  }

  // #########################################################################
  @Override
  public final void putValue(String key, Object newValue) {
    super.putValue(key, newValue);
  }

}
