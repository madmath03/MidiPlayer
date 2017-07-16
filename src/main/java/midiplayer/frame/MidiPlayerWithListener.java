package midiplayer.frame;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import midiplayer.MidiPlayer;
import midiplayer.resources.LocaleChangeListener;
import midiplayer.resources.ResourceUtils;

public class MidiPlayerWithListener extends MidiPlayer
    implements ReorderableTableModel, LocaleChangeListener {

  /**
   * Logger.
   */
  private static final Logger LOGGER =
      Logger.getLogger(MidiPlayerWithListener.class.getName());

  private static MidiPlayerWithListener instance = null;

  public static MidiPlayerWithListener getInstance() {
    if (instance == null) {
      instance = new MidiPlayerWithListener(true);
    }
    return instance;
  }

  // #########################################################################

  public static final String LOOP_CHANGE = "midiplayer.loop";

  public static final String PLAYING_START_CHANGE = "midiplayer.playing.start";

  public static final String PLAYING_PAUSE_CHANGE = "midiplayer.playing.pause";

  public static final String PLAYING_STOP_CHANGE = "midiplayer.playing.stop";

  public static final String CURRENT_SONG_CHANGE =
      "midiplayer.current_song.change";

  public static final String PLAYLIST_LOOP_CHANGE = "midiplayer.playlist.loop";

  public static final String PLAYLIST_SIZE_CHANGE = "midiplayer.playlist.size";

  public static final String PLAYLIST_CONTENT_CHANGE =
      "midiplayer.playlist.content.change";

  // #########################################################################

  private final List<PropertyChangeListener> propertyChangeListeners =
      new ArrayList<>();

  private final List<TableModelListener> tableModelListeners =
      new ArrayList<>();

  private final List<ThrowableListener> midiPlayerListeners = new ArrayList<>();

  /**
   * Whether to notify listeners on EDT.
   *
   * @serial
   */
  private final boolean notifyOnEDT;

  protected MidiPlayerWithListener() {
    this(false);
  }

  protected MidiPlayerWithListener(boolean notifyOnEDT) {
    super();
    this.notifyOnEDT = notifyOnEDT;
  }

  // #########################################################################
  @Override
  public void setLooping(boolean loop) {
    boolean originallyLooping = this.isLooping();
    super.setLooping(loop);
    fireChange(LOOP_CHANGE, originallyLooping, loop);
  }

  @Override
  public void setPlaylistLooping(boolean loop) {
    boolean originallyLooping = this.isPlaylistLooping();
    super.setPlaylistLooping(loop);
    fireChange(PLAYLIST_LOOP_CHANGE, originallyLooping, loop);
  }

  // #########################################################################
  @Override
  public boolean startPlaying() {
    boolean originallyPlaying = this.isPlaying();
    boolean playing = super.startPlaying();
    if (playing) {
      fireChange(PLAYING_START_CHANGE, originallyPlaying, playing);
    }
    return playing;
  }

  @Override
  public boolean pausePlaying() {
    boolean originallyPaused = this.isPaused();
    boolean paused = super.pausePlaying();
    if (paused) {
      fireChange(PLAYING_PAUSE_CHANGE, originallyPaused, this.isPaused());
    }
    return paused;
  }

  @Override
  public boolean stopPlaying() {
    boolean originallyStopped = this.isStopped();
    boolean stopped = super.stopPlaying();
    if (stopped) {
      fireChange(PLAYING_STOP_CHANGE, originallyStopped, stopped);
    }
    return stopped;
  }

  @Override
  public boolean moveToPreviousSong() {
    int originalSongIndex = this.getCurrentSongIndex();
    boolean moved = super.moveToPreviousSong();
    if (moved) {
      fireChange(CURRENT_SONG_CHANGE, originalSongIndex,
          this.getCurrentSongIndex());
    }
    return moved;
  }

  @Override
  protected synchronized boolean moveToNextSong(boolean force) {
    int originalSongIndex = this.getCurrentSongIndex();
    boolean moved = super.moveToNextSong(force);
    if (moved) {
      fireChange(CURRENT_SONG_CHANGE, originalSongIndex,
          this.getCurrentSongIndex());
    }
    return moved;
  }

  @Override
  public boolean moveToSong(int index) {
    int originalSongIndex = this.getCurrentSongIndex();
    boolean moved = super.moveToSong(index);
    if (moved) {
      fireChange(CURRENT_SONG_CHANGE, originalSongIndex,
          this.getCurrentSongIndex());
    }
    return moved;
  }

  // #########################################################################
  @Override
  public boolean add(Object path) {
    int originalSize = this.size();
    boolean added = super.add(path);
    if (added) {
      int newSize = this.size();
      fireChange(PLAYLIST_SIZE_CHANGE, originalSize, newSize);
      fireChange();
    }
    return added;
  }

  @Override
  public boolean add(int index, Object path) {
    int originalSize = this.size();
    int originalSongIndex = this.getCurrentSongIndex();
    boolean added = super.add(index, path);
    if (added) {
      int newSize = this.size();
      fireChange(PLAYLIST_SIZE_CHANGE, originalSize, newSize);
      fireChange(CURRENT_SONG_CHANGE, originalSongIndex,
          this.getCurrentSongIndex());
      fireChange();
    }
    return added;
  }

  @Override
  public boolean addAll(Collection<? extends Object> paths) {
    int originalSize = this.size();
    boolean added = super.addAll(paths);
    if (added) {
      int newSize = this.size();
      fireChange(PLAYLIST_SIZE_CHANGE, originalSize, newSize);
      fireChange();
    }
    return added;
  }

  @Override
  public boolean addAll(int index, Collection<? extends Object> paths) {
    int originalSize = this.size();
    int originalSongIndex = this.getCurrentSongIndex();
    boolean added = super.addAll(index, paths);
    if (added) {
      int newSize = this.size();
      fireChange(PLAYLIST_SIZE_CHANGE, originalSize, newSize);
      fireChange(CURRENT_SONG_CHANGE, originalSongIndex,
          this.getCurrentSongIndex());
      fireChange();
    }
    return added;
  }

  @Override
  public boolean remove(int index) {
    int originalSize = this.size();
    boolean removed = super.remove(index);
    if (removed) {
      fireChange(PLAYLIST_SIZE_CHANGE, originalSize, this.size());
      fireChange();
    }
    return removed;
  }

  @Override
  protected boolean remove(Object path) {
    int originalSize = this.size();
    boolean removed = super.remove(path);
    if (removed) {
      int newSize = this.size();
      fireChange(PLAYLIST_SIZE_CHANGE, originalSize, newSize);
      fireChange();
    }
    return removed;
  }

  @Override
  public boolean removeAll(int[] indexes) {
    int originalSize = this.size();
    boolean removed = super.removeAll(indexes);
    if (removed) {
      fireChange(PLAYLIST_SIZE_CHANGE, originalSize, this.size());
      fireChange();
    }
    return removed;
  }

  @Override
  protected boolean removeAll(Collection<? extends Object> paths) {
    int originalSize = this.size();
    boolean removed = super.removeAll(paths);
    if (removed) {
      fireChange(PLAYLIST_SIZE_CHANGE, originalSize, this.size());
      fireChange();
    }
    return removed;
  }

  @Override
  public boolean clear() {
    int originalSize = this.size();
    boolean cleared = super.clear();
    if (cleared) {
      fireChange(PLAYLIST_SIZE_CHANGE, originalSize, this.size());
      fireChange();
    }
    return cleared;
  }

  // #########################################################################

  /**
   * Returns {@code notifyOnEDT} property.
   *
   * @return {@code notifyOnEDT} property
   * @see MidiPlayerWithListener#MidiPlayerWithListener(boolean)
   */
  public final boolean isNotifyOnEDT() {
    return notifyOnEDT;
  }

  // #########################################################################
  @Override
  public int getRowCount() {
    return size();
  }

  @Override
  public int getColumnCount() {
    return 1;
  }

  @Override
  public String getColumnName(int columnIndex) {
    String columnName;
    switch (columnIndex) {
      case 0:
        columnName =
            ResourceUtils.getMessage("midiplayer.playlist.column.title.name");
        break;
      default:
        columnName = null;
    }
    return columnName;
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    Class<?> columnClass;
    switch (columnIndex) {
      case 0:
        columnClass = Path.class;
        break;
      default:
        columnClass = null;
    }
    return columnClass;
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return false;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    if (columnIndex == 0) {
      return (rowIndex + 1) + ". "
          + getSongInfo(getPlaylist().get(rowIndex), null);
    } else {
      return null;
    }
  }

  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void moveRow(int start, int end, int to) {
    boolean moved = super.moveSongsTo(start, end, to);
    if (moved) {
      fireChange(PLAYLIST_CONTENT_CHANGE, false, moved);
      fireChange();
    }
  }

  @Override
  public boolean shufflePlaylist() {
    boolean shuffled = super.shufflePlaylist();
    if (shuffled) {
      fireChange(PLAYLIST_CONTENT_CHANGE, false, shuffled);
      fireChange();
    }
    return shuffled;
  }

  @Override
  public boolean shufflePlaylist(Random random) {
    boolean shuffled = super.shufflePlaylist(random);
    if (shuffled) {
      fireChange(PLAYLIST_CONTENT_CHANGE, false, shuffled);
      fireChange();
    }
    return shuffled;
  }

  @Override
  public boolean sortPlaylist() {
    boolean sorted = super.sortPlaylist();
    if (sorted) {
      fireChange(PLAYLIST_CONTENT_CHANGE, false, sorted);
      fireChange();
    }
    return sorted;
  }

  @Override
  public boolean sortPlaylist(Comparator<Object> comparator) {
    boolean sorted = super.sortPlaylist(comparator);
    if (sorted) {
      fireChange(PLAYLIST_CONTENT_CHANGE, false, sorted);
      fireChange();
    }
    return sorted;
  }

  @Override
  public void reportThrowable(String msg, Throwable thrown) {
    fireChange(msg, thrown);
  }

  // #########################################################################

  /**
   * Add a {@code ThrowableListener} to the listener list.
   *
   * <p>
   * The same listener object may be added more than once, and will be called as many times as it is
   * added.
   * </p>
   *
   * <p>
   * If {@code listener} is {@code null}, no exception is thrown and no action is taken.
   * </p>
   *
   * @param listener The {@code ThrowableListener} to be added
   */
  public void addExceptionListener(ThrowableListener listener) {
    midiPlayerListeners.add(listener);
  }

  /**
   * Remove a {@code ThrowableListener} from the listener list.
   *
   * <p>
   * This removes a {@code ThrowableListener} that was registered.
   * </p>
   *
   * <p>
   * If {@code listener} was added more than once to the same event source, it will be notified one
   * less time after being removed. If {@code listener} is null, or was never added, no exception is
   * thrown and no action is taken.
   * </p>
   *
   * @param listener The {@code ThrowableListener} to be removed
   */
  public void removeExceptionListener(ThrowableListener listener) {
    midiPlayerListeners.remove(listener);
  }

  /**
   * Remove all {@code ThrowableListener} from the listener list.
   *
   * <p>
   * This removes all {@code ThrowableListener} that were registered.
   * </p>
   */
  public synchronized void clearExceptionListener() {
    midiPlayerListeners.clear();
  }

  /**
   * Returns an unmodifiable {@code List} of all the listeners which have been associated with the
   * {@code MidiPlayer}.
   *
   * @return all of the {@code ThrowableListener}s associated with the {@code MidiPlayer}.
   */
  public synchronized List<ThrowableListener> getExceptionListeners() {
    return Collections.unmodifiableList(midiPlayerListeners);
  }

  /**
   * Returns {@code true} if the listener list contains the specified element.
   *
   * <p>
   * More formally, returns {@code true} if and only if this list contains at least one element
   * {@code e} such that {@code (o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))}.
   * </p>
   *
   * @param listener element whose presence in the listener list is to be tested
   * @return {@code true} if this list contains the specified element
   *
   * @see List#contains(java.lang.Object)
   */
  public synchronized boolean containsExceptionListener(
      ThrowableListener listener) {
    return midiPlayerListeners.contains(listener);
  }

  /**
   * Reports a bound excpetion message to listeners that have been registered to track errors of
   * {@code MidiPlayer}.
   *
   * <p>
   * This is merely a convenience wrapper around the more general
   * {@link #fireExceptionReceived(List, String)} method.
   * </p>
   *
   * @param msg Exception message received.
   */
  public void fireChange(final String msg) {
    if (!isNotifyOnEDT() || SwingUtilities.isEventDispatchThread()) {
      fireExceptionReceived(this.midiPlayerListeners, msg);
    } else {
      SwingUtilities.invokeLater(() -> {
        fireChange(msg);
      });
    }
  }

  /**
   * Reports a bound excpetion to listeners that have been registered to track errors of
   * {@code MidiPlayer}.
   *
   * <p>
   * This is merely a convenience wrapper around the more general
   * {@link #fireExceptionReceived(List, Throwable)} method.
   * </p>
   *
   * @param thrown Exception received.
   */
  public void fireChange(final Throwable thrown) {
    if (!isNotifyOnEDT() || SwingUtilities.isEventDispatchThread()) {
      fireExceptionReceived(this.midiPlayerListeners, thrown);
    } else {
      SwingUtilities.invokeLater(() -> {
        fireChange(thrown);
      });
    }
  }

  /**
   * Fires an exception to listeners that have been registered to track errors of
   * {@code MidiPlayer}.
   *
   * <p>
   * No event is fired if the given event's old and new values are equal and non-null.
   * </p>
   *
   * <p>
   * If {@link #isNotifyOnEDT} is {@code true} and called off the <i>Event Dispatch Thread</i> this
   * implementation uses {@code SwingUtilities.invokeLater} to send out the notification on the
   * <i>Event Dispatch Thread</i>. This ensures listeners are only ever notified on the <i>Event
   * Dispatch Thread</i>.
   * </p>
   *
   * @param msg Exception message received.
   * @param thrown Exception received.
   */
  public void fireChange(final String msg, final Throwable thrown) {
    if (!isNotifyOnEDT() || SwingUtilities.isEventDispatchThread()) {
      fireExceptionReceived(this.midiPlayerListeners, msg, thrown);
    } else {
      SwingUtilities.invokeLater(() -> {
        fireChange(msg, thrown);
      });
    }
  }

  private static void fireExceptionReceived(List<ThrowableListener> listeners,
      String msg) {
    if (listeners != null) {
      listeners.stream().forEach((listener) -> {
        listener.throwableReceived(msg);
      });
    }
  }

  private static void fireExceptionReceived(List<ThrowableListener> listeners,
      Throwable thrown) {
    if (listeners != null) {
      listeners.stream().forEach((listener) -> {
        listener.throwableReceived(thrown);
      });
    }
  }

  private static void fireExceptionReceived(List<ThrowableListener> listeners,
      String msg, Throwable thrown) {
    if (listeners != null) {
      listeners.stream().forEach((listener) -> {
        listener.throwableReceived(msg, thrown);
      });
    }
  }

  // #########################################################################

  @Override
  /**
   * Add a {@code TableModelListener} to the listener list.
   *
   * <p>
   * The same listener object may be added more than once, and will be called as many times as it is
   * added.
   * </p>
   *
   * <p>
   * If {@code listener} is {@code null}, no exception is thrown and no action is taken.
   * </p>
   *
   * @param listener The {@code TableModelListener} to be added
   */
  public void addTableModelListener(TableModelListener listener) {
    tableModelListeners.add(listener);
  }

  @Override
  /**
   * Remove a {@code TableModelListener} from the listener list.
   *
   * <p>
   * This removes a {@code TableModelListener} that was registered.
   * </p>
   *
   * <p>
   * If {@code listener} was added more than once to the same event source, it will be notified one
   * less time after being removed. If {@code listener} is null, or was never added, no exception is
   * thrown and no action is taken.
   * </p>
   *
   * @param listener The {@code TableModelListener} to be removed
   */
  public void removeTableModelListener(TableModelListener listener) {
    tableModelListeners.remove(listener);
  }

  /**
   * Remove all {@code TableModelListener} from the listener list.
   *
   * <p>
   * This removes all {@code TableModelListener} that were registered.
   * </p>
   */
  public synchronized void clearTableModelListener() {
    tableModelListeners.clear();
  }

  /**
   * Returns an unmodifiable {@code List} of all the listeners which have been associated with the
   * {@code MidiPlayer}.
   *
   * @return all of the {@code TableModelListener}s associated with the {@code MidiPlayer}.
   */
  public synchronized List<TableModelListener> getTableModelListeners() {
    return Collections.unmodifiableList(tableModelListeners);
  }

  /**
   * Returns {@code true} if the listener list contains the specified element.
   *
   * <p>
   * More formally, returns {@code true} if and only if this list contains at least one element
   * {@code e} such that {@code (o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))}.
   * </p>
   *
   * @param listener element whose presence in the listener list is to be tested
   * @return {@code true} if this list contains the specified element
   *
   * @see List#contains(java.lang.Object)
   */
  public synchronized boolean containsTableModelListener(
      TableModelListener listener) {
    return tableModelListeners.contains(listener);
  }

  /**
   * Reports a bound table model update to listeners that have been registered to track updates of
   * {@code MidiPlayer}.
   *
   * <p>
   * This is merely a convenience wrapper around the more general
   * {@link #fireChange(TableModelEvent)} method.
   * </p>
   */
  public void fireChange() {
    fireChange(new TableModelEvent(this));
  }

  /**
   * Reports a bound table model update to listeners that have been registered to track updates of
   * {@code MidiPlayer}.
   *
   * <p>
   * This is merely a convenience wrapper around the more general
   * {@link #fireChange(TableModelEvent)} method.
   * </p>
   *
   * @param rowIndex The row of data that has been updated
   */
  public void fireChange(int rowIndex) {
    fireChange(new TableModelEvent(this, rowIndex));
  }

  /**
   * Reports a bound table model update to listeners that have been registered to track updates of
   * {@code MidiPlayer}.
   *
   * <p>
   * This is merely a convenience wrapper around the more general
   * {@link #fireChange(TableModelEvent)} method.
   * </p>
   *
   * @param firstRow The start index of rows of data that have been updated
   * @param lastRow The end index of rows of data that have been updated
   */
  public void fireChange(int firstRow, int lastRow) {
    fireChange(new TableModelEvent(this, firstRow, lastRow));
  }

  /**
   * Reports a bound table model update to listeners that have been registered to track updates of
   * {@code MidiPlayer}.
   *
   * <p>
   * This is merely a convenience wrapper around the more general
   * {@link #fireChange(TableModelEvent)} method.
   * </p>
   *
   * @param firstRow The start index of rows of data that have been updated
   * @param lastRow The end index of rows of data that have been updated
   * @param column The column in which rows of data have been updated
   */
  public void fireChange(int firstRow, int lastRow, int column) {
    fireChange(new TableModelEvent(this, firstRow, lastRow, column));
  }

  /**
   * Fires a table model event to listeners that have been registered to track updates of
   * {@code MidiPlayer}.
   *
   * <p>
   * No event is fired if the given event's old and new values are equal and non-null.
   * </p>
   *
   * <p>
   * If {@link #isNotifyOnEDT} is {@code true} and called off the <i>Event Dispatch Thread</i> this
   * implementation uses {@code SwingUtilities.invokeLater} to send out the notification on the
   * <i>Event Dispatch Thread</i>. This ensures listeners are only ever notified on the <i>Event
   * Dispatch Thread</i>.
   * </p>
   *
   * @param evt the {@code TableModelEvent} to be fired
   *
   * @throws NullPointerException if {@code evt} is {@code null}
   */
  public void fireChange(final TableModelEvent evt) {
    if (evt == null) {
      throw new NullPointerException();
    }
    if (!isNotifyOnEDT() || SwingUtilities.isEventDispatchThread()) {
      fireTableDataChanged(this.tableModelListeners, evt);
    } else {
      SwingUtilities.invokeLater(() -> {
        fireChange(evt);
      });
    }
  }

  /**
   * Alert listeners that have been registered to track updates of {@code MidiPlayer}.
   *
   * <p>
   * No event is given to the listeners in this method.
   * </p>
   *
   * <p>
   * If {@link #isNotifyOnEDT} is {@code true} and called off the <i>Event Dispatch Thread</i> this
   * implementation uses {@code SwingUtilities.invokeLater} to send out the notification on the
   * <i>Event Dispatch Thread</i>. This ensures listeners are only ever notified on the <i>Event
   * Dispatch Thread</i>.
   * </p>
   */
  public void fireTableDataChanged() {
    if (!isNotifyOnEDT() || SwingUtilities.isEventDispatchThread()) {
      fireTableDataChanged(this.tableModelListeners);
    } else {
      SwingUtilities.invokeLater(() -> {
        fireTableDataChanged();
      });
    }
  }

  private static void fireTableDataChanged(List<TableModelListener> listeners,
      TableModelEvent event) {
    if (listeners != null) {
      listeners.stream().forEach((listener) -> {
        listener.tableChanged(event);
      });
    }
  }

  private static void fireTableDataChanged(List<TableModelListener> listeners) {
    if (listeners != null) {
      listeners.stream().forEach((listener) -> {
        listener.tableChanged(null);
      });
    }
  }

  // #########################################################################
  /**
   * Add a {@code PropertyChangeListener} to the listener list.
   *
   * <p>
   * The same listener object may be added more than once, and will be called as many times as it is
   * added.
   * </p>
   *
   * <p>
   * If {@code listener} is {@code null}, no exception is thrown and no action is taken.
   * </p>
   *
   * @param listener The {@code PropertyChangeListener} to be added
   *
   * @return {@code true} (as specified by {@link Collection#add})
   */
  public synchronized boolean addPropertyChangeListener(
      PropertyChangeListener listener) {
    return propertyChangeListeners.add(listener);
  }

  /**
   * Remove a {@code PropertyChangeListener} from the listener list.
   *
   * <p>
   * This removes a {@code PropertyChangeListener} that was registered.
   * </p>
   *
   * <p>
   * If {@code listener} was added more than once to the same event source, it will be notified one
   * less time after being removed. If {@code listener} is null, or was never added, no exception is
   * thrown and no action is taken.
   * </p>
   *
   * @param listener The {@code PropertyChangeListener} to be removed
   *
   * @return {@code true} if this {@code MidiPlayer} contained the {@code PropertyChangeListener}
   */
  public synchronized boolean removePropertyChangeListener(
      PropertyChangeListener listener) {
    return propertyChangeListeners.remove(listener);
  }

  /**
   * Remove all {@code PropertyChangeListener} from the listener list.
   *
   * <p>
   * This removes all {@code PropertyChangeListener} that were registered.
   * </p>
   */
  public synchronized void clearPropertyChangeListener() {
    propertyChangeListeners.clear();
  }

  /**
   * Returns an unmodifiable {@code List} of all the listeners which have been associated with the
   * {@code MidiPlayer}.
   *
   * @return all of the {@code PropertyChangeListener}s associated with the {@code MidiPlayer}.
   */
  public synchronized List<PropertyChangeListener> getPropertyChangeListeners() {
    return Collections.unmodifiableList(propertyChangeListeners);
  }

  /**
   * Returns {@code true} if the listener list contains the specified element.
   *
   * <p>
   * More formally, returns {@code true} if and only if this list contains at least one element
   * {@code e} such that {@code (o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))}.
   * </p>
   *
   * @param listener element whose presence in the listener list is to be tested
   * @return {@code true} if this list contains the specified element
   *
   * @see List#contains(java.lang.Object)
   */
  public synchronized boolean containsPropertyChangeListener(
      PropertyChangeListener listener) {
    return propertyChangeListeners.contains(listener);
  }

  /**
   * Reports a bound property update to listeners that have been registered to track updates of
   * {@code MidiPlayer}.
   *
   * <p>
   * No event is fired if old and new values are equal and non-null.
   * </p>
   *
   * <p>
   * This is merely a convenience wrapper around the more general
   * {@link #fireChange(PropertyChangeEvent)} method.
   * </p>
   *
   * @param propertyName the programmatic name of the property that was changed
   * @param oldValue the old value of the property
   * @param newValue the new value of the property
   */
  public void fireChange(String propertyName, Object oldValue,
      Object newValue) {
    if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
      return;
    }
    fireChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
  }

  /**
   * Fires a property change event to listeners that have been registered to track updates of
   * {@code MidiPlayer}.
   *
   * <p>
   * No event is fired if the given event's old and new values are equal and non-null.
   * </p>
   *
   * <p>
   * If {@link #isNotifyOnEDT} is {@code true} and called off the <i>Event Dispatch Thread</i> this
   * implementation uses {@code SwingUtilities.invokeLater} to send out the notification on the
   * <i>Event Dispatch Thread</i>. This ensures listeners are only ever notified on the <i>Event
   * Dispatch Thread</i>.
   * </p>
   *
   * @param evt the {@code PropertyChangeEvent} to be fired
   *
   * @throws NullPointerException if {@code evt} is {@code null}
   */
  public void fireChange(final PropertyChangeEvent evt) {
    if (evt == null) {
      throw new NullPointerException();
    }
    if (!isNotifyOnEDT() || SwingUtilities.isEventDispatchThread()) {
      Object oldValue = evt.getOldValue();
      Object newValue = evt.getNewValue();
      if (oldValue == null || newValue == null || !oldValue.equals(newValue)) {
        firePropertyChange(this.propertyChangeListeners, evt);
      }
    } else {
      SwingUtilities.invokeLater(() -> {
        fireChange(evt);
      });
    }
  }

  /**
   * Alert listeners that have been registered to track updates of {@code MidiPlayer}.
   *
   * <p>
   * No event is given to the listeners in this method.
   * </p>
   *
   * <p>
   * If {@link #isNotifyOnEDT} is {@code true} and called off the <i>Event Dispatch Thread</i> this
   * implementation uses {@code SwingUtilities.invokeLater} to send out the notification on the
   * <i>Event Dispatch Thread</i>. This ensures listeners are only ever notified on the <i>Event
   * Dispatch Thread</i>.
   * </p>
   */
  public void firePropertyChange() {
    if (!isNotifyOnEDT() || SwingUtilities.isEventDispatchThread()) {
      firePropertyChange(this.propertyChangeListeners);
    } else {
      SwingUtilities.invokeLater(() -> {
        firePropertyChange();
      });
    }
  }

  private static void firePropertyChange(List<PropertyChangeListener> listeners,
      PropertyChangeEvent event) {
    if (listeners != null) {
      listeners.stream().forEach((listener) -> {
        listener.propertyChange(event);
      });
    }
  }

  private static void firePropertyChange(
      List<PropertyChangeListener> listeners) {
    if (listeners != null) {
      listeners.stream().forEach((listener) -> {
        listener.propertyChange(null);
      });
    }
  }

  // #########################################################################
  @Override
  public void localeChanged() {
    this.localeChanged(null);
  }

  @Override
  public void localeChanged(PropertyChangeEvent evt) {
    fireChange(TableModelEvent.HEADER_ROW);
  }

}
