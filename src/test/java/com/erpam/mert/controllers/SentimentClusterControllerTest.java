package com.erpam.mert.controllers;

import com.erpam.mert.application.ClusteringTool;
import com.erpam.mert.models.response.SentimentResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = SentimentClusterController.class)
public class SentimentClusterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClusteringTool clusterApplication;

    @Before
    public void setUp() throws Exception {

        when(clusterApplication.calculateLabelSentiment(0, 0, 0)).thenReturn(new SentimentResponse());
        when(clusterApplication.getSentimentResponse()).thenReturn(new SentimentResponse());
    }

    @Test
    public void calculateSentiment() throws Exception {
        mockMvc.perform(post("/calculateSentiment")
                .param("clusterLimit", "0")
                .param("embeddingDimension", "0")
                .param("shortTextLength", "0"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }

    @Test
    public void getSentimentResponse() throws Exception {
        mockMvc.perform(post("/loadsentiment"))
                .andExpect(status().isOk());
    }

    @Test
    public void doPost() throws Exception {
        mockMvc.perform(post("/refreshsentimentmerge")
                .param("responseData", "{}")
                .param("clusterLimit", "0")
                .param("embeddingDimension", "0")
                .param("shortTextLength", "0"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }

    @Test
    public void uploadSentimentMerge() throws Exception {
        mockMvc.perform(post("/uploadsentimentmerge")
                .param("responseData", "{}"))
                .andExpect(status().isOk());
    }

}