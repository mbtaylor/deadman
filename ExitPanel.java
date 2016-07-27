package uk.ac.bristol.star.deadman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FormPanel that requires user to check some boxes.
 *
 * @author   Mark Taylor
 * @since    27 Jul 2016
 */
public class ExitPanel extends FormPanel {

    private final Runnable doneCallback_;

    private static final List<ConfigKey<Boolean>> CHECK_KEYS =
            createChecklistKeys( new String[] {
        "Park telescope",
        "Dome in home position",
        "Fasten dome clamps",
        "Secure latch on hatch",
        "Switch telescope off",
        "Switch camera off",
    }, false );
    private static final ConfigKey<String> COMMENT_KEY =
        DmConfig.createStringKey( "Comment", "" );

    /**
     * Constructor.
     *
     * @param   doneCallback  called when all checkboxes have been ticked
     */
    public ExitPanel( final Runnable doneCallback ) {
        super( joinKeys( CHECK_KEYS, COMMENT_KEY ), "Exit" );
        doneCallback_ = doneCallback;
    }

    protected boolean consumeConfig( ConfigMap cmap ) {
        for ( ConfigKey<Boolean> key : CHECK_KEYS ) {
            Boolean value = cmap.get( key );
            if ( value == null || ! value.booleanValue() ) {
                return false;
            }
        }
        if ( doneCallback_ != null ) {
            doneCallback_.run();
        }
        return true;
    }

    /**
     * Constructs a list of boolean config keys, each with a given name
     * and a common default value.
     *
     * @param   names   one name for each key
     * @param   dflt   common default value
     * @return  list of keys
     */
    private static List<ConfigKey<Boolean>>
             createChecklistKeys( String[] names, boolean dflt ) {
         List<ConfigKey<Boolean>> list = new ArrayList<ConfigKey<Boolean>>();
         for ( String name : names ) {
             list.add( DmConfig.createBooleanKey( name, dflt ) );
         }
         return Collections.unmodifiableList( list );
    }

    /**
     * Joins a list and a vararg of keys to produce an array.
     *
     * @param  list  list of keys
     * @param  more  additional keys
     * @return  array containing them all
     */
    private static ConfigKey<?>[] joinKeys( List<ConfigKey<Boolean>> list,
                                            ConfigKey<?>... more ) {
         List<ConfigKey<?>> klist = new ArrayList<ConfigKey<?>>();
         for ( ConfigKey<?> k : list ) {
             klist.add( k );
         }
         for ( ConfigKey<?> k : more ) {
             klist.add( k );
         }
         return klist.toArray( new ConfigKey<?>[ 0 ] );
    }
}
