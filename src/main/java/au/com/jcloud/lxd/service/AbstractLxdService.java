package au.com.jcloud.lxd.service;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.jcloud.lxd.model.Container;
import au.com.jcloud.lxd.model.Image;
import au.com.jcloud.lxd.util.LXDUtil;

/**
 * Created by david.vittor on 16/07/16.
 */
public abstract class AbstractLxdService implements LxdService {

    private static final Logger LOG = Logger.getLogger(AbstractLxdService.class);

    private List<Container> containerList = new ArrayList<Container>();
    private Map<String, Container> containerMap = new HashMap<String, Container>();

    private List<Image> imageList = new ArrayList<Image>();
    private Map<String, Image> imageMap = new HashMap<String, Image>();

    @Override
    public void reloadContainerCache() throws IOException, InterruptedException {
        containerMap.clear();
        containerList.clear();
        List<Container> containers = loadContainers();
        for (Container container : containers) {
            LOG.debug("container=" + container);
            containerMap.put(container.getName(), container);
            containerList.add(container);
        }
    }

    @Override
    public List<Container> getContainers() {
        if (!containerList.isEmpty()) {
            return containerList;
        }
        try {
            reloadContainerCache();
            return containerList;
        } catch (Exception e) {
            LOG.error(e, e);
        }
        return Collections.emptyList();
    }

    @Override
    public Container getContainer(String name) {
        getContainers();
        return containerMap.get(name);
    }

    @Override
    public void reloadImageCache() throws IOException, InterruptedException {
        imageMap.clear();
        imageList.clear();
        List<Image> images = loadImages();
        for (Image image : images) {
            LOG.debug("image=" + image);
            imageMap.put(image.getFingerprint(), image);
            imageList.add(image);

            // Add all aliases to the map
            for (Image.Alias alias : image.getAliases()) {
                imageMap.put(alias.getName(), image);
            }
        }
    }

    @Override
    public List<Image> getImages() {
        if (!imageList.isEmpty()) {
            return imageList;
        }
        try {
            reloadImageCache();
            return imageList;
        } catch (Exception e) {
            LOG.error(e, e);
        }
        return Collections.emptyList();
    }

    @Override
    public Image getImage(String nameOrId) {
        getImages();
        return imageMap.get(nameOrId);
    }
}
