package au.com.jcloud.jlxd.ui.controller.rest;

import java.io.IOException;
import java.util.Collection;
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
			Collection<T> entities = getEntities(getLxdService(request)).values();
			result.setResult(entities);
			if (classType == null && entities != null && !entities.isEmpty()) {
				T entity = entities.iterator().next();
				if (entity != null) {
					classType = (Class<T>) entity.getClass();
				}
			}
			result.setMsg((classType != null ? classType.getSimpleName() : StringUtils.EMPTY) + " reloaded");
		} catch (Exception e) {
			LOG.error(e, e);
			result.setMsg(e.getMessage());
			return ResponseEntity.badRequest().body(result);
		}
		return ResponseEntity.ok(result);
	}

	@RequestMapping(value = "/view/{name}", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<?> viewContainer(HttpServletRequest request, @PathVariable String name) {
		try {
			T entity = getEntity(getLxdService(request), name);
			if (entity == null) {
				throw new Exception(name + " could not be found");
			}
			return ResponseEntity.ok(entity);
		} catch (Exception e) {
			LOG.error(e, e);
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	protected boolean isDefaultServerAndWindowsOs(ILxdService lxdService) {
		return LxdConstants.IS_WINDOWS && lxdService.getLxdServerCredential() != null && StringUtils.isEmpty(lxdService.getLxdServerCredential().getRemoteHostAndPort());
	}

	public abstract T getEntity(ICachingLxdService lxdService, String name) throws IOException, InterruptedException;

	public abstract Map<String, T> getEntities(ICachingLxdService lxdService) throws IOException, InterruptedException;

	public Map<String, T> getEntities(HttpServletRequest request) throws IOException, InterruptedException {
		return getEntities(getLxdService(request));
	}

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
