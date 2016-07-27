package uk.ac.bristol.star.deadman;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Visual component that displays a CountdownModel.
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
        final JLabel countLabel = new JLabel();
        countLabel.setOpaque( true );
        countLabel.setFont( new Font( Font.MONOSPACED, Font.BOLD, 96 ) );
        countLabel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder( Color.DARK_GRAY, 8 ),
                BorderFactory.createEmptyBorder( 10, 20, 10, 20 ) ) );
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

        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent evt ) {
                countLabel.setText( model.getCountdownText() );
                countLabel.setBackground( model.getCountdownColor() );
                countLabel.repaint();
                countLabel.revalidate();
            }
        } );
    }
}
