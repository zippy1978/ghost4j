/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package org.ghost4j.converter;

import java.io.IOException;
import java.io.OutputStream;

import org.ghost4j.AbstractComponent;
import org.ghost4j.document.Document;
import org.ghost4j.document.DocumentException;

/**
 * Abstract converter implementation. Contains methods that are common to the
 * different converter types
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractConverter extends AbstractComponent implements
	Converter {

    public void convert(Document document, OutputStream outputStream)
	    throws IOException, ConverterException, DocumentException {

	// perform actual processing
	run(document, outputStream);

    }

    protected abstract void run(Document document, OutputStream outputStream)
	    throws IOException, ConverterException, DocumentException;
}
