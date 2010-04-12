package gnu.cajo;

/*
 * A Generic Standard Interface for for distributing proxies between virtual
 * machines.
 * Written by John Catherino 17-October-2009
 * This interface is offered into the public domain.
 */

/**
 * This class defines a proxy enabled network service. It is intended both
 * as a fundamental pattern, and an example of a service specification. It
 * is implemented by services that wish to provide proxy functionality. A
 * proxy enabled network service means one that either furnishes, or accepts
 * proxies, or both.
 * <p>
 * This service would typically be implemented in situations where the
 * benefit is greater to send all, or a portion, of the <i>code</i>
 * to the receiver; instead of passing a lot of <i>data</i> back or forth.
 * This pattern is also ideal for the provision of graphical user interfaces
 * to requesting JVMs.
 * <p>
 * All services are defined as <i>plain-old</i> Java interfaces. Typically
 * these interfaces contain:<ul>
 * <li>Manifest constants: any static final objects or primitives of use
 * <li>Custom inner class definitions for:
 * <ul><li>arguments <li>returns <li>exceptions</ul>
 * <li>Custom inner interface definitons: used for either arguments, or
 * returns
 * <li>The collection of shared methods implemented by the service</ul>
 * <p>
 * A JVM may furnish as many service objects as it wishes. Normally, related
 * service interfaces are grouped into packages. Typically the javadoc
 * package.html file is used to provide a detailed explanation of the
 * service collection architecture. The package may also include any custom
 * classes shared between the service interfaces, specifically; objects,
 * interfaces, and exceptions. Once a service interface is defined and
 * distributed, it should be considered <tt>immutable</tt>. Service feature
 * enhancements should be handled through <i>subclasses</i> of the
 * original service interface, to ensure backward compatibility.
 * <p>
 * Technically speaking:<ul>
 * <li>Service method signatures may be either static or instance methods on
 * the implementation.
 * <li>Service methods should be considered <i>referentially opaque</i>; e.g.
 * invoking the same method, with the same arguments, at different times is
 * <i>not</i> guaranteed to return the same result.
 * <li><u>All</u> service methods are <i>reentrant,</i> meaning that multiple
 * clients can be executing them <i>simultaneously.</i>
 * <li>Whilst not explicitly declared, any service method invocations
 * could fail for network related reasons, resulting in a
 * java.rmi.RemoteException being thrown implicitly.
 * </ul><p>
 * <b>NB:</b> The service interface is completely cajo project agnostic, as
 * all service definitions properly should be.
 *
 * @author <a href=http://wiki.java.net/bin/view/People/JohnCatherino>
 * John Catherino</a>
 * @version 1.0, 17-Oct-09
 */
public interface Service {
   /**
    * This class is both used by servers to install proxies in a client's JVM,
    * and by clients to install proxies in a server's JVM. A proxy is a
    * serialisable object that on arrival at the target JVM, is initialised
    * with a local to the service object, on which it can communicate. A
    * service would send proxies to offload client processing and storage
    * needs. A client would send proxies to perform highly interactive
    * operation, on potentially large datasets.
    */
   interface Proxy extends java.io.Serializable {
      /**
       * called by the client on receiving a proxy object. The proxy can then
       * prepare itself for operation. However, this method should return
       * quickly; therefore, proxies requiring lengthy initialisation times
       * should perform such work in an internally created thread.
       * @param service a reference to the service object, local for client
       * proxies, remote for service proxies.
       */
      void init(Object service);
   }
   /**
    * used to send a client's proxy code to run at the service. The client's
    * code runs in the address space of the service JVM. A proxy enabled
    * service need not support client proxies, in which case a ClassNotFound
    * exception will be thrown implicitly.
    * <br><b>NB:</b> acceptance of client proxy code implies a significant
    * amount of <u>trust</u> by the server, that the sent code
    * is well behaved. However, client proxies can be made to run in an
    * applet sandbox, as a compromise strategy.
    * @param proxy an object to be sent to the service, which will
    * be provided a local reference to the service upon its arrival.
    * @return a remote reference to the proxy object, installed in the
    * service JVM, or a server proxy to the installed client proxy, over
    * which the client may now interact.
    * @throws ClassNotFoundException if the service JVM does not accept
    * client proxies.
    */
   Object sendProxy(Proxy proxy) throws ClassNotFoundException;
   /**
    * used to request a client-side running, server proxy. It is considered
    * <i>common courtesy</i> of clients to request it, if their use scenario
    * permits, before making use the service reference directly. It allows
    * the service to potentially offload some computing or storage
    * requirements to the client temporarily. It's a bit like the client
    * asking: <i>may I help with my requests?</i>
    * @return a local object also supporting the service interface, which
    * will be provided with a remote reference to its service.
    * <br><b>NB:</b> If a service does not support client proxies, it will
    * return <i><u>null</u></i>.
    * @throws ClassNotFoundException if the client JVM does not accept
    * service proxies.
    */
   Proxy requestProxy() throws ClassNotFoundException;
   /**
    * whilst semantically unrelated to this service, it is just too useful
    * to leave out. Services typically exist as either development, or
    * production. Most often, the <i>unreal</i>, i.e. test/demo/development
    * services, are being used to validate infrastructure, communication,
    * performance, provide simulated functionality, and validate functional
    * correctness. Generally speaking, test/demo/development services
    * interact with each other exclusively, and real/production services
    * behave similarly. However, exceptions <i>can</i> be permitted...
    * @return false if this service is a test, demo, or development
    * implementation; true if this is a production service.
    */
   boolean isReal();
}
