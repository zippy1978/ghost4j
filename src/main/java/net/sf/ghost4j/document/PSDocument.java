/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package net.sf.ghost4j.document;

import java.io.ByteArrayInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.xmlgraphics.ps.DSCConstants;
import org.apache.xmlgraphics.ps.dsc.DSCParser;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentPages;

/**
 * Class representing a PostScript document.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PSDocument extends AbstractDocument{

    public int getPageCount() throws DocumentException{

        int pageCount = 0;

        if (content == null){
            return pageCount;
        }

        ByteArrayInputStream bais = null;

        try {

            //read pages from the %%Pages DSC comment

            bais = new ByteArrayInputStream(content);

            DSCParser parser = new DSCParser(bais);
            DSCCommentPages pages =  (DSCCommentPages)parser.nextDSCComment(DSCConstants.PAGES);
            pageCount = pages.getPageCount();


        } catch (Exception e) {
           throw new DocumentException(e);
        } finally{
            IOUtils.closeQuietly(bais);
        }

        return pageCount;
    }

    public String getType() {
        return "PostScript";
    }

    
}
