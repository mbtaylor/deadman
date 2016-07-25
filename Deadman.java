package uk.ac.bristol.star.deadman;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

        /* Prepare usage message. */
        StringBuffer ubuf = new StringBuffer()
              .append( "\n   Usage:" )
              .append( "\n      " )
              .append( Deadman.class.getSimpleName() )
              .append( " [-help]" )
              .append( " [-writeconfig]" )
              .append( " <name>=<value> ..." )
              .append( "\n\n   Options:" );
        ConfigKey<?>[] keys = Config.KEYS;
        for ( ConfigKey<?> key : keys ) {
            ubuf.append( "\n      " )
                .append( key.toString() );
        }
        ubuf.append( "\n" );
        String usage = ubuf.toString();

        /* Prepare config map from command-line arguments. */
        ConfigMap cmap = new ConfigMap();
        List<String> argList = new ArrayList<String>( Arrays.asList( args ) );
        Map<String,String> argMap = new LinkedHashMap<String,String>();
        for ( Iterator<String> it = argList.iterator(); it.hasNext(); ) {
            String arg = it.next();
            int ieq = arg.indexOf( '=' );
            if ( arg.startsWith( "-h" ) ||
                 arg.startsWith( "--h" ) ) {
                it.remove();
                System.err.println( usage );
                System.exit( 0 );
            }
            else if ( "-writeconfig".equals( arg ) ) {
                it.remove();
                for ( String line : new ConfigMap().getPropertyLines( keys ) ) {
                    System.out.println( line );
                }
                System.exit( 0 );
            }
            else if ( ieq > 0 ) {
                it.remove();
                String name = arg.substring( 0, ieq );
                String value = arg.substring( ieq + 1 );
                argMap.put( name, value );
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

        /* Read config file if present. */
        File file = new File( cmap.get( Config.CONFIG_FILE ) );
        if ( file.exists() ) {
            try {
                logger_.info( "Loading properties from file " + file );
                Properties props = new Properties();
                props.load( new FileInputStream( file ) );
                cmap.addProperties( props, keys );
            }
            catch ( ConfigException e ) {
                System.err.println( e.getMessage() );
                System.exit( 1 );
            }
        }
        else {
            logger_.info( "No file " + file + " - not loading properties" );
        }
        for ( Map.Entry<String,String> entry : argMap.entrySet() ) {
            try {
                cmap.assign( entry.getKey(), entry.getValue(), keys );
            }
            catch ( ConfigException e ) {
                System.err.println( e.getMessage() );
                System.exit( 1 );
            }
        }

        /* Acquire configuration items. */
        boolean isAudio = cmap.get( Config.AUDIO ).booleanValue();
        String[] emails = cmap.get( Config.EMAILS );
        int resetSec = cmap.get( Config.RESET_SEC ).intValue();
        int warningSec = cmap.get( Config.WARNING_SEC ).intValue();
        boolean alwaysOnTop = cmap.get( Config.ONTOP ).booleanValue();

        /* Prepare alerts according to configuration. */
        List<Alert> alerts = new ArrayList<Alert>();
        if ( isAudio ) {
            alerts.add( Alerts.createSirenAlert() );
        }
        alerts.add( Alerts.createLoggingAlert() );
        final Mailer mailer;
        if ( emails.length > 0 ) {
            String smtpServer = cmap.get( Config.SMTP_SERVER );
            String sender = cmap.get( Config.SMTP_SENDER );
            mailer = new Mailer( smtpServer, sender, emails );
            alerts.add( Alerts.createEmailAlert( mailer ) );
        }
        else {
            mailer = null;
        }
        Alert alert = Alerts.createMultiAlert( alerts );
        for ( String line : cmap.getPropertyLines( keys ) ) {
            logger_.info( line );
        }

        /* Set up GUI. */
        JFrame frm = new JFrame();
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

        /* Start running. */
        counter.start();
    }
}
