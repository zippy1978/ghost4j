package gnu.cajo.utils.extra;

import gnu.cajo.invoke.Invoke;
import gnu.cajo.invoke.Remote;

/*
 * Remote Object Invocation Priority Manager
 * Copyright (c) 2009 John Catherino
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file Nice.java is part of the cajo library.
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
 * This class wraps an object, and dynamically alters the priority of the
 * invocation thread at runtime. Normally, the thread is moved to lowest
 * priority, to reduce the burden on the serving JVM for methods requiring a
 * lot of processing resources. However the wrapper can also be used escalate
 * the invocation thread to maximum priority, for time critical tasks.
 *
 * @version 1.0, 07-Mar-09
 * @author John Catherino
 */
public final class Nice implements Invoke {
   /**
    * The presumably local object reference, for which invocation thread
    * priority will be managed.
    */
   public final Object item;
   /**
    * A flag to indicate if this wrapper is maximising or minimising the
    * priority of the invocation thread.
    */
   public final boolean fast;
   /**
    * The constuctor wraps an object reference, and allows invocations to
    * execute at either minumum or maximum priority.
    * @param item The object reference to be invoked
    * @param fast True to execute invocations at maximum priority, false to
    * invoke at minimum priorty.
    */
   public Nice(final Object item, final boolean fast) {
      this.item = item;
      this.fast = fast;
   }
   /**
    * This method intercepts the remote invocation thread, and alters its
    * priority from normal. Typically the invocation thread is set to minimum
    * priority, but can also be used to evelate the thread to maximum
    * priority, if desired. When the invocation is complete, the thread will
    * be restored to its original priority, to prevent potential side affects
    * if an invocation is local.
    * @param  method The name of the method to invoke on the wrapped object
    * @param  args The data relevant to the invocation. It can be a single
    * object, an array, or null
    * @return The method result defined by a wrapped object's implementation,
    * if any
    * @throws Exception As needed by the object, also, a
    * java.rmi.RemoteException can be thrown for network related reasons,
    * also a SecurityException can be thrown, if the codebase does not have
    * permission from the SecurityManager to alter thread priority.
    */
   public Object invoke(final String method, final Object args) throws Exception {
      final Thread thread = Thread.currentThread();
      final int priority  = thread.getPriority();
      thread.setPriority(fast ? Thread.MAX_PRIORITY : Thread.MIN_PRIORITY);
      try { return Remote.invoke(item, method, args); }
      finally { thread.setPriority(priority); }
   }
}
