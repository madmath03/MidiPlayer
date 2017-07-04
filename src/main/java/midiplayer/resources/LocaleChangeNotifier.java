package midiplayer.resources;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.swing.SwingUtilities;

/**
 * This is a utility class that can be used by beans that support internationalization.
 *
 * <p>
 * It manages a list of listeners and may dispatches {@link PropertyChangeEvent}s to them. Since the
 * {@code setDefault(Locale aLocale)} method lets your application set a systemwide (<em>actually
 * VM-wide</em>) resource, you can use an instance of this class as a static field of your class and
 * delegate these types of work to it.
 * </p>
 *
 * <p>
 * This class can be constructed with {@code LocaleChangeNotifier(sourceBean, true)} so that it
 * ensures listeners are only ever notified on the <i>Event Dispatch Thread</i>.
 * </p>
 *
 * @author Mathieu Brunot
 *
 * @see LocaleChangeListener
 * @see <a href=
 *      "http://docs.oracle.com/javame/config/cldc/opt-pkgs/api/ams-3.4/com/sun/ams/LocaleChangeNotifier.html"
 *      >JME - Interface LocaleChangeNotifier</a>
 */
public class LocaleChangeNotifier {

  private final List<LocaleChangeListener> localeChangeListeners = new ArrayList<>();

  /**
   * The object to be provided as the "source" for any generated events.
   */
  private Object source;

  /**
   * Whether to notify listeners on EDT
   *
   * @serial
   */
  private final boolean notifyOnEDT;

  // #########################################################################
  /**
   * Constructs a LocaleChangeNotifier object.
   *
   * @throws NullPointerException if {@code sourceBean} is {@code null}
   */
  public LocaleChangeNotifier() {
    this(Locale.class, false);
  }

  /**
   * Constructs a LocaleChangeNotifier object.
   *
   * @param sourceBean The bean to be given as the source for any events.
   * @throws NullPointerException if {@code sourceBean} is {@code null}
   */
  public LocaleChangeNotifier(Object sourceBean) {
    this(sourceBean, false);
  }

  /**
   * Constructs a LocaleChangeNotifier object.
   *
   * @param sourceBean the bean to be given as the source for any events
   * @param notifyOnEDT whether to notify listeners on the <i>Event Dispatch Thread</i> only
   *
   * @throws NullPointerException if {@code sourceBean} is {@code null}
   */
  public LocaleChangeNotifier(Object sourceBean, boolean notifyOnEDT) {
    if (sourceBean == null) {
      throw new NullPointerException();
    }
    this.source = sourceBean;
    this.notifyOnEDT = notifyOnEDT;
  }

  // #########################################################################
  /**
   * Add a {@code LocaleChangeListener} to the listener list.
   *
   * <p>
   * The same listener object may be added more than once, and will be called as many times as it is
   * added.
   * </p>
   *
   * <p>
   * If {@code listener} is {@code null}, no exception is thrown and no action is taken.
   * </p>
   *
   * @param listener The {@code LocaleChangeListener} to be added
   *
   * @return {@code true} (as specified by {@link Collection#add})
   */
  public synchronized boolean addLocaleChangeListener(LocaleChangeListener listener) {
    return localeChangeListeners.add(listener);
  }

  /**
   * Remove a {@code LocaleChangeListener} from the listener list.
   *
   * <p>
   * This removes a {@code LocaleChangeListener} that was registered for the
   * {@code LocaleChangeNotifier}.
   * </p>
   *
   * <p>
   * If {@code listener} was added more than once to the same event source, it will be notified one
   * less time after being removed. If {@code listener} is null, or was never added, no exception is
   * thrown and no action is taken.
   * </p>
   *
   * @param listener The {@code LocaleChangeListener} to be removed
   *
   * @return {@code true} if this {@code LocaleChangeNotifier} contained the
   *         {@code LocaleChangeListener}
   */
  public synchronized boolean removeLocaleChangeListener(LocaleChangeListener listener) {
    return localeChangeListeners.remove(listener);
  }

  /**
   * Returns an unmodifiable {@code List} of all the listeners which have been associated with the
   * {@code Locale}.
   *
   * @return all of the {@code LocaleChangeListener}s associated with the
   *         {@code LocaleChangeNotifier}.
   */
  public synchronized List<LocaleChangeListener> getLocaleChangeListeners() {
    return Collections.unmodifiableList(localeChangeListeners);
  }

