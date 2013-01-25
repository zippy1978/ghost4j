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
import org.ghost4j.analyzer.InkAnalyzer;
import org.ghost4j.document.PSDocument;

/**
 * Example showing how to analyze ink coverage of a PS (works with PDF as well)
 * document using the high level API.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class InkAnalyzerExample {

    public static void main(String[] args) {

	try {

	    // load PS document
	    PSDocument document = new PSDocument();
	    document.load(new File("input-2pages.ps"));

	    // create analyzer
	    InkAnalyzer analyzer = new InkAnalyzer();

	    // analyze
	    List<AnalysisItem> coverageData = analyzer.analyze(document);

	    // print result
	    for (AnalysisItem analysisItem : coverageData) {
		System.out.println(analysisItem);

	    }

	} catch (Exception e) {
	    System.out.println("ERROR: " + e.getMessage());
	}

    }

}
