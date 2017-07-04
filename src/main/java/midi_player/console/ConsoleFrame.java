package midi_player.console;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;

import jswingshell.IJssController;
import jswingshell.IJssModel;
import jswingshell.action.IJssAction;
import jswingshell.gui.JssTextArea;
import jswingshell.gui.JssTextAreaController;
import midi_player.console.action.ClearAction;
import midi_player.console.action.CloseAction;
import midi_player.console.action.CopyAction;
import midi_player.console.action.CutAction;
import midi_player.console.action.EchoAction;
import midi_player.console.action.FullScreenAction;
import midi_player.console.action.HelpAction;
import midi_player.console.action.LevelAction;
import midi_player.console.action.OpenAction;
import midi_player.console.action.PasteAction;
import midi_player.console.action.RecordAction;
import midi_player.console.action.RecordSaveAction;
import midi_player.console.action.RecordStartAction;
import midi_player.console.action.RecordStopAction;
import midi_player.console.action.SaveScreenAction;
import midi_player.console.action.SelectAllAction;
import midi_player.console.action.SleepAction;
import midi_player.console.action.TimeAction;
import midi_player.console.action.ToggleToolbarAction;
import midi_player.console.action.ToggleToolbarIconsAction;
import midi_player.console.action.ToggleToolbarLargeIconsAction;
import midi_player.console.action.ToggleToolbarLevelCombo;
import midi_player.console.action.ToggleToolbarLocaleCombo;
import midi_player.console.action.ToggleToolbarNamesAction;
import midi_player.console.action.WaitAction;
import midi_player.console.action.ZoomAction;
import midi_player.console.action.ZoomFitAction;
import midi_player.console.action.ZoomInAction;
import midi_player.console.action.ZoomOutAction;
import midi_player.console.action.util.Serialization;
import midi_player.console.resources.ResourceUtils;
import midi_player.frame.MidiPlayerController;
import midi_player.frame.action.ActionWrapper;
import midi_player.frame.action.DisplayConsoleAction;
import midi_player.frame.action.LocaleAction;
import midi_player.frame.shell.LocalizedJssModel;
import midi_player.frame.shell.LocalizedJssTextAreaController;
import midi_player.resources.LocaleChangeListener;

/**
 * A simple (yet complete) JSwingShell application.
 *
 * @author Mathieu Brunot
 */
