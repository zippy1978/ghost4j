Core API
========
Core API is the lowest level of the library that allows direct interaction with the Ghostscript interpreter.

It is composed of :

* **GhostscriptLibrary interface:** the JNA interface with all the native Ghostscript API bindings. Important: this interface must never be used directly, but only through the Ghostscript class.

* **Ghostscript singleton class:** the object used to control the Ghostscript interpreter.

* **Classes of the org.ghost4j.display package:** a set of classes to write custom display callbacks.

### Ghostscript singleton

The native C Ghostscript API allows only one interpreter instance per native system process. That is why Ghostscript is a singleton instance in Ghost4J.

The instance can be obtained by calling **Ghostscript.getInstance()**: the instance is created lazyly at first call.

From the instance it is possible to use all the native interpreter features. Read the javadoc for more informations.

When it is not needed anymore, it is possible to delete the interpreter instance by calling **Ghostscript.deleteInstance()**: even if it is not required, 
it is highly recommended to do it to ensure a new / clean instance is returned next time.

When Ghost4J is used in a multi-thread environment, make sure that the interpreter (from the initialization to the instance delete) is access synchronously.  

### Display callbacks

Display callbacks are used to allow interaction with the Ghostscript interpreter.

In Ghost4J, writing a display callback is done by implementing the **DisplayCallback** interface.

To bind a display callback to the interpreter use the **setDisplayCallback(DisplayCallback dc)** method on the interpreter instance. Make sure to do it before initializing the interpreter.

Finally, in order to make the display callback work, initialize the interpreter by providing the following parameters: **-sDEVICE=display**, **-dDisplayHandle=0**, 
**-dDisplayFormat=16#804** (see Ghostscript documentation for other available formats).