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
 * The localized model will automatically register any localized action it
 * contains to the shell.</p>
 *
 * @author Mathieu Brunot
 */
public class LocalizedJssModel extends JssSimpleModel implements Serializable {

    private static final long serialVersionUID = 1L;

    // #########################################################################
    /**
     * Contruct a shell model with no controller.
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
     * Contruct a shell model, with no controller, and the available commands.
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
                .filter((action) -> (action instanceof LocaleChangeListener))
                .forEach((action) -> {
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
            this.getActionIdentifiers().addAll(Arrays.asList(action.getCommandIdentifiers()));
        }
        return added;
    }

    @Override
    public boolean addAll(Collection<? extends IJssAction> actions) {
        // If there already is an action with the same id, do not add!
        List<IJssAction> newActions = new ArrayList<>(actions.size());
        actions.parallelStream().filter((action) -> (action != null))
                .forEach((action) -> {
                    for (String id : action.getCommandIdentifiers()) {
                        if (this.getActionForCommandIdentifier(id) == null) {
                            newActions.add(action);
                            // Maintain list of identifiers
                            if (newActions.add(action)) {
                                this.getActionIdentifiers().addAll(Arrays.asList(action.getCommandIdentifiers()));
                            }
                        }
                    }
                });
        boolean added = false;
        added = newActions.parallelStream()
                .filter((action) -> (action instanceof LocaleChangeListener))
                .map((action) -> ResourceUtils.addLocaleChangeListener((LocaleChangeListener) action))
                .reduce(added, (accumulator, item) -> accumulator | item);
        return added | super.addAll(newActions);
    }

    @Override
    public boolean remove(IJssAction action) {
        boolean removed = super.remove(action);
        if (removed && action instanceof LocaleChangeListener) {
            removed &= ResourceUtils.removeLocaleChangeListener((LocaleChangeListener) action);
        }
        // Maintain list of identifiers
        if (removed && action != null) {
            this.getActionIdentifiers().removeAll(Arrays.asList(action.getCommandIdentifiers()));
        }
        return removed;
    }

    @Override
    public boolean removeAll(Collection<? extends IJssAction> actions) {
        boolean removed = false;
        removed = actions.parallelStream()
                .filter((action) -> (action instanceof LocaleChangeListener))
                .map((action) -> ResourceUtils.removeLocaleChangeListener((LocaleChangeListener) action))
                .reduce(removed, (accumulator, item) -> accumulator | item);
        this.identifiers = null;
        return removed | super.removeAll(actions);
    }

    @Override
    public boolean retainAll(Collection<? extends IJssAction> actions) {
        boolean retained = false;
        retained = super.getAvailableActions().parallelStream()
                .filter((action) -> (action instanceof LocaleChangeListener && !getAvailableActions().contains(action)))
                .map((action) -> ResourceUtils.removeLocaleChangeListener((LocaleChangeListener) action))
                .reduce(retained, (accumulator, item) -> accumulator | item);
        this.identifiers = null;
        return retained | super.retainAll(actions);
    }

    @Override
    public void setController(IJssController anotherController) {
        super.setController(anotherController);
    }

    public Set<IJssAction> getActions() {
        return Collections.unmodifiableSet(super.getAvailableActions());
    }

    public List<String> getActionIdentifiers() {
        if (identifiers == null) {
            Set<IJssAction> actions = super.getAvailableActions();
            if (actions != null && !actions.isEmpty()) {
                identifiers = new ArrayList<>(actions.size());
                actions.parallelStream().filter((action) -> (action != null)).forEach((action) -> {
                    identifiers.addAll(Arrays.asList(action.getCommandIdentifiers()));
                });
            } else {
                identifiers = Collections.emptyList();
            }
        }
        return identifiers;
    }

}
