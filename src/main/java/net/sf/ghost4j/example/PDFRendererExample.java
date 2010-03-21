/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package net.sf.ghost4j.example;

import java.io.File;
import java.io.FileOutputStream;
import net.sf.ghost4j.document.PSDocument;
import net.sf.ghost4j.renderer.PDFRenderer;
import org.apache.commons.io.IOUtils;

/**
 * Example showing how to render a Postscript document to PDF using the high level API.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PDFRendererExample {

    public static void main(String[] args) {

        FileOutputStream fos = null;
        try{

            //load PostScript document
            PSDocument document = new PSDocument();
            document.load(new File("input.ps"));

            //create OutputStream
            fos = new FileOutputStream(new File("rendition.pdf"));

            //create renderer
            PDFRenderer renderer = new PDFRenderer();

            //set options
            renderer.setPDFSettings(PDFRenderer.OPTION_PDFSETTINGS_PREPRESS);

            //render
            renderer.render(document, fos);

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        } finally{
            IOUtils.closeQuietly(fos);
        }


    }
}
