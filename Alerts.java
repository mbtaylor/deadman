
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Alerts {

    public static final String WARNING_WAV = "onscreen.wav";
    public static final String DANGER_WAV = "redalert.wav";
    private static final Logger logger_ =
        Logger.getLogger( Alerts.class.getName() );

    public static Alert createSirenAlert() throws IOException {
        Class clazz = Alert.class;
        Map<Status,SoundAlert.Sound> soundMap =
            new HashMap<Status,SoundAlert.Sound>();
        soundMap.put( Status.WARNING,
                      new SoundAlert.Sound( getResource( WARNING_WAV ),
                                            2000 ) );
        soundMap.put( Status.DANGER,
                      new SoundAlert.Sound( getResource( DANGER_WAV ) ) );
        return new SoundAlert( soundMap );
    }

    public static Alert createLoggingAlert() {
        return new Alert() {
            Status currentStatus_;
            public void setStatus( Status status ) {
                if ( status != currentStatus_ ) {
                    logger_.info( "Status " + status );
                    currentStatus_ = status;
                }
            }
        };
    }

    public static Alert createEmailAlert( String[] recipients ) {
        final Mailer mailer =
            new Mailer( Mailer.SMTP_HOST, Mailer.SENDER, recipients );
        return new Alert() {
            Status currentStatus_;
            public void setStatus( Status status ) {
                if ( status != currentStatus_ ) {
                    String atTime = " at " + new Date() + ".\n";
                    if ( status == Status.DANGER ) {
                        send( "ALARM",
                              "Deadman danger status triggered" + atTime );
                    }
                    else if ( status == null &&
                              currentStatus_ == Status.DANGER ) {
                        send( "reset",
                              "Deadman status reset to safe" + atTime );
                    }
                    currentStatus_ = status;
                }
            }
            private void send( String word, final String msgTxt ) {
                final String subject = "Message from deadman (" + word + ")";
                new Thread() {
                    public void run() {
                        try {
                            mailer.sendMessage( subject, msgTxt );
                            logger_.info( "Sent alert email" );
                        }
                        catch ( Throwable e ) {
                            logger_.log( Level.SEVERE,
                                         "Failed to send email", e );
                        }
                    }
                }.start();
            }
        };
    }

    public static Alert createMultiAlert( List<Alert> alertList ) {
        return createMultiAlert( alertList.toArray( new Alert[ 0 ] ) );
    }

    public static Alert createMultiAlert( final Alert... alerts ) {
        return new Alert() {
            public void setStatus( Status status ) {
                for ( Alert a : alerts ) {
                    a.setStatus( status );
                }
            }
        };
    }

    private static URL getResource( String relUrl ) throws IOException {
        URL url = Alerts.class.getResource( relUrl );
        if ( url == null ) {
            throw new IOException( "No such resource " + relUrl );
        }
        else {
            return url;
        }
    }
}
