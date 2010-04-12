/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package net.sf.ghost4j.converter;

/**
 * Interface defining a converter with multi process support.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public interface RemoteConverter extends Converter{

    public boolean isConverting();

    public void destroy();

    public void setMaxProcessCount(int maxProcessCount);
}
