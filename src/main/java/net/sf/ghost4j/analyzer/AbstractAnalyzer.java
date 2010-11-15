/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package net.sf.ghost4j.analyzer;

import java.io.IOException;
import java.util.List;

import net.sf.ghost4j.component.AbstractComponent;
import net.sf.ghost4j.component.DocumentNotSupported;
import net.sf.ghost4j.document.Document;

/**
 * Abstract analyzer implementation.
 * Contains methods that are common to the different analyzer types
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractAnalyzer extends AbstractComponent implements Analyzer {

	public List<AnalysisItem> analyze(Document document) throws IOException,
			AnalyzerException, DocumentNotSupported {
		
		//perform actual processing
		return run(document);
	}
	
	public abstract List<AnalysisItem> run(Document document) throws IOException, AnalyzerException, DocumentNotSupported;

}
