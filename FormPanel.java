package uk.ac.bristol.star.deadman;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Panel that requires some configuration information to be filled in.
 *
 * @author   Mark Taylor
 * @since    27 Jul 2016
 */
public abstract class FormPanel extends JPanel {

    private final ConfigPanel configPanel_;
    private final Action doneAct_;

    /**
     * Constructor.
     *
     * @param  keys  config keys to be presented for input
     * @param  buttonText  name of submit button
     */
    public FormPanel( ConfigKey<?>[] keys, String buttonText ) {
        super( new BorderLayout() );
        configPanel_ = new ConfigPanel( keys );
        doneAct_ = new AbstractAction( buttonText ) {
            public void actionPerformed( ActionEvent evt ) {
                if ( ! consumeConfig( configPanel_.getValues() ) ) {
                    JOptionPane
                   .showMessageDialog( FormPanel.this, "Form not complete",
                                       "Incomplete Form",
                                       JOptionPane.WARNING_MESSAGE );
                }
            }
        };
        add( configPanel_, BorderLayout.NORTH );
        JComponent buttLine = Box.createHorizontalBox();
        buttLine.add( Box.createHorizontalGlue() );
        buttLine.add( new JButton( doneAct_ ) );
        buttLine.add( Box.createHorizontalGlue() );
        add( buttLine, BorderLayout.SOUTH );
    }

    /**
     * This method is invoked when the submit button is hit.
     * The return value should indicate whether the information is
     * satisfactory or not; if false is returned, a popup is displayed
     * indicating that the user should fill in more details.
     *
     * @param  cmap   current state of this panel (entries for supplied keys)
     * @return  true iff state is satisfactory
     */
    protected abstract boolean consumeConfig( ConfigMap cmap );

    @Override
    public void setEnabled( boolean isEnabled ) {
        super.setEnabled( isEnabled );
        configPanel_.setEnabled( isEnabled );
        doneAct_.setEnabled( isEnabled );
    }
}
