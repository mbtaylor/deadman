package uk.ac.bristol.star.deadman;

import java.util.Hashtable;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Component that reconfigures the CountownModel's time settings.
 *
 * @author   Mark Taylor
 * @since    29 Jul 2016
 */
public class ResetSlider extends JPanel {

    private final CountdownModel counter_;
    private final JSlider rSlider_;
    private final JSlider wSlider_;

    /**
     * Constructor.
     *
     * @param  counter  countdown model
     */
    public ResetSlider( CountdownModel counter ) {
        super( new BorderLayout() );
        counter_ = counter;
        int maxMins = Math.max( 59, counter_.getResetSeconds() / 60 );
        rSlider_ = createTimeSlider( maxMins );
        wSlider_ = createTimeSlider( maxMins );
        updateSliders();
        rSlider_.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent evt ) {
                counter_.setResetSeconds( rSlider_.getValue() );
                updateSliders();
            }
        } );
        wSlider_.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent evt ) {
                counter_.setWarningSeconds( wSlider_.getValue() );
                updateSliders();
            }
        } );
        JComponent box = Box.createVerticalBox();
        add( box, BorderLayout.NORTH );
        box.add( packageSlider( rSlider_, "Reset Time" ) );
        box.add( packageSlider( wSlider_, "Warning Time" ) );
    }

    /**
     * Ensures the GUI corresponds to the current model state.
     */
    private void updateSliders() {
        int reset = counter_.getResetSeconds();
        int warn = counter_.getWarningSeconds();
        if ( rSlider_.getValue() != reset ) {
            rSlider_.setValue( reset );
        }
        if ( wSlider_.getValue() != warn ) {
            wSlider_.setValue( warn );
        }
    }

    /**
     * Returns a component that presents a time slider along with
     * a title and readout.
     *
     * @param   slider  slider
     * @param   title   short text label
     * @return   component
     */
    private static JComponent packageSlider( final JSlider slider,
                                             String title ) {
        JComponent titleLine = Box.createHorizontalBox();
        final JLabel valueLabel = new JLabel();
        final Runnable labelUpdater = new Runnable() {
            public void run() {
                valueLabel.setText( CountdownModel
                                   .formatMillis( 1000 * slider.getValue() ) );
            }
        };
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent evt ) {
                labelUpdater.run();
            }
        } );
        labelUpdater.run();
        titleLine.add( Box.createHorizontalStrut( 10 ) );
        titleLine.add( new JLabel( title ) );
        titleLine.add( Box.createHorizontalGlue() );
        titleLine.add( valueLabel );
        titleLine.add( Box.createHorizontalStrut( 10 ) );
        JComponent box = Box.createVerticalBox();
        box.add( titleLine );
        box.add( slider );
        return box;
    }

    /**
     * Creates a slider to select a duration of order minutes.
     *
     * @param  maxMins  maximum number of minutes
     * @return   slider
     */
    private static JSlider createTimeSlider( int maxMins ) {
        JSlider slider = new JSlider( 30, 60 * maxMins );
        slider.setMajorTickSpacing( 60 * 10 );
        slider.setMinorTickSpacing( 60 );
        slider.setPaintTicks( true );
        Hashtable labels = new Hashtable();
        for ( int i = 0; i < maxMins; i += 10 ) {
            labels.put( new Integer( 60 * i ),
                        new JLabel( Integer.toString( i ) ) );
        }
        slider.setLabelTable( labels );
        slider.setPaintLabels( true );
        return slider;
    }
}
