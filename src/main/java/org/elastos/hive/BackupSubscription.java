package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.SubscriptionService;

public class BackupSubscription {
	private SubscriptionService service;

	public BackupSubscription(AppContext context, String userDid, String providerAddress) throws HiveException {
		service = new BackupSubscriptionImpl(context, userDid, providerAddress);
	}

	public CompletableFuture<BackupSubscription.Metadata> subscribe(String pricingPlan) {
		return service.subscribe0(pricingPlan, BackupSubscription.Metadata.class);
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

	public CompletableFuture<BackupSubscription.Metadata> checkSubscription() {
		return service.checkSubscription();
	}

	public CompletableFuture<Void> upgrade() {
		return null;
	}

	public class Metadata {
		// TODO;
	}

	class BackupSubscriptionImpl extends ServiceEndpoint implements SubscriptionService {

		protected BackupSubscriptionImpl(AppContext context, String userDid, String providerAddress)
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

	}
}
