package org.elastos.hive;

class ServiceEndpoint {
	@SuppressWarnings("unused")
	private AppContext context;

	private String address;
	private String userDid;
	private String targetDid;

	@SuppressWarnings("unused")
	private String targetAppDid;

	protected ServiceEndpoint(AppContext context, String endpointAddress, String userDid) {
		this(context, endpointAddress, userDid, null, null);
	}

	protected ServiceEndpoint(AppContext context, String endpointAddress, String userDid, String targetDid, String targetAppDid) {
		this.context = context;
		this.address = endpointAddress;
		this.userDid = userDid;
		this.targetDid = targetDid;
		this.targetAppDid = targetAppDid;
	}

	public String getEndpointAddress() {
		return this.address;
	}

	public String getOwnerDid() {
		return this.targetDid;
	}

	public String getUserDid() {
		return this.userDid;
	}

	public String getAppDid() {
		return null;
	}

	public String getAppInstanceDid() {
		return null;
	}

	public String getServiceDid() {
		return null;
	}

	public String getServiceInstanceDid() {
		return null;
	}
}
