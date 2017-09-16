package com.erpam.mert.controllers;

import com.erpam.mert.application.ClusterApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
public class ExportController {

    @Autowired
    private ClusterApplication clusterApplication;

    @PostMapping("/downloadevaluation")
    public ResponseEntity<byte[]> downloadEvaluation() throws IOException {

        File file = clusterApplication.printEvaluations();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        responseHeaders.set("Content-Disposition", "attachment;filename=" + file.getName());

        return new ResponseEntity<>(Files.readAllBytes(file.toPath()), responseHeaders, HttpStatus.CREATED);
    }

    @PostMapping("/downloadclusters")
    public ResponseEntity<byte[]> downloadClusters() throws IOException {

        File file = clusterApplication.printClusters();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        responseHeaders.set("Content-Disposition", "attachment;filename=" + file.getName());

        return new ResponseEntity<>(Files.readAllBytes(file.toPath()), responseHeaders, HttpStatus.CREATED);
    }
}
