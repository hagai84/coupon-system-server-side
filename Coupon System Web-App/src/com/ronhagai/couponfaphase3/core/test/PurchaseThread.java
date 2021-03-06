package com.ronhagai.couponfaphase3.core.test;


import com.ronhagai.couponfaphase3.core.beans.CouponBean;
import com.ronhagai.couponfaphase3.core.enums.UserType;
import com.ronhagai.couponfaphase3.core.exception.CouponSystemException;

public class PurchaseThread extends GenericThread {
	int startingCouponIndex;
	int startingCustomerIndex;
	
	public PurchaseThread(int startingCouponIndex, int startingCustomerIndex) {
		super();
		this.startingCouponIndex = startingCouponIndex;
		this.startingCustomerIndex = startingCustomerIndex;
	}

	@Override
	public void run() {
		super.run();
		multiplePurchases();
	}
	
	private void multiplePurchases() {

		CouponBean[] coupons = new CouponBean[0];
		long couponId, customerId;
		try {
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
					couponService.purchaseCoupon(couponId, customerId, customerId, UserType.CUSTOMER);
					System.out.println(Thread.currentThread().getName() + " : LOG : Coupon purchased " + couponId);
				} catch (CouponSystemException e) {
					System.err.println(Thread.currentThread().getName() + " : " + e);
					continue;
				}		
			}		
		}
	}
}
