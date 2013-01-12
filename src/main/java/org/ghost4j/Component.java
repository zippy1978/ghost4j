/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Defines a high-level API component
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public interface Component {

    /**
     * Copy settings (object properties except for property 'maxProcessCount')
     * to the current component
     * 
     * @param settings
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void copySettings(Map<String, Object> settings)
	    throws IllegalAccessException, InvocationTargetException;

    /**
     * Extract settings (object properties except for property
     * 'maxProcessCount') of the current component
     * 
     * @return a Map of settings
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public Map<String, Object> extractSettings() throws IllegalAccessException,
	    InvocationTargetException, NoSuchMethodException;
}
