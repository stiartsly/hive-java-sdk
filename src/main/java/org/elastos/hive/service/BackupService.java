package org.elastos.hive.service;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.backup.BackupContext;

public interface BackupService {
	enum BackupResult {

	}

	public CompletableFuture<Void> setupContext(BackupContext context);

	public CompletableFuture<Void> start();

	public CompletableFuture<Void> stop();

	public CompletableFuture<BackupResult> checkResult();
}
