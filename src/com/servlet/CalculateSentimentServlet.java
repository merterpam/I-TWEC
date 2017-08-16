package com.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.application.ClusterApplication;
import com.erpam.mert.utils.io.WordEmbeddingsLoader;
import com.models.response.SentimentResponse;

/**
 * Servlet implementation class CalculateSentimentServlet
 */
@WebServlet("/calculateSentiment")
public class CalculateSentimentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CalculateSentimentServlet() {
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
		application.setEmbeddingDimension(Integer.parseInt(request.getParameter("embeddingDimension")));
		application.setWordEmbeddingDict(WordEmbeddingsLoader.getInstance());
		application.setClusterLimit(Integer.parseInt(request.getParameter("clusterLimit")));
		application.setSentimentThreshold(Float.parseFloat(request.getParameter("sentimentThreshold")));
		application.setShortTextLength(Integer.parseInt(request.getParameter("shortTextLength")));
		
		SentimentResponse sR = application.reCalculateSentiment();
		
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(sR.toJSON());
		
	}

}
