package au.com.jcloud.lxd.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import au.com.jcloud.lxd.bean.LxdServerCredential;
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
import au.com.jcloud.lxd.service.ILinuxCliService;
import au.com.jcloud.lxd.service.ILxdApiService;
import au.com.jcloud.lxd.service.ILxdService;

/**
 * Created by david.vittor on 16/07/16.
 */
@Named
public class LxdServiceCliImpl extends AbstractLxdService implements ILxdService {

	private ILxdService lxdServiceDelegate;
	private ILinuxCliService linuxCliService;
	
	@Override
	public void setLxdServerCredential(LxdServerCredential credential) {
		super.setLxdServerCredential(credential);
		lxdServiceDelegate.setLxdServerCredential(credential);
	}

	@Inject
	public LxdServiceCliImpl(ILxdService lxdServiceDelegate) {
		this.lxdServiceDelegate = lxdServiceDelegate;
	}


	// ** ServerInfo **//
	@Override
	public ServerInfo getServerInfo() throws IOException, InterruptedException {
		return lxdServiceDelegate.getServerInfo(); // TODO: could use "lxd info", but need to do mapping
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
		linuxCliService.executeLinuxCmd("lxc image delete " + image.getFingerprint());
	}

	// ** Container Operations **//
	@Override
	public void startContainer(String name) throws IOException, InterruptedException {
		linuxCliService.executeLinuxCmd("lxc start " + name);
	}

	@Override
	public void stopContainer(String name) throws IOException, InterruptedException {
		linuxCliService.executeLinuxCmd("lxc stop " + name);
	}

	@Override
	public void deleteContainer(String name) throws IOException, InterruptedException {
		linuxCliService.executeLinuxCmd("lxc delete " + name);
	}

	@Override
	public void createContainer(String newContainerName, String imageAlias)
			throws IOException, InterruptedException {
		linuxCliService.executeLinuxCmd("lxc launch " + imageAlias + " " + newContainerName);
	}
	
	@Override
	public void copyContainer(String newContainerName, Boolean containerOnly, String existingContainerName)
			throws IOException, InterruptedException {
		lxdServiceDelegate.copyContainer(newContainerName, containerOnly, existingContainerName);
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
		linuxCliService.executeLinuxCmd("lxc file pull "+containerName+filepath+" .");
		return "";
	}


	@Override
	public void setLxdApiService(ILxdApiService lxdApiService) {
		if (lxdServiceDelegate != null) {
			lxdServiceDelegate.setLxdApiService(lxdApiService);
		}
	}


	@Override
	public void reloadContainerCache() throws IOException, InterruptedException {
		lxdServiceDelegate.reloadCertificateCache();
	}


	@Override
	public List<Container> getContainers() {
		return lxdServiceDelegate.getContainers();
	}


	@Override
	public Container getContainer(String name) {
		return lxdServiceDelegate.getContainer(name);
	}


	@Override
	public void reloadImageCache() throws IOException, InterruptedException {
		lxdServiceDelegate.reloadImageCache();
	}


	@Override
	public List<Image> getImages() {
		return lxdServiceDelegate.getImages();
	}


	@Override
	public Image getImage(String nameOrId) {
		return lxdServiceDelegate.getImage(nameOrId);
	}


	@Override
	public void reloadNetworkCache() throws IOException, InterruptedException {
		lxdServiceDelegate.reloadNetworkCache();
	}


	@Override
	public List<Network> getNetworks() throws IOException, InterruptedException {
		return lxdServiceDelegate.getNetworks();
	}


	@Override
	public Network getNetwork(String name) throws IOException, InterruptedException {
		return lxdServiceDelegate.getNetwork(name);
	}


	@Override
	public void reloadProfileCache() throws IOException, InterruptedException {
		lxdServiceDelegate.reloadProfileCache();
	}


	@Override
	public List<Profile> getProfiles() throws IOException, InterruptedException {
		return lxdServiceDelegate.getProfiles();
	}


	@Override
	public Profile getProfile(String name) throws IOException, InterruptedException {
		return lxdServiceDelegate.getProfile(name);
	}


	@Override
	public void reloadCertificateCache() throws IOException, InterruptedException {
		lxdServiceDelegate.reloadCertificateCache();
	}


	@Override
	public List<Certificate> getCertificates() throws IOException, InterruptedException {
		return lxdServiceDelegate.getCertificates();
	}


	@Override
	public Certificate getCertificate(String name) throws IOException, InterruptedException {
		return lxdServiceDelegate.getCertificate(name);
	}


	public void setLinuxCliService(ILinuxCliService linuxCliService) {
		this.linuxCliService = linuxCliService;
	}
}

