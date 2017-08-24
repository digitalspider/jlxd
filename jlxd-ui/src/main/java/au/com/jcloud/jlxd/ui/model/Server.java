package au.com.jcloud.jlxd.ui.model;

import java.util.List;

import au.com.jcloud.lxd.model.Container;

public class Server {
	private String name;
	private String remoteHostAndPort;
	private List<Container> containers;

	@Override
	public String toString() {
		return "Server [name=" + name + ", remoteHostAndPort=" + remoteHostAndPort + ", containers=" + containers + "]";
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRemoteHostAndPort() {
		return remoteHostAndPort;
	}
	public void setRemoteHostAndPort(String remoteHostAndPort) {
		this.remoteHostAndPort = remoteHostAndPort;
	}
	public List<Container> getContainers() {
		return containers;
	}
	public void setContainers(List<Container> containers) {
		this.containers = containers;
	}
}

