package au.com.javacloud.lxd;

import java.io.IOException;
import java.util.List;

import au.com.javacloud.lxd.model.Container;
import au.com.javacloud.lxd.model.Image;
import au.com.javacloud.lxd.model.Operation;
import au.com.javacloud.lxd.model.State;

/**
 * Created by david.vittor on 12/07/16.
 */
public interface LxdService {
    // Containers
    public void reloadContainerCache() throws IOException, InterruptedException;
    public List<Container> getContainers();
    public Container getContainer(String name);
    public State getContainerState(String name) throws IOException, InterruptedException;

    // Images
    public void reloadImageCache() throws IOException, InterruptedException;
    public List<Image> getImages();
    public Image getImage(String nameOrId);
    public void deleteImage(Image image);

    // Container operations
    public void startContainer(String name) throws IOException, InterruptedException;
    public void stopContainer(String name) throws IOException, InterruptedException;
    public void deleteContainer(String name) throws IOException, InterruptedException;
    public void createContainer(String newContainerName, String imageNameOrId) throws IOException, InterruptedException;

    // Operations
    public List<Operation> getOperations() throws IOException, InterruptedException;
    public Operation getOperation(String name) throws IOException, InterruptedException;

    // Snapshots

    // File Ops
}