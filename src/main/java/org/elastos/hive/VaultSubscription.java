package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.PaymentService;
import org.elastos.hive.service.SubscriptionService;

public class VaultSubscription {
	private SubscriptionService service;

	public VaultSubscription(AppContext context, String userDid, String providerAddress) throws HiveException {
		service = new SubscriptionRender(context, userDid, providerAddress);
	}

	public CompletableFuture<VaultInfo> subscribe(String pricingPlan) {
		return service.subscribe0(pricingPlan, VaultInfo.class);
	}

	public CompletableFuture<Void> unsubscribe() {
		return service.unsbuscribe();
	}

	public CompletableFuture<Void> activate() {
		return service.activate();
	}

	public CompletableFuture<Void> deactivate() {
		return service.deactivate();
	}

	public CompletableFuture<VaultInfo> checkSubscription() {
		return service.checkSubscription();
	}

	public class VaultInfo {
		// TODO;
	}

	class SubscriptionRender extends ServiceEndpoint implements SubscriptionService, PaymentService {
		SubscriptionRender(AppContext context, String userDid, String providerAddress) throws HiveException {
			super(context, providerAddress, userDid);
		}

		@Override
		public <T> CompletableFuture<T> subscribe0(String pricingPlan, Class<T> type) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<Void> unsbuscribe() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<Void> activate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<Void> deactivate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> CompletableFuture<T> checkSubscription() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<String> placeOrder(String planName) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<Void> payOrder(String orderId, String transactionId) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
