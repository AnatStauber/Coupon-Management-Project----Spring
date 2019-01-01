package com.anat.coupons.tasks;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.TimerTask;

import com.anat.coupons.dao.CouponsDao;
import com.anat.coupons.exceptions.ApplicationException;

public class DeleteExpiredDailyTask extends TimerTask{
	

	
	CouponsDao couponDao = new CouponsDao();

	long delay = 86400000;

	@Override
	public void run() {

		try {
			Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

			System.out.println("timerTask started!" + new Date());

			couponDao.deleteExpiredCoupons();

			System.out.println("timer task ended!" + new Date());

			long nextExecution = calendar.getTimeInMillis() + delay;
			calendar.setTimeInMillis(nextExecution);
			System.out.println("the next execution will start at:" + calendar.getTime());
			
			

		} catch (ApplicationException e) {
			System.out.println(e.getMessage());
			e.fillInStackTrace();
		}
	}
}

	
