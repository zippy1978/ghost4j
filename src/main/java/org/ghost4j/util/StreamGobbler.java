/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package org.ghost4j.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Utility class used to read a stream and write it to another stream
 * (redirection).
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class StreamGobbler extends Thread {

    /**
     * Input stream to read.
     */
    InputStream inputStream;
    /**
     * Output stream to write.
     */
    OutputStream outputStream;

    public StreamGobbler(InputStream inputStream, OutputStream outputStream) {

	this.inputStream = inputStream;
	this.outputStream = outputStream;
    }

    @Override
    public void run() {

	try {

	    PrintWriter printWriter = null;
	    if (outputStream != null) {
		printWriter = new PrintWriter(outputStream);
	    }

	    InputStreamReader inputStreamReader = new InputStreamReader(
		    inputStream);
	    BufferedReader bufferedReader = new BufferedReader(
		    inputStreamReader);
	    String line = null;
	    while ((line = bufferedReader.readLine()) != null) {
		if (printWriter != null) {
		    printWriter.println(line);
		}
	    }

	    if (printWriter != null) {
		printWriter.flush();
	    }

	} catch (IOException e) {

	    // nothing

	}

    }
}
