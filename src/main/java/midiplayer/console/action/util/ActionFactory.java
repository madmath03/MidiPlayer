package midiplayer.console.action.util;

import jswingshell.IJssController;
import jswingshell.action.IJssAction;
import midiplayer.console.ConsoleFrame;
import midiplayer.console.action.ClearAction;
import midiplayer.console.action.CopyAction;
import midiplayer.console.action.CutAction;
import midiplayer.console.action.EchoAction;
import midiplayer.console.action.FullScreenAction;
import midiplayer.console.action.HelpAction;
import midiplayer.console.action.LevelAction;
import midiplayer.console.action.OpenAction;
import midiplayer.console.action.PasteAction;
import midiplayer.console.action.RecordAction;
import midiplayer.console.action.SaveScreenAction;
import midiplayer.console.action.SelectAllAction;
import midiplayer.console.action.SleepAction;
import midiplayer.console.action.TimeAction;
import midiplayer.console.action.ToggleToolbarAction;
import midiplayer.console.action.ToggleToolbarIconsAction;
import midiplayer.console.action.ToggleToolbarLargeIconsAction;
import midiplayer.console.action.ToggleToolbarLevelCombo;
import midiplayer.console.action.ToggleToolbarLocaleCombo;
import midiplayer.console.action.ToggleToolbarNamesAction;
import midiplayer.console.action.WaitAction;
import midiplayer.console.action.ZoomAction;
import midiplayer.frame.MidiPlayerController;
import midiplayer.frame.shell.LocalizedJssTextAreaController;
import midiplayer.resources.LocaleChangeListener;
import midiplayer.resources.ResourceUtils;

/**
 * Action factory.
 *
 * @author Mathieu Brunot
 */
public class ActionFactory {

  private final ConsoleFrame frame;

  public ActionFactory(ConsoleFrame frame) {
    this.frame = frame;
  }

