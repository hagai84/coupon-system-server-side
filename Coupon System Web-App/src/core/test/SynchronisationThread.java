package core.test;

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

		for (long i = 300001; i < 20; i++) {
			for (long j = 200001; j < 20; j++) {
				try {
					couponService.purchaseCoupon(i, j);
					System.out.println(Thread.currentThread().getName() + " : LOG : Coupon purchased \n" + i);
				} catch (CouponSystemException e) {
					System.err.println(Thread.currentThread().getName() + " : " + e);
					continue;
				}		
			}		
		}
	}
}
