package org.elastos.hive.entity.http;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateServiceJsonResult extends JsonResult<CreateServiceJsonResult> {
	@JsonProperty("existing")
	private boolean existing;

	public boolean existing() {
		return existing;
	}
}