  public IJssAction getAction(String actionId) {
    IJssAction action = frame.getShellModel().getActionForCommandIdentifier(actionId);
    LocalizedJssTextAreaController controller = frame.getShellController();

    switch (actionId) {
      case OpenAction.DEFAULT_IDENTIFIER:
        OpenAction openAction;
        if (action == null) {
          openAction = new OpenAction(frame.getjFileChooser(), frame, controller);
          action = openAction;
          addToShell(controller, action);
        } else {
          openAction = (OpenAction) action;
          openAction.setFileChooser(frame.getjFileChooser());
          openAction.setParent(frame);
          openAction.setDefaultShellController(controller);
        }
        break;
      case SaveScreenAction.DEFAULT_IDENTIFIER:
        SaveScreenAction saveScreenAction;
        if (action == null) {
          saveScreenAction =
              new SaveScreenAction(frame.getjFileChooser(), frame, frame, controller);
          action = saveScreenAction;
          addToShell(controller, action);
        } else {
          saveScreenAction = (SaveScreenAction) action;
          saveScreenAction.setFileChooser(frame.getjFileChooser());
          saveScreenAction.setParent(frame);
          saveScreenAction.setPrintComponent(frame);
          saveScreenAction.setDefaultShellController(controller);
        }
        break;
      case CutAction.DEFAULT_IDENTIFIER:
        CutAction cutAction;
        if (action == null) {
          cutAction = new CutAction(controller);
          action = cutAction;
        } else {
          cutAction = (CutAction) action;
          cutAction.setDefaultShellController(controller);
        }
        break;
      case CopyAction.DEFAULT_IDENTIFIER:
        CopyAction copyAction;
        if (action == null) {
          copyAction = new CopyAction(controller);
          action = copyAction;
        } else {
          copyAction = (CopyAction) action;
          copyAction.setDefaultShellController(controller);
        }
        break;
      case PasteAction.DEFAULT_IDENTIFIER:
        PasteAction pasteAction;
        if (action == null) {
          pasteAction = new PasteAction(controller);
          action = pasteAction;
        } else {
          pasteAction = (PasteAction) action;
          pasteAction.setDefaultShellController(controller);
        }
        break;
      case ClearAction.DEFAULT_IDENTIFIER:
        ClearAction clearAction;
        if (action == null) {
          clearAction = new ClearAction(controller);
          action = clearAction;
          addToShell(controller, action);
        } else {
          clearAction = (ClearAction) action;
          clearAction.setDefaultShellController(controller);
        }
        break;
      case SelectAllAction.DEFAULT_IDENTIFIER:
        SelectAllAction selectAllAction;
        if (action == null) {
          selectAllAction = new SelectAllAction(controller);
          action = selectAllAction;
        } else {
          selectAllAction = (SelectAllAction) action;
          selectAllAction.setDefaultShellController(controller);
        }
        break;
      case ToggleToolbarAction.DEFAULT_IDENTIFIER:
        ToggleToolbarAction toggleToolbarAction;
        if (action == null) {
          toggleToolbarAction =
              new ToggleToolbarAction(frame.isDisplayToolbar(), frame, controller);
          action = toggleToolbarAction;
          addToShell(controller, action);
        } else {
          toggleToolbarAction = (ToggleToolbarAction) action;
          toggleToolbarAction.setFrame(frame);
          toggleToolbarAction.setDefaultShellController(controller);
        }
        break;
      case ToggleToolbarIconsAction.DEFAULT_IDENTIFIER:
        ToggleToolbarIconsAction toggleToolbarIconsAction;
        if (action == null) {
          toggleToolbarIconsAction =
              new ToggleToolbarIconsAction(frame.isDisplayToolbarButtonIcons(), frame, controller);
          action = toggleToolbarIconsAction;
          addToShell(controller, action);
        } else {
          toggleToolbarIconsAction = (ToggleToolbarIconsAction) action;
          toggleToolbarIconsAction.setFrame(frame);
          toggleToolbarIconsAction.setDefaultShellController(controller);
        }
        break;
      case ToggleToolbarNamesAction.DEFAULT_IDENTIFIER:
        ToggleToolbarNamesAction toggleToolbarNamesAction;
        if (action == null) {
          toggleToolbarNamesAction =
              new ToggleToolbarNamesAction(frame.isDisplayToolbarButtonNames(), frame, controller);
          action = toggleToolbarNamesAction;
          addToShell(controller, action);
        } else {
          toggleToolbarNamesAction = (ToggleToolbarNamesAction) action;
          toggleToolbarNamesAction.setFrame(frame);
          toggleToolbarNamesAction.setDefaultShellController(controller);
        }
        break;
      case ToggleToolbarLargeIconsAction.DEFAULT_IDENTIFIER:
        ToggleToolbarLargeIconsAction toggleToolbarLargeIconsAction;
        if (action == null) {
          toggleToolbarLargeIconsAction = new ToggleToolbarLargeIconsAction(
              frame.isDisplayToolbarButtonLargeIcons(), frame, controller);
          action = toggleToolbarLargeIconsAction;
          addToShell(controller, action);
        } else {
          toggleToolbarLargeIconsAction = (ToggleToolbarLargeIconsAction) action;
          toggleToolbarLargeIconsAction.setFrame(frame);
          toggleToolbarLargeIconsAction.setDefaultShellController(controller);
        }
        break;
      case ToggleToolbarLevelCombo.DEFAULT_IDENTIFIER:
        ToggleToolbarLevelCombo toggleToolbarLevelCombo;
        if (action == null) {
          toggleToolbarLevelCombo =
              new ToggleToolbarLevelCombo(frame.isDisplayToolbarLevelCombo(), frame, controller);
          action = toggleToolbarLevelCombo;
          addToShell(controller, action);
        } else {
          toggleToolbarLevelCombo = (ToggleToolbarLevelCombo) action;
          toggleToolbarLevelCombo.setFrame(frame);
          toggleToolbarLevelCombo.setDefaultShellController(controller);
        }
        break;
      case ToggleToolbarLocaleCombo.DEFAULT_IDENTIFIER:
        ToggleToolbarLocaleCombo toggleToolbarLocaleCombo;
        if (action == null) {
          toggleToolbarLocaleCombo =
              new ToggleToolbarLocaleCombo(frame.isDisplayToolbarLocaleCombo(), frame, controller);
          action = toggleToolbarLocaleCombo;
          addToShell(controller, action);
        } else {
          toggleToolbarLocaleCombo = (ToggleToolbarLocaleCombo) action;
          toggleToolbarLocaleCombo.setFrame(frame);
          toggleToolbarLocaleCombo.setDefaultShellController(controller);
        }
        break;
      case ZoomAction.DEFAULT_IDENTIFIER:
        ZoomAction zoomAction;
        if (action == null) {
          zoomAction = new ZoomAction(controller);
          action = zoomAction;
          addToShell(controller, action);
        } else {
          zoomAction = (ZoomAction) action;
          zoomAction.setDefaultShellController(controller);
        }
        break;
      case FullScreenAction.DEFAULT_IDENTIFIER:
        FullScreenAction fullScreenAction;
        if (action == null) {
          fullScreenAction = new FullScreenAction(false, frame, controller);
          action = fullScreenAction;
          addToShell(controller, action);
        } else {
          fullScreenAction = (FullScreenAction) action;
          fullScreenAction.setFrame(frame);
          fullScreenAction.setDefaultShellController(controller);
        }
        break;
      case RecordAction.DEFAULT_IDENTIFIER:
        RecordAction recordAction;
        if (action == null) {
          recordAction = new RecordAction(controller);
          action = recordAction;
          addToShell(controller, action);
        } else {
          recordAction = (RecordAction) action;
          recordAction.setDefaultShellController(controller);
        }
        break;
      case HelpAction.DEFAULT_IDENTIFIER:
        HelpAction helpAction;
        if (action == null) {
          helpAction = new HelpAction(controller);
          action = helpAction;
          addToShell(controller, action);
        } else {
          helpAction = (HelpAction) action;
          helpAction.setDefaultShellController(controller);
        }
        break;
      case EchoAction.DEFAULT_IDENTIFIER:
        if (action == null) {
          action = (EchoAction) Serialization.loadSerializedAction(actionId);
          if (action == null) {
            action = new EchoAction();
            addToShell(controller, action);
          }
        }
        break;
      case TimeAction.DEFAULT_IDENTIFIER:
        if (action == null) {
          action = new TimeAction();
          addToShell(controller, action);
        }
        break;
      case SleepAction.DEFAULT_IDENTIFIER:
        if (action == null) {
          action = new SleepAction();
          addToShell(controller, action);
        }
        break;
      case WaitAction.DEFAULT_IDENTIFIER:
        if (action == null) {
          action = new WaitAction();
          addToShell(controller, action);
        }
        break;
      case LevelAction.DEFAULT_IDENTIFIER:
        LevelAction levelComboAction;
        if (action == null) {
          levelComboAction = (LevelAction) Serialization.loadSerializedAction(actionId);
          if (levelComboAction == null) {
            levelComboAction =
                new LevelAction(IJssController.PublicationLevel.values(), controller);
            levelComboAction.setSelectedItem(controller.getPublicationLevel());
          }
          action = levelComboAction;
          addToShell(controller, action);
        } else {
          levelComboAction = (LevelAction) action;
          levelComboAction.setDefaultShellController(controller);
        }
        break;
      default:
        break;
    }

    if (action instanceof LocaleChangeListener
        && !ResourceUtils.containsLocaleChangeListener((LocaleChangeListener) action)) {
      ResourceUtils.addLocaleChangeListener((LocaleChangeListener) action);
    }

    return action;
  }

  private boolean addToShell(LocalizedJssTextAreaController controller, IJssAction action) {
    // Add action to the shell
    if (action == null) {
      return false;
    }
    boolean added;
    if (controller instanceof MidiPlayerController) {
      added = ((MidiPlayerController) controller).addAction(action);
    } else {
      added = frame.getShellModel().add(action);
    }
    // If the action was not added to the model (already there?)
    if (!added && action instanceof LocaleChangeListener
        && !ResourceUtils.containsLocaleChangeListener((LocaleChangeListener) action)) {
      ResourceUtils.addLocaleChangeListener((LocaleChangeListener) action);
    }
    return added;
  }

}
