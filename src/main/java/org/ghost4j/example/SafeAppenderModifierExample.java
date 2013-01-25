/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.example;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.ghost4j.document.Document;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.document.PSDocument;
import org.ghost4j.modifier.SafeAppenderModifier;

/**
 * Example showing how to append a PostScript document to a PDF document.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class SafeAppenderModifierExample {

    public static void main(String[] args) {

	try {
	    // load PS document
	    PSDocument psDocument = new PSDocument();
	    psDocument.load(new File("input.ps"));

	    // load PDF document
	    PDFDocument pdfDocument = new PDFDocument();
	    pdfDocument.load(new File("input.pdf"));

	    // prepare modifier
	    SafeAppenderModifier modifier = new SafeAppenderModifier();

	    // prepare modifier parameters
	    Map<String, Serializable> parameters = new HashMap<String, Serializable>();
	    parameters.put(SafeAppenderModifier.PARAMETER_APPEND_DOCUMENT,
		    pdfDocument);

	    // run modifier
	    Document result = modifier.modify(psDocument, parameters);

	    // write resulting document to file
	    result.write(new File("merged.ps"));

	} catch (Exception e) {
	    System.out.println("ERROR: " + e.getMessage());
	}
    }

}
