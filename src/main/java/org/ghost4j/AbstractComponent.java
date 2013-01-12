/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package org.ghost4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.ghost4j.document.Document;
import org.ghost4j.document.DocumentException;

/**
 * Abstract component implementation. Contains methods that are common to the
 * different component types (converter, analyzer, modifier ...)
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractComponent implements Component {

    /**
     * Classes of Document supported by the converter.
     */
    protected Class<?>[] supportedDocumentClasses;

    /**
     * Assert a given document instance is supported by the converter
     * 
     * @param document
     * @throws DocumentException
     *             When document is not supported
     */
    protected void assertDocumentSupported(Document document)
	    throws DocumentException {

	if (supportedDocumentClasses != null) {

	    for (Class<?> clazz : supportedDocumentClasses) {
		if (clazz.getName().equals(document.getClass().getName())) {
		    // supported
		    return;
		}
	    }

	    // document not supported
	    throw new DocumentException("Documents of class "
		    + document.getClass().getName()
		    + " are not supported by the converter");
	}
    }

    public void copySettings(Map<String, Object> settings)
	    throws IllegalAccessException, InvocationTargetException {

	if (settings.get("maxProcessCount") != null) {
	    settings.remove("maxProcessCount");
	}

	BeanUtils.populate(this, settings);

    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> extractSettings() throws IllegalAccessException,
	    InvocationTargetException, NoSuchMethodException {

	Map<String, Object> result = PropertyUtils.describe(this);

	if (result.get("maxProcessCount") != null) {
	    result.remove("maxProcessCount");
	}

	return result;
    }

}
