package au.com.jcloud.jlxd.ui.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.jcloud.jlxd.ui.model.Server;
import au.com.jcloud.lxd.bean.LxdServerCredential;
import au.com.jcloud.lxd.service.ILxdService;

@Service
public class ServerService {

	private static final Logger LOG = Logger.getLogger(ServerService.class);

	@Autowired
	private ILxdService lxdService;

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
