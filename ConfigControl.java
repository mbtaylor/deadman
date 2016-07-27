package uk.ac.bristol.star.deadman;

import javax.swing.JComponent;

/**
 * Defines a GUI control that can be used to acquire values for a ConfigKey.
 *
 * @author   Mark Taylor
 * @since    27 Jul 2016
 */
public interface ConfigControl<T> {

    /**
     * Returns the config key for which this control is working.
     *
     * @return  key
     */
    ConfigKey<T> getKey();

    /**
     * Returns the GUI component that forms the business end of this object.
     *
     * @return  component
     */
    JComponent getComponent();

    /**
     * Sets the value displayed by the GUI control.
     *
     * @param  value  new value for control
     */
    void setValue( T value );

    /**
     * Returns the value currently displayed by the GUI control.
     *
     * @return   value of control
     */
    T getValue();
}