public class ConsoleFrame extends javax.swing.JFrame
    implements LocaleChangeListener, AutoCloseable {

  /**
   * The {@code serialVersionUID}.
   */
  private static final long serialVersionUID = -6571723575036191112L;

  /**
   * Logger.
   */
  private static final Logger LOGGER = Logger.getLogger(ConsoleFrame.class.getName());

  private static final String NEXT_MATCH = "next_match";

  private static final String PREVIOUS_MATCH = "previous_match";

  private static final String COMMIT_ACTION = "commit";

  /**
   * Create new form ConsoleFrame.
   */
  public ConsoleFrame() {
    super();
    initComponents();
    initProperties(null);
    initActions();
    initInternationalization();
  }

  /**
   * Create new form ConsoleFrame.
   *
   * @param title the title for the frame
   */
  public ConsoleFrame(String title) {
    this(null, title);
  }

  /**
   * Create new form ConsoleFrame.
   *
   * @param aShellController the shell controller for the frame
   * @param title the title for the frame
   */
  public ConsoleFrame(MidiPlayerController aShellController, String title) {
    super(title);
    initComponents();
    if (aShellController != null) {
      initProperties(aShellController);
      initActions();
    }
    initInternationalization();
  }

  public void setController(MidiPlayerController aShellController) {
    if (aShellController != null) {
      if (this.shellController != null) {
        this.shellController.getModel().clear();
      }
      this.shellController = aShellController;
      initProperties(aShellController);
      initActions();
    }
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPopupMenu = new javax.swing.JPopupMenu();
    jPopupMenuItemCut = new javax.swing.JMenuItem();
    jPopupMenuItemCopy = new javax.swing.JMenuItem();
    jPopupMenuItemPaste = new javax.swing.JMenuItem();
    jPopupMenuSeparatorClear = new javax.swing.JPopupMenu.Separator();
    jPopupMenuItemClear = new javax.swing.JMenuItem();
    jPopupMenuItemSelectAll = new javax.swing.JMenuItem();
    jFileChooser = new javax.swing.JFileChooser();
    jScrollPane = new javax.swing.JScrollPane();
    jssTextArea = new jswingshell.gui.JssTextArea();
    jToolBar = new javax.swing.JToolBar();
    jToolBarButtonCut = new javax.swing.JButton();
    jToolBarButtonCopy = new javax.swing.JButton();
    jToolBarButtonPaste = new javax.swing.JButton();
    jToolBarSeparatorZoom = new javax.swing.JToolBar.Separator();
    jToolBarButtonZoomOut = new javax.swing.JButton();
    jToolBarButtonZoomReset = new javax.swing.JButton();
    jToolBarButtonZoomIn = new javax.swing.JButton();
    jToolBarSeparatorRecord = new javax.swing.JToolBar.Separator();
    jToolBarButtonRecordStart = new javax.swing.JButton();
    jToolBarButtonRecordStop = new javax.swing.JButton();
    jToolBarButtonRecordSave = new javax.swing.JButton();
    jMenuBar = new javax.swing.JMenuBar();
    jMenuFile = new javax.swing.JMenu();
    jMenuFileItemOpenFile = new javax.swing.JMenuItem();
    jMenuFileSeparatorSaveScreenshot = new javax.swing.JPopupMenu.Separator();
    jMenuFileItemSaveScreenshot = new javax.swing.JMenuItem();
    jMenuFileSeparatorExit = new javax.swing.JPopupMenu.Separator();
    jMenuFileItemExit = new javax.swing.JMenuItem();
    jMenuEdit = new javax.swing.JMenu();
    jMenuEditItemCut = new javax.swing.JMenuItem();
    jMenuEditItemCopy = new javax.swing.JMenuItem();
    jMenuEditItemPaste = new javax.swing.JMenuItem();
    jMenuEditSeparatorClear = new javax.swing.JPopupMenu.Separator();
    jMenuEditItemClear = new javax.swing.JMenuItem();
    jMenuEditItemSelectAll = new javax.swing.JMenuItem();
    jMenuEditSeparatorLevel = new javax.swing.JPopupMenu.Separator();
    jMenuEditMenuLevel = new javax.swing.JMenu();
    jMenuEditSeparatorLocale = new javax.swing.JPopupMenu.Separator();
    jMenuEditMenuLocale = new javax.swing.JMenu();
    jMenuView = new javax.swing.JMenu();
    jMenuViewMenuToolbar = new javax.swing.JMenu();
    jMenuToolbarCheckBoxShow = new javax.swing.JCheckBoxMenuItem();
    jMenuToolbarSeparatorDisplay = new javax.swing.JPopupMenu.Separator();
    jMenuToolbarCheckBoxDisplayIcons = new javax.swing.JCheckBoxMenuItem();
    jMenuToolbarCheckBoxDisplayNames = new javax.swing.JCheckBoxMenuItem();
    jMenuToolbarSeparatorDisplayLargeIcons = new javax.swing.JPopupMenu.Separator();
    jMenuToolbarCheckBoxDisplayLargeIcons = new javax.swing.JCheckBoxMenuItem();
    jMenuToolbarSeparatorDisplayLevelCombo = new javax.swing.JPopupMenu.Separator();
    jMenuToolbarCheckBoxDisplayLevelCombo = new javax.swing.JCheckBoxMenuItem();
    jMenuToolbarSeparatorDisplayLocaleCombo = new javax.swing.JPopupMenu.Separator();
    jMenuToolbarCheckBoxDisplayLocaleCombo = new javax.swing.JCheckBoxMenuItem();
    jMenuViewMenuZoom = new javax.swing.JMenu();
    jMenuZoomItemZoomIn = new javax.swing.JMenuItem();
    jMenuZoomItemZoomOut = new javax.swing.JMenuItem();
    jMenuZoomSeparatorZoomIn = new javax.swing.JPopupMenu.Separator();
    jMenuZoomItemZoomReset = new javax.swing.JMenuItem();
    jMenuViewCheckBoxItemFullScreen = new javax.swing.JCheckBoxMenuItem();
    jMenuRecord = new javax.swing.JMenu();
    jMenuRecordItemStart = new javax.swing.JMenuItem();
    jMenuRecordItemStop = new javax.swing.JMenuItem();
    jMenuRecordSeparatorSave = new javax.swing.JPopupMenu.Separator();
    jMenuRecordItemSave = new javax.swing.JMenuItem();
    jMenuHelp = new javax.swing.JMenu();
    jMenuHelpItemCommandHelp = new javax.swing.JMenuItem();

    jPopupMenuItemCut.setText("Cut");
    jPopupMenu.add(jPopupMenuItemCut);

    jPopupMenuItemCopy.setText("Copy");
    jPopupMenu.add(jPopupMenuItemCopy);

    jPopupMenuItemPaste.setText("Paste");
    jPopupMenu.add(jPopupMenuItemPaste);
    jPopupMenu.add(jPopupMenuSeparatorClear);
    jPopupMenuSeparatorClear.getAccessibleContext().setAccessibleName("");

    jPopupMenuItemClear.setText("Clear");
    jPopupMenu.add(jPopupMenuItemClear);
    jPopupMenuItemClear.getAccessibleContext().setAccessibleDescription("");

    jPopupMenuItemSelectAll.setText("Slect All");
    jPopupMenu.add(jPopupMenuItemSelectAll);
    jPopupMenuItemSelectAll.getAccessibleContext().setAccessibleName("Select All");

    jPopupMenu.getAccessibleContext().setAccessibleName("");

    jFileChooser.getAccessibleContext().setAccessibleName("File Chooser");

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

    jssTextArea.setColumns(20);
    jssTextArea.setRows(5);
    jssTextArea.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
      public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
        jssTextAreaMouseWheelMoved(evt);
      }
    });
    jssTextArea.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(java.awt.event.MouseEvent evt) {
        jssTextAreaMousePressed(evt);
      }

      public void mouseReleased(java.awt.event.MouseEvent evt) {
        jssTextAreaMouseReleased(evt);
      }
    });
    jScrollPane.setViewportView(jssTextArea);
    jssTextArea.getAccessibleContext().setAccessibleName("Shell area");

    getContentPane().add(jScrollPane, java.awt.BorderLayout.CENTER);

    jToolBar.setRollover(true);

    jToolBarButtonCut.setText("Cut");
    jToolBarButtonCut.setFocusable(false);
    jToolBarButtonCut.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jToolBarButtonCut.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar.add(jToolBarButtonCut);

    jToolBarButtonCopy.setText("Copy");
    jToolBarButtonCopy.setFocusable(false);
    jToolBarButtonCopy.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jToolBarButtonCopy.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar.add(jToolBarButtonCopy);

    jToolBarButtonPaste.setText("Paste");
    jToolBarButtonPaste.setFocusable(false);
    jToolBarButtonPaste.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jToolBarButtonPaste.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar.add(jToolBarButtonPaste);
    jToolBar.add(jToolBarSeparatorZoom);

    jToolBarButtonZoomOut.setText("Zoom out");
    jToolBarButtonZoomOut.setFocusable(false);
    jToolBarButtonZoomOut.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jToolBarButtonZoomOut.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar.add(jToolBarButtonZoomOut);

    jToolBarButtonZoomReset.setText("Reset");
    jToolBarButtonZoomReset.setFocusable(false);
    jToolBarButtonZoomReset.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jToolBarButtonZoomReset.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar.add(jToolBarButtonZoomReset);

    jToolBarButtonZoomIn.setText("Zoom in");
    jToolBarButtonZoomIn.setFocusable(false);
    jToolBarButtonZoomIn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jToolBarButtonZoomIn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar.add(jToolBarButtonZoomIn);
    jToolBar.add(jToolBarSeparatorRecord);

    jToolBarButtonRecordStart.setText("Start");
    jToolBarButtonRecordStart.setFocusable(false);
    jToolBarButtonRecordStart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jToolBarButtonRecordStart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar.add(jToolBarButtonRecordStart);

    jToolBarButtonRecordStop.setText("Stop");
    jToolBarButtonRecordStop.setFocusable(false);
    jToolBarButtonRecordStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jToolBarButtonRecordStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar.add(jToolBarButtonRecordStop);

    jToolBarButtonRecordSave.setText("Save");
    jToolBarButtonRecordSave.setFocusable(false);
    jToolBarButtonRecordSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jToolBarButtonRecordSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar.add(jToolBarButtonRecordSave);

    getContentPane().add(jToolBar, java.awt.BorderLayout.PAGE_START);

    jMenuFile.setText("File");

    jMenuFileItemOpenFile.setText("Open");
    jMenuFile.add(jMenuFileItemOpenFile);
    jMenuFile.add(jMenuFileSeparatorSaveScreenshot);

    jMenuFileItemSaveScreenshot.setText("Save screenshot");
    jMenuFile.add(jMenuFileItemSaveScreenshot);
    jMenuFile.add(jMenuFileSeparatorExit);

    jMenuFileItemExit.setText("Exit");
    jMenuFile.add(jMenuFileItemExit);

    jMenuBar.add(jMenuFile);

    jMenuEdit.setText("Edit");

    jMenuEditItemCut.setText("Cut");
    jMenuEdit.add(jMenuEditItemCut);

    jMenuEditItemCopy.setText("Copy");
    jMenuEdit.add(jMenuEditItemCopy);

    jMenuEditItemPaste.setText("Paste");
    jMenuEdit.add(jMenuEditItemPaste);
    jMenuEdit.add(jMenuEditSeparatorClear);

    jMenuEditItemClear.setText("Clear");
    jMenuEdit.add(jMenuEditItemClear);

    jMenuEditItemSelectAll.setText("Select All");
    jMenuEdit.add(jMenuEditItemSelectAll);
    jMenuEdit.add(jMenuEditSeparatorLevel);

    jMenuEditMenuLevel.setText("Level");
    jMenuEdit.add(jMenuEditMenuLevel);
    jMenuEdit.add(jMenuEditSeparatorLocale);

    jMenuEditMenuLocale.setText("Locale");
    jMenuEdit.add(jMenuEditMenuLocale);

    jMenuBar.add(jMenuEdit);

    jMenuView.setText("View");

    jMenuViewMenuToolbar.setText("Toolbar");

    jMenuToolbarCheckBoxShow.setSelected(true);
    jMenuToolbarCheckBoxShow.setText("Show toolbar");
    jMenuViewMenuToolbar.add(jMenuToolbarCheckBoxShow);
    jMenuViewMenuToolbar.add(jMenuToolbarSeparatorDisplay);

    jMenuToolbarCheckBoxDisplayIcons.setSelected(true);
    jMenuToolbarCheckBoxDisplayIcons.setText("Display icons");
    jMenuToolbarCheckBoxDisplayIcons.setToolTipText("");
    jMenuViewMenuToolbar.add(jMenuToolbarCheckBoxDisplayIcons);

    jMenuToolbarCheckBoxDisplayNames.setText("Display names");
    jMenuViewMenuToolbar.add(jMenuToolbarCheckBoxDisplayNames);
    jMenuViewMenuToolbar.add(jMenuToolbarSeparatorDisplayLargeIcons);

    jMenuToolbarCheckBoxDisplayLargeIcons.setSelected(true);
    jMenuToolbarCheckBoxDisplayLargeIcons.setText("Display large icons");
    jMenuViewMenuToolbar.add(jMenuToolbarCheckBoxDisplayLargeIcons);
    jMenuViewMenuToolbar.add(jMenuToolbarSeparatorDisplayLevelCombo);

    jMenuToolbarCheckBoxDisplayLevelCombo.setText("Display level combo");
    jMenuViewMenuToolbar.add(jMenuToolbarCheckBoxDisplayLevelCombo);
    jMenuViewMenuToolbar.add(jMenuToolbarSeparatorDisplayLocaleCombo);

    jMenuToolbarCheckBoxDisplayLocaleCombo.setSelected(true);
    jMenuToolbarCheckBoxDisplayLocaleCombo.setText("Display locale combo");
    jMenuViewMenuToolbar.add(jMenuToolbarCheckBoxDisplayLocaleCombo);

    jMenuView.add(jMenuViewMenuToolbar);
    jMenuViewMenuToolbar.getAccessibleContext().setAccessibleName("jMenuViewMenuToolbar");

    jMenuViewMenuZoom.setText("Zoom");

    jMenuZoomItemZoomIn.setText("Zoom in");
    jMenuViewMenuZoom.add(jMenuZoomItemZoomIn);

    jMenuZoomItemZoomOut.setText("Zoom out");
    jMenuViewMenuZoom.add(jMenuZoomItemZoomOut);
    jMenuViewMenuZoom.add(jMenuZoomSeparatorZoomIn);

    jMenuZoomItemZoomReset.setText("Reset");
    jMenuViewMenuZoom.add(jMenuZoomItemZoomReset);

    jMenuView.add(jMenuViewMenuZoom);

    jMenuViewCheckBoxItemFullScreen.setSelected(true);
    jMenuViewCheckBoxItemFullScreen.setText("Full Screen");
    jMenuView.add(jMenuViewCheckBoxItemFullScreen);
    jMenuViewCheckBoxItemFullScreen.getAccessibleContext()
        .setAccessibleName("jMenuBarCheckBoxItemFullScreen");

    jMenuBar.add(jMenuView);

    jMenuRecord.setText("Record");

    jMenuRecordItemStart.setText("Start");
    jMenuRecord.add(jMenuRecordItemStart);

    jMenuRecordItemStop.setText("Stop");
    jMenuRecord.add(jMenuRecordItemStop);
    jMenuRecord.add(jMenuRecordSeparatorSave);

    jMenuRecordItemSave.setText("Save");
    jMenuRecord.add(jMenuRecordItemSave);

    jMenuBar.add(jMenuRecord);

    jMenuHelp.setText("Help");

    jMenuHelpItemCommandHelp.setText("Command help");
    jMenuHelp.add(jMenuHelpItemCommandHelp);

    jMenuBar.add(jMenuHelp);

    setJMenuBar(jMenuBar);
    jMenuBar.getAccessibleContext().setAccessibleName("Menu Bar");

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void jssTextAreaMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jssTextAreaMousePressed
    if (evt.isPopupTrigger()) {
      doPop(evt);
    }
  }// GEN-LAST:event_jssTextAreaMousePressed

  private void jssTextAreaMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jssTextAreaMouseReleased
    if (evt.isPopupTrigger()) {
      doPop(evt);
    }
  }// GEN-LAST:event_jssTextAreaMouseReleased

  private void jssTextAreaMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {// GEN-FIRST:event_jssTextAreaMouseWheelMoved
    doZoom(evt);
  }// GEN-LAST:event_jssTextAreaMouseWheelMoved

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JFileChooser jFileChooser;
  private javax.swing.JMenuBar jMenuBar;
  private javax.swing.JMenu jMenuEdit;
  private javax.swing.JMenuItem jMenuEditItemClear;
  private javax.swing.JMenuItem jMenuEditItemCopy;
  private javax.swing.JMenuItem jMenuEditItemCut;
  private javax.swing.JMenuItem jMenuEditItemPaste;
  private javax.swing.JMenuItem jMenuEditItemSelectAll;
  private javax.swing.JMenu jMenuEditMenuLevel;
  private javax.swing.JMenu jMenuEditMenuLocale;
  private javax.swing.JPopupMenu.Separator jMenuEditSeparatorClear;
  private javax.swing.JPopupMenu.Separator jMenuEditSeparatorLevel;
  private javax.swing.JPopupMenu.Separator jMenuEditSeparatorLocale;
  private javax.swing.JMenu jMenuFile;
  private javax.swing.JMenuItem jMenuFileItemExit;
  private javax.swing.JMenuItem jMenuFileItemOpenFile;
  private javax.swing.JMenuItem jMenuFileItemSaveScreenshot;
  private javax.swing.JPopupMenu.Separator jMenuFileSeparatorExit;
  private javax.swing.JPopupMenu.Separator jMenuFileSeparatorSaveScreenshot;
  private javax.swing.JMenu jMenuHelp;
  private javax.swing.JMenuItem jMenuHelpItemCommandHelp;
  private javax.swing.JMenu jMenuRecord;
  private javax.swing.JMenuItem jMenuRecordItemSave;
  private javax.swing.JMenuItem jMenuRecordItemStart;
  private javax.swing.JMenuItem jMenuRecordItemStop;
  private javax.swing.JPopupMenu.Separator jMenuRecordSeparatorSave;
  private javax.swing.JCheckBoxMenuItem jMenuToolbarCheckBoxDisplayIcons;
  private javax.swing.JCheckBoxMenuItem jMenuToolbarCheckBoxDisplayLargeIcons;
  private javax.swing.JCheckBoxMenuItem jMenuToolbarCheckBoxDisplayLevelCombo;
  private javax.swing.JCheckBoxMenuItem jMenuToolbarCheckBoxDisplayLocaleCombo;
  private javax.swing.JCheckBoxMenuItem jMenuToolbarCheckBoxDisplayNames;
  private javax.swing.JCheckBoxMenuItem jMenuToolbarCheckBoxShow;
  private javax.swing.JPopupMenu.Separator jMenuToolbarSeparatorDisplay;
  private javax.swing.JPopupMenu.Separator jMenuToolbarSeparatorDisplayLargeIcons;
  private javax.swing.JPopupMenu.Separator jMenuToolbarSeparatorDisplayLevelCombo;
  private javax.swing.JPopupMenu.Separator jMenuToolbarSeparatorDisplayLocaleCombo;
  private javax.swing.JMenu jMenuView;
  private javax.swing.JCheckBoxMenuItem jMenuViewCheckBoxItemFullScreen;
  private javax.swing.JMenu jMenuViewMenuToolbar;
  private javax.swing.JMenu jMenuViewMenuZoom;
  private javax.swing.JMenuItem jMenuZoomItemZoomIn;
  private javax.swing.JMenuItem jMenuZoomItemZoomOut;
  private javax.swing.JMenuItem jMenuZoomItemZoomReset;
  private javax.swing.JPopupMenu.Separator jMenuZoomSeparatorZoomIn;
  private javax.swing.JPopupMenu jPopupMenu;
  private javax.swing.JMenuItem jPopupMenuItemClear;
  private javax.swing.JMenuItem jPopupMenuItemCopy;
  private javax.swing.JMenuItem jPopupMenuItemCut;
  private javax.swing.JMenuItem jPopupMenuItemPaste;
  private javax.swing.JMenuItem jPopupMenuItemSelectAll;
  private javax.swing.JPopupMenu.Separator jPopupMenuSeparatorClear;
  private javax.swing.JScrollPane jScrollPane;
  private javax.swing.JToolBar jToolBar;
  private javax.swing.JButton jToolBarButtonCopy;
  private javax.swing.JButton jToolBarButtonCut;
  private javax.swing.JButton jToolBarButtonPaste;
  private javax.swing.JButton jToolBarButtonRecordSave;
  private javax.swing.JButton jToolBarButtonRecordStart;
  private javax.swing.JButton jToolBarButtonRecordStop;
  private javax.swing.JButton jToolBarButtonZoomIn;
  private javax.swing.JButton jToolBarButtonZoomOut;
  private javax.swing.JButton jToolBarButtonZoomReset;
  private javax.swing.JToolBar.Separator jToolBarSeparatorRecord;
  private javax.swing.JToolBar.Separator jToolBarSeparatorZoom;
  private jswingshell.gui.JssTextArea jssTextArea;
  // End of variables declaration//GEN-END:variables

  // #########################################################################
  // Additionnal variables
  private LocalizedJssTextAreaController shellController;

  private midi_player.console.action.util.ActionFactory consoleActionFactory;
  private midi_player.frame.action.util.ActionFactory midiActionFactory;

  private boolean displayToolbar = true;
  private boolean displayToolbarButtonNames = false;
  private boolean displayToolbarButtonIcons = true;
  private boolean displayToolbarButtonLargeIcons = false;
  private boolean displayToolbarLevelCombo = false;
  private boolean displayToolbarLocaleCombo = false;

  private JToolBar.Separator jToolbarSeparatorLevel;
  private JComboBox<IJssController.PublicationLevel> jToolbarLevelComboBox;

  private JToolBar.Separator jToolbarSeparatorLocale;
  private JComboBox<Locale> jToolbarLocaleComboBox;

  // #########################################################################
  private void initProperties(MidiPlayerController aShellController) {
    // Let's create our shell for the GUI we created
    if (aShellController != null) {
      this.shellController = aShellController;
    } else {
      this.shellController = new LocalizedJssTextAreaController(this.getJssTextArea());
    }
    LocalizedJssModel localizedModel = null;
    {
      IJssModel model = Serialization.loadSerializedModel();
      if (model != null && model instanceof LocalizedJssModel) {
        localizedModel = (LocalizedJssModel) model;
        localizedModel.setController(shellController);
      } else if (model == null) {
        // If no serialized model were found, create a new one
        localizedModel = new LocalizedJssModel(shellController);
      }
    }
    shellController.setModel(localizedModel);

    // Create the action factory to initialize actions
    consoleActionFactory = new midi_player.console.action.util.ActionFactory(this);
    midiActionFactory = new midi_player.frame.action.util.ActionFactory(aShellController);
  }

  // #########################################################################
  private void initInternationalization() {
    ResourceUtils.setTextAndMnemonic(jMenuFile, "midi_player.console.menu.file.text");
    ResourceUtils.setTextAndMnemonic(jMenuEdit, "midi_player.console.menu.edit.text");
    ResourceUtils.setTextAndMnemonic(jMenuView, "midi_player.console.menu.view.text");
    ResourceUtils.setTextAndMnemonic(jMenuViewMenuToolbar,
        "midi_player.console.menu.view.toolbar.text");
    ResourceUtils.setTextAndMnemonic(jMenuViewMenuZoom, "midi_player.console.menu.view.zoom.text");
    ResourceUtils.setTextAndMnemonic(jMenuRecord, "midi_player.console.menu.record.text");
    ResourceUtils.setTextAndMnemonic(jMenuHelp, "midi_player.console.menu.help.text");
  }

  // #########################################################################
  private void initActions() {
    LocaleAction localeComboAction =
        (LocaleAction) midiActionFactory.getAction(LocaleAction.DEFAULT_IDENTIFIER);

    // Retrieve actions that hold frame's properties to update
    ToggleToolbarAction toggleToolbarAction = (ToggleToolbarAction) consoleActionFactory
        .getAction(ToggleToolbarAction.DEFAULT_IDENTIFIER);
    this.displayToolbar = toggleToolbarAction.isSelected();
    ToggleToolbarIconsAction toggleToolbarIconsAction =
        (ToggleToolbarIconsAction) consoleActionFactory
            .getAction(ToggleToolbarIconsAction.DEFAULT_IDENTIFIER);
    this.displayToolbarButtonIcons = toggleToolbarIconsAction.isSelected();
    ToggleToolbarNamesAction toggleToolbarNamesAction =
        (ToggleToolbarNamesAction) consoleActionFactory
            .getAction(ToggleToolbarNamesAction.DEFAULT_IDENTIFIER);
    this.displayToolbarButtonNames = toggleToolbarNamesAction.isSelected();
    ToggleToolbarLargeIconsAction toggleToolbarLargeIconsAction =
        (ToggleToolbarLargeIconsAction) consoleActionFactory
            .getAction(ToggleToolbarLargeIconsAction.DEFAULT_IDENTIFIER);
    this.displayToolbarButtonLargeIcons = toggleToolbarLargeIconsAction.isSelected();

    ToggleToolbarLevelCombo toggleToolbarLevelCombo = (ToggleToolbarLevelCombo) consoleActionFactory
        .getAction(ToggleToolbarLevelCombo.DEFAULT_IDENTIFIER);
    this.displayToolbarLevelCombo = toggleToolbarLevelCombo.isSelected();
    ToggleToolbarLocaleCombo toggleToolbarLocaleCombo =
        (ToggleToolbarLocaleCombo) consoleActionFactory
            .getAction(ToggleToolbarLocaleCombo.DEFAULT_IDENTIFIER);
    this.displayToolbarLocaleCombo = toggleToolbarLocaleCombo.isSelected();

    OpenAction openAction =
        (OpenAction) consoleActionFactory.getAction(OpenAction.DEFAULT_IDENTIFIER);
    SaveScreenAction saveScreenAction =
        (SaveScreenAction) consoleActionFactory.getAction(SaveScreenAction.DEFAULT_IDENTIFIER);
    DisplayConsoleAction displayConsoleAction =
        (DisplayConsoleAction) midiActionFactory.getAction(DisplayConsoleAction.DEFAULT_IDENTIFIER);
    final CloseAction closeAction = new CloseAction(displayConsoleAction);
    ResourceUtils.addLocaleChangeListener(closeAction);

    CutAction cutAction = (CutAction) consoleActionFactory.getAction(CutAction.DEFAULT_IDENTIFIER);
    ActionWrapper wrappedCutAction = new ActionWrapper(cutAction, displayToolbarButtonNames,
        displayToolbarButtonIcons, displayToolbarButtonLargeIcons);

    CopyAction copyAction =
        (CopyAction) consoleActionFactory.getAction(CopyAction.DEFAULT_IDENTIFIER);
    ActionWrapper wrappedCopyAction = new ActionWrapper(copyAction, displayToolbarButtonNames,
        displayToolbarButtonIcons, displayToolbarButtonLargeIcons);

    PasteAction pasteAction =
        (PasteAction) consoleActionFactory.getAction(PasteAction.DEFAULT_IDENTIFIER);
    ResourceUtils.addLocaleChangeListener(pasteAction);
    ActionWrapper wrappedPasteAction = new ActionWrapper(pasteAction, displayToolbarButtonNames,
        displayToolbarButtonIcons, displayToolbarButtonLargeIcons);

    ClearAction clearAction =
        (ClearAction) consoleActionFactory.getAction(ClearAction.DEFAULT_IDENTIFIER);

    SelectAllAction selectAllAction =
        (SelectAllAction) consoleActionFactory.getAction(SelectAllAction.DEFAULT_IDENTIFIER);

    ZoomAction zoomAction =
        (ZoomAction) consoleActionFactory.getAction(ZoomAction.DEFAULT_IDENTIFIER);
    ZoomInAction zoomInAction = new ZoomInAction(zoomAction);
    ResourceUtils.addLocaleChangeListener(zoomInAction);
    ZoomFitAction zoomFitAction = new ZoomFitAction(zoomAction);
    ResourceUtils.addLocaleChangeListener(zoomFitAction);
    ZoomOutAction zoomOutAction = new ZoomOutAction(zoomAction);
    ResourceUtils.addLocaleChangeListener(zoomOutAction);
    ActionWrapper wrappedZoomInAction = new ActionWrapper(zoomInAction, displayToolbarButtonNames,
        displayToolbarButtonIcons, displayToolbarButtonLargeIcons);
    ActionWrapper wrappedZoomFitAction = new ActionWrapper(zoomFitAction, displayToolbarButtonNames,
        displayToolbarButtonIcons, displayToolbarButtonLargeIcons);
    ActionWrapper wrappedZoomOutAction = new ActionWrapper(zoomOutAction, displayToolbarButtonNames,
        displayToolbarButtonIcons, displayToolbarButtonLargeIcons);

    FullScreenAction fullScreenAction =
        (FullScreenAction) consoleActionFactory.getAction(FullScreenAction.DEFAULT_IDENTIFIER);

    RecordAction recordAction =
        (RecordAction) consoleActionFactory.getAction(RecordAction.DEFAULT_IDENTIFIER);
    RecordStartAction recordStartAction = new RecordStartAction(recordAction);
    ResourceUtils.addLocaleChangeListener(recordStartAction);
    RecordStopAction recordStopAction = new RecordStopAction(recordAction);
    ResourceUtils.addLocaleChangeListener(recordStopAction);
    RecordSaveAction recordSaveAction =
        new RecordSaveAction(recordAction, this.getjFileChooser(), this);
    ResourceUtils.addLocaleChangeListener(recordSaveAction);
    ActionWrapper wrappedRecordStartAction = new ActionWrapper(recordStartAction,
        displayToolbarButtonNames, displayToolbarButtonIcons, displayToolbarButtonLargeIcons);
    ActionWrapper wrappedRecordStopAction = new ActionWrapper(recordStopAction,
        displayToolbarButtonNames, displayToolbarButtonIcons, displayToolbarButtonLargeIcons);
    ActionWrapper wrappedRecordSaveAction = new ActionWrapper(recordSaveAction,
        displayToolbarButtonNames, displayToolbarButtonIcons, displayToolbarButtonLargeIcons);

    HelpAction helpAction =
        (HelpAction) consoleActionFactory.getAction(HelpAction.DEFAULT_IDENTIFIER);
    EchoAction echoAction =
        (EchoAction) consoleActionFactory.getAction(EchoAction.DEFAULT_IDENTIFIER);
    TimeAction timeAction =
        (TimeAction) consoleActionFactory.getAction(TimeAction.DEFAULT_IDENTIFIER);
    SleepAction sleepAction =
        (SleepAction) consoleActionFactory.getAction(SleepAction.DEFAULT_IDENTIFIER);
    WaitAction waitAction =
        (WaitAction) consoleActionFactory.getAction(WaitAction.DEFAULT_IDENTIFIER);

    LevelAction levelComboAction =
        (LevelAction) consoleActionFactory.getAction(LevelAction.DEFAULT_IDENTIFIER);

    // And link some actions to the GUI
    this.getjMenuFileItemOpenFile().setAction(openAction);
    this.getjMenuFileItemSaveScreenshot().setAction(saveScreenAction);
    this.getjMenuFileItemExit().setAction(closeAction);

    this.getjMenuEditItemCut().setAction(cutAction);
    this.getjMenuEditItemCopy().setAction(copyAction);
    this.getjMenuEditItemPaste().setAction(pasteAction);
    this.getjMenuEditItemClear().setAction(clearAction);
    this.getjMenuEditItemSelectAll().setAction(selectAllAction);

    this.getjToolBar().setVisible(displayToolbar);
    this.getjMenuToolbarCheckBoxShow().setAction(toggleToolbarAction);
    this.getjMenuToolbarCheckBoxDisplayIcons().setAction(toggleToolbarIconsAction);
    this.getjMenuToolbarCheckBoxDisplayNames().setAction(toggleToolbarNamesAction);
    this.getjMenuToolbarCheckBoxDisplayLargeIcons().setAction(toggleToolbarLargeIconsAction);

    this.getjMenuToolbarCheckBoxDisplayLevelCombo().setAction(toggleToolbarLevelCombo);
    this.getjMenuToolbarCheckBoxDisplayLocaleCombo().setAction(toggleToolbarLocaleCombo);

    this.getjMenuZoomItemZoomIn().setAction(zoomInAction);
    this.getjMenuZoomItemZoomReset().setAction(zoomFitAction);
    this.getjMenuZoomItemZoomOut().setAction(zoomOutAction);

    this.getjMenuViewCheckBoxItemFullScreen().setAction(fullScreenAction);

    this.getjMenuRecordItemStart().setAction(recordStartAction);
    this.getjMenuRecordItemStop().setAction(recordStopAction);
    this.getjMenuRecordItemSave().setAction(recordSaveAction);

    this.getjMenuHelpItemCommandHelp().setAction(helpAction);

    this.getjPopupMenuItemCut().setAction(cutAction);
    this.getjPopupMenuItemCopy().setAction(copyAction);
    this.getjPopupMenuItemPaste().setAction(pasteAction);
    this.getjPopupMenuItemClear().setAction(clearAction);
    this.getjPopupMenuItemSelectAll().setAction(selectAllAction);

    this.getjToolBarButtonCut().setAction(wrappedCutAction);
    this.getjToolBarButtonCopy().setAction(wrappedCopyAction);
    this.getjToolBarButtonPaste().setAction(wrappedPasteAction);
    this.getjToolBarButtonZoomOut().setAction(wrappedZoomOutAction);
    this.getjToolBarButtonZoomReset().setAction(wrappedZoomFitAction);
    this.getjToolBarButtonZoomIn().setAction(wrappedZoomInAction);
    this.getjToolBarButtonRecordStart().setAction(wrappedRecordStartAction);
    this.getjToolBarButtonRecordStop().setAction(wrappedRecordStopAction);
    this.getjToolBarButtonRecordSave().setAction(wrappedRecordSaveAction);

    // Create a JComboBox to define the log level
    jToolbarSeparatorLevel = new JToolBar.Separator();
    jToolBar.add(jToolbarSeparatorLevel);
    jToolbarLevelComboBox = new JComboBox<>(levelComboAction);
    jToolbarLevelComboBox.setAction(levelComboAction);
    jToolBar.add(jToolbarLevelComboBox);

    this.getjMenuEditMenuLevel().setAction(levelComboAction);
    levelComboAction.getInnerElementActions().stream().map((levelItemAction) -> {
      JRadioButtonMenuItem jMenuLevelRadioButtonItem = new JRadioButtonMenuItem();
      jMenuLevelRadioButtonItem.setAction(levelItemAction);
      return jMenuLevelRadioButtonItem;
    }).forEach((jMenuLevelRadioButtonItem) -> {
      this.getjMenuEditMenuLevel().add(jMenuLevelRadioButtonItem);
    });
    jToolbarSeparatorLevel.setVisible(displayToolbarLevelCombo);
    jToolbarLevelComboBox.setVisible(displayToolbarLevelCombo);

    // Create a JComboBox to define the locale
    jToolbarSeparatorLocale = new JToolBar.Separator();
    jToolBar.add(jToolbarSeparatorLocale);
    jToolbarLocaleComboBox = new JComboBox<>(localeComboAction);
    jToolbarLocaleComboBox.setAction(localeComboAction);
    jToolBar.add(jToolbarLocaleComboBox);

    this.getjMenuEditMenuLocale().setAction(localeComboAction);
    localeComboAction.getInnerElementActions().stream().map((localeItemAction) -> {
      JRadioButtonMenuItem jMenuLocaleRadioButtonItem = new JRadioButtonMenuItem();
      jMenuLocaleRadioButtonItem.setAction(localeItemAction);
      return jMenuLocaleRadioButtonItem;
    }).forEach((jMenuLocaleRadioButtonItem) -> {
      this.getjMenuEditMenuLocale().add(jMenuLocaleRadioButtonItem);
    });
    jToolbarSeparatorLocale.setVisible(displayToolbarLocaleCombo);
    jToolbarLocaleComboBox.setVisible(displayToolbarLocaleCombo);

    this.addWindowListener(new WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent e) {
        ConsoleFrame.this.dispose();
        closeAction.actionPerformed(null);
      }

    });

    // There should be an empty command line available at this point
    String commandLine = shellController.getCommandLine();
    if (commandLine == null || !commandLine.isEmpty()) {
      // but if it does not (publication of actions initializing), add one
      shellController.addNewCommandLine();
    }

    // Handle Drop of file from OS
    jssTextArea.setDropMode(DropMode.USE_SELECTION);
    TransferHandler transferHandler = new TextAreaFilesTransferHandler(jssTextArea);
    jssTextArea.setTransferHandler(transferHandler);

    // Handle auto-complete for commands
    AutoCompleteDocumentListener autoCompleteListener = new AutoCompleteDocumentListener(
        jssTextArea, shellController.getModel().getActionIdentifiers());
    jssTextArea.getDocument().addDocumentListener(autoCompleteListener);

    /*
     * Maps the tab key to the next action, which changes the autocomplete to the next suggestion
     * (looping).
     */
    jssTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), NEXT_MATCH);
    jssTextArea.getActionMap().put(NEXT_MATCH, autoCompleteListener.new NextMatchAction());

    /*
     * Maps the tab key to the previous action, which changes the autocomplete to the previous
     * suggestion (looping).
     */
    jssTextArea.getInputMap()
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK), PREVIOUS_MATCH);
    jssTextArea.getActionMap().put(PREVIOUS_MATCH, autoCompleteListener.new PreviousMatchAction());

    /*
     * Maps the tab key to the commit action, which finishes the autocomplete when given a
     * suggestion.
     */
    jssTextArea.setFocusTraversalKeysEnabled(false);
    jssTextArea.getInputMap()
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.CTRL_DOWN_MASK), COMMIT_ACTION);
    jssTextArea.getActionMap().put(COMMIT_ACTION, autoCompleteListener.new CommitAction());
  }

  // #########################################################################
  private void doPop(java.awt.event.MouseEvent e) {
    getjPopupMenu().show(e.getComponent(), e.getX(), e.getY());
  }

  private void doZoom(java.awt.event.MouseWheelEvent e) {
    int rotation = e.getWheelRotation();

    if (e.isControlDown() && (rotation > 0 || rotation < 0)) {
      // Down or Up
      IJssAction action =
          shellController.getModel().getActionForCommandIdentifier(ZoomAction.DEFAULT_IDENTIFIER);
      if (action != null && action instanceof ZoomAction) {
        ZoomAction zoomAction = (ZoomAction) action;
        String[] defaultArgs =
            new String[] {zoomAction.getDefaultCommandIdentifier(), "" + (-rotation)};
        zoomAction.run(defaultArgs);
      }
    } else if (e.getSource() != null && e.getSource().equals(jssTextArea)) {
      jScrollPane.dispatchEvent(e);
    }
  }

  // #########################################################################
  public LocalizedJssTextAreaController getShellController() {
    return shellController;
  }

  public LocalizedJssModel getShellModel() {
    return shellController.getModel();
  }

  // #########################################################################
  public boolean isDisplayToolbar() {
    return displayToolbar;
  }

  public boolean isDisplayToolbarButtonIcons() {
    return displayToolbarButtonIcons;
  }

  public boolean isDisplayToolbarButtonLargeIcons() {
    return displayToolbarButtonLargeIcons;
  }

  public boolean isDisplayToolbarButtonNames() {
    return displayToolbarButtonNames;
  }

  public boolean isDisplayToolbarLevelCombo() {
    return displayToolbarLevelCombo;
  }

  public boolean isDisplayToolbarLocaleCombo() {
    return displayToolbarLocaleCombo;
  }

  // #########################################################################
  public JFileChooser getjFileChooser() {
    return jFileChooser;
  }

  // #########################################################################
  public JScrollPane getjScrollPane() {
    return jScrollPane;
  }

  // #########################################################################
  public JMenuBar getjMenuBar() {
    return jMenuBar;
  }

  public JMenu getjMenuEdit() {
    return jMenuEdit;
  }

  public JMenuItem getjMenuEditItemClear() {
    return jMenuEditItemClear;
  }

  public JMenuItem getjMenuEditItemCopy() {
    return jMenuEditItemCopy;
  }

  public JMenuItem getjMenuEditItemCut() {
    return jMenuEditItemCut;
  }

  public JMenuItem getjMenuEditItemPaste() {
    return jMenuEditItemPaste;
  }

  public JMenuItem getjMenuEditItemSelectAll() {
    return jMenuEditItemSelectAll;
  }

  public JPopupMenu.Separator getjMenuEditSeparatorClear() {
    return jMenuEditSeparatorClear;
  }

  public JMenu getjMenuEditMenuLevel() {
    return jMenuEditMenuLevel;
  }

  public JPopupMenu.Separator getjMenuEditSeparatorLevel() {
    return jMenuEditSeparatorLevel;
  }

  public JMenu getjMenuEditMenuLocale() {
    return jMenuEditMenuLocale;
  }

  public JPopupMenu.Separator getjMenuEditSeparatorLocale() {
    return jMenuEditSeparatorLocale;
  }

  public JMenu getjMenuFile() {
    return jMenuFile;
  }

  public JMenuItem getjMenuFileItemExit() {
    return jMenuFileItemExit;
  }

  public JMenuItem getjMenuFileItemOpenFile() {
    return jMenuFileItemOpenFile;
  }

  public JMenuItem getjMenuFileItemSaveScreenshot() {
    return jMenuFileItemSaveScreenshot;
  }

  public JPopupMenu.Separator getjMenuFileSeparatorExit() {
    return jMenuFileSeparatorExit;
  }

  public JPopupMenu.Separator getjMenuFileSeparatorSaveScreenshot() {
    return jMenuFileSeparatorSaveScreenshot;
  }

  public JMenu getjMenuHelp() {
    return jMenuHelp;
  }

  public JMenuItem getjMenuHelpItemCommandHelp() {
    return jMenuHelpItemCommandHelp;
  }

  public JMenu getjMenuRecord() {
    return jMenuRecord;
  }

  public JMenuItem getjMenuRecordItemSave() {
    return jMenuRecordItemSave;
  }

  public JMenuItem getjMenuRecordItemStart() {
    return jMenuRecordItemStart;
  }

  public JMenuItem getjMenuRecordItemStop() {
    return jMenuRecordItemStop;
  }

  public JPopupMenu.Separator getjMenuRecordSeparatorSave() {
    return jMenuRecordSeparatorSave;
  }

  public JCheckBoxMenuItem getjMenuToolbarCheckBoxShow() {
    return jMenuToolbarCheckBoxShow;
  }

  public JPopupMenu.Separator getjMenuToolbarSeparatorDisplay() {
    return jMenuToolbarSeparatorDisplay;
  }

  public JCheckBoxMenuItem getjMenuToolbarCheckBoxDisplayIcons() {
    return jMenuToolbarCheckBoxDisplayIcons;
  }

  public JCheckBoxMenuItem getjMenuToolbarCheckBoxDisplayNames() {
    return jMenuToolbarCheckBoxDisplayNames;
  }

  public JPopupMenu.Separator getjMenuToolbarSeparatorDisplayLargeIcons() {
    return jMenuToolbarSeparatorDisplayLargeIcons;
  }

  public JCheckBoxMenuItem getjMenuToolbarCheckBoxDisplayLargeIcons() {
    return jMenuToolbarCheckBoxDisplayLargeIcons;
  }

  public JPopupMenu.Separator getjMenuToolbarSeparatorDisplayLevelCombo() {
    return jMenuToolbarSeparatorDisplayLevelCombo;
  }

  public JCheckBoxMenuItem getjMenuToolbarCheckBoxDisplayLevelCombo() {
    return jMenuToolbarCheckBoxDisplayLevelCombo;
  }

  public JPopupMenu.Separator getjMenuToolbarSeparatorDisplayLocaleCombo() {
    return jMenuToolbarSeparatorDisplayLocaleCombo;
  }

  public JCheckBoxMenuItem getjMenuToolbarCheckBoxDisplayLocaleCombo() {
    return jMenuToolbarCheckBoxDisplayLocaleCombo;
  }

  public JMenu getjMenuView() {
    return jMenuView;
  }

  public JCheckBoxMenuItem getjMenuViewCheckBoxItemFullScreen() {
    return jMenuViewCheckBoxItemFullScreen;
  }

  public JMenu getjMenuViewMenuToolbar() {
    return jMenuViewMenuToolbar;
  }

  public JMenu getjMenuViewMenuZoom() {
    return jMenuViewMenuZoom;
  }

  public JMenuItem getjMenuZoomItemZoomIn() {
    return jMenuZoomItemZoomIn;
  }

  public JMenuItem getjMenuZoomItemZoomOut() {
    return jMenuZoomItemZoomOut;
  }

  public JMenuItem getjMenuZoomItemZoomReset() {
    return jMenuZoomItemZoomReset;
  }

  public JPopupMenu.Separator getjMenuZoomSeparatorZoomIn() {
    return jMenuZoomSeparatorZoomIn;
  }

  // #########################################################################
  public JPopupMenu getjPopupMenu() {
    return jPopupMenu;
  }

  public JMenuItem getjPopupMenuItemClear() {
    return jPopupMenuItemClear;
  }

  public JMenuItem getjPopupMenuItemCopy() {
    return jPopupMenuItemCopy;
  }

  public JMenuItem getjPopupMenuItemCut() {
    return jPopupMenuItemCut;
  }

  public JMenuItem getjPopupMenuItemPaste() {
    return jPopupMenuItemPaste;
  }

  public JMenuItem getjPopupMenuItemSelectAll() {
    return jPopupMenuItemSelectAll;
  }

  public JPopupMenu.Separator getjPopupMenuSeparatorClear() {
    return jPopupMenuSeparatorClear;
  }

  // #########################################################################
  public JToolBar getjToolBar() {
    return jToolBar;
  }

  public JButton getjToolBarButtonCopy() {
    return jToolBarButtonCopy;
  }

  public JButton getjToolBarButtonCut() {
    return jToolBarButtonCut;
  }

  public JButton getjToolBarButtonPaste() {
    return jToolBarButtonPaste;
  }

  public JToolBar.Separator getjToolBarSeparatorZoom() {
    return jToolBarSeparatorZoom;
  }

  public JButton getjToolBarButtonZoomIn() {
    return jToolBarButtonZoomIn;
  }

  public JButton getjToolBarButtonZoomOut() {
    return jToolBarButtonZoomOut;
  }

  public JButton getjToolBarButtonZoomReset() {
    return jToolBarButtonZoomReset;
  }

  public JssTextArea getJssTextArea() {
    return jssTextArea;
  }

  public JToolBar.Separator getjToolBarSeparatorRecord() {
    return jToolBarSeparatorRecord;
  }

  public JButton getjToolBarButtonRecordStart() {
    return jToolBarButtonRecordStart;
  }

  public JButton getjToolBarButtonRecordStop() {
    return jToolBarButtonRecordStop;
  }

  public JButton getjToolBarButtonRecordSave() {
    return jToolBarButtonRecordSave;
  }

  public JToolBar.Separator getjToolbarSeparatorLevel() {
    return jToolbarSeparatorLevel;
  }

  public JComboBox<IJssController.PublicationLevel> getjToolbarLevelComboBox() {
    return jToolbarLevelComboBox;
  }

  public JToolBar.Separator getjToolbarSeparatorLocale() {
    return jToolbarSeparatorLocale;
  }

  public JComboBox<Locale> getjToolbarLocaleComboBox() {
    return jToolbarLocaleComboBox;
  }

  // #########################################################################
  @Override
  public void close() throws Exception {
    this.dispose();
  }

  // #########################################################################
  @Override
  public void localeChanged() {
    localeChanged(null);
  }

  @Override
  public void localeChanged(PropertyChangeEvent evt) {
    initInternationalization();
  }

  // #########################################################################
  /**
   * Handles drag & drop for rows reordering and files import.
   *
   * @see http://stackoverflow.com/questions/638807/how-do-i-drag-and-drop-a-row-in-a-jtable
   * @see http://stackoverflow.com/questions/811248/how-can-i-use-drag-and-drop-in-swing-to-get-file-path
   * @see http://www.programcreek.com/java-api-examples/index.php?source_dir=jreepad-master/src/jreepad/editor/TextTransferHandler.java
   */
  static class TextAreaFilesTransferHandler extends TransferHandler {

    /**
     * The {@code serialVersionUID}.
     */
    private static final long serialVersionUID = 6871279426080589252L;

    /**
     * Logger.
     */
    private static final Logger LOGGER =
        Logger.getLogger(TextAreaFilesTransferHandler.class.getName());

    private final DataFlavor filesFlavor = DataFlavor.javaFileListFlavor;

    /**
     * The text area expected as drop target. Also used as the drag source.
     */
    private JssTextArea textArea = null;

    /**
     * A container allowed as a drop target. If the drop occurs on this container, drop will occur
     * in table.
     */
    private JComponent container = null;

    /**
     * Start position in the source text.
     * <p>
     * We need this information when performing a MOVE in order to remove the dragged text from the
     * source.
     * </p>
     */
    private Position p0 = null;
    /**
     * End position in the source text.
     * <p>
     * We need this information when performing a MOVE in order to remove the dragged text from the
     * source.
     * </p>
     */
    private Position p1 = null;

    public TextAreaFilesTransferHandler(JssTextArea textArea) {
      this(textArea, null);
    }

    public TextAreaFilesTransferHandler(JssTextArea textArea, JComponent container) {
      this.textArea = textArea;
      this.container = container;
    }

    @Override
    /**
     * Bundle up the data for export.
     */
    protected Transferable createTransferable(JComponent comp) {
      Transferable transferData = null;
      if (comp instanceof JTextComponent) {
        JTextComponent source = (JTextComponent) comp;
        int start = source.getSelectionStart();
        int end = source.getSelectionEnd();
        if (start == end) {
          return null;
        }
        Document doc = source.getDocument();
        try {
          p0 = doc.createPosition(start);
          p1 = doc.createPosition(end);
        } catch (BadLocationException e) {
          // unable to do a drag
          LOGGER.log(Level.SEVERE, "Could not find position: start=" + start + ", end=" + end, e);
          p0 = p1 = null;
        }
        String data = source.getSelectedText();
        transferData = new StringSelection(data);
      }
      return transferData;
    }

    @Override
    /**
     * These text fields handle both copy and move actions.
     */
    public int getSourceActions(JComponent c) {
      return COPY_OR_MOVE;
    }

    @Override
    /**
     * When the export is complete, remove the old text if the action was a move.
     */
    protected void exportDone(JComponent c, Transferable data, int action) {
      if (action != MOVE) {
        return;
      }

      if ((p0 != null) && (p1 != null) && (p0.getOffset() != p1.getOffset())) {
        try {
          JTextComponent tc = (JTextComponent) c;
          tc.getDocument().remove(p0.getOffset(), p1.getOffset() - p0.getOffset());
        } catch (BadLocationException e) {
          LOGGER.log(Level.SEVERE, "Can't remove text from source.", e);
        }
      }
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport info) {
      boolean b = (info.getComponent() == textArea || info.getComponent() == container)
          && (info.isDataFlavorSupported(filesFlavor)
              || info.isDataFlavorSupported(DataFlavor.stringFlavor));
      textArea.setCursor(b ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
      return b;
    }

    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
      boolean supported = false;
      for (int idx = 0; idx < transferFlavors.length && !supported; idx++) {
        supported = DataFlavor.stringFlavor.equals(transferFlavors[idx]);
      }
      return supported;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
      JssTextAreaController controller = textArea.getController();
      Component component = info.getComponent();
      JssTextArea target;
      if (component instanceof JssTextArea) {
        target = (JssTextArea) info.getComponent();
      } else {
        return false;
      }

      target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

      int index = -1;
      if (info.isDrop()) {
        DropLocation dropLocation = info.getDropLocation();
        if (dropLocation instanceof JTextArea.DropLocation) {
          JTextArea.DropLocation dl = (JTextArea.DropLocation) info.getDropLocation();
          index = dl.getIndex();
        }
      } else {
        index = target.getCaretPosition();
      }

      int min = controller.getCommandLinePosition();
      int max = textArea.getDocument().getLength();
      if (index < min) {
        index = min;
      } else if (index > max) {
        index = max;
      }

      Transferable transferable = info.getTransferable();
      if (info.isDataFlavorSupported(filesFlavor)) {
        List<File> files = null;
        try {
          files = (List<File>) transferable.getTransferData(filesFlavor);
        } catch (UnsupportedFlavorException ex) {
          LOGGER.log(Level.SEVERE, filesFlavor + " flavor is not supported.", ex);
        } catch (IOException ex) {
          LOGGER.log(Level.SEVERE, null, ex);
        }

        if (files != null) {
          String shellText = controller.getShellText();
          StringBuilder stringBuilder = new StringBuilder(shellText.substring(0, index));
          for (File file : files) {
            stringBuilder.append(controller.getCommandParameterSeparator())
                .append(JssTextAreaController.COMMAND_PARAMETER_ENCLOSURE_CHARACTER)
                .append(file.toString())
                .append(JssTextAreaController.COMMAND_PARAMETER_ENCLOSURE_CHARACTER);
          }
          if (index < max) {
            stringBuilder.append(shellText.substring(index + 1, max));
          }
          controller.setShellText(stringBuilder.toString());
          return true;
        }
      } else if (info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        String data = null;
        try {
          data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException ex) {
          LOGGER.log(Level.SEVERE, DataFlavor.stringFlavor + " flavor is not supported.", ex);
        } catch (IOException ex) {
          LOGGER.log(Level.SEVERE, null, ex);
        }

        if (data != null) {
          target.replaceSelection(data);
          return true;
        }
      }

      return false;
    }
  }

}
