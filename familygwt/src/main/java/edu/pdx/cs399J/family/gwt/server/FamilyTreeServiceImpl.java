package edu.pdx.cs399J.family.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.pdx.cs399J.family.gwt.client.FamilyTreeService;
import edu.pdx.cs399J.family.FamilyTree;
import edu.pdx.cs399J.family.web.FamilyTreeManager;

/**
 * Server-side implementation of {@link FamilyTreeService}
 */
public class FamilyTreeServiceImpl extends RemoteServiceServlet implements FamilyTreeService {
  public FamilyTree getFamilyTree() {
    return FamilyTreeManager.getFamilyTree(getThreadLocalRequest());
  }
}
