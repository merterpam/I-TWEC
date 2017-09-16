package com.erpam.mert.controllers;

import com.erpam.mert.ST_TWEC.model.Tweet;
import com.erpam.mert.application.ClusteringTool;
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
    public @ResponseBody
    Response clusterUploadedFile(@RequestParam("clusterLimit") int clusterLimit,
                                 @RequestParam("clusterThreshold") float clusterThreshold,
                                 @RequestParam("embeddingDimension") int embeddingDimension,
                                 @RequestParam("file") MultipartFile file,
                                 @RequestParam("shortTextLength") int shortTextThreshold,

                                 HttpServletRequest request) throws IOException {

        HttpSession session = request.getSession(true);

        String directoryPath = session.getServletContext().getRealPath("/tempFiles/") + session.getId() + "/";
        String filename = file.getOriginalFilename().substring(0, file.getOriginalFilename().length() - 4);

        clusterApplication.setDirectoryPath(directoryPath);
        clusterApplication.setFilename(filename);

        BufferedReader inputReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF8"));
        ArrayList<Tweet> tweets = Utility.readStream(inputReader);

        if (tweets != null) {

            ClusterResponse obj = clusterApplication.clusterTweets(clusterLimit, clusterThreshold,
                    embeddingDimension, shortTextThreshold, tweets);
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
    public @ResponseBody
    Response reClusterUploadedFile(@RequestParam("clusterLimit") int clusterLimit,
                                   @RequestParam("clusterThreshold") float clusterThreshold,
                                   @RequestParam("embeddingDimension") int embeddingDimension,
                                   @RequestParam("shortTextLength") int shortTextThreshold) {

        return clusterApplication.recluster(clusterLimit, clusterThreshold, embeddingDimension, shortTextThreshold);
    }
}