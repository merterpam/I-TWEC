package com.listeners;

import java.io.File;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent event) {

		System.out.println("Session created"); 
		String id = event.getSession().getId();
		String filePath = event.getSession().getServletContext().getRealPath("/tempFiles/") + id;
		new File(filePath).mkdir();
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		System.out.println("Session destroyed");
		String id = event.getSession().getId();
		String filePath = event.getSession().getServletContext().getRealPath("/tempFiles/") + id;

		File toBeDeleted = 	new File(filePath);
		String files[] = toBeDeleted.list();
		for(String file:files)
		{
			new File(file).delete();
		}
		toBeDeleted.delete();
	}
}
