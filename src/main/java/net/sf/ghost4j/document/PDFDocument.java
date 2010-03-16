/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.ghost4j.document;

import com.lowagie.text.pdf.PdfReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author ggrousset
 */
public class PDFDocument extends AbstractDocument{

    public void load(InputStream inputStream) throws IOException {
        super.load(inputStream);

        //TODO check that te file is a PDF
    }

    public int getPageCount() throws DocumentException {

        //TODO add unit test for this method

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
            reader.close();

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
