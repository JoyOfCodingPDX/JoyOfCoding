package edu.pdx.cs410J.family.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.core.client.GWT;

/**
 * The client-side interface to the <code>FamilyTreeService</code>
 */
public interface FamilyTreeServiceAsync {
  /**
   * Returns the family tree being edited by the user
   */
  void getFamilyTree(AsyncCallback async);

  public static class Factory {

    public static FamilyTreeServiceAsync create() {
      FamilyTreeServiceAsync service = GWT.create(FamilyTreeService.class);

      ServiceDefTarget endpoint = (ServiceDefTarget) service;
      String moduleRelativeURL = GWT.getModuleBaseURL() + "tree";
      endpoint.setServiceEntryPoint(moduleRelativeURL);

      return service;
    }
  }

}
