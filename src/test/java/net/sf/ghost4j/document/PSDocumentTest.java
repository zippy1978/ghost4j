/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package net.sf.ghost4j.document;

import java.io.File;
import junit.framework.TestCase;

/**
 * PSDocument tests.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PSDocumentTest extends TestCase {
    
    public PSDocumentTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getPageCount method, of class PSDocument.
     */
    public void testGetPageCount() throws Exception {
        
        System.out.println("Test getPageCount");

        //load document
        PSDocument document = new PSDocument();
        document.load(new File("input.ps"));

        //test
        assertEquals(1, document.getPageCount());
    }

}
