package uk.ac.bristol.star.deadman;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Properties;

/**
 * Stores typed values by name.
 *
 * @author   Mark Taylor
 * @since    25 July 2016
 */
public class ConfigMap {

    private final Map<ConfigKey<?>,Object> map_;

    /**
     * Constructor.
     */
    public ConfigMap() {
        map_ = new LinkedHashMap<ConfigKey<?>,Object>();
    }

    /**
     * Puts a value into this map.
     *
     * @param  key  key
     * @param  value   value
     */
    public <T> void put( ConfigKey<T> key, T value ) {
        map_.put( key, value );
    }

    /**
     * Retrieves a value from this map.
     * If no entry has been put, the key's default value is returned.
     *
     * @param   key  key
     * @return  value 
     */
    public <T> T get( ConfigKey<T> key ) {
        return map_.containsKey( key )
             ? key.getValueClass().cast( map_.get( key ) )
             : key.getDefaultValue();
    }

    /**
     * Attempts to put a value into the map given a string-string name-value
     * pair.  If the name does not correspond to any of the supplied list
     * of keys, a ConfigException is thrown.
     *
     * @param  name  key name
     * @param  value   string representation of value
     * @param  knownKeys   all permitted keys
     * @throws   ConfigException  if no key with the right name is present
     *           in the supplied key list, or if value can't be
     *           interpreted by the key
     */
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

    /**
     * Adds entries to this map read from string-valued entries
     * of a properties object.
     *
     * @param  props  properties object
     * @param  knownKeys   all permitted keys
     * @throws   ConfigException  if no key with the right name is present
     *           in the supplied key list, or if value can't be
     *           interpreted by the key
     */
    public void addProperties( Properties props, ConfigKey<?>[] knownKeys )
            throws ConfigException {
        for ( String propName : props.stringPropertyNames() ) {
            String propValue = props.getProperty( propName );
            assign( propName, propValue, knownKeys );
        }
    }

    /**
     * Serializes the contents of this map in a form that could be read
     * as a Properties text file.  Defaulted values are written
     * commented out.
     *
     * @param  knownKeys   all permitted keys
     * @return   list of text lines for a properties file
     * @see   java.util.Properties#load(java.io.InputStream)
     */
    public String[] getPropertyLines( ConfigKey<?>[] knownKeys ) {
        List<String> lines = new ArrayList<String>();
        for ( ConfigKey<?> key : knownKeys ) {
            final boolean isDflt;
            final String valueStr;
            if ( map_.containsKey( key ) ) {
                isDflt = false;
                valueStr = getString( key );
            }
            else {
                isDflt = true;
                valueStr= getDefaultString( key );
            }
            StringBuffer sbuf = new StringBuffer();
            if ( isDflt ) {
                sbuf.append( "# " );
            }
            sbuf.append( key.getName() )
                .append( "=" )
                .append( valueStr );
            lines.add( sbuf.toString() );
        }
        return lines.toArray( new String[ 0 ] );
    }

    /**
     * Returns the string representation of the value of a given key
     * in this map.
     *
     * @param  key  key
     * @return   string representation of key's (actual or default) value
     */
    private <T> String getString( ConfigKey<T> key ) {
        return key.toString( get( key ) );
    }

    /**
     * Sets the value of a given key by supplying its string representation.
     *
     * @param  key   key
     * @param  txtValue  string representation of value
     * @throws   ConfigException  if txtValue cannot be interpreted by key
     */
    private <T> void putString( ConfigKey<T> key, String txtValue )
            throws ConfigException {
        put( key, key.fromString( txtValue ) );
    }

    /**
     * Returns the string representation of a key's default value.
     *
     * @param  key  key
     * @return   string representation of key's default value
     */
    private static <T> String getDefaultString( ConfigKey<T> key ) {
        return key.toString( key.getDefaultValue() );
    }
}
