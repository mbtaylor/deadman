
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class CountdownPanel extends JPanel {

    private final Alert alert_;
    private final JLabel countLabel_;
    private final JButton resetButton_;
    private long zeroEpoch_;
    private int resetSec_ = 30 * 60;
    private int warningSec_ = 3 * 60;
    private static final Logger logger_ =
        Logger.getLogger( CountdownPanel.class.getName() );

    public CountdownPanel( Alert alert ) {
        super( new BorderLayout() );
        alert_ = alert;
        countLabel_ = new JLabel();
        countLabel_.setOpaque( true );
        countLabel_.setFont( new Font( Font.MONOSPACED, Font.BOLD, 96 ) );
        countLabel_.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder( Color.DARK_GRAY, 8 ),
                BorderFactory.createEmptyBorder( 10, 20, 10, 20 ) ) );
        Timer timer = new Timer( 100, new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                updateTime();
            }
        } );
        add( countLabel_, BorderLayout.CENTER );

        resetButton_ = new JButton( new AbstractAction( "Reset" ) {
            public void actionPerformed( ActionEvent evt ) {
                logger_.info( "Reset by user to " + resetSec_ + "s" );
                resetZero();
            }
        } );
        resetButton_.setFont( new Font( Font.DIALOG, Font.BOLD, 48 ) );

        JComponent buttonLine = Box.createHorizontalBox();
        buttonLine.add( Box.createHorizontalGlue() );
        buttonLine.add( resetButton_ );
        buttonLine.add( Box.createHorizontalGlue() );
        buttonLine.setBorder( BorderFactory
                             .createEmptyBorder( 20, 20, 20, 20 ) );
        add( buttonLine, BorderLayout.SOUTH );
        resetZero();
        timer.start();
    }

    public void setResetSeconds( int resetSec ) {
        resetSec_ = resetSec;
        resetZero();
    }

    public void setWarningSeconds( int warningSec ) {
        warningSec_ = warningSec;
        resetZero();
    }

    public void resetZero() {
        zeroEpoch_ = System.currentTimeMillis() + resetSec_ * 1000;
        alert_.setStatus( null );
        updateTime();
    }

    private void updateTime() {
        long millis = zeroEpoch_ - System.currentTimeMillis() + 999;
        long posMillis = Math.max( 0, millis );
        countLabel_.setText( formatMillis( posMillis ) );
        final Status status;
        if ( millis <= 0 ) {
            status = Status.DANGER;
        }
        else if ( millis <= 1000 * warningSec_ ) {
            status = Status.WARNING;
        }
        else {
            status = null;
        }
        final Color bg;
        if ( status == Status.DANGER ) {
            bg = ( millis / 200 ) % 2 == 0 ? Color.RED : Color.PINK;
        }
        else if ( status == Status.WARNING ) {
            bg = Color.ORANGE;
        }
        else {
            bg = Color.GREEN;
        }
        countLabel_.setBackground( bg );
        alert_.setStatus( status );
    }

    private String formatMillis( long positiveMillis ) {
        long cSec = positiveMillis / 1000;
        long cMin = cSec / 60;
        long cHour = cMin / 60;
        int nSec = (int) ( cSec % 60 );
        int nMin = (int) ( cMin % 60 );
        int nHour = (int) cHour;
        String sSec = ( nSec <= 9 ? "0" : "" ) + Integer.toString( nSec );
        String sMin = ( nMin <= 9 ? "0" : "" ) + Integer.toString( nMin );
        String sHour = cHour > 0 ? Integer.toString( nHour ) : "";
        StringBuffer sbuf = new StringBuffer();
        if ( sHour.length() > 0 ) {
            sbuf.append( sHour )
                .append( ":" );
        }
        sbuf.append( sMin )
            .append( ":" )
            .append( sSec );
        return sbuf.toString();
    }
}
