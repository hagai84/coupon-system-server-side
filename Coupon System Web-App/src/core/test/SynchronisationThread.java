package core.test;


import core.beans.CouponBean;
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

		CouponBean[] coupons = new CouponBean[0];
		long couponId, customerId;
		try {
//			System.out.println(couponService.getAllCoupons());
			coupons = couponService.getAllCoupons().toArray(coupons);
		} catch (CouponSystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		for (int i = 0+startingCouponIndex; i < coupons.length; i++) {
			for (char j = (char)(97+startingCustomerIndex) ; j < 117; j++) {
				try {
					couponId = coupons[i].getCouponId();
					customerId = loginCustomer(""+j+j+j+" "+j+j+j+j, ""+j+j+j+j+j+j);
					couponService.purchaseCoupon(couponId, customerId);
					System.out.println(Thread.currentThread().getName() + " : LOG : Coupon purchased \n" + i);
				} catch (CouponSystemException e) {
					System.err.println(Thread.currentThread().getName() + " : " + e);
					continue;
				}		
			}		
		}
	}
}
