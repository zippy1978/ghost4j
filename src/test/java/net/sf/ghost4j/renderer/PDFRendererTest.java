/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package net.sf.ghost4j.renderer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import junit.framework.TestCase;
import net.sf.ghost4j.document.Document;
import net.sf.ghost4j.document.PDFDocument;
import net.sf.ghost4j.document.PSDocument;

/**
 * PDFRenderer tests.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PDFRendererTest extends TestCase {
    
    public PDFRendererTest(String testName) {
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

    public void testRenderWithPS() throws Exception {

        PSDocument document = new PSDocument();
        document.load(new File("input.ps"));


        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PDFRenderer renderer = new PDFRenderer();
        renderer.render(document, baos);

        assertTrue(baos.size() > 0);

        baos.close();
    }

    public void testRenderWithUnsupportedDocument() throws Exception {

        PDFDocument document = new PDFDocument();
        document.load(new File("input.pdf"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PDFRenderer renderer = new PDFRenderer();
        try{
            renderer.render(document, baos);
            fail("Test failed");
        } catch(RenderException e){
            assertTrue(e.getMessage().startsWith("Documents of class"));
        }

        baos.close();
    }

}
