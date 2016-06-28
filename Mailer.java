
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

public class Mailer {

    private final String smtpServer_;
    private final String sender_;
    private final String[] recipients_;
    private final Properties props_;
    
    public static final String SMTP_HOST = "smtp-srv.bris.ac.uk";
    public static final String SENDER = "Deadman <m.b.taylor@bristol.ac.uk>";

    public static final String MARK = "Mark Taylor <M.B.Taylor@bristol.ac.uk>";
    public static final String BEN = "Ben Maughan <Ben.Maughan@bristol.ac.uk>";
    public static final String RHYS = "Rhys Morris <R.Morris@bristol.ac.uk>";

    public Mailer( String smtpServer, String sender, String[] recipients ) {
        smtpServer_ = smtpServer;
        sender_ = sender;
        recipients_ = recipients;
        props_ = new Properties();
        props_.put( "mail.smtp.host", smtpServer );
    }

    public void sendMessage( String subject, String text )
            throws MessagingException {
        MimeMessage msg = new MimeMessage( Session.getInstance( props_ ) );
        msg.setFrom( SENDER );
        for ( String recipient : recipients_ ) {
            msg.setRecipients( Message.RecipientType.TO, recipient );
        }
        msg.setSubject( "Alert from deadman" );
        msg.setSentDate( new Date() );
        msg.setText( "Alert from deadman.\n" );
        Transport.send( msg );
    }

    public static void main( String[] args ) throws MessagingException {
        String[] recipients = new String[] { args[ 0 ] };
        Mailer mailer = new Mailer( SMTP_HOST, SENDER, recipients );
        mailer.sendMessage( "Test from mailer", "It's a test.\n" );
    }
}
