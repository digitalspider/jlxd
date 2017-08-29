package au.com.jcloud.lxd.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

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
import au.com.jcloud.lxd.service.ILxdService;

/**
 * Created by david.vittor on 12/07/16.
 */
@Named
public class LxdServiceImpl extends AbstractLxdService {

	private static final Logger LOG = Logger.getLogger(LxdServiceImpl.class);

	private ILxdApiService lxdApiService;

	@Override
	public ILxdService clone() throws CloneNotSupportedException {
		LxdServiceImpl newService = new LxdServiceImpl();
		newService.setLxdApiService(lxdApiService);
		return newService;
	}

	// ** ServerInfo **//
	@Override
	public ServerInfo getServerInfo() throws IOException, InterruptedException {
		ServerInfo serverInfo = lxdApiService.executeCurlGetCmd(credential, LxdCall.GET_SERVERINFO, null);
		return serverInfo;
	}

	// ** Containers **//
	@Override
	public Map<String, Container> loadContainers() throws IOException, InterruptedException {
		Map<String, Container> containers = lxdApiService.executeCurlGetListCmd(credential, LxdCall.GET_CONTAINER);
		return containers;
	}

	@Override
	public State getContainerState(String name) throws IOException, InterruptedException {
		Container container = getContainer(name);
		if (container != null) {
			State state = lxdApiService.executeCurlGetCmd(credential, LxdCall.GET_STATE, name);
			return state;
		}
		return null;
	}

	// ** Images **//
	@Override
	public Map<String, Image> loadImages() throws IOException, InterruptedException {
		Map<String, Image> images = lxdApiService.executeCurlGetListCmd(credential, LxdCall.GET_IMAGE);
		Map<String, Image> imageAliasMap = new HashMap<>();
		for (Image image : images.values()) {
			LOG.debug("image=" + image);
			// Add all aliases to the map
			for (ImageAlias alias : image.getAliases()) {
				LOG.debug("alias=" + alias.getName());
				imageAliasMap.put(alias.getName(), image);
			}
			// Add fingerprint to the map
			if (image.getFingerprint() != null && image.getFingerprint().length() > 12) {
				imageAliasMap.put(image.getFingerprint().substring(0, 12), image);
			}
		}
		images.putAll(imageAliasMap);
		return images;
	}

	@Override
	public void deleteImage(String imageNameOrId) throws IOException, InterruptedException {
		Image image = getImage(imageNameOrId);
		if (image == null) {
			throw new IllegalArgumentException("Cannot find image with name or id: " + imageNameOrId);
		}
		lxdApiService.executeCurlPostOrPutCmd(credential, LxdCall.POST_IMAGE_DELETE, image.getFingerprint());
		reloadImageCache();
	}

	// ** Container operations **//
	@Override
	public void startContainer(String name) throws IOException, InterruptedException {
		State state = getContainerState(name);
		if (state != null && !state.isRunning()) {
			lxdApiService.executeCurlPostOrPutCmd(credential, LxdCall.PUT_STATE_START, name);
		}
	}

	@Override
	public void stopContainer(String name) throws IOException, InterruptedException {
		State state = getContainerState(name);
		LOG.info("container " + name + " has state " + state);
		if (state != null && state.isRunning()) {
			lxdApiService.executeCurlPostOrPutCmd(credential, LxdCall.PUT_STATE_STOP, name);
		}
	}

	@Override
	public void createContainer(String newContainerName, String imageAlias) throws IOException, InterruptedException {
		if (StringUtils.isBlank(imageAlias)) {
			throw new IllegalArgumentException("Cannot create container where imageAlias is blank");
		}
		if (StringUtils.isBlank(newContainerName)) {
			throw new IllegalArgumentException("Cannot create container where newContainerName is blank");
		}
		if (!imageAlias.contains(":")) {
			Image image = getImage(imageAlias);
			if (image == null) {
				throw new IOException("Could not find local image with alias: " + imageAlias);
			}
			lxdApiService.executeCurlPostCmdToCreateNewContainerFromImage(credential, RemoteServer.LOCAL, newContainerName, imageAlias);
		}
		else {
			boolean found = false;
			for (RemoteServer remoteServer : RemoteServer.values()) {
				if (remoteServer != RemoteServer.LOCAL && imageAlias.startsWith(remoteServer.getName() + ":")) {
					imageAlias = imageAlias.substring((remoteServer.getName() + ":").length());
					lxdApiService.executeCurlPostCmdToCreateNewContainerFromImage(credential, remoteServer, newContainerName, imageAlias);
					found = true;
					break;
				}
			}
			if (!found) {
				throw new IOException("Could not find remote image server: " + imageAlias);
			}
		}
		reloadContainerCache();
	}

