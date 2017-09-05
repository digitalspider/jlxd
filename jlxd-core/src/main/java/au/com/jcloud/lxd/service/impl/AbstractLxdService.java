package au.com.jcloud.lxd.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import au.com.jcloud.lxd.bean.LxdServerCredential;
import au.com.jcloud.lxd.model.Certificate;
import au.com.jcloud.lxd.model.Container;
import au.com.jcloud.lxd.model.Image;
import au.com.jcloud.lxd.model.ImageAlias;
import au.com.jcloud.lxd.model.Network;
import au.com.jcloud.lxd.model.Operation;
import au.com.jcloud.lxd.model.Profile;
import au.com.jcloud.lxd.model.Snapshot;
import au.com.jcloud.lxd.service.ILxdService;

/**
 * Created by david.vittor on 16/07/16.
 */
public abstract class AbstractLxdService implements ILxdService {

	private static final Logger LOG = Logger.getLogger(AbstractLxdService.class);

	@Override
	public abstract ILxdService clone() throws CloneNotSupportedException;

	private Map<String, Container> containerMap;
	private Map<String, Image> imageMap;
	private Map<String, Network> networkMap;
	private Map<String, Profile> profileMap;
	private Map<String, Certificate> certificateMap;
	private Map<String, ImageAlias> imageAliasMap;

	protected LxdServerCredential credential;

	@Override
	public LxdServerCredential getLxdServerCredential() {
		return credential;
	}

	@Override
	public void setLxdServerCredential(LxdServerCredential credential) {
		this.credential = credential;
	}

	// Internal loader methods
	public abstract Map<String, Container> loadContainers() throws IOException, InterruptedException;

	public abstract Map<String, Image> loadImages() throws IOException, InterruptedException;

	public abstract Map<String, Operation> loadOperations() throws IOException, InterruptedException;

	public abstract Map<String, Network> loadNetworks() throws IOException, InterruptedException;

	public abstract Map<String, Profile> loadProfiles() throws IOException, InterruptedException;

	public abstract Map<String, Certificate> loadCertificates() throws IOException, InterruptedException;

	public abstract Map<String, Snapshot> loadSnapshots(Container container) throws IOException, InterruptedException;

	public abstract Map<String, ImageAlias> loadImageAliases() throws IOException, InterruptedException;

	@Override
	public void reloadContainerCache() {
		try {
			containerMap = loadContainers();
		} catch (Exception e) {
			LOG.error(e, e);
			containerMap = new HashMap<>();
		}
	}

	@Override
	public Collection<Container> getContainers() {
		initContainerMap();
		return containerMap.values();
	}

	@Override
	public Container getContainer(String name) {
		initContainerMap();
		return containerMap.get(name);
	}

	@Override
	public void reloadImageCache() {
		try {
			imageMap = loadImages();
		} catch (Exception e) {
			LOG.error(e, e);
			imageMap = new HashMap<>();
		}
	}

	@Override
	public Collection<Image> getImages() {
		initImageMap();
		return imageMap.values();
	}

	@Override
	public Image getImage(String nameOrId) {
		initImageMap();
		return imageMap.get(nameOrId);
	}

	@Override
	public void reloadNetworkCache() {
		try {
			networkMap = loadNetworks();
		} catch (Exception e) {
			LOG.error(e, e);
			networkMap = new HashMap<>();
		}
	}

	@Override
	public Collection<Network> getNetworks() {
		initNetworkMap();
		return networkMap.values();
	}

	@Override
	public Network getNetwork(String name) {
		initNetworkMap();
		return networkMap.get(name);
	}

	@Override
	public void reloadProfileCache() {
		try {
			profileMap = loadProfiles();
		} catch (Exception e) {
			LOG.error(e, e);
			profileMap = new HashMap<>();
		}
	}

	@Override
	public Collection<Profile> getProfiles() {
		initProfileMap();
		return profileMap.values();
	}

	@Override
	public Profile getProfile(String name) {
		initProfileMap();
		return profileMap.get(name);
	}

	@Override
	public void reloadCertificateCache() {
		try {
			certificateMap = loadCertificates();
		} catch (Exception e) {
			LOG.error(e, e);
			certificateMap = new HashMap<>();
		}
	}

	@Override
	public Collection<Certificate> getCertificates() {
		initCertificateMap();
		return certificateMap.values();
	}

	@Override
	public Certificate getCertificate(String name) {
		initCertificateMap();
		return certificateMap.get(name);
	}

	@Override
	public void reloadImageAliasCache() {
		try {
			imageAliasMap = loadImageAliases();
		} catch (Exception e) {
			LOG.error(e, e);
			imageAliasMap = new HashMap<>();
		}
	}

	@Override
	public Collection<ImageAlias> getImageAliases() {
		initImageAliasMap();
		return imageAliasMap.values();
	}

	@Override
	public ImageAlias getImageAlias(String name) {
		initImageAliasMap();
		return imageAliasMap.get(name);
	}

	@Override
	public Map<String, Container> getContainerMap() {
		initContainerMap();
		return containerMap;
	}

	@Override
	public Map<String, Image> getImageMap() {
		initImageMap();
		return imageMap;
	}

	@Override
	public Map<String, Network> getNetworkMap() {
		initNetworkMap();
		return networkMap;
	}

	@Override
	public Map<String, Profile> getProfileMap() {
		initProfileMap();
		return profileMap;
	}

	@Override
	public Map<String, Certificate> getCertificateMap() {
		initCertificateMap();
		return certificateMap;
	}

	@Override
	public Map<String, ImageAlias> getImageAliasMap() {
		initImageAliasMap();
		return imageAliasMap;
	}

	protected void initContainerMap() {
		if (containerMap == null) {
			reloadContainerCache();
		}
	}

	protected void initImageMap() {
		if (imageMap == null) {
			reloadImageCache();
		}
	}

	protected void initProfileMap() {
		if (profileMap == null) {
			reloadProfileCache();
		}
	}

	protected void initCertificateMap() {
		if (certificateMap == null) {
			reloadCertificateCache();
		}
	}

	protected void initNetworkMap() {
		if (networkMap == null) {
			reloadNetworkCache();
		}
	}

	protected void initImageAliasMap() {
		if (imageAliasMap == null) {
			reloadImageAliasCache();
		}
	}
}
