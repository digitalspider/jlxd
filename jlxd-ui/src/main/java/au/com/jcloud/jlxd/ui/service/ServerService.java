package au.com.jcloud.jlxd.ui.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.jcloud.jlxd.ui.Constants;
import au.com.jcloud.jlxd.ui.model.Server;
import au.com.jcloud.lxd.bean.LxdServerCredential;
import au.com.jcloud.lxd.service.ILxdService;

@Service
public class ServerService {

	private static final Logger LOG = Logger.getLogger(ServerService.class);

	@Autowired
	private ILxdService lxdService;

	@Autowired
	private RequestHelperService requestHelperService;

	public Map<String, Server> getServerMap(HttpServletRequest request) {
		Map<String, Server> serverMap = requestHelperService.getAttributeFromSession(request, Constants.SESSION_LXD_SERVERS);
		if (serverMap == null) {
			serverMap = new HashMap<>();
			requestHelperService.setAttributeInSession(request, Constants.SESSION_LXD_SERVERS, serverMap);
		}
		return serverMap;
	}

	public void clearServerInSession(HttpServletRequest request) {
		requestHelperService.removeAttributeFromSession(request, Constants.SESSION_LXD_SERVER);
	}

	public void setServerInSession(HttpServletRequest request, Server server) {
		requestHelperService.setAttributeInSession(request, Constants.SESSION_LXD_SERVER, server);
	}

	public Server getServerFromSession(HttpServletRequest request) {
		Server server = requestHelperService.getAttributeFromSession(request, Constants.SESSION_LXD_SERVER);
		return server;
	}

	public Server createNewServer(String name, String description, String hostAndPort, String remoteCert, String remoteKey) throws CloneNotSupportedException {
		Server server = new Server();
		server.setName(name);
		server.setDescription(description);
		ILxdService service = lxdService.clone();
		LxdServerCredential credential = new LxdServerCredential();
		credential.setRemoteHostAndPort(hostAndPort);
		credential.setRemoteCert(remoteCert);
		credential.setRemoteKey(remoteKey);
		service.setLxdServerCredential(credential);
		server.setLxdService(service);
		return server;
	}

	public void setLxdService(ILxdService lxdService) {
		this.lxdService = lxdService;
	}
}
