package midi_player.frame.action;

import java.beans.PropertyChangeEvent;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JOptionPane;

import jswingshell.IJssController;
import jswingshell.action.AbstractJssAction;
import midi_player.frame.MidiPlayerController;
import midi_player.frame.MidiPlayerFrame;
import midi_player.resources.LocaleChangeListener;
import midi_player.resources.ResourceUtils;

/**
 * Action to display the "about" screen.
 *
 * @author Mathieu Brunot
 */
public final class DisplayAboutAction extends AbstractJssAction implements LocaleChangeListener {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = 3215386723682515613L;

  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger.getLogger(DisplayAboutAction.class.getName());

  /**
   * This action default identifier.
   *
   * @since 1.2
   */
  public static final String DEFAULT_IDENTIFIER = "displayAbout";

  private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

  private static final String ACTION_LABEL = "About";

  private static final String ACTION_LABEL_KEY = "midi_player.action.display_about.name";

  private static final String COMMAND_BRIEF_HELP = "About the MIDI Player.";

  private static final String COMMAND_BRIEF_HELP_KEY =
      "midi_player.action.display_about.help.short";

  private static final String COMMAND_HELP_KEY = "midi_player.action.display_about.help.long";

  private static final String ABOUT_SCREEN_TITLE_KEY =
      "midi_player.action.display_about.screen.title";

  private static final String ABOUT_SCREEN_MESSAGE_KEY =
      "midi_player.action.display_about.screen.message";

  private static final String ICON_KEY = "information.png";

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
  public static final String getHelp(DisplayAboutAction action) {
    if (!commandHelpInitialized && action != null) {
      StringBuilder stringBuilder = new StringBuilder();

      String commandIdsAsString = action.getCommandIdentifiersAsString();
      stringBuilder.append(action.getBriefHelp());
      stringBuilder.append("\n");
      try {
        stringBuilder.append(ResourceUtils.getMessage(COMMAND_HELP_KEY, commandIdsAsString));
      } catch (MissingResourceException e) {
        LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_HELP_KEY + "\"", e);
        stringBuilder.append("\n").append("Displays the information screen about the MIDI Player:");
        stringBuilder.append("\t").append(commandIdsAsString);
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
  public static final String getBriefHelp(DisplayAboutAction action) {
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
  public DisplayAboutAction(MidiPlayerController shellController, String... args) {
    super(ACTION_LABEL, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL), shellController,
        args);
    if (shellController == null) {
      throw new IllegalArgumentException("Midi player controller is null");
    }
    putValue(Action.LARGE_ICON_KEY, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL, true));
    putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
    localeChanged();
  }

  public DisplayAboutAction(MidiPlayerController shellController) {
    this(shellController, (String[]) null);
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
    if (!(shellController instanceof MidiPlayerController)) {
      return AbstractJssAction.ERROR;
    }
    MidiPlayerController midiPlayerController = (MidiPlayerController) shellController;

    JOptionPane.showMessageDialog(midiPlayerController.getFrame(),
        "<html><body>" + ResourceUtils.getMessage(ABOUT_SCREEN_MESSAGE_KEY) + "</body></html>",
        ResourceUtils.getMessage(ABOUT_SCREEN_TITLE_KEY), JOptionPane.INFORMATION_MESSAGE,
        ResourceUtils.createImageIcon(MidiPlayerFrame.ICON_KEY, MidiPlayerFrame.ICON_LABEL,
            "128x128"));

    return AbstractJssAction.SUCCESS;
  }

  @Override
  public MidiPlayerController getDefaultShellController() {
    return (MidiPlayerController) super.getDefaultShellController();
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
