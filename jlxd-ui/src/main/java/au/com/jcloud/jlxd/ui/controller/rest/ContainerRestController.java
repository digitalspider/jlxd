package au.com.jcloud.jlxd.ui.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import au.com.jcloud.jlxd.ui.Constants;
import au.com.jcloud.jlxd.ui.model.Server;
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

    @RequestMapping(value="/search", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> getSearchResult(HttpServletRequest request) {
    	return getSearchResult(request, StringUtils.EMPTY);
    }

    @RequestMapping(value="/search/{searchTerm}", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> getSearchResult(HttpServletRequest request, @PathVariable String searchTerm) {

        AjaxResponseBody<Server> result = new AjaxResponseBody<>();

		List<Server> servers = new ArrayList<>();

		if (StringUtils.isEmpty(searchTerm)) {
			result.setMsg("Showing all containers!");
		}
		int containersFound = 0;

		// Get all servers
		Map<String,ILxdService> serverMap = (Map<String,ILxdService>) request.getSession().getAttribute(Constants.SESSION_LXD_SERVERS);
		if (serverMap==null) {
			serverMap = new HashMap<>();
			request.getSession().setAttribute(Constants.SESSION_LXD_SERVERS, serverMap);
		}

		// initialise default server
		if (serverMap.isEmpty() && lxdService!=null) {
			serverMap.put("default", lxdService);
		}

		for (String name : serverMap.keySet()) {
			try {
				Server server = new Server();
				server.setName(name);
				ILxdService lxdService = serverMap.get(name);
				String remoteHostAndPort = (lxdService.getLxdServerCredential()!=null) ? lxdService.getLxdServerCredential().getRemoteHostAndPort() : null;
				server.setRemoteHostAndPort(remoteHostAndPort);
				List<Container> containers = findContainersForLxdService(lxdService, searchTerm);
				containersFound += containers.size();
				LOG.debug("Fonund "+containers.size()+" containers with name: "+searchTerm);
				server.setContainers(containers);
				servers.add(server);
			} catch (Exception e) {
				LOG.error(e,e);
				result.setMsg(e.getMessage());
				return ResponseEntity.badRequest().body(result);
			}
		}

		result.setResult(servers);
		result.setMsg("success. found "+ containersFound +" conatiners for searchTerm: "+searchTerm);

        return ResponseEntity.ok(result);
    }

    private List<Container> findContainersForLxdService(ILxdService lxdService, String searchTerm) throws IOException, InterruptedException {
		List<Container> result = new ArrayList<>();
		Map<String,Container> containers = loadContainersForLxdService(lxdService);
		if (containers.isEmpty()) {
			return result;
		}
		else if (StringUtils.isEmpty(searchTerm)) {
			result.addAll(containers.values());
		}
		else if (containers.containsKey(searchTerm)) {
			result.add(containers.get(searchTerm));
		}
		return result;
	}

    private Map<String,Container> loadContainersForLxdService(ILxdService lxdService) throws IOException, InterruptedException {
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
		return containers;
	}

    @RequestMapping(value="/start/{containerName}", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> startContainer(HttpServletRequest request, @PathVariable String containerName) {
    	AjaxResponseBody<Container> result = new AjaxResponseBody<>();

    	try {
    		if (StringUtils.isBlank(containerName)) {
    			throw new IllegalArgumentException("Cannot start container if containerName is blank");
    		}
			getLxdService(request).startContainer(containerName);
		} catch (Exception e) {
			LOG.error(e,e);
			result.setMsg(e.getMessage());
			return ResponseEntity.badRequest().body(result);
		}
    	return ResponseEntity.ok(result);
	}

    @RequestMapping(value="/stop/{containerName}", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> stopContainer(HttpServletRequest request, @PathVariable String containerName) {
    	AjaxResponseBody<Container> result = new AjaxResponseBody<>();

    	try {
    		if (StringUtils.isBlank(containerName)) {
    			throw new IllegalArgumentException("Cannot start container if containerName is blank");
    		}
			getLxdService(request).stopContainer(containerName);
		} catch (Exception e) {
			LOG.error(e,e);
			result.setMsg(e.getMessage());
			return ResponseEntity.badRequest().body(result);
		}
    	return ResponseEntity.ok(result);
	}

	private ILxdService getLxdService(HttpServletRequest request) {
		ILxdService lxdService = this.lxdService;
		ILxdService lxdServiceInRequest = (ILxdService) request.getAttribute(Constants.REQUEST_LXD_SERVICE);
		if (lxdServiceInRequest!=null) {
			lxdService = lxdServiceInRequest;
		}
		return lxdService;
	}

    @PostMapping("/create/{newContainerName}/{imageName}")
    public ResponseEntity<?> createNew(HttpServletRequest request, @PathVariable String newContainerName, @PathVariable String imageName) {

    	AjaxResponseBody<Container> result = new AjaxResponseBody<>();

    	try {
    		if (StringUtils.isBlank(newContainerName)) {
    			throw new IllegalArgumentException("Cannot create new container if newContainerName is blank");
    		}
    		if (StringUtils.isBlank(imageName)) {
    			throw new IllegalArgumentException("Cannot create new container if imageName is blank");
    		}
			getLxdService(request).createContainer(newContainerName, imageName);
			Container container = getLxdService(request).getContainer(newContainerName);
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
