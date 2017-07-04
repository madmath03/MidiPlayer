package midi_player.frame.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * Wrapper for actions to hide some properties.
 *
 * @author Mathieu Brunot
 */
public class ActionWrapper extends AbstractAction {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = 7238641692757335774L;

  private final Action wrappedAction;

  private boolean nameAllowed;

  private boolean iconAllowed;

  private boolean largeIconDisplayed;

  // #########################################################################
  public ActionWrapper(Action wrappedAction, boolean nameAllowed, boolean iconAllowed,
      boolean largeIconDisplayed) {
    this.wrappedAction = wrappedAction;
    this.nameAllowed = nameAllowed;
    this.iconAllowed = iconAllowed;
    this.largeIconDisplayed = largeIconDisplayed;
  }

  public ActionWrapper(Action wrappedAction, boolean nameAllowed, boolean iconAllowed) {
    this(wrappedAction, nameAllowed, iconAllowed, false);
  }

  public ActionWrapper(Action wrappedAction, boolean nameAllowed) {
    this(wrappedAction, nameAllowed, true, false);
  }

  public ActionWrapper(Action wrappedAction) {
    this(wrappedAction, true, true, false);
  }

  // #########################################################################
  public Action getWrappedAction() {
    return wrappedAction;
  }

  public boolean isNameAllowed() {
    return nameAllowed;
  }

  public void setNameAllowed(boolean nameAllowed) {
    this.nameAllowed = nameAllowed;
    if (!isNameAllowed()) {
      firePropertyChange(Action.NAME, wrappedAction.getValue(Action.NAME), null);
    } else {
      firePropertyChange(Action.NAME, null, wrappedAction.getValue(Action.NAME));
    }
  }

  public boolean isIconAllowed() {
    return iconAllowed;
  }

  public void setIconAllowed(boolean iconAllowed) {
    this.iconAllowed = iconAllowed;
    if (!isIconAllowed()) {
      firePropertyChange(Action.SMALL_ICON, wrappedAction.getValue(Action.SMALL_ICON), null);
      firePropertyChange(Action.LARGE_ICON_KEY, wrappedAction.getValue(Action.LARGE_ICON_KEY),
          null);
    } else {
      firePropertyChange(Action.SMALL_ICON, null, wrappedAction.getValue(Action.SMALL_ICON));
      firePropertyChange(Action.LARGE_ICON_KEY, null,
          wrappedAction.getValue(Action.LARGE_ICON_KEY));
    }
  }

  public boolean isLargeIconDisplayed() {
    return largeIconDisplayed;
  }

  public void setLargeIconDisplayed(boolean largeIconDisplayed) {
    this.largeIconDisplayed = largeIconDisplayed;
    if (!isLargeIconDisplayed()) {
      firePropertyChange(Action.LARGE_ICON_KEY, wrappedAction.getValue(Action.LARGE_ICON_KEY),
          null);
    } else {
      firePropertyChange(Action.LARGE_ICON_KEY, null,
          wrappedAction.getValue(Action.LARGE_ICON_KEY));
    }
  }

  // #########################################################################
  @Override
  public Object getValue(String key) {
    if (Action.NAME.equals(key) && !isNameAllowed()) {
      return null;
    } else if ((Action.SMALL_ICON.equals(key) || Action.LARGE_ICON_KEY.equals(key))) {
      if (!isIconAllowed() || (Action.LARGE_ICON_KEY.equals(key) && !isLargeIconDisplayed())) {
        return null;
      } else if (Action.SMALL_ICON.equals(key) && isLargeIconDisplayed()) {
        return wrappedAction.getValue(Action.LARGE_ICON_KEY);
      }
    }
    return this.wrappedAction.getValue(key);
  }

  @Override
  public void putValue(String key, Object value) {
    if (Action.NAME.equals(key) && !isNameAllowed()) {
      return;
    } else if ((Action.SMALL_ICON.equals(key) || Action.LARGE_ICON_KEY.equals(key))
        && !isIconAllowed()) {
      return;
    }
    this.wrappedAction.putValue(key, value);
  }

  @Override
  public void setEnabled(boolean b) {
    this.wrappedAction.setEnabled(b);
  }

  @Override
  public boolean isEnabled() {
    return this.wrappedAction.isEnabled();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    this.wrappedAction.actionPerformed(e);
  }

  @Override
  public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
    super.addPropertyChangeListener(listener);
    this.wrappedAction.addPropertyChangeListener(listener);
  }

  @Override
  public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
    super.removePropertyChangeListener(listener);
    this.wrappedAction.removePropertyChangeListener(listener);
  }

}
