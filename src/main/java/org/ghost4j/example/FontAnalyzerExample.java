/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.example;

import java.io.File;
import java.util.List;

import org.ghost4j.analyzer.AnalysisItem;
import org.ghost4j.analyzer.FontAnalyzer;
import org.ghost4j.document.PDFDocument;

/**
 * Example showing how to list fonts of a PDF document using the high level API.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class FontAnalyzerExample {

    public static void main(String[] args) {

	try {

	    // load PDF document
	    PDFDocument document = new PDFDocument();
	    document.load(new File("input.pdf"));

	    // create analyzer
	    FontAnalyzer analyzer = new FontAnalyzer();

	    // analyze
	    List<AnalysisItem> fonts = analyzer.analyze(document);

	    // print result
	    for (AnalysisItem analysisItem : fonts) {
		System.out.println(analysisItem);

	    }

	} catch (Exception e) {
	    System.out.println("ERROR: " + e.getMessage());
	}

    }
}
