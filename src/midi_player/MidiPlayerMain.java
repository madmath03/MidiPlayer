package midi_player;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import midi_player.frame.MidiPlayerFrame;
import midi_player.resources.ResourceUtils;

/**
 *
 */
public class MidiPlayerMain {

    private static final Logger LOGGER = Logger.getLogger(MidiPlayerMain.class.getName());

    private static void setLookAndFeel(String lookAndFeelName) {
        if (lookAndFeelName == null) {
            return;
        }

        /* Set look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if (lookAndFeelName.equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            LOGGER.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Parse arguments
        boolean silentMode = false;
        final List<Path> filesToOpen = new ArrayList<>(args.length);
        if (args.length > 0) {
            // Play MIDI files without GUI?
            int i = 0;
            if ("-s".equalsIgnoreCase(args[i]) || "--silent".equalsIgnoreCase(args[i])) {
                silentMode = true;
                i++;
            }
            for (int n = args.length; i < n; i++) {
                String arg = args[i];
                Path path = Paths.get(arg).toAbsolutePath().normalize();
                filesToOpen.add(path);
            }
        }

        System.out.println("Starting MIDI player");
        if (silentMode) {
            MidiPlayer midiPlayer = MidiPlayer.getInstance();
            if (midiPlayer == null || midiPlayer.getSequencer() == null) {
                LOGGER.severe("No MIDI sequencer available!!");
                System.exit(1);
            }

            // Add files to the player and start playing
            midiPlayer.startPlaying(filesToOpen);
        } else {
            /* Set the Nimbus look and feel */
            setLookAndFeel("Nimbus");

            /* Create and display the form */
            java.awt.EventQueue.invokeLater(() -> {
                final MidiPlayerFrame appFrame = new MidiPlayerFrame("MIDI Player", filesToOpen);
                
                // Register frame for locale change
                ResourceUtils.addLocaleChangeListener(appFrame);
                
                // Make frame visible
                appFrame.setVisible(true);
            });
        }

    }

}
