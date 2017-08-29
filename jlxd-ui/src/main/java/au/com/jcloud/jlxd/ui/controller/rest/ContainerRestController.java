package au.com.jcloud.jlxd.ui.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import au.com.jcloud.jlxd.ui.service.RequestHelperService;
import au.com.jcloud.jlxd.ui.service.ServerService;
import au.com.jcloud.lxd.model.Container;
import au.com.jcloud.lxd.model.State;
import au.com.jcloud.lxd.model.StatusCode;
import au.com.jcloud.lxd.service.ILinuxCliService;
import au.com.jcloud.lxd.service.ILxdService;

@RequestMapping("/container")
@RestController
public class ContainerRestController {

	private static final Logger LOG = Logger.getLogger(ContainerRestController.class);
	public static final String SERVER_NAME_DEFAULT = "default";

	@Autowired
	private ILxdService lxdService;

	@Autowired
	private ServerService serverService;

	@Autowired
	private RequestHelperService requestHelperService;

	@RequestMapping(value = "/search", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<?> getSearchResult(HttpServletRequest request) {
		return getSearchResult(request, StringUtils.EMPTY);
	}

	@RequestMapping(value = "/search/{searchTerm}", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<?> getSearchResult(HttpServletRequest request, @PathVariable String searchTerm) {

		AjaxResponseBody<Server> result = new AjaxResponseBody<>();

		if (StringUtils.isEmpty(searchTerm)) {
			result.setMsg("Showing all containers!");
		}
		int containersFound = 0;

		// Get all servers
		Map<String, Server> serverMap = requestHelperService.getServerMapFromSession(request);
		Server serverInRequest = (Server) request.getAttribute(Constants.REQUEST_LXD_SERVER);
		if (serverInRequest != null) {
			serverMap.clear();
			serverMap.put(serverInRequest.getName(), serverInRequest);
		}

		// initialise default server
		if (serverMap.isEmpty() && lxdService != null) {
			try {
				Server defaultServer = serverService.createNewServer(SERVER_NAME_DEFAULT, "Default server on host", null, null, null);
				serverMap.put(SERVER_NAME_DEFAULT, defaultServer);

				String serverName = "odr1";
				String serverDesc = "description";
				String serverHost = "192.168.1.113";
				String remoteCert = "C:/apps/lxd/client.crt";
				String remoteKey = "C:/apps/lxd/client.key";
				Server testServer = serverService.createNewServer(serverName, serverDesc, serverHost, remoteCert, remoteKey);
				serverMap.put(serverName, testServer);

				serverName = "odr2";
				serverDesc = "description";
				serverHost = "192.168.1.112";
				remoteCert = "C:/apps/lxd/client.crt";
				remoteKey = "C:/apps/lxd/client.key";
				testServer = serverService.createNewServer(serverName, serverDesc, serverHost, remoteCert, remoteKey);
				serverMap.put(serverName, testServer);
			} catch (Exception e) {
				LOG.error(e, e);
			}
		}

		Collection<Server> servers = serverMap.values();

		for (String name : serverMap.keySet()) {
			try {
				Server server = serverMap.get(name);
				List<Container> containers = findContainersForLxdService(server.getLxdService(), searchTerm);
				containersFound += containers.size();
				LOG.debug("Fonund " + containers.size() + " containers with name: " + searchTerm);
				server.setContainers(containers);
			} catch (Exception e) {
				LOG.error(e, e);
				result.setMsg(e.getMessage());
				return ResponseEntity.badRequest().body(result);
			}
		}

		result.setResult(servers);
		result.setMsg("success. found " + containersFound + " conatiners for searchTerm: " + searchTerm);

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
		if (ILinuxCliService.IS_WINDOWS && StringUtils.isEmpty(lxdService.getLxdServerCredential().getRemoteHostAndPort())) {
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
			containers = lxdService.loadContainers();
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
		} catch (Exception e) {
			LOG.error(e, e);
			result.setMsg(e.getMessage());
			return ResponseEntity.badRequest().body(result);
		}
		return ResponseEntity.ok(result);
	}

	private ILxdService getLxdService(HttpServletRequest request) {
		ILxdService lxdService = this.lxdService;
		Server lxdServer = (Server) request.getSession().getAttribute(Constants.REQUEST_LXD_SERVER);
		if (lxdServer != null) {
			lxdService = lxdServer.getLxdService();
			request.getSession().removeAttribute(Constants.REQUEST_LXD_SERVER);
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
			if (container != null) {
				result.setResult(new ArrayList<Container>());
				result.getResult().add(container);
			}
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

	public void setRequestHelperService(RequestHelperService requestHelperService) {
		this.requestHelperService = requestHelperService;
	}

}
