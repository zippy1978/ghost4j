/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.analyzer;

import java.io.IOException;
import java.util.List;

import org.ghost4j.Component;
import org.ghost4j.document.Document;
import org.ghost4j.document.DocumentException;

/**
 * Interface defining an analyzer used to retrieve info on a Document to a given
 * format.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public interface Analyzer extends Component {

    /**
     * Analyzes a given document and outputs result as a list of AnalysisItem
     * objects.
     * 
     * @param document
     *            Document to analyze. Document type may or may no be supported
     *            (support left to the analyze final implementation).
     * @return a List of AnalysisItem objects
     * @throws IOException
     * @throws AnalyzerException
     * @throws DocumentException
     */
    public List<AnalysisItem> analyze(Document document) throws IOException,
	    AnalyzerException, DocumentException;
}
