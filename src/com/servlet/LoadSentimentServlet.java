package com.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.erpam.mert.utils.Utility;
import com.models.response.SentimentResponse;

/**
 * Servlet implementation class LoadSentimentServlet
 */
@WebServlet("/loadsentiment")
public class LoadSentimentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoadSentimentServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			HttpSession session = request.getSession();
			String directoryPath = getServletContext().getRealPath("/tempFiles/") + session.getId() + "/";

			System.out.println("Serialization is done, deserializating sentiment");
			SentimentResponse sR = Utility.deserialize(directoryPath + "sResponse.ser", Utility.sentimentMutex);

			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(sR.toJSON());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
