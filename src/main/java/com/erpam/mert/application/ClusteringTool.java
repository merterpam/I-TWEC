package com.erpam.mert.application;

import com.erpam.mert.ST_TWEC.model.Tweet;
import com.erpam.mert.models.response.ClusterResponse;
import com.erpam.mert.models.response.SentimentResponse;

import java.io.File;
import java.util.List;


public interface ClusteringTool {

    SentimentResponse calculateLabelSentiment(int clusterLimit, int dimension, int shortTextThreshold);

    ClusterResponse clusterTweets(int clusterLimit, float clusterThreshold, int embeddingDimension,
                                  int shortTextThreshold, List<Tweet> tweets);

    void mergeClusters(SentimentResponse sentimentResponse);

    File printClusters();

    File printEvaluations();

    ClusterResponse recluster(int clusterLimit, float clusterThreshold, int embeddingDimension, int shortTextThreshold);

    void setDirectoryPath(String directoryPath);

    void setFilename(String filename);

    SentimentResponse getSentimentResponse();
}
