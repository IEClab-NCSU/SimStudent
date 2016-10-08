package edu.cmu.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Servlet implementation class UploadServlet
 * @author SHRUTI
 * @see http://stackoverflow.com/questions/7114087/html5-file-upload-to-java-servlet
 */
@MultipartConfig
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		out.print("<html><body>testing servlet</body></html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		OutputStream outStream = null;
		
		try{
			String title = request.getParameter("title");
			String description = request.getParameter("description");
			System.out.println("Title: "+title);
			System.out.println("Description: "+description);
			
			// Retrieve the file that has been uploaded
			Part file = request.getPart("file");
			String filename = getFilename(file);
			InputStream filecontent = file.getInputStream();
			System.out.println("Context path: " + request.getContextPath());
			String fullPath = getServletContext().getRealPath("\\") + filename;
			System.out.println("Path: "+fullPath);
			
			/*
			 * If the input stream is linked to an ongoing stream of data – for
			 * example an HTTP response coming from an ongoing connection – then
			 * reading the entire stream once is not an option. In that case, we
			 * need to make sure we keep reading until we reach the end of the
			 * stream.
			 */
			File targetFile = new File(fullPath);
			outStream = new FileOutputStream(targetFile);
			
			// read the file from input stream and save it in directory of servlet
			byte[] buffer = new byte[8*1024];
			int bytesRead;
			while((bytesRead = filecontent.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}
			outStream.close();
			
			// set the response
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(
					"File " + filename + " successfully uploaded");
		}
		catch(Exception ex){
			System.out.println(UploadServlet.class.getName() + " doPost() " + ex.getStackTrace());
			
			if(outStream != null)
					outStream.close();
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("Something went wrong. Please try again in sometime.");
		}

	}

	/**
	 * This method retrieves the filename from the file
	 * @param file
	 * @return filename String
	 */
	private static String getFilename(Part file) {
		for (String cd : file.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				String filename = cd.substring(cd.indexOf('=') + 1).trim()
						.replace("\"", "");
				return filename.substring(filename.lastIndexOf('/') + 1)
						.substring(filename.lastIndexOf('\\') + 1);
			}
		}
		return null;
	}

}
