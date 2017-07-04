package midiplayer.frame.action.util;

import java.util.Collections;

import jswingshell.action.IJssAction;
import midiplayer.MidiPlayer;
import midiplayer.frame.MidiPlayerController;
import midiplayer.frame.MidiPlayerFrame;
import midiplayer.frame.MidiPlayerWithListener;
import midiplayer.frame.action.AddAction;
import midiplayer.frame.action.ClearAction;
import midiplayer.frame.action.DisplayAboutAction;
import midiplayer.frame.action.DisplayConsoleAction;
import midiplayer.frame.action.ExitAction;
import midiplayer.frame.action.LocaleAction;
import midiplayer.frame.action.LoopAction;
import midiplayer.frame.action.NextAction;
import midiplayer.frame.action.PauseAction;
import midiplayer.frame.action.PlayAction;
import midiplayer.frame.action.PreviousAction;
import midiplayer.frame.action.RemoveAction;
import midiplayer.frame.action.ShufflePlaylistAction;
import midiplayer.frame.action.SortPlaylistAction;
import midiplayer.frame.action.StopAction;
import midiplayer.frame.action.ToggleControlsIconsAction;
import midiplayer.frame.action.ToggleControlsLargeIconsAction;
import midiplayer.frame.action.ToggleControlsNamesAction;
import midiplayer.resources.LocaleChangeListener;
import midiplayer.resources.ResourceUtils;

/**
 * Action factory.
 *
 * @author Mathieu Brunot
 */
public class ActionFactory {

  private final MidiPlayerController controller;

  public ActionFactory(MidiPlayerController controller) {
    if (controller == null) {
      throw new IllegalArgumentException("Controller cannot be null");
    }
    this.controller = controller;
  }

