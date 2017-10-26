package com.erpam.mert.controllers;

import com.erpam.mert.ST_TWEC.model.Cluster;
import com.erpam.mert.ST_TWEC.model.Tweet;
import com.erpam.mert.application.ClusteringTool;
import com.erpam.mert.models.response.ClusterResponse;
import com.erpam.mert.models.response.ErrorResponse;
import com.utils.Utility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = LexicalClusterController.class)
public class LexicalClusterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClusteringTool clusterApplication;

    private MockMultipartFile uploadedFile;


    @Before
    public void setUp() throws Exception {
        uploadedFile = new MockMultipartFile("file", "sample.txt", "", new byte[0]);
        List<Tweet> tweets = Utility.readStream(new BufferedReader(new InputStreamReader(uploadedFile.getInputStream(), "UTF8")));

        when(clusterApplication.clusterTweets(0, 0, 0, 0, tweets)).thenReturn(new ErrorResponse("Empty response"));
        when(clusterApplication.clusterTweets(10, 0, 0, 0, tweets)).thenReturn(new ClusterResponse(new ArrayList<Cluster>()));
        when(clusterApplication.recluster(0, 0, 0, 0)).thenReturn(new ClusterResponse(new ArrayList<Cluster>()));

    }

    @Test
    public void clusterUploadedFile() throws Exception {

        mockMvc.perform(fileUpload("/uploadfile")
                .file(uploadedFile)
                .param("clusterLimit", "0")
                .param("clusterThreshold", "0")
                .param("embeddingDimension", "0")
                .param("shortTextLength", "0"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"error\":\"Empty response\"}"));

        mockMvc.perform(fileUpload("/uploadfile")
                .file(uploadedFile)
                .param("clusterLimit", "10")
                .param("clusterThreshold", "0")
                .param("embeddingDimension", "0")
                .param("shortTextLength", "0"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"clusterSize\":0,\"clusters\":[null],\"evaluated\":false}"));
    }

    @Test
    public void reClusterUploadedFile() throws Exception {
        mockMvc.perform(post("/uploadClusterThreshold")
                .param("clusterLimit", "0")
                .param("clusterThreshold", "0")
                .param("embeddingDimension", "0")
                .param("shortTextLength", "0"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"clusterSize\":0,\"clusters\":[null],\"evaluated\":false}"));
    }
}