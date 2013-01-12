/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.example;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.ghost4j.converter.PDFConverter;
import org.ghost4j.document.PSDocument;

/**
 * Example showing how to convert a Postscript document to PDF using the high
 * level API.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PDFConverterExample {

    public static void main(String[] args) {

	FileOutputStream fos = null;
	try {

	    // load PostScript document
	    PSDocument document = new PSDocument();
	    document.load(new File("input.ps"));

	    // create OutputStream
	    fos = new FileOutputStream(new File("rendition.pdf"));

	    // create converter
	    PDFConverter converter = new PDFConverter();

	    // set options
	    converter.setPDFSettings(PDFConverter.OPTION_PDFSETTINGS_PREPRESS);

	    // convert
	    converter.convert(document, fos);

	} catch (Exception e) {
	    System.out.println("ERROR: " + e.getMessage());
	} finally {
	    IOUtils.closeQuietly(fos);
	}

    }
}
