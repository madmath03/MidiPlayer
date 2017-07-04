package midiplayer.frame;

import java.beans.PropertyChangeListener;
import java.util.Collection;

import jswingshell.IJssView;
import jswingshell.action.IJssAction;
import jswingshell.gui.JssTextArea;
import midiplayer.MidiPlayer;
import midiplayer.frame.shell.LocalizedJssTextAreaController;

/**
 *
 * @author brunot
 */
public class MidiPlayerController extends LocalizedJssTextAreaController {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = -7874735592057964099L;

  private transient MidiPlayerFrame frame;

  private transient MidiPlayer player;

  public MidiPlayerController() {
    this(null, null, null);
  }

  public MidiPlayerController(JssTextArea view, MidiPlayerFrame frame, MidiPlayer player) {
    super(view);
    this.frame = frame;
    this.player = player;
  }

  // #########################################################################
  public MidiPlayer getPlayer() {
    return player;
  }

  public void setPlayer(MidiPlayer player) {
    this.player = player;
  }

  public MidiPlayerFrame getFrame() {
    return frame;
  }

  public void setFrame(MidiPlayerFrame frame) {
    this.frame = frame;
  }

  // #########################################################################
  public boolean addAction(IJssAction action) {
    boolean added = getModel().add(action);
    if (player instanceof MidiPlayerWithListener && action instanceof PropertyChangeListener) {
      added &= ((MidiPlayerWithListener) player)
          .addPropertyChangeListener((PropertyChangeListener) action);
    }
    return added;
  }

  public boolean addAllAction(Collection<? extends IJssAction> actions) {
    boolean added = getModel().addAll(actions);
    if (player instanceof MidiPlayerWithListener) {
      MidiPlayerWithListener midiPlayer = (MidiPlayerWithListener) player;
      actions.parallelStream().filter((action) -> (action instanceof PropertyChangeListener))
          .forEach((action) -> {
            midiPlayer.addPropertyChangeListener((PropertyChangeListener) action);
          });
    }
    return added;
  }

  public boolean removeAction(IJssAction action) {
    boolean removed = getModel().remove(action);
    if (player instanceof MidiPlayerWithListener && action instanceof PropertyChangeListener) {
      ((MidiPlayerWithListener) player)
          .removePropertyChangeListener((PropertyChangeListener) action);
    }
    return removed;
  }

  public boolean removeAllAction(Collection<? extends IJssAction> actions) {
    boolean removed = getModel().removeAll(actions);
    if (player instanceof MidiPlayerWithListener) {
      MidiPlayerWithListener midiPlayer = (MidiPlayerWithListener) player;
      actions.parallelStream().filter((action) -> (action instanceof PropertyChangeListener))
          .forEach((action) -> {
            midiPlayer.removePropertyChangeListener((PropertyChangeListener) action);
          });
    }
    return removed;
  }

  public void clear() {
    getModel().clear();
    if (player instanceof MidiPlayerWithListener) {
      MidiPlayerWithListener midiPlayer = (MidiPlayerWithListener) player;
      midiPlayer.clearPropertyChangeListener();
    }
  }

  // #########################################################################
  @Override
  public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
    super.addPropertyChangeListener(listener);
    if (player instanceof MidiPlayerWithListener) {
      ((MidiPlayerWithListener) player).addPropertyChangeListener(listener);
    }
  }

  @Override
  public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
    super.removePropertyChangeListener(listener);
    if (player instanceof MidiPlayerWithListener) {
      ((MidiPlayerWithListener) player).removePropertyChangeListener(listener);
    }
  }

  @Override
  protected void setView(IJssView anotherView) {
    super.setView(anotherView);
  }

}
