package au.com.javacloud.lxd.model;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class ResponseBase {
	private String type;
	private String status;
	@SerializedName("status_code")
	private String statusCode;
	private Map<String, Object> metadata;

	@Override
	public String toString() {
		return "ResponseBase [type=" + type + ", status=" + status + ", statusCode=" + statusCode + ", metadata=" + metadata + "]";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}
}
