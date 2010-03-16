/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package net.sf.ghost4j.renderer;

/**
 * Class representing a render exception.
 * This exception may be thrown while rendering a document with a Render subclass.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class RenderException extends Exception{

     public RenderException() {
        super();
    }

     public RenderException(String message) {
        super(message);
    }

    public RenderException(Throwable cause) {
        super(cause);
    }

    public RenderException(String message, Throwable cause) {
        super(message, cause);
    }
}
