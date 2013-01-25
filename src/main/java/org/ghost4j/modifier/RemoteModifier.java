/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package org.ghost4j.modifier;

/**
 * Interface defining a remote modifier (for Ghostscript multi process support).
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public interface RemoteModifier extends Modifier {

    /**
     * Sets max parallel rendering processes allowed for the modifier
     * 
     * @param maxProcessCount
     */
    public void setMaxProcessCount(int maxProcessCount);

}
