package uk.ac.bristol.star.deadman;

import java.util.Date;

/**
 * Panel that requires user initialisation information to be filled in.
 *
 * @author   Mark Taylor
 * @since    27 Jul 2016
 */
public class InitPanel extends FormPanel {

    private final Mailer mailer_;
    private final Runnable initCallback_;

    private static final ConfigKey<String> USER_NAME;
    private static final ConfigKey<String> USER_PHONE;
    private static final ConfigKey<String> USER_COMMENTS;

    private static final ConfigKey<?>[] USER_KEYS = {
        USER_NAME = DmConfig.createStringKey( "name", "" ),
        USER_PHONE = DmConfig.createStringKey( "phone", "" ),
        USER_COMMENTS = DmConfig.createStringKey( "comments", "" ),
    };

    /**
     * Constructor.
     *
     * @param   mailer   if not null, will be used to send a message if
     *                   initialisation is completed successfully
     * @param   initCallback  will be invoked if initialisation is
     *                        completed successfully
     */
    public InitPanel( Mailer mailer, Runnable initCallback ) {
        super( USER_KEYS, "Start" );
        mailer_ = mailer;
        initCallback_ = initCallback;
    }

    protected boolean consumeConfig( ConfigMap cmap ) {
        if ( isFilled( cmap ) ) {
            if ( mailer_ != null ) {
                sendEmail( mailer_, cmap );
            }
            initCallback_.run();
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Indicates whether the supplied config map is sufficiently complete
     * to permit initialisation.
     *
     * @param  cmap  config map
     * @return   true iff map contains sufficient inialialisation information
     */
    private static boolean isFilled( ConfigMap cmap ) {
        String userName = cmap.get( USER_NAME );
        String userPhone = cmap.get( USER_PHONE );
        if ( userName != null && userName.trim().length() > 0 &&
             userPhone != null && userPhone.trim().length() > 0 ) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Sends an email given a config map.
     *
     * @param  mailer  can send emails, not null
     * @param  cmap   user configuration info
     */
    private static void sendEmail( Mailer mailer, ConfigMap cmap ) {
        String topic = "Startup by " + cmap.get( USER_NAME );
        StringBuffer sbuf = new StringBuffer()
            .append( "Deadman application started at " )
            .append( new Date() )
            .append( "\n" )
            .append( "\nUser info:\n" );
        for ( ConfigKey<?> key : USER_KEYS ) {
            sbuf.append( "\n   " )
                .append( key.getName() )
                .append( ":\n      " )
                .append( cmap.getString( key ) )
                .append( "\n" );
        }
        String body = sbuf.toString();
        mailer.scheduleSendMessage( topic, body );
    }
}
