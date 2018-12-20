package com.ronhagai.couponfaphase3.core;

import java.io.Serializable;
import java.util.TimeZone;

import com.ronhagai.couponfaphase3.core.dao.CouponDAO;
import com.ronhagai.couponfaphase3.core.dao.ICouponDAO;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;

/**
 * A daily task that checks and deletes all the coupons in accordance to their expiration date.
 * 
 * @author Ron
 *
 */
public class DailyCouponExpirationTask2 extends Thread implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static DailyCouponExpirationTask2 dailyExpirationTaskInstance = new DailyCouponExpirationTask2();

	private ICouponDAO couponDAO  = CouponDAO.getInstance();
	private boolean quit = false; 

	/**
	 * Private constructor
	 * @throws DAOException 
	 * 
	 */
	private DailyCouponExpirationTask2(){
		this.start();
	}

	@Override
	public void run() {
		System.out.println("LOG : Daily Task started");
		while(!quit) {
			try {
				//Sets the next iteration for 00:00 server local time
				Thread.sleep((long)((24*60*60*1000) - System.currentTimeMillis()%(24*60*60*1000) - TimeZone.getDefault().getRawOffset()));
			} catch (InterruptedException e) {
				// TODO Manager handling
				// shld be picked by tomcat logger
				System.err.println("LOG : Daily task sleep interrupted : " + e);	
				continue;
			}
						
			try {
				couponDAO.removeExpiredCoupons();
			} catch (CouponSystemException e) {
				// TODO Manager handling
				// shld be picked by tomcat logger
				System.err.println("LOG : Daily task incomplited : " + e);
			}
		}
		System.out.println("LOG : Daily Task ended");
	}

	/**
	 * Stops the task.
	 */
	public void stopTask() {
		System.out.println("LOG : Daily Task stopped");
		quit = true;
		interrupt();
	}

	/**
	 * Returns an instance of the task
	 * @return An instance of the task
	 */
	public static DailyCouponExpirationTask2 getInstance() {
		return dailyExpirationTaskInstance;
	}
}
