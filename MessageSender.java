
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

public class MessageSender {

    private final String smtpServer_;
    private final String sender_;
    private final Properties props_;
    
    public static final String SMTP_HOST = "smtp-srv.bris.ac.uk";
    public static final String SENDER = "Deadman <m.b.taylor@bristol.ac.uk>";

    public static final String MARK = "Mark Taylor <M.B.Taylor@bristol.ac.uk>";
    public static final String BEN = "Ben Maughan <Ben.Maughan@bristol.ac.uk>";
    public static final String RHYS = "Rhys Morris <R.Morris@bristol.ac.uk>";

    public MessageSender( String smtpServer, String sender ) {
        smtpServer_ = smtpServer;
        sender_ = sender;
        props_ = new Properties();
        props_.put( "mail.smtp.host", smtpServer );
    }

    public MessageSender() {
        this( SMTP_HOST, SENDER );
    }

    public MimeMessage createDefaultMessage( String... recipients )
            throws MessagingException {
        MimeMessage msg = new MimeMessage( Session.getInstance( props_ ) );
        msg.setFrom( SENDER );
        for ( String recipient : recipients ) {
            msg.setRecipients( Message.RecipientType.TO, recipient );
        }
        msg.setSubject( "Alert from deadman" );
        msg.setSentDate( new Date() );
        msg.setText( "Alert from deadman.\n" );
        return msg;
    }

    public void sendMessage( MimeMessage msg ) throws MessagingException {
        Transport.send( msg );
    }

    public static void main( String[] args ) throws MessagingException {
        String recipient = args[ 0 ];
        MessageSender sender = new MessageSender();
        MimeMessage msg = sender.createDefaultMessage( recipient );
        msg.setSubject( "Test from MessageSender" );
        msg.setText( "It's a test.\n" );
        sender.sendMessage( msg );
    }
}
