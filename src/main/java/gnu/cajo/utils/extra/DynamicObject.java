package gnu.cajo.utils.extra;

import gnu.cajo.invoke.Invoke;
import gnu.cajo.invoke.Remote;

/*
 * Dynamic Server Item Functionality Redirector
 * Copyright (c) 2005 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file DynamicObject.java is part of the cajo library.
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
 * This is a dynamic server object dispatcher. Its purpose it to provide a
 * client with a single remote object reference, whose functionality can be
 * changed by the server at runtime. A server would remote this object
 * wrapper, then use its changeObject method to redefine the functionality
 * of the client's reference dynamically. <i><u>Note</u>:</i> the wrapped
 * object can be of any type, and it need not even implement the
 * gnu.cajo.invoke.Invoke interface.<p>
 *
 * While being only 5 lines of Java source code, its capability is
 * elegantly sophisticated.
 *
 * @version 1.0, 03-Nov-05 Initial release
 * @author John Catherino
 */
public final class DynamicObject implements Invoke {
   private transient Object object;
   /**
    * The constructor assigns the initial server object reference.
    * @param object The initial encapsulated server functionality to be
    * remoted through this object.
    */
   public DynamicObject(Object object) { this.object = object; }
   /**
    * This method simply redirects all invocations to the currently wrapped
    * object.
    * @param method The method name to be invoked on the wrapped object.
    * @param args The arguments to provide to the method for its invocation.
    * @return The resulting data, if any, from the invocation.
    * @throws NoSuchMethodException If no matching method can be found.
    * @throws Exception If the wrapped object rejected the invocation, for
    * application specific reasons.
    */
   public Object invoke(String method, Object args) throws Exception {
      return Remote.invoke(object, method, args);
   }
   /**
    * This method is used to dynamically redefine the functionality of
    * this wrapper at runtime. The remote client reference never changes,
    * but what the reference does, can be updated by the server repeatedly.
    * @param object The new encapsulated functionality to be provided to
    * the remote clients.
    */
   public void changeObject(Object object) { this.object = object; }
}
