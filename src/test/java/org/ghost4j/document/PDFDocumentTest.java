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
 * PDFDocument tests.
 * 
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

	// load document
	PDFDocument document = new PDFDocument();
	document.load(this.getClass().getClassLoader().getResourceAsStream("input.pdf"));

	// test
	assertEquals(1, document.getPageCount());
    }

    public void testLoadWrongFormat() throws Exception {

	// load document (PS when PDF expected)
	try {
	    PDFDocument document = new PDFDocument();
	    document.load(this.getClass().getClassLoader().getResourceAsStream("input.ps"));
	    fail("Test failed");
	} catch (IOException e) {
	    assertEquals("PDF document is not valid", e.getMessage());
	}
    }

    public void testExtractPages() throws Exception {

	// load document (2 pages)
	PDFDocument document = new PDFDocument();
	document.load(this.getClass().getClassLoader().getResourceAsStream("input-2pages.pdf"));

	// extract first page
	Document extracted = document.extract(1, 1);

	// test
	assertEquals(1, extracted.getPageCount());
    }

    public void testAppendPages() throws Exception {

	// load document (1 page)
	PDFDocument document = new PDFDocument();
	document.load(this.getClass().getClassLoader().getResourceAsStream("input.pdf"));

	// load second document (2 pages)
	PDFDocument document2 = new PDFDocument();
	document2.load(this.getClass().getClassLoader().getResourceAsStream("input-2pages.pdf"));

	// append
	document.append(document2);

	// test
	assertEquals(3, document.getPageCount());

    }

    public void testAppendPagesWrongFormat() throws Exception {

	// load document (2 pages)
	PDFDocument document = new PDFDocument();
	document.load(this.getClass().getClassLoader().getResourceAsStream("input-2pages.pdf"));

	// load second document but of different type (1 page)
	PSDocument document2 = new PSDocument();
	document2.load(this.getClass().getClassLoader().getResourceAsStream("input.ps"));

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
