package com.erpam.mert.models.response;

import com.erpam.mert.ST_TWEC.model.Cluster;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class ClusterResponse implements Response {

    private int clusterSize;
    private Cluster[] clusters;

    private boolean evaluated;

    public ClusterResponse(ArrayList<Cluster> clusterArray) {
        clusterSize = clusterArray.size();
        clusters = clusterArray.toArray(new Cluster[1]);
        setEvaluated(false);

    }

    public String toJSON() {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = null;
        try {
            jsonInString = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonInString;
    }

    public int getClusterSize() {
        return clusterSize;
    }

    public Cluster[] getClusters() {
        return clusters;
    }

    public boolean isEvaluated() {
        return evaluated;
    }

    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }
}
