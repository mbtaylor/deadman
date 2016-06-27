
import java.awt.BorderLayout;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class Deadman {

    private static final Logger logger_ =
        Logger.getLogger( Deadman.class.getName() );

    public static void main( String[] args ) {
        String usage = new StringBuffer()
            .append( "   Usage: " )
            .append( Deadman.class.getName() )
            .append( " [-help]" )
            .append( " [-limit <sec>]" )
            .append( " [-warning <sec>]" )
            .append( " [-[no]alwaysontop]" )
            .toString();
        int resetSec = 30 * 60;
        int warningSec = 3 * 60;
        boolean alwaysOnTop = true;
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
        Alarm alarm = Alarm.createAlarm();
        logger_.info( "Limit: " + resetSec + "s; "
                    + "Warning: " + warningSec + "s" );
        CountdownPanel counter = new CountdownPanel( alarm );
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
    }
}
