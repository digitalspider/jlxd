package au.com.jcloud.jlxd.ui.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import au.com.jcloud.jlxd.ui.Constants;
import au.com.jcloud.jlxd.ui.model.Server;

@Service
public class RequestHelperService {

	public Map<String, Server> getServerMapFromSession(HttpServletRequest request) {
		Map<String, Server> serverMap = (Map<String, Server>) request.getSession().getAttribute(Constants.SESSION_LXD_SERVERS);
		if (serverMap==null) {
			serverMap = new HashMap<>();
			request.getSession().setAttribute(Constants.SESSION_LXD_SERVERS, serverMap);
		}
		return serverMap;
	}

}
