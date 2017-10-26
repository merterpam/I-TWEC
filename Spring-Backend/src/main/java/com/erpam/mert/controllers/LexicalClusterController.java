package com.erpam.mert.controllers;

import com.erpam.mert.ST_TWEC.model.Tweet;
import com.erpam.mert.application.ClusteringTool;
import com.erpam.mert.models.response.Response;
import com.utils.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

@RestController
public class LexicalClusterController {

    @Autowired
    private ClusteringTool clusterApplication;

    /**
     * Uploads tweets to the server and runs the clustering tool
     *
     * @param clusterLimit       is the number of clusters whose semantic relatedness' are calculated
     * @param clusterThreshold   is the threshold for lexical clustering
     * @param embeddingDimension is the word embeddings dimension
     * @param shortTextThreshold is the minimum length of a label whose semantic relatedness is calculated
     * @param file               is the uploaded file
     * @param request            is the Http request
     * @return lexical clustering results
     * @throws IOException if the uploaded file cannot be opened
     */
    @PostMapping("/uploadfile")
    @ResponseBody
    public Response clusterUploadedFile(@RequestParam("clusterLimit") int clusterLimit,
                                        @RequestParam("clusterThreshold") float clusterThreshold,
                                        @RequestParam("embeddingDimension") int embeddingDimension,
                                        @RequestParam("file") MultipartFile file,
                                        @RequestParam("shortTextLength") int shortTextThreshold,
                                        HttpServletRequest request) throws IOException {

        HttpSession session = request.getSession(true);

        String directoryPath = session.getServletContext().getRealPath("/tempFiles/") + session.getId() + "/";
        String filename = file.getOriginalFilename();
        if (filename.endsWith(".txt"))
            filename = filename.substring(0, file.getOriginalFilename().length() - 4);

        BufferedReader inputReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF8"));
        ArrayList<Tweet> tweets = Utility.readStream(inputReader);

        clusterApplication.setDirectoryPath(directoryPath);
        clusterApplication.setFilename(filename);

        return clusterApplication.clusterTweets(clusterLimit, clusterThreshold, embeddingDimension, shortTextThreshold, tweets);
    }

    /**
     * Reruns the clustering tool with the updated parameters
     *
     * @param clusterLimit       is the number of clusters whose semantic relatedness' are calculated
     * @param clusterThreshold   is the threshold for lexical clustering
     * @param embeddingDimension is the word embeddings dimension
     * @param shortTextThreshold is the minimum length of a label whose semantic relatedness is calculated
     * @return lexical clustering results
     */
    @PostMapping("/uploadClusterThreshold")
    @ResponseBody
    public Response reClusterUploadedFile(@RequestParam("clusterLimit") int clusterLimit,
                                   @RequestParam("clusterThreshold") float clusterThreshold,
                                   @RequestParam("embeddingDimension") int embeddingDimension,
                                   @RequestParam("shortTextLength") int shortTextThreshold) {

        return clusterApplication.recluster(clusterLimit, clusterThreshold, embeddingDimension, shortTextThreshold);
    }
}