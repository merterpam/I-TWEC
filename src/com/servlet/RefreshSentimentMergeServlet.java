package com.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.application.ClusterApplication;
import com.models.response.SentimentResponse;

/**
 * Servlet implementation class UploadSentimentMergeServlet
 */
@WebServlet("/refreshsentimentmerge")
public class RefreshSentimentMergeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RefreshSentimentMergeServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String jsonData = request.getParameter("responseData");
		SentimentResponse sR = SentimentResponse.fromJSON(jsonData);
		
		HttpSession session = request.getSession();
		String directoryPath = getServletContext().getRealPath("/tempFiles/") + session.getId() + "/";
		
		ClusterApplication application = new ClusterApplication();
		
		application.setEmbeddingDimension(Integer.parseInt(request.getParameter("embeddingDimension")));
		application.setWordEmbeddingDict(ClusterServlet.wordEmbeddingDict);
		application.setClusterLimit(Integer.parseInt(request.getParameter("clusterLimit")));
		application.setSentimentThreshold(Float.parseFloat(request.getParameter("sentimentThreshold")));
		application.setShortTextLength(Integer.parseInt(request.getParameter("shortTextLength")));
		
		application.setDirectoryPath(directoryPath);
		
		if(sR != null && sR.isMergeOperation())
			application.mergeClusters(sR);
		
		sR = application.reCalculateSentiment();
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(sR.toJSON());
	}

}
