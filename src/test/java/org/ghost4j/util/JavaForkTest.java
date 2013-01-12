/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ghost4j.util;

import junit.framework.TestCase;

/**
 * 
 * @author ggrousset
 */
public class JavaForkTest extends TestCase {

    public JavaForkTest(String testName) {
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

    /**
     * Test of start method, of class JavaFork.
     */
    public void testStart() throws Exception {

	// create fork
	JavaFork fork = new JavaFork();
	fork.setRedirectStreams(true);
	fork.setWaitBeforeExiting(true);
	fork.setStartClass(ForkTest.class);

	// run
	fork.start();

    }

}
