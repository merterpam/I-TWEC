package com.models.response;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.erpam.mert.TWEC.model.Cluster;

public class ClusterResponse {
	
	private int clusterSize;
	private Cluster[] clusters;
	
	private boolean evaluated;
	
	public ClusterResponse(ArrayList<Cluster> clusterArray)
	{
		clusterSize = clusterArray.size();
		clusters = clusterArray.toArray(new Cluster[1]);
		setEvaluated(false);
		
	}
	
	public String toJSON()
	{
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
