/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.modifier;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.ghost4j.document.Document;
import org.ghost4j.document.PSDocument;

/**
 * SafeAppenderModifier tests.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class SafeAppenderModifierTest extends TestCase {

    public SafeAppenderModifierTest(String testName) {
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

    public void testModifyWithPSAppendToPS() throws Exception {

	PSDocument source = new PSDocument();
	source.load(this.getClass().getClassLoader().getResourceAsStream("input.ps"));

	PSDocument append = new PSDocument();
	append.load(this.getClass().getClassLoader().getResourceAsStream("input.ps"));

	Map<String, Serializable> parameters = new HashMap<String, Serializable>();
	parameters.put(SafeAppenderModifier.PARAMETER_APPEND_DOCUMENT, append);

	SafeAppenderModifier modifier = new SafeAppenderModifier();
	Document result = modifier.modify(source, parameters);

	// test result
	assertEquals(2, result.getPageCount());
    }

    public void testModifyWithPSAppendToMultiProcess() throws Exception {

	final PSDocument source = new PSDocument();
	source.load(this.getClass().getClassLoader().getResourceAsStream("input.ps"));

	final PSDocument append = new PSDocument();
	append.load(this.getClass().getClassLoader().getResourceAsStream("input.ps"));

	final Map<String, Serializable> parameters = new HashMap<String, Serializable>();
	parameters.put(SafeAppenderModifier.PARAMETER_APPEND_DOCUMENT, append);

	final SafeAppenderModifier modifier = new SafeAppenderModifier();
	modifier.setMaxProcessCount(2);

	// results map
	final Map<String, Document> results = new HashMap<String, Document>();

	Thread thread1 = new Thread() {
	    @Override
	    public void run() {
		try {
		    System.out.println("START 1 " + Thread.currentThread());
		    results.put("1", modifier.modify(source, parameters));
		    System.out.println("END 1 " + Thread.currentThread());
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    };
	};
	thread1.start();

	Thread thread2 = new Thread() {
	    @Override
	    public void run() {
		try {
		    System.out.println("START 2 " + Thread.currentThread());
		    results.put("2", modifier.modify(source, parameters));
		    System.out.println("END 2 " + Thread.currentThread());
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    };
	};
	thread2.start();

	// the last one will block until a previous one finishes
	Thread thread3 = new Thread() {
	    @Override
	    public void run() {
		try {
		    System.out.println("START 3 " + Thread.currentThread());
		    results.put("3", modifier.modify(source, parameters));
		    System.out.println("END 3 " + Thread.currentThread());
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    };
	};
	thread3.start();

	thread1.join();
	thread2.join();
	thread3.join();

	assertEquals(2, results.get("1").getPageCount());
	assertEquals(2, results.get("2").getPageCount());
	assertEquals(2, results.get("3").getPageCount());
    }
}
