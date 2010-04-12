package gnu.cajo.utils.extra;

import gnu.cajo.invoke.Invoke;
import gnu.cajo.invoke.Remote;

/*
 * Service Method Availability Test
 * Copyright (c) 2007 John Catherino
 * The cajo project: https://cajo.dev.java.net
 *
 * For issues or suggestions mailto:cajo@dev.java.net
 *
 * This file Implements.java is part of the cajo library.
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
 * This class takes any service object, and allows its methods to be tested
 * for <u>existence</u>, <i>without</i> having to invoke them. This is
 * particularly important to enable <a href=http://en.wikipedia.org/wiki/Liskov_substitution_principle>
 * Type Substitution</a>. In other words; a client could check if a service
 * object implemented its <a href=http://weblogs.java.net/blog/cajo/archive/2006/12/dynamic_client.html>
 * specific</a> interface.<p>
 * When a service is remoted, wrapped in this object; clients can invoke the
 * methods they <i>assume</i> this object implements. Instead of the normal
 * method return; it will return a boolean. True will indicate that the
 * service supports the method, otherwise it will return false. No side
 * effects to the wrapped object will be incurred by this service. In
 * fact; the service object will be completely unaware of the testing.<p>
 * One <i>suggested</i> protocol: A service object could provide a public
 * <tt>getImplements();</tt> method, which would return a remote reference to
 * the service object, wrapped by an <tt>Implements</tt> instance. A new
 * reference needn't be created for each call, and in fact, the reference can
 * even be instantiated lazily, i.e. if needed.<p>
 * As a template:<p>
 * <pre><tt> private Remote myImplements;
 * public RemoteInvoke getImplements() throws java.rmi.RemoteException {
 *    return myImplements == null ?
 *       myImplements = new Remote(new Implements(this)) : myImplements;
 * }</tt></pre>
 *
 * @version 1.0, 03-Apr-07
 * @author John Catherino
 */
public final class Implements implements Invoke {
   private final Object service;
   /**
    * The constructor takes any service object, and allows it to be remotely
    * tested for method <i>existence,</i> without having to <U>invoke</u> it.
    * @param  service The service object to make remotely testable for method
    * callability.
    */
   public Implements(Object service) { this.service = service; }
   /**
    * Instead of actually invoking the method on the target object, this
    * object will test for the existence of the method on the target object,
    * either and return true or false.
    * @param  method The method to check for existence
    * @param args The signature of the particular method would accept, the
    * arguments can be null, or subclasses of the expected arguments.
    * Typically these are represented by class, but they also can be passed
    * in by instance.
    * @return Instead of the method's normal return, if any, it will be true
    * if the service supports this method and signature, otherwise false.
    */
   public Object invoke(String method, Object args) {
      if (args instanceof Object[]) {
         if (((Object[])args).length == 0) try {
            service.getClass().getMethod(method, null);
            return Boolean.TRUE;
         } catch(NoSuchMethodException x) { return Boolean.FALSE; }
         if (args instanceof Class[])
            return Remote.findBestMethod(service, method, (Class[])args)
               != null ? Boolean.TRUE : Boolean.FALSE;
         Object[] o_args = (Object[])args;
         Class[]  c_args = new Class[o_args.length];
         for(int i = 0; i < o_args.length; i++)
            c_args[i] = o_args[i] != null ? o_args[i].getClass() : null;
         return Remote.findBestMethod(service, method, c_args) != null
            ? Boolean.TRUE : Boolean.FALSE;
      } else if (args != null) {
         Class[] c_arg = args instanceof Class ?
            new Class[] { (Class)args } : new Class[]{ args.getClass() };
         return Remote.
            findBestMethod(service, method, c_arg) != null ?
               Boolean.TRUE : Boolean.FALSE;
      } else try {
         service.getClass().getMethod(method, null);
         return Boolean.TRUE;
      } catch(NoSuchMethodException x) { return Boolean.FALSE; }
   }
}
