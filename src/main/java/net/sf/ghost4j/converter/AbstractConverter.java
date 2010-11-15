/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package net.sf.ghost4j.converter;

import java.io.IOException;
import java.io.OutputStream;

import net.sf.ghost4j.component.AbstractComponent;
import net.sf.ghost4j.component.DocumentNotSupported;
import net.sf.ghost4j.document.Document;

/**
 * Abstract converter implementation.
 * Contains methods that are common to the different converter types
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractConverter extends AbstractComponent implements Converter {

    public void convert(Document document, OutputStream outputStream) throws IOException, ConverterException, DocumentNotSupported {

        //perform actual processing
        run(document, outputStream);
     
    }

    public abstract void run(Document document, OutputStream outputStream) throws IOException, ConverterException, DocumentNotSupported;
}
