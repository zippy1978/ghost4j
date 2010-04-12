package gnu.cajo.utils;

import java.awt.*;
import gnu.cajo.invoke.*;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.io.IOException;
import java.net.MalformedURLException;

/*
 * Abstract Proxy Item Base Class
 * Copyright (C) 1999 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file BaseProxy.java is part of the cajo library.
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
 * A standard abstract base class for proxy objects.  Proxies are remote
 * object interfaces to server objects.  They are intended to offload routine
 * processing.  They differ from server objects in that they are sent to
 * remote VMs to operate, and are often not even instantiated in the runtime
 * of the server's VM.
 * 
 * @version 1.0, 01-Nov-99 Initial release
 * @author John Catherino
 */
public abstract class BaseProxy implements Serializable {
   /**
    * A remote reference to the proxy itself, which it can send to its server,
    * or other remote VMs on which they can asynchronously callback.
    */
   protected transient Remote remoteThis;
   /**
    * The reference to the sending server, on which the proxy may
    * asynchronously callback.  It is set by the {@link ItemServer ItemServer}
    * during the bind operation.
    */
   protected RemoteInvoke item;
   /**
    * A reference to the proxy's processing code.  If non-null, it will be
    * started automatically upon arrival at the host.  Its thread can be
    * accessed through the thread member.
    */
   protected MainThread runnable;
   /**
    * The processing thread of the proxy object, it will be started
    * automatically upon arrival at the client when the init method is invoked.
    */
   public transient Thread thread;
   /**
    * A reference to the proxy's graphical user interface, if any.  It will be
    * returned to the client as a result of its initialization invocation.
    */
   public Container container;
   /**
    * The path/filename of the resource bundle in the proxy's codebase jar
    * file. It will be used to localize any displayed strings, to the language
    * of the proxy recipient, as close as possible, if supplied.  It is
    * declared public since its value is typically assigned by a <i>builder</i>
    * application.
    */
   public String bundle;
   /**
    * The collection of strings to be displayed at the host VM.  On
    * instantiation at the host, the array will be loaded with localized
    * strings from the most appropriate resource bundle for the locale of
    * the receiving VM, if provided.  It is public since its value is
    * typically assigned by a <i>builder</i> program.
    */
   public String strings[];
   /**
    * The main processing thread of this object.  An object can be either
    * entirely, event driven, i.e. executing only when its methods are being
    * invoked, or can also have a thread of its own. If non-null, it will be
    * started upon its arrival at the host via the client's proxy inialisation
    * invocation.<br><br>
    * This is an an inner class of BaseProxy, to allow its implementations
    * access to the object's private and protected members and methods.
    * This is critical because <b>all</b> public methods of BaseProxy can be
    * invoked by remote objects, just like with local objects.
    */
   public abstract class MainThread implements Runnable, Serializable {
      /**
       * Nothing is performed in the constructor. Construction and
       * configuration are generally performed by a builder application.
       */
      public MainThread() {}
      /**
       * The run method is exectued by the thread created for the BaseProxy
       * at its initialization at the client, and runs until it returns.
       */
      public abstract void run();
   }
   /**
    * A standard base class for graphical proxy objects. A graphical proxy
    * provides a user interface to itself which can be displayed at the
    * receiving VM. It is implemented as an inner class of BaseProxy, to allow
    * its subclass implementations access to its outer object's private and
    * protected members and methods. This is critical because <b>all</b> public
    * methods of BaseProxy can be invoked by remote objects, just like with
    * local objects.
    * 
    * @version 1.0, 01-Nov-99 Initial release
    * @author John Catherino
    */
   public class Panel extends Container {
      /**
       * Nothing is performed in the constructor. Construction and
       * configuration are generally performed by a <i>builder</i> application.
       */
      public Panel() {}
      /**
       * The update method is overridden to directly invoke the paint method.
       * It makes drawing faster, and cleaner, but also means that the panel
       * background will not be cleared on a size change.
       */
      public final void update(Graphics g) { paint(g); }
      /**
       * The paint method is overridden to directly paint its components.
       * It makes drawing faster, and cleaner, but also means that the panel
       * has no default appearance.
       */
      public final void paint(Graphics  g) { paintComponents(g); }
      /**
       * This method simply returns the actual size of the component.  This
       * method returns the result of the getSize() method meaning that
       * subclasses should set the panel size <i>before</i> sending it to the
       * host.
       */
      public final Dimension getPreferredSize() { return getSize(); }
   }
   /**
    * Nothing is performed in the constructor. Construction and configuration
    * of the proxy are generally performed by a builder application.
    */
   public BaseProxy() {}
   /**
    * This function is called by the {@link ItemServer ItemServer} during its
    * bind operation.
    * @param  item A remote reference to the server object, on which the proxy
    * may asynchronously call back to it.
    */
   public void setItem(RemoteInvoke item) {
      if (this.item == null) this.item = item;
      else throw new IllegalArgumentException("Item already set");
   }
   /**
    * This function is called by the hosting client on upon the proxy's
    * arrival.  The client will provide a reference to the proxy, remoted in
    * the context of the client's VM.  This value will be saved in the
    * {@link #remoteThis remoteThis} member, and can be provided to other
    * remote objects, on which they can contact the proxy.
    * If the proxy has a string bundle, the localized strings most
    * closely matching the locale of the receiving host will be loaded. If the
    * proxy is graphical in nature, i.e. provides a graphical user interface,
    * this method will return it to the host, so that it may display it, if it
    * wishes.
    * @param  remoteRef A reference to the proxy, remoted in the context of the
    * client's VM.
    * @return The proxy's graphical user interface, if it has one, otherwise
    * null.
    */
   public Container init(Remote remoteRef) {
      if (remoteThis == null) {
         remoteThis = remoteRef;
         if (bundle != null) {
            java.util.ResourceBundle rb =
               java.util.ResourceBundle.getBundle(bundle);
            for (int i = 0; i < strings.length; i++) {
               try { strings[i] = rb.getString(strings[i]); }
               catch (java.util.MissingResourceException e) {
                  strings[i] = e.getLocalizedMessage();
               }
            }
         }
         if (runnable != null) {
            thread = new Thread(runnable);
            thread.start();
         }
      } else throw new IllegalArgumentException("Item already initialized");
      return container;
   }
   /**
    * A method will load either an object, or a zipped marshalled object
    * (zedmob) of an object, from a URL, file, or from a remote rmiregistry.
    * If the object is in a local file, it can be either inside the server's
    * jar file, or on its local file system.<p> Loading an object from a file
    * can be specified in one of three ways:<p><ul>
    * <li>As a URL; in the format file://path/name.
    * <li>As a class file; in the format path/name
    * <li>As a serialized object; in the format /path/name</ul><p>
    * @param url The URL where to get the object: file://, http://, ftp://,
    * /path/name, path/name, or //[host][:port]/[name]. The host, port,
    * and name, are all optional. If missing the host is presumed local, the
    * port 1099, and the name "main". The referenced resource can be
    * returned as a MarshalledObject, it will be extracted automatically.
    * If the URL is null, it will be assumed to be ///.
    * @return A remote reference to the object contained in the URL. It may be
    * either local, or remote to this VM.
    * @throws RemoteException if the remote registry could not be reached,
    * or the remote instance could not be be created.
    * @throws NotBoundException if the requested name is not in the registry.
    * @throws IOException if the zedmob format is invalid.
    * @throws ClassNotFoundException if a proxy was sent to the VM, and
    * proxy hosting was not enabled.
    * @throws InstantiationException when the URL specifies a class name
    * which cannot be instantiated at runtime.
    * @throws IllegalAccessException when the url specifies a class name
    * and it does not support a no-arg constructor.
    * @throws MalformedURLException if the URL is not in the format explained
    */
   public Remote getItem(String url) throws RemoteException,
      NotBoundException, IOException, ClassNotFoundException,
      InstantiationException, IllegalAccessException, MalformedURLException {
      return new Remote(Remote.getItem(url));
   }
   /**
    * This method is invoked by remote users of this object. It is expected
    * that subclasses will override this method to provide detailed usage
    * information. Use of HTML for particularly long descriptions is permitted.
    * By default this method will return: not defined.
    * @return A description of the callable methods, their arguments, returns,
    * and functionality.
    */
   public String getDescription() { return "not defined"; }
   /**
    * This method is canonically called when an proxy announces its reference
    * via the {@link Multicast Multicast} class. It is expected to receive
    * the URLs of objects that heard the announcement, and wish to be contacted.
    * @param url A //host:port/name type URL on which the 'first-contact' object
    * of a remote VM can be reached.
    */
   public void contact(String url) {}
}
