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

import org.ghost4j.AbstractComponent;
import org.ghost4j.document.Document;
import org.ghost4j.document.DocumentException;

/**
 * Abstract modifier implementation. Contains methods that are common to the
 * different modifier types
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractModifier extends AbstractComponent implements
	Modifier {

    public Document modify(Document source, Map<String, Serializable> parameters)
	    throws ModifierException, DocumentException, IOException {

	// perform actual processing
	return run(source, parameters);
    }

    protected abstract Document run(Document source,
	    Map<String, Serializable> parameters) throws ModifierException,
	    DocumentException;
}
