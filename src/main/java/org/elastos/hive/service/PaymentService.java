package org.elastos.hive.service;

import java.util.concurrent.CompletableFuture;

public interface PaymentService {

	// public CompletableFuture<PricingPlan> getPricingPlan(String planName);

	public CompletableFuture<String> placeOrder(String planName);

	public CompletableFuture<Void> payOrder(String orderId, String transactionId);

	//public CompletableFuture<Order> getOrder(String orderId);

}
