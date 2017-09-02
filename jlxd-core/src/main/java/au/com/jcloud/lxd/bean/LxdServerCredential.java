package au.com.jcloud.lxd.bean;

import java.io.File;

import au.com.jcloud.lxd.LxdConstants;

public class LxdServerCredential {

	public static final String DEFAULT_FILEPATH_REMOTE_CERT = LxdConstants.USER_LXD_DIR + File.separator + "client.crt";
	public static final String DEFAULT_FILEPATH_REMOTE_KEY = LxdConstants.USER_LXD_DIR + File.separator + "client.key";

	private String remoteHostAndPort;
	private String remoteCert;
	private String remoteKey;

	@Override
	public String toString() {
		return "LxdServerCredential [remoteHostAndPort=" + remoteHostAndPort + ", remoteCert=" + remoteCert + ", remoteKey="
				+ remoteKey + "]";
	}

	public String getRemoteHostAndPort() {
		return remoteHostAndPort;
	}

	public void setRemoteHostAndPort(String remoteHostAndPort) {
		this.remoteHostAndPort = remoteHostAndPort;
	}

	public String getRemoteCert() {
		return remoteCert;
	}

	public void setRemoteCert(String remoteCert) {
		this.remoteCert = remoteCert;
	}

	public String getRemoteKey() {
		return remoteKey;
	}

	public void setRemoteKey(String remoteKey) {
		this.remoteKey = remoteKey;
	}
}
