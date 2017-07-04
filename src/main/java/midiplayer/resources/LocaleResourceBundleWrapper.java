package midiplayer.resources;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * A wrapper for handling localized {@code ResourceBundle}.
 *
 * @author Mathieu Brunot
 */
public class LocaleResourceBundleWrapper implements LocaleChangeListener {

  /**
   * Logger.
   */
  private static final Logger LOGGER =
      Logger.getLogger(LocaleResourceBundleWrapper.class.getName());

  // #########################################################################
  private ResourceBundle rb;

  private final String baseName;
  private final ClassLoader loader;
  private final ResourceBundle.Control control;

  // #########################################################################
  public LocaleResourceBundleWrapper(String baseName) {
    this(baseName, null, null);
  }

  public LocaleResourceBundleWrapper(String baseName, ResourceBundle.Control control) {
    this(baseName, null, control);
  }

  public LocaleResourceBundleWrapper(String baseName, ClassLoader loader) {
    this(baseName, loader, null);
  }

  public LocaleResourceBundleWrapper(String baseName, ClassLoader loader,
      ResourceBundle.Control control) {
    this.baseName = baseName;
    this.loader = loader;
    this.control = control;
  }

  // #########################################################################
  @Override
  public void localeChanged() {
    this.rb = null;
  }

  @Override
  public void localeChanged(PropertyChangeEvent evt) {
    this.rb = null;
  }

  // #########################################################################
  public ResourceBundle getResourceBundle() {
    if (rb == null) {
      if (this.control == null && this.loader == null) {
        rb = ResourceBundle.getBundle(this.baseName);
      } else if (this.control != null && this.loader == null) {
        rb = ResourceBundle.getBundle(this.baseName, this.control);
      } else if (this.control == null && this.loader != null) {
        rb = ResourceBundle.getBundle(this.baseName, ResourceUtils.getLocale(), this.loader);
      } else {
        rb = ResourceBundle.getBundle(this.baseName, ResourceUtils.getLocale(), this.loader,
            this.control);
      }
    }
    return rb;
  }

  // #########################################################################
  /**
   * Creates a MessageFormat with the given pattern and uses it to format the given arguments. This
   * is equivalent to <blockquote> <code>(new {@link #MessageFormat(String) MessageFormat}(pattern, 
   * ResourceUtils.getLocale())).{@link MessageFormat#format(java.lang.Object[], java.lang.StringBuffer, java.text.FieldPosition) format}(arguments,
   * new StringBuffer(), null).toString()</code> </blockquote>
   *
   * @param pattern the pattern for the temporary message format
   * @param arguments the arguments used to format the pattern
   * @return Formatted string.
   *
   * @exception IllegalArgumentException if the pattern is invalid, or if an argument in the [@code
   *            arguments} array is not of the type expected by the format element(s) that use it.
   */
  public static String format(String pattern, Object... arguments) {
    MessageFormat temp = new MessageFormat(pattern, ResourceUtils.getLocale());
    return temp.format(arguments);
  }

  // #########################################################################
  public String getMessage(String key, Object... arguments) throws MissingResourceException {
    try {
      String msg = getResourceBundle().getString(key);

      for (int idx = 0; (idx = msg.indexOf('&', idx)) != -1; idx++) {
        String mnemonic = msg.substring(idx + 1, idx + 2);

        if (!mnemonic.equals(" ")) {
          String first = msg.substring(0, idx);
          String last = msg.substring(idx + 1);
          String remain = first.concat(last);

          if (arguments != null && arguments.length > 0) {
            remain = format(remain, arguments);
          }

          return remain;
        }

      }

      if (arguments != null && arguments.length > 0) {
        String pattern = msg;
        msg = MessageFormat.format(pattern, arguments);
      }

      return msg;
    } catch (SecurityException | IllegalArgumentException e) {
      Object[] args = {key, e};
      LOGGER.log(Level.SEVERE, "Error occurred while retrieving message for key {0}: {1}", args);
      return null;
    }

  }

  public int getMnemonic(String key) throws MissingResourceException {
    try {
      String msg = getResourceBundle().getString(key);

      for (int idx = 0; (idx = msg.indexOf('&', idx)) != -1; idx++) {
        char mnemonic = msg.charAt(idx + 1);
        if (Character.isLowerCase(mnemonic) || Character.isUpperCase(mnemonic)) {
          Class<?> keyEvent = Class.forName("java.awt.event.KeyEvent");
          Field field = keyEvent.getDeclaredField("VK_" + Character.toUpperCase(mnemonic));
          int keyCode = field.getInt(null);

          return keyCode;
        }
      }
    } catch (ClassNotFoundException | NoSuchFieldException | SecurityException
        | IllegalArgumentException | IllegalAccessException e) {
      Object[] args = {key, e};
      LOGGER.log(Level.SEVERE, "Error occurred while retrieving mnemonic for key {0}: {1}", args);
    }
    return -1;
  }

