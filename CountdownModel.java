package uk.ac.bristol.star.deadman;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Object that counts down to zero and messages a given alert instance
 * as appropriate.  It also serves as the model for a CountdownPanel.
 *
 * @author    Mark Taylor
 * @since     27 Jul 2016
 */
public class CountdownModel {

    private final Alert alert_;
    private final Timer timer_;
    private final List<ChangeListener> listeners_;
    private long zeroEpoch_;
    private int resetSec_ = 30 * 60;
    private int warningSec_ = 3 * 60;
    private String text_;
    private Color color_;
    private static final Logger logger_ =
        Logger.getLogger( CountdownModel.class.getName() );

    /**
     * Constructor.
     *
     * @param  alert  object that will be notified of status updates
     */
    public CountdownModel( Alert alert ) {
        alert_ = alert;
        listeners_ = new ArrayList<ChangeListener>();
        timer_ = new Timer( 100, new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                updateTime();
            }
        } );
        resetZero();
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
        if ( warningSec_ > resetSec_ ) {
            warningSec_ = resetSec_;
        }
        resetZero();
    }

    /**
     * Returns the number of seconds from countdown start to danger status.
     *
     * @return  full countdown time in seconds
     */
    public int getResetSeconds() {
        return resetSec_;
    }

    /**
     * Sets the number of seconds from countdown start to warning status.
     *
     * @param  warningSec  countdown time to warning in seconds
     */
    public void setWarningSeconds( int warningSec ) {
        warningSec_ = warningSec;
        if ( warningSec_ > resetSec_ ) {
            resetSec_ = warningSec_;
        }
        resetZero();
    }

    /**
     * Returns the number of seconds from countdown start to warning status.
     *
     * @return  countdown time to warning in seconds
     */
    public int getWarningSeconds() {
        return warningSec_;
    }

    /**
     * Resets the counter.
     */
    public void resetZero() {
        zeroEpoch_ = System.currentTimeMillis() + resetSec_ * 1000;
        alert_.setStatus( null );
        updateTime();
    }

    /**
     * Returns the text to display for current timer state.
     *
     * @return   countdown label text
     */
    public String getCountdownText() {
        return text_;
    }

    /**
     * Returns the colour to use for current timer state.
     *
     * @return   countdown indicator colour
     */
    public Color getCountdownColor() {
        return color_;
    }

    /**
     * Adds a listener that will be messaged if this counter's
     * countdown colour or text has changed.
     *
     * @param   l   listener
     */
    public void addChangeListener( ChangeListener l ) {
        listeners_.add( l );
    }

    /**
     * Removes a listener added previously.
     *
     * @param   l   listener
     */
    public void removeChangeListener( ChangeListener l ) {
        listeners_.remove( l );
    }

    /**
     * Updates current state for current time.
     */
    private void updateTime() {
        long millis = zeroEpoch_ - System.currentTimeMillis() + 999;
        text_ = formatMillis( Math.max( 0, millis ) );
        Status status = getStatus( millis );
        color_ = getCountdownColor( status );
        ChangeEvent evt = new ChangeEvent( this );
        for ( ChangeListener l : listeners_ ) {
            l.stateChanged( evt );
        }
        alert_.setStatus( status );
    }

    /**
     * Returns the colour associated with a given status at the current time.
     *
     * @param  status  status
     * @return   colour
     */
    private Color getCountdownColor( Status status ) {
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

    /**
     * Returns the status associated with a given time.
     *
     * @param   millis  milliseconds until zero
     * @return   status
     */
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
