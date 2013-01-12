/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package org.ghost4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Class used to wrap Ghostscript interpreter log messages in Log4J messages.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class GhostscriptLoggerOutputStream extends OutputStream {

    /**
     * Logger name.
     */
    private static final String LOGGER_NAME = Ghostscript.class.getName();

    /**
     * Line termination for a log message.
     */
    private static final int LINE_END = (int) '\n';

    /**
     * ByteArrayOutputStream used to store outputed messages being written.
     */
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();

    /**
     * Log4J logger used to log messages.
     */
    private Logger logger;

    /**
     * Log level used when outputing messages to the Log4J logger.
     */
    private Level level;

    /**
     * Constructor.
     * 
     * @param level
     *            Defines the log level of outputed messages.
     */
    public GhostscriptLoggerOutputStream(Level level) {

	logger = Logger.getLogger(LOGGER_NAME);
	baos = new ByteArrayOutputStream();
	this.level = level;
    }

    /**
     * Write method that stores data to write in the ByteArrayOutputStream and
     * sends messages to the Log4J logger when a line ends.
     * 
     * @param b
     *            Byte to write
     * @throws IOException
     */
    public void write(int b) throws IOException {

	if (b == LINE_END) {
	    logger.log(level, baos.toString());
	    baos.reset();
	} else {
	    baos.write(b);
	}
    }
}
