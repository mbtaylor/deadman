package uk.ac.bristol.star.deadman;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

/**
 * Sends mail.
 * This convenience class abstracts use of the Javamail classes
 * (part of J2EE or available separately).
 * Developed against javamail-1.5.5, but probably any version will do.
 *
 * @author   Mark Taylor
 * @since    28 Jun 2016
 */
public class Mailer {

    private final String smtpServer_;
    private final String sender_;
    private final String[] recipients_;
    private final String subjectPrefix_;
    private final Properties props_;

    private static final Logger logger_ =
        Logger.getLogger( Mailer.class.getName() );
    
    /**
     * Constructor.
     *
     * @param  smtpServer   SMTP server
     * @param  sender   sender's email address
     * @param  recipients   one or more email addresses To which
     *                      emails will be sent
     * @param  subjectPrefix  string prefixed to topic string to make
     *                        Subject line (may be null)
     */
    public Mailer( String smtpServer, String sender, String[] recipients,
                   String subjectPrefix ) {
        smtpServer_ = smtpServer;
        sender_ = sender;
        recipients_ = recipients;
        subjectPrefix_ = subjectPrefix == null ? "" : subjectPrefix;
        props_ = new Properties();
        props_.put( "mail.smtp.host", smtpServer );
    }

    /**
     * Sends an email.
     * May throw an exception, though delivery failures may end up just
     * getting returned to the sender.
     *
     * @param  topic   short summary of message (included in Subject line)
     * @param  body    content of email
     */
    public void sendMessage( String topic, String body )
            throws MessagingException {
        MimeMessage msg = new MimeMessage( Session.getInstance( props_ ) );
        msg.setFrom( sender_ );
        for ( String recipient : recipients_ ) {
            msg.setRecipients( Message.RecipientType.TO, recipient );
        }
        msg.setSentDate( new Date() );
        msg.setSubject( subjectPrefix_ + topic );
        msg.setText( body );
        Transport.send( msg );
    }

    /**
     * Sends a message asynchronously.  Returns immediately without error.
     * Success or failure is logged through the logging system.
     *
     * @param  topic   short summary of message (included in Subject line)
     * @param  body    content of email
     */
    public void scheduleSendMessage( final String topic, final String body ) {
        new Thread( "Mailer" ) {
            public void run() {
                try { 
                    sendMessage( topic, body );
                    logger_.info( "Sent email: " + topic );
                }
                catch ( MessagingException e ) {
                    logger_.log( Level.SEVERE,
                                 "Failed to send email: " + topic, e );
                }
            }
        }.start();
    }

    public static void main( String[] args ) throws MessagingException {
        String[] recipients = new String[] { args[ 0 ] };
        ConfigMap cmap = new ConfigMap();
        Mailer mailer = new Mailer( cmap.get( Config.SMTP_SERVER ),
                                    cmap.get( Config.SMTP_SENDER ),
                                    recipients, "[mailer] " );
        mailer.sendMessage( "Test", "It's a test.\n" );
    }
}
