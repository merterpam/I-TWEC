package com.application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.erpam.mert.ST_TWEC.ClusterEvaluator;
import com.erpam.mert.ST_TWEC.TweetClusteringTool;
import com.erpam.mert.ST_TWEC.TweetPreprocessor;
import com.erpam.mert.ST_TWEC.model.Cluster;
import com.erpam.mert.ST_TWEC.model.Tweet;
import com.erpam.mert.utils.Utility;
import com.erpam.mert.utils.io.WordEmbeddingLoader;
import com.models.response.ClusterResponse;
import com.models.response.Label;
import com.models.response.SentimentResponse;

public class ClusterApplication {

	//Input 
	private List<Tweet> tweets;

	//Serialization - Deserialization
	private String directoryPath;

	//Lexical Clustering
	private float clusterThreshold;

	private double clusterTime;

	//Sentiment Relatedness
	private int embeddingDimension;
	private WordEmbeddingLoader wordEmbeddingDict;
	private int clusterLimit;
	private float sentimentThreshold;
	private int shortTextLength;

	private TweetClusteringTool clusterTool;
	private ClusterEvaluator evaluator;

	private String filename;

	public ClusterResponse calculateClusterStatistics(ArrayList<Cluster> clusters) {
		long startTime = System.nanoTime();

		ClusterResponse obj = new ClusterResponse(clusters);
		long endTime = System.nanoTime();
		System.out.println("Cluster statistics are calculated using embeddding in " + com.utils.Utility.convertElapsedTime(startTime, endTime) + " secs");
		return obj;
	}

	public SentimentResponse calculateLabelSentiment(int clusterLimit, ArrayList<Cluster> clusters, int dimension, float sentimentThreshold, int shortTextThreshold, WordEmbeddingLoader wordEmbeddingDict) {

		long startTime = System.nanoTime();
		ArrayList<Label> labels = new ArrayList<Label>();

		for (int i = 0, count = 0; count < clusterLimit && i < clusters.size(); i++) {
			String label = clusters.get(i).getLabel();
			if (label.length() > shortTextThreshold && !clusters.get(i).isDisplayedForMerge()) {
				labels.add(new Label(clusters.get(i).getLabel(), count, i));
				count++;
			}
		}

		SentimentResponse obj = new SentimentResponse(labels);
		obj.createSentimentMatrix(wordEmbeddingDict, dimension);
		long endTime = System.nanoTime();
		System.out.println("Labels are calculated using embeddding in " + com.utils.Utility.convertElapsedTime(startTime, endTime) + " secs");
		return obj;
	}

	public ClusterResponse clusterTweets()
	{
		long startTime = System.nanoTime();

		TweetPreprocessor tweetPreprocessor = new TweetPreprocessor(0.6f, tweets);
		tweetPreprocessor.preProcessTweets();

		try {
			clusterTool = new TweetClusteringTool(tweets);
			clusterTool.prepareSuffixTree();


			clusterTool.createClusters(clusterThreshold);
			clusterTool.removeOverlappingAndMerge();
			clusterTool.createAndExtendLabels();

			long endTime = System.nanoTime();
			clusterTime = com.utils.Utility.convertElapsedTime(startTime, endTime);

			evaluator = new ClusterEvaluator(clusterTool, clusterThreshold, filename, tweetPreprocessor.getNoContentTweets(), tweets);

			ClusterResponse cR = calculateClusterStatistics(clusterTool.getClusters());
			asyncSentimentAndSerialization();

			return cR;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		}
	}

	public void mergeClusters(SentimentResponse sR)
	{
		try {
			initializeClusterTool();
			ArrayList<Label> labels = sR.getLabels();
			for (int i = 0; i < labels.size(); i++) {
				Label l = labels.get(i);
				Cluster c = clusterTool.getClusters().get(l.getClusterIndex());
				c.setDisplayedForMerge(true);
				for (int j : l.getMergedIndexes()) {
					c.getMergedClusterIndexes().add(j);
				}
			}

			Utility.clusterToolMutex.acquire();
			Utility.serialize(clusterTool, directoryPath + "clusterTool.ser");
			Utility.clusterToolMutex.release();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public File printClusters() {
		initializeEvaluator();
		return evaluator.printClusters(directoryPath);
	}

	public File printEvaluations() {
		initializeEvaluator();

		evaluator.evaluateClusters();

		File file =  evaluator.printEvaluationResults(directoryPath);
		evaluator.printSummary(directoryPath, clusterTime);
		return file;
	}

	public SentimentResponse reCalculateSentiment()
	{
		initializeClusterTool();
		SentimentResponse sR = calculateLabelSentiment(clusterLimit, clusterTool.getClusters(), embeddingDimension, sentimentThreshold, shortTextLength, wordEmbeddingDict);

		return sR;
	}

	public ClusterResponse reCluster()
	{	
		initializeClusterTool();
		clusterTool.clearClusterFlags();
		clusterTool.createClusters(clusterThreshold);
		clusterTool.removeOverlappingAndMerge();
		clusterTool.createAndExtendLabels();

		initializeEvaluator();
		evaluator.setClusters(clusterTool.getClusters());
		evaluator.setOccurrenceMask(clusterTool.getOccurrenceMask());

		ClusterResponse cR = calculateClusterStatistics(clusterTool.getClusters());
		asyncSentimentAndSerialization();

		return cR;
	}

	private void asyncSentimentAndSerialization()
	{
		new Thread(new Runnable() {
			public void run() {
				try {
					Utility.clusterToolMutex.acquire();
					Utility.evaluatorMutex.acquire();
					Utility.sentimentMutex.acquire();

					SentimentResponse sR = calculateLabelSentiment(clusterLimit, clusterTool.getClusters(), embeddingDimension, sentimentThreshold, shortTextLength, wordEmbeddingDict);
					Utility.serialize(sR, directoryPath + "sResponse.ser");
					Utility.sentimentMutex.release();

					Utility.serialize(clusterTool, directoryPath + "clusterTool.ser");
					Utility.clusterToolMutex.release();

					Utility.serialize(evaluator, directoryPath + "evaluator.ser");
					Utility.evaluatorMutex.release();

					BufferedWriter printWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(directoryPath + "sR.json"), "UTF-8"));
					printWriter.write(sR.toJSON());
					printWriter.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void initializeClusterTool()
	{
		if(clusterTool == null)
		{
			clusterTool = Utility.deserialize(directoryPath + "clusterTool.ser", Utility.clusterToolMutex);
		}
	}

	private void initializeEvaluator()
	{
		if(evaluator == null)
		{
			evaluator = Utility.deserialize(directoryPath + "evaluator.ser", Utility.evaluatorMutex);
		}
	}

	public void setFileName(String filename)
	{
		this.filename = filename;
	}

	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
	}

	public void setClusterThreshold(float clusterThreshold) {
		this.clusterThreshold = clusterThreshold;
	}

	public void setWordEmbeddingDict(WordEmbeddingLoader wordEmbeddingDict) {
		this.wordEmbeddingDict = wordEmbeddingDict;
	}

	public void setClusterLimit(int clusterLimit) {
		this.clusterLimit = clusterLimit;
	}

	public void setSentimentThreshold(float sentimentThreshold) {
		this.sentimentThreshold = sentimentThreshold;
	}

	public void setShortTextLength(int shortTextLength) {
		this.shortTextLength = shortTextLength;
	}

	public void setEmbeddingDimension(int embeddingDimension) {
		this.embeddingDimension = embeddingDimension;
	}

	public void setTweets(ArrayList<Tweet> tweets) {
		this.tweets = tweets;
	}
}