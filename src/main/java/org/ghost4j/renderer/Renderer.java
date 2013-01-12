/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.renderer;

import java.awt.Image;
import java.io.IOException;
import java.util.List;

import org.ghost4j.Component;
import org.ghost4j.analyzer.AnalyzerException;
import org.ghost4j.document.Document;
import org.ghost4j.document.DocumentException;

/**
 * Interface defining a renderer used to render a Document to a given format.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public interface Renderer extends Component {

    /**
     * Renders a given document an outputs result as a list of Image objects (on
     * image per page).
     * 
     * @param document
     *            Document to render. Document type may or may no be supported
     *            (support left to the render final implementation).
     * @return a List of Image objects
     * @throws IOException
     * @throws AnalyzerException
     * @throws RendererException
     */
    public List<Image> render(Document document) throws IOException,
	    RendererException, DocumentException;

    /**
     * Renders pages of a given document an outputs result as a list of Image
     * objects (on image per page).
     * 
     * @param document
     *            Document to render. Document type may or may no be supported
     *            (support left to the render final implementation).
     * @param begin
     *            Index of the first page to render
     * @param end
     *            Index of the last page to render
     * @return a List of Image objects
     * @throws IOException
     * @throws AnalyzerException
     * @throws RendererException
     */
    public List<Image> render(Document document, int begin, int end)
	    throws IOException, RendererException, DocumentException;
}
