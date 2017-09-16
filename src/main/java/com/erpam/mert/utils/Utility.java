package com.erpam.mert.utils;

public class Utility {

	/*public static <T> void serialize(final T serializableObject, final String filePath)
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
	}*/
}
