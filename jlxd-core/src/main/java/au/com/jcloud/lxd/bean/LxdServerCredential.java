package au.com.jcloud.lxd.bean;

public class LxdServerCredential {

	private String remoteHostAndPort;
	private String remoteCert = "~/.config/lxc/client.crt";
	private String remoteKey = "~/.config/lxc/client.key";
	
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
