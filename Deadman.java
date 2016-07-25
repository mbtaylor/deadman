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
        StringBuffer ubuf = new StringBuffer()
              .append( "\n   Usage:" )
              .append( "\n      " )
              .append( Deadman.class.getName() )
              .append( " <name>=<value> ..." )
              .append( "\n\n   Options:" );
        ConfigKey<?>[] keys = Config.KEYS;
        for ( ConfigKey<?> key : keys ) {
            ubuf.append( "\n      " )
                .append( key.toString() );
        }
        String usage = ubuf.toString();
        ConfigMap cmap = new ConfigMap();
        List<String> argList = new ArrayList<String>( Arrays.asList( args ) );
        for ( Iterator<String> it = argList.iterator(); it.hasNext(); ) {
            String arg = it.next();
            int ieq = arg.indexOf( '=' );
            if ( arg.startsWith( "-h" ) ||
                 arg.startsWith( "--h" ) ) {
                it.remove();
                System.err.println( usage );
                System.exit( 0 );
            }
            else if ( ieq > 0 ) {
                it.remove();
                String name = arg.substring( 0, ieq );
                String value = arg.substring( ieq + 1 );
                try {
                    cmap.assign( name, value, keys );
                }
                catch ( ConfigException e ) {
                    System.err.println( e.getMessage() );
                    System.exit( 1 );
                }
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
        boolean isAudio = cmap.get( Config.AUDIO ).booleanValue();
        String[] emails = cmap.get( Config.EMAILS );
        int resetSec = cmap.get( Config.RESET_SEC ).intValue();
        int warningSec = cmap.get( Config.WARNING_SEC ).intValue();
        boolean alwaysOnTop = cmap.get( Config.ONTOP ).booleanValue();

        JFrame frm = new JFrame();
        List<Alert> alerts = new ArrayList<Alert>();
        if ( isAudio ) {
            alerts.add( Alerts.createSirenAlert() );
        }
        alerts.add( Alerts.createLoggingAlert() );
        if ( emails.length > 0 ) {
            alerts.add( Alerts.createEmailAlert( emails ) );
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
