package midi_player.console.action.util;

import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.event.ListDataListener;

import jswingshell.IJssModel;
import jswingshell.action.AbstractJssComboAction;
import jswingshell.action.IJssAction;
import midi_player.frame.shell.LocalizedJssModel;
import midi_player.frame.shell.LocalizedJssTextAreaController;

/**
 * Serialization utils.
 *
 * @author Mathieu Brunot
 */
public final class Serialization {

  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger.getLogger(Serialization.class.getName());

  protected static final Path JAR_PATH;
  protected static final String SERIALIZED_MODEL_RELATIVE_PATH = "midiplayer_actions.data";
  protected static final Path SERIALIZED_MODEL_PATH;

  static {
    Path tempJarPath = null, tempSerializedModelPath = null;
    try {
      tempJarPath = Paths.get(LocalizedJssTextAreaController.class.getProtectionDomain()
          .getCodeSource().getLocation().toURI());
      // If the path is not a directory (current behavior when packaged)
      while (tempJarPath != null && !Files.isDirectory(tempJarPath)) {
        tempJarPath = tempJarPath.getParent();
      }
      if (tempJarPath != null) {
        tempSerializedModelPath = tempJarPath.resolve(SERIALIZED_MODEL_RELATIVE_PATH);
      } else {
        LOGGER.log(Level.SEVERE, "Cannot find the JAR path!");
      }
    } catch (URISyntaxException urie) {
      LOGGER.log(Level.SEVERE, "Cannot find the JAR path", urie);
    } finally {
      JAR_PATH = tempJarPath;
      SERIALIZED_MODEL_PATH = tempSerializedModelPath;
    }
  }

  public static IJssModel loadSerializedModel() {
    IJssModel serializedModel = null;

    if (SERIALIZED_MODEL_PATH != null && Files.isReadable(SERIALIZED_MODEL_PATH)) {
      try (ObjectInputStream ois =
          new ObjectInputStream(new FileInputStream(SERIALIZED_MODEL_PATH.toString()))) {
        Object obj = ois.readObject();
        if (obj != null && obj instanceof IJssModel) {
          serializedModel = (IJssModel) obj;
          LOGGER.log(Level.INFO, "Loaded model successfully!");
        }
      } catch (FileNotFoundException fnfe) {
        LOGGER.log(Level.SEVERE, "Cannot open a file with the given file name: {0}",
            SERIALIZED_MODEL_PATH);
        LOGGER.log(Level.SEVERE, null, fnfe);
      } catch (IOException ioe) {
        LOGGER.log(Level.SEVERE, "An I/O error occurred while processing the file {0}",
            SERIALIZED_MODEL_PATH);
        LOGGER.log(Level.SEVERE, null, ioe);
      } catch (ClassNotFoundException cnfe) {
        LOGGER.log(Level.SEVERE,
            "Cannot recognize the class of the object - is the file {0} corrupted?",
            SERIALIZED_MODEL_PATH);
        LOGGER.log(Level.SEVERE, null, cnfe);
      } catch (Exception ex) {
        LOGGER.log(Level.SEVERE, "An unexpected exception occurred!!");
        LOGGER.log(Level.SEVERE, null, ex);
      }
    }

    return serializedModel;
  }

