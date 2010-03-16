/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package net.sf.ghost4j.document;

/**
 * Class representing a document exception.
 * This exception may be thrown while handling a subclass of Document interface.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class DocumentException extends Exception{

    public DocumentException() {
        super();
    }

     public DocumentException(String message) {
        super(message);
    }

    public DocumentException(Throwable cause) {
        super(cause);
    }

    public DocumentException(String message, Throwable cause) {
        super(message, cause);
    }


}
