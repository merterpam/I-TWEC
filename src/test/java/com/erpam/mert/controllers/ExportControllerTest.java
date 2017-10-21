package com.erpam.mert.controllers;

import com.erpam.mert.application.ClusteringTool;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ExportController.class)
public class ExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClusteringTool clusterApplication;

    private byte[] testBytes = {101, 100, 103, 127};

    @Before
    public void setUp() throws IOException {
        Mockito.when(clusterApplication.printClusters()).thenReturn(testBytes);
        Mockito.when(clusterApplication.printEvaluations()).thenReturn(testBytes);
        Mockito.when(clusterApplication.getFilename()).thenReturn("test.txt");
    }

    @Test
    public void downloadEvaluation() throws Exception {
        mockMvc.perform(post("/downloadevaluation"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(header().string("Content-Disposition", "attachment;filename=test.txt"))
                .andExpect(content().bytes(testBytes));
    }

    @Test
    public void downloadClusters() throws Exception {
        mockMvc.perform(post("/downloadclusters"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(header().string("Content-Disposition", "attachment;filename=test.txt"))
                .andExpect(content().bytes(testBytes));
    }

}