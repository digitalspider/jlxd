package au.com.jcloud.lxd.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import au.com.jcloud.lxd.annotation.DefaultLxdService;
import au.com.jcloud.lxd.enums.LxdCall;
import au.com.jcloud.lxd.enums.RemoteServer;
import au.com.jcloud.lxd.model.Certificate;
import au.com.jcloud.lxd.model.Container;
import au.com.jcloud.lxd.model.Image;
import au.com.jcloud.lxd.model.ImageAlias;
import au.com.jcloud.lxd.model.Network;
import au.com.jcloud.lxd.model.Operation;
import au.com.jcloud.lxd.model.Profile;
import au.com.jcloud.lxd.model.ServerInfo;
import au.com.jcloud.lxd.model.Snapshot;
import au.com.jcloud.lxd.model.State;
import au.com.jcloud.lxd.service.ILxdApiService;

/**
 * Created by david.vittor on 12/07/16.
 */
@DefaultLxdService
public class LxdServiceImpl extends AbstractLxdService {

	private static final Logger LOG = Logger.getLogger(LxdServiceImpl.class);

	private ILxdApiService lxdApiService;
	
	// ** ServerInfo **//
	@Override
	public ServerInfo getServerInfo() throws IOException, InterruptedException {
		ServerInfo serverInfo = lxdApiService.executeCurlGetCmd(remoteHostAndPort, LxdCall.GET_SERVERINFO, null);
		return serverInfo;
	}
	
	// ** Containers **//
	@Override
	public Map<String, Container> loadContainers() throws IOException, InterruptedException {
		Map<String, Container> containers = lxdApiService.executeCurlGetListCmd(remoteHostAndPort, LxdCall.GET_CONTAINER);
		return containers;
	}

	@Override
	public State getContainerState(String name) throws IOException, InterruptedException {
		Container container = getContainer(name);
		if (container != null) {
			State state = lxdApiService.executeCurlGetCmd(remoteHostAndPort, LxdCall.GET_STATE, name);
			return state;
		}
		return null;
	}

	// ** Images **//
	@Override
	public Map<String, Image> loadImages() throws IOException, InterruptedException {
		Map<String, Image> images = lxdApiService.executeCurlGetListCmd(remoteHostAndPort, LxdCall.GET_IMAGE);
		Map<String, Image> imageAliasMap = new HashMap<>();
		for (Image image : images.values()) {
			LOG.debug("image=" + image);
			// Add all aliases to the map
			for (ImageAlias alias : image.getAliases()) {
				LOG.debug("alias=" + alias.getName());
				imageAliasMap.put(alias.getName(), image);
			}
			// Add fingerprint to the map
			if (image.getFingerprint()!=null && image.getFingerprint().length()>12) {
				imageAliasMap.put(image.getFingerprint().substring(0,12), image);
			}
		}
		images.putAll(imageAliasMap);
		return images;
	}

	@Override
	public void deleteImage(Image image) throws IOException, InterruptedException {
		// TODO: Implement
	}

	// ** Container operations **//
	@Override
	public void startContainer(String name) throws IOException, InterruptedException {
		State state = getContainerState(name);
		if (state != null && !state.isRunning()) {
			lxdApiService.executeCurlPostOrPutCmd(remoteHostAndPort, LxdCall.PUT_STATE_START, name);
		}
	}

	@Override
	public void stopContainer(String name) throws IOException, InterruptedException {
		State state = getContainerState(name);
		LOG.info(state);
		if (state != null && state.isRunning()) {
			lxdApiService.executeCurlPostOrPutCmd(remoteHostAndPort, LxdCall.PUT_STATE_STOP, name);
		}
	}

	@Override
	public void createContainer(String newContainerName, String imageAlias) throws IOException, InterruptedException {
		
		if (!imageAlias.contains(":")) {
			Image image = getImage(imageAlias);
			if (image != null) {
				lxdApiService.executeCurlPostCmdToCreateNewContainerFromImage(remoteHostAndPort, RemoteServer.LOCAL, newContainerName, imageAlias);
			} else {
				throw new IOException("Could not find local image with alias: "+imageAlias);
			}
		} else {
			boolean found = false;
			for (RemoteServer remoteServer : RemoteServer.values()) {
				if (remoteServer!=RemoteServer.LOCAL && imageAlias.startsWith(remoteServer.getName()+":")) {
					imageAlias = imageAlias.substring((remoteServer.getName()+":").length());
					lxdApiService.executeCurlPostCmdToCreateNewContainerFromImage(remoteHostAndPort, remoteServer, newContainerName, imageAlias);
					found = true;
					break;
				}
			}
			if (!found) {
				throw new IOException("Could not find remote image server: "+imageAlias);
			}
		}
	}

