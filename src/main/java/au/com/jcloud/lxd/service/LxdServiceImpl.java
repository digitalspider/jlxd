package au.com.jcloud.lxd.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import au.com.jcloud.lxd.model.Container;
import au.com.jcloud.lxd.model.Image;
import au.com.jcloud.lxd.model.Image.Alias;
import au.com.jcloud.lxd.model.Operation;
import au.com.jcloud.lxd.model.State;
import au.com.jcloud.lxd.util.LXDUtil;
import au.com.jcloud.lxd.util.LXDUtil.LxdCall;

/**
 * Created by david.vittor on 12/07/16.
 */
public class LxdServiceImpl extends AbstractLxdService {

	private static final Logger LOG = Logger.getLogger(LxdServiceImpl.class);

	private List<Container> containerList = new ArrayList<Container>();
	private Map<String, Container> containerMap = new HashMap<String, Container>();

	private List<Image> imageList = new ArrayList<Image>();
	private Map<String, Image> imageMap = new HashMap<String, Image>();

	//** Containers **//
	@Override
	public List<Container> loadContainers() throws IOException, InterruptedException {
		List<Container> containers = LXDUtil.executeCurlGetListCmd(LxdCall.GET_CONTAINER);
		return containers;
	}

	@Override
	public State getContainerState(String name) throws IOException, InterruptedException {
		Container container = getContainer(name);
		if (container != null) {
			State state = LXDUtil.executeCurlGetCmd(LxdCall.GET_STATE, name);
			return state;
		}
		return null;
	}

	//** Images **//
	@Override
	public List<Image> loadImages() throws IOException, InterruptedException {
		List<Image> images = LXDUtil.executeCurlGetListCmd(LxdCall.GET_IMAGE);
		return images;
	}


	@Override
	public void deleteImage(Image image) throws IOException, InterruptedException {
		// TODO: Implement
	}


	//** Container operations **//
	@Override
	public void startContainer(String name) throws IOException, InterruptedException {
		State state = getContainerState(name);
		if (state != null && !state.isRunning()) {
			LXDUtil.executeCurlPostOrPutCmd(LxdCall.PUT_STATE_START, name);
		}
	}

	@Override
	public void stopContainer(String name) throws IOException, InterruptedException {
		State state = getContainerState(name);
		LOG.info(state);
		if (state != null && state.isRunning()) {
			LXDUtil.executeCurlPostOrPutCmd(LxdCall.PUT_STATE_STOP, name);
		}
	}

	@Override
	public void createContainer(String newContainerName, String imageNameOrId) throws IOException, InterruptedException {
//		Image image = getImage(imageNameOrId);
//		if (image != null) {
			LXDUtil.executeCurlPostOrPutCmd(LxdCall.POST_CONTAINER_CREATE, newContainerName, imageNameOrId);
//		}
	}

	@Override
	public void deleteContainer(String name) throws IOException, InterruptedException {
		State state = getContainerState(name);
		if (state != null) {
			if (!state.isStopped()) {
				throw new IOException("Cannot delete a container that is not stopped. Container="+name+" status="+state);
			}
			LXDUtil.executeCurlPostOrPutCmd(LxdCall.POST_CONTAINER_DELETE, name);
		}
	}

	//** Operations **//
	@Override
	public List<Operation> getOperations() throws IOException, InterruptedException {
		List<Operation> opertaions = LXDUtil.executeCurlGetListCmd(LxdCall.GET_OPERATION);
		return opertaions;
	}

	@Override
	public Operation getOperation(String name) throws IOException, InterruptedException {
		Operation opertaion = LXDUtil.executeCurlGetCmd(LxdCall.GET_OPERATION, name);
		return opertaion;
	}
}

