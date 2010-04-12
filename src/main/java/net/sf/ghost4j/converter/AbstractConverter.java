/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package net.sf.ghost4j.converter;

import gnu.cajo.Cajo;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.ghost4j.document.Document;
import net.sf.ghost4j.util.JavaFork;
import java.rmi.RemoteException;

/**
 * Abstract converter implementation.
 * Contains methods that are common to the different converter types
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractConverter implements Converter {

    /**
     * Classes of Document supported by the converter.
     */
    protected Class[] supportedDocumentClasses;

    /**
     * Assert a given document instance is supported by the converter
     * @param document
     * @return
     */
    protected void assertDocumentSupported(Document document) throws ConverterException {

        if (supportedDocumentClasses != null) {

            for (Class clazz : supportedDocumentClasses) {
                if (clazz.getName().equals(document.getClass().getName())) {
                    //supported
                    return;
                }
            }

            //document not supported
            throw new ConverterException("Documents of class " + document.getClass().getName() + " are not supported by the converter");
        }
    }

    public synchronized void convert(Document document, OutputStream outputStream) throws IOException, ConverterException {

        //perform actual processing
        run(document, outputStream);
     
    }

    public abstract void run(Document document, OutputStream outputStream) throws IOException, ConverterException;
}
