package uk.ac.bristol.star.deadman;

import java.awt.Toolkit;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * Provides application-specific configuration keys.
 *
 * @author   Mark Taylor
 * @since    25 Jul 2016
 */
public class DmConfig {

    /** Key for configuration properties filename. */
    public static final ConfigKey<String> CONFIG_FILE;

    /** Key for coundown time in seconds. */
    public static final ConfigKey<Integer> RESET_SEC;

    /** Key for number of seconds before countdown end that triggers warning. */
    public static final ConfigKey<Integer> WARNING_SEC;

    /** Key for whether counter window is always on top of window stack. */
    public static final ConfigKey<Boolean> ONTOP;

    /** Key giving recipient email message list. */
    public static final ConfigKey<Address[]> EMAILS;

    /** Key for whether the audio alarm is in effect. */
    public static final ConfigKey<Boolean> AUDIO;

    /** Key giving SMTP server for emails. */
    public static final ConfigKey<String> SMTP_SERVER;

    /** Key giving From address for email messages. */
    public static final ConfigKey<Address> SMTP_SENDER;

    /** Key indicating whether email contacts are required. */
    public static final ConfigKey<Boolean> REQUIRE_EMAIL;

    /**
     * Known configuration keys.
     */
    public static final ConfigKey<?>[] KEYS = {
        CONFIG_FILE = new StringConfigKey( "config", "deadman.props" ),
        RESET_SEC = new IntegerConfigKey( "reset", 30 * 60 ),
        WARNING_SEC = new IntegerConfigKey( "warning", 3 * 60 ),
        ONTOP = new BooleanConfigKey( "alwaysOnTop", true ),
        EMAILS = new AddressesConfigKey( "emails", ',', new Address[ 0 ] ),
        AUDIO = new BooleanConfigKey( "audio", true ),
        SMTP_SERVER =
            new StringConfigKey( "smtpHost", "smtp-srv.bristol.ac.uk" ),
        SMTP_SENDER =
            new AddressConfigKey(
                    "mailSender",
                    createAddress( "Deadman <astro-deadman@bristol.ac.uk>" ) ),
        REQUIRE_EMAIL = new BooleanConfigKey( "requireEmail", true ),
    };

    /**
     * Creates a string-valued key.
     *
     * @param   name  key name
     * @param   dflt  default value
     * @return  new key
     */
    public static ConfigKey<String> createStringKey( String name,
                                                     String dflt ) {
        return new StringConfigKey( name, dflt );
    }

    /**
     * Creates a string-array-valued key with a GUI control that lets you
     * add a single entry to the default list.
     *
     * @param   name  key name
     * @param   dflt  default value
     * @return  new key
     */
    public static ConfigKey<String[]> createAdd1StringsKey( String name,
                                                            String[] dflt ) {
        return new ArrayConfigKey<String>( name, String[].class, ',', dflt ) {
            public String fromStringComponent( String txt ) {
                return txt;
            }
            public String toStringComponent( String value ) {
                return value;
            }
            public ConfigControl<String[]> createControl() {
                return new Add1ArrayControl<String>( this );
            }
        };
    }

    /**
     * Creates an address-array-valued key with a GUI control that lets you
     * add a single entry to the default list.
     *
     * @param   name  key name
     * @param   dflt  default value
     * @return  new key
     */
    public static ConfigKey<Address[]>
            createAdd1AddressesKey( String name, Address[] dflt ) {
        return new ArrayConfigKey<Address>( name, Address[].class, ',', dflt ) {
            public Address fromStringComponent( String txt )
                    throws ConfigException {
                return addressFromString( txt );
            }
            public String toStringComponent( Address value ) {
                return addressToString( value );
            }
            public ConfigControl<Address[]> createControl() {
                return new Add1ArrayControl<Address>( this );
            }
        };
    }

    /**
     * Creates a boolean-valued key.
     *
     * @param   name   key name
     * @param   dflt   default value
     * @return  new key
     */
    public static ConfigKey<Boolean> createBooleanKey( String name,
                                                       boolean dflt ) {
        return new BooleanConfigKey( name, dflt );
    }

