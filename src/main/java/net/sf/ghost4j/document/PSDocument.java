/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package net.sf.ghost4j.document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.xmlgraphics.ps.DSCConstants;
import org.apache.xmlgraphics.ps.dsc.DSCException;
import org.apache.xmlgraphics.ps.dsc.DSCParser;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentPages;

/**
 * Class representing a PostScript document.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PSDocument extends AbstractDocument{

    public void load(InputStream inputStream) throws IOException {

        super.load(inputStream);

        //check that the file is a PostScript
        ByteArrayInputStream bais = null;
        try {

            bais = new ByteArrayInputStream(content);

            DSCParser parser = new DSCParser(bais);
            if (parser.nextDSCComment(DSCConstants.END_COMMENTS) == null){
                throw new IOException("PostScript document is not valid");
            }


        } catch (DSCException e) {
           throw new IOException(e.getMessage());
        } finally{
            IOUtils.closeQuietly(bais);
        }
    }

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
