/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.converter;

import java.io.IOException;
import java.io.OutputStream;

import org.ghost4j.Component;
import org.ghost4j.document.Document;
import org.ghost4j.document.DocumentException;

/**
 * Interface defining a converter used to convert a Document to a given format.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public interface Converter extends Component {

    /**
     * Converts a given document and output results in provided output stream.
     * 
     * @param document
     *            Document to convert. Document type may or may no be supported
     *            (support left to the convert final implementation).
     * @param outputStream
     *            Output stream where converted document is written.
     * @throws IOException
     * @throws ConverterException
     * @throws DocumentException
     */
    public void convert(Document document, OutputStream outputStream)
	    throws IOException, ConverterException, DocumentException;

}
