package uk.ac.bristol.star.deadman;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.Handler;

/**
 * Utilities for customising the logging system.
 *
 * @author   Mark Taylor
 * @since    26 Jul 2016
 */
public class Logging {

    /**
     * Customises the logger to be not too verbose (one line per message).
     *
     * @param  logger  logger to adjust
     */
    public static void configureLogger( Logger logger ) {
        for ( Handler handler : logger.getHandlers() ) {
            handler.setFormatter( new LineFormatter() );
        }
    }

    /**
     * Returns a log Handler that writes log messages to a log file.
     *
     * @param  file  filename
     * @param   append   if true, writes at the end of the file
     * @return   new log handler
     */
    public static Handler createFileLogHandler( String file, boolean append )
            throws IOException {
        FileHandler handler = new FileHandler( file, true );
        handler.setFormatter( new LineFormatter() );
        return handler;
    }

    /**
     * One-line formatter for log messages.
     */
    private static class LineFormatter extends Formatter {
        private final DateFormat dateFormat_;

        /**
         * Constructor.
         */
        public LineFormatter() {
            dateFormat_ = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        }

        public String format( LogRecord record ) {
            return new StringBuffer()
                .append( dateFormat_.format( new Date( record.getMillis() ) ) )
                .append( " " )
                .append( record.getLevel() )
                .append( ": " )
                .append( formatMessage( record ) )
                .append( "\n" )
                .toString();
        }
    }
}
