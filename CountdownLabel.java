package uk.ac.bristol.star.deadman;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Visual component that displays the state of a CountdownModel.
 *
 * @author   Mark Taylor
 * @since    27 Jul 2016
 */
public class CountdownLabel extends JLabel {

    private final CountdownModel model_;

    /**
     * Constructor.
     *
     * @param  model  countdown model
     * @param  fontSize  size of counter text
     */
    public CountdownLabel( final CountdownModel model, int fontSize ) {
        model_ = model;
        setOpaque( true );
        setFont( new Font( Font.MONOSPACED, Font.BOLD, fontSize ) );
        int g0 = (int) ( fontSize / 12 );
        int g1 = (int) ( fontSize / 10 );
        int g2 = g1 * 2;
        setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder( Color.DARK_GRAY, g0 ),
                BorderFactory.createEmptyBorder( g1, g2, g1, g2 ) ) );
        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent evt ) {
                updateCounterState();
            }
        } );
        updateCounterState();
    }

    /**
     * Updates visual state if there may have been a change.
     */
    private void updateCounterState() {
        setText( model_.getCountdownText() );
        setBackground( model_.getCountdownColor() );
    }
}
