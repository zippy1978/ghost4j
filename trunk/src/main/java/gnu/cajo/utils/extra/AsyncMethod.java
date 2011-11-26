package gnu.cajo.utils.extra;

import gnu.cajo.invoke.Remote;
import java.lang.reflect.InvocationTargetException;

/*
 * Asynchronous Method Invocation Class
 * Copyright (C) 2006 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file AsyncMethod.java is part of the cajo library.
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
 * This class is used to asynchronously invoke methods on objects. Each time
 * the invoke method is called, it spawns a one-use thread, to perform the
 * method invocation. From the perspective of the caller, the method returns
 * immediately. When the invocation is completed, it will callback the
 * provided listening object; invoking a method of the identical name as the
 * one invoked on the called object; passing a single object argument, which
 * is either the resulting data of the invocation, if any, or the exception
 * resulting from the invocation. Canonically the callback object should
 * therfore define up to three methods; each having the name of the method
 * called. One method accepting no arguments, one receiving the expected
 * result object, and one accepting an Exception argument. The callback can
 * be null, in which case the asynchronous invocation result will be
 * silently discarded.<p>
 * This class can be used in either of two fundamentally different ways:<ul>
 * <li>An instance can be constructed for a given called object, and its
 * corresponding callback object, for repeated use.
 * <li>The static invoke method can be used for infrequent combinations of
 * called objects and callbacks.</ul>
 * This class is intended for method invocations which require a
 * <i>significant</i> amount of time to complete. It can also be remoted,
 * which would open some considerably interesting possibilities. It could
 * even be passed <u>between</u> virtual machines, that is <i>really</i>
 * something to think about.
 *
 * @version 1.0, 22-Jan-06 Initial release
 * @author John Catherino
 */
public final class AsyncMethod implements gnu.cajo.invoke.Invoke {
   private static final long serialVersionUID = 1L;
   /**
    * This is the reference to the object, usually remote, on which to
    * invoke asynchronously. It works on local objects as well.
    */
   public final Object item;
   /**
    * This is the reference to the object, local or remote, which to call
    * back asynchronously, when the invocation has completed. (if non-null)
    */
   public final Object callback;
   /**
    * The constructor takes any object, and allows its methods to be
    * invoked asynchronously; then calls back the listening object, with the
    * result of the invocation.
    * @param  item The object to make asynchronously callable.  It may be an
    * any arbitrary object, of any type, local or remote.
    * @param  callback The object to asynchronously call back, on the
    * completion of the asynchronous method call.  It may be any arbitrary
    * object, of any type, local or remote. If the argument is null, the
    * asynchronous invocations result will silently be silently discarded.
    */
   public AsyncMethod(Object item, Object callback) {
      this.item = item;
      this.callback = callback;
   }
   /**
    * This method invokes the method specified, with the argument(s)
    * specified, if any, asynchronously.
    * @param  method The name of the method of the member object to be
    * invoked asynchronously.
    * @param  args The argument, or arguments, to be provided to the method.
    * The paramater can be null, if the method takes none, or it can be
    * an array of objects, if the method takes several. In actuality,
    * this method simply invokes the static invoke method of this class,
    * providing its local item and callback objects, to centralise the
    * asynchronous invocation processing.
    * @return null No return is provided, it is required to fulfill the
    * Invoke interface.
    */
   public Object invoke(String method, Object args) {
      invoke(item, method, args, callback);
      return null;
   }
   /**
    * This method invokes the method specified, on the object specified,
    * with the argument(s) specified, if any, asynchronously. It then calls
    * the callback object specified, if any.
    * @param item The object on which to invoke the method asynchronously.
    * It may be any arbitrary object, of any type, local or remote.
    * @param method The name of the method on the called object to be
    * invoked asynchronously.
    * @param args The argument, or arguments, to be provided to the method.
    * The paramater can be null, if the method takes none, or it can be
    * an array of objects, when the method takes several.
    * @param callback The object to asynchronously call back, on the
    * completion of the asynchronous method call.  It may be any arbitrary
    * object, of any type, local or remote. If the argument is null, the
    * invocation result will be silently discarded.
    */
   public static void invoke(final Object item, final String method,
      final Object args, final Object callback) {
      new Thread() {
         public void run() {
            Object result;
            try { result = Remote.invoke(item, method, args); }
            catch(InvocationTargetException x) {
               result = x.getTargetException();
            }
            catch(Exception x) { result = x; }
            if (callback != null)
               try { Remote.invoke(callback, method, result); }
               catch(Exception x) { x.printStackTrace(); }
         }
      }.start();
   }
}
