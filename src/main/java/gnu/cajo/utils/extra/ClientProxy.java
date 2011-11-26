package gnu.cajo.utils.extra;

import gnu.cajo.invoke.*;
import java.rmi.RemoteException;
import java.rmi.NoSuchObjectException;
import java.rmi.server.UnicastRemoteObject;

/*
 * Callback proxy for a firewalled client, used by a server item
 * Copyright (c) 2004 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file ClientProxy.java is part of the cajo library.
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
 * This class is used to send server item callbacks to a firewalled client.
 * A client whose firewall settings prohibit incoming socket connections is a
 * common problem. This class is called ClientProxy, as it is a stand-in
 * representation of a remote reference to a client, behind a firewall. This
 * allows server objects to be designed without regard to client firewall
 * issues. An intermediary process would give the server item a real remote
 * reference to the client object when there is no client firewall, or a
 * ClientProxy when there is. The client links the remote reference to this
 * ClientProxy, to the locally firewalled client, using an {@link ItemProxy
 * ItemProxy} object. The server item invokes methods on this client proxy
 * which result in an immediate callback invocation on the client. <p>
 * <i>Note:</i> this paradigm is <u>not</u> threadsafe! It is expected that
 * callbacks to the remote client will <i>not</i> be invoked reentrantly.
 * Correspondingly, a unique instance of this object must be given to each
 * remote client object. A practical usage <a href=http://wiki.java.net/bin/view/Communications/FirewalledClients>
 * example</a> is available online.
 *
 * @version 1.0, 28-Mar-04 Initial release
 */
public final class ClientProxy implements Invoke {
   private String method;
   private Object args;
   private boolean done, connected;
   /**
    * This is the longest value, in milliseconds, that the server will wait
    * for a client invocation to execute before it aborts it. It is set
    * by default to 5000 (5 seconds). Depending on the type of functionality
    * being performed in the client methods, this time may require adjusting.
    * When it is changed, the new value will apply for all subsequent calls.
    */
   public int timeout = 5000;
   /**
    * This is the remoted reference to the server's ClientProxy. It is passed
    * back to the client, to be used int the {@link ItemProxy ItemProxy}
    * constructor, to create a firewall traversing asynchronous callback link.
    * In order to work, logically, it can only be passed to one remote client.
    */
   public final Remote remoteThis;
   /**
    * A server creates this object, then provides the remote reference member
    * remoteThis to the client. This creates the first half of the bridge,
    * the {@link ItemProxy ItemProxy} class completes the second half.
    * @throws RemoteException If the remote reference creation fails.
    */
   public ClientProxy() throws RemoteException {
      remoteThis = new Remote(this);
   }
   /**
    * This method abruptly terminates the ClientProxy link to the server.
    * The client can detect the detachment, if it wishes, by implementing a
    * similar cutOff method. The client may also remotely invoke this method,
    * if it wishes to sever its link to the server. Any subsequent
    * invocations by the server will result in client timeout exceptions.
    * This method also calls notify(), in case there was an invocation
    * in process when this happened.
    * <i><u>Note</u>:</i> it would not make sense to call this method more
    * than once, ever, for a given object instance.
    * @throws NoSuchObjectException Should this method ever be called more
    * than once, on the same object.
    */
   public void cutOff() throws NoSuchObjectException {
      UnicastRemoteObject.unexportObject(remoteThis, true);
      done = false;
      synchronized(this) { notify(); }
   }
   /**
    * This method serves two fundamentally different, but symmetrical
    * purposes. Initially a remote {@link ItemProxy ItemProxy} calls this
    * method to have its calling thread blocked until the server item needs
    * to make an asynchronous callback. Secondly, the server item will also
    * invoke this method, and will have its thread blocked, until the
    * resulting data, or exception, is returned from the firewalled client,
    * via its ItemProxy.
    * @param method The name of the method on the firewalled remote client
    * to be invoked asynchronously.
    * @param args The data to be provided the method of the callback method,
    * <i>or</i> data resulting from the client callback.
    * @return The result of the client object callback.
    * @throws InterruptedException If the client is not listening, or if
    * the callback timeout has expired.
    * @throws RemoteException For any network related failures.
    * @throws Exception For any client specific reasons.
    */
   public synchronized Object invoke(String method, Object args)
      throws Exception {
      if (method == null) {     // client callback response thread
         connected = true;      // indicate client is connected
         this.args = args;      // save the callback result
         done = true;           // indicate callback complete
         notify();              // wake the server item thread
         wait();                // suspend the client callback thread
         return new Object[] { this.method, this.args };
      } else if (method.equals("cutOff") && (args == null ||
         (args instanceof Object[] && ((Object[])args).length == 0))) {
         cutOff();              // client or server wants to terminate
         return null;           // connexion to client is now severed
      } else {                  // server callback invocation thread
         if (!connected) wait(timeout); // delay for initial client connect
         if (!done) throw new InterruptedException("Client not listening");
         this.method = method;  // save the client method to be invoked
         this.args   = args;    // save the data to provide the invocation
         done = false;          // indicate callback pending
         notify();              // wake the client callback thread
         wait(timeout);         // suspend the server item thread
         if (!done) throw new InterruptedException("Callback Timeout");
         if (this.args instanceof Exception) throw (Exception)this.args;
         return this.args;      // return the callback result
      }
   }
}
