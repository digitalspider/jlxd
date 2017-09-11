package au.com.jcloud.jlxd.ui.controller.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.com.jcloud.jlxd.ui.search.AjaxResponseBody;
import au.com.jcloud.jlxd.ui.search.SearchCriteria;
import au.com.jcloud.lxd.model.Profile;
import au.com.jcloud.lxd.service.ICachingLxdService;

@RequestMapping("/profile")
@RestController
public class ProfileRestController extends BaseRestController<Profile> {

	private static final Logger LOG = Logger.getLogger(ProfileRestController.class);

	@Override
	public void performSearch(Map<String, Profile> entities, SearchCriteria search, AjaxResponseBody<Profile> result) throws Exception {
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
	public Map<String, Profile> getEntities(ICachingLxdService lxdService) throws IOException, InterruptedException {
		Map<String, Profile> profiles = new HashMap<>();
		if (isDefaultServerAndWindowsOs(lxdService)) {
			Profile p = new Profile();
			p.setStatus("status");
			p.setStatusCode("statusCode");
			p.setType("type");
			profiles.put(p.getType(), p);
		}
		else {
			profiles = lxdService.getProfileMap();
		}

		return profiles;
	}

	@Override
	public Profile getEntity(ICachingLxdService lxdService, String name) {
		return lxdService.getProfile(name);
	}
}