  /**
   * Returns {@code true} if the listener list contains the specified element.
   *
   * <p>
   * More formally, returns {@code true} if and only if this list contains at least one element
   * {@code e} such that {@code (o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))}.
   * </p>
   *
   * @param listener element whose presence in the listener list is to be tested
   * @return {@code true} if this list contains the specified element
   *
   * @see List#contains(java.lang.Object)
   */
  public synchronized boolean containsLocaleChangeListener(LocaleChangeListener listener) {
    return localeChangeListeners.contains(listener);
  }

  /**
   * Returns {@code notifyOnEDT} property.
   *
   * @return {@code notifyOnEDT} property
   * @see #LocaleChangeNotifier(Object sourceBean, boolean notifyOnEDT)
   */
  public final boolean isNotifyOnEDT() {
    return notifyOnEDT;
  }

  // #########################################################################
  /**
   * Reports the bound property "{@code user.language}" update to listeners that have been
   * registered to track updates of {@code Locale}.
   *
   * <p>
   * No event is fired if old and new {@code Locale} are equal and non-null.
   * </p>
   *
   * <p>
   * This is merely a convenience wrapper around the more general
   * {@link LocaleChangeNotifier#firePropertyChange(PropertyChangeEvent)} method.
   * </p>
   *
   * @param oldLocale the old {@code Locale} of the property "{@code user.language}"
   * @param newLocale the new {@code Locale} of the property "{@code user.language}"
   */
  public void fireLocaleChange(Locale oldLocale, Object newLocale) {
    fireLocaleChange("user.language", oldLocale, newLocale);
  }

  /**
   * Reports a bound property update to listeners that have been registered to track updates of
   * {@code Locale}.
   *
   * <p>
   * No event is fired if old and new values are equal and non-null.
   * </p>
   *
   * <p>
   * This is merely a convenience wrapper around the more general
   * {@link LocaleChangeNotifier#firePropertyChange(PropertyChangeEvent)} method.
   * </p>
   *
   * @param propertyName the programmatic name of the property that was changed
   * @param oldValue the old value of the property
   * @param newValue the new value of the property
   */
  public void fireLocaleChange(String propertyName, Locale oldValue, Object newValue) {
    if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
      return;
    }
    fireLocaleChange(new PropertyChangeEvent(this.source, propertyName, oldValue, newValue));
  }

  /**
   * Fires a property change event to listeners that have been registered to track updates of
   * {@code Locale}.
   *
   * <p>
   * No event is fired if the given event's old and new values are equal and non-null.
   * </p>
   *
   * <p>
   * If {@link LocaleChangeNotifier#isNotifyOnEDT} is {@code true} and called off the <i>Event Dispatch Thread</i> this
   * implementation uses {@code SwingUtilities.invokeLater} to send out the notification on the
   * <i>Event Dispatch Thread</i>. This ensures listeners are only ever notified on the <i>Event
   * Dispatch Thread</i>.
   * </p>
   *
   * @param evt the {@code PropertyChangeEvent} to be fired
   *
   * @throws NullPointerException if {@code evt} is {@code null}
   */
  public void fireLocaleChange(final PropertyChangeEvent evt) {
    if (evt == null) {
      throw new NullPointerException();
    }
    if (!isNotifyOnEDT() || SwingUtilities.isEventDispatchThread()) {
      Object oldValue = evt.getOldValue();
      Object newValue = evt.getNewValue();
      if (oldValue == null || newValue == null || !oldValue.equals(newValue)) {
        fire(this.localeChangeListeners, evt);
      }
    } else {
      SwingUtilities.invokeLater(() -> {
        fireLocaleChange(evt);
      });
    }
  }

  /**
   * Alert listeners that have been registered to track updates of {@code Locale}.
   *
   * <p>
   * No event is given to the listeners in this method.
   * </p>
   *
   * <p>
   * If {@link LocaleChangeNotifier#isNotifyOnEDT} is {@code true} and called off the <i>Event Dispatch Thread</i> this
   * implementation uses {@code SwingUtilities.invokeLater} to send out the notification on the
   * <i>Event Dispatch Thread</i>. This ensures listeners are only ever notified on the <i>Event
   * Dispatch Thread</i>.
   * </p>
   */
  public void fireLocaleChange() {
    if (!isNotifyOnEDT() || SwingUtilities.isEventDispatchThread()) {
      fire(this.localeChangeListeners);
    } else {
      SwingUtilities.invokeLater(() -> {
        fireLocaleChange();
      });
    }
  }

  private static void fire(List<LocaleChangeListener> listeners, PropertyChangeEvent event) {
    if (listeners != null) {
      listeners.stream().forEach((listener) -> {
        listener.localeChanged(event);
      });
    }
  }

  private static void fire(List<LocaleChangeListener> listeners) {
    if (listeners != null) {
      listeners.stream().forEach((listener) -> {
        listener.localeChanged();
      });
    }
  }

}
