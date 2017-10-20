package com.erpam.mert.controllers;

import com.erpam.mert.application.ClusteringTool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class ExportControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ClusteringTool clusterApplication;

    @Test
    public void downloadEvaluation() throws Exception {


    }

    @Test
    public void downloadClusters() throws Exception {
    }

}