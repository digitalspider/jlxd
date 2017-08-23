package au.com.jcloud.jlxd.ui.controller.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.com.jcloud.jlxd.ui.search.AjaxResponseBody;
import au.com.jcloud.lxd.model.Container;
import au.com.jcloud.lxd.model.State;
import au.com.jcloud.lxd.model.StatusCode;
import au.com.jcloud.lxd.service.ILinuxCliService;
import au.com.jcloud.lxd.service.ILxdService;

@RequestMapping("/container")
@RestController
public class ContainerRestController {

	private static final Logger LOG = Logger.getLogger(ContainerRestController.class);
	
    ILxdService lxdService;

    @Autowired
    public void setLxdService(ILxdService lxdService) {
        this.lxdService = lxdService;
    }

    @PostMapping("/api/search")
    public ResponseEntity<?> getSearchResult() {
    	return getSearchResult("");
    }

    @PostMapping("/api/search/{searchTerm}")
    public ResponseEntity<?> getSearchResult(@PathVariable String searchTerm) {

        AjaxResponseBody<Container> result = new AjaxResponseBody<>();

		try {
			Map<String,Container> containers = new HashMap<>();
			if (ILinuxCliService.IS_WINDOWS) {
				Container c = new Container();
				c.setName("david");
				c.setStatus("Running");
				c.setStatusCode(StatusCode.RUNNING.getValue());
				State s = new State();
				s.setStatusCode(State.STATUS_CODE_RUNNING);
				s.setPid(123);
				c.setState(s);
				c.setArchitecture("x64");
				containers.put(c.getName(), c);
				
				Container c2 = new Container();
				c2.setName("test");
				c2.setStatus("Frozen");
				c2.setStatusCode(StatusCode.FROZEN.getValue());
				State s2 = new State();
				s2.setStatusCode(State.STATUS_CODE_STOPPED);
				s2.setPid(456);
				c2.setState(s2);
				c2.setArchitecture("win");
				containers.put(c2.getName(), c2);
			} else {
				containers = lxdService.loadContainers();
			}
			
			if (containers.isEmpty()) {
	            throw new Exception("no containers found!");
	        }
			else if (StringUtils.isEmpty(searchTerm)) {
				result.setMsg("Showing all containers!");
				result.setResult(containers.values());				
			}
			else {
	        	if (containers.containsKey(searchTerm)) {
	        		result.setResult(new ArrayList<Container>());
	        		result.getResult().add(containers.get(searchTerm));
	        		result.setMsg("success. found conatiner: "+containers.get(searchTerm));
	        	} else {
	        		throw new Exception("No container found with name: "+searchTerm);
	        	}
	        }
		} catch (Exception e) {
			LOG.error(e,e);
			result.setMsg(e.getMessage());
			return ResponseEntity.badRequest().body(result);
		}

        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/api/create/{newContainerName}/{imageName}")
    public ResponseEntity<?> createNew(@PathVariable String newContainerName, @PathVariable String imageName) {

    	AjaxResponseBody<Container> result = new AjaxResponseBody<>();

    	try {
    		if (StringUtils.isBlank(newContainerName)) {
    			throw new IllegalArgumentException("Cannot create new container if newContainerName is blank");
    		}
    		if (StringUtils.isBlank(imageName)) {
    			throw new IllegalArgumentException("Cannot create new container if imageName is blank");
    		}
			lxdService.createContainer(newContainerName, imageName);
			Container container = lxdService.getContainer(newContainerName);
			if (container!=null) {
				result.setResult(new ArrayList<Container>());
				result.getResult().add(container);
			}
		} catch (Exception e) {
			LOG.error(e,e);
			result.setMsg(e.getMessage());
			return ResponseEntity.badRequest().body(result);
		}
    	return ResponseEntity.ok(result);
    }    
}
