package au.com.jcloud.jlxd.ui.controller.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.com.jcloud.jlxd.ui.search.AjaxResponseBody;
import au.com.jcloud.lxd.model.Image;
import au.com.jcloud.lxd.model.ImageAlias;
import au.com.jcloud.lxd.service.ICachingLxdService;

@RequestMapping("/image")
@RestController
public class ImageRestController extends BaseRestController<Image> {

	private static final Logger LOG = Logger.getLogger(ImageRestController.class);

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

	@PostMapping("/add/{imageName}/alias/{aliasName}")
	public ResponseEntity<?> createNewContainer(HttpServletRequest request, @PathVariable String imageName,
			@PathVariable String aliasName) {
		AjaxResponseBody<Image> result = new AjaxResponseBody<>();

		try {
			ICachingLxdService lxdService = getLxdService(request);
			Image image = lxdService.getImage(imageName);
			if (image == null) {
				throw new Exception("Could not find image with name : " + imageName);
			}
			ImageAlias alias = lxdService.getImageAlias(aliasName);
			if (alias != null) {
				if (alias.getTarget().equals(image.getFingerprint())) {
					throw new Exception("Alias " + aliasName + " already assigned to " + imageName);
				}
				else {
					throw new Exception("Alias " + aliasName + " already assigned to " + alias.getTarget());
				}
			}
			ImageAlias newImageAlias = lxdService.createImageAlias(aliasName, image.getFingerprint());
			if (newImageAlias == null) {
				throw new Exception("Could not create image alias: " + aliasName);
			}

			result.setResult(getEntities(request).values());
			result.setMsg("New alias " + aliasName + " assigned to image: " + imageName);
		} catch (Exception e) {
			LOG.error(e, e);
			result.setMsg(e.getMessage());
			return ResponseEntity.badRequest().body(result);
		}
		return ResponseEntity.ok(result);
	}

}
