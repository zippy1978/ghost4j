/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 * 
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html. 
 */
package net.sf.ghost4j;

import com.sun.jna.ptr.IntByReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Class representing the Ghostscript interpreter.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class Ghostscript {

    /**
     * Holds Ghostscript instance.
     */
    private static GhostscriptLibrary.gs_main_instance.ByReference instanceByRef;

    /**
     * Singleton factory method for getting a Ghostscript,interpreter instance. Only called from class itself.
     * @return Ghostscript instance.
     * @throws net.sf.ghost4j.GhostscriptException
     */
    private synchronized GhostscriptLibrary.gs_main_instance.ByReference getInstanceByRef() throws GhostscriptException {

        if (instanceByRef == null) {

            System.out.println("--- NEW INSTANCE");
            
            //prepare instance
            instanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();
            //create instance
            int result = GhostscriptLibrary.instance.gsapi_new_instance(instanceByRef.getPointer(), null);

            //test result
            if (result != 0) {
                //failure
                instanceByRef = null;
                throw new GhostscriptException("Cannot get Ghostscript interpreter instance. Error code is " + result);
            }
        }
        
        System.out.println("--- GET INSTANCE");

        return instanceByRef;
    }

    /**
     * Gets Ghostscript revision data.
     * @return Revision data.
     */
    public static GhostscriptRevision getRevision() {

        //prepare revision structure and call revision function
        GhostscriptLibrary.gsapi_revision_s revision = new GhostscriptLibrary.gsapi_revision_s();
        GhostscriptLibrary.instance.gsapi_revision(revision, revision.size());

        GhostscriptRevision result = new GhostscriptRevision();
        result.setProduct(revision.product);
        result.setCopyright(revision.copyright);
        result.setNumber(new Float(revision.revision.floatValue() / 100).toString());
        //parse revision date
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            result.setRevisionDate(sdf.parse(revision.revisiondate.toString()));
        } catch (ParseException e) {
            result.setRevisionDate(null);
        }

        return result;

    }

    /**
     * Initializes Ghostscript interpreter.
     * @param args Interpreter parameters. Use the same as Ghostscript command line arguments.
     * @throws net.sf.ghost4j.GhostscriptException
     */
    public void initialize(String[] args) throws GhostscriptException {

        int result = 0;

        if (args != null) {
            result = GhostscriptLibrary.instance.gsapi_init_with_args(getInstanceByRef().getValue(), args.length, args);
        } else {
            result = GhostscriptLibrary.instance.gsapi_init_with_args(getInstanceByRef().getValue(), 0, null);
        }

        //test result
        if (result != 0) {
            throw new GhostscriptException("Cannot initialize Ghostscript interpreter. Error code is " + result);
        }
    }
    
    /**
     * Exits Ghostscript interpreter. Must be called after initialize.
     * @throws net.sf.ghost4j.GhostscriptException
     */
    public void exit() throws GhostscriptException{
        
        if (instanceByRef == null){
            int result = GhostscriptLibrary.instance.gsapi_exit(getInstanceByRef().getValue());
            
            if (result != 0) {
            throw new GhostscriptException("Cannot exit Ghostscript interpreter. Error code is " + result);
        }
        }
    }
    
    /**
     * Sends command string to Ghostscript interpreter. Must be called after initialize method.
     * @param string Command string
     * @throws net.sf.ghost4j.GhostscriptException
     */
    public void runString(String string) throws GhostscriptException{
        
        IntByReference exitCode = new IntByReference();
        
        
        GhostscriptLibrary.instance.gsapi_run_string_begin(getInstanceByRef().getValue(), 0, exitCode);
          
        //test exit code
        if (exitCode.getValue() !=0){
            throw new GhostscriptException("Cannot run command on Ghostscript interpreter. gsapi_run_string_begin failed with error code " + exitCode.getValue());
        }
        
        //TODO must split string if too long here !!!!
        GhostscriptLibrary.instance.gsapi_run_string_continue(getInstanceByRef().getValue(), string, string.length(),0, exitCode);
       
        //test exit code
        if (exitCode.getValue() !=0){
            throw new GhostscriptException("Cannot run command on Ghostscript interpreter. gsapi_run_string_continue failed with error code " + exitCode.getValue());
        }
        
        GhostscriptLibrary.instance.gsapi_run_string_end(getInstanceByRef().getValue(), 0, exitCode);
        
        //test exit code
        if (exitCode.getValue() !=0){
            throw new GhostscriptException("Cannot run command on Ghostscript interpreter. gsapi_run_string_end failed with error code " + exitCode.getValue());
        }
        
        
    }
    
    public void deleteInstance() throws GhostscriptException{
        
         System.out.println("--- DELETE INSTANCE");
        if (instanceByRef != null) {
            exit();
            GhostscriptLibrary.instance.gsapi_delete_instance(instanceByRef.getValue());
            instanceByRef = null;
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();

        deleteInstance();

    }

}
