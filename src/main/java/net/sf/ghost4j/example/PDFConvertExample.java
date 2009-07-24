/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package net.sf.ghost4j.example;

import net.sf.ghost4j.Ghostscript;
import net.sf.ghost4j.GhostscriptException;

/**
 * Example showing how to convert a Postscript file to PDF.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PDFConvertExample {

     public static void main(String[] args) {

        //get Ghostscript instance
        Ghostscript gs = Ghostscript.getInstance();

        //prepare Ghostscript interpreter parameters
        String[] gsArgs = new String[8];
        gsArgs[0] = "-dQUIET";
        gsArgs[1] = "-dNOPAUSE";
        gsArgs[2] = "-dBATCH";
        gsArgs[3] = "-dSAFER";
        gsArgs[4] = "-sDEVICE=pdfwrite";
        gsArgs[5] = "-sOutputFile=output.pdf";
        gsArgs[6] = "-c";
        gsArgs[7] = ".setpdfwrite";

        //execute and exit interpreter
        try {

            gs.initialize(gsArgs);
            gs.runFile("input.ps");
            gs.exit();

        } catch (GhostscriptException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
     }

}
