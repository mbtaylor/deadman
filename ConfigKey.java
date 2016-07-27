package uk.ac.bristol.star.deadman;

/**
 * Map key for indexing typed values.
 * There is a default value, and value&lt;-&gt;String conversion
 * methods are provided.
 *
 * @param   <T>  type of value this key indexes
 * @author   Mark Taylor
 * @since    25 Jul 2016
 */
public abstract class ConfigKey<T> {

    private final String name_;
    private final Class<T> clazz_;
    private final T dflt_;

    /**
     * Constructor.
     *
     * @param   name   key name
     * @param    clazz  type of data this key indexes
     * @param   dflt   default value
     */
    public ConfigKey( String name, Class<T> clazz, T dflt ) {
        name_ = name;
        clazz_ = clazz;
        dflt_ = dflt;
    }

    /**
     * Returns the key name.
     *
     * @return   key name
     */
    public String getName() {
        return name_;
    }

    /**
     * Returns the type of value this key indexes.
     *
     * @return  value class
     */
    public Class<T> getValueClass() {
        return clazz_;
    }

    /**
     * Returns the default value for this key.
     *
     * @return   default value
     */
    public T getDefaultValue() {
        return dflt_;
    }

    /**
     * Reads a typed value from its string representation.
     *
     * @param   txt   string representation, not null or empty
     * @return   typed value
     */
    public abstract T fromString( String txt ) throws ConfigException;

    /**
     * Returns the string representation for a given typed value.
     *
     * @param  value   typed value, not null
     * @return   string representation
     */
    public abstract String toString( T value );

    /**
     * Creates a GUI control that can be used to acquire values
     * for this key.
     *
     * @return   new config control
     */
    public abstract ConfigControl<T> createControl();

    @Override
    public String toString() {
        return new StringBuffer()
             .append( name_ )
             .append( " - " )
             .append( clazz_.getSimpleName() )
             .append( " (" )
             .append( toString( dflt_ ) )
             .append( ")" )
             .toString();
    }
}
