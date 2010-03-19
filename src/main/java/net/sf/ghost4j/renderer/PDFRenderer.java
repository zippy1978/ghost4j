/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package net.sf.ghost4j.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.sf.ghost4j.Ghostscript;
import net.sf.ghost4j.GhostscriptException;
import net.sf.ghost4j.document.Document;

/**
 * PDF renderer.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PDFRenderer extends AbstractRenderer{

    public void render(Document document, OutputStream outputStream) throws IOException, RenderException {

        //get Ghostscript instance
        Ghostscript gs = Ghostscript.getInstance();

        //prepare Ghostscript interpreter parameters
        String[] gsArgs = new String[11];
        gsArgs[0] = "-ps2pdf";
        gsArgs[1] = "-dNOPAUSE";
        gsArgs[2] = "-dBATCH";
        gsArgs[3] = "-dSAFER";
        gsArgs[4] = "-sDEVICE=pdfwrite";
        gsArgs[5] = "-sOutputFile=%stdout";
        gsArgs[6] = "-q";
        gsArgs[7] = "-c";
        gsArgs[8] = ".setpdfwrite";
        gsArgs[9] = "-f";
        gsArgs[10] = "-";

        //execute and exit interpreter
        try {

            synchronized(gs){
                InputStream is = document.getInputStream();
                gs.setStdIn(is);
                gs.setStdOut(outputStream);
                gs.initialize(gsArgs);
                gs.deleteInstance();
            }

        } catch (GhostscriptException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

    }

    
}
