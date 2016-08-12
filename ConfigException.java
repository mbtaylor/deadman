package uk.ac.bristol.star.deadman;

/**
 * Exception thrown when a bad configuration value is supplied by the user.
 * Note the exception message should be comprehensible, and preferably
 * helpful, to a user of the application.
 *
 * @author   Mark Taylor
 * @since    25 July 2016
 */
public class ConfigException extends Exception {

    /**
     * Constructor.
     *
     * @param  msg   message, should be human-readable
     */
    public ConfigException( String msg ) {
        super( msg );
    }

    /**
     * Chained constructor.
     *
     * @param  msg   message, should be human-readable
     * @param  cause   underlying exception
     */
    public ConfigException( String msg, Throwable cause ) {
        super( msg, cause );
    }
}
