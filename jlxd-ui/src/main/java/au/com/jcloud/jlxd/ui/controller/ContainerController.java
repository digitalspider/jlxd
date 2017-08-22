package au.com.jcloud.jlxd.ui.controller;

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
import org.springframework.web.bind.annotation.RestController;

import au.com.jcloud.jlxd.ui.SearchCriteria;
import au.com.jcloud.jlxd.ui.model.AjaxResponseBody;
import au.com.jcloud.lxd.model.Container;
import au.com.jcloud.lxd.model.State;
import au.com.jcloud.lxd.service.ILxdService;

@RestController
public class ContainerController {

	private static final Logger LOG = Logger.getLogger(ContainerController.class);
	
    ILxdService lxdService;

    @Autowired
    public void setLxdService(ILxdService lxdService) {
        this.lxdService = lxdService;
    }

    @PostMapping("/api/search")
    public ResponseEntity<?> getSearchResultViaAjax(
            @Valid @RequestBody SearchCriteria search, Errors errors) {

        AjaxResponseBody result = new AjaxResponseBody();

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
//			Map<String,Container> containers = lxdService.loadContainers();
			Map<String,Container> containers = new HashMap<>();
			Container c = new Container();
			c.setName("david");
			State s = new State();
			s.setStatusCode(State.STATUS_CODE_RUNNING);
			c.setState(s);
			c.setArchitecture("x64");
			containers.put(c.getName(), c);
			
			if (containers!=null) {
				result.setResult(containers.values());
			}
	        if (containers.isEmpty()) {
	            result.setMsg("no containers found!");
	        } else {
	        	if (containers.containsKey(search.getSearchTerm())) {
	        		result.setMsg("success. found conatiner: "+containers.get(search.getSearchTerm()));
	        	} else {
	        		result.setMsg("success. No specific container found");
	        	}
	        }
		} catch (Exception e) {
			LOG.error(e,e);
			result.setMsg("Error occurred: "+e.getMessage());
		}

        return ResponseEntity.ok(result);
    }
}
