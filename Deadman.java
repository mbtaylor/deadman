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

        /* Configure logging. */
        Logging.configureLogger( Logger.getLogger( "" ) );

        /* Prepare usage message. */
        StringBuffer ubuf = new StringBuffer()
              .append( "\n   Usage:" )
              .append( "\n      " )
              .append( Deadman.class.getSimpleName() )
              .append( " [-help]" )
              .append( " [-writeconfig]" )
              .append( " <name>=<value> ..." )
              .append( "\n\n   Options:" );
        ConfigKey<?>[] keys = DmConfig.KEYS;
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
        File file = new File( cmap.get( DmConfig.CONFIG_FILE ) );
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

        /* Log configuration. */
        logger_.info( "Initial configuration: ");
        for ( String line : cmap.getPropertyLines( keys ) ) {
            logger_.info( line );
        }

        /* Set up GUI and post window. */
        DmPanel dmPanel = new DmPanel( cmap );
        JFrame frm = new JFrame();
        Container content = frm.getContentPane();
        content.setLayout( new BorderLayout() );
        content.add( dmPanel, BorderLayout.CENTER );
        frm.setLocationRelativeTo( null );
        boolean alwaysOnTop = cmap.get( DmConfig.ONTOP ).booleanValue();
        if ( alwaysOnTop ) {
            if ( frm.isAlwaysOnTopSupported() ) {
                frm.setAlwaysOnTop( true );
            }
            else {
                logger_.warning( "Always on top unsupported for window" );
            }
        }
        frm.pack();
        frm.setVisible( true );
    }
}
