/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.modifier;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.ghost4j.document.Document;
import org.ghost4j.document.DocumentException;

/**
 * Interface defining a modifier used to edit / modify a document.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public interface Modifier {

    /**
     * Modify a document with optional parameters
     * 
     * @param source
     *            Document to modify
     * @param parameters
     *            Modifier parameters
     * @return Modifier version of the document
     * @throws ModifierException
     */
    public Document modify(Document source, Map<String, Serializable> parameters)
	    throws ModifierException, DocumentException, IOException;
}
