package edu.pdx.cs410J.gwt.server.di;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * A GWT remote service that uses Guice to serve GWT-RPC requests.  This means that remote service
 * implementations do not need to extend {@link RemoteServiceServlet}
 */
@Singleton
public class GuiceRemoteServiceServlet extends RemoteServiceServlet {
  private final Provider<Injector> injector;

  /**
   * Zero-argument constructor is necessary for GWT tests
   */
  public GuiceRemoteServiceServlet() {
    injector = new Provider<Injector>() {
      @Override
      public Injector get() {
        return getInjector();
      }
    };
  }

  /**
   * Lame hack to get the Injector in an environment (such as a GWT test) in which the
   * servlet is not created with Guice
   * @return The Guice injector
   */
  protected Injector getInjector() {
    throw new UnsupportedOperationException("You must override this method");
  }

  @Inject
  public GuiceRemoteServiceServlet(Provider<Injector> injector) {
    this.injector = injector;
  }

  @Override
  public String processCall(String payload) throws SerializationException {
    try {
      RPCRequest req = RPC.decodeRequest(payload, null, this);

      RemoteService service = getServiceInstance(req.getMethod().getDeclaringClass());

      return RPC.invokeAndEncodeResponse(service, req.getMethod(),
        req.getParameters(), req.getSerializationPolicy());
      
    } catch (IncompatibleRemoteServiceException ex) {
      log("IncompatibleRemoteServiceException in the processCall(String) method.",
          ex);
      return RPC.encodeResponseForFailure(null, ex);
    }
  }

  @SuppressWarnings({"unchecked"})
  private RemoteService getServiceInstance(Class serviceClass) {
    return (RemoteService) injector.get().getInstance(serviceClass);
  }
  @Override
  protected void doUnexpectedFailure(Throwable unhandled) {
    unhandled.printStackTrace(System.err);
    super.doUnexpectedFailure(unhandled);
  }

}
