package au.com.jcloud.jlxd.ui.controller.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import au.com.jcloud.jlxd.ui.model.Server;
import au.com.jcloud.jlxd.ui.search.AjaxResponseBody;
import au.com.jcloud.jlxd.ui.service.ServerService;
import au.com.jcloud.lxd.LxdConstants;
import au.com.jcloud.lxd.service.ICachingLxdService;
import au.com.jcloud.lxd.service.ILxdService;

public abstract class BaseRestController<T> {

	private static final Logger LOG = Logger.getLogger(ContainerRestController.class);

	@Autowired
	private ICachingLxdService lxdService;

	@Autowired
	private ServerService serverService;

	public ICachingLxdService getLxdService(HttpServletRequest request) {
		ICachingLxdService lxdService = this.lxdService;
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
			if (isDefaultServerAndWindowsOs(lxdService)) {
				getLxdService(request).reloadContainerCache();
			}
			Collection<T> entities = loadEntities(getLxdService(request)).values();
			result.setResult(entities);
			if (classType==null && entities!=null && !entities.isEmpty()) {
				T entity = entities.iterator().next();
				if (entity !=null) {
					classType = (Class<T>) entity.getClass();
				}
			}
			result.setMsg((classType!=null? classType.getSimpleName() : StringUtils.EMPTY) + " reloaded");
		} catch (Exception e) {
			LOG.error(e, e);
			result.setMsg(e.getMessage());
			return ResponseEntity.badRequest().body(result);
		}
		return ResponseEntity.ok(result);
	}

	protected boolean isDefaultServerAndWindowsOs(ILxdService lxdService) {
		return LxdConstants.IS_WINDOWS && lxdService.getLxdServerCredential()!=null && StringUtils.isEmpty(lxdService.getLxdServerCredential().getRemoteHostAndPort());
	}

	public abstract Map<String, T> loadEntities(ICachingLxdService lxdService) throws IOException, InterruptedException;

	public ICachingLxdService getLxdService() {
		return lxdService;
	}

	public void setLxdService(ICachingLxdService lxdService) {
		this.lxdService = lxdService;
	}

	public ServerService getServerService() {
		return serverService;
	}

	public void setServerService(ServerService serverService) {
		this.serverService = serverService;
	}
}
