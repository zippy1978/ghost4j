/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.document;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * PSDocument tests.
 * 
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

	// load document
	PSDocument document = new PSDocument();
	document.load(this.getClass().getClassLoader().getResourceAsStream("input.ps"));

	// test
	assertEquals(1, document.getPageCount());
    }

    public void testLoadWrongFormat() throws Exception {

	// load document (PDF when PS expected)
	try {
	    PSDocument document = new PSDocument();
	    document.load(this.getClass().getClassLoader().getResourceAsStream("input.pdf"));
	    fail("Test failed");
	} catch (IOException e) {
	    assertEquals("PostScript document is not valid", e.getMessage());
	}
    }

    public void testExtractPages() throws Exception {

	// load document (2 pages)
	PSDocument document = new PSDocument();
	document.load(this.getClass().getClassLoader().getResourceAsStream("input-2pages.ps"));

	// extract first page
	Document extracted = document.extract(1, 1);

	// test
	assertEquals(1, extracted.getPageCount());
    }

    public void testAppendPages() throws Exception {

	// load document (1 page)
	PSDocument document = new PSDocument();
	document.load(this.getClass().getClassLoader().getResourceAsStream("input.ps"));

	// load second document (2 pages)
	PSDocument document2 = new PSDocument();
	document2.load(this.getClass().getClassLoader().getResourceAsStream("input-2pages.ps"));

	// append
	document.append(document2);

	// test
	assertEquals(3, document.getPageCount());

    }

    public void testAppendPagesWrongFormat() throws Exception {

	// load document (2 pages)
	PSDocument document = new PSDocument();
	document.load(this.getClass().getClassLoader().getResourceAsStream("input-2pages.ps"));

	// load second document but of different type (1 page)
	PDFDocument document2 = new PDFDocument();
	document2.load(this.getClass().getClassLoader().getResourceAsStream("input.pdf"));

	// append
	try {
	    document.append(document2);
	    fail("Test failed");
	} catch (DocumentException e) {
	    assertEquals("Cannot append document of different types",
		    e.getMessage());
	}
    }

}
