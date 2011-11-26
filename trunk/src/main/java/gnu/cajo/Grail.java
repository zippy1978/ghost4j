package gnu.cajo;

/*
 * A Generic Standard Interface for any distributed computing library.
 * Written by John Catherino 17-August-2007
 * This interface is offered into the public domain.
 */

/**
 * This class defines a Generic Standard Java Interface for a Distributed
 * Computing Library. It could be implemented by any reasonably sophisticated
 * framework.<p>
 * Four fundamental constraints are imposed:<ul>
 * <li> The server need only have <u>one</u> open port
 * <li> A client can operate with <u>all</u> of its ports closed
 * <li> Both the server, and the client, can be behind NAT routers
 * <li> Common classes are <i>mutual</i>; i.e. <u>no</u> remote codebase
 * serving is <i>required</i></ul><p>
 * The specific package in which this class resides, and that of its
 * implementing class, can be framework specific.
 *
 * @author <a href=http://wiki.java.net/bin/view/People/JohnCatherino>
 * John Catherino</a>
 */
public interface Grail {
   /**
    * This method makes an object's public methods, whether instance or
    * static, remotely invocable. If not all methods are safe to be made
    * remotely invocable, then wrap the object with a special-case <a href=http://en.wikipedia.org/wiki/Decorator_pattern>
    * decorator</a>.
    * Additionally, the registry hosting this reference must have the ability
    * to dynamically detect, and be detected, by other similar registries; to
    * create an implicit universal: <i>Meta-Registry.</i>
    * <p><i><u>Note</u>:</i> There is <u>no</u> <i>silly</i> requirement that
    * the object being exported implement a no-arg constructor; any
    * syntactically valid class definition will work.
    * @param object The <a href=http://en.wikipedia.org/wiki/Plain_Old_Java_Object>
    * POJO</a> to be made remotely invocable, i.e. there is no requirement
    * for it to implement any special interfaces, nor to be derived from any
    * particular class
    * @throws Exception For any network or framework specific reasons
    */
   void export(Object object) throws Exception;
   /**
    * This method finds all remotely invocable objects, supporting the
    * specified method set. The method set is a <i>client</i> defined
    * interface. It specifies the method signatures required.<p>
    * Four levels of remote object <a href=http://en.wikipedia.org/wiki/Covariance_and_contravariance_%28computer_science%29>
    * covariance</a> must be supported here:<p><ul>
    * <li> The remote object does not have to implement the method set
    * interface class, rather merely have the matching methods, if not more.
    * The package of the client interface does <i>not</i> matter either. This is
    * technically known as <a href=http://en.wikipedia.org/wiki/Liskov_substitution_principle>
    * Liskov Substitution</a>, <i>casually</i> refered to as <i>"<a href=http://en.wikipedia.org/wiki/Duck_test>
    * duck-typing</a>."</i>
    * <li> The remote object return type can be a subclass of the one
    * specified by the client. Also, if the client method specifies void,
    * any return type is acceptable.
    * <li> The remote object can throw exeptions which are subclasses of the
    * ones specified by the client, and need not throw any or all of the
    * exceptions specified.
    * <li> The interface method arguments do not have to match the specified
    * server types exactly, they can be subclasses.</ul><p>
    * <b><i><u>Notes</u>:</i></b> If the client interface has superinterfaces,
    * their methods must also be matched similarly. Method arguments, and
    * returns, are alowed to be <i>primitive</i> types as well.
    * @param methodSetInterface The interface of methods that remote objects
    * are required to support
    * @return An array of remote object references, specific to the
    * framework, implementing the specified method collection
    * @throws Exception For any network or framework specific reasons<br>
    * <tt>java.lang.IllegalArgumentException</tt> - when the provided class
    * is <i>not</i> a Java interface
    */
   Object[] lookup(Class methodSetInterface) throws Exception;
   /**
    * This method instantiates a <a href=http://java.sun.com/j2se/1.3/docs/guide/reflection/proxy.html>
    * Dynamic Proxy</a> at the client, which implements the method set
    * specified. This allows a remote object reference to be used in a
    * semantically identical fashion as if it were local. Also, the remote
    * object must do its best to invoke the correct method, when <i>null</i></b>
    * arguments are provided.
    * @param reference A reference to a remote object returned by the
    * lookup method of this interface
    * @param methodSetInterface The set <i>(or subset)</i> of client methods,
    * static or instance, that the remote object implements
    * @return A object implementing the method set interface provided, the
    * local method invocations will be transparently passed onto the remote
    */
   Object proxy(Object reference, Class methodSetInterface);
}
