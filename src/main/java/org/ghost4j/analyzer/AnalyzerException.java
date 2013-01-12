/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.analyzer;

/**
 * Class representing an analyser exception. This exception may be thrown while
 * analysing a document with an Analyser subclass.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class AnalyzerException extends Exception {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -2183524735620201412L;

    public AnalyzerException() {
	super();
    }

    public AnalyzerException(String message) {
	super(message);
    }

    public AnalyzerException(Throwable cause) {
	super(cause);
    }

    public AnalyzerException(String message, Throwable cause) {
	super(message, cause);
    }
}
