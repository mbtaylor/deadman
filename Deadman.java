package uk.ac.bristol.star.deadman;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * Harness class for the Dead Man's Timer application.
 * Run the main method from the command line.
 * You can use the <code>-h</code> flag for help.
 *
 * @author   Mark Taylor
 * @since    28 Jun 2016
 */
public class Deadman {

    private static final Logger logger_ =
        Logger.getLogger( Deadman.class.getName() );

    public static void main( String[] args ) throws IOException {
        String usage = new StringBuffer()
            .append( "\n   Usage:" )
            .append( "\n" )
            .append( "\n      " )
            .append( Deadman.class.getName() )
            .append( "\n         " )
            .append( " [-help]" )
            .append( " [-limit <sec>]" )
            .append( " [-warning <sec>]" )
            .append( " [-[no]alwaysontop]" )
            .append( "\n         " )
            .append( " [-mailed <recipient>]" )
            .append( " [-[no]audio]" )
            .append( "\n" )
            .toString();
        int resetSec = 30 * 60;
        int warningSec = 3 * 60;
        boolean alwaysOnTop = true;
        boolean isAudio = true;
        List<String> mailedList = new ArrayList<String>();
        List<String> argList = new ArrayList<String>( Arrays.asList( args ) );
        for ( Iterator<String> it = argList.iterator(); it.hasNext(); ) {
            String arg = it.next();
            if ( arg.startsWith( "-h" ) ||
                 arg.startsWith( "--h" ) ) {
                it.remove();
                System.err.println( usage );
                System.exit( 0 );
            }
            else if ( "-limit".equals( arg ) && it.hasNext() ) {
                it.remove();
                resetSec = Integer.parseInt( it.next() );
                it.remove();
            }
            else if ( "-warning".equals( arg ) && it.hasNext() ) {
                it.remove();
                warningSec = Integer.parseInt( it.next() );
                it.remove();
            }
            else if ( "-alwaysontop".equals( arg ) ) {
                it.remove();
                alwaysOnTop = true;
            }
            else if ( "-noalwaysontop".equals( arg ) ) {
                it.remove();
                alwaysOnTop = false;
            }
            else if ( "-mailed".equals( arg ) && it.hasNext() ) {
                it.remove();
                mailedList.add( it.next() );
                it.remove();
            }
            else if ( "-audio".equals( arg ) ) {
                isAudio = true;
            }
            else if ( "-noaudio".equals( arg ) ) {
                isAudio = false;
            }
            else {
                System.err.println( usage );
                System.exit( 1 );
            }
        }
        if ( argList.size() > 0 ) {
            System.err.println( usage );
            System.exit( 1 );
        }
        JFrame frm = new JFrame();
        List<Alert> alerts = new ArrayList<Alert>();
        if ( isAudio ) {
            alerts.add( Alerts.createSirenAlert() );
        }
        alerts.add( Alerts.createLoggingAlert() );
        if ( mailedList.size() > 0 ) {
            String[] recipients = mailedList.toArray( new String[ 0 ] );
            alerts.add( Alerts.createEmailAlert( recipients ) );
        }
        Alert alert = Alerts.createMultiAlert( alerts );
        logger_.info( "Limit: "
                    + CountdownPanel.formatMillis( resetSec * 1000 )
                    + "; Warning: "
                    + CountdownPanel.formatMillis( warningSec * 1000 ) );
        CountdownPanel counter = new CountdownPanel( alert );
        counter.setResetSeconds( resetSec );
        counter.setWarningSeconds( warningSec );
        Container content = frm.getContentPane();
        content.setLayout( new BorderLayout() );
        content.add( counter, BorderLayout.CENTER );
        counter.setBorder( BorderFactory.createEmptyBorder( 24, 24, 24, 24 ) );
        frm.pack();
        frm.setLocationRelativeTo( null );
        frm.setVisible( true );
        if ( alwaysOnTop ) {
            if ( frm.isAlwaysOnTopSupported() ) {
                frm.setAlwaysOnTop( true );
            }
            else {
                logger_.warning( "Always on top unsupported for window" );
            }
        }
        counter.start();
    }
}
