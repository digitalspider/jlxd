package au.com.jcloud.lxd.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.jcloud.lxd.model.Container;
import au.com.jcloud.lxd.model.Image;
import au.com.jcloud.lxd.model.Operation;
import au.com.jcloud.lxd.model.State;
import au.com.jcloud.lxd.model.extra.Config;
import au.com.jcloud.lxd.util.LXDUtil;
import au.com.jcloud.lxd.util.LinuxUtil;

/**
 * Created by david.vittor on 16/07/16.
 */
public class LxdServiceCliImpl extends AbstractLxdService {

    private LxdService lxdServiceDelegate;

    public LxdServiceCliImpl(LxdService lxdServiceDelegate) {
        this.lxdServiceDelegate = lxdServiceDelegate;
    }

    //** Containers **//
    @Override
    public List<Container> loadContainers() throws IOException, InterruptedException {
        return lxdServiceDelegate.loadContainers();
    }

    @Override
    public State getContainerState(String name) throws IOException, InterruptedException {
        return lxdServiceDelegate.getContainerState(name);
    }

    //** Images **//
    @Override
    public List<Image> loadImages() throws IOException, InterruptedException {
        return lxdServiceDelegate.loadImages();
    }


    @Override
    public void deleteImage(Image image) throws IOException, InterruptedException {
        LinuxUtil.executeLinuxCmd("lxc image delete "+image.getFingerprint());
    }

    @Override
    public void startContainer(String name) throws IOException, InterruptedException {
        LinuxUtil.executeLinuxCmd("lxc start "+name);
    }

    @Override
    public void stopContainer(String name) throws IOException, InterruptedException {
        LinuxUtil.executeLinuxCmd("lxc stop "+name);
    }

    @Override
    public void deleteContainer(String name) throws IOException, InterruptedException {
        LinuxUtil.executeLinuxCmd("lxc delete "+name);
    }

    @Override
    public void createContainer(String newContainerName, String imageNameOrId) throws IOException, InterruptedException {
        LinuxUtil.executeLinuxCmd("lxc launch "+imageNameOrId+" "+newContainerName);
    }

    @Override
    public List<Operation> getOperations() throws IOException, InterruptedException {
        return lxdServiceDelegate.getOperations();
    }

    @Override
    public Operation getOperation(String name) throws IOException, InterruptedException {
        return lxdServiceDelegate.getOperation(name);
    }
}
