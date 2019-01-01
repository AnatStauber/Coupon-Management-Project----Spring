package com.anat.coupons.servlets;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServlet;

import com.anat.coupons.tasks.DeleteExpiredDailyTask;


public class LoadThreadOnStartup extends HttpServlet  {
	
	
	 	private static final long    serialVersionUID    = 1L;
	

		public void init() {
			
			Calendar c = Calendar.getInstance(TimeZone.getDefault());
			
			System.out.println("the thread timer was initialized. " + c.getTime() );
			
			
			Calendar c2 = c;
			
			int nextDay = c.get(Calendar.DATE) + 1;
	
			c2.set(Calendar.DATE, nextDay);
	
			c2.set(Calendar.HOUR_OF_DAY, 00);
	
			c2.set(Calendar.MINUTE,00);
	
			c2.set(Calendar.SECOND, 00);
			
			//testing:
			System.out.println("the task will start running at: " + c2.getTime());
			
			// Creating a task
			TimerTask timerTask = new DeleteExpiredDailyTask();
		
			// Creating a timer
			Timer timer = new Timer();
	
			// Tell the timer to run the task day at midnight of next midnight.
			timer.scheduleAtFixedRate(timerTask,c2.getTime(), 8640000);
			
			
			
			
		}
}
	
			

		
