package midi_player.console;

import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

/**
 * Document Listener for auto-completion.
 * 
 * @author Scott Robinson
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
        COMPLETION
    };

    /**
     * The text component that will receive auto-completion.
     */
    private final JTextComponent textComponent;
    /**
     * Keywords for auto-completion.
     */
    private List<String> keywords;
    /**
     * Current edition mode.
     */
    private Mode mode = Mode.INSERT;

    /**
     * 
     * @param textComponent
     * @param keywords 
     */
    public AutoCompleteDocumentListener(JTextComponent textComponent, List<String> keywords) {
        this.textComponent = textComponent;
        this.keywords = keywords;
        Collections.sort(keywords);
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
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
        int w;
        for (w = pos; w >= 0; w--) {
            if (!Character.isLetter(content.charAt(w))) {
                break;
            }
        }

        // Too few chars
        if (pos - w < 2) {
            return;
        }

        String prefix = content.substring(w + 1).toLowerCase();
        int n = Collections.binarySearch(keywords, prefix);
        if (n < 0 && -n <= keywords.size()) {
            String match = keywords.get(-n - 1);
            if (match.startsWith(prefix)) {
                // A completion is found
                String completion = match.substring(pos - w);
                // We cannot modify Document from within notification,
                // so we submit a task that does the change later
                SwingUtilities.invokeLater(this.new CompletionTask(completion, pos + 1));
            }
        } else {
            // Nothing found
            mode = Mode.INSERT;
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
                textComponent.replaceSelection("\t");
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
