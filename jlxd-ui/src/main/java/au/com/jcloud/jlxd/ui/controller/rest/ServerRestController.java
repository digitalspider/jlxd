package au.com.jcloud.jlxd.ui.controller.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.com.jcloud.jlxd.ui.Constants;
import au.com.jcloud.lxd.service.ILxdService;

@RequestMapping("/server")
@RestController
public class ServerRestController {

	public static final Logger LOG = Logger.getLogger(ServerRestController.class);

	@PostMapping("/{id}/**")
	public void getServer(HttpServletRequest request, HttpServletResponse response, ModelMap model, @PathVariable String id) throws IOException, ServletException {
		// Get all registered servers
		Map<String, ILxdService> serverMap = (Map<String, ILxdService>) request.getSession().getAttribute(Constants.SESSION_LXD_SERVERS);
		if (serverMap==null) {
			serverMap = new HashMap<>();
			request.getSession().setAttribute(Constants.SESSION_LXD_SERVERS, serverMap);
		}
		// find lxdService from id;
		if (serverMap.containsKey(id)) {
			ILxdService lxdService = serverMap.get(id);
			request.setAttribute(Constants.REQUEST_LXD_SERVICE, lxdService);
		}
		// Forward to the actual controller
		String forwardUrl = request.getServletPath().replace("/server/" + id,StringUtils.EMPTY);

		response.sendRedirect(forwardUrl); // This sends a GET
	}
}
