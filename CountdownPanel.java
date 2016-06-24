
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class CountdownPanel extends JPanel {

    private final Alarm alarm_;
    private final int resetSec_;
    private final int warningSec_;
    private final JLabel countLabel_;
    private final JButton resetButton_;
    private long zeroEpoch_;

    public CountdownPanel( Alarm alarm, int resetSec, int warningSec ) {
        super( new BorderLayout() );
        alarm_ = alarm;
        resetSec_ = resetSec;
        warningSec_ = warningSec;
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
        resetZero( resetSec_ );
        add( countLabel_, BorderLayout.CENTER );

        resetButton_ = new JButton( new AbstractAction( "Reset" ) {
            public void actionPerformed( ActionEvent evt ) {
                resetZero( resetSec_ );
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
        timer.start();
    }

    public void resetZero( int resetsec ) {
        zeroEpoch_ = System.currentTimeMillis() + resetsec * 1000;
        alarm_.stop();
        updateTime();
    }

    private void updateTime() {
        long millis = zeroEpoch_ - System.currentTimeMillis();
        long posMillis = Math.max( 0, millis );
        countLabel_.setText( formatMillis( posMillis ) );
        boolean isWarning = posMillis <= 1000 * warningSec_;
        boolean isTimeout = millis <= 0;
        final Color bg;
        if ( isTimeout ) {
            bg = ( millis / 200 ) % 2 == 0 ? Color.RED : Color.PINK;
        }
        else if ( isWarning ) {
            bg = Color.ORANGE;
        }
        else {
            bg = Color.GREEN;
        }
        countLabel_.setBackground( bg );
        if ( millis <= 0 ) {
            alarm_.start();
        }
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