  public int getDisplayedMnemonicIndex(String key) throws MissingResourceException {
    try {
      String msg = getResourceBundle().getString(key);

      for (int idx = 0; (idx = msg.indexOf('&', idx)) != -1; idx++) {
        char mnemonic = msg.charAt(idx + 1);
        if (Character.isLowerCase(mnemonic) || Character.isUpperCase(mnemonic)) {
          return idx;
        }
      }
    } catch (SecurityException | IllegalArgumentException e) {
      Object[] args = {key, e};
      LOGGER.log(Level.SEVERE, "Error occurred while retrieving mnemonic for key {0}: {1}", args);
    }
    return -1;
  }

  public void setTextAndMnemonic(JComponent comp, String key, Object... arguments)
      throws MissingResourceException {
    try {
      String msg = getResourceBundle().getString(key);
      String txt = null;
      char mnemonic = ' ';
      int idx;

      // Look for the first '&' followed by a letter
      for (idx = 0; (idx = msg.indexOf('&', idx)) != -1; idx++) {
        mnemonic = msg.charAt(idx + 1);

        // Split the message around the '&'
        if (txt == null && mnemonic != ' ') {
          String first = msg.substring(0, idx);
          String last = msg.substring(idx + 1);
          txt = first.concat(last);
        }
        // and break the loop
        if (Character.isLowerCase(mnemonic) || Character.isUpperCase(mnemonic)) {
          break;
        }
      }

      // If no split was done, use original message
      if (txt == null) {
        txt = msg;
      }
      // If some arguments were provided, replace markers in the text
      if (arguments != null && arguments.length > 0) {
        txt = format(txt, arguments);
      }

      // Set the component text
      if (comp instanceof AbstractButton) {
        ((AbstractButton) comp).setText(txt);
      } else if (comp instanceof JLabel) {
        ((JLabel) comp).setText(txt);
      }

      // If a mnemonic was extracted
      if (Character.isLowerCase(mnemonic) || Character.isUpperCase(mnemonic)) {
        Class<?> keyEvent = Class.forName("java.awt.event.KeyEvent");
        Field field = keyEvent.getDeclaredField("VK_" + Character.toUpperCase(mnemonic));
        int keyCode = field.getInt(null);

        if (comp instanceof AbstractButton) {
          ((AbstractButton) comp).setMnemonic(keyCode);
          ((AbstractButton) comp).setDisplayedMnemonicIndex(idx);
        } else if (comp instanceof JLabel) {
          ((JLabel) comp).setDisplayedMnemonic(keyCode);
          ((JLabel) comp).setDisplayedMnemonicIndex(idx);
        }
      }
    } catch (ClassNotFoundException | NoSuchFieldException | SecurityException
        | IllegalArgumentException | IllegalAccessException e) {
      Object[] args = {comp, key, e};
      LOGGER.log(Level.SEVERE,
          "Error occurred while setting text and menomic for component {0} and key {1}: {2}", args);
    }
  }

  public void setTextAndMnemonic(Action action, String key, Object... arguments)
      throws MissingResourceException {
    try {
      String msg = getResourceBundle().getString(key);
      String txt = null;
      char mnemonic = ' ';
      int idx;

      // Look for the first '&' followed by a letter
      for (idx = 0; (idx = msg.indexOf('&', idx)) != -1; idx++) {
        mnemonic = msg.charAt(idx + 1);

        // Split the message around the '&'
        if (txt == null && mnemonic != ' ') {
          String first = msg.substring(0, idx);
          String last = msg.substring(idx + 1);
          txt = first.concat(last);
        }
        // and break the loop
        if (Character.isLowerCase(mnemonic) || Character.isUpperCase(mnemonic)) {
          break;
        }
      }

      // If no split was done, use original message
      if (txt == null) {
        txt = msg;
      }
      // If some arguments were provided, replace markers in the text
      if (arguments != null && arguments.length > 0) {
        txt = format(txt, arguments);
      }

      // Set the action name
      action.putValue(Action.NAME, txt);

      // If a mnemonic was extracted
      if (Character.isLowerCase(mnemonic) || Character.isUpperCase(mnemonic)) {
        Class<?> keyEvent = Class.forName("java.awt.event.KeyEvent");
        Field field = keyEvent.getDeclaredField("VK_" + Character.toUpperCase(mnemonic));
        int keyCode = field.getInt(null);

        action.putValue(Action.MNEMONIC_KEY, keyCode);
        action.putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, idx);
      }
    } catch (ClassNotFoundException | NoSuchFieldException | SecurityException
        | IllegalArgumentException | IllegalAccessException e) {
      Object[] args = {action, key, e};
      LOGGER.log(Level.SEVERE,
          "Error occurred while setting text and menomic for action {0} and key {1}: {2}", args);
    }
  }

}
