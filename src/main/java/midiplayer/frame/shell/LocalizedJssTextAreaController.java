package midiplayer.frame.shell;

import jswingshell.JssSimpleModel;
import jswingshell.gui.JssTextArea;
import jswingshell.gui.JssTextAreaController;

/**
 * A shell controller with a localized model.
 *
 * <p>
 * The localized model must be set separately through {@link #setModel(LocalizedJssModel)}.
 * </p>
 *
 * <p>
 * The localized model will automatically register any localized action it contains to the shell.
 * </p>
 *
 * @author Mathieu Brunot
 */
public class LocalizedJssTextAreaController extends JssTextAreaController {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = 7099716556841969986L;

  public LocalizedJssTextAreaController(JssTextArea view) {
    super(view, null);
  }

  public LocalizedJssTextAreaController(String text) {
    super((JssSimpleModel) null, text);
  }

  public void setModel(LocalizedJssModel anotherModel) {
    super.setModel(anotherModel);
  }

  @Override
  public LocalizedJssModel getModel() {
    return (LocalizedJssModel) super.getModel();
  }

}
