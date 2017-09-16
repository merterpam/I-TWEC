package com.erpam.mert.models.response;

import com.erpam.mert.models.Label;
import com.erpam.mert.utils.io.WordEmbeddingsLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

public class SentimentResponse implements Response {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private ArrayList<Label> labels;

    private float[][] matrix;
    private int size;

    private boolean mergeOperation = false;

    public SentimentResponse() {

    }

    public SentimentResponse(ArrayList<Label> labels) {
        this.setLabels(labels);
        size = labels.size();
        setMatrix(new float[size][]);

        for (int i = 0; i < size; i++) {
            getMatrix()[i] = new float[size];
        }
    }

    public static SentimentResponse fromJSON(String json) {
        SentimentResponse response = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            response = mapper.readValue(json, SentimentResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
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

    public void createSentimentMatrix(WordEmbeddingsLoader wordEmbeddingDict, int dimension) {
        float minVal = Float.MAX_VALUE, maxVal = Float.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                float[] firstValue = calculateEmbeddingValues(getLabels().get(i).getLabel(), wordEmbeddingDict, dimension);
                float[] secondValue = calculateEmbeddingValues(getLabels().get(j).getLabel(), wordEmbeddingDict, dimension);
                float val = calculateCosineSimilarity(firstValue, secondValue);
                getMatrix()[i][j] = val;
                getMatrix()[j][i] = val;

                if (minVal > val)
                    minVal = val;
                if (maxVal < val)
                    maxVal = val;
            }
        }

        normalizeMatrix(minVal, maxVal);
    }

    private void normalizeMatrix(float minValue, float maxValue) {
        float ratio = maxValue - minValue;
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (minValue != 0) {
                    getMatrix()[i][j] -= minValue;
                    getMatrix()[j][i] -= minValue;
                }

                if (ratio != 1 && ratio != 0) {
                    getMatrix()[i][j] /= ratio;
                    getMatrix()[j][i] /= ratio;
                }
            }
        }
    }

    private float calculateCosineSimilarity(float[] vectorA, float[] vectorB) {
        float dotProduct = 0.0f;
        float normA = 0.0f;
        float normB = 0.0f;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        double sqrtVectors = (Math.sqrt(normA) * Math.sqrt(normB));
        if (sqrtVectors == 0)
            return 0;
        else
            return (float) (dotProduct / sqrtVectors);
    }

    private float[] calculateEmbeddingValues(String label, WordEmbeddingsLoader wordEmbeddingDict, int dimension) {
        String[] firstLabelSplits = label.split(" ");
        dimension = Math.min(dimension, wordEmbeddingDict.getVectorSize());
        float[] firstlabelValues = new float[dimension];
        for (String word : firstLabelSplits) {
            float[] dictValue = wordEmbeddingDict.getWord(word, dimension);

            if (dictValue != null) {
                for (int k = 0; k < dimension; k++) {
                    firstlabelValues[k] += dictValue[k];
                }
            }
        }

        return firstlabelValues;

    }

    @SuppressWarnings("unused")
    private float[] calculateAlgebra(String label1, String label2, WordEmbeddingsLoader wordEmbeddingDict, int dimension, boolean minus) {
        dimension = Math.min(dimension, wordEmbeddingDict.getVectorSize());
        float[] firstlabelValues = new float[dimension];
        float[] dictValue1 = wordEmbeddingDict.getWord(label1, dimension);
        float[] dictValue2 = wordEmbeddingDict.getWord(label2, dimension);
        for (int k = 0; k < dimension; k++) {
            if (minus)
                firstlabelValues[k] = (dictValue1[k] - dictValue2[k]) / 2;
            else
                firstlabelValues[k] = (dictValue1[k] + dictValue2[k]) / 2;
        }

        return firstlabelValues;
    }


    public ArrayList<Label> getLabels() {
        return labels;
    }


    private void setLabels(ArrayList<Label> labels) {
        this.labels = labels;
    }


    public float[][] getMatrix() {
        return matrix;
    }


    private void setMatrix(float[][] matrix) {
        this.matrix = matrix;
    }

    public boolean isMergeOperation() {
        return mergeOperation;
    }

    public void setMergeOperation(boolean mergeOperation) {
        this.mergeOperation = mergeOperation;
    }
}
