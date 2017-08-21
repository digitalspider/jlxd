package au.com.jcloud.jlxd.ui.controller;

import java.util.Collection;

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
import au.com.jcloud.lxd.service.ILxdService;

@RestController
public class ContainerController {

	private static final Logger LOG = Logger.getLogger(ContainerController.class);
	
    ILxdService lxdService;

    @Autowired
    public void setUserService(ILxdService lxdService) {
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
			Collection<Container> containers = lxdService.loadContainers().values();
	        if (containers.isEmpty()) {
	            result.setMsg("no containers found!");
	        } else {
	            result.setMsg("success");
	        }
	        result.setResult(containers);
		} catch (Exception e) {
			LOG.error(e,e);
		}

        return ResponseEntity.ok(result);
    }
}
