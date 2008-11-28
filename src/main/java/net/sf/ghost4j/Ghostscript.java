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
     * Holds Ghostscript interpreter native instance (C pointer).
     */
    private static GhostscriptLibrary.gs_main_instance.ByReference nativeInstanceByRef;
    /**
     * Holds singleton instance.
     */
    private static Ghostscript instance;

    /**
     * Singleton access method.
     * @return The singleton instance.
     */
    public static synchronized Ghostscript getInstance() {

        if (instance == null) {
            instance = new Ghostscript();
        }

        return instance;
    }

    /**
     * Private constructor.
     */
    private Ghostscript() {

    }

    /**
     * Singleton factory method for getting a Ghostscript,interpreter instance. Only called from class itself.
     * @return Ghostscript instance.
     * @throws net.sf.ghost4j.GhostscriptException
     */
    private synchronized GhostscriptLibrary.gs_main_instance.ByReference getNativeInstanceByRef() throws GhostscriptException {

        if (nativeInstanceByRef == null) {

            //prepare instance
            nativeInstanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();
            //create instance
            int result = GhostscriptLibrary.instance.gsapi_new_instance(nativeInstanceByRef.getPointer(), null);

            //test result
            if (result != 0) {
                //failure
                nativeInstanceByRef = null;
                throw new GhostscriptException("Cannot get Ghostscript interpreter instance. Error code is " + result);
            }
        }

        return nativeInstanceByRef;
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
            result = GhostscriptLibrary.instance.gsapi_init_with_args(getNativeInstanceByRef().getValue(), args.length, args);
        } else {
            result = GhostscriptLibrary.instance.gsapi_init_with_args(getNativeInstanceByRef().getValue(), 0, null);
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
    public void exit() throws GhostscriptException {

        if (nativeInstanceByRef == null) {
            int result = GhostscriptLibrary.instance.gsapi_exit(getNativeInstanceByRef().getValue());

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
    public void runString(String string) throws GhostscriptException {

        IntByReference exitCode = new IntByReference();

        GhostscriptLibrary.instance.gsapi_run_string_begin(getNativeInstanceByRef().getValue(), 0, exitCode);

        //test exit code
        if (exitCode.getValue() != 0) {
            throw new GhostscriptException("Cannot run command on Ghostscript interpreter. gsapi_run_string_begin failed with error code " + exitCode.getValue());
        }

        //split string on carriage return
        String[] slices = string.split("\n");

        for (int i = 0; i < slices.length; i++) {
            String slice = slices[i] + "\n";
            GhostscriptLibrary.instance.gsapi_run_string_continue(getNativeInstanceByRef().getValue(), slice, slice.length(), 0, exitCode);

            //test exit code
            if (exitCode.getValue() != 0) {
                throw new GhostscriptException("Cannot run command on Ghostscript interpreter. gsapi_run_string_continue failed with error code " + exitCode.getValue());
            }
        }

        GhostscriptLibrary.instance.gsapi_run_string_end(getNativeInstanceByRef().getValue(), 0, exitCode);

        //test exit code
        if (exitCode.getValue() != 0) {
            throw new GhostscriptException("Cannot run command on Ghostscript interpreter. gsapi_run_string_end failed with error code " + exitCode.getValue());
        }


    }
    
    /**
     * Sends file Ghostscript interpreter. Must be called after initialize method.
     * @param fileName File name
     * @throws net.sf.ghost4j.GhostscriptException
     */
    public void runFile(String fileName) throws GhostscriptException {
        
        
         IntByReference exitCode = new IntByReference();

        GhostscriptLibrary.instance.gsapi_run_file(getNativeInstanceByRef().getValue(), fileName, 0, exitCode);

        //test exit code
        if (exitCode.getValue() != 0) {
            throw new GhostscriptException("Cannot run file on Ghostscript interpreter. Error code " + exitCode.getValue());
        }

    }

    /**
     * Deletes the singleton instance of the Ghostscript object.
     * This ensures that the native Ghostscrit interpreter instance is deleted.
     * This method must be called if Ghostscript is not used anymore or maybe reinitialized.
     * @throws net.sf.ghost4j.GhostscriptException
     */
    public static synchronized void deleteInstance() throws GhostscriptException {

        //clear instance
        if (instance != null) {
            //exit interpreter
            instance.exit();
            //unreference singleton instance
            instance = null;
        }

        //delete native interpeter instance
        if (nativeInstanceByRef != null) {
            GhostscriptLibrary.instance.gsapi_delete_instance(nativeInstanceByRef.getValue());
            nativeInstanceByRef = null;
        }
    }
}
