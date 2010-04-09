/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package net.sf.ghost4j.converter;

import net.sf.ghost4j.converter.ConverterException;
import net.sf.ghost4j.converter.PDFConverter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import junit.framework.TestCase;
import net.sf.ghost4j.document.Document;
import net.sf.ghost4j.document.PDFDocument;
import net.sf.ghost4j.document.PSDocument;

/**
 * PDFConverter tests.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PDFConverterTest extends TestCase {
    
    public PDFConverterTest(String testName) {
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

    public void testConvertWithPS() throws Exception {

        PSDocument document = new PSDocument();
        document.load(new File("input.ps"));


        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PDFConverter converter = new PDFConverter();
        converter.convert(document, baos);

        assertTrue(baos.size() > 0);

        baos.close();
    }

    public void testConvertWithUnsupportedDocument() throws Exception {

        PDFDocument document = new PDFDocument();
        document.load(new File("input.pdf"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PDFConverter converter = new PDFConverter();
        try{
            converter.convert(document, baos);
            fail("Test failed");
        } catch(ConverterException e){
            assertTrue(e.getMessage().startsWith("Documents of class"));
        }

        baos.close();
    }

}
