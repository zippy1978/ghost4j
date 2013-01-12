/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package org.ghost4j.converter;

/**
 * Interface defining a remote converter (for Ghostscript multi process
 * support).
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public interface RemoteConverter extends Converter {

    /**
     * Sets max parallel conversion processes allowed for the converter
     * 
     * @param maxProcessCount
     */
    public void setMaxProcessCount(int maxProcessCount);

}
