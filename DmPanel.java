package uk.ac.bristol.star.deadman;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Main GUI panel for Deadman application.
 *
 * @author   Mark Taylor
 * @since    27 Jul 2016
 */
public class DmPanel extends JPanel {

    /**
     * Constructor.
     *
     * @param   cmap  populated configuration map for application
     */
    public DmPanel( ConfigMap cmap ) throws IOException {

        /* Acquire configuration items. */
        boolean isAudio = cmap.get( DmConfig.AUDIO ).booleanValue();
        String[] emails = cmap.get( DmConfig.EMAILS );
        int resetSec = cmap.get( DmConfig.RESET_SEC ).intValue();
        int warningSec = cmap.get( DmConfig.WARNING_SEC ).intValue();
        boolean alwaysOnTop = cmap.get( DmConfig.ONTOP ).booleanValue();

        /* Prepare alerts according to configuration. */
        List<Alert> alerts = new ArrayList<Alert>();
        if ( isAudio ) {
            alerts.add( Alerts.createSirenAlert() );
        }
        alerts.add( Alerts.createLoggingAlert() );
        final Mailer mailer;
        if ( emails.length > 0 ) {
            String smtpServer = cmap.get( DmConfig.SMTP_SERVER );
            String sender = cmap.get( DmConfig.SMTP_SENDER ); 
            mailer = new Mailer( smtpServer, sender, emails, "[deadman] " );
            alerts.add( Alerts.createEmailAlert( mailer ) );
        }
        else {
            mailer = null; 
        }   
        Alert alert = Alerts.createMultiAlert( alerts );

        /* Set up GUI. */
        CountdownPanel counter = new CountdownPanel( alert );
        counter.setBorder( BorderFactory.createEmptyBorder( 24, 24, 24, 24 ) );
        counter.setResetSeconds( resetSec );
        counter.setWarningSeconds( warningSec );
        add( counter );
        counter.start();
    }
}
