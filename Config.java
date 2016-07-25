package uk.ac.bristol.star.deadman;

public class Config {

    public static final ConfigKey<Integer> RESET_SEC;
    public static final ConfigKey<Integer> WARNING_SEC;
    public static final ConfigKey<Boolean> ONTOP;
    public static final ConfigKey<String[]> EMAILS;
    public static final ConfigKey<Boolean> AUDIO;

    public static final ConfigKey<?>[] KEYS = {
        RESET_SEC = new IntegerConfigKey( "reset", 30 * 60 ),
        WARNING_SEC = new IntegerConfigKey( "warning", 3 * 60 ),
        ONTOP = new BooleanConfigKey( "alwaysOnTop", true ),
        EMAILS = new StringsConfigKey( "emails", ',', new String[ 0 ] ),
        AUDIO = new BooleanConfigKey( "audio", true ),
    };

    private static class IntegerConfigKey extends ConfigKey<Integer> {
        IntegerConfigKey( String name, int dflt ) {
            super( name, Integer.class, new Integer( dflt ) );
        }
        public Integer fromString( String txt ) throws ConfigException {
            try {
                return Integer.parseInt( txt );
            }
            catch ( NumberFormatException e ) {
                throw new ConfigException( "Not an integer" );
            }
        }
        public String toString( Integer val ) {
            return val.toString();
        }
    }

    private static class BooleanConfigKey extends ConfigKey<Boolean> {
        BooleanConfigKey( String name, boolean dflt ) {
            super( name, Boolean.class, Boolean.valueOf( dflt ) );
        }
        public Boolean fromString( String txt ) throws ConfigException {
            if ( txt.equalsIgnoreCase( "true" ) ||
                 txt.equalsIgnoreCase( "t" ) ) {
                return Boolean.TRUE;
            }
            else if ( txt.equalsIgnoreCase( "false" ) ||
                      txt.equalsIgnoreCase( "f" ) ) {
                return Boolean.FALSE;
            }
            else {
                throw new ConfigException( "Not t[rue]/f[alse]" );
            }
        }
        public String toString( Boolean value ) {
            return Boolean.TRUE.equals( value ) ? "true" : "false";
        }
    }

    private static class StringsConfigKey extends ConfigKey<String[]> {
        private final char sepChar_;
        StringsConfigKey( String name, char sepChar, String[] dflt ) {
            super( name, String[].class, dflt );
            sepChar_ = sepChar;
        }
        public String[] fromString( String txt ) {
            return txt.split( "\\Q" + sepChar_ + "\\E" );
        }
        public String toString( String[] value ) {
            StringBuffer sbuf = new StringBuffer();
            for ( int i = 0; i < value.length; i++ ) {
                if ( i > 0 ) {
                    sbuf.append( sepChar_ );
                }
                sbuf.append( value[ i ] );
            }
            return sbuf.toString();
        }
    }
}
