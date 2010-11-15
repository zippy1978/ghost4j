/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package net.sf.ghost4j.component;

import net.sf.ghost4j.document.Document;

/**
 * Abstract component implementation.
 * Contains methods that are common to the different component types (converter, analyzer, modifier ...)
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractComponent {

	/**
     * Classes of Document supported by the converter.
     */
    protected Class[] supportedDocumentClasses;

    /**
     * Assert a given document instance is supported by the converter
     * @param document
     * @return
     */
    protected void assertDocumentSupported(Document document) throws DocumentNotSupported {

        if (supportedDocumentClasses != null) {

            for (Class clazz : supportedDocumentClasses) {
                if (clazz.getName().equals(document.getClass().getName())) {
                    //supported
                    return;
                }
            }

            //document not supported
            throw new DocumentNotSupported("Documents of class " + document.getClass().getName() + " are not supported by the converter");
        }
    }
    
}
