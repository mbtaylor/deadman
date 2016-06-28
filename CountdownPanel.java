package uk.ac.bristol.star.deadman;

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

/**
 * Visual component that counts down to zero and messages a given alert
 * instance as appropriate.
 *
 * @author   Mark Taylor
 * @since    28 Jun 2016
 */
public class CountdownPanel extends JPanel {

    private final Alert alert_;
    private final JLabel countLabel_;
    private final JButton resetButton_;
    private final Timer timer_;
    private long zeroEpoch_;
    private int resetSec_ = 30 * 60;
    private int warningSec_ = 3 * 60;
    private static final Logger logger_ =
        Logger.getLogger( CountdownPanel.class.getName() );

    /**
     * Constructor.
     *
     * @param  alert  object that will be notified of status updates
     */
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
        timer_ = new Timer( 100, new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                updateTime();
            }
        } );
        add( countLabel_, BorderLayout.CENTER );

        resetButton_ = new JButton( new AbstractAction( "Reset" ) {
            public void actionPerformed( ActionEvent evt ) {
                logger_.info( "Reset by user to "
                            + formatMillis( resetSec_ * 1000 ) );
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
    }

    /**
     * Resets the counter and starts the countdown.
     */
    public void start() {
        resetZero();
        timer_.start();
    }

    /**
     * Stops the countdown.
     */
    public void stop() {
        timer_.stop();
    }

    /**
     * Sets the number of seconds from countdown start to danger status.
     *
     * @param  resetSec   full countdown time in seconds
     */
    public void setResetSeconds( int resetSec ) {
        resetSec_ = resetSec;
        if ( warningSec_ >= resetSec_ ) {
            warningSec_ = resetSec_ / 5;
        }
        resetZero();
    }

    /**
     * Sets the number of seconds from countdown start to warning status.
     *
     * @param  warningSec  countdown time to warning in seconds
     */
    public void setWarningSeconds( int warningSec ) {
        warningSec_ = warningSec;
        resetZero();
    }

    /**
     * Resets the counter.
     */
    public void resetZero() {
        zeroEpoch_ = System.currentTimeMillis() + resetSec_ * 1000;
        alert_.setStatus( null );
        updateTime();
    }

    private void updateTime() {
        long millis = zeroEpoch_ - System.currentTimeMillis() + 999;
        countLabel_.setText( formatMillis( Math.max( 0, millis ) ) );
        Status status = getStatus( millis );
        countLabel_.setBackground( getBackgroundColor( status ) );
        alert_.setStatus( status );
    }

    private Color getBackgroundColor( Status status ) {
        if ( status == Status.DANGER ) {
            return ( System.currentTimeMillis() / 200 ) % 2 == 0
                 ? Color.RED
                 : Color.PINK;
        }
        else if ( status == Status.WARNING ) {
            return Color.ORANGE;
        }
        else {
            return Color.GREEN;
        }
    }

    private Status getStatus( long millis ) {
        if ( millis <= 0 ) {
            return Status.DANGER;
        }
        else if ( millis <= 1000 * warningSec_ ) {
            return Status.WARNING;
        }
        else {
            return null;
        }
    }

    /**
     * Formats a time in milliseconds as sexagesimal.
     *
     * @param  positiveMillis   a time interval in milliseconds,
     *                          must be positive
     * @return  formatted string
     */
    public static String formatMillis( long positiveMillis ) {
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
