package midi_player.frame.shell;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jswingshell.AbstractJssModel;
import jswingshell.IJssController;
import jswingshell.JssSimpleModel;
import jswingshell.action.IJssAction;
import midi_player.resources.LocaleChangeListener;
import midi_player.resources.ResourceUtils;

/**
 * A localized shell model.
 *
 * <p>
 * The localized model will automatically register any localized action it contains to the shell.
 * </p>
 *
 * @author Mathieu Brunot
 */
public class LocalizedJssModel extends JssSimpleModel implements Serializable {

  private static final long serialVersionUID = 1L;

  // #########################################################################
  /**
   * Construct a shell model with no controller.
   *
   * @see #setController(jswingshell.IJssController)
   * @see AbstractJssModel#AbstractJssModel()
   *
   * @since 1.4
   */
  protected LocalizedJssModel() {
    super();
  }

  /**
   * Construct a shell model, with no controller, and the available commands.
   *
   * @param actions the available commands.
   *
   * @see #setController(jswingshell.IJssController)
   *
   * @since 1.4
   */
  protected LocalizedJssModel(Collection<IJssAction> actions) {
    super(actions);
  }

  public LocalizedJssModel(IJssController controller) {
    super(controller);
  }

  public LocalizedJssModel(IJssController controller, int initialCapacity) {
    super(controller, initialCapacity);
  }

  public LocalizedJssModel(IJssController controller, Collection<IJssAction> actions) {
    super(controller, actions);
  }

  public LocalizedJssModel(AbstractJssModel anotherModel) {
    super(anotherModel);
  }

  public LocalizedJssModel(IJssController anotherController, JssSimpleModel anotherModel) {
    super(anotherController, anotherModel);
  }

  private List<String> identifiers = null;

  // #########################################################################
  @Override
  public void clear() {
    super.getAvailableActions().parallelStream()
        .filter((action) -> (action instanceof LocaleChangeListener)).forEach((action) -> {
          ResourceUtils.removeLocaleChangeListener((LocaleChangeListener) action);
        });
    // Maintain list of identifiers
    this.getActionIdentifiers().clear();
    super.clear();
  }

  @Override
  public boolean add(IJssAction action) {
    // If there already is an action with the same id, exit!
    if (action != null) {
      for (String id : action.getCommandIdentifiers()) {
        if (this.getActionForCommandIdentifier(id) != null) {
          return false;
        }
      }
    }
    boolean added = super.add(action);
    if (added && action instanceof LocaleChangeListener) {
      added &= ResourceUtils.addLocaleChangeListener((LocaleChangeListener) action);
    }
    // Maintain list of identifiers
    if (added && action != null) {
      final Collection<String> ids = getActionIdentifiers();
      ids.addAll(Arrays.asList(action.getCommandIdentifiers()));
      if (this.isSorted() && ids instanceof List) {
        Collections.sort((List<String>) ids);
      }
    }
    return added;
  }

  @Override
  public boolean addAll(Collection<? extends IJssAction> actions) {
    if (actions == null || actions.isEmpty()) {
      return false;
    }
    // If there already is an action with the same id, do not add!
    List<IJssAction> newActions = new ArrayList<>(actions.size());
    actions.parallelStream().filter((action) -> (action != null)).forEach((action) -> {
      for (String id : action.getCommandIdentifiers()) {
        if (this.getActionForCommandIdentifier(id) == null) {
          newActions.add(action);
        }
      }
    });
    boolean added = false;
    added = newActions.parallelStream().filter((action) -> (action instanceof LocaleChangeListener))
        .map((action) -> ResourceUtils.addLocaleChangeListener((LocaleChangeListener) action))
        .reduce(added, (accumulator, item) -> accumulator | item);
    added |= super.addAll(newActions);
    if (added) {
      // Maintain list of identifiers
      final Collection<String> ids = getActionIdentifiers();
      newActions.parallelStream().forEach((action) -> {
        ids.addAll(Arrays.asList(action.getCommandIdentifiers()));
      });
      if (this.isSorted() && ids instanceof List) {
        Collections.sort((List<String>) ids);
      }
    }
    return added;
  }

  @Override
  public boolean remove(IJssAction action) {
    boolean removed = super.remove(action);
    if (removed && action instanceof LocaleChangeListener) {
      removed &= ResourceUtils.removeLocaleChangeListener((LocaleChangeListener) action);
    }
    // Maintain list of identifiers
    if (removed && action != null) {
      final Collection<String> ids = getActionIdentifiers();
      ids.removeAll(Arrays.asList(action.getCommandIdentifiers()));
      if (this.isSorted() && ids instanceof List) {
        Collections.sort((List<String>) ids);
      }
    }
    return removed;
  }

  @Override
  public boolean removeAll(Collection<? extends IJssAction> actions) {
    if (actions == null || actions.isEmpty()) {
      return false;
    }
    boolean removed = false;
    removed = actions.parallelStream().filter((action) -> (action instanceof LocaleChangeListener))
        .map((action) -> ResourceUtils.removeLocaleChangeListener((LocaleChangeListener) action))
        .reduce(removed, (accumulator, item) -> accumulator | item);
    removed |= super.removeAll(actions);
    if (removed) {
      // Maintain list of identifiers
      final Collection<String> ids = getActionIdentifiers();
      actions.parallelStream().forEach((action) -> {
        ids.removeAll(Arrays.asList(action.getCommandIdentifiers()));
      });
      if (this.isSorted() && ids instanceof List) {
        Collections.sort((List<String>) ids);
      }
    }
    return removed;
  }

  @Override
  public boolean retainAll(Collection<? extends IJssAction> actions) {
    if (actions == null) {
      return false;
    }
    boolean retained = false;
    retained = super.getAvailableActions().parallelStream()
        .filter((action) -> (action instanceof LocaleChangeListener
            && !getAvailableActions().contains(action)))
        .map((action) -> ResourceUtils.removeLocaleChangeListener((LocaleChangeListener) action))
        .reduce(retained, (accumulator, item) -> accumulator | item);
    retained |= super.retainAll(actions);
    if (retained) {
      // Maintain list of identifiers
      updateActionIdentifiers();
    }
    return retained;
  }

  @Override
  public void setController(IJssController anotherController) {
    super.setController(anotherController);
  }

  public Set<IJssAction> getActions() {
    return Collections.unmodifiableSet(super.getAvailableActions());
  }

  public Collection<String> getActionIdentifiers() {
    if (identifiers == null) {
      Set<IJssAction> actions = super.getAvailableActions();
      if (actions != null && !actions.isEmpty()) {
        identifiers = new ArrayList<>(actions.size());
      } else {
        identifiers = new ArrayList<>();
      }
    }
    return identifiers;
  }

  public void updateActionIdentifiers() {
    final Collection<String> ids = getActionIdentifiers();
    ids.clear();
    // Update from actions ids
    Set<IJssAction> actions = super.getAvailableActions();
    if (actions != null && !actions.isEmpty()) {
      actions.parallelStream().filter((action) -> (action != null)).forEach((action) -> {
        ids.addAll(Arrays.asList(action.getCommandIdentifiers()));
      });
      if (this.isSorted() && ids instanceof List) {
        Collections.sort((List<String>) ids);
      }
    }
  }

}
