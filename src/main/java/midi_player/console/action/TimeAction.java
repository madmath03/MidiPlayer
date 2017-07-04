package midi_player.console.action;

import java.beans.PropertyChangeEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.Icon;

import jswingshell.IJssController;
import jswingshell.action.AbstractJssAction;
import midi_player.console.resources.ResourceUtils;
import midi_player.resources.LocaleChangeListener;

/**
 * Action to display the current time and date in the shell.
 *
 * @author Mathieu Brunot
 */
public final class TimeAction extends AbstractJssAction implements LocaleChangeListener {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = -7421356568084437300L;

  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger.getLogger(TimeAction.class.getName());

  /**
   * This action default identifier.
   * 
   * @since 1.2
   */
  public static final String DEFAULT_IDENTIFIER = "time";

  private static final String[] IDENTIFIERS = {DEFAULT_IDENTIFIER};

  private static final String COMMAND_BRIEF_HELP =
      "Display the current time and date in the shell.";

  private static final String COMMAND_BRIEF_HELP_KEY = "midi_player.console.action.time.help.short";

  private static final String COMMAND_HELP_KEY = "midi_player.console.action.time.help.long";

  private static final String COMMAND_HELP_EXAMPLE_KEY =
      "midi_player.console.action.time.help.long.example";

  private static final String COMMAND_RUN_INVALID_DATE_FORMAT_KEY =
      "midi_player.console.action.time.run.invalid_date_format";

  private static final String COMMAND_RUN_INVALID_LANGUAGE_FORMAT_KEY =
      "midi_player.console.action.time.run.invalid_language_format";

  private static final String COMMAND_RUN_INVALID_COUNTRY_FORMAT_KEY =
      "midi_player.console.action.time.run.invalid_country_format";