	@Override
	public void deleteContainer(String name) throws IOException, InterruptedException {
		State state = getContainerState(name);
		if (state != null) {
			if (!state.isStopped()) {
				throw new IOException(
						"Cannot delete a container that is not stopped. Container=" + name + " status=" + state);
			}
			lxdApiService.executeCurlPostOrPutCmd(remoteHostAndPort, LxdCall.POST_CONTAINER_DELETE, name);
		}
	}

	// ** Operations **//
	@Override
	public Map<String, Operation> loadOperations() throws IOException, InterruptedException {
		Map<String, Operation> opertaions = lxdApiService.executeCurlGetListCmd(remoteHostAndPort, LxdCall.GET_OPERATION);
		return opertaions;
	}

	@Override
	public List<Operation> getOperations() throws IOException, InterruptedException {
		Map<String, Operation> opertaions = loadOperations();
		return new ArrayList<Operation>(opertaions.values());
	}

	@Override
	public Operation getOperation(String name) throws IOException, InterruptedException {
		Operation opertaion = lxdApiService.executeCurlGetCmd(remoteHostAndPort, LxdCall.GET_OPERATION, name);
		return opertaion;
	}

	// ** Networks **//
	@Override
	public Map<String, Network> loadNetworks() throws IOException, InterruptedException {
		Map<String, Network> networks = lxdApiService.executeCurlGetListCmd(remoteHostAndPort, LxdCall.GET_NETWORK);
		return networks;
	}

	@Override
	public List<Container> getContainersUsedByNetwork(Network network) throws IOException, InterruptedException {
		List<Container> usedByContainers = new ArrayList<Container>();
		String[] usedByArray = network.getMetadata().getUsedBy();
		for (String usedByString : usedByArray) {
			String containerName = usedByString.substring(usedByString.lastIndexOf("/"));
			Container container = getContainer(containerName);
			usedByContainers.add(container);
		}
		return usedByContainers;
	}

	// ** Profiles **//
	@Override
	public Map<String, Profile> loadProfiles() throws IOException, InterruptedException {
		Map<String, Profile> profiles = lxdApiService.executeCurlGetListCmd(remoteHostAndPort, LxdCall.GET_PROFILE);
		return profiles;
	}

	// ** Certificates **//
	@Override
	public Map<String, Certificate> loadCertificates() throws IOException, InterruptedException {
		Map<String, Certificate> certificates = lxdApiService.executeCurlGetListCmd(remoteHostAndPort,
				LxdCall.GET_CERTIFICATE);
		return certificates;
	}

	// ** Snapshots **//
	@Override
	public Map<String, Snapshot> loadSnapshots(Container container) throws IOException, InterruptedException {
		Map<String, Snapshot> snapshots = lxdApiService.executeCurlGetListCmd(remoteHostAndPort, LxdCall.GET_SNAPSHOTS,
				container.getName());
		return snapshots;
	}

	@Override
	public List<Snapshot> getSnapshots(Container container) throws IOException, InterruptedException {
		Map<String, Snapshot> snapshots = loadSnapshots(container);
		return new ArrayList<>(snapshots.values());
	}

	@Override
	public Snapshot getSnapshot(Container container, String name) throws IOException, InterruptedException {
		Snapshot snapshot = lxdApiService.executeCurlGetCmd(remoteHostAndPort, LxdCall.GET_SNAPSHOTS, name);
		return snapshot;
	}

	// ** Image Aliases **//
	@Override
	public Map<String, ImageAlias> loadImageAliases() throws IOException, InterruptedException {
		Map<String, ImageAlias> aliases = lxdApiService.executeCurlGetListCmd(remoteHostAndPort, LxdCall.GET_IMAGEALIAS);
		return aliases;
	}

	@Override
	public List<ImageAlias> getImageAliases() throws IOException, InterruptedException {
		Map<String, ImageAlias> aliases = loadImageAliases();
		return new ArrayList<>(aliases.values());
	}

	@Override
	public ImageAlias getImageAlias(String name) throws IOException, InterruptedException {
		ImageAlias alias = lxdApiService.executeCurlGetCmd(remoteHostAndPort, LxdCall.GET_IMAGEALIAS, name);
		return alias;
	}
	
	// ** File Ops **//
	@Override
	public String getFile(String containerName, String filepath) throws IOException, InterruptedException {
		String response = lxdApiService.executeCurlGetCmd(remoteHostAndPort, LxdCall.GET_FILE, null, containerName, filepath);
		return response;
	}

	@Override
	@Inject
	public void setLxdApiService(ILxdApiService lxdApiService) {
		this.lxdApiService = lxdApiService;
	}
}
