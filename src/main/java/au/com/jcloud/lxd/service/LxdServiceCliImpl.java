package au.com.jcloud.lxd.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import au.com.jcloud.lxd.model.Certificate;
import au.com.jcloud.lxd.model.Container;
import au.com.jcloud.lxd.model.Image;
import au.com.jcloud.lxd.model.ImageAlias;
import au.com.jcloud.lxd.model.Network;
import au.com.jcloud.lxd.model.Operation;
import au.com.jcloud.lxd.model.Profile;
import au.com.jcloud.lxd.model.Snapshot;
import au.com.jcloud.lxd.model.State;
import au.com.jcloud.lxd.util.LinuxUtil;

/**
 * Created by david.vittor on 16/07/16.
 */
public class LxdServiceCliImpl extends AbstractLxdService {

	private LxdService lxdServiceDelegate;

	public LxdServiceCliImpl(LxdService lxdServiceDelegate) {
		this.lxdServiceDelegate = lxdServiceDelegate;
	}

	// ** Containers **//
	@Override
	public Map<String, Container> loadContainers() throws IOException, InterruptedException {
		return lxdServiceDelegate.loadContainers();
	}

	@Override
	public State getContainerState(String name) throws IOException, InterruptedException {
		return lxdServiceDelegate.getContainerState(name);
	}

	// ** Images **//
	@Override
	public Map<String, Image> loadImages() throws IOException, InterruptedException {
		return lxdServiceDelegate.loadImages();
	}

	@Override
	public void deleteImage(Image image) throws IOException, InterruptedException {
		LinuxUtil.executeLinuxCmd("lxc image delete " + image.getFingerprint());
	}

	// ** Container Operations **//
	@Override
	public void startContainer(String name) throws IOException, InterruptedException {
		LinuxUtil.executeLinuxCmd("lxc start " + name);
	}

	@Override
	public void stopContainer(String name) throws IOException, InterruptedException {
		LinuxUtil.executeLinuxCmd("lxc stop " + name);
	}

	@Override
	public void deleteContainer(String name) throws IOException, InterruptedException {
		LinuxUtil.executeLinuxCmd("lxc delete " + name);
	}

	@Override
	public void createContainer(String newContainerName, String imageNameOrId)
			throws IOException, InterruptedException {
		LinuxUtil.executeLinuxCmd("lxc launch " + imageNameOrId + " " + newContainerName);
	}

	// ** Operations **//
	@Override
	public Map<String, Operation> loadOperations() throws IOException, InterruptedException {
		return lxdServiceDelegate.loadOperations();
	}

	@Override
	public List<Operation> getOperations() throws IOException, InterruptedException {
		return lxdServiceDelegate.getOperations();
	}

	@Override
	public Operation getOperation(String name) throws IOException, InterruptedException {
		return lxdServiceDelegate.getOperation(name);
	}

	// ** Networks **//
	@Override
	public Map<String, Network> loadNetworks() throws IOException, InterruptedException {
		return lxdServiceDelegate.loadNetworks();
	}

	@Override
	public List<Container> getContainersUsedByNetwork(Network network) throws IOException, InterruptedException {
		return lxdServiceDelegate.getContainersUsedByNetwork(network);
	}

	// ** Profiles **//
	@Override
	public Map<String, Profile> loadProfiles() throws IOException, InterruptedException {
		return lxdServiceDelegate.loadProfiles();
	}

	// ** Certificates **//
	@Override
	public Map<String, Certificate> loadCertificates() throws IOException, InterruptedException {
		return lxdServiceDelegate.loadCertificates();
	}

	// ** Snapshots **//
	@Override
	public Map<String, Snapshot> loadSnapshots(Container container) throws IOException, InterruptedException {
		return lxdServiceDelegate.loadSnapshots(container);
	}

	@Override
	public List<Snapshot> getSnapshots(Container container) throws IOException, InterruptedException {
		return lxdServiceDelegate.getSnapshots(container);
	}

	@Override
	public Snapshot getSnapshot(Container container, String name) throws IOException, InterruptedException {
		return lxdServiceDelegate.getSnapshot(container, name);
	}

	// ** Image Aliases **//
	@Override
	public Map<String, ImageAlias> loadImageAliases() throws IOException, InterruptedException {
		return lxdServiceDelegate.loadImageAliases();
	}

	@Override
	public List<ImageAlias> getImageAliases() throws IOException, InterruptedException {
		return lxdServiceDelegate.getImageAliases();
	}

	@Override
	public ImageAlias getImageAlias(String name) throws IOException, InterruptedException {
		return lxdServiceDelegate.getImageAlias(name);
	}
	
	// ** File Ops **//
	@Override
	public String getFile(String containerName, String filepath) throws IOException, InterruptedException {
		LinuxUtil.executeLinuxCmd("lxc file pull "+containerName+filepath+" .");
		return "";
	}
}
