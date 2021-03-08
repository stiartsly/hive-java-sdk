package org.elastos.hive.payment;

public class Order {
	private String orderId;
	private String appDid;
	private String orderer;
	private PricingPlan pricingPlan;

	public String getOrderId() {
		return orderId;
	}

	public String getAppDid() {
		return appDid;
	}

	public String getOrdererDid() {
		return orderer;
	}

	public PricingPlan getPricingPlan() {
		return pricingPlan;
	}
}
