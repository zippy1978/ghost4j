/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package net.sf.ghost4j.component;

/**
 * Exception thrown when a component does not support a type of document
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class DocumentNotSupported extends Exception {

	public DocumentNotSupported() {
		super();
	}

	public DocumentNotSupported(String message) {
		super(message);
	}

	public DocumentNotSupported(Throwable throwable) {
		super(throwable);
	}

	public DocumentNotSupported(String message, Throwable throwable) {
		super(message, throwable);
	}

}
