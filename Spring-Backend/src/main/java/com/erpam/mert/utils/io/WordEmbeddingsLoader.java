package com.erpam.mert.utils.io;

import com.utils.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;

@Component
public class WordEmbeddingsLoader {

    private static final int MAX_SIZE = 50;
    //private static WordEmbeddingsLoader wordEmbeddingsLoader;
    private String wordEmbeddingsPath;
    private HashMap<String, Long> wordMapping;
    private RandomAccessFile stream;
    private int wordCount;
    private int vectorSize;

    public WordEmbeddingsLoader(@Value("/Users/mert/Documents/WordEmbedding/GoogleNews-vectors-negative300.bin") String newWordEmbeddingsPath) {
        wordEmbeddingsPath = newWordEmbeddingsPath;
        wordMapping = new HashMap<String, Long>();
        long startTime = System.nanoTime();
        try {
            stream = new RandomAccessFile(wordEmbeddingsPath, "r");

            wordCount = Integer.parseInt(readString());
            vectorSize = Integer.parseInt(readString());

            if (!deserializeHash()) {
                readBinaryModel();
                serializeHash();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();
        System.out.println("Word Embedding Model created in " + Utility.convertElapsedTime(startTime, endTime) + " secs");
    }

	/*public static WordEmbeddingsLoader getInstance() {
        if(wordEmbeddingsLoader == null) {
			String wDirectory = "";
			try {
				wDirectory = (String) new InitialContext().lookup("java:comp/env/com.I-TWEC.wordEmbeddingsPath");
				wordEmbeddingsPath = wDirectory;
			} catch (NamingException e) {
				e.printStackTrace();
			}
			wordEmbeddingsLoader = new WordEmbeddingsLoader();
		}
		
		return wordEmbeddingsLoader;
	} */

    public void setWordEmbeddingsPath(String newWordEmbeddingsPath) {
        wordEmbeddingsPath = newWordEmbeddingsPath;
    }

    public int getVectorSize() {
        return vectorSize;
    }

    private void readBinaryModel() throws Exception {
        String word;
        for (int i = 0; i < wordCount; i++) {

            word = readString();
            wordMapping.put(word, stream.getFilePointer());
            stream.seek(stream.getFilePointer() + vectorSize * 4);
        }

    }

    public synchronized float[] getWord(String word, int dimension) {
        float[] vector = null;
        try {
            Long position = wordMapping.get(word);
            if (position != null) {
                stream.seek(position);
                vector = new float[dimension];
                for (int j = 0; j < dimension; j++) {
                    vector[j] = readFloat();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vector;
    }


    private float readFloat()
            throws IOException {
        byte[] bytes = new byte[4];
        stream.read(bytes);
        return getFloat(bytes);
    }

    private float getFloat(byte[] b) {
        int accum = 0;
        accum = accum | (b[0] & 0xff) << 0;
        accum = accum | (b[1] & 0xff) << 8;
        accum = accum | (b[2] & 0xff) << 16;
        accum = accum | (b[3] & 0xff) << 24;
        return Float.intBitsToFloat(accum);
    }


    private String readString()
            throws IOException {
        byte[] bytes = new byte[MAX_SIZE];
        byte b = stream.readByte();
        int i = -1;
        StringBuilder sb = new StringBuilder();
        while (b != 32 && b != 10) {
            i++;
            bytes[i] = b;
            b = stream.readByte();
            if (i == 49) {
                sb.append(new String(bytes));
                i = -1;
                bytes = new byte[MAX_SIZE];
            }
        }
        sb.append(new String(bytes, 0, i + 1));
        return sb.toString();
    }

    private void serializeHash() {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(wordEmbeddingsPath + "_ser");

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(wordMapping);
            oos.close();
            fos.close();
            System.out.printf("Word mappings are serialized");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean deserializeHash() {
        try {
            FileInputStream fis = new FileInputStream(wordEmbeddingsPath + "_ser");
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(fis));
            wordMapping = (HashMap) ois.readObject();
            ois.close();
            fis.close();
            System.out.println("Serialized word mapping is found, using them for as reference");
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("There is no serialized word mapping, reading from binary model");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
