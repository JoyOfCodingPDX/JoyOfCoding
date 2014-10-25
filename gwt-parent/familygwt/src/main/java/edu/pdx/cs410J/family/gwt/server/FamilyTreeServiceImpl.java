package edu.pdx.cs410J.family.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.pdx.cs410J.family.gwt.client.FamilyTreeService;
import edu.pdx.cs410J.family.FamilyTree;
import edu.pdx.cs410J.family.web.FamilyTreeManager;

/**
 * Server-side implementation of {@link FamilyTreeService}
 */
public class FamilyTreeServiceImpl extends RemoteServiceServlet implements FamilyTreeService {
  @Override
  public FamilyTree getFamilyTree() {
    return FamilyTreeManager.getFamilyTree(getThreadLocalRequest());
  }

  @Override
  protected void doUnexpectedFailure(Throwable unhandled) {
    unhandled.printStackTrace(System.err);
    super.doUnexpectedFailure(unhandled);
  }

}
