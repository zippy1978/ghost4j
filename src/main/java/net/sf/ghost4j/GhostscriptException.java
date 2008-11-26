/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 * 
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html. 
 */
package net.sf.ghost4j;


/**
 * Ghostscript exception.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class GhostscriptException extends Exception{

    public GhostscriptException() {
        super();
    }
    
    public GhostscriptException(String message) {
        super(message);
    }
    
    public GhostscriptException(Throwable cause) {
        super(cause);
    }
    
    public GhostscriptException(String message, Throwable cause) {
        super(message, cause);
    }
    
    
    
}