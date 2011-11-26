package gnu.cajo.utils.extra;

import gnu.cajo.invoke.*;

/*
 * Callback proxy for a remote item, used by a firewalled client
 * Copyright (c) 2004 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file ItemProxy.java is part of the cajo library.
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
 * This class is used to receive server item callbacks by a firewalled
 * client. A client whose firewall settings prohibit incoming socket
 * connections is a common problem. To solve this, a client would request
 * a remote reference to a {@link ClientProxy ClientProxy} from the server.
 * It would then use an ItemProxy to link the remote item to the local client
 * item. This class is a special purpose thread, which will make an outgoing
 * call to the remote ClientProxy. This outgoing call will be blocked until
 * the server has some data for it, at which point it will wake this thread,
 * causing it to return with the callback method to be invoked on the local
 * client object, and the data to be provided it. This object will call its
 * local client, and return the resulting data, or exception to the server.
 * This will result in this thread being put back to sleep again, until there
 * is another callback. This lets local client objects be designed without
 * regard for whether they will be behind a firewall or not. This technique
 * enables asynchronous server callbacks using the client's <i>outgoing</i>
 * socket, thereby solving the firewall issue. The development of this
 * process was originally championed by project member Fredrik Larsen.<p>
 *
 * Normally, a client would not invoke a method on the remote reference
 * it receives to the server's ClientProxy object. However, if the client
 * wishes to forcibly terminate its connection with the server, it can
 * invoke a no-arg cutOff method on the reference. (See the note below)</p>
 *
 * <i><u>Note</u>:</i> The server could cut its connection to the client
 * at any time, either intentionally by invoking its cutOff method, or
 * worse, by a server crash. If the client wishes to be notified of this
 * event, it must define a null argument method called <tt>cutOff</tt>. This
 * will be invoked by the ItemProxy, in that event the cutOff method must
 * accept a single argument, of type Exception, it will describe the reason
 * behind the disconnection. A practical usage <a href=http://wiki.java.net/bin/view/Communications/FirewalledClients>
 * example</a> is available online.
 *
 * @version 1.0, 28-Mar-04 Initial release
 */
public final class ItemProxy extends Thread {
   private final Object item, client;
   /**
    * The constructor links the remote object to the firewalled client.
    * It will automatically start the thread, which will call the remote
    * {@link ClientProxy ClientProxy}, blocking until there is a callback
    * method to be invoked.
    * @param item A remote reference to a ClientProxy, from which the remote
    * object will invoke asynchronous callbacks
    * @param client The firewalled local object that wishes to receive
    * asynchronous callbacks
    */
   public ItemProxy(RemoteInvoke item, Object client) {
      this.item   = item;
      this.client = client;
      setDaemon(true);
      start();
   }
   /**
    * The processing thread, and the crux of this technique. This thread
    * starts out by calling the remote {@link ClientProxy ClientProxy}, to
    * enter a blocking wait. The ClientProxy will wake the thread, providing
    * it an object array containing two things; the name of the method to be
    * called on the local object, and the data to be provided it. This thread
    * will invoke the local object's method, and return the result, or
    * exception, to the ClientProxy, beginning the cycle again.
    */
   public void run() {
      try {
         Object args = null;
         while(true) {
            args = Remote.invoke(item, null, args);
            String method = (String)((Object[])args)[0];
            args = ((Object[])args)[1];
            try { args = Remote.invoke(client, method, args); }
            catch(Exception x) { args = x; }
         }
      } catch(Exception x) {
         try { Remote.invoke(client, "cutOff", x); }
         catch(Exception y) {}
      }
   }
}
