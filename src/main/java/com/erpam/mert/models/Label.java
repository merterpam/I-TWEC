package com.erpam.mert.models;

import java.io.Serializable;

public class Label implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String label;
    private int index;
    private int clusterIndex;
    private int[] mergedIndexes = new int[0];
    private boolean merged;

    public Label() {

    }

    public Label(String label, int index, int clusterIndex) {
        this.setLabel(label);
        this.setIndex(index);
        this.setClusterIndex(clusterIndex);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getClusterIndex() {
        return clusterIndex;
    }

    public void setClusterIndex(int clusterIndex) {
        this.clusterIndex = clusterIndex;
    }

    public int[] getMergedIndexes() {
        return mergedIndexes;
    }

    public void setMergedIndexes(int[] mergedIndexes) {
        this.mergedIndexes = mergedIndexes;
    }

    public boolean isMerged() {
        return merged;
    }

    public void setMerged(boolean merged) {
        this.merged = merged;
    }

}
