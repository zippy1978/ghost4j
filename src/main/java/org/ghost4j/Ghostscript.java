/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package org.ghost4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Level;
import org.ghost4j.display.DisplayCallback;
import org.ghost4j.display.DisplayData;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

/**
 * Class representing the Ghostscript interpreter.
 * 
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class Ghostscript {

    /**
     * Name of the system property used to set the encoding to use for stdin.
     */
    public static final String PROPERTY_NAME_ENCODING = "ghost4j.encoding";
    /**
     * Holds Ghostscript interpreter native instance (C pointer).
     */
    private static GhostscriptLibrary.gs_main_instance.ByReference nativeInstanceByRef;
    /**
     * Holds singleton instance.
     */
    private static Ghostscript instance;
    /**
     * Standard input stream.
     */
    private static InputStream stdIn;
    /**
     * Standard output stream.
     */
    private static OutputStream stdOut;
    /**
     * Error output stream.
     */
    private static OutputStream stdErr;
    /**
     * Display callback used to handle display.
     */
    private static DisplayCallback displayCallback;
    /**
     * Stores display data when working with display callback.
     */
    private static DisplayData displayData;
    /**
     * Holds the native display callback.
     */
    private static GhostscriptLibrary.display_callback_s nativeDisplayCallback;

    /**
     * Singleton access method.
     * 
     * @return The singleton instance.
     */
    public static synchronized Ghostscript getInstance() {

	if (instance == null) {

	    // new instance
	    instance = new Ghostscript();

	}

	return instance;
    }

    /**
     * Gets the display callback set on the Ghostscript interpreter (may be null
     * if not set).
     * 
     * @return The DisplayCallback or null
     */
    public synchronized DisplayCallback getDisplayCallback() {
	return displayCallback;
    }

    /**
     * Sets a display callback for the Ghostscript interpreter.
     * 
     * @param displayCallback
     *            DisplayCallback object
     */
    public synchronized void setDisplayCallback(DisplayCallback displayCallback) {
	this.displayCallback = displayCallback;
    }

    /**
     * Gets the error output stream of the Ghostscript interpreter (may be null
     * if not set).
     * 
     * @return The OutputStream or null
     */
    public synchronized OutputStream getStdErr() {
	return stdErr;
    }

    /**
     * Sets the error output stream of the Ghostscript interpreter.
     * 
     * @param stdErr
     *            OutputStream object
     */
    public synchronized void setStdErr(OutputStream stdErr) {
	this.stdErr = stdErr;
    }

    /**
     * Gets the standard output stream of the Ghostscript interpreter (may be
     * null if not set).
     * 
     * @return The OutputStream or null
     */
    public synchronized OutputStream getStdOut() {
	return stdOut;
    }

    /**
     * Sets the standard output stream of the Ghostscript interpreter.
     * 
     * @param stdOut
     *            OutputStream object
     */
    public synchronized void setStdOut(OutputStream stdOut) {
	this.stdOut = stdOut;
    }

    /**
     * Gets the standard input stream of the Ghostscript interpreter (may be
     * null if not set).
     * 
     * @return The InputStream or null
     */
    public synchronized InputStream getStdIn() {
	return stdIn;
    }

    /**
     * Sets the standard input stream of the Ghostscript interpreter.
     * 
     * @param stdIn
     *            InputStream object
     */
    public synchronized void setStdIn(InputStream stdIn) {
	this.stdIn = stdIn;
    }

    /**
     * Private constructor.
     */
    private Ghostscript() {
    }

    /**
     * Singleton factory method for getting a Ghostscript,interpreter instance.
     * Only called from class itself.
     * 
     * @return Ghostscript instance.
     * @throws org.ghost4j.GhostscriptException
     */
    private synchronized GhostscriptLibrary.gs_main_instance.ByReference getNativeInstanceByRef()
	    throws GhostscriptException {

	if (nativeInstanceByRef == null) {

	    // prepare instance
	    nativeInstanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();
	    // create instance
	    int result = GhostscriptLibrary.instance.gsapi_new_instance(
		    nativeInstanceByRef.getPointer(), null);

	    // test result
	    if (result != 0) {
		// failure
		nativeInstanceByRef = null;
		throw new GhostscriptException(
			"Cannot get Ghostscript interpreter instance. Error code is "
				+ result);
	    }
	}

	return nativeInstanceByRef;
    }

    private synchronized DisplayData getDisplayData() {

	if (displayData == null) {
	    displayData = new DisplayData();
	}

	return displayData;
    }

    /**
     * Gets Ghostscript revision data.
     * 
     * @return Revision data.
     */
    public static GhostscriptRevision getRevision() {

	// prepare revision structure and call revision function
	GhostscriptLibrary.gsapi_revision_s revision = new GhostscriptLibrary.gsapi_revision_s();
	GhostscriptLibrary.instance.gsapi_revision(revision, revision.size());

	GhostscriptRevision result = new GhostscriptRevision();
	result.setProduct(revision.product);
	result.setCopyright(revision.copyright);
	result.setNumber(new Float(revision.revision.floatValue() / 100)
		.toString());
	// parse revision date
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
     * 
     * @param args
     *            Interpreter parameters. Use the same as Ghostscript command
     *            line arguments.
     * @throws org.ghost4j.GhostscriptException
     */
    public void initialize(String[] args) throws GhostscriptException {

	int result = 0;

	// stdin callback
	GhostscriptLibrary.stdin_fn stdinCallback = null;
	if (getStdIn() != null) {
	    stdinCallback = new GhostscriptLibrary.stdin_fn() {

		public int callback(Pointer caller_handle, Pointer buf, int len) {

		    // retrieve encoding, if no ghost4j encoding defined = use
		    // JVM default
		    String encoding = System.getProperty(
			    PROPERTY_NAME_ENCODING,
			    System.getProperty("file.encoding"));

		    try {
			byte[] buffer = new byte[1000];
			int read = getStdIn().read(buffer);
			if (read != -1) {
			    buf.setString(0, new String(buffer, 0, read,
				    encoding));
			    buffer = null;
			    return read;
			}
		    } catch (Exception e) {
			// an error occurs: do nothing
		    }

		    return 0;
		}
	    };
	}

	// stdout callback, if no stdout explicitly defined, use a
	// GhostscriptLoggerOutputStream to log messages
	GhostscriptLibrary.stdout_fn stdoutCallback = null;
	if (getStdOut() == null) {
	    setStdOut(new GhostscriptLoggerOutputStream(Level.INFO));
	}

	stdoutCallback = new GhostscriptLibrary.stdout_fn() {

	    public int callback(Pointer caller_handle, String str, int len) {

		try {
		    getStdOut().write(str.getBytes(), 0, len);
		} catch (IOException ex) {
		    // do nothing
		}

		return len;
	    }
	};

	// stderr callback, if no stdout explicitly defined, use a
	// GhostscriptLoggerOutputStream to log messages
	GhostscriptLibrary.stderr_fn stderrCallback = null;
	if (getStdErr() == null) {
	    setStdErr(new GhostscriptLoggerOutputStream(Level.ERROR));
	}

	stderrCallback = new GhostscriptLibrary.stderr_fn() {

	    public int callback(Pointer caller_handle, String str, int len) {

		try {
		    getStdErr().write(str.getBytes(), 0, len);
		} catch (IOException ex) {
		    // do nothing
		}

		return len;
	    }
	};

	// io setting
	result = GhostscriptLibrary.instance.gsapi_set_stdio(
		getNativeInstanceByRef().getValue(), stdinCallback,
		stdoutCallback, stderrCallback);

	// test result
	if (result != 0) {
	    throw new GhostscriptException(
		    "Cannot set IO on Ghostscript interpreter. Error code is "
			    + result);
	}

	// display callback setting
	if (getDisplayCallback() != null) {
	    result = GhostscriptLibrary.instance.gsapi_set_display_callback(
		    getNativeInstanceByRef().getValue(),
		    buildNativeDisplayCallback(getDisplayCallback()));

	    // test result
	    if (result != 0) {
		throw new GhostscriptException(
			"Cannot set display callback on Ghostscript interpreter. Error code is "
				+ result);
	    }
	}

	// init
	if (args != null) {
	    result = GhostscriptLibrary.instance.gsapi_init_with_args(
		    getNativeInstanceByRef().getValue(), args.length, args);
	} else {
	    result = GhostscriptLibrary.instance.gsapi_init_with_args(
		    getNativeInstanceByRef().getValue(), 0, null);
	}

	// interpreter exited: this is not an error
	if (result == -101) {
	    exit();
	    result = 0;
	}

	// test result
	if (result != 0) {
	    throw new GhostscriptException(
		    "Cannot initialize Ghostscript interpreter. Error code is "
			    + result);
	}
    }

    /**
     * Builds a native display callback from a DisplayCallback object.
     * 
     * @param displayCallback
     *            DisplayCallback to use.
     * @return The created native display callback.
     */
    private synchronized GhostscriptLibrary.display_callback_s buildNativeDisplayCallback(
	    DisplayCallback displayCallback) throws GhostscriptException {

	nativeDisplayCallback = new GhostscriptLibrary.display_callback_s();

	// determine display callback version from Ghostscript version
	float version = Float.parseFloat(getRevision().getNumber());
	// some versions report version 8.15 as 815.05
	if (version < 8.50 || version > 100) {
	    nativeDisplayCallback.version_major = 1;
	} else {
	    nativeDisplayCallback.version_major = 2;
	}
	nativeDisplayCallback.version_minor = 0;

	nativeDisplayCallback.display_open = new GhostscriptLibrary.display_callback_s.display_open() {

	    public int callback(Pointer handle, Pointer device) {

		// call to java callback
		try {
		    getDisplayCallback().displayOpen();
		} catch (GhostscriptException e) {
		    return 1;
		}

		return 0;
	    }
	};
	nativeDisplayCallback.display_preclose = new GhostscriptLibrary.display_callback_s.display_preclose() {

	    public int callback(Pointer handle, Pointer device) {

		// call to java callback
		try {
		    getDisplayCallback().displayPreClose();
		} catch (GhostscriptException e) {
		    return 1;
		}

		return 0;
	    }
	};
	nativeDisplayCallback.display_close = new GhostscriptLibrary.display_callback_s.display_close() {

	    public int callback(Pointer handle, Pointer device) {

		// call to java callback
		try {
		    getDisplayCallback().displayClose();
		} catch (GhostscriptException e) {
		    return 1;
		}

		return 0;
	    }
	};
	nativeDisplayCallback.display_presize = new GhostscriptLibrary.display_callback_s.display_presize() {

	    public int callback(Pointer handle, Pointer device, int width,
		    int height, int raster, int format) {

		// call to java callback
		try {
		    getDisplayCallback().displayPreSize(width, height, raster,
			    format);
		} catch (GhostscriptException e) {
		    return 1;
		}

		return 0;
	    }
	};
	nativeDisplayCallback.display_size = new GhostscriptLibrary.display_callback_s.display_size() {

	    public int callback(Pointer handle, Pointer device, int width,
		    int height, int raster, int format, Pointer pimage) {

		// prepare current page data
		getDisplayData().setWidth(width);
		getDisplayData().setHeight(height);
		getDisplayData().setRaster(raster);
		getDisplayData().setFormat(format);
		getDisplayData().setPimage(pimage);

		// call to java callback
		try {
		    getDisplayCallback().displaySize(width, height, raster,
			    format);
		} catch (GhostscriptException e) {
		    return 1;
		}

		return 0;
	    }
	};
	nativeDisplayCallback.display_sync = new GhostscriptLibrary.display_callback_s.display_sync() {

	    public int callback(Pointer handle, Pointer device) {

		// call to java callback
		try {
		    getDisplayCallback().displaySync();
		} catch (GhostscriptException e) {
		    return 1;
		}

		return 0;
	    }
	};
	nativeDisplayCallback.display_page = new GhostscriptLibrary.display_callback_s.display_page() {

	    public int callback(Pointer handle, Pointer device, int copies,
		    int flush) {

		byte[] data = getDisplayData().getPimage().getByteArray(
			0,
			getDisplayData().getRaster()
				* getDisplayData().getHeight());

		// call to java callback
		try {
		    getDisplayCallback().displayPage(
			    getDisplayData().getWidth(),
			    getDisplayData().getHeight(),
			    getDisplayData().getRaster(),
			    getDisplayData().getFormat(), copies, flush, data);
		} catch (GhostscriptException e) {
		    return 1;
		}

		return 0;
	    }
	};
	nativeDisplayCallback.display_update = new GhostscriptLibrary.display_callback_s.display_update() {

	    public int callback(Pointer handle, Pointer device, int x, int y,
		    int w, int h) {

		// call to java callback
		try {
		    getDisplayCallback().displayUpdate(x, y, w, h);
		} catch (GhostscriptException e) {
		    return 1;
		}

		return 0;
	    }
	};

	nativeDisplayCallback.display_memalloc = null;
	nativeDisplayCallback.display_memfree = null;

	switch (nativeDisplayCallback.version_major) {
	case 1:
	    nativeDisplayCallback.size = nativeDisplayCallback.size()
		    - Pointer.SIZE;
	    break;
	default:
	    nativeDisplayCallback.size = nativeDisplayCallback.size();
	    break;
	}

	nativeDisplayCallback.display_separation = null;

	return nativeDisplayCallback;
    }

    /**
     * Exits Ghostscript interpreter. Must be called after initialize.
     * 
     * @throws org.ghost4j.GhostscriptException
     */
    public void exit() throws GhostscriptException {

	if (nativeInstanceByRef != null) {
	    int result = GhostscriptLibrary.instance
		    .gsapi_exit(getNativeInstanceByRef().getValue());

	    if (result != 0) {
		throw new GhostscriptException(
			"Cannot exit Ghostscript interpreter. Error code is "
				+ result);
	    }
	}
    }

    /**
     * Sends command string to Ghostscript interpreter. Must be called after
     * initialize method.
     * 
     * @param string
     *            Command string
     * @throws org.ghost4j.GhostscriptException
     */
    public void runString(String string) throws GhostscriptException {

	IntByReference exitCode = new IntByReference();

	GhostscriptLibrary.instance.gsapi_run_string_begin(
		getNativeInstanceByRef().getValue(), 0, exitCode);

	// test exit code
	if (exitCode.getValue() != 0) {
	    throw new GhostscriptException(
		    "Cannot run command on Ghostscript interpreter. gsapi_run_string_begin failed with error code "
			    + exitCode.getValue());
	}

	// split string on carriage return
	String[] slices = string.split("\n");

	for (int i = 0; i < slices.length; i++) {
	    String slice = slices[i] + "\n";
	    GhostscriptLibrary.instance.gsapi_run_string_continue(
		    getNativeInstanceByRef().getValue(), slice, slice.length(),
		    0, exitCode);

	    // test exit code
	    if (exitCode.getValue() != 0) {
		throw new GhostscriptException(
			"Cannot run command on Ghostscript interpreter. gsapi_run_string_continue failed with error code "
				+ exitCode.getValue());
	    }
	}

	GhostscriptLibrary.instance.gsapi_run_string_end(
		getNativeInstanceByRef().getValue(), 0, exitCode);

	// test exit code
	if (exitCode.getValue() != 0) {
	    throw new GhostscriptException(
		    "Cannot run command on Ghostscript interpreter. gsapi_run_string_end failed with error code "
			    + exitCode.getValue());
	}

    }

    /**
     * Sends file Ghostscript interpreter. Must be called after initialize
     * method.
     * 
     * @param fileName
     *            File name
     * @throws org.ghost4j.GhostscriptException
     */
    public void runFile(String fileName) throws GhostscriptException {

	IntByReference exitCode = new IntByReference();

	GhostscriptLibrary.instance.gsapi_run_file(getNativeInstanceByRef()
		.getValue(), fileName, 0, exitCode);

	// test exit code
	if (exitCode.getValue() != 0) {
	    throw new GhostscriptException(
		    "Cannot run file on Ghostscript interpreter. Error code "
			    + exitCode.getValue());
	}

    }

    /**
     * Deletes the singleton instance of the Ghostscript object. This ensures
     * that the native Ghostscrit interpreter instance is deleted. This method
     * must be called if Ghostscript is not used anymore or maybe reinitialized.
     * 
     * @throws org.ghost4j.GhostscriptException
     */
    public static synchronized void deleteInstance()
	    throws GhostscriptException {

	// clear instance
	if (instance != null) {
	    // unreference singleton instance
	    instance = null;
	}

	// delete native interpeter instance
	if (nativeInstanceByRef != null) {
	    GhostscriptLibrary.instance
		    .gsapi_delete_instance(nativeInstanceByRef.getValue());
	    nativeInstanceByRef = null;
	}
    }
}
