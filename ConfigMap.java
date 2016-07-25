package uk.ac.bristol.star.deadman;

import java.util.Map;
import java.util.LinkedHashMap;

public class ConfigMap {

    private final Map<ConfigKey<?>,Object> map_;

    public ConfigMap() {
        map_ = new LinkedHashMap<ConfigKey<?>,Object>();
    }

    public <T> void put( ConfigKey<T> key, T value ) {
        map_.put( key, value );
    }

    public <T> T get( ConfigKey<T> key ) {
        return map_.containsKey( key )
             ? key.getValueClass().cast( map_.get( key ) )
             : key.getDefaultValue();
    }

    public void assign( String name, String value, ConfigKey<?>[] knownKeys )
            throws ConfigException {
        for ( ConfigKey<?> key : knownKeys ) {
            if ( name.equalsIgnoreCase( key.getName() ) ) {
                putString( key, value );
                return;
            }
        }
        throw new ConfigException( "No known key \"" + name + "\"" );
    }

    private <T> void putString( ConfigKey<T> key, String txtValue )
            throws ConfigException {
        put( key, key.fromString( txtValue ) );
    }
}
