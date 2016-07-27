package uk.ac.bristol.star.deadman;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Component to hold GUI configuration controls.
 *
 * @author   Mark Taylor
 * @since    27 Jul 2016
 */
public class ConfigPanel extends JPanel {

    private final List<ConfigControl<?>> controls_;

    /**
     * Constructor.
     *
     * @param  keys  config keys whose controls will be displayed
     */
    public ConfigPanel( ConfigKey<?>[] keys ) {
        super( new BorderLayout() );
        controls_ = new ArrayList<ConfigControl<?>>();
        GridBagLayout gridder = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        cons.anchor = GridBagConstraints.NORTHWEST;
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridy = 0;
        cons.insets = new Insets( 5, 0, 0, 0 );
        JComponent panel = new JPanel( gridder );
        ConfigMap dfltsMap = new ConfigMap();
        for ( ConfigKey<?> key : keys ) {
            ConfigControl<?> control = key.createControl();
            updateControl( control, dfltsMap );
            controls_.add( control );
            JComponent nameComp = new JLabel( key.getName() + ": " );
            JComponent queryComp = control.getComponent();
            cons.gridx = 0;
            cons.weightx = 0;
            gridder.setConstraints( nameComp, cons ); 
            panel.add( nameComp );
            cons.gridx = 1;
            cons.weightx = 1;
            gridder.setConstraints( queryComp, cons );
            panel.add( queryComp );
            cons.gridy++;
        }
        add( panel, BorderLayout.NORTH );
    }

    /**
     * Sets the state of this component from a config map.
     *
     * @param   map  map giving required configuration
     */
    public void setValues( ConfigMap map ) {
        for ( ConfigControl<?> control : controls_ ) {
            updateControl( control, map );
        }
    }

    /**
     * Returns the current state of this component.
     *
     * @return   map giving current configuration
     */
    public ConfigMap getValues() {
        ConfigMap map = new ConfigMap();
        for ( ConfigControl<?> control : controls_ ) {
            updateMap( map, control );
        }
        return map;
    }

    @Override
    public void setEnabled( boolean isEnabled ) {
        super.setEnabled( isEnabled );
        for ( ConfigControl<?> control : controls_ ) {
            control.getComponent().setEnabled( isEnabled );
        }
    }

    /**
     * Updates a given config map with the state of a given control.
     *
     * @param  map  map to update
     * @param  control   control whose value is to be extracted
     */
    private static <T> void updateMap( ConfigMap map,
                                       ConfigControl<T> control ) {
        map.put( control.getKey(), control.getValue() );
    }

    /**
     * Updates a given control with the state of a given config map.
     *
     * @param  control  control to update
     * @param  map   map from which value is to be extracted
     */
    private static <T> void updateControl( ConfigControl<T> control,
                                           ConfigMap map ) {
        control.setValue( map.get( control.getKey() ) );
    }
}
