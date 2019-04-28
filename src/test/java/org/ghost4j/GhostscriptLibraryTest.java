/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 * 
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html. 
 */

package org.ghost4j;

import java.io.File;

import junit.framework.TestCase;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

/**
 * GhostscriptLibrary tests.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class GhostscriptLibraryTest extends TestCase {
    
    private final String testResourcesPath = "target/test-classes";

    private GhostscriptLibrary ghostscriptLibrary;

    public GhostscriptLibraryTest(String testName) {
	super(testName);

	// create Ghostscript lib instance
	ghostscriptLibrary = GhostscriptLibrary.instance;
    }

    protected void setUp() throws Exception {
	super.setUp();
    }

    protected void tearDown() throws Exception {
	super.tearDown();
    }

    /**
     * Test of gsapi_revision method, of class GhostscriptLibrary.
     */
    public void testGsapi_revision() {

	// prepare revision structure and call revision function
	GhostscriptLibrary.gsapi_revision_s revision = new GhostscriptLibrary.gsapi_revision_s();
	ghostscriptLibrary.gsapi_revision(revision, revision.size());

	// test result
	assertTrue(revision.product.contains("Ghostscript"));
    }

    /**
     * Test of gsapi_new_instance method, of class GhostscriptLibrary.
     */
    public void testGsapi_new_instance() {

	// create pointer to hold Ghostscript instance
	GhostscriptLibrary.gs_main_instance.ByReference instanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();

	// create instance
	int result = ghostscriptLibrary.gsapi_new_instance(
		instanceByRef.getPointer(), null);

	// delete instance
	if (result == 0) {
	    ghostscriptLibrary.gsapi_delete_instance(instanceByRef.getValue());
	}

	// test result
	assertEquals(0, result);
    }

    /**
     * Test of gsapi_exit method, of class GhostscriptLibrary.
     */
    public void testGsapi_exit() {

	// create pointer to hold Ghostscript instance
	GhostscriptLibrary.gs_main_instance.ByReference instanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();

	// create instance
	ghostscriptLibrary.gsapi_new_instance(instanceByRef.getPointer(), null);

	// enter interpreter
	int result = ghostscriptLibrary.gsapi_init_with_args(
		instanceByRef.getValue(), 0, null);

	// exit interpreter
	if (result == 0) {
	    result = ghostscriptLibrary.gsapi_exit(instanceByRef.getValue());

	    // test result
	    assertEquals(0, result);
	} else {
	    fail("Failed to initialize interpreter");
	}

	// delete instance
	ghostscriptLibrary.gsapi_delete_instance(instanceByRef.getValue());

    }

    /**
     * Test of gsapi_init_with_args method, of class GhostscriptLibrary.
     */
    public void testGsapi_init_with_args() {

	// create pointer to hold Ghostscript instance
	GhostscriptLibrary.gs_main_instance.ByReference instanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();

	// create instance
	ghostscriptLibrary.gsapi_new_instance(instanceByRef.getPointer(), null);

	// call interpreter for PS to PDF convertion
	String[] args = new String[10];
	args[0] = "ps2pdf";
	args[1] = "-dNOPAUSE";
	args[2] = "-dBATCH";
	args[3] = "-dSAFER";
	args[4] = "-sDEVICE=pdfwrite";
	args[5] = "-sOutputFile=output.pdf";
	args[6] = "-c";
	args[7] = ".setpdfwrite";
	args[8] = "-f";
        File file = new File(testResourcesPath, "input.ps");
	args[9] = file.getPath();
	int result = ghostscriptLibrary.gsapi_init_with_args(
		instanceByRef.getValue(), args.length, args);

	// exit
	ghostscriptLibrary.gsapi_exit(instanceByRef.getValue());

	// delete instance
	ghostscriptLibrary.gsapi_delete_instance(instanceByRef.getValue());

	// test
	File outputFile = new File("output.pdf");
	assertEquals(0, result);
	assertEquals(true, outputFile.exists());
	outputFile.delete();

    }

    /**
     * Test of gsapi_run_string method, of class GhostscriptLibrary.
     */
    public void testGsapi_run_string() {

	// create pointer to hold Ghostscript instance
	GhostscriptLibrary.gs_main_instance.ByReference instanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();

	// create instance
	ghostscriptLibrary.gsapi_new_instance(instanceByRef.getPointer(), null);

	// enter interpreter
	String[] args = { "-dNODISPLAY", "-dNOPAUSE", "-dBATCH", "-dSAFER" };
	ghostscriptLibrary.gsapi_init_with_args(instanceByRef.getValue(),
		args.length, args);

	// run command
	IntByReference exitCode = new IntByReference();
	ghostscriptLibrary.gsapi_run_string(instanceByRef.getValue(),
		"devicenames ==\n", 0, exitCode);
	// test result
	assertEquals(0, exitCode.getValue());

	// exit interpreter
	ghostscriptLibrary.gsapi_exit(instanceByRef.getValue());

	// delete instance
	ghostscriptLibrary.gsapi_delete_instance(instanceByRef.getValue());
    }

    /**
     * Test of gsapi_run_string_with_length method, of class GhostscriptLibrary.
     */
    public void testGsapi_run_string_with_length() {

	// create pointer to hold Ghostscript instance
	GhostscriptLibrary.gs_main_instance.ByReference instanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();

	// create instance
	ghostscriptLibrary.gsapi_new_instance(instanceByRef.getPointer(), null);

	// enter interpreter
	String[] args = { "-dNODISPLAY", "-dNOPAUSE", "-dBATCH", "-dSAFER" };
	ghostscriptLibrary.gsapi_init_with_args(instanceByRef.getValue(),
		args.length, args);

	// run command
	IntByReference exitCode = new IntByReference();
	String str = "devicenames ==\n";
	ghostscriptLibrary.gsapi_run_string_with_length(
		instanceByRef.getValue(), str, str.length(), 0, exitCode);
	// test result
	assertEquals(0, exitCode.getValue());

	// exit interpreter
	ghostscriptLibrary.gsapi_exit(instanceByRef.getValue());

	// delete instance
	ghostscriptLibrary.gsapi_delete_instance(instanceByRef.getValue());
    }

    /**
     * Test of gsapi_run_string_continue method, of class GhostscriptLibrary.
     */
    public void testGsapi_run_string_continue() {

	// create pointer to hold Ghostscript instance
	GhostscriptLibrary.gs_main_instance.ByReference instanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();

	// create instance
	ghostscriptLibrary.gsapi_new_instance(instanceByRef.getPointer(), null);

	// enter interpreter
	String[] args = { "-dNODISPLAY", "-dNOPAUSE", "-dBATCH", "-dSAFER" };
	ghostscriptLibrary.gsapi_init_with_args(instanceByRef.getValue(),
		args.length, args);

	// run command
	IntByReference exitCode = new IntByReference();
	ghostscriptLibrary.gsapi_run_string_begin(instanceByRef.getValue(), 0,
		exitCode);
	String str = "devicenames ==\n";
	ghostscriptLibrary.gsapi_run_string_continue(instanceByRef.getValue(),
		str, str.length(), 0, exitCode);
	// test result
	assertEquals(0, exitCode.getValue());
	ghostscriptLibrary.gsapi_run_string_end(instanceByRef.getValue(), 0,
		exitCode);

	// exit interpreter
	ghostscriptLibrary.gsapi_exit(instanceByRef.getValue());

	// delete instance
	ghostscriptLibrary.gsapi_delete_instance(instanceByRef.getValue());
    }

    /**
     * Test of gsapi_run_file method, of class GhostscriptLibrary.
     */
    public void testGsapi_run_file() {

	// create pointer to hold Ghostscript instance
	GhostscriptLibrary.gs_main_instance.ByReference instanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();

	// create instance
	ghostscriptLibrary.gsapi_new_instance(instanceByRef.getPointer(), null);

	// enter interpreter
	String[] args = { "-dNODISPLAY", "-dNOPAUSE", "-dBATCH", "-dSAFER" };
	ghostscriptLibrary.gsapi_init_with_args(instanceByRef.getValue(),
		args.length, args);

	// run command
	IntByReference exitCode = new IntByReference();
        File file = new File(testResourcesPath, "input.ps");
	ghostscriptLibrary.gsapi_run_file(instanceByRef.getValue(), file.getPath(),
		0, exitCode);
	// test result
	assertEquals(0, exitCode.getValue());

	// exit interpreter
	ghostscriptLibrary.gsapi_exit(instanceByRef.getValue());

	// delete instance
	ghostscriptLibrary.gsapi_delete_instance(instanceByRef.getValue());
    }

    /**
     * Test of gsapi_set_stdio method, of class GhostscriptLibrary.
     */
    public void testGsapi_set_stdio() {

	// create pointer to hold Ghostscript instance
	GhostscriptLibrary.gs_main_instance.ByReference instanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();

	// create instance
	ghostscriptLibrary.gsapi_new_instance(instanceByRef.getPointer(), null);

	// buffer to hold standard output
	final StringBuffer stdOutBuffer = new StringBuffer();
	// buffer to store value if stdin callback is called
	final StringBuffer stdInBuffer = new StringBuffer();

	// callbacks
	// stdin_fn
	GhostscriptLibrary.stdin_fn stdinCallback = new GhostscriptLibrary.stdin_fn() {

	    public int callback(Pointer caller_handle, Pointer buf, int len) {

		stdInBuffer.append("OK");
		return 0;
		/*
		 * String input = "devicenames ==\n"; buf.setString(0, input);
		 * return input.length();
		 */
	    }
	};
	// stdout_fn
	GhostscriptLibrary.stdout_fn stdoutCallback = new GhostscriptLibrary.stdout_fn() {

	    public int callback(Pointer caller_handle, String str, int len) {
		stdOutBuffer.append(str.substring(0, len));
		return len;
	    }
	};
	// stderr_fn
	GhostscriptLibrary.stderr_fn stderrCallback = new GhostscriptLibrary.stderr_fn() {

	    public int callback(Pointer caller_handle, String str, int len) {
		return len;
	    }
	};
	// io setting
	ghostscriptLibrary.gsapi_set_stdio(instanceByRef.getValue(),
		stdinCallback, stdoutCallback, stderrCallback);

	// enter interpreter
	String[] args = { "-dNODISPLAY", "-dQUIET", "-dNOPAUSE", "-dBATCH",
		"-sOutputFile=%stdout", "-f", "-" };
	ghostscriptLibrary.gsapi_init_with_args(instanceByRef.getValue(),
		args.length, args);

	IntByReference exitCode = new IntByReference();
	String command = "devicenames ==\n";
	ghostscriptLibrary.gsapi_run_string_with_length(
		instanceByRef.getValue(), command, command.length(), 0,
		exitCode);

	// exit
	ghostscriptLibrary.gsapi_exit(instanceByRef.getValue());

	// delete instance
	ghostscriptLibrary.gsapi_delete_instance(instanceByRef.getValue());

	// assert std in was redirected
	assertTrue(stdInBuffer.toString().length() > 0);
	// assert std out was redirected
	assertTrue(stdOutBuffer.toString().length() > 0);
    }

    /**
     * Test of gsapi_set_display_callback method, of class GhostscriptLibrary.
     */
    public void testGsapi_set_display_callback() {

	// create pointer to hold Ghostscript instance
	GhostscriptLibrary.gs_main_instance.ByReference instanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();

	// create instance
	ghostscriptLibrary.gsapi_new_instance(instanceByRef.getPointer(), null);

	// buffer holding callback results
	final StringBuffer result = new StringBuffer();

	// set display callbacks
	GhostscriptLibrary.display_callback_s displayCallback = new GhostscriptLibrary.display_callback_s();
	displayCallback.version_major = 2;
	displayCallback.version_minor = 0;
	displayCallback.display_open = new GhostscriptLibrary.display_callback_s.display_open() {

	    public int callback(Pointer handle, Pointer device) {
		result.append("OPEN-");
		return 0;
	    }
	};
	displayCallback.display_preclose = new GhostscriptLibrary.display_callback_s.display_preclose() {

	    public int callback(Pointer handle, Pointer device) {
		result.append("PRECLOSE-");
		return 0;
	    }
	};
	displayCallback.display_close = new GhostscriptLibrary.display_callback_s.display_close() {

	    public int callback(Pointer handle, Pointer device) {
		result.append("CLOSE");
		return 0;
	    }
	};
	displayCallback.display_presize = new GhostscriptLibrary.display_callback_s.display_presize() {

	    public int callback(Pointer handle, Pointer device, int width,
		    int height, int raster, int format) {
		result.append("PRESIZE-");
		return 0;
	    }
	};
	displayCallback.display_size = new GhostscriptLibrary.display_callback_s.display_size() {

	    public int callback(Pointer handle, Pointer device, int width,
		    int height, int raster, int format, Pointer pimage) {
		result.append("SIZE-");
		return 0;
	    }
	};
	displayCallback.display_sync = new GhostscriptLibrary.display_callback_s.display_sync() {

	    public int callback(Pointer handle, Pointer device) {
		result.append("SYNC-");
		return 0;
	    }
	};
	displayCallback.display_page = new GhostscriptLibrary.display_callback_s.display_page() {

	    public int callback(Pointer handle, Pointer device, int copies,
		    int flush) {
		result.append("PAGE-");
		return 0;
	    }
	};
	displayCallback.display_update = new GhostscriptLibrary.display_callback_s.display_update() {

	    public int callback(Pointer handle, Pointer device, int x, int y,
		    int w, int h) {
		result.append("UPDATE-");
		return 0;
	    }
	};

	displayCallback.display_memalloc = null;
	displayCallback.display_memfree = null;

	displayCallback.size = displayCallback.size();

	ghostscriptLibrary.gsapi_set_display_callback(instanceByRef.getValue(),
		displayCallback);

	// enter interpreter
	String[] args = new String[7];
	args[0] = "-dQUIET";
	args[1] = "-dNOPAUSE";
	args[2] = "-dBATCH";
	args[3] = "-dSAFER";
	args[4] = "-sDEVICE=display";
	args[5] = "-sDisplayHandle=0";
	args[6] = "-dDisplayFormat=16#a0800";
	ghostscriptLibrary.gsapi_init_with_args(instanceByRef.getValue(),
		args.length, args);

	// run command
	IntByReference exitCode = new IntByReference();
	String command = "showpage\n";
	ghostscriptLibrary.gsapi_run_string_with_length(
		instanceByRef.getValue(), command, command.length(), 0,
		exitCode);

	// exit interpreter
	ghostscriptLibrary.gsapi_exit(instanceByRef.getValue());

	// delete instance
	ghostscriptLibrary.gsapi_delete_instance(instanceByRef.getValue());

	// assert all display callbacks were called successfully
	assertEquals(
		"OPEN-PRESIZE-UPDATE-SIZE-PAGE-UPDATE-SYNC-PRECLOSE-CLOSE",
		result.toString());
    }
}
