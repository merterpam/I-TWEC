package com.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.application.ClusterApplication;
import com.erpam.mert.ST_TWEC.model.Tweet;
import com.erpam.mert.utils.io.WordEmbeddingLoader;
import com.models.response.ClusterResponse;
import com.models.response.ErrorResponse;
import com.utils.Utility;

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet(loadOnStartup=10,urlPatterns={"/uploadfile"})
@MultipartConfig
public class ClusterServlet extends HttpServlet {

	private static WordEmbeddingLoader wordEmbeddingDict;

	private static final String wordEmbeddingsPath = "/Users/mert/Documents/WordEmbedding/GoogleNews-vectors-negative300.bin";

	private static final long serialVersionUID = 1L;



	public static WordEmbeddingLoader getWordEmbeddingDict() {
		return wordEmbeddingDict;
	}

	public static void setWordEmbeddingDict(WordEmbeddingLoader wordEmbeddingDict) {
		ClusterServlet.wordEmbeddingDict = wordEmbeddingDict;
	}

	/**
	 * Initialize servlet and word embeddings directory
	 * wDirectory is the path for binary word embeddings dictionary, please set it correctly for evaluation
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		if(getWordEmbeddingDict() == null)
		{
			String wDirectory = "";
			try {
				wDirectory = (String) new InitialContext().lookup("java:comp/env/com.I-TWEC.wordEmbeddingsPath");
			} catch (NamingException e) {
				wDirectory = wordEmbeddingsPath;
				e.printStackTrace();
			}

			setWordEmbeddingDict(new WordEmbeddingLoader(wDirectory));
			getWordEmbeddingDict().generateModel();
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
		application.setWordEmbeddingDict(getWordEmbeddingDict());
		application.setClusterLimit(Integer.parseInt(request.getParameter("clusterLimit")));
		application.setSentimentThreshold(Float.parseFloat(request.getParameter("sentimentThreshold")));
		application.setShortTextLength(Integer.parseInt(request.getParameter("shortTextLength")));

		BufferedReader inputReader = new BufferedReader(new InputStreamReader(request.getPart("file").getInputStream(), "UTF8"));
		String fileName = request.getPart("file").getSubmittedFileName();
		application.setFileName(fileName.substring(0,fileName.length()-4));

		ArrayList<Tweet> tweets = Utility.readStream(inputReader);

		String responseMessage;
		if(tweets != null) {
			application.setTweets(tweets);
			ClusterResponse obj = application.clusterTweets();
			if(obj != null) {
				if(obj.getClusterSize() != 0) {
					responseMessage = obj.toJSON();
				} else {
					responseMessage = new ErrorResponse("With the given dataset, we could not form clusters").toJSON();
				}
			} else {
				responseMessage = new ErrorResponse("We could not process the data. Please try again later or try with a smaller set of data.").toJSON();;
			}
		}
		else {
			responseMessage = new ErrorResponse("Invalid Data Format. \nPlease format your data and try again").toJSON(); 
		}

		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(responseMessage);

	}

}
