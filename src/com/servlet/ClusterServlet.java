package com.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.application.ClusterApplication;
import com.erpam.mert.utils.io.WordEmbeddingLoader;
import com.models.response.ClusterResponse;
import com.utils.Utility;

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet(loadOnStartup=10,urlPatterns={"/uploadfile"})
@MultipartConfig
public class ClusterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static WordEmbeddingLoader wordEmbeddingDict = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ClusterServlet() {
		super();
	}

	/**
	 * Initialize servlet and word embeddings directory
	 * wDirectory is the path for binary word embeddings dictionary, please set it correctly for evaluation
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		if(wordEmbeddingDict == null)
		{
			String wDirectory = "/Users/mert/Documents/WordEmbedding/GoogleNews-vectors-negative300.bin";
			wordEmbeddingDict = new WordEmbeddingLoader(wDirectory);
			wordEmbeddingDict.generateModel();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		ClusterApplication application = new ClusterApplication();

		HttpSession session = request.getSession(true);

		application.setDirectoryPath(getServletContext().getRealPath("/tempFiles/") + session.getId() + "/");

		application.setClusterThreshold(Float.parseFloat(request.getParameter("clusterThreshold")));

		application.setEmbeddingDimension(Integer.parseInt(request.getParameter("embeddingDimension")));
		application.setWordEmbeddingDict(wordEmbeddingDict);
		application.setClusterLimit(Integer.parseInt(request.getParameter("clusterLimit")));
		application.setSentimentThreshold(Float.parseFloat(request.getParameter("sentimentThreshold")));
		application.setShortTextLength(Integer.parseInt(request.getParameter("shortTextLength")));

		BufferedReader inputReader = new BufferedReader(new InputStreamReader(request.getPart("file").getInputStream(), "UTF8"));
		String fileName = request.getPart("file").getSubmittedFileName();
		application.setFileName(fileName.substring(0,fileName.length()-4));
		application.setTweets(Utility.readStream(inputReader));

		ClusterResponse obj = application.clusterTweets();

		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(obj.toJSON());
	}

}