  public static void saveSerializedModel(IJssModel serializedModel) {
    if (SERIALIZED_MODEL_PATH != null
        && (!Files.exists(SERIALIZED_MODEL_PATH, LinkOption.NOFOLLOW_LINKS)
            || Files.isWritable(SERIALIZED_MODEL_PATH))) {

      // Prevent save of any GUI component listening to actions
      Map<AbstractAction, PropertyChangeListener[]> propertyChangeListenersByActions = null;
      Map<AbstractListModel<?>, ListDataListener[]> listDataListenersByModels = null;
      if (serializedModel instanceof LocalizedJssModel) {
        LocalizedJssModel localizedJssModel = (LocalizedJssModel) serializedModel;
        Set<IJssAction> actions = localizedJssModel.getActions();
        if (actions != null) {
          propertyChangeListenersByActions = new HashMap<>(actions.size());
          listDataListenersByModels = new HashMap<>(actions.size());
          for (IJssAction action : actions) {
            // Remove AbstractActions property listeners
            if (action instanceof AbstractAction) {
              AbstractAction abstractAction = (AbstractAction) action;
              PropertyChangeListener[] listeners = abstractAction.getPropertyChangeListeners();
              for (PropertyChangeListener listener : listeners) {
                abstractAction.removePropertyChangeListener(listener);
              }
              propertyChangeListenersByActions.put(abstractAction, listeners);
            }
            // Remove AbstractListModels list data listeners
            AbstractListModel<?> abstractListModel = null;
            if (action instanceof AbstractListModel) {
              abstractListModel = (AbstractListModel<?>) action;
            } else if (action instanceof AbstractJssComboAction
                && ((AbstractJssComboAction<?>) action).getModel() instanceof AbstractListModel) {
              abstractListModel = (AbstractListModel<?>) ((AbstractJssComboAction<?>) action).getModel();
            }

            if (abstractListModel != null) {
              ListDataListener[] listeners = abstractListModel.getListDataListeners();
              for (ListDataListener listener : listeners) {
                abstractListModel.removeListDataListener(listener);
              }
              listDataListenersByModels.put(abstractListModel, listeners);
            }
          }
        }
      }

      try (ObjectOutputStream ois =
          new ObjectOutputStream(new FileOutputStream(SERIALIZED_MODEL_PATH.toString()))) {
        ois.writeObject(serializedModel);
        LOGGER.log(Level.INFO, "Saved model successfully!");
      } catch (FileNotFoundException fnfe) {
        LOGGER.log(Level.SEVERE, "Cannot write a file with the given file name: {0}",
            SERIALIZED_MODEL_PATH);
        LOGGER.log(Level.SEVERE, null, fnfe);
      } catch (IOException ioe) {
        LOGGER.log(Level.SEVERE, "An I/O error occurred while processing the file {0}",
            SERIALIZED_MODEL_PATH);
        LOGGER.log(Level.SEVERE, null, ioe);
      }

      // Restore AbstractActions property listeners
      if (propertyChangeListenersByActions != null && !propertyChangeListenersByActions.isEmpty()) {
        for (Map.Entry<AbstractAction, PropertyChangeListener[]> entry : propertyChangeListenersByActions
            .entrySet()) {
          AbstractAction abstractAction = entry.getKey();
          PropertyChangeListener[] listeners = entry.getValue();
          for (PropertyChangeListener listener : listeners) {
            abstractAction.addPropertyChangeListener(listener);
          }
        }
      }
      // Restore AbstractListModels list data listeners
      if (listDataListenersByModels != null && !listDataListenersByModels.isEmpty()) {
        for (Map.Entry<AbstractListModel<?>, ListDataListener[]> entry : listDataListenersByModels
            .entrySet()) {
          AbstractListModel<?> abstractListModel = entry.getKey();
          ListDataListener[] listeners = entry.getValue();
          for (ListDataListener listener : listeners) {
            abstractListModel.addListDataListener(listener);
          }
        }
      }
    }
  }

