package org.elastos.hive;

import org.elastos.hive.connection.AuthHelper;
import org.elastos.hive.connection.AuthenticationAdapter;
import org.elastos.hive.service.BackupService;
import org.elastos.hive.service.Database;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.service.PubsubService;
import org.elastos.hive.service.ScriptingService;
import org.elastos.hive.vault.ServiceBuilder;

import java.util.concurrent.CompletableFuture;

/**
 * This class explicitly represents the vault service subscribed by "myDid".
 */
public class Vault extends ServiceEndpoint {
	private FilesService 	filesService;
	private Database	  	database;
	private ScriptingService scripting;
	private PubsubService 	pubsubService;
	private BackupService 	backupService;
	private AuthHelper      authHelper;

	public Vault(AppContext context, String myDid) {
		super(context, null, myDid);
	}

	public Vault(AppContext context, String myDid, String preferredProviderAddress) {
		super(context, preferredProviderAddress, myDid);

		this.authHelper = new AuthHelper(getContext().getContextProvider(), myDid, preferredProviderAddress,
				(ctx, jwtToken) -> ctx.getAuthorization(jwtToken));

		this.filesService 	= new ServiceBuilder(this).createFilesService();
		this.database 		= new ServiceBuilder(this).createDatabase();
		this.pubsubService 	= new ServiceBuilder(this).createPubsubService();
		this.backupService 	= new ServiceBuilder(this).createBackupService();
	}

	public FilesService getFilesService() {
		return this.filesService;
	}

	public Database getDatabase() {
		return this.database;
	}

	public ScriptingService getScripting() {
		return this.scripting;
	}

	public PubsubService getPubsubService() {
		return this.pubsubService;
	}

	public BackupService getBackupService() {
		return this.backupService;
	}

	public AuthHelper getAuthHelper() {
		return this.authHelper;
	}
}
