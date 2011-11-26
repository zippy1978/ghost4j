package gnu.cajo.utils.extra;

import gnu.cajo.invoke.Remote;
import gnu.cajo.invoke.Invoke;
import gnu.cajo.invoke.RemoteInvoke;

/*
 * Generic Object Invocation Wrapper
 * Copyright (c) 2004 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file Wrapper.java is part of the cajo library.
 *
 * The cajo library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public Licence as published
 * by the Free Software Foundation, at version 3 of the licence, or (at your
 * option) any later version.
 *
 * The cajo library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public Licence for more details.
 *
 * You should have received a copy of the GNU Lesser General Public Licence
 * along with this library. If not, see http://www.gnu.org/licenses/lgpl.html
 */

/**
 * The base class for wrapping objects, remote references, and proxies for
 * syntactical transparency with the remaining codebase. It allows the object
 * type to change between local object/remote/proxy, without affecting the
 * calling code. It enforces compile time type checking, and the standard
 * invocation systax, while still allowing invocation of methods via the
 * reflection based invoke paradigm. It also promotes <i>"lazy instantiation,"
 * </i> as the wrapped reference will not be loaded into the runtime unless
 * and until it is needed. If the wrapped object url is compatible, the wrapper
 * may be freely passed between JVM instances.
 *
 * @version 1.0, 27-Apr-04 Initial release
 * @author John Catherino
 */
public class Wrapper implements Invoke {
   private static final long serialVersionUID = 1L;
   /**
    * The object being wrapped by the reflection based invocation paradigm.
    * It may be a reference to a remote object, or a proxy sent by a remote
    * object, or even an ordinary local object. It is not declared final, as
    * to allow its representation to change, as required for performance or
    * other application specific reasons, for example to be further wrapped by
    * a {@link gnu.cajo.utils.MonitorItem MonitorItem}. It is transient to
    * force subclasses to manage application specific persistence.
    */
   protected transient Object object;
   /**
    * The URL where to get the wrapped object: file://, http://, ftp://,
    * /path/name, path/name, or //[host][:port]/[name]. The host, port,
    * and name, are all optional. If missing the host is presumed local, the
    * port 1099, and the name "main". The referenced resource can be
    * returned as a MarshalledObject, it will be extracted automatically.
    * If the URL is null, it will be assumed to be ///.
    */
   protected String url;
   /**
    * This method is used to support lazy loading of remote object
    * references, only when needed. It also absorbs the checked
    * RemoteException, which will cause an unchecked NullPointerException,
    * for the calling method's subsequent operation on the wrapped object.
    */
   protected synchronized void load() {
      if (object == null) try { object = Remote.getItem(url); }
      catch(Exception x) {} 
   }
   /**
    * The no-arg constructor does nothing, it is protected for use only by
    * subclasses.
    */
   protected Wrapper() {}
   /**
    * The constructor loads an object, or a zipped marshalled object (zedmob)
    * from a URL, a file, or from a remote rmiregistry. If the object is in a
    * local file, it can be either inside the application's jar file, or on its
    * local file system.<p>
    * Loading an item from a file can be specified in one of three ways:<p><ul>
    * <li>As a URL; in the format file://path/name
    * <li>As a class file; in the format path/name
    * <li>As a serialized item; in the format /path/name</ul><p>
    * File loading will first be attempted from within the application's jar
    * file, if that fails, it will then look in the local filesystem.
    * <i>Note:</i> any socket connections made by the incoming item cannot be
    * known at compile time, therefore proper operation if this VM is behind a
    * firewall could be blocked. Use behind a firewall would require knowing
    * all the ports that would be needed in advance, and enabling them before
    * loading the item. <i>Note:</i> for efficiency, the item will <u>not</u>
    * be loaded at this time, rather on the first time it is need to perform
    * in a particular method.
    * @param url The URL where to get the object: file://, http://, ftp://,
    * /path/name, path/name, or //[host][:port]/[name]. The host, port,
    * and name, are all optional. If missing the host is presumed local, the
    * port 1099, and the name "main". The referenced resource can be
    * returned as a MarshalledObject, it will be extracted automatically.
    * If the URL is null, it will be assumed to be ///.
    */
   public Wrapper(String url) { this.url = url; }
   /**
    * This method is used to test if the inner object is a reference to a
    * remote object. This can be important to know as remote invocations are
    * not time deterministic, and not assured of execution, as with local
    * objects. If the wrapped object has not yet been loaded, this will cause
    * it to happen, which could result in a NullPointerException on a load
    * failure.
    * @return True if the inner object is remote, false otherwise.
    */
   public boolean isRemote() {
      if (object == null) load();
      return object instanceof RemoteInvoke;
   }
   /**
    * This method attempts to extract usage information about the inner object,
    * if the method is supported.
    * @return A detailed guide to object usage, preferably with examples, with
    * HTML markup permitted.
    * @throws NoSuchMethodException If the inner object does not support the
    * description method.
    * @throws Exception If the innter object rejected the invocation, for
    * any application specific reasons.
    */
   public String getDescription() throws Exception {
      if (object == null) load();
      return (String)Remote.invoke(object, "getDescription", null);
   }
   /**
    * This method <u><i>must</i></u> be called by all interface methods of the
    * subclass, as it will load the wrapped object if it has not yet been
    * loaded.
    * @param method The method name to be invoked.
    * @param args The arguments to provide to the method for its invocation,
    * possibly null.
    * @return The resulting data, from the invocation, possibly null.
    * @throws RemoteException if the remote registry could not be reached.
    * @throws NotBoundException if the requested name is not in the registry.
    * @throws IOException if the zedmob format is invalid.
    * @throws InstantiationException when the URL specifies a class name
    * which cannot be instantiated at runtime.
    * @throws IllegalAccessException when the url specifies a class name
    * and it does not support a no-arg constructor.
    * @throws MalformedURLException if the URL is not in the format explained
    * @throws IllegalArgumentException If the method argument is null.
    * @throws NoSuchMethodException If no matching method can be found.
    * @throws ClassNotFoundException If the wrapped object's codebase cannot be
    * found on deserialization.
    * @throws StreamCorruptedException If control information in the stream is
    * not consistent on deserialization.
    * @throws Exception If the inner object rejected the invocation, for
    * any application specific reasons.
    */
   public Object invoke(String method, Object args) throws Exception {
      if (object == null) load();
      return Remote.invoke(object, method, args);
   }
}
