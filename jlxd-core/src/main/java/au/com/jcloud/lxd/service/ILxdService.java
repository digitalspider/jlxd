package au.com.jcloud.lxd.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import au.com.jcloud.lxd.bean.ImageConfig;
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

/**
 * Created by david.vittor on 12/07/16.
 */
public interface ILxdService {

	// Clone
	ILxdService clone() throws CloneNotSupportedException;

	// Dependencies
	void setLxdApiService(ILxdApiService lxdApiService);

	// LxdServerCredential
	LxdServerCredential getLxdServerCredential();

	void setLxdServerCredential(LxdServerCredential credentials);

	// ServerInfo
	ServerInfo getServerInfo() throws IOException, InterruptedException;

	// Containers
	void reloadContainerCache();

	Map<String, Container> getContainerMap();

	Collection<Container> getContainers();

	Container getContainer(String name) throws IOException, InterruptedException;

	State getContainerState(String name) throws IOException, InterruptedException;

	// Images
	void reloadImageCache();

	Map<String, Image> getImageMap();

	Collection<Image> getImages();

	Image getImage(String nameOrId) throws IOException, InterruptedException;

	void deleteImage(String nameOrId) throws IOException, InterruptedException;

	// Container operations
	void startContainer(String name) throws IOException, InterruptedException;

	void stopContainer(String name) throws IOException, InterruptedException;

	void deleteContainer(String name) throws IOException, InterruptedException;

	void createContainer(String newContainerName, String imageAlias) throws IOException, InterruptedException;

	void createContainer(String newContainerName, String imageAlias, ImageConfig imageConfig) throws IOException, InterruptedException;

	void renameContainer(String name, String newContainerName) throws IOException, InterruptedException;

	void copyContainer(String name, String newContainerName, Boolean containerOnly) throws IOException, InterruptedException;

	void execOnContainer(String name, String[] commandAndArgs, String env, Boolean waitForSocket) throws IOException, InterruptedException;

	// Operations
	Collection<Operation> getOperations() throws IOException, InterruptedException;

	Operation getOperation(String name) throws IOException, InterruptedException;

	// Networks
	void reloadNetworkCache();

	Map<String, Network> getNetworkMap();

	Collection<Network> getNetworks();

	Network getNetwork(String name) throws IOException, InterruptedException;

	Collection<Container> getContainersUsedByNetwork(Network network) throws IOException, InterruptedException;

	void deleteNetwork(String name) throws IOException, InterruptedException;

	// Profiles
	void reloadProfileCache();

	Map<String, Profile> getProfileMap();

	Collection<Profile> getProfiles();

	Profile getProfile(String name) throws IOException, InterruptedException;

	void deleteProfile(String name) throws IOException, InterruptedException;

	// Certificates
	void reloadCertificateCache();

	Map<String, Certificate> getCertificateMap();

	Collection<Certificate> getCertificates();

	Certificate getCertificate(String name) throws IOException, InterruptedException;

	// Snapshots
	Collection<Snapshot> getSnapshots(Container container) throws IOException, InterruptedException;

	Snapshot getSnapshot(String containerName, String snapshotName) throws IOException, InterruptedException;

	void renameSnapshot(String containerName, String snapshotName, String newSnapshotName) throws IOException, InterruptedException;

	void createSnapshot(String containerName, String snapshotName) throws IOException, InterruptedException;

	void deleteSnapshot(String containerName, String snapshotName) throws IOException, InterruptedException;

	// Image Aliases
	void reloadImageAliasCache();

	Map<String, ImageAlias> getImageAliasMap();

	Collection<ImageAlias> getImageAliases();

	ImageAlias getImageAlias(String name) throws IOException, InterruptedException;

	// File Ops
	String getFile(String containerName, String filepath) throws IOException, InterruptedException;
}
