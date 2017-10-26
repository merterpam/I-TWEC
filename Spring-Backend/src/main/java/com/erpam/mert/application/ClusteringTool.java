package com.erpam.mert.application;

import com.erpam.mert.ST_TWEC.model.Tweet;
import com.erpam.mert.models.response.ClusterResponse;
import com.erpam.mert.models.response.Response;
import com.erpam.mert.models.response.SentimentResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;


public interface ClusteringTool {

    SentimentResponse calculateLabelSentiment(int clusterLimit, int dimension, int shortTextThreshold);

    Response clusterTweets(int clusterLimit, float clusterThreshold, int embeddingDimension,
                           int shortTextThreshold, List<Tweet> tweets) throws UnsupportedEncodingException;

    void mergeClusters(SentimentResponse sentimentResponse);

    String getFilename();

    byte[] printClusters() throws IOException;

    byte[] printEvaluations() throws IOException;

    ClusterResponse recluster(int clusterLimit, float clusterThreshold, int embeddingDimension, int shortTextThreshold);

    void setDirectoryPath(String directoryPath);

    void setFilename(String filename);

    SentimentResponse getSentimentResponse();
}
