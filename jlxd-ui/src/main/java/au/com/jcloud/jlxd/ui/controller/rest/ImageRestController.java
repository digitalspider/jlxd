package au.com.jcloud.jlxd.ui.controller.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.com.jcloud.jlxd.ui.search.AjaxResponseBody;
import au.com.jcloud.jlxd.ui.search.SearchCriteria;
import au.com.jcloud.lxd.model.Image;
import au.com.jcloud.lxd.service.ICachingLxdService;

@RequestMapping("/image")
@RestController
public class ImageRestController extends BaseRestController<Image> {

	private static final Logger LOG = Logger.getLogger(ImageRestController.class);

	@Override
	public void performSearch(Map<String, Image> entities, SearchCriteria search, AjaxResponseBody<Image> result) throws Exception {
		String searchTerm = search.getSearchTerm();
		if (entities.containsKey(searchTerm)) {
			result.getResult().add(entities.get(searchTerm));
			result.setMsg("success. found image: " + entities.get(searchTerm));
		}
		else {
			throw new Exception("No image found with name: " + searchTerm);
		}
	}

	@Override
	public Map<String, Image> getEntities(ICachingLxdService lxdService) throws IOException, InterruptedException {
		Map<String, Image> images = new HashMap<>();
		if (isDefaultServerAndWindowsOs(lxdService)) {
			Image image = new Image();
			image.setFingerprint("123");
			image.setArchitecture("x64");
			image.setDescription("ubuntu");
			images.put(image.getFingerprint(), image);

			Image image2 = new Image();
			image2.setFingerprint("456");
			image2.setArchitecture("x32");
			image2.setDescription("windows");
			images.put(image2.getFingerprint(), image2);
		}
		else {
			images = getLxdService().getImageMap();
		}
		return images;
	}

	@Override
	public Image getEntity(ICachingLxdService lxdService, String name) {
		return lxdService.getImage(name);
	}
}
