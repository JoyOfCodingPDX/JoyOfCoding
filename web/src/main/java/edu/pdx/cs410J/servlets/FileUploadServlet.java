package edu.pdx.cs410J.servlets;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Iterator;

/**
 * A servlet that demonstrates how to upload a file to a web server.  It uses the Apache Commons
 * <a href="http://commons.apache.org/fileupload">FileUpload</a> library to read the file from the
 * HTTP request.
 *
 * @since Summer 2008
 */
public class FileUploadServlet extends HttpServlet {

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("text/html");
    PrintWriter pw = response.getWriter();
    pw.println("<html>");
    pw.println("<body>");

    if (ServletFileUpload.isMultipartContent(request)) {
      FileItemFactory factory = new DiskFileItemFactory();
      ServletFileUpload upload = new ServletFileUpload(factory);
      List items;
      try {
        items = upload.parseRequest(request);

      } catch (FileUploadException ex) {
        throw new ServletException("Could not upload file", ex);
      }

      for (Iterator iter = items.iterator(); iter.hasNext(); ) {
        FileItem item = (FileItem) iter.next();
        if (item.isFormField()) {
          // Non-file field in the form

        } else {
          String fileName = item.getName();
          String contentType = item.getContentType();
          pw.println("<h1>You uploaded " + fileName + "</h1>");
          pw.println("<h2>Content type is " + contentType + "</h2>");
          
          if (contentType.equals("text/plain")) {
            pw.println("<pre>");

            BufferedReader br = new BufferedReader(new InputStreamReader(item.getInputStream()));
            while (br.ready()) {
              pw.println(br.readLine());
            }

            pw.println("</pre>");
          }
        }
      }

    } else {
      pw.println("<h1>You did not upload a file?!</h1>");
    }

    ServletInfoServlet.dump(request, pw);

    pw.println("</body>");
    pw.println("</html>");
  }
}
