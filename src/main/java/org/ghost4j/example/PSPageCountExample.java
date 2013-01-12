/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.example;

import java.io.File;

import org.ghost4j.document.PSDocument;

/**
 * Example showing how to count pages of a PostScript.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PSPageCountExample {

    public static void main(String[] args) {
	try {

	    PSDocument psDocument = new PSDocument();
	    psDocument.load(new File("input.ps"));
	    System.out.println("Page count is : " + psDocument.getPageCount());

	} catch (Exception e) {
	    System.out.println("ERROR: " + e.getMessage());
	}
    }
}
