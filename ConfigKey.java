package uk.ac.bristol.star.deadman;

public abstract class ConfigKey<T> {

    private final String name_;
    private final Class<T> clazz_;
    private final T dflt_;

    public ConfigKey( String name, Class<T> clazz, T dflt ) {
        name_ = name;
        clazz_ = clazz;
        dflt_ = dflt;
    }

    public String getName() {
        return name_;
    }

    public Class<T> getValueClass() {
        return clazz_;
    }

    public T getDefaultValue() {
        return dflt_;
    }

    // txt is not null or empty
    public abstract T fromString( String txt ) throws ConfigException;

    // value is not null
    public abstract String toString( T value );

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
