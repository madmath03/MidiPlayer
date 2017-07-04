package midi_player.frame;

import java.util.EventListener;

/**
 * A simple listener for exceptions.
 *
 * <p>
 * It is mostly adapted for reporting of exceptions thrown (and ignored) by other components: some
 * internal components might handle some exceptions (closing resources, specific return values), but
 * in the meantime, the exception can be reported to the frontend through this interface.
 * </p>
 */
public interface ThrowableListener extends EventListener {

  public void throwableReceived(String msg, Throwable thrown);

  default public void throwableReceived(Throwable thrown) {
    this.throwableReceived(null, thrown);
  }

  default public void throwableReceived(String msg) {
    this.throwableReceived(msg, null);
  }

}
