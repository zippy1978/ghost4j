/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package net.sf.ghost4j.document;

import com.lowagie.text.pdf.PdfReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author ggrousset
 */
public class PDFDocument extends AbstractDocument implements Serializable{

    public void load(InputStream inputStream) throws IOException {
        super.load(inputStream);

        //check that the file is a PDF
        ByteArrayInputStream bais = null;
        PdfReader reader = null;

        try{

            bais = new ByteArrayInputStream(content);
            reader = new PdfReader(bais);

        } catch(Exception e){
            throw new IOException("PDF document is not valid");
        }  finally{
            if (reader != null) reader.close();
            IOUtils.closeQuietly(bais);
        }
    }

    public int getPageCount() throws DocumentException {

        int pageCount = 0;

        if (content == null){
            return pageCount;
        }

        ByteArrayInputStream bais = null;
        PdfReader reader = null;

        try{

            bais = new ByteArrayInputStream(content);
            reader = new PdfReader(bais);
            pageCount = reader.getNumberOfPages();

        } catch(Exception e){
            throw new DocumentException(e);
        }  finally{
            if (reader != null) reader.close();
            IOUtils.closeQuietly(bais);
        }

        return pageCount;

    }

    

    public String getType() {
        return "PDF";
    }
}
