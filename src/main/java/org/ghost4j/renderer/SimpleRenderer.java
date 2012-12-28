/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */

package org.ghost4j.renderer;

import java.io.IOException;
import java.util.List;

import org.ghost4j.Ghostscript;
import org.ghost4j.GhostscriptException;
import org.ghost4j.display.PageRaster;
import org.ghost4j.display.PageRasterDisplayCallback;
import org.ghost4j.document.Document;
import org.ghost4j.document.DocumentException;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.document.PSDocument;
import org.ghost4j.util.DiskStore;


public class SimpleRenderer extends AbstractRemoteRenderer {

    /**
     * Define renderer output resolution in DPI (default is 75dpi).
     */
    private int resolution = 75;

    public SimpleRenderer() {

        //set supported classes
        supportedDocumentClasses = new Class[2];
        supportedDocumentClasses[0] = PDFDocument.class;
        supportedDocumentClasses[1] = PSDocument.class;
    }

    /**
     * Main method used to start the renderer in standalone 'slave mode'.
     * @param args
     * @throws RendererException
     */
    public static void main(String[] args) throws RendererException {

        startRemoteRenderer(new SimpleRenderer());
    }

    @Override
    public List<PageRaster> run(Document document, int begin, int end)
            throws IOException, RendererException, DocumentException {

        //assert document is supported
        this.assertDocumentSupported(document);

        //get Ghostscript instance
        Ghostscript gs = Ghostscript.getInstance();

        //generate a unique diskstore key for input file
        DiskStore diskStore = DiskStore.getInstance();
        String inputDiskStoreKey = document.toString() + String.valueOf(System.currentTimeMillis() + String.valueOf((int) (Math.random() * (1000 - 0))));

        //write document to input file
        document.write(diskStore.addFile(inputDiskStoreKey));

        //create display callback
        PageRasterDisplayCallback displayCallback = new PageRasterDisplayCallback();

        //prepare args
        String[] gsArgs = {
            "-dQUIET",
            "-dNOPAUSE",
            "-dBATCH",
            "-dSAFER",
            "-dFirstPage=" + (begin + 1),
            "-dLastPage=" + (end + 1),
            "-sDEVICE=display",
            "-sDisplayHandle=0",
            "-dDisplayFormat=16#804",
            "-r" + this.getResolution(),
            "-f",
            diskStore.getFile(inputDiskStoreKey).getAbsolutePath()};

        //execute and exit interpreter
        try {
            synchronized (gs) {

                //set display callback
                gs.setDisplayCallback(displayCallback);

                gs.initialize(gsArgs);
                gs.exit();

            }
        } catch (GhostscriptException e) {

            throw new RendererException(e);

        } finally {

            //delete Ghostscript instance
            try {
                Ghostscript.deleteInstance();
            } catch (GhostscriptException e) {
                throw new RendererException(e);
            }

            //remove temporary file
            diskStore.removeFile(inputDiskStoreKey);
        }

        return displayCallback.getRasters();

    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }
}
