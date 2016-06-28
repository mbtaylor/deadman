
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundAlert implements Alert {

    private static final Logger logger_ =
        Logger.getLogger( Alert.class.getName() );

    private final Map<Status,Sound> soundMap_;
    private final Clip clip_;
    private Sound currentSound_;

    public SoundAlert( Map<Status,Sound> soundMap ) throws IOException {
        soundMap_ = soundMap;
        try {
            clip_ = AudioSystem.getClip();
        }
        catch ( LineUnavailableException e ) {
            throw new IOException( "Audio trouble", e );
        }
    }

    public synchronized void setStatus( Status status ) {
        Sound sound = soundMap_.get( status );
        if ( sound != currentSound_ ) {
            if ( currentSound_ != null ) {
                currentSound_.stop( clip_ );
            }
            currentSound_ = sound;
            if ( currentSound_ != null ) {
                try {
                    currentSound_.start( clip_ );
                }
                catch ( LineUnavailableException e ) {
                    logger_.log( Level.SEVERE,
                                 "Cannot sound audio alarm!", e );
                }
            }
        }
    }

    public static class Sound {
        private final AudioFormat format_;
        private final byte[] buf_;
        private final int intervalMillis_;
        private Timer timer_;
        public Sound( URL url ) throws IOException {
            this( url, 0 );
        }
        public Sound( URL url, int intervalMillis ) throws IOException {
            intervalMillis_ = intervalMillis;
            AudioInputStream audioIn;
            try {
                audioIn = AudioSystem.getAudioInputStream( url );
            }
            catch ( UnsupportedAudioFileException e ) {
                throw new IOException( "Unknown audio file type", e );
            }
            format_ = audioIn.getFormat();
            ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
            InputStream bufIn = new BufferedInputStream( audioIn );
            for ( int c; ( c = bufIn.read() ) >= 0; ) {
                bufOut.write( c );
            }
            bufOut.close();
            buf_ = bufOut.toByteArray();
        }
        synchronized void start( final Clip clip )
                throws LineUnavailableException {
            if ( clip.isOpen() ) {
                clip.close();
            }
            clip.open( format_, buf_, 0, buf_.length );
            long periodMillis = 
                  (long) Math.ceil( clip.getMicrosecondLength() * 0.001 )
                + intervalMillis_;
            TimerTask task = new TimerTask() {
                public void run() {
                    clip.setFramePosition( 0 );
                    clip.start();
                }
            };
            timer_ = new Timer( "sounder", true );
            timer_.scheduleAtFixedRate( task, 0, periodMillis );
        }
        synchronized void stop( Clip clip ) {
            clip.stop();
            clip.close();
            if ( timer_ != null ) {
                timer_.cancel();
                timer_ = null;
            }
        }
    }

    /** Plays an audio file from a URL. */
    public static void main( String[] args ) throws Exception {
        URL url = new URL( args[ 0 ] );
        Sound sound = new Sound( new URL( args[ 0 ] ), 1000 );
        Clip clip = AudioSystem.getClip();
        sound.start( clip );
        Thread.sleep( 5000 );
        sound.stop( clip );
    }
}
