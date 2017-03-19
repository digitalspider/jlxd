package au.com.jcloud.lxd;

public enum RemoteServer {
	LOCAL("unix://", "lxd"),
	IMAGES("https://images.linuxcontainers.org", "simplestreams"),
	UBUNTU("https://cloud-images.ubuntu.com/releases", "simplestreams"),
	UBUNTU_DAILY("https://cloud-images.ubuntu.com/daily", "simplestreams");
	
	private String url;
	private String protocol;
	
	RemoteServer(String url, String protocol) {
		this.url = url;
		this.protocol = protocol;
	}

	public String getUrl() {
		return url;
	}

	public String getProtocol() {
		return protocol;
	}
}
