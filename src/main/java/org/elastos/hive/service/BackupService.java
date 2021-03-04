package org.elastos.hive.service;

import java.util.concurrent.CompletableFuture;

public interface BackupService {
	enum BackupResult {
		
	}
	
	public CompletableFuture<Void> start();

	public CompletableFuture<Void> stop();

	public CompletableFuture<BackupResult> checkResult();
}
