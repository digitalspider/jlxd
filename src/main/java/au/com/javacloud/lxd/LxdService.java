package au.com.javacloud.lxd;

import java.io.IOException;
import java.util.List;

import au.com.javacloud.lxd.model.Container;
import au.com.javacloud.lxd.model.Image;

/**
 * Created by david on 12/07/16.
 */
public interface LxdService {
    public void reloadContainerCache() throws IOException, InterruptedException;
    public List<Container> getContainers();
    public Container getContainer(String name);
    public void deleteContainer(String name);
    public void startContainer(String name);
    public void stopContainer(String name);
    public void launchContainer(String newContainerName, String imageNameOrId);

    public void reloadImageCache() throws IOException, InterruptedException;
    public List<Image> getImages();
    public Image getImage(String nameOrId);
    public void deleteImage(Image image);
}