package uk.ac.bristol.star.deadman;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
    private final CountdownModel counter_;
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
        counter_ = new CountdownModel( alert );
        CountdownPanel countPanel = new CountdownPanel( counter_ );
        countPanel.setBorder( BorderFactory
                             .createEmptyBorder( 24, 24, 24, 24 ) );
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
        itIniter_ = addTab( tabber_, "Initialise", withCounter( initer_ ) );
        itCounter_ = addTab( tabber_, "Counter", countPanel );
        itExiter_ = addTab( tabber_, "Exit", withCounter( exiter_ ) );
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
        Window win = SwingUtilities.getWindowAncestor( this );
        if ( win instanceof JFrame ) {
            JFrame frm = (JFrame) win;
            frm.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
            frm.addWindowListener( new WindowAdapter() {
                @Override
                public void windowClosing( WindowEvent evt ) {
                    String[] msg = new String[] {
                        "Don't exit the application by closing the window.",
                        "",
                        "Please go to the Exit tab and fill in the checklist.",
                    };
                    JOptionPane
                   .showMessageDialog( DmPanel.this, msg, "Close Prevented",
                                       JOptionPane.WARNING_MESSAGE );
                }
            } );
        }

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
     * Returns a component that includes the supplied component along with
     * a view of the counter state.
     *
     * @param  comp  component to display
     * @return  containing component
     */
    private JComponent withCounter( JComponent comp ) {
        JPanel panel = new JPanel( new BorderLayout() );
        JComponent counterLine = Box.createHorizontalBox();
        counterLine.add( Box.createHorizontalGlue() );
        counterLine.add( new CountdownLabel( counter_, 24 ) );
        counterLine.setBorder( BorderFactory.createEmptyBorder( 0, 0, 5, 0 ) );
        panel.add( counterLine, BorderLayout.NORTH );
        panel.add( comp, BorderLayout.CENTER );
        panel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
        return panel;
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
