package org.elastos.hive;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.BackupService;
import org.elastos.hive.service.Database;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.service.PubsubService;
import org.elastos.hive.service.RestoreService;
import org.elastos.hive.vault.BackupImpl;
import org.elastos.hive.vault.DatabaseImpl;
import org.elastos.hive.vault.FilesServiceImpl;
import org.elastos.hive.vault.PubsubServiceImpl;
import org.elastos.hive.vault.RestoreServiceImpl;

/**
 * This class explicitly represents the vault service subscribed by "myDid".
 */
public class Vault extends ServiceEndpoint {
	private FilesService 	filesService;
	private Database	  	database;
	private PubsubService 	pubsubService;

	private BackupService 	backupService;
	private RestoreService 	restoreService;

	public Vault(AppContext context, String myDid) throws HiveException {
		this(context, myDid, null);
	}

	public Vault(AppContext context, String myDid, String preferredProviderAddress) throws HiveException {
		super(context, preferredProviderAddress, myDid, myDid, null);

		this.filesService 	= new FilesServiceImpl(this);
		this.database 		= new DatabaseImpl(this);
		this.pubsubService 	= new PubsubServiceImpl(this);

		this.backupService 	= new BackupImpl(this);
		this.restoreService = new RestoreServiceImpl(this);
	}

	public FilesService getFileService() {
		return this.filesService;
	}

	public Database getDatabase() {
		return this.database;
	}

	public PubsubService getPubsubService() {
		return this.pubsubService;
	}

	public BackupService getBackupService() {
		return this.backupService;
	}

	public RestoreService getRestoreService() {
		return this.restoreService;
	}
}
