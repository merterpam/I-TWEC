package com.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.application.ClusterApplication;


/**
 * Servlet implementation class DownloadServlet
 */
@WebServlet("/downloadclusters")
public class DownloadClusterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadClusterServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		String directoryPath = getServletContext().getRealPath("/tempFiles/") + session.getId() + "/";
		
		ClusterApplication application = new ClusterApplication();
		application.setDirectoryPath(directoryPath);
		File file = application.printClusters();
		
		FileInputStream fileIn = new FileInputStream(file);
		
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition","attachment;filename=" + file.getName());
		ServletOutputStream out = response.getOutputStream();

		
		byte[] outputByte = new byte[(int) file.length()];
		fileIn.read(outputByte);
		out.write(outputByte);
		
		fileIn.close();
		out.flush();
		out.close();
	}

}
