/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package org.ghost4j;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.ghost4j.document.Document;
import org.ghost4j.document.DocumentException;

/**
 * Abstract component implementation. Contains methods that are common to the
 * different component types (converter, analyzer, modifier ...)
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractComponent implements Component {

    /**
     * Holds available device names of the Ghostscript interperter.
     */
    private static final List<String> AVAILABLE_DEVICE_NAMES = new ArrayList<String>();

    /**
     * Classes of Document supported by the converter.
     */
    protected Class<?>[] supportedDocumentClasses;

    /**
     * Assert a given document instance is supported by the converter
     * 
     * @param document
     * @throws DocumentException
     *             When document is not supported
     */
    protected void assertDocumentSupported(Document document)
	    throws DocumentException {

	if (supportedDocumentClasses != null) {

	    for (Class<?> clazz : supportedDocumentClasses) {
		if (clazz.getName().equals(document.getClass().getName())) {
		    // supported
		    return;
		}
	    }

	    // document not supported
	    throw new DocumentException("Documents of class "
		    + document.getClass().getName()
		    + " are not supported by the component");
	}
    }

    public void copySettings(Map<String, Object> settings)
	    throws IllegalAccessException, InvocationTargetException {

	if (settings.get("maxProcessCount") != null) {
	    settings.remove("maxProcessCount");
	}

	BeanUtils.populate(this, settings);

    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> extractSettings() throws IllegalAccessException,
	    InvocationTargetException, NoSuchMethodException {

	Map<String, Object> result = PropertyUtils.describe(this);

	if (result.get("maxProcessCount") != null) {
	    result.remove("maxProcessCount");
	}

	return result;
    }

    /**
     * Checks if a given device is supported by the current Ghostscript version.
     * 
     * @param deviceName
     *            Device name
     * @return true/false
     * @throws GhostscriptException
     */
    protected synchronized boolean isDeviceSupported(String deviceName)
	    throws GhostscriptException {

	// if no device names know yet : query the interpreter
	if (AVAILABLE_DEVICE_NAMES.size() == 0) {

	    // get Ghostscript instance
	    Ghostscript gs = Ghostscript.getInstance();

	    // retrieve available devices
	    try {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		String[] gsArgs = { "-dQUIET", "-dNOPAUSE", "-dBATCH",
			"-dNODISPLAY" };

		synchronized (gs) {
		    gs.setStdOut(baos);
		    gs.initialize(gsArgs);
		    gs.runString("devicenames ==");
		    gs.exit();
		}

		// result string
		String result = new String(baos.toByteArray());
		String[] lines = result.split("\n");
		int i = 0;
		while (!lines[i].startsWith("[")) {
		    i++;
		}
		String[] deviceNames = lines[i].substring(1,
			lines[i].length() - 2).split("/");
		for (String string : deviceNames) {
		    AVAILABLE_DEVICE_NAMES.add(string.trim());
		}

	    } catch (GhostscriptException e) {
		throw e;
	    } finally {
		Ghostscript.deleteInstance();
	    }
	}

	return AVAILABLE_DEVICE_NAMES.contains(deviceName);
    }

    /**
     * Asserts a given device is supported by the current Ghostscript version.
     * 
     * @param deviceName
     *            Device name
     * @throws GhostscriptException
     *             Thrown is device is not supported, or call to the interpreter
     *             fails
     */
    protected void assertDeviceSupported(String deviceName)
	    throws GhostscriptException {

	if (!this.isDeviceSupported(deviceName)) {
	    throw new GhostscriptException(
		    "device "
			    + deviceName
			    + " is not supported by the current Ghostscript interpreter.");
	}
    }

}
