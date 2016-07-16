package midi_player.resources;

import java.beans.PropertyChangeEvent;

/**
 * This interface should be used to receive locale change notifications.
 *
 * <p>
 * To receive notifications from system, application should register listener
 * using {@code LocaleChangeNotifier.addLocaleChangeListener()}.</p>
 *
 * @author Mathieu Brunot
 *
 * @see LocaleChangeNotifier
 * @see
 * http://docs.oracle.com/javame/config/cldc/opt-pkgs/api/ams-3.4/com/sun/ams/LocaleChangeListener.html
 */
public interface LocaleChangeListener {

    /**
     * This method is called to notify about locale change.
     *
     * <p>
     * New locale can be retrieved by getting {@code user.language},
     * {@code user.country}, and {@code user.variant} system properties, or
     * through {@code Locale.getDefault()}.</p>
     */
    public void localeChanged();

    /**
     * This method is called to notify about locale change.
     *
     * <p>
     * New locale can be retrieved through the {@code evt} properties.</p>
     *
     * @param evt A PropertyChangeEvent object describing the event source and
     * how the locale has changed.
     */
    public void localeChanged(PropertyChangeEvent evt);

}
