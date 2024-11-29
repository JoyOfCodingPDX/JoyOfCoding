package edu.pdx.cs.joy.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileUploadException;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

/**
 * A servlet that demonstrates how to upload a file to a web server.  It uses the Apache Commons
 * <a href="http://commons.apache.org/fileupload">FileUpload</a> library to read the file from the
 * HTTP request.
 *
 * @since Summer 2008
 */
public class FileUploadServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("text/html");
    PrintWriter pw = response.getWriter();
    pw.println("<html>");
    pw.println("<body>");

    if (JakartaServletFileUpload.isMultipartContent(request)) {
      DiskFileItemFactory factory = new DiskFileItemFactory.Builder().get();
      JakartaServletFileUpload<DiskFileItem, DiskFileItemFactory> upload = new JakartaServletFileUpload<>(factory);
      List<DiskFileItem> items;
      try {
        items = upload.parseRequest(request);

      } catch (FileUploadException ex) {
        throw new ServletException("Could not upload file", ex);
      }

      for (DiskFileItem item : items) {
        if (!item.isFormField()) {
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
