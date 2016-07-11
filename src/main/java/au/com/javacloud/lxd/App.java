package au.com.javacloud.lxd;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class App {

	private static final Logger LOG = Logger.getLogger(App.class);

	public static void main(String[] args) {
		LOG.info("LXC START");
		try {
		}
		catch (Exception e) {
			LOG.error(e,e);
		}
		LOG.info("LXC DONE");
	}

}
		
