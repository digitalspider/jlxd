package au.com.jcloud.jlxd.ui.controller.rest;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.com.jcloud.jlxd.ui.Constants;
import au.com.jcloud.jlxd.ui.model.Server;
import au.com.jcloud.jlxd.ui.service.RequestHelperService;
import au.com.jcloud.lxd.bean.LxdServerCredential;
import au.com.jcloud.lxd.service.ILxdService;
import au.com.jcloud.lxd.service.impl.LxdServiceImpl;

@RequestMapping("/server")
@RestController
public class ServerRestController {

	public static final Logger LOG = Logger.getLogger(ServerRestController.class);
	
	@Autowired
	private RequestHelperService requestHelperService;

	@PostMapping("/{name}/**")
	public void getServer(HttpServletRequest request, HttpServletResponse response, ModelMap model, @PathVariable String name) throws IOException, ServletException {
		request.removeAttribute(Constants.REQUEST_LXD_SERVER);
		// Get all registered servers
		Map<String, Server> serverMap = requestHelperService.getServerMapFromSession(request);
		// find lxdService from id;
		if (serverMap.containsKey(name)) {
			Server server = serverMap.get(name);
			request.setAttribute(Constants.REQUEST_LXD_SERVER, server);
		} else {
			LOG.warn("Could not find server with name: "+name);
		}
		// Forward to the actual controller
		String forwardUrl = request.getServletPath().replace("/server/" + name,StringUtils.EMPTY);

		response.sendRedirect(forwardUrl); // This sends a GET
	}

	
	@PostMapping("/add/{name}/{remoteHostAndPort}/{description}")
	public void addServer(HttpServletRequest request, HttpServletResponse response, ModelMap model, 
			@PathVariable String name, @PathVariable String remoteHostAndPort, @PathVariable String description) 
					throws IOException, ServletException {
		Map<String, Server> serverMap = requestHelperService.getServerMapFromSession(request);
		if (serverMap.containsKey(name)) {
			throw new ServletException("The server with name: "+name+" already exists");
		}
		Server server = new Server();
		server.setName(name);
		server.setDescription(description);
		server.setRemoteHostAndPort(remoteHostAndPort);
		ILxdService lxdService = new LxdServiceImpl();
		LxdServerCredential credential = new LxdServerCredential();
		credential.setRemoteHostAndPort(remoteHostAndPort);
		// TODO: How to upload these
//		credential.setRemoteCert(remoteCert);
//		credential.setRemoteKey(remoteKey);
		lxdService.setLxdServerCredential(credential);
		server.setLxdService(lxdService);
		serverMap.put(server.getName(), server);
	}
	
	@PostMapping("/delete/{name}")
	public void deleteServer(HttpServletRequest request, HttpServletResponse response, ModelMap model, 
			@PathVariable String name) throws IOException, ServletException {
		Map<String, Server> serverMap = requestHelperService.getServerMapFromSession(request);
		if (serverMap.containsKey(name)) {
			serverMap.remove(name);
		}
	}

	public void setRequestHelperService(RequestHelperService requestHelperService) {
		this.requestHelperService = requestHelperService;
	}
}
