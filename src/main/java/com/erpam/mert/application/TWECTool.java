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
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
public class TWECTool implements ClusteringTool {

    /**
     * Mutex for serialization/deserialization of clusterTool field
     * <p>
     * Not used for now
     */
    private Semaphore clusterToolMutex = new Semaphore(1);

    /**
     * Mutex for serialization/deserialization of sentimentResponse field
     */
    private Semaphore sentimentMutex = new Semaphore(1);

    /**
     * Stores sentiment clustering results
     */
    private SentimentResponse sentimentResponse;

    /**
     * Stored the temporary directory path which contains lexical/sentiment results
     */
    private String directoryPath;

    /**
     * Name of the input file, used when printing output
     */
    private String filename;

    /**
     * Word Embeddings Loader
     */
    @Autowired
    private WordEmbeddingsLoader wordEmbeddingDict;

    /**
     * Tweet clustering tool
     */
    private TweetClusteringTool clusterTool;

    /**
     * Cluster evaluator tool
     */
    private ClusterEvaluator evaluator;

    /**
     * Calculates sentiment relatedness between cluster labels and returns the results in a 2D array
     *
     * @param clusterLimit        is the number of calculated clusters
     * @param embeddingsDimension is the word embeddings embeddingsDimension
     * @param shortTextThreshold  is the minimum length a cluster label must have to be included into the calculation
     * @return 2D array containing the sentiment relatedness score between clusters
     */
    public SentimentResponse calculateLabelSentiment(int clusterLimit, int embeddingsDimension, int shortTextThreshold) {
        List<Label> labels = new ArrayList<>();
        List<Cluster> clusters = clusterTool.getClusters();


        int count = 0;
        for (int i = 0; i < clusters.size(); i++) {
            Cluster cluster = clusters.get(i);
            String label = cluster.getLabel();

            if (label.length() < shortTextThreshold || cluster.isDisplayedForMerge())
                continue;

            labels.add(new Label(label, count, i));
            count++;

            if (count == clusterLimit)
                break;
        }

        SentimentResponse obj = new SentimentResponse(labels);
        obj.createSentimentMatrix(wordEmbeddingDict, embeddingsDimension);

        return obj;
    }

    /**
     * Clusters tweets lexically and asynchronously semantic relatedness
     *
     * @param clusterLimit       is the number of clusters whose semantic relatedness' are calculated
     * @param clusterThreshold   is the threshold for lexical clustering
     * @param embeddingDimension is the word embeddings dimension
     * @param shortTextThreshold is the minimum length of a label whose semantic relatedness is calculated
     * @param tweets             is the array of tweets which are clustered
     * @return results of the lexical clustering
     */
    public ClusterResponse clusterTweets(int clusterLimit, float clusterThreshold, int embeddingDimension,
                                         int shortTextThreshold, List<Tweet> tweets) {

        TweetPreprocessor tweetPreprocessor = new TweetPreprocessor(0.6f, tweets);
        tweetPreprocessor.preProcessTweets();

        try {
            clusterTool = new TweetClusteringTool(tweets);
            clusterTool.prepareSuffixTree();

            clusterTool.createClusters(clusterThreshold);
            clusterTool.removeOverlappingAndMerge();
            clusterTool.createAndExtendLabels();

            evaluator = new ClusterEvaluator(clusterTool, clusterThreshold, filename, tweetPreprocessor.getNoContentTweets(), tweets);

            ClusterResponse cR = new ClusterResponse(clusterTool.getClusters());
            asyncSentimentAndSerialization(clusterLimit, directoryPath, embeddingDimension, shortTextThreshold);

            return cR;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Merges clusters which are selected similar by the user using semantic relatedness
     *
     * @param sentimentResponse contains clusters merged by user
     */
    public void mergeClusters(SentimentResponse sentimentResponse) {
        try {
            List<Label> labels = sentimentResponse.getLabels();
            for (Label l : labels) {
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

    /**
     * Prints the clusters to the file and returns the file
     *
     * @return the file which contains clusters
     */
    public File printClusters() {
        return evaluator.printClusters(directoryPath);
    }

    /**
     * Prints the evaluations to the file and returns the file
     *
     * @return the file which contains evaluations
     */
    public File printEvaluations() {
        evaluator.evaluateClusters();

        File file = evaluator.printEvaluationResults(directoryPath);
        evaluator.printSummary(directoryPath, -1);
        return file;
    }

    /**
     * Reclusters tweets lexically and asynchronously semantic relatedness
     *
     * @param clusterLimit       is the number of clusters whose semantic relatedness' are calculated
     * @param clusterThreshold   is the threshold for lexical clustering
     * @param embeddingDimension is the word embeddings dimension
     * @param shortTextThreshold is the minimum length of a label whose semantic relatedness is calculated
     * @return results of the lexical clustering
     */
    public ClusterResponse recluster(int clusterLimit, float clusterThreshold, int embeddingDimension, int shortTextThreshold) {
        clusterTool.clearClusterFlags();
        clusterTool.createClusters(clusterThreshold);
        clusterTool.removeOverlappingAndMerge();
        clusterTool.createAndExtendLabels();

        evaluator.setClusters(clusterTool.getClusters());
        evaluator.setOccurrenceMask(clusterTool.getOccurrenceMask());

        ClusterResponse cR = new ClusterResponse(clusterTool.getClusters());
        asyncSentimentAndSerialization(clusterLimit, directoryPath, embeddingDimension, shortTextThreshold);

        return cR;
    }

    @PreDestroy
    public void OnDestroy() {
        System.out.println("I-TWEC Tool is destroyed");
    }

    public SentimentResponse getSentimentResponse() {
        try {
            sentimentMutex.acquire();
            sentimentMutex.release();
            return sentimentResponse;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }

    private void asyncSentimentAndSerialization(int clusterLimit, String directoryPath, int embeddingDimension, int shortTextThreshold) {
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

                        sentimentResponse = calculateLabelSentiment(clusterLimit, embeddingDimension, shortTextThreshold);
                        sentimentMutex.release();

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
}