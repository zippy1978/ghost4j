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

import org.ghost4j.AbstractComponent;
import org.ghost4j.document.Document;
import org.ghost4j.document.DocumentException;

/**
 * Abstract renderer implementation. Contains methods that are common to the
 * different renderer types
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractRenderer extends AbstractComponent implements
	Renderer {

    public List<Image> render(Document document) throws IOException,
	    RendererException, DocumentException {

	return this.render(document, 0, document.getPageCount() - 1);

    }

    public List<Image> render(Document document, int begin, int end)
	    throws IOException, RendererException, DocumentException {

	// check range
	if ((begin > end) || (end > document.getPageCount()) || (begin < 0)
		|| (end < 0)) {
	    throw new RendererException("Invalid page range");
	}

	// perform actual processing
	return this.run(document, begin, end);

    }

    protected abstract List<Image> run(Document document, int begin, int end)
	    throws IOException, RendererException, DocumentException;

}
