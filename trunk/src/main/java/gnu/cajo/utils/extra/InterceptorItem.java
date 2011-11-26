package gnu.cajo.utils.extra;

import gnu.cajo.invoke.*;

/*
 * Item Interceptor
 * Copyright (c) 2004 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file InteceptorItem.java is part of the cajo library.
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
 * This class is used to transparently intercept method invocations on any
 * given object reference. It is typically used to dynamically substitute
 * functionality, without having to change the implementation of the
 * intercepted object. If the interceptor object does not wish to process a
 * particular invocation, it will be automatically passed to the intercepted
 * object for processing.
 *
 *
 * @version 1.0, 22-Sep-07 Initial release
 * @author John Catherino
 */
public class InterceptorItem implements Invoke {
   /**
    * The interceptor object. This object will recieve the remote invocation
    * first, on a method of matching signature. The interceptor has the
    * option to process the invocation itself, or pass it on to the
    * intercepted object for processing. It is declared as public to allow the
    * reference of the InterceptorItem, and its interceptor object, from a
    * single instance of InterceptorItem.
    */
   public final Object interceptor;
   /**
    * This is the object to be intercepted. Since it has no knowlege of the
    * interception it's structure need not be changed in any way to
    * accomodate it. It is declared as public to allow the reference of the
    * InterceptorItem, and its intercepted object, from a single instance of
    * InterceptorItem.
    */
   public final Object item;
   /**
    * This object is used a signal from an interceptor object. When it is
    * returned from a method invocation, it means that the interceptor object
    * wants to have the intercepted object process the invocation instead.
    */
   public static final Object CONTINUE = new Object();
   /**
    * This creates the object, to intercept the target object's calls. The
    * class is not declared final, to allow no-arg intercepting items to be
    * subclased, if needed.
    * @param item The object to receive the client invocation. It can be
    * local, remote, or even a proxy.
    * @param interceptor The object to receive the calls prior to the
    * intercepted item's operation. It can be local, remote, or even a proxy.
    */
   public InterceptorItem(Object item, Object interceptor) {
      this.item = item;
      this.interceptor = interceptor;
   }
   /**
    * This method intercepts the incoming calls. The interceptor object has
    * three options:<ul><p>
    * <li>Process the method invocation itself, effectively <i>overriding</i>
    * the functionality of the intercepted object.
    * <li>Throw an exception, effectively <i>excepting</i> the method of the
    * intercepted object.
    * <li>Return the static final {@link #CONTINUE InterceptorItem.CONTINUE}
    * object, indicating that the intercepted object should process the
    * invocation.
    * </ul><p><i><u>Note</u>:</i> if the interceptor object does not have a
    * method comparably matching what is being invoked, it will be
    * <i>automatically</i> passed on to the intercepted object. This allows
    * the interceptor to define only the methods it wishes to potentially
    * override/except.
    * @param method The intercepted object's public method being called.
    * @param  args The arguments being passed to the intercepted object's
    * method.
    * @return The actual or intercepted result, if any, from the invocation.
    * @throws NoSuchMethodException If the method/agruments signature cannot
    * be matched to the internal object's public method interface.
    * @throws java.rmi.RemoteException For any network realated failures.
    * @throws Exception If the interceptor object's method rejects the
    * invocation, or if it has been rejected by the intercepted object.
    */
   public Object invoke(String method, Object args) throws Exception {
      Object result;
      try { result = Remote.invoke(interceptor, method, args); }
      catch(NoSuchMethodException x) { result = CONTINUE; }
      return result.equals(CONTINUE) ? Remote.invoke(item, method, args) :
         result;
   }
}