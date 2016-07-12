package au.com.javacloud.lxd.model.response;

import com.google.gson.annotations.SerializedName;

public abstract class ResponseBase {
	private String type;
	private String status;
	@SerializedName("status_code")
	private String statusCode;

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": type=" + type + ", status=" + status + ", statusCode=" + statusCode + " metadata=" + getMetadata();
	}

	public abstract Object getMetadata();

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
}
