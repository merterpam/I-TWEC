package com.servlet;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.application.ClusterApplication;
import com.erpam.mert.utils.io.WordEmbeddingsLoader;
import com.models.response.ClusterResponse;

/**
 * Servlet implementation class ReClusterServlet
 */
@WebServlet("/uploadClusterThreshold")
public class ReClusterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReClusterServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ClusterApplication application = new ClusterApplication();
		
		HttpSession session = request.getSession();	
		
		String directoryPath = getServletContext().getRealPath("/tempFiles/") + session.getId() + "/";
		application.setDirectoryPath(directoryPath);
		
		application.setClusterThreshold(Float.parseFloat(request.getParameter("clusterThreshold")));
		
		application.setEmbeddingDimension(Integer.parseInt(request.getParameter("embeddingDimension")));
		application.setWordEmbeddingDict(WordEmbeddingsLoader.getInstance());
		application.setClusterLimit(Integer.parseInt(request.getParameter("clusterLimit")));
		application.setSentimentThreshold(Float.parseFloat(request.getParameter("sentimentThreshold")));
		application.setShortTextLength(Integer.parseInt(request.getParameter("shortTextLength")));
		
		ClusterResponse cR = application.reCluster();

		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(cR.toJSON());
		
		try {
			BufferedWriter printWriter = new BufferedWriter
					(new OutputStreamWriter(new FileOutputStream(directoryPath + "cR.json"), "UTF-8"));
			printWriter.write(cR.toJSON());
			printWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
