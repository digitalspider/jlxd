package au.com.jcloud.lxd.enums;

public enum RemoteServer {
	LOCAL("","unix://", "lxd"),
	IMAGES("images","https://images.linuxcontainers.org", "simplestreams"),
	UBUNTU("ububtu","https://cloud-images.ubuntu.com/releases", "simplestreams"),
	UBUNTU_DAILY("ubuntu-daily","https://cloud-images.ubuntu.com/daily", "simplestreams");
	
	private String name;
	private String url;
	private String protocol;
	
	RemoteServer(String name, String url, String protocol) {
		this.name = name;
		this.url = url;
		this.protocol = protocol;
	}

	public String getUrl() {
		return url;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getName() {
		return name;
	}
}
