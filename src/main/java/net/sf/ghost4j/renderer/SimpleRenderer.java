package net.sf.ghost4j.renderer;

import java.awt.Image;
import java.io.IOException;
import java.util.List;

import net.sf.ghost4j.Ghostscript;
import net.sf.ghost4j.GhostscriptException;
import net.sf.ghost4j.display.ImageWriterDisplayCallback;
import net.sf.ghost4j.document.Document;
import net.sf.ghost4j.document.DocumentException;
import net.sf.ghost4j.document.PDFDocument;
import net.sf.ghost4j.document.PSDocument;
import net.sf.ghost4j.util.DiskStore;

public class SimpleRenderer extends AbstractRemoteRenderer {
	
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
	public List<Image> run(Document document, int begin, int end)
			throws IOException, RendererException, DocumentException {
		
		 //assert document is supported
        this.assertDocumentSupported(document);
        
        //get Ghostscript instance
        Ghostscript gs = Ghostscript.getInstance();
        
        //generate a unique diskstore key for input file
        DiskStore diskStore = DiskStore.getInstance();
        String inputDiskStoreKey = document.toString() + String.valueOf(System.currentTimeMillis() + String.valueOf((int)(Math.random() * (1000-0))));
        
        //write document to input file
        document.write(diskStore.addFile(inputDiskStoreKey));
        
        //create display callback
        ImageWriterDisplayCallback displayCallback = new ImageWriterDisplayCallback();
        
        //prepare args
        String[] gsArgs = {
        		"-dQUIET",
        		"-dNOPAUSE",
        		"-dBATCH",
        		"-dSAFER",
        		"-dFirstPage=" + (begin + 1),
        		"-dLastPage=" + (end + 1),
        		"-sDEVICE=display",
        		"-dDisplayHandle=0",
        		"-dDisplayFormat=16#804"};
		
        //execute and exit interpreter
    	try {
	        synchronized(gs){
	    	  
	        	//set display callback
	            gs.setDisplayCallback(displayCallback);
	            
				gs.initialize(gsArgs);
	            gs.runFile(diskStore.getFile(inputDiskStoreKey).getAbsolutePath());
	            Ghostscript.deleteInstance();
	        }
		} catch (GhostscriptException e) {
			
			throw new RendererException(e);
			
		} finally {
			
			//remove temporary file
            diskStore.removeFile(inputDiskStoreKey);
		}
		
		return displayCallback.getImages();

        
	}

	@Override
	public void cloneSettings(RemoteRenderer remoteRenderer) {
		
		//nothing to clone

	}

}
