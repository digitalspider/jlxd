package au.com.jcloud.lxd.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
	
    // Dependencies
    void setLxdApiService(ILxdApiService lxdApiService);

	// LxdServerCredential
    LxdServerCredential getLxdServerCredential();
	void setLxdServerCredential(LxdServerCredential credentials);
	
	// ServerInfo
	ServerInfo getServerInfo() throws IOException, InterruptedException;
	
    // Containers
    Map<String,Container> loadContainers() throws IOException, InterruptedException;
    void reloadContainerCache() throws IOException, InterruptedException;
    List<Container> getContainers();
    Container getContainer(String name);
    State getContainerState(String name) throws IOException, InterruptedException;

    // Images
    Map<String,Image> loadImages() throws IOException, InterruptedException;
    void reloadImageCache() throws IOException, InterruptedException;
    List<Image> getImages();
    Image getImage(String nameOrId);
    void deleteImage(Image image) throws IOException, InterruptedException;

    // Container operations
    void startContainer(String name) throws IOException, InterruptedException;
    void stopContainer(String name) throws IOException, InterruptedException;
    void deleteContainer(String name) throws IOException, InterruptedException;
    void createContainer(String newContainerName, String imageAlias) throws IOException, InterruptedException;
    void copyContainer(String newContainerName, Boolean containerOnly, String existingContainerName) throws IOException, InterruptedException;

    // Operations
    Map<String,Operation> loadOperations() throws IOException, InterruptedException;
    List<Operation> getOperations() throws IOException, InterruptedException;
    Operation getOperation(String name) throws IOException, InterruptedException;

    // Networks
    Map<String,Network> loadNetworks() throws IOException, InterruptedException;
    void reloadNetworkCache() throws IOException, InterruptedException;
    List<Network> getNetworks() throws IOException, InterruptedException;
    Network getNetwork(String name) throws IOException, InterruptedException;
    List<Container> getContainersUsedByNetwork(Network network) throws IOException, InterruptedException;

    // Profiles
    Map<String,Profile> loadProfiles() throws IOException, InterruptedException;
    void reloadProfileCache() throws IOException, InterruptedException;
    List<Profile> getProfiles() throws IOException, InterruptedException;
    Profile getProfile(String name) throws IOException, InterruptedException;

    // Certificates
    Map<String,Certificate> loadCertificates() throws IOException, InterruptedException;
    void reloadCertificateCache() throws IOException, InterruptedException;
    List<Certificate> getCertificates() throws IOException, InterruptedException;
    Certificate getCertificate(String name) throws IOException, InterruptedException;

    // Snapshots
    Map<String,Snapshot> loadSnapshots(Container container) throws IOException, InterruptedException;
    List<Snapshot> getSnapshots(Container container) throws IOException, InterruptedException;
    Snapshot getSnapshot(String containerName, String snapshotName) throws IOException, InterruptedException;
    void createSnapshot(String containerName, String snapshotName) throws IOException, InterruptedException;
    void deleteSnapshot(String containerName, String snapshotName) throws IOException, InterruptedException;

    // Image Aliases
    Map<String,ImageAlias> loadImageAliases() throws IOException, InterruptedException;
    List<ImageAlias> getImageAliases() throws IOException, InterruptedException;
    ImageAlias getImageAlias(String name) throws IOException, InterruptedException;

    // File Ops
    String getFile(String containerName, String filepath) throws IOException, InterruptedException;    
}
