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
import au.com.javacloud.lxd.util.LXDUtil;
import au.com.javacloud.lxd.util.LXDUtil.LxdCall;

public class LxdServiceImpl implements LxdService {

	private static final Logger LOG = Logger.getLogger(LxdServiceImpl.class);

	private List<Container> containerList = new ArrayList<Container>();
	private Map<String, Container> containerMap = new HashMap<String, Container>();

	private List<Image> imageList = new ArrayList<Image>();
	private Map<String, Image> imageMap = new HashMap<String, Image>();

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
	public void reloadContainerCache() throws IOException, InterruptedException {
		containerMap.clear();
		containerList.clear();
		List<Container> containers = LXDUtil.executeCurlGetListCmd(LxdCall.CONTAINER_GET);
		for (Container container : containers) {
			LOG.debug("container=" + container);
			containerMap.put(container.getName(), container);
			containerList.add(container);
		}
	}

	@Override
	public Container getContainer(String name) {
		getContainers();
		return containerMap.get(name);
	}

	@Override
	public void deleteContainer(String name) {
		// TODO : Implement
	}

	@Override
	public void reloadImageCache() throws IOException, InterruptedException {
		imageMap.clear();
		imageList.clear();
		List<Image> images = LXDUtil.executeCurlGetListCmd(LxdCall.IMAGE_GET);
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

	public static void main(String[] args) {
		LOG.info("LXC START");
		try {
			LxdService service = new LxdServiceImpl();
			List<Container> containers = service.getContainers();
			LOG.info("containers=" + containers.size());
			for (Container container : containers) {
				LOG.info("container=" + container);
			}
			List<Image> images = service.getImages();
			LOG.info("");
			LOG.info("images=" + images.size());
			for (Image image : images) {
				LOG.info("image=" + image);
			}

			/*
			Container container = LXDUtil.executeCurlGetCmd(LxdCall.CONTAINER_GET, "www");
			LOG.info("container=" + container);
			List<Container> containers = LXDUtil.executeCurlGetListCmd(LxdCall.CONTAINER_GET);
			for (Container container : containers) {
				LOG.info("container=" + container);
			}
			*/
		} catch (Exception e) {
			LOG.error(e, e);
		}
		LOG.info("LXC DONE");
	}

}

