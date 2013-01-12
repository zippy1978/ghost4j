/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package org.ghost4j;

import com.sun.jna.Native;
import com.sun.jna.Platform;

/**
 * Native Ghostscript API loader.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class GhostscriptLibraryLoader {

    /**
     * Load native library according to host OS.
     * 
     * @return The loaded library.
     */
    protected static GhostscriptLibrary loadLibrary() {

	// library name
	String libName = "gs";

	// on Windows: library has a different name according to the
	// architecture
	if (Platform.isWindows()) {
	    // architecture
	    String arch = System.getProperty("sun.arch.data.model");

	    libName = "gsdll" + arch;

	}

	return (GhostscriptLibrary) Native.loadLibrary(libName,
		GhostscriptLibrary.class);
    }
}