  private static final String COMMAND_RUN_INVALID_VARIANT_FORMAT_KEY =
      "midi_player.console.action.time.run.invalid_variant_format";

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
  public static final String getHelp(TimeAction action) {
    if (!commandHelpInitialized && action != null) {
      StringBuilder stringBuilder = new StringBuilder();

      String commandIdsAsString = action.getCommandIdentifiersAsString(),
          defaultCommandIdentifier = action.getDefaultCommandIdentifier();
      stringBuilder.append(action.getBriefHelp()).append("\n");
      stringBuilder.append("\n");

      try {
        stringBuilder.append(ResourceUtils.getMessage(COMMAND_HELP_KEY, commandIdsAsString));
      } catch (MissingResourceException e) {
        LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_HELP_KEY + "\"", e);
        stringBuilder.append("Displays the current time and date:").append("\n");
        stringBuilder.append("\t").append(commandIdsAsString).append("\n");
        stringBuilder.append("The date/time format can be specified as well as the language:")
            .append("\n");
        stringBuilder.append("\t").append(commandIdsAsString)
            .append(" [format] [language] [country] [variant] ").append("\n");
        stringBuilder.append("\n");
        stringBuilder.append(
            "If no format and language are provided, the default date format and locale will be used.");
      }

      if (defaultCommandIdentifier != null) {
        String exampleMsg;
        try {
          exampleMsg = ResourceUtils.getMessage(COMMAND_HELP_EXAMPLE_KEY, commandIdsAsString);
        } catch (MissingResourceException e) {
          LOGGER.log(Level.SEVERE, "Resource not found: \"" + COMMAND_HELP_EXAMPLE_KEY + "\"", e);
          exampleMsg = "Example: ";
        }

        stringBuilder.append("\n");
        stringBuilder.append(exampleMsg).append("\n");
        stringBuilder.append(defaultCommandIdentifier).append(" yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .append("\n");
        stringBuilder.append("2001-07-04T12:08:56.235-0700").append("\n");

        stringBuilder.append("\n");
        stringBuilder.append(exampleMsg).append("\n");
        stringBuilder.append(defaultCommandIdentifier).append(" \"EEE, MMM d, ''yy\" en us")
            .append("\n");
        stringBuilder.append("Wed, Jul 4, '01").append("\n");

        stringBuilder.append("\n");
        stringBuilder.append(exampleMsg).append("\n");
        stringBuilder.append(defaultCommandIdentifier).append(" \"EEEE dd MMMM yyyy\" th th th")
            .append("\n");
        stringBuilder.append("วันพุธ 04 กรกฎาคม 2001").append("\n");

        stringBuilder.append("\n");
        stringBuilder.append(exampleMsg).append("\n");
        stringBuilder.append(defaultCommandIdentifier).append(" \"EEEE dd MMMM yyyy\" th TH TH")
            .append("\n");
        stringBuilder.append("วันพุธ ๐๘ กรกฎาคม ๒๕๔๔");
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
  public static final String getBriefHelp(TimeAction action) {
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
  public TimeAction(String name, Icon icon, IJssController shellController, String... args) {
    super(name, icon, shellController, args);
    putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
    localeChanged();
  }

  public TimeAction(String name, IJssController shellController, String... args) {
    super(name, shellController, args);
    putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
    localeChanged();
  }

  public TimeAction(IJssController shellController, String... args) {
    super(shellController, args);
    putValue(Action.ACTION_COMMAND_KEY, getDefaultCommandIdentifier());
    localeChanged();
  }

  public TimeAction(IJssController shellController) {
    this(shellController, (String[]) null);
  }

  public TimeAction() {
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
  public int run(IJssController shellController, String... args) {
    int commandReturnStatus = AbstractJssAction.SUCCESS;

    if (shellController == null) {
      commandReturnStatus = 1;
    } else {
      Date currentDate = new Date();

      DateFormat dateFormat = prepareDateFormat(shellController, args);

      // If date format was properly defined
      if (dateFormat != null) {
        shellController.publish(IJssController.PublicationLevel.SUCCESS,
            dateFormat.format(currentDate));
      } else {
        commandReturnStatus = AbstractJssAction.ERROR;
      }
    }

    return commandReturnStatus;
  }

  private DateFormat prepareDateFormat(IJssController shellController, String... args) {
    DateFormat dateFormat;

    if (args != null && args.length > 1) {
      switch (args.length) {
        // If format and locale are provided
        case 5:
          try {
            Locale displayLocale = new Locale(args[2], args[3], args[4]);
            dateFormat = new SimpleDateFormat(args[1], displayLocale);
          } catch (IllegalArgumentException e) {
            String[] parameters = new String[] {e.getMessage(), args[1], args[2], args[3], args[4]};
            LOGGER.log(Level.WARNING,
                "Error occurred while parsing date/time format {1} for language {2} and country {3} and variant {4}: {0}",
                parameters);
            String msg;
            try {
              msg = ResourceUtils.getMessage(COMMAND_RUN_INVALID_VARIANT_FORMAT_KEY,
                  e.getLocalizedMessage(), args[1], args[2], args[3], args[4]);
            } catch (MissingResourceException e1) {
              LOGGER.log(Level.SEVERE,
                  "Resource not found: \"" + COMMAND_RUN_INVALID_VARIANT_FORMAT_KEY + "\"", e1);
              msg = "Error occurred while parsing date/time format " + args[1] + " for language "
                  + args[2] + " and country " + args[3] + " and variant " + args[4] + ": "
                  + e.getMessage();
            }
            shellController.publish(IJssController.PublicationLevel.ERROR, msg);
            dateFormat = null;
          }
          break;
        case 4:
          try {
            Locale displayLocale = new Locale(args[2], args[3]);
            dateFormat = new SimpleDateFormat(args[1], displayLocale);
          } catch (IllegalArgumentException e) {
            String[] parameters = new String[] {e.getMessage(), args[1], args[2], args[3]};
            LOGGER.log(Level.WARNING,
                "Error occurred while parsing date/time format {1} for language {2} and country {3}: {0}",
                parameters);
            String msg;
            try {
              msg = ResourceUtils.getMessage(COMMAND_RUN_INVALID_COUNTRY_FORMAT_KEY,
                  e.getLocalizedMessage(), args[1], args[2], args[3]);
            } catch (MissingResourceException e1) {
              LOGGER.log(Level.SEVERE,
                  "Resource not found: \"" + COMMAND_RUN_INVALID_COUNTRY_FORMAT_KEY + "\"", e1);
              msg = "Error occurred while parsing date/time format " + args[1] + " for language "
                  + args[2] + " and country " + args[3] + ": " + e.getMessage();
            }
            shellController.publish(IJssController.PublicationLevel.ERROR, msg);
            dateFormat = null;
          }
          break;
        case 3:
          try {
            Locale displayLocale = new Locale(args[2]);
            dateFormat = new SimpleDateFormat(args[1], displayLocale);
          } catch (IllegalArgumentException e) {
            String[] parameters = new String[] {e.getMessage(), args[1], args[2]};
            LOGGER.log(Level.WARNING,
                "Error occurred while parsing date/time format {1} for language {2}: {0}",
                parameters);
            String msg;
            try {
              msg = ResourceUtils.getMessage(COMMAND_RUN_INVALID_LANGUAGE_FORMAT_KEY,
                  e.getLocalizedMessage(), args[1], args[2]);
            } catch (MissingResourceException e1) {
              LOGGER.log(Level.SEVERE,
                  "Resource not found: \"" + COMMAND_RUN_INVALID_LANGUAGE_FORMAT_KEY + "\"", e1);
              msg = "Error occurred while parsing date/time format " + args[1] + " for language "
                  + args[2] + ": " + e.getMessage();
            }
            shellController.publish(IJssController.PublicationLevel.ERROR, msg);
            dateFormat = null;
          }
          break;
        // Assume only format was provided
        case 2:
        default:
          try {
            dateFormat = new SimpleDateFormat(args[1], ResourceUtils.getLocale());
          } catch (IllegalArgumentException e) {
            String[] parameters = new String[] {e.getMessage(), args[1]};
            LOGGER.log(Level.WARNING, "Error occurred while parsing date/time format {1}: {0}",
                parameters);
            String msg;
            try {
              msg = ResourceUtils.getMessage(COMMAND_RUN_INVALID_DATE_FORMAT_KEY,
                  e.getLocalizedMessage(), args[1]);
            } catch (MissingResourceException e1) {
              LOGGER.log(Level.SEVERE,
                  "Resource not found: \"" + COMMAND_RUN_INVALID_DATE_FORMAT_KEY + "\"", e1);
              msg = "Error occurred while parsing date/time format " + args[1] + ": "
                  + e.getMessage();
            }
            shellController.publish(IJssController.PublicationLevel.ERROR, msg);
            dateFormat = null;
          }
          break;
      }

    } else {
      dateFormat = SimpleDateFormat.getInstance();
    }

    return dateFormat;
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
