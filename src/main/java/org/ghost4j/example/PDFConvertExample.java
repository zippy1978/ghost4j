/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package org.ghost4j.example;

import org.ghost4j.Ghostscript;
import org.ghost4j.GhostscriptException;

/**
 * Example showing how to convert a Postscript file to PDF.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PDFConvertExample {

    public static void main(String[] args) {

	// get Ghostscript instance
	Ghostscript gs = Ghostscript.getInstance();

	// prepare Ghostscript interpreter parameters
	// refer to Ghostscript documentation for parameter usage
	String[] gsArgs = new String[10];
	gsArgs[0] = "-ps2pdf";
	gsArgs[1] = "-dNOPAUSE";
	gsArgs[2] = "-dBATCH";
	gsArgs[3] = "-dSAFER";
	gsArgs[4] = "-sDEVICE=pdfwrite";
	gsArgs[5] = "-sOutputFile=output.pdf";
	gsArgs[6] = "-c";
	gsArgs[7] = ".setpdfwrite";
	gsArgs[8] = "-f";
	gsArgs[9] = "input.ps";

	// execute and exit interpreter
	try {

	    gs.initialize(gsArgs);
	    gs.exit();

	} catch (GhostscriptException e) {
	    System.out.println("ERROR: " + e.getMessage());
	}
    }

}
