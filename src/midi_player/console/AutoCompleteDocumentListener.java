package midi_player.console;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

/**
 * Document Listener for auto-completion.
 *
 * @author Scott Robinson
 * @author Mathieu Brunot
 * @see http://stackabuse.com/example-adding-autocomplete-to-jtextfield/
 * @see http://java.sun.com/docs/books/tutorial/index.html
 */
public class AutoCompleteDocumentListener implements DocumentListener {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AutoCompleteDocumentListener.class.getName());

    /**
     * Edition mode: insertion of new characters, or completion.
     */
    private static enum Mode {
        /**
         * Insertion mode.
         */
        INSERT,
        /**
         * Completion mode.
         */
        COMPLETION;
    };
    
    private static final String DEFAULT_REPLACE = "\t";

    /**
     * The text component that will receive auto-completion.
     */
    private final JTextComponent textComponent;
    /**
     * Keywords for auto-completion.
     */
    private final List<String> keywords;
    /**
     * Should the keywords be sorted on every call to autocompletion?
     *
     * <p>
     * If not, they will be sorted only once, during construction.</p>
     */
    private final boolean sortOnCallNeeded;
    /**
     * Completions currently matching for auto-completion.
     */
    private final List<String> matches = new ArrayList<>();
    /**
     * Current position in matching completions for auto-completion.
     */
    private int matchPosition;
    /**
     * Current edition mode.
     */
    private Mode mode = Mode.INSERT;

    /**
     * Construct a listener for a text component and some keywords.
     *
     * <p>
     * The keywords cannot be {@code null} or it will throw an
     * {@code IllegalArgumentException}.</p>
     *
     * @param textComponent Text component
     * @param keywords keywords for autocompletion.
     */
    public AutoCompleteDocumentListener(JTextComponent textComponent, Collection<String> keywords) {
        this(textComponent, keywords, false);
    }

    /**
     * Construct a listener for a text component and some keywords whihch may be
     * used directly or copied.
     *
     * <p>
     * The keywords cannot be {@code null} or it will throw an
     * {@code IllegalArgumentException}.</p>
     *
     * <p>
     * Making a copy of the keywords will ensure that the original keywords
     * collection is never altered (the autocompletion listener requires a
     * sorted list) but will prevent updates of the keywords available.</p>
     *
     * <p>
     * Using the original keywords will ensure that the keywords are always up
     * to date but will (slightly) decrease the performances of the
     * autocompletion as it will sort the keywords on each unitary insert in the
     * text document.</p>
     *
     * @param textComponent Text component
     * @param keywords keywords for autocompletion.
     * @param copy Should we make a copy of the keywords collection?
     */
    public AutoCompleteDocumentListener(JTextComponent textComponent, Collection<String> keywords, boolean copy) {
        this.textComponent = textComponent;
        if (keywords == null) {
            throw new IllegalArgumentException("Keywords cannot be null");
        } else if (copy || !(keywords instanceof List)) {
            this.keywords = new ArrayList<>(keywords);
            Collections.sort(this.keywords);
        } else {
            this.keywords = (List) keywords;
        }
        this.sortOnCallNeeded = !copy;
    }

    protected List<String> getKeywords() {
        return keywords;
    }

    protected boolean isSortOnCallNeeded() {
        return sortOnCallNeeded;
    }

    @Override
    public void changedUpdate(DocumentEvent ev) {
    }

    @Override
    public void removeUpdate(DocumentEvent ev) {
    }

    @Override
    public void insertUpdate(DocumentEvent ev) {
        if (ev.getLength() != 1) {
            return;
        }

        int pos = ev.getOffset();
        String content;
        try {
            content = textComponent.getText(0, pos + 1);
        } catch (BadLocationException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }

        // Find where the word starts
        int start;
        for (start = pos; start >= 0; start--) {
            if (!Character.isLetter(content.charAt(start))) {
                break;
            }
        }

        // Too few chars
        if (pos - start < 2) {
            return;
        }

        if (isSortOnCallNeeded()) {
            Collections.sort(this.keywords);
        }

        String prefix = content.substring(start + 1).toLowerCase();
        int matchStart = Collections.binarySearch(keywords, prefix);
        if (matchStart < 0 && -matchStart <= keywords.size()) {
            String match = keywords.get(-matchStart - 1);
            if (match.startsWith(prefix)) {
                // A completion is found
                String completion = match.substring(pos - start);
                // We cannot modify Document from within notification,
                // so we submit a task that does the change later
                SwingUtilities.invokeLater(this.new CompletionTask(completion, pos + 1));

                // Construct a list of all matches
                matches.clear();
                matchPosition = 0;
                for (int i = -matchStart - 1, n = keywords.size();
                        i < n && match.startsWith(prefix);
                        i++, match = keywords.get(i)) {
                    matches.add(match.substring(pos - start));
                }
            }
        } else {
            // Nothing found
            mode = Mode.INSERT;

            // Empty list of all matches for future navigation
            matches.clear();
            matchPosition = -1;
        }
    }

    /**
     * Action to move to next match.
     */
    public class NextMatchAction extends AbstractAction {

        /**
         * Default Serial Version ID.
         */
        private static final long serialVersionUID = 1L;

        private final String textForInsertion;

        public NextMatchAction() {
            this(DEFAULT_REPLACE);
        }

        public NextMatchAction(String textForInsertion) {
            this.textForInsertion = textForInsertion;
        }

        public NextMatchAction(String textForInsertion, String name) {
            super(name);
            this.textForInsertion = textForInsertion;
        }

        public NextMatchAction(String textForInsertion, String name, Icon icon) {
            super(name, icon);
            this.textForInsertion = textForInsertion;
        }
        
        @Override
        public void actionPerformed(ActionEvent ev) {
            if (mode == Mode.COMPLETION) {
                final int position = textComponent.getSelectionStart();
                matchPosition = (matchPosition + 1) % matches.size();
                final String completion = matches.get(matchPosition);
                String content;
                try {
                    content = textComponent.getText(0, position);
                } catch (BadLocationException e) {
                    LOGGER.log(Level.SEVERE, null, e);
                    return;
                }
                final StringBuilder sb = new StringBuilder(content);
                sb.insert(position, completion);
                textComponent.setText(sb.toString());
                textComponent.setCaretPosition(position + completion.length());
                textComponent.moveCaretPosition(position);
            } else {
                textComponent.replaceSelection(textForInsertion);
            }
        }
    }

    /**
     * Action to move to previous match.
     */
    public class PreviousMatchAction extends AbstractAction {

        /**
         * Default Serial Version ID.
         */
        private static final long serialVersionUID = 1L;

        private final String textForInsertion;

        public PreviousMatchAction() {
            this(DEFAULT_REPLACE);
        }

        public PreviousMatchAction(String textForInsertion) {
            this.textForInsertion = textForInsertion;
        }

        public PreviousMatchAction(String textForInsertion, String name) {
            super(name);
            this.textForInsertion = textForInsertion;
        }

        public PreviousMatchAction(String textForInsertion, String name, Icon icon) {
            super(name, icon);
            this.textForInsertion = textForInsertion;
        }

        @Override
        public void actionPerformed(ActionEvent ev) {
            if (mode == Mode.COMPLETION) {
                final int position = textComponent.getSelectionStart();
                final int size = matches.size();
                matchPosition = (matchPosition + size - 1) % size;
                final String completion = matches.get(matchPosition);
                String content;
                try {
                    content = textComponent.getText(0, position);
                } catch (BadLocationException e) {
                    LOGGER.log(Level.SEVERE, null, e);
                    return;
                }
                final StringBuilder sb = new StringBuilder(content);
                sb.insert(position, completion);
                textComponent.setText(sb.toString());
                textComponent.setCaretPosition(position + completion.length());
                textComponent.moveCaretPosition(position);
            } else {
                textComponent.replaceSelection(textForInsertion);
            }
        }
    }

    /**
     * Action to commit the auto-completion.
     */
    public class CommitAction extends AbstractAction {

        /**
         * Generated Serial Version ID.
         */
        private static final long serialVersionUID = 5794543109646743416L;

        private final String textForInsertion;

        public CommitAction() {
            this(DEFAULT_REPLACE);
        }

        public CommitAction(String textForInsertion) {
            this.textForInsertion = textForInsertion;
        }

        public CommitAction(String textForInsertion, String name) {
            super(name);
            this.textForInsertion = textForInsertion;
        }

        public CommitAction(String textForInsertion, String name, Icon icon) {
            super(name, icon);
            this.textForInsertion = textForInsertion;
        }

        @Override
        public void actionPerformed(ActionEvent ev) {
            if (mode == Mode.COMPLETION) {
                int pos = textComponent.getSelectionEnd();
                StringBuilder sb = new StringBuilder(textComponent.getText());
                sb.insert(pos, " ");
                textComponent.setText(sb.toString());
                textComponent.setCaretPosition(pos + 1);
                mode = Mode.INSERT;
            } else {
                textComponent.replaceSelection(textForInsertion);
            }
        }
    }

    /**
     * Runnable to offer auto-completion.
     */
    private class CompletionTask implements Runnable {

        private final String completion;
        private final int position;

        CompletionTask(String completion, int position) {
            this.completion = completion;
            this.position = position;
        }

        @Override
        public void run() {
            StringBuilder sb = new StringBuilder(textComponent.getText());
            sb.insert(position, completion);
            textComponent.setText(sb.toString());
            textComponent.setCaretPosition(position + completion.length());
            textComponent.moveCaretPosition(position);
            mode = Mode.COMPLETION;
        }
    }

}
