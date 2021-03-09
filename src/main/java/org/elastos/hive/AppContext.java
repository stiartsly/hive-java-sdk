package org.elastos.hive;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.did.DID;
import org.elastos.did.DIDBackend;
import org.elastos.did.DIDDocument;
import org.elastos.did.backend.ResolverCache;
import org.elastos.did.exception.DIDException;
import org.elastos.did.exception.DIDResolveException;
import org.elastos.did.exception.MalformedDIDException;
import org.elastos.hive.connection.AuthHelper;
import org.elastos.hive.connection.AuthenticationAdapter;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.ProviderNotFoundException;
import org.elastos.hive.exception.ProviderNotSetException;

/**
 * The application context would contain the resources list below:
 *  - the reference of application context provider;
 *  -
 *
 */
public class AppContext {
	private static boolean resolverHasSetup = false;

	@SuppressWarnings("unused")
	private AppContextProvider contextProvider;
	@SuppressWarnings("unused")
	private String userDid;
	@SuppressWarnings("unused")
	private String providerAddress;

	private AppContext(AppContextProvider provider, String userDid) {
		this(provider, userDid, null);
	}

	private AppContext(AppContextProvider provider, String userDid, String providerAddress) {
		this.contextProvider = provider;
	}

	public static void setupResover(String resolver, String cacheDir) throws HiveException {
		if (cacheDir == null || resolver == null)
			throw new IllegalArgumentException("invalid value for parameter resolver or cacheDir");

		if (resolverHasSetup)
			throw new HiveException("Resolver already setup before");

		try {
			DIDBackend.initialize(resolver, cacheDir);
			ResolverCache.reset();
			resolverHasSetup = true;
		} catch (DIDResolveException e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	public static AppContext build(AppContextProvider provider) {
		if (provider == null)
			throw new IllegalArgumentException("Missing AppContext provider");

		if (provider.getLocalDataDir() == null)
			throw new IllegalArgumentException("Missing method to acquire data location in AppContext provider");

		if (provider.getAppInstanceDocument() == null)
			throw new IllegalArgumentException("Missing method to acquire App instance DID document in AppContext provider");

		// if (!resolverHasSetup)
		// throw new HiveException("Setup DID resolver first");

		return new AppContext(provider, null, null);
	}

	public static CompletableFuture<String> getProviderAddress(String targetDid) {
		return getProviderAddress(targetDid, null);
	}

	public static CompletableFuture<String> getProviderAddress(String targetDid, String preferredProviderAddress) {
		if (targetDid == null)
			throw new IllegalArgumentException("Missing input parameter for target Did");

		return CompletableFuture.supplyAsync(() -> {
			// Prioritize the use of external input value for 'preferredProviderAddress';
			if (preferredProviderAddress != null)
				return preferredProviderAddress;

			try {
				List<DIDDocument.Service> services = null;
				DID did = new DID(targetDid);
				DIDDocument doc;

				doc = did.resolve();
				if (doc == null)
					throw new ProviderNotFoundException(
							String.format("The DID %s has not published onto sidechain", targetDid));

				services = doc.selectServices((String) null, "HiveVault");
				if (services == null || services.size() == 0)
					throw new ProviderNotSetException(
							String.format("No 'HiveVault' services declared on DID document %s", targetDid));

				/*
				 * TODO: should we throw special exception when it has more than one end-point
				 * of service "HiveVault";
				 */
				return services.get(0).getServiceEndpoint();
			} catch (MalformedDIDException e) {
				throw new IllegalArgumentException("Invalid format for DID " + targetDid);

			} catch (DIDResolveException e) {
				// throw new CompletionException(new HiveException(e.getLocalizedMessage()));
				// TODO:
				return null;
			}
		});
	}

	/**
	 * get Vault instance with specified DID.
	 * Try to get a vault on target provider address with following steps:
	 *  - Get the target provider address;
	 *  - Create a new vaule of local instance..
	 *
	 * @param ownerDid  the owner did related to target vault
	 * @param preferredProviderAddress the preferred target provider address
	 * @return a new vault instance.
	 */
	public CompletableFuture<Vault> getVault(String ownerDid, String preferredProviderAddress) {
		return getVaultProvider(ownerDid, preferredProviderAddress)
				.thenApplyAsync(provider -> new Vault(this, ownerDid, provider));
	}

	/**
	 * Try to acquire provider address for the specific user DID with rules with sequence orders:
	 *  - Use 'preferredProviderAddress' first when it's being with real value; Otherwise
	 *  - Resolve DID document according to the ownerDid from DID sidechain,
	 *    and find if there are more than one "HiveVault" services, then would
	 *    choose the first one service point as target provider address. Otherwise
	 *  - It means no service endpoints declared on this DID Document, then would throw the
	 *    corresponding exception.
	 *
	 * @param ownerDid the owner did that want be set provider address
	 * @param preferredProviderAddress the preferred provider address to use
	 * @return the provider address
	 */
	public CompletableFuture<String> getVaultProvider(String ownerDid, String preferredProviderAddress) {
		if (ownerDid == null)
			throw new IllegalArgumentException("Missing ownerDid to get the provider for");

		return CompletableFuture.supplyAsync(() -> {
			/* Choose 'preferredProviderAddress' as target provider address if it's with value;
			 */
			if (preferredProviderAddress != null)
				return preferredProviderAddress;

			try {
				List<DIDDocument.Service> services = null;
				DID did = new DID(ownerDid);
				DIDDocument doc;

				doc = did.resolve();
				if (doc == null)
					throw new java.nio.file.ProviderNotFoundException(
							String.format("The DID document %s has not published", ownerDid));

				services = doc.selectServices((String) null, "HiveVault");
				if (services == null || services.size() == 0)
					throw new ProviderNotSetException(
							String.format("No 'HiveVault' services declared on DID document %s", ownerDid));

				/* TODO: should we throw special exception when it has more than one
				 *       endpoints of service "HiveVault";
				 */
				return services.get(0).getServiceEndpoint();
			} catch (DIDException e) {
				throw new CompletionException(new HiveException(e.getLocalizedMessage()));
			}
		});
	}

	public AppContextProvider getContextProvider() {
		return contextProvider;
	}
}
