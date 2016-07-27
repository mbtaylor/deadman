package uk.ac.bristol.star.deadman;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
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
    private final FormPanel initer_;
    private final CountdownModel counter_;
    private final ExitPanel exiter_;
    private final int itIniter_;
    private final int itCounter_;
    private final int itExiter_;
    private final List<Alert> alertList_;
    private Mailer mailer_;
    private String userName_;

    private static final ConfigKey<String> USER_NAME =
        DmConfig.createStringKey( "Name", "" );
    private static final ConfigKey<String> USER_PHONE =
        DmConfig.createStringKey( "Phone", "" );
    private static final ConfigKey<String> USER_COMMENTS =
        DmConfig.createStringKey( "Comments", "" );
    private static final Logger logger_ =
        Logger.getLogger( DmPanel.class.getName() );

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
        final String smtpServer = cmap.get( DmConfig.SMTP_SERVER );
        final String sender = cmap.get( DmConfig.SMTP_SENDER ); 
        final boolean requireEmail = cmap.get( DmConfig.REQUIRE_EMAIL );

        /* Prepare alerts according to configuration.
         * Note the alerts list can be altered later to adjust the
         * actual alert targets. */
        alertList_ = new ArrayList<Alert>();
        if ( isAudio ) {
            alertList_.add( Alerts.createSirenAlert() );
        }
        alertList_.add( Alerts.createLoggingAlert() );
        Alert alert = new Alert() {
            public void setStatus( Status status ) {
                for ( Alert a : alertList_ ) {
                    a.setStatus( status );
                }
            }
        };

        /* Set up a custom key for getting email contact addresses.
         * This takes its default value from the application config,
         * but will allow the user to adjust it. */
        final ConfigKey<String[]> mailsKey =
            DmConfig.createAdd1StringsKey( "Contact Emails", 
                                           cmap.get( DmConfig.EMAILS ) );

        /* Prepare the other config keys used for the initialisation panel. */
        final ConfigKey<?>[] initKeys = new ConfigKey<?>[] {
            USER_NAME,
            USER_PHONE,
            mailsKey,
            USER_COMMENTS,
        };
        final List<ConfigKey<String>> requiredInitKeys =
            new ArrayList<ConfigKey<String>>();
        requiredInitKeys.add( USER_NAME );
        requiredInitKeys.add( USER_PHONE );

        /* Set up the initialiser panel. */
        initer_ = new FormPanel( initKeys, "Start" ) {
            protected boolean consumeConfig( ConfigMap initCmap ) {
                String[] emails = initCmap.get( mailsKey );

                /* Check the form is filled in completely enough. */
                if ( hasAllValues( initCmap, requiredInitKeys ) &&
                     ( emails.length > 0 || ! requireEmail ) ) {

                    /* If so, perform some additional initialisation.
                     * In particular record the user name and mailer defined
                     * by this user config, since we will need it later. */
                    String userName = initCmap.get( USER_NAME );
                    final Mailer mailer;
                    if ( emails.length > 0 ) {
                        mailer = new Mailer( smtpServer, sender, emails,
                                             "[deadman] " );
                        sendInitEmail( mailer, userName, initCmap, initKeys );
                        alertList_.add( Alerts.createEmailAlert( mailer ) );
                    }
                    else {
                        mailer = null;
                    }
                    userName_ = userName;
                    mailer_ = mailer;
                    logger_.info( "Initialised by " + userName );
                    if ( emails.length > 0 ) {
                        for ( int i = 0; i < emails.length; i++ ) {
                            logger_.info( "Email contact #" + ( i + 1 ) + ": "
                                        + emails[ i ] );
                        }
                    }
                    else {
                        logger_.warning( "No email contacts" );
                    }

                    /* Perform other GUI-related initialisation tasks. */
                    initialised();
                    return true;
                }
                else {
                    return false;
                }
            }
        };

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
        tabber_.setEnabledAt( itExiter_, false );
        add( tabber_, BorderLayout.CENTER );
    }

    /**
     * Invoked when initialisation is complete and the countdown
     * is about to start.
     */
    private void initialised() {
        initer_.setEnabled( false );
        tabber_.setEnabledAt( itCounter_, true );
        tabber_.setEnabledAt( itExiter_, true );
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

    /**
     * Sends an initialisation email given a config map.
     * Sending is asynchronous.
     *
     * @param  mailer  can send emails, not null
     * @param  userName  user name
     * @param  cmap   user configuration info
     * @param  keys   keys for which information should be reported
     */
    private static void sendInitEmail( Mailer mailer, String userName,
                                       ConfigMap cmap, ConfigKey<?>[] keys ) {
        String topic = "Startup by " + cmap.get( USER_NAME );
        StringBuffer sbuf = new StringBuffer()
            .append( "Deadman application started at " )
            .append( new Date() )
            .append( "\n" )
            .append( "\nUser info:\n" );
        for ( ConfigKey<?> key : keys ) {
            sbuf.append( "\n   " )
                .append( key.getName() )
                .append( ":\n      " )
                .append( cmap.getString( key ) )
                .append( "\n" );
        }
        String body = sbuf.toString();
        mailer.scheduleSendMessage( topic, body );
    }

    /**
     * Tests whether all of a list of keys have non-blank entries in
     * a given config map.
     *
     * @param  cmap   config map
     * @param  reqKeys   keys required to have non-blank entries
     * @return  true iff all required keys have non-empty string entries
     */
    private static boolean hasAllValues( ConfigMap cmap,
                                         List<ConfigKey<String>> reqKeys ) {
        for ( ConfigKey<String> key : reqKeys ) {
            String value = cmap.get( key );
            if ( value == null || value.trim().length() == 0 ) {
                return false;
            }
        }
        return true;
    }
}