  public static Action loadSerializedAction(String actionCommandKey) {
    Action action = null;

    if (JAR_PATH != null) {
      Path serializedActionPath = JAR_PATH.resolve(actionCommandKey);
      if (Files.isReadable(serializedActionPath)) {
        try (ObjectInputStream ois =
            new ObjectInputStream(new FileInputStream(serializedActionPath.toString()))) {
          Object obj = ois.readObject();
          if (obj != null && obj instanceof Action) {
            action = (Action) obj;
            LOGGER.log(Level.INFO, "Action loaded from previous state successfully!");
          }
        } catch (FileNotFoundException fnfe) {
          LOGGER.log(Level.SEVERE, "Cannot open a file with the given file name: {0}",
              serializedActionPath);
          LOGGER.log(Level.SEVERE, null, fnfe);
        } catch (IOException ioe) {
          LOGGER.log(Level.SEVERE, "An I/O error occurred while processing the file {0}",
              serializedActionPath);
          LOGGER.log(Level.SEVERE, null, ioe);
        } catch (ClassNotFoundException cnfe) {
          LOGGER.log(Level.SEVERE,
              "Cannot recognize the class of the object - is the file {0} corrupted?",
              serializedActionPath);
          LOGGER.log(Level.SEVERE, null, cnfe);
        } catch (Exception ex) {
          LOGGER.log(Level.SEVERE, "An unexpected exception occurred!!");
          LOGGER.log(Level.SEVERE, null, ex);
        }
      }
    }

    return action;
  }

  public static void saveSerializedAction(Action action) {
    if (action != null && JAR_PATH != null) {
      String actionName;
      if (action.getValue(Action.ACTION_COMMAND_KEY) != null) {
        actionName = (String) action.getValue(Action.ACTION_COMMAND_KEY);
      } else {
        actionName = action.getClass().toString();
      }

      Path serializedActionPath = JAR_PATH.resolve(actionName);
      if (!Files.exists(serializedActionPath, LinkOption.NOFOLLOW_LINKS)
          || Files.isWritable(serializedActionPath)) {

        // Remove AbstractAction property listeners
        AbstractAction abstractAction = null;
        PropertyChangeListener[] propertyChangeListeners = null;
        if (action instanceof AbstractAction) {
          abstractAction = (AbstractAction) action;
          propertyChangeListeners = abstractAction.getPropertyChangeListeners();
          for (PropertyChangeListener listener : propertyChangeListeners) {
            abstractAction.removePropertyChangeListener(listener);
          }
        }
        // Remove AbstractListModels list data listeners
        AbstractListModel<?> abstractListModel = null;
        ListDataListener[] listDataListeners = null;
        if (action instanceof AbstractListModel) {
          abstractListModel = (AbstractListModel<?>) action;
        } else if (action instanceof AbstractJssComboAction
            && ((AbstractJssComboAction<?>) action).getModel() instanceof AbstractListModel) {
          abstractListModel = (AbstractListModel<?>) ((AbstractJssComboAction<?>) action).getModel();
        }

        if (abstractListModel != null) {
          listDataListeners = abstractListModel.getListDataListeners();
          for (ListDataListener listener : listDataListeners) {
            abstractListModel.removeListDataListener(listener);
          }
        }

        try (ObjectOutputStream ois =
            new ObjectOutputStream(new FileOutputStream(serializedActionPath.toString()))) {
          ois.writeObject(action);
          LOGGER.log(Level.INFO, "Saved action {0} successfully!",
              serializedActionPath.getFileName().toString());
        } catch (FileNotFoundException fnfe) {
          LOGGER.log(Level.SEVERE, "Cannot write a file with the given file name: {0}",
              serializedActionPath);
          LOGGER.log(Level.SEVERE, null, fnfe);
        } catch (IOException ioe) {
          LOGGER.log(Level.SEVERE, "An I/O error occurred while processing the file {0}",
              serializedActionPath);
          LOGGER.log(Level.SEVERE, null, ioe);
        }

        // Restore AbstractActions property listeners
        if (abstractAction != null && propertyChangeListeners != null
            && propertyChangeListeners.length > 0) {
          for (PropertyChangeListener listener : propertyChangeListeners) {
            abstractAction.addPropertyChangeListener(listener);
          }
        }
        // Restore AbstractListModel list data listeners
        if (abstractListModel != null && listDataListeners != null
            && listDataListeners.length > 0) {
          for (ListDataListener listener : listDataListeners) {
            abstractListModel.addListDataListener(listener);
          }
        }
      }
    }
  }

  // #########################################################################
  /**
   * Private final constructor to prevent instantiation.
   */
  private Serialization() {}

}
