/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 * 
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html. 
 */
package net.sf.ghost4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import junit.framework.TestCase;
import net.sf.ghost4j.display.DisplayCallback;
import net.sf.ghost4j.display.ImageWriterDisplayCallback;

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
            gs.runString("devicenames ==");
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

    /**
     * Test of runFile method, of class Ghostscript.
     */
    public void testRunFile() {

        System.out.println("Test runFile");

        Ghostscript gs = Ghostscript.getInstance();

        //initialize
        try {
            String[] args = new String[4];
            args[0] = "-dQUIET";
            args[1] = "-dNOPAUSE";
            args[2] = "-dBATCH";
            args[3] = "-dSAFER";
            gs.initialize(args);
        } catch (GhostscriptException e) {
            fail(e.getMessage());
        }

        //run file
        try {
            gs.runFile("input.ps");
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

    /**
     * Test Ghostscript standard input.
     */
    public void testStdIn() {

        System.out.println("Test stdIn");

        Ghostscript gs = Ghostscript.getInstance();

        InputStream is = null;

        //initialize
        try {

            is = new FileInputStream("input.ps");

            gs.setStdIn(is);

            String[] args = new String[6];
            args[0] = "-dQUIET";
            args[1] = "-dNOPAUSE";
            args[2] = "-dBATCH";
            args[3] = "-sOutputFile=%stdout";
            args[4] = "-f";
            args[5] = "-";

            gs.initialize(args);

            is.close();

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test Ghostscript standard output.
     */
    public void testStdOut() {

        System.out.println("Test stdOut");

        Ghostscript gs = Ghostscript.getInstance();

        InputStream is = null;
        ByteArrayOutputStream os = null;

        //initialize
        try {

            //input
            is = new ByteArrayInputStream(new String("devicenames ==\n").getBytes());
            gs.setStdIn(is);

            //output
            os = new ByteArrayOutputStream();
            gs.setStdOut(os);

            String[] args = new String[3];
            args[0] = "-sOutputFile=%stdout";
            args[1] = "-f";
            args[2] = "-";

            gs.initialize(args);

            assertTrue(os.toString().length() > 0);

            os.close();
            is.close();

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test Ghostscript standard error output.
     */
    public void testStdErr() {

        System.out.println("Test stdErr");

        Ghostscript gs = Ghostscript.getInstance();

        InputStream is = null;
        ByteArrayOutputStream os = null;

        //initialize
        try {

            //input
            is = new ByteArrayInputStream(new String("stupid\n").getBytes());
            gs.setStdIn(is);

            //output
            os = new ByteArrayOutputStream();
            gs.setStdErr(os);

            String[] args = new String[3];
            args[0] = "-sOutputFile=%stdout";
            args[1] = "-f";
            args[2] = "-";

            gs.initialize(args);

            is.close();

        } catch (Exception e) {
            //do not notice error because we want to test error output
            if (!e.getMessage().contains("Error code is -100")) {
                fail(e.getMessage());
            }
        } finally {
            try {
                assertTrue(os.toString().length() > 0);
                os.close();
            } catch (IOException e2) {
                fail(e2.getMessage());
            }
        }
    }

    /**
     * Test Ghostscript set with custom display.
     */
    public void testDisplayCallback() {

        System.out.println("Test displayCallback");

        Ghostscript gs = Ghostscript.getInstance();

        try {

            //create display callback
            ImageWriterDisplayCallback displayCallback = new ImageWriterDisplayCallback();

            //set display callback
            gs.setDisplayCallback(displayCallback);

            String[] args = new String[7];
            args[0] = "-dQUIET";
            args[1] = "-dNOPAUSE";
            args[2] = "-dBATCH";
            args[3] = "-dSAFER";
            args[4] = "-sDEVICE=display";
            args[5] = "-dDisplayHandle=0";
            args[6] = "-dDisplayFormat=16#804";


            gs.initialize(args);

            gs.runFile("input.ps");

            gs.exit();

            assertEquals(1, displayCallback.getImages().size());


        } catch (Exception e) {
            fail(e.getMessage());
        }


    }
}
