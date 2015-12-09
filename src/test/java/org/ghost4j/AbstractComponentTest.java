/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 * 
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html. 
 */

package org.ghost4j;

import junit.framework.TestCase;

/**
 * AbstractComponent tests.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class AbstractComponentTest extends TestCase {

    public AbstractComponentTest(String testName) {
	super(testName);

    }

    @Override
    protected void setUp() throws Exception {
	super.setUp();

    }

    @Override
    protected void tearDown() throws Exception {
	super.tearDown();
    }

    public void testIsDeviceSupported() throws Exception {

	AbstractComponent component = new AbstractComponent() {
	};

	// pdfwrite should be available in every Ghostscript version
	assertTrue(component.isDeviceSupported("pdfwrite"));
    }

    public void testIsDeviceSupportedWithNonExistingDevice() throws Exception {

	AbstractComponent component = new AbstractComponent() {
	};

	assertFalse(component.isDeviceSupported("nonexistingdevice"));
    }
}
