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
import au.com.jcloud.lxd.model.Network;
import au.com.jcloud.lxd.model.Profile;
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

    private List<Network> networkList = new ArrayList<Network>();
    private Map<String, Network> networkMap = new HashMap<String, Network>();

    private List<Profile> profileList = new ArrayList<Profile>();
    private Map<String, Profile> profileMap = new HashMap<String, Profile>();

    @Override
    public void reloadContainerCache() throws IOException, InterruptedException {
        containerMap.clear();
        containerList.clear();
        containerMap = loadContainers();
        containerList.addAll(containerMap.values());
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
        imageMap = loadImages();
        imageList.addAll(imageMap.values());
        for (Image image : imageMap.values()) {
            LOG.debug("image=" + image);
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

    @Override
    public void reloadNetworkCache() throws IOException, InterruptedException {
        networkMap.clear();
        networkList.clear();
        networkMap = loadNetworks();
        networkList.addAll(networkMap.values());
    }

    @Override
    public List<Network> getNetworks() {
        if (!networkList.isEmpty()) {
            return networkList;
        }
        try {
            reloadNetworkCache();
            return networkList;
        } catch (Exception e) {
            LOG.error(e, e);
        }
        return Collections.emptyList();
    }

    @Override
    public Network getNetwork(String name) {
        getNetworks();
        return networkMap.get(name);
    }

    @Override
    public void reloadProfileCache() throws IOException, InterruptedException {
        profileMap.clear();
        profileList.clear();
        profileMap = loadProfiles();
        profileList.addAll(profileMap.values());
    }

    @Override
    public List<Profile> getProfiles() {
        if (!profileList.isEmpty()) {
            return profileList;
        }
        try {
            reloadProfileCache();
            return profileList;
        } catch (Exception e) {
            LOG.error(e, e);
        }
        return Collections.emptyList();
    }

    @Override
    public Profile getProfile(String name) {
        getProfiles();
        return profileMap.get(name);
    }
}
