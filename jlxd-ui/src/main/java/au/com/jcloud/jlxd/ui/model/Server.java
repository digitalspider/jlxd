package au.com.jcloud.jlxd.ui.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import au.com.jcloud.lxd.model.Container;
import au.com.jcloud.lxd.service.ILxdService;

public class Server {
	private String name;
	private String remoteHostAndPort;
	private String description;
	private List<Container> containers;
	@JsonIgnore
	private ILxdService lxdService;

	@Override
	public String toString() {
		return "Server [name=" + name + ", remoteHostAndPort=" + remoteHostAndPort + ", description=" + description + ", lxdService=" + lxdService + ", containers=" + containers + "]";
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ILxdService getLxdService() {
		return lxdService;
	}

	public void setLxdService(ILxdService lxdService) {
		this.lxdService = lxdService;
		String remoteHostAndPort = (lxdService!=null && lxdService.getLxdServerCredential()!=null) ? lxdService.getLxdServerCredential().getRemoteHostAndPort() : null;
		this.setRemoteHostAndPort(remoteHostAndPort);
	}
}

