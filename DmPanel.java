package uk.ac.bristol.star.deadman;

import java.awt.BorderLayout;
import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 * Main GUI panel for Deadman application.
 *
 * @author   Mark Taylor
 * @since    27 Jul 2016
 */
public class DmPanel extends JPanel {

    private final JTabbedPane tabber_;
    private final InitPanel initer_;
    private final CountdownPanel counter_;
    private final ExitPanel exiter_;
    private final int itIniter_;
    private final int itCounter_;
    private final int itExiter_;
    private final Mailer mailer_;
    private String userName_;

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
        if ( emails.length > 0 ) {
            String smtpServer = cmap.get( DmConfig.SMTP_SERVER );
            String sender = cmap.get( DmConfig.SMTP_SENDER ); 
            mailer_ = new Mailer( smtpServer, sender, emails, "[deadman] " );
            alerts.add( Alerts.createEmailAlert( mailer_ ) );
        }
        else {
            mailer_ = null; 
        }   
        Alert alert = Alerts.createMultiAlert( alerts );

        /* Set up initialiser panel. */
        initer_ = new InitPanel( mailer_, new Runnable() {
            public void run() {
                initialised();
            }
        } );

        /* Set up counter panel. */
        counter_ = new CountdownPanel( alert );
        counter_.setBorder( BorderFactory.createEmptyBorder( 24, 24, 24, 24 ) );
        counter_.setResetSeconds( resetSec );
        counter_.setWarningSeconds( warningSec );

        /* Set up exit panel. */
        exiter_ = new ExitPanel( new Runnable() {
            public void run() {
                finished();
            }
        } );

        /* Place GUI components in a tabber. */
        tabber_ = new JTabbedPane();
        itIniter_ = addTab( tabber_, "Initialise", initer_ );
        itCounter_ = addTab( tabber_, "Counter", counter_ );
        itExiter_ = addTab( tabber_, "Exit", exiter_ );
        tabber_.setEnabledAt( itCounter_, false );
        add( tabber_, BorderLayout.CENTER );
    }

    /**
     * Invoked when initialisation is complete and the countdown
     * is about to start.
     */
    private void initialised() {
        userName_ = initer_.getUserName();
        initer_.setEnabled( false );
        tabber_.setEnabledAt( itCounter_, true );
        tabber_.setSelectedIndex( itCounter_ );
        counter_.start();
    }

    /**
     * Invoked when user has wound up operations and is ready to
     * exit the application.
     */
    private void finished() {
        counter_.stop();
        tabber_.setEnabledAt( itCounter_, false );
        if ( mailer_ != null ) {
            String topic = "Exit by " + userName_;
            String body = new StringBuffer()
                .append( "Deadman application exited at " )
                .append( new Date() )
                .append( "\n" )
                .append( "User comment:\n" )
                .append( "   " )
                .append( exiter_.getUserComment() )
                .append( "\n" )
                .toString();
            mailer_.sendMessage( topic, body );
        }
        Window win = SwingUtilities.getWindowAncestor( this );
        win.dispose();
    }

    /**
     * Utility method that adds a component to a tabber and records its
     * tab index.
     *
     * @param  tabber   tabber
     * @param  label    label for tab
     * @param  comp     tab content component
     * @return   index of added tab
     */
    private static int addTab( JTabbedPane tabber, String label,
                               JComponent comp ) {
        int index = tabber.getTabCount();
        tabber.add( label, comp );
        return index;
    }
}
