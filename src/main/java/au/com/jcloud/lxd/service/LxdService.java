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

/**
 * Created by david.vittor on 12/07/16.
 */
public interface LxdService {
    // Containers
    public Map<String,Container> loadContainers() throws IOException, InterruptedException;
    public void reloadContainerCache() throws IOException, InterruptedException;
    public List<Container> getContainers();
    public Container getContainer(String name);
    public State getContainerState(String name) throws IOException, InterruptedException;

    // Images
    public Map<String,Image> loadImages() throws IOException, InterruptedException;
    public void reloadImageCache() throws IOException, InterruptedException;
    public List<Image> getImages();
    public Image getImage(String nameOrId);
    public void deleteImage(Image image) throws IOException, InterruptedException;

    // Container operations
    public void startContainer(String name) throws IOException, InterruptedException;
    public void stopContainer(String name) throws IOException, InterruptedException;
    public void deleteContainer(String name) throws IOException, InterruptedException;
    public void createContainer(String newContainerName, String imageNameOrId) throws IOException, InterruptedException;

    // Operations
    public Map<String,Operation> loadOperations() throws IOException, InterruptedException;
    public List<Operation> getOperations() throws IOException, InterruptedException;
    public Operation getOperation(String name) throws IOException, InterruptedException;

    // Networks
    public Map<String,Network> loadNetworks() throws IOException, InterruptedException;
    public void reloadNetworkCache() throws IOException, InterruptedException;
    public List<Network> getNetworks() throws IOException, InterruptedException;
    public Network getNetwork(String name) throws IOException, InterruptedException;
    public List<Container> getContainersUsedByNetwork(Network network) throws IOException, InterruptedException;

    // Profiles
    public Map<String,Profile> loadProfiles() throws IOException, InterruptedException;
    public void reloadProfileCache() throws IOException, InterruptedException;
    public List<Profile> getProfiles() throws IOException, InterruptedException;
    public Profile getProfile(String name) throws IOException, InterruptedException;

    // Certificates
    public Map<String,Certificate> loadCertificates() throws IOException, InterruptedException;
    public void reloadCertificateCache() throws IOException, InterruptedException;
    public List<Certificate> getCertificates() throws IOException, InterruptedException;
    public Certificate getCertificate(String name) throws IOException, InterruptedException;

    // Snapshots
    public Map<String,Snapshot> loadSnapshots(Container container) throws IOException, InterruptedException;
    public List<Snapshot> getSnapshots(Container container) throws IOException, InterruptedException;
    public Snapshot getSnapshot(Container container, String name) throws IOException, InterruptedException;

    // Image Aliases
    public Map<String,ImageAlias> loadImageAliases() throws IOException, InterruptedException;
    public List<ImageAlias> getImageAliases() throws IOException, InterruptedException;
    public ImageAlias getImageAlias(String name) throws IOException, InterruptedException;

    // File Ops
}
