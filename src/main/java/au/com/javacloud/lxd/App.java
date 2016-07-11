package au.com.javacloud.lxd;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import au.com.javacloud.lxd.model.Container;
import au.com.javacloud.lxd.util.LinuxUtil;

public class App {

	private static final Logger LOG = Logger.getLogger(App.class);

	public static void main(String[] args) {
		LOG.info("LXC START");
		try {
			String result = LinuxUtil.executeLinuxCmd("echo david");
			LOG.info("result="+result);
			int lines = LinuxUtil.getLinesCountForFile(new File("pom.xml"));
			LOG.info("lines="+lines);
			List<String> linesList = LinuxUtil.getLastNFileLines(new File("pom.xml"),5);
			LOG.info("linesList="+linesList);
			Container[] containers = LinuxUtil.executeLinuxCmdWithResultJsonObject("lxc list --format json", Container[].class);
			for (Container container : containers) {
				LOG.info("container=" + container);
			}
		}
		catch (Exception e) {
			LOG.error(e,e);
		}
		LOG.info("LXC DONE");
	}

}
