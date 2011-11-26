package gnu.cajo.utils;

import gnu.cajo.invoke.*;
import java.rmi.registry.*;
import java.text.DateFormat;
import java.rmi.RemoteException;
import java.net.URL;
import java.util.HashMap;
import java.io.InputStream;
import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;

/*
 * Standard Item Registry Utility
 * Copyright (C) 1999 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file ItemServer.java is part of the cajo library.
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
 * You should have received a copy of the GNU Lesser Genferal Public Licence
 * along with this library. If not, see http://www.gnu.org/licenses/lgpl.html
 */
/**
 * These routines are used for server object construction.  The objects can be
 * utilized by remote clients to compose larger, cooperative, functionality.
 * It can be used to bind all object servers for external access.  A given
 * application can bind as many objects as it wants.
 * <p>A security policy file, named "server.policy" will be loaded from the
 * local directory. By default, the policy file need only allow the following
 * permissions:
 * <p><pre> grant codeBase "file:${java.class.path}" {
 *    permission java.security.AllPermission;
 * };
 * grant {
 *    permission java.net.SocketPermission "*:1024-", "accept";
 *    permission java.net.SocketPermission "*", "connect";
 * };</pre><p>
 * This will allow the three things:<ul>
 * <li> The server codebase has full priviliges.
 * <li> Proxies can only create server sockets only on non-priviliged local ports.
 * <li> Proxies can only create client sockets to any remote port, on any host.
 * </ul><i>Note:</i> if  the {@link gnu.cajo.invoke.Remote Remote} class needs
 * configuration, it must be done <b>before</b> binding an object, since doing
 * this will use the server's assigned name and port number. Configuration is
 * accomplished by calling its {@link gnu.cajo.invoke.Remote#config config}
 * static method. Also, by default, acceptance of proxies is disabled.
 *
 * @version 1.0, 01-Nov-99 Initial release
 * @author John Catherino
 */
