package uk.ac.bristol.star.deadman;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    public static final ConfigKey<String[]> EMAILS;

    /** Key for whether the audio alarm is in effect. */
    public static final ConfigKey<Boolean> AUDIO;

    /** Key giving SMTP server for emails. */
    public static final ConfigKey<String> SMTP_SERVER;

    /** Key giving From address for email messages. */
    public static final ConfigKey<String> SMTP_SENDER;

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
        EMAILS = new StringsConfigKey( "emails", ',', new String[ 0 ] ),
        AUDIO = new BooleanConfigKey( "audio", true ),
        SMTP_SERVER =
            new StringConfigKey( "smtpHost", "smtp-srv.bristol.ac.uk" ),
        SMTP_SENDER =
            new StringConfigKey( "mailSender",
                                 "Deadman <m.b.taylor@bristol.ac.uk>" ),
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
        return new StringsConfigKey( name, ',', dflt ) {
            public ConfigControl<String[]> createControl() {
                return new Add1StringsControl( this );
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
     * Config key for string array values.
     */
    private static class StringsConfigKey extends ConfigKey<String[]> {
        private final char sepChar_;

        /**
         * Constructor.
         *
         * @param  name   key name
         * @param  sepChar   separator character
         * @param  dflt   default value
         */
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
    private static class Add1StringsControl implements ConfigControl<String[]> {
        private final ConfigKey<String[]> key_;
        private final int nd_;
        private final String[] dflts_;
        private final JCheckBox[] checkBoxes_;
        private final JTextField textField_;
        private final JComponent box_;

        /**
         * Constructor.
         *
         * @param  key
         */
        public Add1StringsControl( ConfigKey<String[]> key ) {
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
                JCheckBox checkBox = new JCheckBox( dflts_[ i ] );
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
        public ConfigKey<String[]> getKey() {
            return key_;
        }
        public JComponent getComponent() {
            return box_;
        }
        public void setValue( String[] txts ) {
            List<String> txtList =
                new ArrayList<String>( Arrays.asList( txts ) );
            for ( int i = 0; i < nd_; i++ ) {
                checkBoxes_[ i ].setSelected( txtList.remove( dflts_[ i ] ) );
            }
            textField_.setText( txtList.size() > 0 ? txtList.remove( 0 )
                                                   : null );
        }
        public String[] getValue() {
            List<String> list = new ArrayList<String>();
            for ( int i = 0; i < nd_; i++ ) {
                if ( checkBoxes_[ i ].isSelected() ) {
                    list.add( dflts_[ i ] );
                }
            }
            String fieldTxt = textField_.getText();
            if ( fieldTxt != null && fieldTxt.trim().length() > 0 ) {
                list.add( fieldTxt );
            }
            return list.toArray( new String[ 0 ] );
        }
    }
}
