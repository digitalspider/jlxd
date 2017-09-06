package au.com.jcloud.jlxd.ui.controller.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import au.com.jcloud.jlxd.ui.model.Server;
import au.com.jcloud.jlxd.ui.search.AjaxResponseBody;
import au.com.jcloud.jlxd.ui.service.ServerService;
import au.com.jcloud.lxd.service.ILxdService;

public abstract class BaseRestController<T> {

	private static final Logger LOG = Logger.getLogger(ContainerRestController.class);

	@Autowired
	private ILxdService lxdService;

	@Autowired
	private ServerService serverService;

	public ILxdService getLxdService(HttpServletRequest request) {
		ILxdService lxdService = this.lxdService;
		Server lxdServer = serverService.getServerFromSession(request);
		if (lxdServer != null) {
			lxdService = lxdServer.getLxdService();
		}
		return lxdService;
	}

	@PostMapping("/reload")
	public ResponseEntity<?> reload(HttpServletRequest request, Class<T> classType) {
		AjaxResponseBody<T> result = new AjaxResponseBody<>();
		try {
			getLxdService(request).reloadContainerCache();
			Collection<T> entities = loadEntities(getLxdService(request)).values();
			result.setResult(entities);
			result.setMsg(classType.getSimpleName() + " reloaded");
		} catch (Exception e) {
			LOG.error(e, e);
			result.setMsg(e.getMessage());
			return ResponseEntity.badRequest().body(result);
		}
		return ResponseEntity.ok(result);
	}

	public abstract Map<String, T> loadEntities(ILxdService lxdService) throws IOException, InterruptedException;

	public ILxdService getLxdService() {
		return lxdService;
	}

	public void setLxdService(ILxdService lxdService) {
		this.lxdService = lxdService;
	}

	public ServerService getServerService() {
		return serverService;
	}

	public void setServerService(ServerService serverService) {
		this.serverService = serverService;
	}
}
