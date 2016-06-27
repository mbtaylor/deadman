
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public abstract class Alert {

    public static final String WARNING_WAV = "onscreen.wav";
    public static final String DANGER_WAV = "redalert.wav";

    public abstract void setStatus( Status status );

    public static Alert createAlert() throws IOException {
        Class clazz = Alert.class;
        Map<Status,URL> clipMap = new HashMap<Status,URL>();
        clipMap.put( Status.WARNING, getResource( WARNING_WAV ) );
        clipMap.put( Status.DANGER, getResource( DANGER_WAV ) );
        return new ClipAlert( clipMap );
    }

    private static class ClipAlert extends Alert {

        private final Map<Status,URL> clipMap_;
        private URL currentUrl_;
        private Clip clip_;

        public ClipAlert( Map<Status,URL> clipMap ) throws IOException {

            // I'd rather store the clips here, but if I try to have
            // multiple open (i.e. data-bearing) clips in existence
            // at the same time, I get a LineUnavailableException.
            clipMap_ = clipMap;
            for ( URL url : clipMap.values() ) {
                Clip clip = createClip( url );
                clip.close();
            }
        }

        public synchronized void setStatus( Status status ) {
            URL url = clipMap_.get( status );
            if ( url != currentUrl_ ) {
                if ( clip_ != null ) {
                    clip_.stop();
                    clip_.close();
                }
                currentUrl_ = url;
                try {
                    clip_ = currentUrl_ == null ? null : createClip( url );
                }
                catch ( IOException e ) {
                    throw new Error( "Audio trouble!!", e );
                }
                if ( clip_ != null ) {
                    clip_.loop( Clip.LOOP_CONTINUOUSLY );
                }
            }
        }
    }

    private static URL getResource( String relUrl ) throws IOException {
        URL url = Alert.class.getResource( relUrl );
        if ( url == null ) {
            throw new IOException( "No such resource " + relUrl );
        }
        else {
            return url;
        }
    }

    private static Clip createClip( URL url ) throws IOException {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open( AudioSystem.getAudioInputStream( url ) );
            return clip;
        }
        catch ( UnsupportedAudioFileException e ) {
            throw new IOException( "Unsupported file type for " + url, e );
        }
        catch ( LineUnavailableException e ) {
            throw new IOException( "Audio trouble", e );
        }
    }

    /** Plays an audio file from a URL. */
    public static void main( String[] args ) throws Exception {
        Clip clip = createClip( new URL( args[ 0 ] ) );
        clip.loop( Clip.LOOP_CONTINUOUSLY );
        Thread.sleep( 2000 );
    }
}
