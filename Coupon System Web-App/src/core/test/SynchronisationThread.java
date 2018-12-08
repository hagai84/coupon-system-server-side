package core.test;

import core.beans.CouponBean;
import core.beans.CustomerBean;
import core.exception.CouponSystemException;

public class SynchronisationThread extends TestThread {
	int startingCouponIndex;
	int startingCustomerIndex;
	
	public SynchronisationThread(int startingCouponIndex, int startingCustomerIndex) {
		super();
		this.startingCouponIndex = startingCouponIndex;
		this.startingCustomerIndex = startingCustomerIndex;
	}

	@Override
	public void run() {
		System.out.println("Start Synchonyzed Thread");
		super.run();
		multiplePurchases();
	}
	
	private void multiplePurchases() {

		CustomerBean customer = new CustomerBean();
		CouponBean coupon = new CouponBean();
		
		for (char j = (char)(97+startingCouponIndex); j < 117; j++) {
			for (char i = (char)(97+startingCustomerIndex); i < 117; i++) {			
				try {
					coupon = couponController.getCouponByTitle(""+j+j+j+" "+j+j+j+j);
					customer = customerController.getCustomerByName(""+i+i+i+" "+i+i+i+i);
					couponController.purchaseCoupon(coupon.getCouponId(), customer.getId());
					System.out.println(Thread.currentThread().getName() + " : LOG : Coupon purchased \n" + coupon);
				} catch (CouponSystemException e) {
					System.err.println(Thread.currentThread().getName() + " : " + e);
					continue;
				}
			}
		}
	}
}
