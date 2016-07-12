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
import au.com.javacloud.lxd.util.LXDUtil;
import au.com.javacloud.lxd.util.LXDUtil.LxdCall;
import au.com.javacloud.lxd.util.LinuxUtil;

public class App implements LXDAPI {

	private static final Logger LOG = Logger.getLogger(App.class);

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
		Container[] containers = LinuxUtil.executeLinuxCmdWithResultJsonObject("lxc list --format json", Container[].class);
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
		List<String> imageDataList = LinuxUtil.executeLinuxCmdWithResultLines("lxc image list | grep -v +");
		for (String imageData : imageDataList) {
			LOG.debug("imageData=" + imageData);
			try {
				Image image = Image.parse(imageData);
				if (image != null) {
					imageMap.put(image.getFingerprint(), image);
					imageMap.put(image.getAlias(), image);
					imageList.add(image);
				}
			} catch (Exception e) {
				LOG.error(e, e);
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
			/*
			App app = new App();
			List<Container> containers = app.getContainers();
			LOG.info("containers=" + containers.size());
			for (Container container : containers) {
				LOG.info("container=" + container);
			}
			List<Image> images = app.getImages();
			LOG.info("");
			LOG.info("images=" + images.size());
			for (Image image : images) {
				LOG.info("image=" + image);
			}
			*/
			List<Container> containers = LXDUtil.executeCurlGetListCmd(LxdCall.CONTAINER_GET);
			for (Container container : containers) {
				LOG.info("container=" + container);
			}
		} catch (Exception e) {
			LOG.error(e, e);
		}
		LOG.info("LXC DONE");
	}

}
