package edu.pdx.cs410J.family;

import edu.pdx.cs410J.family.web.FamilyTreeManager;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

/**
 * A servlet that uploads a family tree XML file and stores it in the session.
 */
public class FamilyTreeUploadServlet extends HttpServlet {
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("text/plain");

    PrintWriter pw = response.getWriter();

    if (ServletFileUpload.isMultipartContent(request)) {
      FileItemFactory factory = new DiskFileItemFactory();
      ServletFileUpload upload = new ServletFileUpload(factory);
      List<FileItem> items;
      try {
        items = upload.parseRequest(request);

      } catch (FileUploadException ex) {
        throw new ServletException("Could not upload file", ex);
      }

      FileItem file = null;
      for (FileItem item : items) {
        if (!item.isFormField()) {
          if (file != null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Only one file can be uploaded");
            return;

          } else {
            file = item;
          }
        }
      }

      if (file == null) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No file specified");
        return;
      }

      XmlParser parser = new XmlParser(new InputStreamReader(file.getInputStream()));

      pw.println("Uploading " + file.getName());

      FamilyTree tree;
      try {
        tree = parser.parse();

      } catch (FamilyTreeException ex) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Couldn't parse " + file.getName());
        return;
      }

      FamilyTreeManager.setFamilyTree(tree, request);
      pw.println("Uploaded " + file.getName());

    } else {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No files specified");
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("text/plain");

    PrintWriter pw = response.getWriter();

    FamilyTree tree = FamilyTreeManager.getFamilyTree(request);
    if (tree == null) {
      pw.println("No tree has been uploaded");

    } else {
      TextDumper dumper = new TextDumper(pw);
      dumper.dump(tree);
    }
  }
}

