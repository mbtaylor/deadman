package uk.ac.bristol.star.deadman;

import java.util.Date;
import java.util.Properties;
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
    private final Properties props_;
    
    /**
     * Constructor.
     *
     * @param  smtpServer   SMTP server
     * @param  sender   sender's email address
     * @param  recipients   one or more email addresses To which
     *                      emails will be sent
     */
    public Mailer( String smtpServer, String sender, String[] recipients ) {
        smtpServer_ = smtpServer;
        sender_ = sender;
        recipients_ = recipients;
        props_ = new Properties();
        props_.put( "mail.smtp.host", smtpServer );
    }

    /**
     * Sends an email.
     * May throw an exception, though delivery failures may end up just
     * getting returned to the sender.
     *
     * @param  subject  subject line
     * @param  body    content of email
     */
    public void sendMessage( String subject, String body )
            throws MessagingException {
        MimeMessage msg = new MimeMessage( Session.getInstance( props_ ) );
        msg.setFrom( sender_ );
        for ( String recipient : recipients_ ) {
            msg.setRecipients( Message.RecipientType.TO, recipient );
        }
        msg.setSentDate( new Date() );
        msg.setSubject( subject );
        msg.setText( body );
        Transport.send( msg );
    }

    public static void main( String[] args ) throws MessagingException {
        String[] recipients = new String[] { args[ 0 ] };
        ConfigMap cmap = new ConfigMap();
        Mailer mailer = new Mailer( cmap.get( Config.SMTP_SERVER ),
                                    cmap.get( Config.SMTP_SENDER ),
                                    recipients );
        mailer.sendMessage( "Test from mailer", "It's a test.\n" );
    }
}
