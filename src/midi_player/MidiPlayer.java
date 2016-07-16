package midi_player;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

/**
 * A MIDI player.
 *
 * @see
 * http://www.java2s.com/Code/Java/Development-Class/AnexamplethatplaysaMidisequence.htm
 * @see
 * https://www.midi.org/specifications/category/complete-midi-1-0-detailed-specification
 */
public class MidiPlayer implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(MidiPlayer.class.getName());

    /**
     * Midi meta event for end of track.
     */
    protected static final int END_OF_TRACK_MESSAGE = 47;

    private static MidiPlayer instance = null;

    public static MidiPlayer getInstance() {
        if (instance == null) {
            instance = new MidiPlayer();
        }
        return instance;
    }

    // #########################################################################
    protected static void printSongInfo(Path songPath, Sequence song) {
        String songInfo = getSongInfo(songPath, song);
        System.out.println(songInfo);
    }

    public static String getSongInfo(Path songPath, Sequence song) {
        // Get file name
        String fileName = songPath.getName(songPath.getNameCount() - 1).toString();
        // Get song length
        long ms = song.getMicrosecondLength() / 1000;
        long min = ms / 60000;
        long s = (ms / 1000) - (min * 60);

        return String.format("%s (%d:%02d)", fileName, min, s);
    }

    // #########################################################################
    private final LinkedList<Path> playlist = new LinkedList<>();
    private transient Sequencer sequencer = null;
    private int currentSongIndex = 0;
    private transient Path currentSongPath = null;
    private transient MidiThread playingThread = null;

    private boolean looping = false;

    protected MidiPlayer() {
    }

    // #########################################################################
    public Sequencer getSequencer() {
        if (sequencer == null) {
            // Retrieve MIDI Sequencer
            try {
                sequencer = MidiSystem.getSequencer();
                System.out.println("MIDI Sequencer information:");
                System.out.println("\t" + sequencer.getDeviceInfo().getName());
                System.out.println("\t" + sequencer.getDeviceInfo().getDescription());
                System.out.println("\t" + sequencer.getDeviceInfo().getVendor());
                System.out.println("\t" + sequencer.getDeviceInfo().getVersion());
                System.out.println("\t" + sequencer.getDeviceInfo().getClass());

                // Add listener to detect end of song
                sequencer.addMetaEventListener((MetaMessage meta) -> {
                    switch (meta.getType()) {
                        case MidiPlayer.END_OF_TRACK_MESSAGE:
                            // Notify player to force move to next song
                            MidiPlayer.this.moveToNextSong(true);
                            break;
                            
                    }
                });

                // Add listener to detect end of song
                sequencer.addMetaEventListener((MetaMessage meta) -> {
                    switch (meta.getType()) {
                        case MidiPlayer.END_OF_TRACK_MESSAGE:
                            // Notify player to force move to next song
                            MidiPlayer.this.moveToNextSong(true);
                            break;
                            
                    }
                });

                // Opens the device, indicating that it should now acquire any
                // system resources it requires and become operational.
                sequencer.open();
                System.out.println("MIDI Sequencer opened and ready for usage.");

                setupLoopCount();

            } catch (MidiUnavailableException ex) {
                if (sequencer != null) {
                    sequencer.close();
                    sequencer = null;
                }
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

        return sequencer;
    }

    protected void resetSequencer() {
        this.sequencer = null;
    }

    public List<Path> getPlaylist() {
        return Collections.unmodifiableList(playlist);
    }

    protected MidiThread getPlayingThread() {
        return playingThread;
    }

    public int getCurrentSongIndex() {
        return currentSongIndex;
    }

    protected void setCurrentSongIndex(int currentSongIndex) {
        this.currentSongIndex = currentSongIndex;
    }

    public boolean isLooping() {
        return looping;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;

        if (this.sequencer != null) {
            setupLoopCount();
        }
    }

    private void setupLoopCount() {
        if (this.looping) {
            // Loop until interrupted
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
        } else {
            // repeat 0 times (play once)
            sequencer.setLoopCount(0);
        }
    }

    // #########################################################################
    public synchronized boolean startPlaying() {
        // If nothing to play, quit
        if (this.playlist == null || this.playlist.isEmpty()) {
            return false;
        }

        // Retrieve MIDI Sequencer
        Sequencer player = this.getSequencer();
        // If no MIDI Sequencer, quit
        if (player == null) {
            return false;
        }

        // If there already is a thread playing songs
        if (playingThread != null && playingThread.isAlive() && player.isOpen()) {
            // Set sequencer at the start of the song
            player.setMicrosecondPosition(0);
            // If the sequencer is not playing, replay current song
            if (!player.isRunning() && player.getSequence() != null) {
                player.start();
            }
            return true;
        }

        // Create a thread to play songs
        this.playingThread = createNewMidiThread();
        this.playingThread.start();

        return true;
    }

    protected synchronized MidiThread createNewMidiThread() {
        return this.new MidiThread("MidiPlayerThread");
    }

    public boolean startPlaying(int index) {
        if (this.moveToSong(index)) {
            return this.startPlaying();
        } else {
            return false;
        }
    }

    public boolean startPlaying(Path path) {
        if (this.moveToSong(path)) {
            return this.startPlaying();
        } else {
            return false;
        }
    }

    public boolean startPlaying(Collection<? extends Path> paths) {
        if (this.addAll(paths)) {
            return this.startPlaying();
        } else {
            return false;
        }
    }

    public synchronized boolean pausePlaying() {
        // If no MIDI Sequencer, quit
        if (this.sequencer == null) {
            return false;
        }
        // Retrieve MIDI Sequencer
        Sequencer player = this.getSequencer();

        // If there is a thread playing songs
        if (playingThread != null && playingThread.isAlive() && player.isOpen()) {
            // If the sequencer is playing, stop current song
            if (player.isRunning()) {
                player.stop();
            } else if (player.getSequence() != null) {
                // If it is paused, restart playing where it paused
                player.start();
            }
        }

        return true;
    }

    public synchronized boolean stopPlaying() {
        // If no MIDI Sequencer, quit
        if (this.sequencer == null) {
            return false;
        }
        // Retrieve MIDI Sequencer
        Sequencer player = this.getSequencer();

        // If there is a thread playing songs
        if (playingThread != null && playingThread.isAlive() && player.isOpen()) {
            // If the sequencer is playing, stop current song
            player.stop();
            player.close();

            this.resetSequencer();
            this.notifyAll();
        }

        return true;
    }

    public synchronized boolean moveToPreviousSong() {
        // Decrement position by 1 in playlist
        if (this.currentSongIndex > 0) {
            this.setCurrentSongIndex(currentSongIndex - 1);
            this.notifyAll();
            return true;
        }
        return false;
    }

    public boolean moveToNextSong() {
        return moveToNextSong(false);
    }

    protected synchronized boolean moveToNextSong(boolean force) {
        boolean moved = false;
        // Increment position by 1 in playlist (if still inside playlist)
        if (force || this.currentSongIndex < this.playlist.size() - 1) {
            this.setCurrentSongIndex(currentSongIndex + 1);
            moved = true;
        }
        this.notifyAll();
        return moved;
    }

    public synchronized boolean moveToSong(int index) {
        // Set position to index in playlist (if still inside playlist)
        if (index >= 0 && index < this.playlist.size()) {
            this.setCurrentSongIndex(index);
            this.notifyAll();
            return true;
        }
        return false;
    }

    public boolean moveToSong(Path path) {
        return moveToSong(this.playlist.indexOf(path));
    }

    public boolean moveSongsTo(int start, int end, int to) {
        if (start > end) {
            throw new IllegalArgumentException("Start (" + start + ") must be lesser or equal to end (" + end + ") index.");
        }
        if (to >= start && to <= end) {
            return false;
        }

        // Rotation algorithm
        int distance = (1 + end - start);
        int rotationStart, rotationEnd;
        if (start >= to) {
            rotationStart = to;
            rotationEnd = end + 1;
        } else {
            distance *= -1;
            rotationStart = start;
            rotationEnd = to;
        }
        List<Path> subList = this.playlist.subList(rotationStart, rotationEnd);
        Collections.rotate(subList, distance);

        // Update current song index
        if (this.currentSongIndex >= rotationStart && this.currentSongIndex <= rotationEnd) {
            int offset;
            if (this.currentSongIndex >= start && this.currentSongIndex <= end) {
                offset = (rotationEnd - rotationStart - Math.abs(distance)) * (this.currentSongIndex >= to ? -1 : 1);
            } else {
                offset = distance;
            }
            this.setCurrentSongIndex(this.currentSongIndex + offset);
        }

        return true;
    }

    public boolean shufflePlaylist() {
        if (playlist == null || playlist.isEmpty()) {
            return false;
        }
        Path currentSong = getCurrentSongPath();
        Collections.shuffle(playlist);
        if (currentSong != null) {
            setCurrentSongIndex(playlist.indexOf(currentSong));
        }
        return true;
    }

    public boolean shufflePlaylist(Random random) {
        if (playlist == null || playlist.isEmpty()) {
            return false;
        }
        Path currentSong = getCurrentSongPath();
        Collections.shuffle(playlist, random);
        if (currentSong != null) {
            setCurrentSongIndex(playlist.indexOf(currentSong));
        }
        return true;
    }

    public boolean sortPlaylist() {
        if (playlist == null || playlist.isEmpty()) {
            return false;
        }
        Path currentSong = getCurrentSongPath();
        Collections.sort(playlist);
        if (currentSong != null) {
            setCurrentSongIndex(playlist.indexOf(currentSong));
        }
        return true;
    }

    public boolean sortPlaylist(Comparator<Path> comparator) {
        if (playlist == null || playlist.isEmpty()) {
            return false;
        }
        Path currentSong = getCurrentSongPath();
        Collections.sort(playlist, comparator);
        if (currentSong != null) {
            setCurrentSongIndex(playlist.indexOf(currentSong));
        }
        return true;
    }

    // #########################################################################
    public boolean isPlaying() {
        // If no MIDI Sequencer, quit
        if (this.sequencer == null) {
            return false;
        }
        // Retrieve MIDI Sequencer
        Sequencer player = this.getSequencer();

        return playingThread != null
                && playingThread.isAlive()
                && player.getSequence() != null
                && player.isRunning();
    }

    public boolean isPaused() {
        // If no MIDI Sequencer, quit
        if (this.sequencer == null) {
            return false;
        }
        // Retrieve MIDI Sequencer
        Sequencer player = this.getSequencer();

        return playingThread != null
                && playingThread.isAlive()
                && player.isOpen()
                && player.getSequence() != null
                && !player.isRunning();
    }

    public boolean isStopped() {
        // If no MIDI Sequencer, quit
        if (this.sequencer == null || playingThread == null) {
            return true;
        }
        // Retrieve MIDI Sequencer
        Sequencer player = this.getSequencer();

        return !playingThread.isAlive() && !player.isOpen();
    }

    protected Path getCurrentSongPath() {
        if (this.currentSongIndex < 0
                || this.currentSongIndex >= this.playlist.size()) {
            return null;
        }
        this.currentSongPath = this.playlist.get(this.currentSongIndex);
        return this.currentSongPath;
    }

    public boolean isCurrentSong(Path path) {
        Path currentPath = getCurrentSongPath();
        return currentPath != null ? currentPath.equals(path) : false;
    }

    protected Sequence getCurrentSong() {
        if (isStopped()) {
            return null;
        }

        return getSequencer().getSequence();
    }

    public void printCurrentSongInfo() {
        Sequence song = getCurrentSong();
        Path songPath = getCurrentSongPath();
        if (songPath == null || song == null) {
            return;
        }

        printSongInfo(songPath, song);
    }

    public String getCurrentSongInfo() {
        Sequence song = getCurrentSong();
        Path songPath = getCurrentSongPath();
        if (songPath == null || song == null) {
            return null;
        }

        return getSongInfo(songPath, song);
    }

    // #########################################################################
    public boolean isEmpty() {
        return this.playlist.isEmpty();
    }

    public int size() {
        return this.playlist.size();
    }

    public boolean add(Path path) {
        if (path == null) {
            return false;
        }
        boolean added = this.playlist.add(path);
        return added;
    }

    public boolean add(int index, Path path) {
        if (path == null) {
            return false;
        }
        this.playlist.add(index, path);
        return true;
    }

    public boolean addAll(Collection<? extends Path> paths) {
        if (paths == null || paths.isEmpty()) {
            return false;
        }
        boolean added = this.playlist.addAll(paths);
        return added;
    }

    public boolean addAll(int index, Collection<? extends Path> paths) {
        if (paths == null || paths.isEmpty()) {
            return false;
        }
        boolean added = this.playlist.addAll(index, paths);
        return added;
    }

    private boolean removeSongAtIndex(int index) {
        if (index < 0 || index >= this.playlist.size()) {
            return false;
        }
        Path removedPath = this.playlist.remove(index);
        boolean removed = removedPath != null;
        afterRemove(index);
        return removed;
    }

    public boolean remove(int index) {
        return removeSongAtIndex(index);
    }

    protected void afterRemove(int removedIndex) {
        if (this.isPlaying() && this.currentSongIndex == removedIndex) {
            this.stopPlaying();
        } else if (removedIndex < this.currentSongIndex) {
            this.setCurrentSongIndex(this.currentSongIndex - 1);
        }
    }

    protected boolean remove(Path path) {
        if (path == null) {
            return false;
        }
        boolean removed = this.playlist.remove(path);
        afterRemove(path);
        return removed;
    }

    protected void afterRemove(Path removedPath) {
        Path songPath = getCurrentSongPath();
        if (this.isPlaying() && songPath == removedPath) {
            this.stopPlaying();
        } else {
            this.setCurrentSongIndex(this.playlist.indexOf(songPath));
        }
    }

    public boolean removeAll(int[] indexes) {
        if (indexes == null || indexes.length < 0) {
            return false;
        }

        // Create a copy of array
        int[] sortedIndexes = Arrays.copyOf(indexes, indexes.length);
        // Sort it in ascending order
        Arrays.sort(sortedIndexes);
        // Then reverse array
        for (int i = 0, n = sortedIndexes.length / 2; i < n; i++) {
            int temp = sortedIndexes[i];
            sortedIndexes[i] = sortedIndexes[sortedIndexes.length - i - 1];
            sortedIndexes[sortedIndexes.length - i - 1] = temp;
        }

        boolean removed = true;
        for (int index : sortedIndexes) {
            removed &= removeSongAtIndex(index);
        }
        return removed;
    }

    protected boolean removeAll(Collection<? extends Path> paths) {
        if (paths == null || paths.isEmpty()) {
            return false;
        }
        boolean removed = this.playlist.removeAll(paths);
        afterRemoveAll(paths);
        return removed;
    }

    protected void afterRemoveAll(Collection<? extends Path> paths) {
        Path songPath = getCurrentSongPath();
        if (this.isPlaying() && paths.contains(songPath)) {
            this.stopPlaying();
        } else {
            this.setCurrentSongIndex(this.playlist.indexOf(songPath));
        }
    }

    public boolean clear() {
        this.playlist.clear();
        afterClear();
        return true;
    }

    protected void afterClear() {
        if (this.isPlaying()) {
            this.stopPlaying();
        }
    }

    // #########################################################################
    protected void reportThrowable(String msg, Throwable thrown) {
    }

    // #########################################################################
    @Override
    public void close() throws Exception {
        System.out.println("Closing allocated resources");
        if (playingThread != null) {
            playingThread.interrupt();
            playingThread = null;
        }
        if (sequencer != null) {
            sequencer.stop();
            sequencer.close();
        }
    }

    // #########################################################################
    public class MidiThread extends Thread {

        private transient Path currentPath = null;

        public MidiThread() {
        }

        public MidiThread(String name) {
            super(name);
        }

        public Path getCurrentPath() {
            return currentPath;
        }
        
        private void playSequence(URL url) {
            try {
                // Sets the current sequence on which the MidiPlayer operates.
                // The stream must point to MIDI file data.
                Sequence song = MidiSystem.getSequence(url);
                playSequence(song);
            } catch (IOException | InvalidMidiDataException ex) {
                MidiPlayer.this.stopPlaying();
                MidiPlayer.LOGGER.log(Level.SEVERE, "Impossible to read URL: "+url, ex);
                MidiPlayer.this.reportThrowable("Impossible to read URL: "+url, ex);
            }
        }
        
        private void playSequence(Path path) {
            try (InputStream is = new BufferedInputStream(new FileInputStream(path.toFile()))) {
                // Sets the current sequence on which the MidiPlayer operates.
                // The stream must point to MIDI file data.
                Sequence song = MidiSystem.getSequence(is);
                playSequence(song);
            } catch (IOException | InvalidMidiDataException ex) {
                MidiPlayer.this.stopPlaying();
                MidiPlayer.LOGGER.log(Level.SEVERE, "Impossible to read file: "+path, ex);
                MidiPlayer.this.reportThrowable("Impossible to read file: "+path, ex);
            }
        }
        
        private void playSequence(Sequence song) throws IOException, InvalidMidiDataException {
            sequencer.setSequence(song);

            printSongInfo(currentPath, song);

            // Starts playback of the MIDI data in the currently loaded sequence.
            sequencer.start();
        }

        @Override
        public void run() {
            synchronized (MidiPlayer.this) {
                try {
                    // Retrieve or create a MIDI sequencer
                    Sequencer sequencer = MidiPlayer.this.getSequencer();
                    while (sequencer != null && sequencer.isOpen()) {
                        // Get current song to play
                        this.currentPath = MidiPlayer.this.getCurrentSongPath();
                        if (this.currentPath == null) {
                            System.out.println("No songs to play. Stopping thread and player...");
                            return;
                        }

                        playSequence(this.currentPath);

                        // Wait until the song ends and must be changed
                        MidiPlayer.this.wait();
                    }
                } catch (InterruptedException ex) {
                    MidiPlayer.LOGGER.log(Level.INFO, null, ex);
                } finally {
                    // Whatever happens, make sure to close resources
                    System.out.println("Closing player");
                    MidiPlayer.this.stopPlaying();
                }
            }
        }
    }

}
