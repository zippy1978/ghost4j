/*
 * Ghost4J: a Java wrapper for Ghostscript API.
 *
 * Distributable under LGPL license.
 * See terms of license at http://www.gnu.org/licenses/lgpl.html.
 */
package org.ghost4j;

import com.sun.jna.Callback;
import com.sun.jna.FromNativeContext;
import com.sun.jna.Library;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary.StdCallCallback;
import java.util.Arrays;
import java.util.List;

/**
 * Interface (JNA) bridging Ghostscript API (C language) with Java. All API
 * methods are bridged except for: gsapi_set_poll and gsapi_set_visual_tracer.
 * Note: in this interface variable names are kept unchanged compared with the C
 * API. Ghostscript API documentation can be found here:
 * http://ghostscript.com/doc/8.54/API.htm
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public interface GhostscriptLibrary extends Library {

    /**
     * Static instance of the library itself.
     */
    public static GhostscriptLibrary instance = GhostscriptLibraryLoader
            .loadLibrary();

    /**
     * Structure in charge of holding Ghostscript revision data.
     */
    public class gsapi_revision_s extends Structure {

        /**
         * Product name.
         */
        public String product;
        /**
         * Copyright.
         */
        public String copyright;
        /**
         * Revision number.
         */
        public NativeLong revision;
        /**
         * Revision date.
         */
        public NativeLong revisiondate;

        protected List<?> getFieldOrder() {
            return Arrays.asList("product", "copyright", "revision", "revisiondate");
        }
    }

    /**
     * Structure defining display callback functions.
     */
    public class display_callback_s extends Structure {

        /**
         * Callback called when new device has been opened. This is the first
         * event from this device.
         */
        public static interface display_open extends Callback {

            public int callback(Pointer handle, Pointer device);
        }

        /**
         * Callback called when device is about to be closed. Device will not be
         * closed until this function returns.
         */
        public static interface display_preclose extends Callback {

            public int callback(Pointer handle, Pointer device);
        }

        /**
         * Callback called when device has been closed. This is the last event
         * from this device.
         */
        public static interface display_close extends Callback {

            public int callback(Pointer handle, Pointer device);
        }

        /**
         * Callback called when device is about to be resized. Resize will only
         * occur if this function returns 0. raster is byte count of a row.
         */
        public static interface display_presize extends Callback {

            public int callback(Pointer handle, Pointer device, int width,
                    int height, int raster, int format);
        }

        /**
         * Callback called when device has been resized. New pointer to raster
         * is returned in pimage.
         */
        public static interface display_size extends Callback {

            public int callback(Pointer handle, Pointer device, int width,
                    int height, int raster, int format, Pointer pimage);
        }

        /**
         * Callback called on page flush.
         */
        public static interface display_sync extends Callback {

            public int callback(Pointer handle, Pointer device);
        }

        /**
         * Callback called on show page. If you want to pause on showpage, then
         * don't return immediately.
         */
        public static interface display_page extends Callback {

            public int callback(Pointer handle, Pointer device, int copies,
                    int flush);
        }

        /**
         * Callback called to notify the caller whenever a portion of the raster
         * is updated. This can be used for cooperative multitasking or for
         * progressive update of the display.
         */
        public static interface display_update extends Callback {

            public int callback(Pointer handle, Pointer device, int x, int y,
                    int w, int h);
        }

        /**
         * Callback called to allocate memory for bitmap This is provided in
         * case you need to create memory in a special way, e.g. shared. This
         * will only be called to allocate the image buffer. The first row will
         * be placed at the address returned by display_memalloc.
         */
        public static interface display_memalloc extends Callback {

            public void callback(Pointer handle, Pointer device, NativeLong size);
        }

        /**
         * Callback called to free memory for bitmap.
         */
        public static interface display_memfree extends Callback {

            public int callback(Pointer handle, Pointer device, Pointer mem);
        }

        public static interface display_separation extends Callback {

            public int callback(Pointer handle, Pointer device, int component,
                    String component_name, short c, short m, short y, short k);
        }

        /**
         * Size of this structure. Used for checking if we have been handed a
         * valid structure.
         */
        public int size;
        /**
         * Major version of this structure. The major version number will change
         * if this structure changes.
         */
        public int version_major;
        /**
         * Minor version of this structure. The minor version number will change
         * if new features are added without changes to this structure. For
         * example, a new color format.
         */
        public int version_minor;
        /**
         * Holds a display_open callback.
         */
        public display_open display_open;
        /**
         * Holds a display_preclose callback.
         */
        public display_preclose display_preclose;
        /**
         * Holds a display_close callback.
         */
        public display_close display_close;
        /**
         * Holds a display_presize callback.
         */
        public display_presize display_presize;
        /**
         * Holds a display_size callback.
         */
        public display_size display_size;
        /**
         * Holds a display_sync callback.
         */
        public display_sync display_sync;
        /**
         * Holds a display_page callback.
         */
        public display_page display_page;
        /**
         * Holds a display_update callback. Set this to null if not required.
         */
        public display_update display_update;
        /**
         * Holds a display_memalloc callback. Set this to null if not required.
         */
        public display_memalloc display_memalloc;
        /**
         * Holds a display_memfree callback. Set this to null if not required.
         */
        public display_memfree display_memfree;
        /**
         * Holds a display_separation callback. Set this to null if not
         * required. Ghostscript must only use this callback if version_major >=
         * 2.
         */
        public display_separation display_separation;

        protected List<?> getFieldOrder() {
            return Arrays.asList("size", "version_major", "version_minor", "display_open", "display_preclose", "display_close", "display_presize", "display_size", "display_sync", "display_page", "display_update", "display_memalloc", "display_memfree", "display_separation");
        }
    }

    /**
     * Pointer holding a native Ghostscript instance.
     */
    public class gs_main_instance extends PointerType {

        @Override
        public Object fromNative(Object arg0, FromNativeContext arg1) {
            return super.fromNative(arg0, arg1);
        }

        public static class ByReference extends PointerByReference {

            @Override
            public Object fromNative(Object arg0, FromNativeContext arg1) {
                return super.fromNative(arg0, arg1);
            }
        }
    }

    /**
     * Callback called to provide a custom input to Ghostscript. buf is a
     * pointer to a char array. len is the length of the char array.
     */
    public interface stdin_fn extends StdCallCallback {

        public int callback(Pointer caller_handle, Pointer buf, int len);
    }

    /**
     * Callback called to provide a custom output to Ghostscript. Important: The
     * output is not the resulting file, but the output of the Postscript
     * interpreter. str holds output characters. len is the length for str.
     */
    public interface stdout_fn extends StdCallCallback {

        public int callback(Pointer caller_handle, String str, int len);
    }

    /**
     * Callback called to provide a custom error output to Ghostscript. str
     * holds output characters. len is the length for str.
     */
    public interface stderr_fn extends StdCallCallback {

        public int callback(Pointer caller_handle, String str, int len);
    }

    /**
     * This function returns the revision numbers and strings of the Ghostscript
     * interpreter library. You should call it before any other interpreter
     * library functions to make sure that the correct version of the
     * Ghostscript interpreter has been loaded.
     *
     * @param pr Pointer to the gsapi_revision_s that will hold return values.
     * @param len pr Length
     * @see gsapi_revision_s
     * @return 0 if everything is OK, < 0 otherwise
     */
    public int gsapi_revision(Structure pr, int len);

    /**
     * Create a new instance of Ghostscript. This instance is passed to most
     * other gsapi functions. The caller_handle will be provided to callback
     * functions. At this stage, Ghostscript supports only one instance.
     *
     * @param pinstance Pointer to gs_main_instance that will hold the
     * Ghostscript instance.
     * @param caller_handle Caller handler pointer (may be null).
     * @return 0 if everything is OK, < 0 otherwise
     */
    public int gsapi_new_instance(Pointer pinstance, Pointer caller_handle);

    /**
     * Destroy an instance of Ghostscript. Before you call this, Ghostscript
     * must have finished. If Ghostscript has been initialised, you must call
     * gsapi_exit before gsapi_delete_instance.
     *
     * @param instance Pointer to the Ghostscript instance.
     */
    public void gsapi_delete_instance(Pointer instance);

    /**
     * Exit the interpreter. This must be called on shutdown if
     * gsapi_init_with_args() has been called, and just before
     * gsapi_delete_instance().
     *
     * @param instance Pointer to the Ghostscript instance.
     * @return 0 if everything is OK, < 0 otherwise
     */
    public int gsapi_exit(Pointer instance);

    /**
     * Initialise the interpreter. This calls gs_main_init_with_args() in
     * imainarg.c. The arguments are the same as the "C" main function: argv[0]
     * is ignored and the user supplied arguments are argv[1] to argv[argc-1].
     *
     * @param instance Pointer to the Ghostscript instance.
     * @param argc Argument count
     * @param argv Argument array
     * @return 0 if everything is OK, < 0 otherwise
     */
    public int gsapi_init_with_args(Pointer instance, int argc, String[] argv);

    /**
     * Send instruction to the Ghostscript interpreter. The address passed in
     * pexit_code will be used to return the exit code for the interpreter in
     * case of a quit or fatal error.
     *
     * @param instance Pointer to the Ghostscript instance.
     * @param str Instructions. Max length for the string is 65535.
     * @param user_errors If set to 0 errors are returned the normal way (to the
     * interpreter output), if a negative value is used errors are returns
     * directly by the function.
     * @param pexit_code Pointer to the exit return code
     * @return 0 if everything is OK, < 0 otherwise
     */
    public int gsapi_run_string(Pointer instance, String str, int user_errors,
            IntByReference pexit_code);

    /**
     * Send instruction to the Ghostscript interpreter. The address passed in
     * pexit_code will be used to return the exit code for the interpreter in
     * case of a quit or fatal error.
     *
     * @param instance Pointer to the Ghostscript instance.
     * @param str Instructions. Max length for the string is 65535.
     * @param length str length.
     * @param user_errors If set to 0 errors are returned the normal way (to the
     * interpreter output), if a negative value is used errors are returns
     * directly by the function.
     * @param pexit_code Pointer to the exit return code
     * @return 0 if everything is OK, < 0 otherwise
     */
    public int gsapi_run_string_with_length(Pointer instance, String str,
            int length, int user_errors, IntByReference pexit_code);

    /**
     * Open an instruction block to the Ghostscript interpreter. The address
     * passed in pexit_code will be used to return the exit code for the
     * interpreter in case of a quit or fatal error.
     *
     * @param instance Pointer to the Ghostscript instance.
     * @param user_errors If set to 0 errors are returned the normal way (to the
     * interpreter output), if a negative value is used errors are returns
     * directly by the function.
     * @param pexit_code Pointer to the exit return code
     * @return 0 if everything is OK, < 0 otherwise
     */
    public int gsapi_run_string_begin(Pointer instance, int user_errors,
            IntByReference pexit_code);

    /**
     * Send instruction to the Ghostscript interpreter. Must be used after
     * gsapi_run_string_begin is called. The address passed in pexit_code will
     * be used to return the exit code for the interpreter in case of a quit or
     * fatal error.
     *
     * @param instance Pointer to the Ghostscript instance.
     * @param str Instructions. Max length for the string is 65535.
     * @param length str length.
     * @param user_errors If set to 0 errors are returned the normal way (to the
     * interpreter output), if a negative value is used errors are returns
     * directly by the function.
     * @param pexit_code Pointer to the exit return code
     * @return 0 if everything is OK, < 0 otherwise
     */
    public int gsapi_run_string_continue(Pointer instance, String str,
            int length, int user_errors, IntByReference pexit_code);

    /**
     * Close an instruction block to the Ghostscript interpreter. The address
     * passed in pexit_code will be used to return the exit code for the
     * interpreter in case of a quit or fatal error.
     *
     * @param instance Pointer to the Ghostscript instance.
     * @param user_errors If set to 0 errors are returned the normal way (to the
     * interpreter output), if a negative value is used errors are returns
     * directly by the function.
     * @param pexit_code Pointer to the exit return code
     * @return 0 if everything is OK, < 0 otherwise
     */
    public int gsapi_run_string_end(Pointer instance, int user_errors,
            IntByReference pexit_code);

    /**
     * Send instructions from a file to the Ghostscript interpreter.
     *
     * @param instance Pointer to the Ghostscript instance.
     * @param file_name File name.
     * @param user_errors If set to 0 errors are returned the normal way (to the
     * interpreter output), if a negative value is used errors are returns
     * directly by the function.
     * @param pexit_code
     * @return 0 if everything is OK, < 0 otherwise
     */
    public int gsapi_run_file(Pointer instance, String file_name,
            int user_errors, IntByReference pexit_code);

    /**
     * Set the callback functions for stdio. The stdin callback function should
     * return the number of characters read, 0 for EOF, or -1 for error. The
     * stdout and stderr callback functions should return the number of
     * characters written.
     *
     * @param instance Pointer to the Ghostscript instance.
     * @param stdin_fn Stdin callback function.
     * @param stdout_fn Stdout callback function.
     * @param stderr_fn Stderr callback function.
     * @return 0 if everything is OK, < 0 otherwise
     */
    public int gsapi_set_stdio(Pointer instance, stdin_fn stdin_fn,
            stdout_fn stdout_fn, stderr_fn stderr_fn);

    /**
     * Set the callback structure for the display device. If the display device
     * is used, this must be called after gsapi_new_instance() and before
     * gsapi_init_with_args(). See gdevdsp.h for more details.
     *
     * @param instance Pointer to the Ghostscript instance.
     * @param callback display_callback_s Structure holding display callback
     * functions.
     * @return 0 if everything is OK, < 0 otherwise
     */
    public int gsapi_set_display_callback(Pointer instance, Structure callback);
}
