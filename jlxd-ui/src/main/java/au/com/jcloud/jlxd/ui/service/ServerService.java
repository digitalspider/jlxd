package au.com.jcloud.jlxd.ui.service;

import java.util.Map;
import java.util.TreeMap;

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

	public static final String SERVER_NAME_DEFAULT = "default";

	@Autowired
	private ILxdService lxdService;

	@Autowired
	private RequestHelperService requestHelperService;

	public Map<String, Server> getServerMap(HttpServletRequest request) {
		Map<String, Server> serverMap = requestHelperService.getAttributeFromSession(request, Constants.SESSION_LXD_SERVERS);
		if (serverMap == null) {
			serverMap = new TreeMap<>();
			requestHelperService.setAttributeInSession(request, Constants.SESSION_LXD_SERVERS, serverMap);
			initServerMap(request, serverMap);
		}
		return serverMap;
	}

	private void initServerMap(HttpServletRequest request, Map<String, Server> serverMap) {
		// initialise default server
		if (serverMap.isEmpty() && lxdService != null) {
			try {
				Server defaultServer = createNewServer(SERVER_NAME_DEFAULT, "Default server on host", null, null, null);
				serverMap.put(SERVER_NAME_DEFAULT, defaultServer);
				setServerInSession(request, defaultServer);
				defaultServer.setActive(true);

				String serverName = "odr1";
				String serverDesc = "description";
				String serverHost = "192.168.1.113";
				String remoteCert = "C:/apps/lxd/client.crt";
				String remoteKey = "C:/apps/lxd/client.key";
				Server testServer = createNewServer(serverName, serverDesc, serverHost, remoteCert, remoteKey);
				serverMap.put(serverName, testServer);

				serverName = "odr2";
				serverDesc = "description";
				serverHost = "192.168.1.112";
				remoteCert = "C:/apps/lxd/client.crt";
				remoteKey = "C:/apps/lxd/client.key";
				testServer = createNewServer(serverName, serverDesc, serverHost, remoteCert, remoteKey);
				serverMap.put(serverName, testServer);
			} catch (Exception e) {
				LOG.error(e, e);
			}
		}
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
