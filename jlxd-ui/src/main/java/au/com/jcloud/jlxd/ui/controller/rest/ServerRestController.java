package au.com.jcloud.jlxd.ui.controller.rest;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import au.com.jcloud.jlxd.ui.bean.AddServerInput;
import au.com.jcloud.jlxd.ui.model.Server;
import au.com.jcloud.jlxd.ui.search.AjaxResponseBody;
import au.com.jcloud.jlxd.ui.service.ServerService;
import au.com.jcloud.lxd.model.ServerInfo;
import au.com.jcloud.lxd.service.ICachingLxdService;

@RequestMapping("/server")
@RestController
public class ServerRestController extends BaseRestController<Server> {

	public static final Logger LOG = Logger.getLogger(ServerRestController.class);

	@Autowired
	private ServerService serverService;

	@RequestMapping(value = "", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<?> getAllServers(HttpServletRequest request) {
		AjaxResponseBody<Server> result = new AjaxResponseBody<>();
		try {
			Collection<Server> servers = serverService.getServerMap(request).values();
			result.setResult(servers);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			LOG.error(e, e);
			result.setMsg(e.getMessage());
			return ResponseEntity.badRequest().body(result);
		}
	}

	@PostMapping("/{name}")
	public ResponseEntity<?> selectServer(HttpServletRequest request, HttpServletResponse response, ModelMap model, @PathVariable String name) throws IOException, ServletException {
		AjaxResponseBody<Server> result = new AjaxResponseBody<>();

		Server currentServer = serverService.getServerFromSession(request);
		// Get all registered servers
		Map<String, Server> serverMap = serverService.getServerMap(request);
		// find lxdService from id;
		if (serverMap.containsKey(name)) {
			Server server = serverMap.get(name);
			if (currentServer != null) {
				currentServer.setActive(false);
			}
			serverService.setServerInSession(request, server);

			Collection<Server> servers = serverService.getServerMap(request).values();
			result.setResult(servers);
			return ResponseEntity.ok(result);
		}
		else {
			String errorMessage = "Could not find server with name: " + name;
			LOG.warn(errorMessage);
			result.setMsg(errorMessage);
			return ResponseEntity.badRequest().body(result);
		}
	}

	@PostMapping("/create")
	public ResponseEntity<?> createNewFromForm(HttpServletRequest request,
			@RequestBody AddServerInput addServerInput, Errors errors) {
		AjaxResponseBody<Server> result = new AjaxResponseBody<>();

		// TODO: x

		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/info")
	public ResponseEntity<?> getServerInfo(HttpServletRequest request) {
		AjaxResponseBody<ServerInfo> result = new AjaxResponseBody<>();
	
		Gson gson = new Gson();
		ServerInfo serverInfo;
		try {
//			if (isDefaultServerAndWindowsOs(getLxdService(request))) {
				File file = new ClassPathResource("/static/json/serverinfo.json").getFile();
				serverInfo = gson.fromJson(new FileReader(file), ServerInfo.class);
//			} else {
//				// TODO: get server info
//				serverInfo = getLxdService(request).getServerInfo();
//			}
			result.setMsg("read serverInfo");
			result.setResult(new ArrayList<ServerInfo>());
			result.getResult().add(serverInfo);
		} catch (Exception e) {
			LOG.error(e, e);
			result.setMsg(e.getMessage());
			return ResponseEntity.badRequest().body(result);
		}
		return ResponseEntity.ok(result);
	}

	@PostMapping("/create/{name}/{remoteHostAndPort}/{description}")
	public ResponseEntity<?> addServer(HttpServletRequest request, HttpServletResponse response, ModelMap model,
			@PathVariable String name, @PathVariable String remoteHostAndPort, @PathVariable String description)
			throws IOException, ServletException, CloneNotSupportedException {
		AjaxResponseBody<Server> result = new AjaxResponseBody<>();

		Map<String, Server> serverMap = serverService.getServerMap(request);
		if (serverMap.containsKey(name)) {
			throw new ServletException("The server with name: " + name + " already exists");
		}
		for (Server server : serverMap.values()) {
			if (remoteHostAndPort.equalsIgnoreCase(server.getRemoteHostAndPort())) {
				throw new ServletException("The server with remoteHostAndPort: " + remoteHostAndPort + " already exists");
			}
		}

		// TODO: How to upload these
		String remoteCert = null;
		String remoteKey = null;
		Server server = serverService.createNewServer(name, description, remoteHostAndPort, remoteCert, remoteKey);
		if (server == null) {
			throw new ServletException("Failed to create server with name: " + name);
		}
		serverMap.put(server.getName(), server);

		// Set the active server as the newly added server
		Server currentServer = serverService.getServerFromSession(request);
		currentServer.setActive(false);
		server.setActive(true);

		Collection<Server> servers = serverService.getServerMap(request).values();
		result.setResult(servers);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/delete/{name}")
	public ResponseEntity<?> deleteServer(HttpServletRequest request, HttpServletResponse response, ModelMap model,
			@PathVariable String name) throws IOException, ServletException {
		AjaxResponseBody<Server> result = new AjaxResponseBody<>();

		Server currentServer = serverService.getServerFromSession(request);
		Map<String, Server> serverMap = serverService.getServerMap(request);
		if (serverMap.containsKey(name)) {
			Server serverRemoved = serverMap.remove(name);
			if (currentServer != null && currentServer.equals(serverRemoved)) {
				if (!serverMap.isEmpty()) {
					currentServer = serverMap.values().iterator().next();
					serverService.setServerInSession(request, currentServer);
				}
			}
		}

		Collection<Server> servers = serverMap.values();
		result.setResult(servers);
		return ResponseEntity.ok(result);
	}

	@Override
	public void setServerService(ServerService serverService) {
		this.serverService = serverService;
	}

	@Override
	public Server getEntity(ICachingLxdService lxdService, String name) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Server> getEntities(ICachingLxdService lxdService) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}
}
