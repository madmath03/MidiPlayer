package midiplayer.console.resources;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import midiplayer.resources.LocaleChangeListener;
import midiplayer.resources.LocaleChangeNotifier;
import midiplayer.resources.LocaleResourceBundleWrapper;

/**
 * Common resource management utilities.
 *
 * @author Mathieu Brunot
 */
public final class ResourceUtils {

  private ResourceUtils() {}

  // #########################################################################
  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger.getLogger(ResourceUtils.class.getName());

  private static final String SMALL_ICON_PATH = "icons/16x16/";

  private static final String LARGE_ICON_PATH = "icons/32x32/";

  private static final String CONSOLE_FRAME_RESOURCE_BUNDLE =
      "midi_player.console.resources.i8n.consoleframe";

  // #########################################################################
  /**
   * Locale change notifier for {@link LocaleResourceBundleWrapper}s.
   */
  private static final LocaleChangeNotifier RESOURCE_BUNDLE_LOCALE_CHANGE_NOTIFIER =
      new LocaleChangeNotifier(ResourceUtils.class, false);

  /**
   * Application's available locales.
   */
  private static final Locale[] APPLICATION_AVAILABLE_LOCALES =
      midiplayer.resources.ResourceUtils.getAvailableLocales();

  /**
   * Returns an array of all installed locales.
   *
   * <p>
   * The returned array represents the union of locales supported by the application.
   * </p>
   *
   * @return An array of installed locales.
   */
  public static Locale[] getAvailableLocales() {
    // If application did not define its available locales
    if (APPLICATION_AVAILABLE_LOCALES == null || APPLICATION_AVAILABLE_LOCALES.length == 0) {
      // Use the JVM available locales
      return Locale.getAvailableLocales();
    } else {
      return APPLICATION_AVAILABLE_LOCALES;
    }
  }

  /**
   * Gets the current value of the default locale for this instance of the application.
   *
   * @return the default locale for this instance of the application
   */
  public static Locale getLocale() {
    return midiplayer.resources.ResourceUtils.getLocale();
  }

  /**
   * Sets the default locale for this instance of the application and alert all registered
   * {@code LocaleChangeListener}s.
   *
   * @param newLocale the new default locale
   *
   * @throws SecurityException if a security manager exists and its {@code checkPermission} method
   *         doesn't allow the operation.
   * @throws NullPointerException if {@code newLocale} is {@code null}
   */
  public static void setLocale(Locale newLocale) {
    midiplayer.resources.ResourceUtils.setLocale(newLocale);
    Locale oldLocale = getLocale();
    // First alert resource bundle wrappers to reload of resource bundles
    RESOURCE_BUNDLE_LOCALE_CHANGE_NOTIFIER.fireLocaleChange(oldLocale, newLocale);
    // Then call main resource
    midiplayer.resources.ResourceUtils.setLocale(newLocale);
  }

  /**
   * Add a {@code LocaleChangeListener} to the listener list.
   *
   * @param listener The {@code LocaleChangeListener} to be added
   *
   * @return {@code true} (as specified by {@link Collection#add})
   */
  public static boolean addLocaleChangeListener(LocaleChangeListener listener) {
    return midiplayer.resources.ResourceUtils.addLocaleChangeListener(listener);
  }

  /**
   * Remove a {@code LocaleChangeListener} from the listener list.
   *
   * @param listener The {@code LocaleChangeListener} to be removed
   *
   * @return {@code true} if this {@code LocaleChangeNotifier} contained the
   *         {@code LocaleChangeListener}
   */
  public static boolean removeLocaleChangeListener(LocaleChangeListener listener) {
    return midiplayer.resources.ResourceUtils.removeLocaleChangeListener(listener);
  }

  /**
   * Is a {@code LocaleChangeListener} in the listener list.
   *
   * @param listener The {@code LocaleChangeListener} to be test
   *
   * @return {@code true} if this {@code LocaleChangeNotifier} contains the
   *         {@code LocaleChangeListener}
   */
  public static boolean containsLocaleChangeListener(LocaleChangeListener listener) {
    return midiplayer.resources.ResourceUtils.containsLocaleChangeListener(listener);
  }

  // #########################################################################
  private static LocaleResourceBundleWrapper localeResourceBundleManager;

  protected static LocaleResourceBundleWrapper getLocaleResourceBundle() {
    if (localeResourceBundleManager == null) {
      localeResourceBundleManager = new LocaleResourceBundleWrapper(CONSOLE_FRAME_RESOURCE_BUNDLE);
      midiplayer.resources.ResourceUtils.addResourceBundleWrapper(localeResourceBundleManager);
    }
    return localeResourceBundleManager;
  }

  public static ResourceBundle getResourceBundle() {
    return getLocaleResourceBundle().getResourceBundle();
  }

  public static String getMessage(String key, Object... arguments) throws MissingResourceException {
    return getLocaleResourceBundle().getMessage(key, arguments);
  }

  public static int getMnemonic(String key) throws MissingResourceException {
    return getLocaleResourceBundle().getMnemonic(key);
  }

  public static int getDisplayedMnemonicIndex(String key) throws MissingResourceException {
    return getLocaleResourceBundle().getDisplayedMnemonicIndex(key);
  }

  public static void setTextAndMnemonic(JComponent comp, String key, Object... arguments)
      throws MissingResourceException {
    getLocaleResourceBundle().setTextAndMnemonic(comp, key, arguments);
  }

  public static void setTextAndMnemonic(Action action, String key, Object... arguments)
      throws MissingResourceException {
    getLocaleResourceBundle().setTextAndMnemonic(action, key, arguments);
  }

  // #########################################################################
  /**
   * Returns an ImageIcon, or null if the path was invalid.
   *
   * <p>
   * Defines the path to the file depending on the size requested (either small or large).
   * </p>
   *
   * @param fileName the file name of the icon
   * @param description the icon image description
   * @param largeSize Use a large size icon?
   * @return an ImageIcon, or null if the path was invalid.
   */
  public static ImageIcon createImageIcon(String fileName, String description, boolean largeSize) {
    String path = (largeSize ? LARGE_ICON_PATH : SMALL_ICON_PATH) + fileName;
    java.net.URL imgURL = ResourceUtils.class.getResource(path);
    if (imgURL != null) {
      return new ImageIcon(imgURL, description);
    } else {
      LOGGER.log(Level.WARNING, "Couldn''t find file: {0}", fileName);
      return null;
    }
  }

  /**
   * Returns an ImageIcon, or null if the path was invalid.
   *
   * @param fileName the file name of the icon
   * @param description the icon image description
   * @return an ImageIcon, or null if the path was invalid.
   */
  public static ImageIcon createImageIcon(String fileName, String description) {
    return createImageIcon(fileName, description, false);
  }

  /**
   * Returns a list of image icons, or null if the path was invalid.
   *
   * @param fileName the file name of the images
   * @param description the images description
   * @return a list of image icons, or null if the path was invalid.
   */
  public static List<Image> createImages(String fileName, String description) {
    ImageIcon icon = createImageIcon(fileName, description, true);
    if (icon != null) {
      List<Image> images = new ArrayList<>(2);
      images.add(icon.getImage());
      icon = createImageIcon(fileName, description, false);
      images.add(icon.getImage());
      return images;
    } else {
      return null;
    }
  }

}
