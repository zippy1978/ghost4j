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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * Class used to wrap Ghostscript interpreter log messages in Slf4j messages.
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
	 * Logger used to log messages.
	 */
	private final Logger logger;

	/**
	 * Log level used when outputing messages to the logger.
	 */
	private final Level level;

	/**
	 * Constructor.
	 * 
	 * @param level
	 *            Defines the log level of outputed messages.
	 */
	public GhostscriptLoggerOutputStream(final Level level) {
		logger = LoggerFactory.getLogger(LOGGER_NAME);
		baos = new ByteArrayOutputStream();
		this.level = level;
	}

	/**
	 * Write method that stores data to write in the ByteArrayOutputStream and
	 * sends messages to the logger when a line ends.
	 * 
	 * @param b
	 *            Byte to write
	 * @throws IOException
	 */
	public void write(int b) throws IOException {

		if (b == LINE_END) {
			if (level == Level.INFO) {
				logger.info(baos.toString());
			} else if (level == Level.ERROR) {
				logger.error(baos.toString());
			}
			baos.reset();
		} else {
			baos.write(b);
		}
	}
}
