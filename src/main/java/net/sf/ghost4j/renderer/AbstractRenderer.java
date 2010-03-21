/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package net.sf.ghost4j.renderer;

import net.sf.ghost4j.document.Document;


/**
 * Abstract renderer implementation.
 * Contains methods that are common to the different renderer types
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractRenderer implements Renderer{

    /**
     * Classes of Document supported by the renderer.
     */
    protected Class[] supportedDocumentClasses;

    /**
     * Assert a given document instance is supported by the rendered
     * @param document
     * @return
     */
    protected void assertDocumentSupported(Document document) throws RenderException{

        if (supportedDocumentClasses != null){

            for(Class clazz : supportedDocumentClasses){
                if (clazz.getName().equals(document.getClass().getName())){
                    //supported
                    return;
                }
            }

            //document not supported
            throw new RenderException("Documents of class " + document.getClass().getName() + " are not supported by the renderer");
        }
    }
}
