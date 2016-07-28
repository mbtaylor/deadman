package uk.ac.bristol.star.deadman;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;

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
    private final Component parent_;

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
     * @param  parent    parent component, if present will be used to signal
     *                   errors using JOptionPane; may be null for no GUI
     */
    public Mailer( String smtpServer, String sender, String[] recipients,
                   String subjectPrefix, Component parent ) {
        smtpServer_ = smtpServer;
        sender_ = sender;
        recipients_ = recipients;
        subjectPrefix_ = subjectPrefix == null ? "" : subjectPrefix;
        parent_ = parent;
        props_ = new Properties();
        props_.put( "mail.smtp.host", smtpServer );
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
                sendMessage( topic, body );
            }
        }.start();
    }

    /**
     * Makes a best effort to send an email synchronously.
     * Returns without error, success or failure is logged through the
     * logging system and as a return value.
     *
     * @param  topic   short summary of message (included in Subject line)
     * @param  body    content of email
     * @return   true  iff send attempt was apparently successful
     */
    public boolean sendMessage( String topic, String body ) {
        try { 
            attemptSendMessage( topic, body );
            int nr = recipients_.length;
            logger_.info( "Sent email to " + nr + " "
                        + ( nr == 1 ? "address" : "addresses" )
                        + ": " + topic );
            return true;
        }
        catch ( MessagingException e ) {
            logger_.log( Level.SEVERE,
                         "Failed to send email: " + topic, e );
            logger_.log( Level.SEVERE,
                         "Failed recipients: "
                       + Arrays.toString( recipients_ ) );
            if ( parent_ != null ) {
                List<String> lines = new ArrayList();
                lines.add( "Failed to send email: " + topic );
                lines.add( " " );
                lines.add( "Intended recipients: " );
                for ( String r : recipients_ ) {
                    lines.add( "   " + r );
                }
                lines.add( " " );
                lines.add( "Error: " + e );
                JOptionPane.showMessageDialog( parent_,
                                               lines.toArray( new String[ 0 ] ),
                                               "Email failure",
                                               JOptionPane.WARNING_MESSAGE );
            }
            return false;
        }
    }

    /**
     * Attempts to send an email synchronously.
     * May throw an exception, though delivery failures may end up just
     * getting returned to the sender.
     *
     * @param  topic   short summary of message (included in Subject line)
     * @param  body    content of email
     */
    public void attemptSendMessage( String topic, String body )
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

    public static void main( String[] args ) throws MessagingException {
        String[] recipients = new String[] { args[ 0 ] };
        ConfigMap cmap = new ConfigMap();
        Mailer mailer = new Mailer( cmap.get( DmConfig.SMTP_SERVER ),
                                    cmap.get( DmConfig.SMTP_SENDER ),
                                    recipients, "[mailer] ", null );
        mailer.attemptSendMessage( "Test", "It's a test.\n" );
    }
}
