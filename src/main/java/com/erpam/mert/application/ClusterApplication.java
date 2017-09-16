package com.erpam.mert.application;

import com.erpam.mert.ST_TWEC.ClusterEvaluator;
import com.erpam.mert.ST_TWEC.TweetClusteringTool;
import com.erpam.mert.ST_TWEC.TweetPreprocessor;
import com.erpam.mert.ST_TWEC.model.Cluster;
import com.erpam.mert.ST_TWEC.model.Tweet;
import com.erpam.mert.models.Label;
import com.erpam.mert.models.response.ClusterResponse;
import com.erpam.mert.models.response.SentimentResponse;
import com.erpam.mert.utils.io.WordEmbeddingsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

@Service
public class ClusterApplication {

    private Semaphore clusterToolMutex = new Semaphore(1);
    private Semaphore sentimentMutex = new Semaphore(1);

    private SentimentResponse sentimentResponse;

    //Input
    private List<Tweet> tweets;

    //Serialization - Deserialization
    private String directoryPath;

    //Lexical Clustering
    private float clusterThreshold;

    private double clusterTime;

    //Sentiment Relatedness
    private int embeddingDimension;

    @Autowired
    private WordEmbeddingsLoader wordEmbeddingDict;

    private int clusterLimit;
    private float sentimentThreshold;
    private int shortTextLength;

    private TweetClusteringTool clusterTool;
    private ClusterEvaluator evaluator;

    private String filename;

    private ClusterResponse calculateClusterStatistics(ArrayList<Cluster> clusters) {
        long startTime = System.nanoTime();

        ClusterResponse obj = new ClusterResponse(clusters);
        long endTime = System.nanoTime();
        System.out.println("Cluster statistics are calculated using embeddding in " + com.utils.Utility.convertElapsedTime(startTime, endTime) + " secs");
        return obj;
    }

    private SentimentResponse calculateLabelSentiment(int clusterLimit, ArrayList<Cluster> clusters, int dimension, float sentimentThreshold, int shortTextThreshold, WordEmbeddingsLoader wordEmbeddingDict) {
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

    public ClusterResponse clusterTweets() {
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

    public void mergeClusters(SentimentResponse sR) {
        try {
            ArrayList<Label> labels = sR.getLabels();
            for (int i = 0; i < labels.size(); i++) {
                Label l = labels.get(i);
                Cluster c = clusterTool.getClusters().get(l.getClusterIndex());
                c.setDisplayedForMerge(true);
                for (int j : l.getMergedIndexes()) {
                    c.getMergedClusterIndexes().add(j);
                }
            }

            clusterToolMutex.acquire();
            // Utility.serialize(clusterTool, directoryPath + "clusterTool.ser");
            clusterToolMutex.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public File printClusters() {
        return evaluator.printClusters(directoryPath);
    }

    public File printEvaluations() {
        evaluator.evaluateClusters();

        File file = evaluator.printEvaluationResults(directoryPath);
        evaluator.printSummary(directoryPath, clusterTime);
        return file;
    }

    public SentimentResponse reCalculateSentiment() {
        return calculateLabelSentiment(clusterLimit, clusterTool.getClusters(), embeddingDimension, sentimentThreshold, shortTextLength, wordEmbeddingDict);
    }

    public ClusterResponse reCluster() {
        clusterTool.clearClusterFlags();
        clusterTool.createClusters(clusterThreshold);
        clusterTool.removeOverlappingAndMerge();
        clusterTool.createAndExtendLabels();

        evaluator.setClusters(clusterTool.getClusters());
        evaluator.setOccurrenceMask(clusterTool.getOccurrenceMask());

        ClusterResponse cR = calculateClusterStatistics(clusterTool.getClusters());
        asyncSentimentAndSerialization();

        return cR;
    }

    private void asyncSentimentAndSerialization() {
        try {
            sentimentMutex.acquire();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        clusterToolMutex.acquire();

                        File directory = new File(directoryPath);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }

                        setSentimentResponse(calculateLabelSentiment(clusterLimit, clusterTool.getClusters(), embeddingDimension, sentimentThreshold, shortTextLength, wordEmbeddingDict));
                        sentimentMutex.release();

                        //Utility.serialize(clusterTool, directoryPath + "clusterTool.ser");
                        clusterToolMutex.release();


                        BufferedWriter printWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(directoryPath + "sR.json"), "UTF-8"));
                        printWriter.write(sentimentResponse.toJSON());
                        printWriter.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void OnDestroy() {
        System.out.println("ClusterApplicaiton is destroyed");
    }

    public void setFileName(String filename) {
        this.filename = filename;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public void setClusterThreshold(float clusterThreshold) {
        this.clusterThreshold = clusterThreshold;
    }

    public void setWordEmbeddingDict(WordEmbeddingsLoader wordEmbeddingDict) {
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

    public SentimentResponse getSentimentResponse() throws InterruptedException {
        sentimentMutex.acquire();
        sentimentMutex.release();
        return sentimentResponse;
    }

    public void setSentimentResponse(SentimentResponse sentimentResponse) throws InterruptedException {
        this.sentimentResponse = sentimentResponse;
    }
}