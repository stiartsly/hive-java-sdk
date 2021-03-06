package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.exception.HiveException;

/**
 * This class is used to fetch some possible information from remote hive node.
 * eg. version;
 *     latest commit Id;
 *     How many DID involved;
 *     How many vault service running there;
 *     How many backup service running there;
 *     How much disk storage filled there;
 *     etc.
 */
public class Provider extends ServiceEndpoint {
	public Provider(AppContext context, String myDid) throws HiveException {
		this(context, myDid, null);
	}

	public Provider(AppContext context, String myDid, String endpoint) throws HiveException {
		super(context, endpoint, myDid);
	}

	public CompletableFuture<Version> getVersion() {
		// TODO:
		return null;
	}

	public CompletableFuture<String> getLatestCommitId() {
		// TODO:
		return null;
	}

	public class Version {
		private int major;
		private int minor;
		private int hotfix;

		public int getMajorNumber() {
			return this.major;
		}

		public int getMinorNumber() {
			return this.minor;
		}

		public int getFixNumber() {
			return this.hotfix;
		}

		public int getFullNumber() {
			return 0;
		}

		public String getVersionName() {
			return null;
		}

		@Override
		public String toString() {
			return getVersionName();
		}
	}
}