  public IJssAction getAction(String actionId) {
    IJssAction action = controller.getActionForCommandIdentifier(actionId);
    MidiPlayerController midiPlayerController = controller;
    MidiPlayer midiPlayer = controller.getPlayer();
    MidiPlayerFrame midiPlayerFrame = controller.getFrame();

    switch (actionId) {
      case AddAction.DEFAULT_IDENTIFIER:
        AddAction addAction;
        if (action == null) {
          addAction = new AddAction(midiPlayerFrame.getFileChooser(), midiPlayerFrame, midiPlayer,
              midiPlayerController);
          action = addAction;
          addToShell(midiPlayerController, action);
        } else {
          addAction = (AddAction) action;
          addAction.setFileChooser(midiPlayerFrame.getFileChooser());
          addAction.setParent(midiPlayerFrame);
          addAction.setMidiPlayer(midiPlayer);
          addAction.setDefaultShellController(midiPlayerController);
        }
        break;
      case RemoveAction.DEFAULT_IDENTIFIER:
        RemoveAction removeAction;
        if (action == null) {
          removeAction = new RemoveAction(midiPlayer, midiPlayerController);
          action = removeAction;
          addToShell(midiPlayerController, action);
        } else {
          removeAction = (RemoveAction) action;
          removeAction.setMidiPlayer(midiPlayer);
          removeAction.setDefaultShellController(midiPlayerController);
        }
        removeAction.setMidiPlayerFrame(midiPlayerFrame);
        break;
      case ClearAction.DEFAULT_IDENTIFIER:
        ClearAction clearAction;
        if (action == null) {
          clearAction = new ClearAction(midiPlayer, midiPlayerController);
          action = clearAction;
          addToShell(midiPlayerController, action);
        } else {
          clearAction = (ClearAction) action;
          clearAction.setMidiPlayer(midiPlayer);
          clearAction.setDefaultShellController(midiPlayerController);
        }
        break;
      case ExitAction.DEFAULT_IDENTIFIER:
        ExitAction exitAction;
        if (action == null) {
          exitAction = new ExitAction(midiPlayerController);
          action = exitAction;
          addToShell(midiPlayerController, action);
        } else {
          exitAction = (ExitAction) action;
          exitAction.setDefaultShellController(midiPlayerController);
        }
        exitAction.setClosableResources(Collections.singletonList(midiPlayerFrame));
        break;
      case PreviousAction.DEFAULT_IDENTIFIER:
        PreviousAction previousAction;
        if (action == null) {
          previousAction = new PreviousAction(midiPlayer, midiPlayerController);
          action = previousAction;
          addToShell(midiPlayerController, action);
        } else {
          previousAction = (PreviousAction) action;
          previousAction.setMidiPlayer(midiPlayer);
          previousAction.setDefaultShellController(midiPlayerController);
        }
        // Make all controls enabled on startup
        previousAction.setEnabled(midiPlayer.size() > 1);
        if (midiPlayer instanceof MidiPlayerWithListener) {
          ((MidiPlayerWithListener) midiPlayer).addPropertyChangeListener(previousAction);
        }
        break;
      case PlayAction.DEFAULT_IDENTIFIER:
        PlayAction playAction;
        if (action == null) {
          playAction = new PlayAction(midiPlayer, midiPlayerController);
          action = playAction;
          addToShell(midiPlayerController, action);
        } else {
          playAction = (PlayAction) action;
          playAction.setMidiPlayer(midiPlayer);
          playAction.setDefaultShellController(midiPlayerController);
        }
        // Make all controls enabled on startup
        playAction.setEnabled(!midiPlayer.isEmpty() && !midiPlayer.isPlaying());
        if (midiPlayer instanceof MidiPlayerWithListener) {
          ((MidiPlayerWithListener) midiPlayer).addPropertyChangeListener(playAction);
        }
        break;
      case PauseAction.DEFAULT_IDENTIFIER:
        PauseAction pauseAction;
        if (action == null) {
          pauseAction = new PauseAction(midiPlayer, midiPlayerController);
          action = pauseAction;
          addToShell(midiPlayerController, action);
        } else {
          pauseAction = (PauseAction) action;
          pauseAction.setMidiPlayer(midiPlayer);
          pauseAction.setDefaultShellController(midiPlayerController);
        }
        // Make all controls enabled on startup
        pauseAction.setEnabled(!midiPlayer.isEmpty() && midiPlayer.isPlaying());
        if (midiPlayer instanceof MidiPlayerWithListener) {
          ((MidiPlayerWithListener) midiPlayer).addPropertyChangeListener(pauseAction);
        }
        break;
      case StopAction.DEFAULT_IDENTIFIER:
        StopAction stopAction;
        if (action == null) {
          stopAction = new StopAction(midiPlayer, midiPlayerController);
          action = stopAction;
          addToShell(midiPlayerController, action);
        } else {
          stopAction = (StopAction) action;
          stopAction.setMidiPlayer(midiPlayer);
          stopAction.setDefaultShellController(midiPlayerController);
        }
        // Make all controls enabled on startup
        stopAction.setEnabled(!midiPlayer.isEmpty() && !midiPlayer.isStopped());
        if (midiPlayer instanceof MidiPlayerWithListener) {
          ((MidiPlayerWithListener) midiPlayer).addPropertyChangeListener(stopAction);
        }
        break;
      case NextAction.DEFAULT_IDENTIFIER:
        NextAction nextAction;
        if (action == null) {
          nextAction = new NextAction(midiPlayer, midiPlayerController);
          action = nextAction;
          addToShell(midiPlayerController, action);
        } else {
          nextAction = (NextAction) action;
          nextAction.setMidiPlayer(midiPlayer);
          nextAction.setDefaultShellController(midiPlayerController);
        }
        // Make all controls enabled on startup
        nextAction.setEnabled(midiPlayer.size() > 1);
        if (midiPlayer instanceof MidiPlayerWithListener) {
          ((MidiPlayerWithListener) midiPlayer).addPropertyChangeListener(nextAction);
        }
        break;
      case LoopAction.DEFAULT_IDENTIFIER:
        LoopAction loopAction;
        if (action == null) {
          loopAction = new LoopAction(midiPlayer, midiPlayer.isLooping(), midiPlayerController);
          action = loopAction;
          addToShell(midiPlayerController, action);
        } else {
          loopAction = (LoopAction) action;
          loopAction.setMidiPlayer(midiPlayer);
          loopAction.setDefaultShellController(midiPlayerController);
        }
        break;
      case ShufflePlaylistAction.DEFAULT_IDENTIFIER:
        ShufflePlaylistAction shufflePlaylistAction;
        if (action == null) {
          shufflePlaylistAction = new ShufflePlaylistAction(midiPlayer, midiPlayerController);
          action = shufflePlaylistAction;
          addToShell(midiPlayerController, action);
        } else {
          shufflePlaylistAction = (ShufflePlaylistAction) action;
          shufflePlaylistAction.setMidiPlayer(midiPlayer);
          shufflePlaylistAction.setDefaultShellController(midiPlayerController);
        }
        // Make all controls enabled on startup
        shufflePlaylistAction.setEnabled(midiPlayer.size() > 1);
        if (midiPlayer instanceof MidiPlayerWithListener) {
          ((MidiPlayerWithListener) midiPlayer).addPropertyChangeListener(shufflePlaylistAction);
        }
        break;
      case SortPlaylistAction.DEFAULT_IDENTIFIER:
        SortPlaylistAction sortPlaylistAction;
        if (action == null) {
          sortPlaylistAction = new SortPlaylistAction(midiPlayer, midiPlayerController);
          action = sortPlaylistAction;
          addToShell(midiPlayerController, action);
        } else {
          sortPlaylistAction = (SortPlaylistAction) action;
          sortPlaylistAction.setMidiPlayer(midiPlayer);
          sortPlaylistAction.setDefaultShellController(midiPlayerController);
        }
        // Make all controls enabled on startup
        sortPlaylistAction.setEnabled(midiPlayer.size() > 1);
        if (midiPlayer instanceof MidiPlayerWithListener) {
          ((MidiPlayerWithListener) midiPlayer).addPropertyChangeListener(sortPlaylistAction);
        }
        break;
      case DisplayConsoleAction.DEFAULT_IDENTIFIER:
        DisplayConsoleAction displayConsoleAction;
        if (action == null) {
          displayConsoleAction = new DisplayConsoleAction(midiPlayerController);
          displayConsoleAction.setSelected(Boolean.FALSE);
          action = displayConsoleAction;
          addToShell(midiPlayerController, action);
        } else {
          displayConsoleAction = (DisplayConsoleAction) action;
          displayConsoleAction.setDefaultShellController(midiPlayerController);
        }
        break;
      case ToggleControlsNamesAction.DEFAULT_IDENTIFIER:
        ToggleControlsNamesAction toggleControlsNamesAction;
        if (action == null) {
          toggleControlsNamesAction =
              new ToggleControlsNamesAction(midiPlayerFrame.isDisplayControlsButtonNames(),
                  midiPlayerFrame, midiPlayerController);
          action = toggleControlsNamesAction;
          addToShell(midiPlayerController, action);
        } else {
          toggleControlsNamesAction = (ToggleControlsNamesAction) action;
          toggleControlsNamesAction.setFrame(midiPlayerFrame);
          toggleControlsNamesAction.setDefaultShellController(midiPlayerController);
        }
        break;
      case ToggleControlsIconsAction.DEFAULT_IDENTIFIER:
        ToggleControlsIconsAction toggleControlsIconsAction;
        if (action == null) {
          toggleControlsIconsAction =
              new ToggleControlsIconsAction(midiPlayerFrame.isDisplayControlsButtonIcons(),
                  midiPlayerFrame, midiPlayerController);
          action = toggleControlsIconsAction;
          addToShell(midiPlayerController, action);
        } else {
          toggleControlsIconsAction = (ToggleControlsIconsAction) action;
          toggleControlsIconsAction.setFrame(midiPlayerFrame);
          toggleControlsIconsAction.setDefaultShellController(midiPlayerController);
        }
        break;
      case ToggleControlsLargeIconsAction.DEFAULT_IDENTIFIER:
        ToggleControlsLargeIconsAction toggleControlsLargeIconsAction;
        if (action == null) {
          toggleControlsLargeIconsAction = new ToggleControlsLargeIconsAction(
              midiPlayerFrame.isDisplayControlsButtonLargeIcons(), midiPlayerFrame,
              midiPlayerController);
          action = toggleControlsLargeIconsAction;
          addToShell(midiPlayerController, action);
        } else {
          toggleControlsLargeIconsAction = (ToggleControlsLargeIconsAction) action;
          toggleControlsLargeIconsAction.setFrame(midiPlayerFrame);
          toggleControlsLargeIconsAction.setDefaultShellController(midiPlayerController);
        }
        break;
      case LocaleAction.DEFAULT_IDENTIFIER:
        LocaleAction localeAction;
        if (action == null) {
          localeAction =
              new LocaleAction(ResourceUtils.getAvailableLocales(), midiPlayerController);
          action = localeAction;
          addToShell(midiPlayerController, action);
          localeAction.setSelectedItem(ResourceUtils.getLocale());
        } else {
          localeAction = (LocaleAction) action;
          localeAction.setDefaultShellController(midiPlayerController);
        }
        break;
      case DisplayAboutAction.DEFAULT_IDENTIFIER:
        DisplayAboutAction displayAboutAction;
        if (action == null) {
          displayAboutAction = new DisplayAboutAction(midiPlayerController);
          action = displayAboutAction;
          addToShell(midiPlayerController, action);
        } else {
          displayAboutAction = (DisplayAboutAction) action;
          displayAboutAction.setDefaultShellController(midiPlayerController);
        }
        break;
      default:
        break;
    }

    return action;
  }

  private boolean addToShell(MidiPlayerController midiPlayerController, IJssAction action) {
    // Add action to the shell
    if (action == null) {
      return false;
    }
    boolean added = midiPlayerController.addAction(action);
    // If the action was not added to the model (already there?)
    if (!added && action instanceof LocaleChangeListener
        && !ResourceUtils.containsLocaleChangeListener((LocaleChangeListener) action)) {
      ResourceUtils.addLocaleChangeListener((LocaleChangeListener) action);
    }
    return added;
  }
}
