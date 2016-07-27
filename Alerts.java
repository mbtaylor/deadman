package uk.ac.bristol.star.deadman;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Utility class providing some Alert implementations.
 *
 * @author   Mark Taylor
 * @since    28 Jun 2016
 */
public class Alerts {

    /** Audio file for Status.WARNING siren. */
    public static final String WARNING_WAV = "onscreen.wav";

    /** Audio file for Status.DANGER siren. */
    public static final String DANGER_WAV = "redalert.wav";

    private static final Logger logger_ =
        Logger.getLogger( Alerts.class.getName() );

    /**
     * Returns an alert that sounds audible sirens.
     *
     * @return  audio alert
     */
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

    /**
     * Returns an alert that reports status changes through the logging system.
     *
     * @return  logger alert
     */
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

    /**
     * Returns an alert that sends emails to indicated recipients
     * when status transitions to DANGER or back again.
     *
     * @param  mailer  mailer instance
     * @return   email alert
     */
    public static Alert createEmailAlert( final Mailer mailer ) {
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
                        send( "Reset",
                              "Deadman status reset to safe" + atTime );
                    }
                    currentStatus_ = status;
                }
            }

            /**
             * Sends an email message.
             *
             * @param  topic   short summary of message
             *                 (included in Subject line)
             * @param  body    content of email
             */
            private void send( String topic, String body ) {
                mailer.scheduleSendMessage( topic, body );
            }
        };
    }

    /**
     * Returns an alert instance which multiplexes status updates
     * to an array of child alerts.
     *
     * @param  alerts  array of child alerts
     */
    public static Alert createMultiAlert( final Alert... alerts ) {
        return new Alert() {
            public void setStatus( Status status ) {
                for ( Alert a : alerts ) {
                    a.setStatus( status );
                }
            }
        };
    }

    /**
     * Returns a resource URL for a resource stored in the same place
     * as this class.
     *
     * @param   relUrl  relative URL
     * @return  usable URL, not null
     * @throws  IOException  if resource does not exist
     */
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
