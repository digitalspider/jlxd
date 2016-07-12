package au.com.javacloud.lxd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import au.com.javacloud.lxd.model.Container;
import au.com.javacloud.lxd.model.Image;
import au.com.javacloud.lxd.model.Image.Alias;
import au.com.javacloud.lxd.model.Operation;
import au.com.javacloud.lxd.model.State;
import au.com.javacloud.lxd.util.LXDUtil;
import au.com.javacloud.lxd.util.LXDUtil.LxdCall;

/**
 * Created by david.vittor on 12/07/16.
 */
public class LxdServiceImpl implements LxdService {

	private static final Logger LOG = Logger.getLogger(LxdServiceImpl.class);

	private List<Container> containerList = new ArrayList<Container>();
	private Map<String, Container> containerMap = new HashMap<String, Container>();

	private List<Image> imageList = new ArrayList<Image>();
	private Map<String, Image> imageMap = new HashMap<String, Image>();

	//** Containers **//

	@Override
	public void reloadContainerCache() throws IOException, InterruptedException {
		containerMap.clear();
		containerList.clear();
		List<Container> containers = LXDUtil.executeCurlGetListCmd(LxdCall.GET_CONTAINER);
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
	public void reloadImageCache() throws IOException, InterruptedException {
		imageMap.clear();
		imageList.clear();
		List<Image> images = LXDUtil.executeCurlGetListCmd(LxdCall.GET_IMAGE);
		for (Image image : images) {
			LOG.debug("image=" + image);
			imageMap.put(image.getFingerprint(), image);
			imageList.add(image);

			// Add all aliases to the map
			for (Alias alias : image.getAliases()) {
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
	public void deleteImage(Image image) {
		// TODO: Implement
	}


	//** Container operations **//
	@Override
	public void startContainer(String name) throws IOException, InterruptedException {
		Container container = getContainer(name);
		if (container != null) {
			LXDUtil.executeCurlPostOrPutCmd(LxdCall.PUT_STATE_START, name);
		}
	}

	@Override
	public void stopContainer(String name) throws IOException, InterruptedException {
		Container container = getContainer(name);
		if (container != null) {
			LXDUtil.executeCurlPostOrPutCmd(LxdCall.PUT_STATE_STOP, name);
		}
	}

	@Override
	public void createContainer(String newContainerName, String imageNameOrId) throws IOException, InterruptedException {
		Image image = getImage(imageNameOrId);
		if (image != null) {
			LXDUtil.executeCurlPostOrPutCmd(LxdCall.POST_CONTAINER_CREATE, newContainerName, imageNameOrId);
		}
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
		// TODO: Implement
		return new ArrayList<Operation>();
	}

	@Override
	public Operation getOperation(String name) throws IOException, InterruptedException {
		Operation opertaion = LXDUtil.executeCurlGetCmd(LxdCall.GET_OPERATION, name);
		return opertaion;
	}

	public static void main(String[] args) {
		LOG.info("LXC START");
		try {
			LxdService service = new LxdServiceImpl();
//			List<Container> containers = service.getContainers();
//			LOG.info("containers=" + containers.size());
//			for (Container container : containers) {
//				LOG.info("container=" + container);
//			}
//			List<Image> images = service.getImages();
//			LOG.info("");
//			LOG.info("images=" + images.size());
//			for (Image image : images) {
//				LOG.info("image=" + image);
//			}

			service.startContainer("alpine1");
		} catch (Exception e) {
			LOG.error(e, e);
		}
		LOG.info("LXC DONE");
	}

}

