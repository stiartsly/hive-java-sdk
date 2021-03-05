package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.PaymentService;
import org.elastos.hive.service.SubscriptionService;

public class BackupSubscription {
	private SubscriptionService service;

	public BackupSubscription(AppContext context, String userDid, String providerAddress) throws HiveException {
		service = new SubscriptionProxy(context, userDid, providerAddress);
	}

	public CompletableFuture<BackupInfo> subscribe(String pricingPlan) {
		return service.subscribe0(pricingPlan, BackupInfo.class);
	}

	public CompletableFuture<Void> unsbuscribe() {
		return service.unsbuscribe();
	}

	public CompletableFuture<Void> activate() {
		return service.activate();
	}

	public CompletableFuture<Void> deactivate() {
		return service.deactivate();
	}

	public CompletableFuture<BackupInfo> checkSubscription() {
		return service.checkSubscription();
	}

	/*public CompletableFuture<Void> upgrade() {
		return null;
	}*/

	public class BackupInfo {
		// TODO;
	}

	class SubscriptionProxy extends ServiceEndpoint implements SubscriptionService, PaymentService {
		protected SubscriptionProxy(AppContext context, String userDid, String providerAddress)
				throws HiveException {
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
