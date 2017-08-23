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
    public ResponseEntity<?> getSearchResultViaAjax(
            @Valid @RequestBody SearchCriteria search, Errors errors) {

        AjaxResponseBody<Container> result = new AjaxResponseBody<>();

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
			
			String searchTerm = search.getSearchTerm();
			if ("*".equalsIgnoreCase(searchTerm)) {
				result.setMsg("Showing all containers!");
				result.setResult(containers.values());				
			}
			else if (containers.isEmpty()) {
	            throw new Exception("no containers found!");
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
		}

        return ResponseEntity.ok(result);
    }
}
