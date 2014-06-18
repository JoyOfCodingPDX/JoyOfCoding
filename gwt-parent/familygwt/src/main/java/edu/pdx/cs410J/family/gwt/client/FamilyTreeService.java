package edu.pdx.cs410J.family.gwt.client;

import com.google.gwt.user.client.rpc.RemoteService;
import edu.pdx.cs410J.family.FamilyTree;

/**
 * A remote service invoked by a GWT client
 */
public interface FamilyTreeService extends RemoteService {

  /**
   * Returns the family tree being edited by the user
   */
  public FamilyTree getFamilyTree();

  
}
