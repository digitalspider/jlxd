package au.com.jcloud.jlxd.ui.bean;

public class AddServerInput {
	private String name;
	private String hostAndPort;
	private String description;

	@Override
	public String toString() {
		return "AddContainerData [name=" + name + ", hostAndPort=" + hostAndPort + ", description=" + description + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHostAndPort() {
		return hostAndPort;
	}

	public void setHostAndPort(String hostAndPort) {
		this.hostAndPort = hostAndPort;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
