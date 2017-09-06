package au.com.jcloud.jlxd.ui.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import au.com.jcloud.jlxd.ui.bean.AddContainerInput;
import au.com.jcloud.jlxd.ui.model.Server;
import au.com.jcloud.jlxd.ui.search.AjaxResponseBody;
import au.com.jcloud.jlxd.ui.service.ServerService;
import au.com.jcloud.lxd.LxdConstants;
import au.com.jcloud.lxd.bean.ImageConfig;
import au.com.jcloud.lxd.model.Container;
import au.com.jcloud.lxd.model.State;
import au.com.jcloud.lxd.model.StatusCode;
import au.com.jcloud.lxd.service.ILxdService;

@RequestMapping("/container")
@RestController
public class ContainerRestController {

	private static final Logger LOG = Logger.getLogger(ContainerRestController.class);

	@Autowired
	private ILxdService lxdService;

	@Autowired
	private ServerService serverService;

	@RequestMapping(value = "/search", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<?> getSearchResult(HttpServletRequest request) {
		return getSearchResult(request, StringUtils.EMPTY);
	}

	@RequestMapping(value = "/search/{searchTerm}", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<?> getSearchResult(HttpServletRequest request, @PathVariable String searchTerm) {

		AjaxResponseBody<Container> result = new AjaxResponseBody<>();

		if (StringUtils.isEmpty(searchTerm)) {
			result.setMsg("Showing all containers!");
		}
		int containersFound = 0;

		// Get all servers
		Map<String, Server> serverMap = serverService.getServerMap(request);
		Server serverInRequest = serverService.getServerFromSession(request);
		Collection<Server> serversToSearch = new ArrayList<>();
		if (serverInRequest != null) {
			serversToSearch.add(serverInRequest);
		}
		else {
			serversToSearch.addAll(serverMap.values());
		}

		Collection<Container> resultContainers = new ArrayList<>();
		StringBuilder serverString = new StringBuilder();
		for (Server server : serversToSearch) {
			try {
				List<Container> containers = findContainersForLxdService(server.getLxdService(), searchTerm);
				containersFound += containers.size();
				LOG.debug("Fonund " + containers.size() + " containers with name: " + searchTerm);
				server.setContainers(containers);
				resultContainers.addAll(containers);
				if (serverString.length() > 0) {
					serverString.append(",");
				}
				serverString.append(server.getName());
			} catch (Exception e) {
				LOG.error(e, e);
				result.setMsg(e.getMessage());
				return ResponseEntity.badRequest().body(result);
			}
		}

		result.setResult(resultContainers);
		result.setMsg("success. found " + containersFound + " conatiners in server(s) " + serverString + " for searchTerm: " + searchTerm);

		return ResponseEntity.ok(result);
	}

	private List<Container> findContainersForLxdService(ILxdService lxdService, String searchTerm) throws IOException, InterruptedException {
		List<Container> result = new ArrayList<>();
		Map<String, Container> containers = loadContainersForLxdService(lxdService);
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

	private Map<String, Container> loadContainersForLxdService(ILxdService lxdService) throws IOException, InterruptedException {
		Map<String, Container> containers = new HashMap<>();
		if (LxdConstants.IS_WINDOWS && StringUtils.isEmpty(lxdService.getLxdServerCredential().getRemoteHostAndPort())) {
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
		}
		else {
			containers = lxdService.getContainerMap();
			for (Container container : containers.values()) {
				State state = lxdService.getContainerState(container.getName());
				container.setState(state);
			}
		}
		return containers;
	}

	@RequestMapping(value = "/start/{containerName}", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<?> startContainer(HttpServletRequest request, @PathVariable String containerName) {
		AjaxResponseBody<Container> result = new AjaxResponseBody<>();

		try {
			if (StringUtils.isBlank(containerName)) {
				throw new IllegalArgumentException("Cannot start container if containerName is blank");
			}
			getLxdService(request).startContainer(containerName);
			result.setResult(loadContainersForLxdService(getLxdService(request)).values());
			result.setMsg("container started: " + containerName);
		} catch (Exception e) {
			LOG.error(e, e);
			result.setMsg(e.getMessage());
			return ResponseEntity.badRequest().body(result);
		}
		return ResponseEntity.ok(result);
	}

	@RequestMapping(value = "/stop/{containerName}", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<?> stopContainer(HttpServletRequest request, @PathVariable String containerName) {
		AjaxResponseBody<Container> result = new AjaxResponseBody<>();

		try {
			if (StringUtils.isBlank(containerName)) {
				throw new IllegalArgumentException("Cannot start container if containerName is blank");
			}
			getLxdService(request).stopContainer(containerName);
			result.setResult(loadContainersForLxdService(getLxdService(request)).values());
			result.setMsg("container stopped: " + containerName);
		} catch (Exception e) {
			LOG.error(e, e);
			result.setMsg(e.getMessage());
			return ResponseEntity.badRequest().body(result);
		}
		return ResponseEntity.ok(result);
	}

	private ILxdService getLxdService(HttpServletRequest request) {
		ILxdService lxdService = this.lxdService;
		Server lxdServer = serverService.getServerFromSession(request);
		if (lxdServer != null) {
			lxdService = lxdServer.getLxdService();
		}
		return lxdService;
	}

	@PostMapping("/create")
	public ResponseEntity<?> createNewFromForm(HttpServletRequest request,
			@Valid @RequestBody AddContainerInput addContainerInput, Errors errors) {
		AjaxResponseBody<Container> result = new AjaxResponseBody<>();

		// If validation errors, just return a 400 bad request, along with the error message
		if (errors.hasErrors()) {
			result.setMsg(StringUtils.join(errors.getAllErrors(), ","));
			return ResponseEntity.badRequest().body(result);
		}

		try {
			String containerName = addContainerInput.getName();
			getLxdService(request).createContainer(addContainerInput.getName(), addContainerInput.getImageAlias(), addContainerInput);
			result.setResult(loadContainersForLxdService(getLxdService(request)).values());
			result.setMsg("container created: " + containerName);
		} catch (Exception e) {
			LOG.error(e, e);
			result.setMsg(e.getMessage());
			return ResponseEntity.badRequest().body(result);
		}
		return ResponseEntity.ok(result);
	}

	@PostMapping("/create/{newContainerName}/{imageAlias}/{ephemeral}/{profile}/{config}")
	public ResponseEntity<?> createNew(HttpServletRequest request, @PathVariable String newContainerName,
			@PathVariable String imageAlias, @PathVariable String ephemeral, @PathVariable String profile,
			@PathVariable String config) {

		AjaxResponseBody<Container> result = new AjaxResponseBody<>();

		try {
			if (StringUtils.isBlank(newContainerName)) {
				throw new IllegalArgumentException("Cannot create new container if newContainerName is blank");
			}
			if (StringUtils.isBlank(imageAlias)) {
				throw new IllegalArgumentException("Cannot create new container if imageName is blank");
			}
			ILxdService lxdService = getLxdService(request);
			Boolean ephemeralValue = null;
			if (StringUtils.isNotBlank(ephemeral)) {
				ephemeralValue = Boolean.valueOf(ephemeral);
			}
			String architecture = null;
			List<String> profilesList = Arrays.asList(profile.split(","));
			ImageConfig imageConfig = new ImageConfig(ephemeralValue, architecture, profilesList, config);
			lxdService.createContainer(newContainerName, imageAlias, imageConfig);
			Container container = lxdService.getContainer(newContainerName);
			if (container == null) {
				throw new Exception("Could not get newly created container. " + newContainerName);
			}
			result.setMsg("container created: " + container.getName());
			result.setResult(loadContainersForLxdService(getLxdService(request)).values());
		} catch (Exception e) {
			LOG.error(e, e);
			result.setMsg(e.getMessage());
			return ResponseEntity.badRequest().body(result);
		}
		return ResponseEntity.ok(result);
	}

	public void setLxdService(ILxdService lxdService) {
		this.lxdService = lxdService;
	}

	public void setServerService(ServerService serverService) {
		this.serverService = serverService;
	}
}
