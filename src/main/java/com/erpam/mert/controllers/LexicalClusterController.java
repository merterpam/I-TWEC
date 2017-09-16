package com.erpam.mert.controllers;

import com.erpam.mert.ST_TWEC.model.Tweet;
import com.erpam.mert.application.ClusterApplication;
import com.erpam.mert.models.response.ClusterResponse;
import com.erpam.mert.models.response.ErrorResponse;
import com.erpam.mert.models.response.Response;
import com.utils.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

@RestController
public class LexicalClusterController {

    @Autowired
    private ClusterApplication clusterApplication;

    @PostMapping("/uploadfile")
    public @ResponseBody
    Response clusterUploadedFile(@RequestParam("file") MultipartFile file,
                                 @RequestParam("embeddingDimension") int embeddingDimension,
                                 @RequestParam("sentimentThreshold") float sentimentThreshold,
                                 @RequestParam("shortTextLength") int shortTextLength,
                                 @RequestParam("clusterLimit") int clusterLimit,
                                 @RequestParam("clusterThreshold") float clusterThreshold,
                                 HttpServletRequest request, HttpServletResponse response) throws IOException {


        HttpSession session = request.getSession(true);

        clusterApplication.setDirectoryPath(session.getServletContext().getRealPath("/tempFiles/") + session.getId() + "/");

        clusterApplication.setClusterThreshold(clusterThreshold);

        clusterApplication.setEmbeddingDimension(embeddingDimension);
        clusterApplication.setClusterLimit(clusterLimit);
        clusterApplication.setSentimentThreshold(sentimentThreshold);
        clusterApplication.setShortTextLength(shortTextLength);

        BufferedReader inputReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF8"));
        String fileName = file.getOriginalFilename();
        clusterApplication.setFileName(fileName.substring(0, fileName.length() - 4));

        ArrayList<Tweet> tweets = Utility.readStream(inputReader);

        if (tweets != null) {
            clusterApplication.setTweets(tweets);
            ClusterResponse obj = clusterApplication.clusterTweets();
            if (obj != null) {
                if (obj.getClusterSize() != 0) {
                    return obj;
                } else {
                    return new ErrorResponse("We could not form any clusters with the given dataset");
                }
            } else {
                return new ErrorResponse("We could not process the data. Please try again later or try with a smaller set of data.");
            }
        } else {
            return new ErrorResponse("Invalid Data Format. \nPlease format your data and try again");
        }
    }


    @PostMapping("/uploadClusterThreshold")
    public @ResponseBody
    Response reClusterUploadedFile(@RequestParam("clusterThreshold") float clusterThreshold) {

        clusterApplication.setClusterThreshold(clusterThreshold);
        return clusterApplication.reCluster();
    }
}