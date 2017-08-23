package au.com.jcloud.jlxd.ui.controller.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.com.jcloud.jlxd.ui.SearchCriteria;
import au.com.jcloud.jlxd.ui.model.AjaxResponseBody;
import au.com.jcloud.lxd.model.Profile;
import au.com.jcloud.lxd.service.ILinuxCliService;
import au.com.jcloud.lxd.service.ILxdService;

@RequestMapping("/profile")
@RestController
public class ProfileRestController {

	private static final Logger LOG = Logger.getLogger(ProfileRestController.class);
	
    ILxdService lxdService;

    @Autowired
    public void setLxdService(ILxdService lxdService) {
        this.lxdService = lxdService;
    }

    @PostMapping("/api/search")
    public ResponseEntity<?> getSearchResultViaAjax(
            @Valid @RequestBody SearchCriteria search, Errors errors) {

        AjaxResponseBody<Profile> result = new AjaxResponseBody<>();

        //If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
        	StringBuffer errorString = new StringBuffer();
            for (ObjectError error : errors.getAllErrors()) {
            	if (errorString.length()>0) {
            		errorString.append(",");
            	}
            	errorString.append(error.getDefaultMessage());
            }
            result.setMsg(errorString.toString());
            return ResponseEntity.badRequest().body(result);
        }

		try {
			Map<String,Profile> profiles = new HashMap<>();
			if (ILinuxCliService.IS_WINDOWS) {
				Profile p = new Profile();
//				p.setName("david");
				p.setStatus("status");
				p.setStatusCode("statusCode");
				p.setType("type");
				profiles.put(p.getType(), p);
			} else {
				profiles = lxdService.loadProfiles();
			}
			
			String searchTerm = search.getSearchTerm();
			if ("*".equalsIgnoreCase(searchTerm)) {
				result.setMsg("Showing all containers!");
				result.setResult(profiles.values());				
			}
			else if (profiles.isEmpty()) {
	            throw new Exception("no containers found!");
	        }
			else {
	        	if (profiles.containsKey(searchTerm)) {
	        		result.setResult(new ArrayList<Profile>());
	        		result.getResult().add(profiles.get(searchTerm));
	        		result.setMsg("success. found conatiner: "+profiles.get(searchTerm));
	        	} else {
	        		throw new Exception("No container found with name: "+searchTerm);
	        	}
	        }
		} catch (Exception e) {
			LOG.error(e,e);
			result.setMsg(e.getMessage());
		}

        return ResponseEntity.ok(result);
    }
}
