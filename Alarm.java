
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Timer;

public abstract class Alarm {

    public static final String ALARM_WAV = "redalert.wav";
    private static final Logger logger_ =
        Logger.getLogger( Alarm.class.getName() );

    public abstract void start();
    public abstract void stop();

    public static Alarm createAlarm() {
        URL url = Alarm.class.getResource( ALARM_WAV );
        if ( url != null ) {
            try {
                return createClipAlarm( url );
            }
            catch ( Exception e ) {
                logger_.log( Level.WARNING, "Can't load alarm", e );
            }
        }
        else {
            logger_.warning( "Can't find alarm sound " + ALARM_WAV );
        }
        return createBeepAlarm();
    }

    public static Alarm createClipAlarm( URL url )
            throws IOException, UnsupportedAudioFileException,
                   LineUnavailableException {
        final Clip clip = AudioSystem.getClip();
        clip.open( AudioSystem.getAudioInputStream( url ) );
        return new Alarm() {
            public void start() {
                clip.loop( Clip.LOOP_CONTINUOUSLY );
            }
            public void stop() {
                clip.stop();
            }
        };
    }

    public static Alarm createBeepAlarm() {
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Timer timer = new Timer( 1500, new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                toolkit.beep();
            }
        } );
        return new Alarm() {
            public void start() {
                timer.start();
            }
            public void stop() {
                timer.stop();
            }
        };
    }
}
