/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package net.sf.ghost4j.document;

import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;

/**
 * PDFDocument tests.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PDFDocumentTest extends TestCase {
    
    public PDFDocumentTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getPageCount method, of class PDFDocument.
     */
    public void testGetPageCount() throws Exception {

        //load document
        PDFDocument document = new PDFDocument();
        document.load(new File("input.pdf"));

        //test
        assertEquals(1, document.getPageCount());
    }

     public void testLoadWrongFormat() throws Exception{

        //load document (PS when PDF expected)
        try{
            PDFDocument document = new PDFDocument();
            document.load(new File("input.ps"));
            fail("Test failed");
        } catch(IOException e){
            assertEquals("PDF document is not valid", e.getMessage());
        }
    }

}
