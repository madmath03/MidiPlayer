package midi_player.frame.action;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.ComboBoxModel;

import jswingshell.IJssController;
import jswingshell.action.AbstractJssComboAction;
import jswingshell.action.ActionGroup;
import midi_player.resources.LocaleChangeListener;
import midi_player.resources.ResourceUtils;

/**
 * Action to change the JVM default locale.
 *
 * @author Mathieu Brunot
 */
public final class LocaleAction extends jswingshell.action.AbstractJssComboAction<Locale>
    implements LocaleChangeListener {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = 366984660670526263L;

  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger.getLogger(LocaleAction.class.getName());

  /**
   * This action default identifier.
   */
  public static final String DEFAULT_IDENTIFIER = "locale";

  private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

  private static final String ACTION_LABEL = "Locale";

  private static final String ACTION_LABEL_KEY = "midi_player.action.locale.name";

  private static final String COMMAND_BRIEF_HELP = "Change the JVM default locale.";

  private static final String COMMAND_BRIEF_HELP_KEY = "midi_player.action.locale.help.short";

  private static final String COMMAND_HELP_KEY = "midi_player.action.locale.help.long";

  private static final String COMMAND_RUN_CURRENT_LOCALE_MESSAGE = "Current locale is ";

  private static final String COMMAND_RUN_CURRENT_LOCALE_MESSAGE_KEY =
      "midi_player.action.locale.run.current_locale";

  private static final String INTERNAL_COMMAND_BRIEF_HELP = "Sets the shell's locale to ";

  private static final String INTERNAL_COMMAND_BRIEF_HELP_KEY =
      "midi_player.action.locale.internal_command.help.short";

  private static final String ICON_KEY = "change_languange.png";

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
  public static final String getHelp(LocaleAction action) {
    if (!commandHelpInitialized && action != null) {
      StringBuilder stringBuilder = new StringBuilder();

      String commandIdsAsString = action.getCommandIdentifiersAsString(),
          commandIdentifier = action.getDefaultCommandIdentifier();
      stringBuilder.append(action.getBriefHelp()).append("\n");
      stringBuilder.append("\t").append(commandIdsAsString).append("\n");
      stringBuilder.append("\n");

      try {
        stringBuilder.append(
            ResourceUtils.getMessage(COMMAND_HELP_KEY, commandIdsAsString, commandIdentifier));
      } catch (MissingResourceException e) {
        LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_HELP_KEY + "\"", e);

        stringBuilder.append("You can set the locale as follow:");
      }

      Map<Locale, Collection<String>> argumentsByValue = action.getSwitchArgumentsByValue();
      if (argumentsByValue != null) {
        argumentsByValue.entrySet().stream().forEach((entry) -> {
          stringBuilder.append("\n");
          stringBuilder.append("\t").append(commandIdentifier).append(" ").append(entry.getValue());
        });
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
  public static final String getBriefHelp(LocaleAction action) {
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

  /**
   * Get an appropriate icon key for a locale.
   *
   * @param locale the locale reference
   *
   * @return an appropriate icon key.
   */
  public static final String getLocaleIcon(Locale locale) {
    if (locale == null) {
      return null;
    }

    String itemLanguageCode = locale.getLanguage();
    String iconKey;
    switch (itemLanguageCode) {
      case "en":
        iconKey = "flag_usa.png";
        break;

      case "fr":
        iconKey = "flag_france.png";
        break;

      default:
        iconKey = null;
    }

    return iconKey;
  }

  // #########################################################################
  public LocaleAction() {
    super(ACTION_LABEL, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL), null);
    putValue(Action.LARGE_ICON_KEY, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL, true));
    putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
    localeChanged();
  }

  public LocaleAction(Locale[] items) {
    this(items, null, (String[]) null);
  }

  public LocaleAction(ComboBoxModel<Locale> model) {
    this(model, null, (String[]) null);
  }

  public LocaleAction(IJssController shellController) {
    this(shellController, (String[]) null);
  }

  public LocaleAction(Locale[] items, IJssController shellController) {
    this(items, shellController, (String[]) null);
  }

  public LocaleAction(ComboBoxModel<Locale> model, IJssController shellController) {
    this(model, shellController, (String[]) null);
  }

  public LocaleAction(IJssController shellController, String... args) {
    super(ACTION_LABEL, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL), shellController,
        args);
    putValue(Action.LARGE_ICON_KEY, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL, true));
    putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
    localeChanged();
  }

  public LocaleAction(Locale[] items, IJssController shellController, String... args) {
    super(items, ACTION_LABEL, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL),
        shellController, args);
    putValue(Action.LARGE_ICON_KEY, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL, true));
    putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
    localeChanged();
  }

  public LocaleAction(ComboBoxModel<Locale> model, IJssController shellController, String... args) {
    super(model, ACTION_LABEL, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL),
        shellController, args);
    putValue(Action.LARGE_ICON_KEY, ResourceUtils.createImageIcon(ICON_KEY, ACTION_LABEL, true));
    putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
    localeChanged();
  }

  // #########################################################################
  @Override
  protected boolean doSwitch(IJssController shellController, Locale switchValue) {
    if (switchValue == null) {
      String currentLocale = ResourceUtils.getLocale().getDisplayLanguage();
      String msg;
      try {
        msg = ResourceUtils.getMessage(COMMAND_RUN_CURRENT_LOCALE_MESSAGE_KEY, currentLocale);
      } catch (MissingResourceException e1) {
        LOGGER.log(Level.SEVERE,
            "Resource not found: \"" + COMMAND_RUN_CURRENT_LOCALE_MESSAGE_KEY + "\"", e1);
        msg = COMMAND_RUN_CURRENT_LOCALE_MESSAGE + currentLocale;
      }
      shellController.publish(IJssController.PublicationLevel.INFO, msg);
      return false;
    }

    // Set application locale
    ResourceUtils.setLocale(switchValue);
    return true;
  }

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
  public void setDefaultShellController(IJssController shellController) {
    super.setDefaultShellController(shellController);
    // Apply selected Locale to new shell controller
    doSwitch(shellController, (Locale) this.getSelectedItem());
  }

  @Override
  protected Collection<ComboElementAction<Locale>> initInnerElements() {
    ActionGroup innerGroup = new ActionGroup();
    ArrayList<ComboElementAction<Locale>> innerElementActions =
        new ArrayList<>(dataModel.getSize());

    for (int i = 0, n = dataModel.getSize(); i < n; i++) {
      LocaleAction.LocaleComboElementAction elementAction =
          this.new LocaleComboElementAction(this, dataModel.getElementAt(i));
      elementAction.setEnabled(this.isEnabled());
      ResourceUtils.addLocaleChangeListener(elementAction);
      innerElementActions.add(elementAction);
      innerGroup.add(elementAction);
    }
    setInnerGroup(innerGroup);

    return innerElementActions;
  }

  @Override
  public void resetInnerElements() {
    // Remove from Locale change notifiers internal actions
    if (this.hasInnerElementActions()) {
      this.getInnerElementActions().parallelStream()
          .filter((internalAction) -> (internalAction instanceof LocaleChangeListener))
          .forEach((internalAction) -> {
            ResourceUtils.removeLocaleChangeListener((LocaleChangeListener) internalAction);
          });
    }
    super.resetInnerElements();
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

  // #########################################################################
  public final class LocaleComboElementAction extends ComboElementAction<Locale>
      implements LocaleChangeListener {

    /**
     * The {@code serialVersionUID}.
     */
    private static final long serialVersionUID = 7721594780647575603L;

    protected LocaleComboElementAction(AbstractJssComboAction<Locale> parentAction,
        Locale dataItem) {
      super(parentAction, dataItem);

      String iconKey = LocaleAction.getLocaleIcon(dataItem);
      if (iconKey != null) {
        putValue(Action.SMALL_ICON, ResourceUtils.createImageIcon(iconKey, ACTION_LABEL));
        putValue(Action.LARGE_ICON_KEY, ResourceUtils.createImageIcon(iconKey, ACTION_LABEL, true));
      }

      putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
      localeChanged();
    }

    @Override
    protected String initBriefHelp() {
      String briefHelp;
      String localeNameToDisplay = getDataItem().getDisplayName(ResourceUtils.getLocale());
      try {
        briefHelp = ResourceUtils.getMessage(INTERNAL_COMMAND_BRIEF_HELP_KEY, localeNameToDisplay);
      } catch (MissingResourceException e) {
        LOGGER.log(Level.SEVERE, "Resource not found: \"" + INTERNAL_COMMAND_BRIEF_HELP_KEY + "\"",
            e);
        briefHelp = INTERNAL_COMMAND_BRIEF_HELP + localeNameToDisplay;
      }
      return briefHelp;
    }

    // #########################################################################
    @Override
    public void localeChanged() {
      localeChanged(null);
    }

    @Override
    public void localeChanged(PropertyChangeEvent evt) {
      resetBriefHelp();
      putValue(Action.SHORT_DESCRIPTION, this.getBriefHelp());
      putValue(Action.NAME, getDataItem().getDisplayName(getDataItem()));
    }

    @Override
    public final void putValue(String key, Object newValue) {
      super.putValue(key, newValue);
    }

    @Override
    public final String getDefaultCommandIdentifier() {
      return super.getDefaultCommandIdentifier();
    }
  }

}
