package uk.ac.bristol.star.deadman;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;


/**
 * Visual component that displays a CountdownLabel and a button to reset
 * the counter.
 *
 * @author   Mark Taylor
 * @since    27 Jul 2016
 */
public class CountdownPanel extends JPanel {

    private static final Logger logger_ =
        Logger.getLogger( CountdownPanel.class.getName() );

    /**
     * Constructor.
     *
     * @param  model  model
     */
    public CountdownPanel( final CountdownModel model ) {
        super( new BorderLayout() );
        
        final CountdownLabel countLabel = new CountdownLabel( model, 96 );
        add( countLabel, BorderLayout.CENTER );
        JButton resetButton = new JButton( new AbstractAction( "Reset" ) {
            public void actionPerformed( ActionEvent evt ) {
                logger_.info( "Reset by user to "
                            + CountdownModel
                             .formatMillis( model.getResetSeconds() * 1000 ) );
                model.resetZero();
            }
        } );
        resetButton.setFont( new Font( Font.DIALOG, Font.BOLD, 48 ) );

        JComponent buttonLine = Box.createHorizontalBox();
        buttonLine.add( Box.createHorizontalGlue() );
        buttonLine.add( resetButton );
        buttonLine.add( Box.createHorizontalGlue() );
        buttonLine.setBorder( BorderFactory
                             .createEmptyBorder( 20, 20, 20, 20 ) );
        add( buttonLine, BorderLayout.SOUTH );
    }
}
