/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package net.sf.ghost4j.converter;

/**
 * Class representing a converter exception.
 * This exception may be thrown while converting a document with a Converter subclass.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class ConverterException extends Exception{

     public ConverterException() {
        super();
    }

     public ConverterException(String message) {
        super(message);
    }

    public ConverterException(Throwable cause) {
        super(cause);
    }

    public ConverterException(String message, Throwable cause) {
        super(message, cause);
    }
}