    /**
     * Config key for integer values.
     */
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
        public ConfigControl<Integer> createControl() {
            return new TextFieldControl<Integer>( this );
        }
    }

    /**
     * Config key for boolean values.
     */
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
        public ConfigControl<Boolean> createControl() {
            final JCheckBox checkBox = new JCheckBox();
            return new ConfigControl<Boolean>() {
                public ConfigKey<Boolean> getKey() {
                    return BooleanConfigKey.this;
                }
                public JComponent getComponent() {
                    return checkBox;
                }
                public void setValue( Boolean value ) {
                    checkBox.setSelected( Boolean.TRUE.equals( value ) );
                }
                public Boolean getValue() {
                    return Boolean.valueOf( checkBox.isSelected() );
                }
            };
        }
    }

    /**
     * ConfigKey for Address objects.
     */
    private static class AddressConfigKey extends ConfigKey<Address> {
        AddressConfigKey( String name, Address dflt ) {
            super( name, Address.class, dflt );
        }
        public Address fromString( String txt ) throws ConfigException {
            return addressFromString( txt );
        }
        public String toString( Address val ) {
            return addressToString( val );
        }
        public ConfigControl<Address> createControl() {
            return new TextFieldControl<Address>( this );
        }
    }

    /**
     * ConfigKey for an array of Address objects.
     */
    private static class AddressesConfigKey extends ArrayConfigKey<Address> {
        public AddressesConfigKey( String name, char sepChar, Address[] dflt ) {
            super( name, Address[].class, sepChar, dflt );
        }
        public Address fromStringComponent( String txt )
                throws ConfigException {
            return addressFromString( txt );
        }
        public String toStringComponent( Address value ) {
            return addressToString( value );
        }
        public ConfigControl<Address[]> createControl() {
            return new TextFieldControl<Address[]>( this );
        }
    }

    /**
     * Creates an address but throws a ConfigException not a checked one.
     *
     * @param   txt  RFC-822-compliant address
     * @return   Address object
     * @throws   ConfigException  if there's trouble
     */
    private static Address addressFromString( String txt )
            throws ConfigException {
        try {
            return new InternetAddress( txt, true );
        }
        catch ( AddressException e ) {
            throw new ConfigException( "Bad email address", e );
        }
    }

    /**
     * Turns an address into a string.
     *
     * @param   addr  aaddress
     * @return   string representation
     */
    private static String addressToString( Address addr ) {
        return addr == null ? null : addr.toString();
    }

    /**
     * Creates an address but throws a RuntimeException not a checked one.
     *
     * @param   addr  RFC-822-compliant address
     * @return   Address object
     * @throws   RuntimeException  if there's trouble
     */
    private static Address createAddress( String addr ) {
        try {
            return new InternetAddress( addr );
        }
        catch ( AddressException e ) {
            throw new RuntimeException( "Bad address: " + addr, e );
        }
    }

    /**
     * Config key for string values.
     */
    private static class StringConfigKey extends ConfigKey<String> {
        StringConfigKey( String name, String dflt ) {
            super( name, String.class, dflt );
        }
        public String fromString( String txt ) {
            return txt;
        }
        public String toString( String value ) {
            return value;
        }
        public ConfigControl<String> createControl() {
            return new TextFieldControl<String>( this );
        }
    }

    /**
     * Config key for an array of typed values.
     *
     * @param  <T>  component type
     */
    private static abstract class ArrayConfigKey<T> extends ConfigKey<T[]> {
        private final char sepChar_;
        private final Class<T> compClazz_;

        /**
         * Constructor.
         *
         * @param  name   key name
         * @param  clazz   class of array type
         * @param  sepChar   separator character
         * @param  dflt   default value
         */
        ArrayConfigKey( String name, Class<T[]> clazz, char sepChar,
                        T[] dflt ) {
            super( name, clazz, dflt );
            @SuppressWarnings("unchecked")
            Class<T> cc = (Class<T>) clazz.getComponentType();
            compClazz_ = cc;
            sepChar_ = sepChar;
        }

        /**
         * Converts a single string to a value of this key's component type.
         */
        public abstract T fromStringComponent( String txt )
                throws ConfigException;

        /**
         * Converts a single component value to a string.
         */
        public abstract String toStringComponent( T value );

        public T[] fromString( String txt ) throws ConfigException {
            String[] words = txt.split( "\\Q" + sepChar_ + "\\E" );
            int n = words.length;
            @SuppressWarnings("unchecked")
            T[] array = (T[]) Array.newInstance( compClazz_, n );
            for ( int i = 0; i < n; i++ ) {
                array[ i ] = fromStringComponent( words[ i ] );
            }
            return array;
        }

        public String toString( T[] value ) {
            StringBuffer sbuf = new StringBuffer();
            int n = value.length;
            for ( int i = 0; i < n; i++ ) {
                if ( i > 0 ) {
                    sbuf.append( sepChar_ );
                }
                sbuf.append( toStringComponent( value[ i ] ) );
            }
            return sbuf.toString();
        }

        public ConfigControl<T[]> createControl() {
            return new TextFieldControl<T[]>( this );
        }
    }

    /**
     * Config key for array of string values.
     */
    private static class StringsConfigKey extends ArrayConfigKey<String> {

        /**
         * Constructor.
         *
         * @param  name   key name
         * @param  sepChar   separator character
         * @param  dflt   default value
         */
        StringsConfigKey( String name, char sepChar, String[] dflt ) {
            super( name, String[].class, sepChar, dflt );
        }
        public String fromStringComponent( String txt ) {
            return txt;
        }
        public String toStringComponent( String value ) {
            return value;
        }
        public ConfigControl<String[]> createControl() {
            return new TextFieldControl<String[]>( this );
        }
    }

    /**
     * GUI Control based on a JTextField.
     */
    private static class TextFieldControl<T> implements ConfigControl<T> {
        private final ConfigKey<T> key_;
        private final JTextField textField_;

        /**
         * Constructor.
         *
         * @param  key   key
         */
        public TextFieldControl( ConfigKey<T> key ) {
            key_ = key;
            textField_ = new JTextField();
        }

        public ConfigKey<T> getKey() {
            return key_;
        }

        public JComponent getComponent() {
            return textField_;
        }

        public void setValue( T value ) {
            textField_.setText( key_.toString( value ) );
        }

        public T getValue() {
            try {
                return key_.fromString( textField_.getText() );
            }
            catch ( ConfigException e ) {
                T dflt = key_.getDefaultValue();
                Toolkit.getDefaultToolkit().beep();
                textField_.setText( key_.toString( dflt ) );
                return dflt;
            }
        }
    }

    /**
     * GUI control that gives you checkboxes for including all the
     * default values, and the option to add one new entry.
     */
    private static class Add1ArrayControl<T> implements ConfigControl<T[]> {
        private final ArrayConfigKey<T> key_;
        private final int nd_;
        private final T[] dflts_;
        private final JCheckBox[] checkBoxes_;
        private final JTextField textField_;
        private final JComponent box_;

        /**
         * Constructor.
         *
         * @param  key
         */
        public Add1ArrayControl( ArrayConfigKey<T> key ) {
            key_ = key;
            dflts_ = key.getDefaultValue();
            nd_ = dflts_.length;
            checkBoxes_ = new JCheckBox[ nd_ ];
            box_ = new Box( BoxLayout.Y_AXIS ) {
                @Override
                public void setEnabled( boolean isEnabled ) {
                    super.setEnabled( isEnabled );
                    for ( JCheckBox cb : checkBoxes_ ) {
                        cb.setEnabled( isEnabled );
                    }
                    textField_.setEnabled( isEnabled );
                }
            };
            for ( int i = 0; i < nd_; i++ ) {
                JComponent line = Box.createHorizontalBox();
                JCheckBox checkBox =
                    new JCheckBox( key_.toStringComponent( dflts_[ i ] ) );
                line.add( checkBox );
                line.add( Box.createHorizontalGlue() );
                checkBoxes_[ i ] = checkBox;
                box_.add( line );
                box_.add( Box.createVerticalStrut( 2 ) );
            }
            textField_ = new JTextField();
            box_.add( textField_ );
            box_.setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEtchedBorder(),
                    BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) ) );
        }
        public ConfigKey<T[]> getKey() {
            return key_;
        }
        public JComponent getComponent() {
            return box_;
        }
        public void setValue( T[] values ) {
            List<T> valueList = new ArrayList<T>( Arrays.asList( values ) );
            for ( int i = 0; i < nd_; i++ ) {
                checkBoxes_[ i ].setSelected( valueList.remove( dflts_[ i ] ) );
            }
            textField_.setText( valueList.size() > 0
                              ? key_.toStringComponent( valueList.remove( 0 ) )
                              : null );
        }
        public T[] getValue() {
            List<T> list = new ArrayList<T>();
            for ( int i = 0; i < nd_; i++ ) {
                if ( checkBoxes_[ i ].isSelected() ) {
                    list.add( dflts_[ i ] );
                }
            }
            String fieldTxt = textField_.getText();
            if ( fieldTxt != null && fieldTxt.trim().length() > 0 ) {
                try {
                    list.add( key_.fromStringComponent( fieldTxt ) );
                }
                catch ( ConfigException e ) {
                    Toolkit.getDefaultToolkit().beep();
                    textField_.setText( null );
                }
            }
            @SuppressWarnings("unchecked")
            T[] array0 = (T[]) Array.newInstance( key_.compClazz_, 0 );
            return list.toArray( array0 );
        }
    }
}
