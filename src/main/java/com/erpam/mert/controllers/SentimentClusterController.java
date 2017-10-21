package com.erpam.mert.controllers;

import com.erpam.mert.application.ClusteringTool;
import com.erpam.mert.models.response.Response;
import com.erpam.mert.models.response.SentimentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class SentimentClusterController {

    @Autowired
    private ClusteringTool clusterApplication;

    /**
     * Calculates the semantic relatedness between clusters and returns the results
     *
     * @param clusterLimit       is the number of calculated clusters
     * @param embeddingDimension is the word embeddings embeddingsDimension
     * @param shortTextLength    is the minimum length a cluster label must have to be included into the calculation
     * @return 2D array containing the sentiment relatedness score between clusters
     */
    @PostMapping("/calculateSentiment")
    @ResponseBody
    public Response calculateSentiment(@RequestParam("clusterLimit") int clusterLimit,
                                @RequestParam("embeddingDimension") int embeddingDimension,
                                @RequestParam("shortTextLength") int shortTextLength) {

        return clusterApplication.calculateLabelSentiment(clusterLimit, embeddingDimension, shortTextLength);
    }

    /**
     * Gets the sentiment relatedness score results between clusters
     *
     * @return the sentiment relatedness score results between clusters
     * @throws InterruptedException
     */
    @PostMapping("/loadsentiment")
    @ResponseBody
    public Response getSentimentResponse() throws InterruptedException {
        return clusterApplication.getSentimentResponse();
    }

    /**
     * Merges the clusters based on user's choice and reloads new clusters
     *
     * @param sentimentResponse  contains the user's merging choices
     * @param clusterLimit       is the number of calculated clusters
     * @param embeddingDimension is the word embeddings embeddingsDimension
     * @param shortTextLength    is the minimum length a cluster label must have to be included into the calculation
     * @return 2D array containing the sentiment relatedness score between clusters
     */
    @PostMapping("/refreshsentimentmerge")
    @ResponseBody
    public Response doPost(@ModelAttribute("responseData") SentimentResponse sentimentResponse,
                    @RequestParam("clusterLimit") int clusterLimit,
                    @RequestParam("embeddingDimension") int embeddingDimension,
                    @RequestParam("shortTextLength") int shortTextLength) {

        if (sentimentResponse != null && sentimentResponse.isMergeOperation())
            clusterApplication.mergeClusters(sentimentResponse);

        return clusterApplication.calculateLabelSentiment(clusterLimit, embeddingDimension, shortTextLength);
    }

    /**
     * Merges the clusters based on user's choice
     *
     * @param sentimentResponse contains the user's merging choices
     */
    @PostMapping("/uploadsentimentmerge")
    public void uploadSentimentMerge(@ModelAttribute("responseData") SentimentResponse sentimentResponse) {

        if (sentimentResponse != null && sentimentResponse.isMergeOperation())
            clusterApplication.mergeClusters(sentimentResponse);
    }
}