	@Override
	public void deleteContainer(String name) throws IOException, InterruptedException {
		State state = getContainerState(name);
		if (state == null) {
			throw new IllegalArgumentException("Cannot find a valid state for container name: " + name);
		}

		if (!state.isStopped()) {
			throw new IOException("Cannot delete a container that is not stopped. Container=" + name + " status=" + state);
		}
		lxdApiService.executeCurlPostOrPutCmd(credential, LxdCall.POST_CONTAINER_DELETE, name);
		reloadContainerCache();
	}

	@Override
	public void renameContainer(String name, String newContainerName) throws IOException, InterruptedException {
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("Cannot rename container where name is blank");
		}
		if (StringUtils.isBlank(newContainerName)) {
			throw new IllegalArgumentException("Cannot rename container where newContainerName is blank");
		}
		lxdApiService.executeCurlPostOrPutCmd(credential, LxdCall.POST_CONTAINER_RENAME, name, newContainerName);
		Container container = getContainerMap().remove(name);
		if (container != null) {
			getContainerMap().put(newContainerName, container);
		}
	}

	@Override
	public void copyContainer(String name, String newContainerName, Boolean containerOnly) throws IOException, InterruptedException {
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("Cannot copy container where name is blank");
		}
		if (StringUtils.isBlank(newContainerName)) {
			throw new IllegalArgumentException("Cannot copy container where newContainerName is blank");
		}

		Container container = getContainer(name);
		if (container == null) {
			throw new IOException("Could not find existing container with name: " + name);
		}
		lxdApiService.executeCurlPostCmdToCopyContainer(credential, name, newContainerName, containerOnly);
		reloadContainerCache();
	}

	@Override
	public void execOnContainer(String name, String[] commandAndArgs, String env, Boolean waitForSocket)
			throws IOException, InterruptedException {
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("Cannot execute command where containerName is blank");
		}
		if (commandAndArgs == null || commandAndArgs.length == 0) {
			throw new IllegalArgumentException("Cannot execute empty command on container: " + name);
		}

		Container container = getContainer(name);
		if (container == null) {
			throw new IOException("Could not find container with name: " + name);
		}
		lxdApiService.executeCurlPostOrPutCmdForExec(credential, LxdCall.POST_CONTAINER_EXEC, name, commandAndArgs, env, waitForSocket);
	}

	// ** Operations **//
	@Override
	public Map<String, Operation> loadOperations() throws IOException, InterruptedException {
		Map<String, Operation> opertaions = lxdApiService.executeCurlGetListCmd(credential, LxdCall.GET_OPERATION);
		return opertaions;
	}

	@Override
	public List<Operation> getOperations() throws IOException, InterruptedException {
		Map<String, Operation> opertaions = loadOperations();
		return new ArrayList<Operation>(opertaions.values());
	}

	@Override
	public Operation getOperation(String name) throws IOException, InterruptedException {
		Operation opertaion = lxdApiService.executeCurlGetCmd(credential, LxdCall.GET_OPERATION, name);
		return opertaion;
	}

	// ** Networks **//
	@Override
	public Map<String, Network> loadNetworks() throws IOException, InterruptedException {
		Map<String, Network> networks = lxdApiService.executeCurlGetListCmd(credential, LxdCall.GET_NETWORK);
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

	@Override
	public void deleteNetwork(String name) throws IOException, InterruptedException {
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("Cannot delete a network with an empty name");
		}
		Network network = getNetwork(name);
		if (network == null) {
			throw new IllegalArgumentException("Cannot find a network with name: " + name);
		}
		lxdApiService.executeCurlPostOrPutCmd(credential, LxdCall.POST_NETWORK_DELETE, name);
		reloadNetworkCache();
	}

	// ** Profiles **//
	@Override
	public Map<String, Profile> loadProfiles() throws IOException, InterruptedException {
		Map<String, Profile> profiles = lxdApiService.executeCurlGetListCmd(credential, LxdCall.GET_PROFILE);
		return profiles;
	}

	@Override
	public void deleteProfile(String name) throws IOException, InterruptedException {
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("Cannot delete a profile with an empty name");
		}
		Profile profile = getProfile(name);
		if (profile == null) {
			throw new IllegalArgumentException("Cannot find a profile with name: " + name);
		}
		lxdApiService.executeCurlPostOrPutCmd(credential, LxdCall.POST_PROFILE_DELETE, name);
		reloadProfileCache();
	}

	// ** Certificates **//
	@Override
	public Map<String, Certificate> loadCertificates() throws IOException, InterruptedException {
		Map<String, Certificate> certificates = lxdApiService.executeCurlGetListCmd(credential, LxdCall.GET_CERTIFICATE);
		return certificates;
	}

	// ** Snapshots **//
	@Override
	public Map<String, Snapshot> loadSnapshots(Container container) throws IOException, InterruptedException {
		Map<String, Snapshot> snapshots = lxdApiService.executeCurlGetListCmd(credential, LxdCall.GET_SNAPSHOTS,
				container.getName());
		return snapshots;
	}

	@Override
	public List<Snapshot> getSnapshots(Container container) throws IOException, InterruptedException {
		Map<String, Snapshot> snapshots = loadSnapshots(container);
		return new ArrayList<>(snapshots.values());
	}

	@Override
	public Snapshot getSnapshot(String containerName, String snapshotName) throws IOException, InterruptedException {
		Snapshot snapshot = lxdApiService.executeCurlGetCmd(credential, LxdCall.GET_SNAPSHOTS, snapshotName, containerName);
		return snapshot;
	}

	@Override
	public void renameSnapshot(String containerName, String snapshotName, String newSnapshotName)
			throws IOException, InterruptedException {
		if (StringUtils.isBlank(containerName)) {
			throw new IllegalArgumentException("Cannot rename snapshot where containerName is blank");
		}
		if (StringUtils.isBlank(snapshotName)) {
			throw new IllegalArgumentException("Cannot rename snapshot where snapshotName is blank");
		}
		if (StringUtils.isBlank(newSnapshotName)) {
			throw new IllegalArgumentException("Cannot rename snapshot where newSnapshotName is blank");
		}
		lxdApiService.executeCurlPostOrPutCmdForSnapshot(credential, LxdCall.POST_CONTAINER_RENAME, containerName, snapshotName, newSnapshotName);
	}

	@Override
	public void createSnapshot(String containerName, String snapshotName) throws IOException, InterruptedException {
		lxdApiService.executeCurlPostOrPutCmdForSnapshot(credential, LxdCall.POST_SNAPSHOT_CREATE, containerName, snapshotName);
	}

	@Override
	public void deleteSnapshot(String containerName, String snapshotName) throws IOException, InterruptedException {
		lxdApiService.executeCurlPostOrPutCmdForSnapshot(credential, LxdCall.POST_SNAPSHOT_DELETE, containerName, snapshotName);
	}

	// ** Image Aliases **//
	@Override
	public Map<String, ImageAlias> loadImageAliases() throws IOException, InterruptedException {
		Map<String, ImageAlias> aliases = lxdApiService.executeCurlGetListCmd(credential, LxdCall.GET_IMAGEALIAS);
		return aliases;
	}

	@Override
	public List<ImageAlias> getImageAliases() throws IOException, InterruptedException {
		Map<String, ImageAlias> aliases = loadImageAliases();
		return new ArrayList<>(aliases.values());
	}

	@Override
	public ImageAlias getImageAlias(String name) throws IOException, InterruptedException {
		ImageAlias alias = lxdApiService.executeCurlGetCmd(credential, LxdCall.GET_IMAGEALIAS, name);
		return alias;
	}

	// ** File Ops **//
	@Override
	public String getFile(String containerName, String filepath) throws IOException, InterruptedException {
		String response = lxdApiService.executeCurlGetCmd(credential, LxdCall.GET_FILE, null, containerName, filepath);
		return response;
	}

	@Override
	@Inject
	public void setLxdApiService(ILxdApiService lxdApiService) {
		this.lxdApiService = lxdApiService;
	}
}