public class ItemServer {
  // a small utility ClassLoader, to load server plug-in objects from jar files.
  private static final class JarClassLoader extends ClassLoader {
     final String path;
     JarClassLoader(String jarFile) { path = "jar:file:" + jarFile + "!/"; }
     public Class loadClass(String name) throws ClassNotFoundException {
        try { return super.findSystemClass(name); }
        catch(ClassNotFoundException x) {
           try {
              String url = path + name.replace('.', '/') + ".class";
              InputStream is = new URL(url).openConnection().getInputStream();
              byte bytes[] = new byte[is.available()];
              is.read(bytes);
              is.close();
              Class result = defineClass(name, bytes, 0, bytes.length);
              resolveClass(result);
              return result;
           } catch(IOException y) { return null; }
        }
     }
  }
  static {
     if (System.getProperty("java.security.policy") == null)
        System.setProperty("java.security.policy", "server.policy");
     System.setProperty("java.rmi.server.useCodebaseOnly", "true");
  }
  private static Registry registry;
  private static HashMap registries;
  /**
   * Nothing happens in the default constructor of this class. This is used
   * when the server has its own internal {@link CodebaseServer CodebaseServer}
   * instance running. To take advantage the client loading capability of the
   * CodebaseServer, it must be running in the same instance of the server's VM.
   * @deprecated As only the static methods of this class are necessary now.
   */
  public ItemServer() {}
  /**
   * This constructor sets the RMI codebase property for this VM instance.
   * This is necessary if the server is serving proxies, or other types of
   * classes, <b>and</b> is using a common, or remote, code base server.
   * @deprecated Normally use of this constructor is not necessary, as the
   * CodebaseServer class manages this property automatically when used.
   * @param host The public IP address or host name, on which the codebase
   * is being served. It need not be the same physical machine as the object
   * server.
   * @param port The TCP port on which the codebase server is operating.
   * @param codebase The path/filename of the jar file containing the
   * codebase, relative to the working directory of the codebase server.
   */
  public ItemServer(String host, int port, String codebase) {
     codebase = "http://" + host + ':' + port + '/' + codebase;
     if (System.getProperty("java.rmi.server.codebase") != null)
        codebase += ' ' + System.getProperty("java.rmi.server.codebase");
     System.setProperty("java.rmi.server.codebase", codebase);
  }
  /**
   * This method enables this VM to host proxies, and accept other mobile
   * code, from other remote servers. Hosting mobile code can result in the
   * overloading of this server VM, either accidentially, or maliciously.
   * Therefore hosting should be done either in a trusted environment, or on
   * a non-essential JVM. Hosting of mobile code is disabled by default.
   * <p><i>Note:</i> accepting proxies may be disabled via a command line
   * argument at the server's startup, in which case, this method will
   * accomplish nothing.  The loading of proxies can be prohibited when
   * launching the server with the <b>-Djava.security.manager</b> switch. It
   * installs a default SecurityManager, which will not allow the loading of
   * proxies, or any other type of mobile code, and prohibits itself from
   * being replaced by the application. <i>Note:</i> this is an
   * <i>extremely</i> important command line switch; worth <u>memorizing</u>!
   * @throws SecurityException If a SecurityManager is already installed, and
   * explicitly prohibits itself from being replaced.
   */
  public static void acceptProxies() throws SecurityException {
     System.setProperty("java.rmi.server.useCodebaseOnly", "false");
     System.setSecurityManager(new java.rmi.RMISecurityManager());
  }
  /**
   * This method remotes the provided object in the local rmiregistry. The
   * registry will not be created until the binding of the first object, to
   * allow the opportunity for the {@link gnu.cajo.invoke.Remote Remote}
   * class' network settings to be {@link gnu.cajo.invoke.Remote#config
   * configured}. Strictly speaking, it performs a rebind operation on the
   * rmiregistry, to more easily allow the application to dynamically replace
   * server objects at runtime, if necessary. Since the registry is not shared
   * with other applications, checking for already bound objects is
   * unnecessary.
   * <p> The provided object will first have its setItem method invoked with
   * remote reference to itself, with which it can share with remote VMs, in
   * an application specific manner <i>(if it has one)</i>. Then it will have
   * its startThread method invoked with no argument, to signal it to start
   * its main processing thread <i>(again, if it has one)</i>.
   * @param item The object to be bound.  It may be either local to the
   * machine, or remote, it can even be a proxy from a remote object, if proxy
   * {@link #acceptProxies acceptance} was enabled for this VM.
   * @param name The name under which to bind the object reference in the
   * local rmiregistry.
   * @return A remoted reference to the object within the context of this VM's
   * settings.
   * @throws IOException For network configuration related issues.
   */
  public static Remote bind(Object item, String name) throws IOException {
     return bind(item, name, null, null, null, 0);
  }
  /**
   * This method is used to bind a server object, contained in its own jar
   * file into this server's VM, for binding at runtime. This <i>plug-in</i>
   * object's codebase will be loaded into the <i>master</i> server's runtime,
   * from the the object's jar file. The plug-in object's class will have its
   * <tt>newInstance</tt> method invoked, to create the object for binding;
   * therefore the plug-in object is required to have a no-arg constructor.
   * The instantiated class is handled to the <tt>bind(object, name)</tt>
   * method of this class, for final processing.<p>
   * This allows server objects to be modularised, into multiple standalone
   * jar files. This method can be called several times, for objects in the
   * same jar file. It can even be called with the file name of the running
   * master server jar itself. This is to aid in the creation of server
   * configuration scripts, containing the names of files, names of classes,
   * and names under which to bind the server objects. I have left the file
   * parser out, as there are two predominant approaches for this; .ini files
   * for the Windows crowd, properties files, and XML files for the rest of
   * the world. Feel free to devise your own format!<p>
   * Now for an ultra important, <i><u>super cool</u></i> optimisation:<p>
   * When compiling plug-in modules, make use of the javac <tt>-classpath
   * yourpath/masterserver.jar</tt> option. This has a really great
   * advantage:<blockquote>
   * It will prevent the needless recompilation of utility classes
   * already contained in the master server. This can <i>significantly</i>
   * reduce the size of your server plug-in object jars!</blockquote>
   * <i>Note:</i> plug-in objects typically do not support proxies. This
   * is because the system rmi codebase property is typically set by the
   * master server, to serve its own proxy jar file, when it has one. There
   * can be only one codebase for a given JVM instance.
   * @param name The name to bind the loaded server object in the registry.
   * @param item The name of the class from which to instantiate the object.
   * Example: myclass.mypackage.MyServerObject
   * @param file The absolute/relative path/filename where to find the jar
   * file.
   * Example: myitem.jar or plugins/myitem.jar or /usr/local/jar/myitem.jar
   * @return A remoted reference to the object within the context of this VM's
   * settings.
   * @throws NullPointerException If the jar file referenced in the file
   * argument could not be found in the filesystem.
   * @throws ClassNotFoundException If the jar file referenced in the file
   * argument did not contain the class specified in the item argument.
   * @throws InstantiationException If the class file specified in the item
   * argument was in an invalid, or in a corrupted format.
   * @throws IllegalAccessException If the class specified in the item
   * argument did not have a no-argument constructor.
   * @throws RemoteException If the registry did not yet exist, and could not
   * be created.
   */
  public static Remote bind(String name, String item, String file) throws
     ClassNotFoundException, InstantiationException, IllegalAccessException,
     RemoteException {
     Class c = new JarClassLoader(file).loadClass(item);
     try { return bind(c.newInstance(), name, null, null, null, 0); }
     catch(IOException x) { return null; } // can't happen, no proxy
  }
  /**
   * This method is used to bind a proxy serving object in the defalut local
   * registry. It will remote a reference to the server object, and bind it
   * under the name provided. If the proxy has a setItem method, it will be
   * called with a remote reference to the serving object. Next it will have
   * its setProxy method invoked, <i>(again if it has one)</i> with a
   * MarshalledObject containing the proxy object. Finally the object will have
   * its startThread method invoked <i>(if it has one)</i> with no argument,
   * to signal it to start its main processing thread.
   * @param item The object to be bound.  It may be either local to the
   * machine, or remote, it can even be a proxy from a remote object, if proxy
   * {@link #acceptProxies acceptance} was enabled for this VM.
   * @param name The name under which to bind the object reference in the
   * local rmiregistry.
   * @param proxy The proxy object to be sent to requesting clients, it is
   * normally encased in a java.rmi.MarshalledObject, for efficiency. If it
   * is not when passed in, it will be, automatically.
   * @return A remoted reference to the object within the context of this VM's
   * settings.
   * @throws RemoteException If the registry could not be created.
   * @throws IOException If the provided proxy object is not serialisable.
   */
  public static Remote bind(Object item, String name,
     Object proxy) throws RemoteException, IOException {
     return bind(item, name, proxy, null, null, 0);
  }
  /**
   * This method is used to bind a proxy serving object with complete
   * configurability. It will remote a reference to the server object, and
   * bind in it a local rmiregistry under the name ant the TCP port provided.
   * If the proxy has a setItem method, it will be called with a remote
   * reference to the serving object. Then it will have its setProxy method
   * invoked, <i>(if it has one)</i> with a MarshalledObject containing the
   * proxy object. The object will then have its startThread method invoked
   * <i>(again if it has one)</i> with no argument, to signal it to start its
   * main processing thread.
   * @param item The object to be bound.  It may be either local to the
   * machine, or remote, it can even be a proxy from a remote object, if proxy
   * {@link #acceptProxies acceptance} was enabled for this VM.
   * @param name The name under which to bind the object reference in the
   * a local rmiregistry.
   * @param proxy The proxy object to be sent to requesting clients, it is
   * normally encased in a java.rmi.MarshalledObject, for efficiency. If it
   * is not when passed in, it will be, automatically.
   * @param ssf The custom RMIServerSocketFactory to be used by the registry,
   * typically something using strong cryptography. This value can be null,
   * to indicate using the default socket factory for the JVM.
   * @param csf The custom RMIClientSocketFactory to be used by the registry,
   * typically something using strong cryptography. This value can be null,
   * to indicate using the default socket factory for the JVM.
   * @param port The TCP port on which both this registry, and the binding
   * object will accept connections. This value can be zero, to indicate
   * using the default registry settings for the JVM. <p><i><u>Note</u>:</i>
   * a given port will only support one given set of socket factories, the
   * first ones bound.
   * @return A remoted reference to the object within the context of this VM's
   * settings.
   * @throws RemoteException If the registry could not be created.
   * @throws IOException If the provided proxy object is not serialisable.
   */
  public static Remote bind(Object item, String name, Object proxy,
     RMIServerSocketFactory ssf, RMIClientSocketFactory csf,
     int port) throws RemoteException, IOException {
     Remote handle = item instanceof Remote ? (Remote)item : new Remote(item);
     if (proxy != null) {
        try { Remote.invoke(proxy, "setItem", handle); }
        catch(Exception x) { /* method unimplemented, odd, but OK? */ }
        if (!(proxy instanceof MarshalledObject))
           proxy = new MarshalledObject(proxy);
        try { Remote.invoke(item, "setProxy", proxy); }
        catch(Exception x) { /* method unimplemented, odd, but OK? */ }
     }
     try { Remote.invoke(item, "startThread", null); }
     catch(Exception x) { /* method unimplemented, that's OK */ }
     synchronized(ItemServer.class) { // time to bind it
        if (csf == null || ssf == null) {
           if (registry == null)
              registry = LocateRegistry.createRegistry(
                 Remote.getDefaultServerPort(),
                    Remote.getDefaultClientSocketFactory(),
                        Remote.getDefaultServerSocketFactory());
           registry.rebind(name, handle);
        } else {
           Registry custom = (Registry)registries.get(new Integer(port));
           if (custom == null) {
              custom = LocateRegistry.createRegistry(port, csf, ssf);
              registries.put(new Integer(port), custom);
           }
           custom.rebind(name, handle);
        }
     }
     return handle;
  }
  /**
   * The application loads either a zipped marshalled object (zedmob) from a
   * URL, a file, or alternately, it will fetch a remote object reference from
   * an rmiregistry. It uses the {@link gnu.cajo.invoke.Remote#getItem getitem}
   * method of the {@link gnu.cajo.invoke.Remote Remote} class. The
   * application startup will be announced over a default
   * {@link Multicast Multicast} object.  The remote interface to the object
   * will be bound under the name "main" in the local rmiregistry using the
   * local {@link #bind bind} method.
   * <p>The startup can take up to six optional configuration parameters,
   * which must be in order, progressing from most important, to most
   * least:<ul>
   * <li> args[0] The URL where to get the object: file:// http:// ftp://
   * /path/file, path/file or alternatively; //[host][:port]/[name] -- the
   * host port and name are optional, if omitted the host is presumed local,
   * the port 1099, and the name proxy. If no arguments are provided, this
   * will be assumed to be ///main.
   * <li> args[1] The optional external client host name, if using NAT.
   * <li> args[2] The optional external client port number, if using NAT.
   * <li> args[3] The optional internal client host name, if multi home/NIC.
   * <li> args[4] The optional internal client port number, if using NAT.
   * <li> args[5] The optional URL where to get a proxy object: file://
   * http:// ftp:// ..., //host:port/name (rmiregistry), /path/name
   * (serialized), or path/name (class).  It will be passed into the loaded
   * object as the sole argument to its setItem method.<ul>
   */
   public static void main(String args[]) throws Exception {
      String url        = args.length > 0 ? args[0] : null;
      String clientHost = args.length > 1 ? args[1] : null;
      int clientPort    = args.length > 2 ? Integer.parseInt(args[2]) : 0;
      String localHost  = args.length > 3 ? args[3] : null;
      int localPort     = args.length > 4 ? Integer.parseInt(args[4]) : 0;
      Remote.config(localHost, localPort, clientHost, clientPort);
      Remote main = new Remote(Remote.getItem(url));
      Object proxy = args.length > 5 ? Remote.getItem(args[5]) : null;
      new Multicast().announce(bind(main, "main", proxy), 16);
      acceptProxies();
      System.out.println("Server started: " +
         DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).
            format(new java.util.Date()));
      System.out.print("Serving item ");
      System.out.print(args[0]);
      System.out.println(" bound under the name main");
      System.out.print("internally operating on\t");
      System.out.print(Remote.getDefaultServerHost());
      System.out.print(" port ");
      System.out.println(Remote.getDefaultServerPort());
      System.out.print("externally operating on\t");
      System.out.print(Remote.getDefaultClientHost());
      System.out.print(" port ");
      System.out.println(Remote.getDefaultClientPort());
   }
}
