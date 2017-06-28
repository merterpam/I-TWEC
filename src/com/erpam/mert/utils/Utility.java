package com.erpam.mert.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.Semaphore;

public class Utility {

	public static Semaphore clusterToolMutex = new Semaphore(1);
	public static Semaphore evaluatorMutex = new Semaphore(1);
	public static Semaphore sentimentMutex = new Semaphore(1);
	
	public static <T> void serialize(final T serializableObject, final String filePath)
	{
		FileOutputStream fos;
		try {
			
			long startTime = System.nanoTime();

			fos = new FileOutputStream(filePath);
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos));
			oos.writeObject(serializableObject);
			oos.close();
			fos.close();

			long endTime = System.nanoTime();
			System.out.println("Serialization is done in " + com.utils.Utility.convertElapsedTime(startTime, endTime) + " secs");

		} catch (Exception e) {
			e.printStackTrace();
		}           
	}

	@SuppressWarnings("unchecked")
	public static <T> T deserialize(String filePath, Semaphore mutex)
	{
		T serializableObject = null;
		try
		{
			mutex.acquire(1);
			long startTime = System.nanoTime();

			FileInputStream fis = new FileInputStream(filePath);
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream (fis));
			serializableObject = (T) ois.readObject();
			ois.close();
			fis.close();

			long endTime = System.nanoTime();
			System.out.println("Deserialization is done in " + com.utils.Utility.convertElapsedTime(startTime, endTime) + " secs");
			
			mutex.release(1);
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return serializableObject;
	}
}
