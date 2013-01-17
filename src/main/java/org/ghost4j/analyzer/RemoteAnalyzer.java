/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.analyzer;

/**
 * Interface defining a remote analyzer (for Ghostscript multi process support).
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public interface RemoteAnalyzer extends Analyzer {

    /**
     * Sets max parallel analysis processes allowed for the analyzer
     * 
     * @param maxProcessCount
     */
    public void setMaxProcessCount(int maxProcessCount);

}
