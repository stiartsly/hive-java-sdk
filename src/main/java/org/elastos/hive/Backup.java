package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.exception.HiveException;

public class Backup extends ServiceEndpoint {
	public Backup(AppContext context, String userDid, String providerAddress) throws HiveException {
		super(context, providerAddress, userDid, userDid, null);
	}

	class BackupInfo {

	}

	public CompletableFuture<BackupInfo> getMetaInfo() {
		// TODO;
		return null;
	}

}
