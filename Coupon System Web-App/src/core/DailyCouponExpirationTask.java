package core;

import java.io.Serializable;
import core.dao.CouponDAO;
import core.dao.ICouponDAO;
import core.exception.CouponSystemException;

/**
 * A daily task that checks and deletes all the coupons in accordance to their expiration date.
 * 
 * @author Ron
 *
 */
public class DailyCouponExpirationTask implements Runnable , Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static DailyCouponExpirationTask dailyExpirationTaskInstance = new DailyCouponExpirationTask();
	private Thread dailyTaskThread; 	
	
	
	
	public Thread getT() {
		return dailyTaskThread;
	}

	private ICouponDAO couponDAO  = CouponDAO.getInstance();
//	private CouponController couponController = new CouponController();
	private boolean quit = false; 

	/**
	 * Private constructor
	 * @throws DAOException 
	 * 
	 */
	private DailyCouponExpirationTask(){
		this.dailyTaskThread = new Thread(this);
		this.dailyTaskThread.start();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		System.out.println("LOG : Daily Task started");
		while(!quit) {
			try {
				//Sets the next iteration for 00:00 hour  
				Thread.sleep((long)(System.currentTimeMillis()%(24*60*60*1000) + (2*60*60*1000)));
			} catch (InterruptedException e) {
				// TODO Manager handling
				// e.printStackTrace();
				System.err.println("Daily task sleep interrupted : " + e);	
				continue;
			}
						
			try {
				couponDAO.removeExpiredCoupons();
			} catch (CouponSystemException e) {
				// TODO Manager handling
				System.err.println("Daily task incomplited : " + e);
				e.printStackTrace();
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
		dailyTaskThread.interrupt();
	}

	/**
	 * Returns an instance of the task
	 * @return An instance of the task
	 */
	public static DailyCouponExpirationTask getInstance() {
		return dailyExpirationTaskInstance;
	}
}
