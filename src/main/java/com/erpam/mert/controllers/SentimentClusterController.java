package com.erpam.mert.controllers;

import com.erpam.mert.application.ClusterApplication;
import com.erpam.mert.models.response.Response;
import com.erpam.mert.models.response.SentimentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class SentimentClusterController {

    @Autowired
    private ClusterApplication clusterApplication;

    @PostMapping("/calculateSentiment")
    public @ResponseBody
    Response calculateSentiment(@RequestParam("embeddingDimension") int embeddingDimension,
                                @RequestParam("clusterLimit") int clusterLimit,
                                @RequestParam("sentimentThreshold") float sentimentThreshold,
                                @RequestParam("shortTextLength") int shortTextLength) {

        clusterApplication.setEmbeddingDimension(embeddingDimension);
        clusterApplication.setClusterLimit(clusterLimit);
        clusterApplication.setSentimentThreshold(sentimentThreshold);
        clusterApplication.setShortTextLength(shortTextLength);

        return clusterApplication.reCalculateSentiment();
    }

    @PostMapping("/loadsentiment")
    public @ResponseBody
    Response loadSentiment() throws InterruptedException {
        String directoryPath = clusterApplication.getDirectoryPath();

        System.out.println("Serialization is done, deserializating sentiment");

        return clusterApplication.getSentimentResponse();
        //return Utility.deserialize(directoryPath + "sResponse.ser", Utility.sentimentMutex);
    }

    @PostMapping("/refreshsentimentmerge")
    public @ResponseBody
    Response doPost(@ModelAttribute("responseData") SentimentResponse sentimentResponse,
                    @RequestParam("embeddingDimension") int embeddingDimension,
                    @RequestParam("clusterLimit") int clusterLimit,
                    @RequestParam("sentimentThreshold") float sentimentThreshold,
                    @RequestParam("shortTextLength") int shortTextLength) {

        clusterApplication.setEmbeddingDimension(embeddingDimension);
        clusterApplication.setClusterLimit(clusterLimit);
        clusterApplication.setSentimentThreshold(sentimentThreshold);
        clusterApplication.setShortTextLength(shortTextLength);

        if (sentimentResponse != null && sentimentResponse.isMergeOperation())
            clusterApplication.mergeClusters(sentimentResponse);

        return clusterApplication.reCalculateSentiment();
    }

    @PostMapping("/uploadsentimentmerge")
    public void uploadSentimentMerge(@ModelAttribute("responseData") SentimentResponse sentimentResponse) {

        if (sentimentResponse != null && sentimentResponse.isMergeOperation())
            clusterApplication.mergeClusters(sentimentResponse);
    }
}
