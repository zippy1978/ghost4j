package gnu.cajo.utils.extra;

import gnu.cajo.invoke.Remote;
import gnu.cajo.invoke.RemoteInvoke;
import gnu.cajo.utils.Multicast;
import java.util.Hashtable;
import java.rmi.server.ServerNotActiveException;

/*
 * Global Remote Object Reference Registry
 * Copyright (c) 2004 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file Registry.java is part of the cajo library.
 *
 * The cajo library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public Licence as published
 * by the Free Software Foundation, at version 3 of the licence, or (at your
 * option) any later version.
 *
 * Th cajo library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public Licence for more details.
 *
 * You should have received a copy of the GNU Lesser General Public Licence
 * along with this library. If not, see http://www.gnu.org/licenses/lgpl.html
 */

/**
 * This class runs as a server. It allows remote VMs to register an object
 * reference with it. Ideally, many instances of the servers would be
 * running on the network. To keep the server memory footprint manageable,
 * only one object is allowed per virtual machine IP address, any
 * subsequent entries overwrite the previous. It also runs a lightwieght task,
 * which periodically runs through the registry, purging object references
 * that have become invalid. To encourage spontaneous internetworking, the
 * registry will announce itself hourly, on the cajo hailing frequency,
 * and listen on it, for other reference announcements; which it will
 * automatically register.
 *
 * @version 1.0, 11-Oct-04 Initial release
 * @author John Catherino
 */
public final class Registry {
   private final Hashtable entries = new Hashtable();
   /**
    * This method is called solely by this registry's Multicast member
    * object, to register objects of remote server announcements.
    */
   public void multicast(Multicast multicast) {
      entries.put(multicast.iaddr.getHostAddress(), multicast.item);
   }
   /**
    * This method statically called by a remote machine to register an object
    * reference.
    * @param ref The remote reference to the object to be registered
    */
   public void post(RemoteInvoke ref) throws ServerNotActiveException {
      entries.put(java.rmi.server.RemoteServer.getClientHost(), ref);
   } // technically it couldn't ever throw this exception at a remote client
   /**
    * This method is called by remote VMs, to request a copy of the remote
    * object reference registry.
    * @return A hashtable containing all the references, keyed by their
    * server addresses
    */
   public Hashtable get() { return entries; }
   /**
    * Always a good idea; this method describes how to use the registry
    * object, and what its features are. Not unlike these comments
    * themselves.
    * @return A string destribing this server object
    */
   public String getDescription() {
      return "Welcome to the cajo item registry!\n\n" +
      "There are two ways to register a remote item reference:\n\n" +
      "\tFirst, Multicast hailing frequency announcements are\n" +
      "\tautomatically registered.\n\n" +
      "\tSecond, invocations of the 'post' method, with a remote\n" +
      "\treference are also registered.\n\n" +
      "All currently registered references can be requested via the\n" +
      "'get' method. It takes no arguments, and returns a\n" +
      "java.util.Hashtable containing the registered remote item\n" +
      "references, keyed by their server addresses. Additionally, it\n" +
      "automatically purges inactive references periodically.\n\n" +
      "Enjoy!";
   }
   /**
    * This method is used to start up the registry server. It will announce
    * its startup on the hailing frequency, to allow linkage to other
    * registries, before starting to listen on it, for new announcements.
    * <p><i>Note:</i> to build this application, it will also require an
    * RMI stub class for Remote. This means you must also run the following
    * instruction:<p>
    * <blockquote>rmic -v1.2 gnu.cajo.invoke.Remote</blockquote>
    * @param args If the server is behind a NAT router, the only argument
    * should be the network address used outside the router. Otherwise
    * provide nothing, and it will use the default local address.
    */
   public static void main(String args[]) {
      try {
         Remote.config(null, 1099, args.length > 0 ? args[0] : null, 0);
         Registry registry = new Registry();
         Remote ref = new Remote(registry);
         Multicast multicast = new Multicast();
         gnu.cajo.utils.ItemServer.bind(ref, "registry");
         multicast.listen(registry);
         Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
         do { // periodically purge dead references:
            multicast.announce(ref, 200);
            Thread.sleep(3600000L); // wait an hour
            java.util.Enumeration keys = registry.entries.keys();
            while (keys.hasMoreElements()) {
               Object key = keys.nextElement();
               Object o = registry.entries.get(key);
               try { Remote.invoke(o, "toString", null); }
               catch(Exception x) { registry.entries.remove(key); }
            }
         } while(true);
      } catch(Exception x) { x.printStackTrace(); }
   }
}
