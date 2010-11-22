/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package net.sf.ghost4j.analyzer;

import java.io.IOException;
import java.util.List;

import net.sf.ghost4j.document.Document;
import net.sf.ghost4j.document.DocumentException;

/**
 * Interface defining a remote analyzer (for Ghostscript multi process support).
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public interface RemoteAnalyzer extends Analyzer{
	
	/**
     * Sets max parallel analysis processes allowed for the analyzer
     * @param maxProcessCount
     */
    public void setMaxProcessCount(int maxProcessCount);

    /**
     * Analyzes a document and return results as a list of AnalysisItem objects.
     * @param document Document to analyze
     * @return Analysis results as a list of AnalysisItem objects
     * @throws IOException
     * @throws AnalyzerException
     * @throws DocumentException
     */
    public List<AnalysisItem> remoteAnalyze(Document document) throws IOException, AnalyzerException, DocumentException;

    /**
     * Clones settings of an analyzer to another one.
     * This operation is allowed only when analyzers (source and target) are instances of the same class.
     * @param remoteAnalyzer
     */
    public void cloneSettings(RemoteAnalyzer remoteAnalyzer);

}
