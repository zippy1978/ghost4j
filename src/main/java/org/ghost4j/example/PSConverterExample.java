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
import org.ghost4j.converter.PSConverter;
import org.ghost4j.document.PDFDocument;

/**
 * Example showing how to convert a PDF document to PostScript using the high
 * level API.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PSConverterExample {

    public static void main(String[] args) {

	FileOutputStream fos = null;
	try {

	    // load PDF document
	    PDFDocument document = new PDFDocument();
	    document.load(new File("input.pdf"));

	    // create OutputStream
	    fos = new FileOutputStream(new File("rendition.ps"));

	    // create converter
	    PSConverter converter = new PSConverter();

	    // convert
	    converter.convert(document, fos);

	} catch (Exception e) {
	    System.out.println("ERROR: " + e.getMessage());
	} finally {
	    IOUtils.closeQuietly(fos);
	}

    }

}
