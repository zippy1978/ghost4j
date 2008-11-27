/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 * 
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html. 
 */
package net.sf.ghost4j;

import junit.framework.TestCase;

/**
 * GhostscriptLibrary tests.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class GhostscriptTest extends TestCase {

    public GhostscriptTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        
    }

    protected void tearDown() throws Exception {
        super.tearDown();

        //delete loaded Ghostscript instance after each test
        Ghostscript.deleteInstance();
    }

    /**
     * Test of getRevision method, of class Ghostscript.
     */
    public void testGetRevision() {

        System.out.println("Test getRevision");

        GhostscriptRevision revision = Ghostscript.getRevision();

        assertNotNull(revision.getProduct());
        assertNotNull(revision.getCopyright());
        assertNotNull(revision.getRevisionDate());
        assertNotNull(revision.getNumber());

    }

    /**
     * Test of initialize method, of class Ghostscript.
     */
    public void testInitialize() {

        System.out.println("Test initialize");

        Ghostscript gs = Ghostscript.getInstance();

        try {
            gs.initialize(null);
        } catch (GhostscriptException e) {
            fail(e.getMessage());
        }

    }

    /**
     * Test of exit method, of class Ghostscript.
     */
    public void testExit() {

        System.out.println("Test exit");

        Ghostscript gs = Ghostscript.getInstance();

        //initialize
        try {
            gs.initialize(null);
        } catch (GhostscriptException e) {
            fail(e.getMessage());
        }

        //exit
        try {
            gs.exit();
        } catch (GhostscriptException e) {
            fail(e.getMessage());
        }

    }

    public void testRunString() {

        System.out.println("Test runString");

        Ghostscript gs = Ghostscript.getInstance();
        
        //initialize
        try {
            gs.initialize(null);
        } catch (GhostscriptException e) {
            fail(e.getMessage());
        }

        //run string
        try {
            gs.runString("devicenames ==\n");
        } catch (GhostscriptException e) {
            fail(e.getMessage());
        }

        //exit
        try {
            gs.exit();
        } catch (GhostscriptException e) {
            fail(e.getMessage());
        }

    }
}
