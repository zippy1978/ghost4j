package net.sf.ghost4j.analyzer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.sf.ghost4j.Ghostscript;
import net.sf.ghost4j.GhostscriptException;
import net.sf.ghost4j.component.DocumentNotSupported;
import net.sf.ghost4j.document.Document;
import net.sf.ghost4j.document.PDFDocument;
import net.sf.ghost4j.util.DiskStore;

/**
 * Font analyzer: analyze fonts used in a document.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 *
 */
public class FontAnalyzer extends AbstractRemoteAnalyzer {

	public FontAnalyzer() {
		
		 //set supported classes
        supportedDocumentClasses = new Class[1];
        supportedDocumentClasses[0] = PDFDocument.class;
	}
	
	/**
     * Main method used to start the analyzer in standalone 'slave mode'.
     * @param args
     * @throws AnalyzerException

     */
    public static void main(String args[]) throws AnalyzerException {

        startRemoteAnalyzer(new FontAnalyzer()) ;
    }
	
	/**
	 * @return A list of FontAnalysisItem
	 */
	@Override
	public List<AnalysisItem> run(Document document) throws IOException,
			AnalyzerException, DocumentNotSupported {
		
        //assert document is supported
        this.assertDocumentSupported(document);
		
		//support PDF documents only at the moment
		return  run((PDFDocument)document);
	}

	public void cloneSettings(RemoteAnalyzer remoteAnalyzer) {
		
		//nothing to clone here
	}
	
	private List<AnalysisItem> run(PDFDocument document) throws IOException,
	AnalyzerException {
		
		//get Ghostscript instance
        Ghostscript gs = Ghostscript.getInstance();
        
		//generate a unique diskstore key
        DiskStore diskStore = DiskStore.getInstance();
        String inputDiskStoreKey = document.toString() + String.valueOf(System.currentTimeMillis() + String.valueOf((int)(Math.random() * (1000-0))));
        
        // Write document to input file
        document.write(diskStore.addFile(inputDiskStoreKey));
        
		//prepare args
		String[] gsArgs = {"-dQUIET", "-dNOPAUSE", "-dBATCH", "-dNODISPLAY", "-sFile=" + diskStore.getFile(inputDiskStoreKey).getAbsolutePath(), "-sOutputFile=%stdout", "-f", "-"};
		
		try {
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //execute and exit interpreter
            synchronized(gs){
            
            	//load .ps script
            	InputStream is = this.getClass().getClassLoader().getResourceAsStream("net/sf/ghost4j/script/AnalyzePDFFonts.ps");
            	
                gs.setStdIn(is);
                gs.setStdOut(baos);
                gs.initialize(gsArgs);
                gs.exit();
                Ghostscript.deleteInstance();
                is.close();
            }

           //parse results from stdout
            List<AnalysisItem> result = new ArrayList<AnalysisItem>();
           String scriptResult = baos.toString();
           
           String[] lines = scriptResult.split("\n");
           boolean inResults = false;
           for (String line : lines) {
        	   
			if (line.equals("---")){
				//start of result output detected
				inResults = true;
			} else	if (inResults){
				String[] columns = line.split(" ");
				if (columns.length == 2){
					//create new font analysis item object
					FontAnalysisItem font = new FontAnalysisItem();
					font.setName(columns[0]);
					font.setEmbedded(false);
					if (columns[1].equals("EMBEDDED")){
						font.setEmbedded(true);	
					}
					result.add(font);
				}
			}
			
           }
           
           baos.close();
           return result;

        } catch (GhostscriptException e) {
        	
           throw new AnalyzerException(e);

        } finally{

            //remove temporary file
            diskStore.removeFile(inputDiskStoreKey);
        }
        
	}

}
