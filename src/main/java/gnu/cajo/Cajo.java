package gnu.cajo;

import gnu.cajo.invoke.Remote;
import gnu.cajo.invoke.Invoke;
import gnu.cajo.utils.Multicast;
import gnu.cajo.utils.ItemServer;
import gnu.cajo.utils.extra.TransparentItemProxy;
import java.lang.reflect.Method;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;
import java.util.ArrayList;
import java.rmi.RemoteException;

/*
 * A Generic Standard Interface to the cajo distributed computing library.
 * Copyright (C) 2007 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file Cajo.java is part of the cajo library.
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
 * This class implements the Generic Standard Interface for the cajo library.
 * It is designed to work with all JRE's: 1.3 and higher.
 *
 * @version 1.0, 21-Aug-07
 * @author John Catherino
 */
public final class Cajo implements Grail {
   private final Multicast multicast;
   private final Vector items = new Vector();
   private final Registrar registrar = new Registrar(items);
   protected void finalize() throws Throwable {
      Remote.shutdown();
      super.finalize();
   }
   /**
    * This internal use only helper class maintains a registry of exported
    * objects. It cannot be instantiated outside this class, it is made
    * public only because it is being invoked reflectively. It uses UDP
    * multicasts to find other instances of registries, and shares references
    * between them.
    */
   public static final class Registrar {
      private final Vector items;
      private Registrar(Vector items) { this.items = items; }
      /**
       * This method is called either when a Cajo instance starts up, or
       * exports an object reference. All operating servers will request the
       * collection of references owned by the remote JVM, and
       * correspondingly send a copy of their registries.
       * @param multicast A reference to the announcing JVM
       * @return null To keep the multicast object listening
       * @throws Exception if the request for remote references failed, or the
       * sending its reference collection failed, for either network, or
       * application specific reasons.
       */
      public Object multicast(Multicast multicast) throws Exception {
         if (items.size() > 0 )
            multicast.item.invoke("register", items);
         register((Vector)multicast.item.invoke("request", null));
         return null; // pass any list to announcer & keep listening
      }
      /**
       * This method is called either by remote JVMs to provide a copy
       * of their registry, in response to a startup or export announcement.
       * The registries will be merged, and duplicate references discarded.
       * @param elements A collection of remote object references
       */
      public void register(Vector elements) { // include all responders
         if (elements != null && elements.size() > 0) synchronized(items) {
            for (int i = 0; i < elements.size(); i++)
               if (!(items.contains(elements.elementAt(i))))
                  items.add(elements.elementAt(i));
         }
      }
      /**
       * This method allows remote JVMs to request the contents of the
       * registry. The contents of the registry are dynamic, changing
       * with the exports, and shutdowns, of remote servers.
       * @return The collection of remote object references currently
       * registered
       */
      public Vector request() { return items; }
   };
   /**
    * This internal use only helper class scans an exported object to see if
    * it has methods matching the client method set. It cannot be
    * instantiated outside this class, it is made public only because it is
    * being invoked reflectively.
    */
   public static final class Searchable implements Invoke {
      private static final long serialVersionUID = 1L;
      private final Object object;
      private Searchable(Object object) { this.object = object; }
      /**
       * This method, invoked transparently when any remote server method is
       * called, checks the exported server object's method signatures for
       * a match with the set provided by the client.
       * @param method The name of the method to be invoked
       * @param args The arguments to be provided to the method
       * @return Boolean.TRUE if all of the requested methods matched, null
       * otherwise
       * @throws Exception For reflection based voilations, most commonly
       * when an exported server class is not public
       */
      public Object invoke(String method, Object args) throws Exception {
         if (method == null) { // special case signal
            Class  ireturns[] = (Class[])((Object[])args)[0];
            String inames[]   = (String[])((Object[])args)[1];
            Class  iargs[][]  = (Class[][])((Object[])args)[2];
            Method methods[]  = object.getClass().getMethods();
            String mnames[]   = new String[methods.length];
            Class  mreturns[] = new Class[methods.length];
            for (int i = 0; i < methods.length; i++) {
               mreturns[i] = methods[i].getReturnType();
               mnames[i]   = methods[i].getName();
            }
            searching: for (int i = 0; i < inames.length; i++) {
               for (int j = 0; j < mnames.length; j++) {
                  if (mnames[j].equals(inames[i])
                     && (ireturns[i].equals(void.class)
                     || Remote.autobox(ireturns[i]).
                     isAssignableFrom(Remote.autobox(mreturns[j])))) {
                     Class  margs[] = methods[j].getParameterTypes();
                     if (margs.length != iargs[i].length) break;
                     for (int k = 0; k < margs.length; k++)
                        if (!Remote.autobox(margs[k]).
                           isAssignableFrom(iargs[i][k])) break;
                     continue searching;
                  }
               }
               return null;
            }
            return Boolean.TRUE; // all methods were successfully matched
         } else return Remote.invoke(object, method, args);
      }
   }
   /**
    * This internal use only helper class automatically removes unresponsive
    * server references from the internal queue. It cannot be instantiated
    * outside this class, it is made public only because it is being invoked
    * reflectively.
    */
   public static final class Purger implements Invoke {
      private static final long serialVersionUID = 1L;
      private final Object object;
      private transient Vector items;
      private Purger(Object object, Vector items) {
         this.object = object;
         this.items = items;
      }
      /**
       * This method, invoked transparently when any remote server method is
       * called, monitors the progress of the invocation. If the call results
       * in a java.rmi.RemoteException, the server object reference will be
       * deleted from the internal queue automatically. All exceptions will
       * be propagated out to the client.
       * @param method The name of the method to be invoked
       * @param args The arguments to be provided to the method
       * @return The result, if any, returned by the remote procedure call
       * @throws Exception For either network, or server object logic related
       * reasons
       */
      public Object invoke(String method, Object args) throws Exception {
         try { return Remote.invoke(object, method, args); }
         catch(RemoteException x) { // if object is not responsive
            if (items != null) items.remove(object);
            throw x;
         }
      }
   }
   /**
    * The constructor configures the network address settings. If a machine
    * is operating behind a Network Adress Translating router (NAT), the
    * internal and external addresses are requried. If not, then both
    * addresses can be the same, or null arguments can be used.
    * @param port The TCP port to be used for communications, for servers
    * canonically it is the IANA assigned cajo port of 1198, for clients it
    * can be any value, including 0, meaining chosen from any unused port
    * available at the time of startup
    * @param serverHost The local network interface on which the item will
    * will be remotely invokable. Typically it is specified when the server
    * has multiple phyisical network interfaces, or is multi-homed, i.e.
    * having multiple logical network interfaces. The value can be null,
    * which will make the item accessible on <i>all</i> network interfaces,
    * this is identical to providing the special port address "0.0.0.0".
    * @param clientHost The host name, or IP address the remote client will
    * use to communicate with this server.  If null, it will be the same as
    * serverHost resolution.  This would need to be explicitly specified if
    * the server is operating behind NAT; i.e. when the server's subnet IP
    * address is <i>not</i> the same as its address outside the subnet.
    * @throws UnknownHostException If the either host address/name cannot be
    * resolved, or is invalid
    * @throws IOException If the startup announcement datagram packet could
    * not be sent
    */
   public Cajo(int port, String serverHost, String clientHost)
      throws java.net.UnknownHostException, IOException {
      Remote.config(serverHost, port, clientHost, port);
      multicast = new Multicast("224.0.23.162", port);
      multicast.listen(registrar);
      multicast.announce(registrar, 255);
      ItemServer.bind(registrar, "registrar");
   }
   /**
    * This method makes any object's public methods, whether instance or
    * static, remotely invocable. As the object being remoted is already
    * instantiated, there is no <i>artificial</i> requirement for it to
    * implement a no-arg constructor. If not all methods are safe to be made
    * remotely invocable, then wrap the object with a special-case <a href=http://en.wikipedia.org/wiki/Decorator_pattern>
    * decorator</a>.<p>
    * <i><u>Note</u>:</i> if an object is exported more than once, it will be
    * registered each time, you generally do not want to do this. Also, if
    * you plan to use the register method, to contact remote registries
    * directly, it is <i>highly</i> advisible to export all objects
    * <i>prior</i> to doing so.
    * @param object The <a href=http://en.wikipedia.org/wiki/Plain_Old_Java_Object>
    * POJO</a> to be made remotely invocable, i.e. there is no requirement
    * for it to implement any special interfaces, nor to be derived from any
    * particular class
    * @throws RemoteException If the internal registry could not be created
    * @throws IOException If the announcement datagram packet could not be
    * sent
    */
   public void export(Object object) throws IOException {
      items.add(new Remote(new Searchable(object)));
      multicast.announce(registrar, 255);
   }
   /**
    * This method finds all remotely invocable objects, supporting the
    * specified method set. The method set is a <i>client</i> defined
    * interface. It specifies the method signatures required.
    * @param methodSetInterface The interface of methods that remote objects
    * are required to support
    * @return An array of remote object references, specific to the
    * framework, implementing the specified method collection
    * @throws Exception For any network or framework specific reasons<br>
    * <tt>java.lang.IllegalArgumentException</tt> - when the provided class
    * is <i>not</i> a Java interface
    */
   public Object[] lookup(Class methodSetInterface) throws Exception {
      if (!methodSetInterface.isInterface())
         throw new IllegalArgumentException("class must be an interface");
      Method methods[] = methodSetInterface.getMethods();
      Class returns[]  = new Class[methods.length];
      String names[]   = new String[methods.length];
      Class args[][]   = new Class[methods.length][];
      for (int i = 0; i < methods.length; i++) {
         returns[i] = methods[i].getReturnType();
         names[i]   = methods[i].getName();
         args[i]    = methods[i].getParameterTypes();
      }
      Object params     = new Object[] { returns, names, args };
      ArrayList list    = new ArrayList();
      Object elements[] = items.toArray();
      for (int i = 0; i < elements.length; i++) try {
         Object match = Remote.invoke(elements[i], null, params);
         if (Boolean.TRUE.equals(match)) list.add(elements[i]);
      } catch(Exception x) { items.removeElement(elements[i]); }
      return list.toArray();
   }
   /**
    * This method instantiates a <a href=http://java.sun.com/j2se/1.3/docs/guide/reflection/proxy.html>
    * Dynamic Proxy</a> at the client, which implements the method set
    * specified. This allows a remote object reference to be used in a
    * semantically identical fashion as if it were local. The proxies can,
    * if the service object reference is serialisable, be freely passed
    * between JVMs, or persisted to storage for later use.
    * @param reference A reference to a remote object returned by the
    * lookup method of this interface, though actually, any object reference
    * implementing the client method set would work
    * @param methodSetInterface The set <i>(or subset)</i> of public methods,
    * static or instance, that the object reference implements
    * @return An object implementing the method set interface provided.
    */
   public Object proxy(Object reference, Class methodSetInterface) {
      return TransparentItemProxy.getItem(new Purger(reference, items),
         new Class[] { methodSetInterface });
   }
   /**
    * This method is used to allow clients to pass references to its own
    * local objects, to other JVMs. Normally all arguments are passed by
    * value, meaning copies are sent to the remote JVM. Sometimes however,
    * what is needed is for all users to have a reference to the same object
    * instance, on which to perform operations.
    * @param object The local client object for which a pass-by-reference is
    * sought
    * @return A proxy object, implementing all of the interfaces of the
    * wrapped object argument, it will even work in the local context
    */
   public static Object proxy(Object object) {
      HashSet interfaces = new HashSet();
      for (Class c = object.getClass(); c != null; c = c.getSuperclass())
         interfaces.addAll(Arrays.asList(c.getInterfaces()));
      return TransparentItemProxy.getItem(object,
         (Class[])interfaces.toArray(new Class[0]));
   }
   /**
    * This method is used to manually collect remote registry entries. The
    * specific addresses or host names of the remote JVMs must be known. It
    * is used to reach JVMs that for some reason are not accessible by UDP.
    * The method will also share all of its references. <i><u>Note</u>:</i>
    * you will generally want to export all of your service objects first,
    * before making calls to register.
    * @param hostname The address or domain name of a remote grail JVM
    * @param port The TCP port on which the object is being shared,
    * canonically it 1198
    * @throws Exception Various types, related to network related errors:
    * invalid host name, host unavailable, host unreachable, etc...
    */
   public void register(String hostname, int port) throws Exception {
      Object reg = Remote.getItem("//"+hostname+':'+port+"/registrar");
      if (items.size() > 0) Remote.invoke(reg, "register", items);
      registrar.register((Vector)Remote.invoke(reg, "request", null));
   }
   /**
    * Technically this method is unrelated to the class, it is used
    * to furnish library version information. It provides an execution point
    * called when the library jar is executed. It simply copies the contents
    * of the internal readme.txt file to the console.
    */
    public static void main(String args[]) throws Exception {
       java.io.InputStream is =
          Cajo.class.getResourceAsStream("/readme.txt");
       byte text[] = new byte[is.available()];
       is.read(text);
       is.close();
       System.out.println(new String(text));
    }
}
