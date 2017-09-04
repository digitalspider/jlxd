package au.com.jcloud.lxd;

import java.io.File;

public class LxdConstants {
	public static final String OS_NAME = System.getProperty("os.name");
	public static final String USER_DIR = System.getProperty("user.dir");
	public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

	public static final String USER_LXD_DIR = LxdConstants.USER_DIR + File.separator + ".lxd";

	public static final boolean IS_WINDOWS = OS_NAME.toLowerCase().startsWith("win");

	public static final String DEFAULT_FILEPATH_REMOTE_CERT = LxdConstants.USER_LXD_DIR + File.separator + "client.crt";
	public static final String DEFAULT_FILEPATH_REMOTE_KEY = LxdConstants.USER_LXD_DIR + File.separator + "client.key";

	public static final String COLON = ":";
}
