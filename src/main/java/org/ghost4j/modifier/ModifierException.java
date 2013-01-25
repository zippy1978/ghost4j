/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.modifier;

/**
 * Class representing a modifier exception. This exception may be thrown while
 * modifying a document with a Modifier subclass.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class ModifierException extends Exception {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 2810773454523525125L;

    public ModifierException() {
	super();
    }

    public ModifierException(String message) {
	super(message);
    }

    public ModifierException(Throwable cause) {
	super(cause);
    }

    public ModifierException(String message, Throwable cause) {
	super(message, cause);
    }
}
