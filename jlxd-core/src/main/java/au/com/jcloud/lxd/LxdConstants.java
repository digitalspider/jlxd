package au.com.jcloud.lxd;

import java.io.File;

public class LxdConstants {
	public static final String OS_NAME = System.getProperty("os.name");
	public static final String USER_DIR = System.getProperty("user.dir");
	public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

	public static final String USER_LXD_DIR = LxdConstants.USER_DIR + File.separator + ".lxd";

	public static final boolean IS_WINDOWS = OS_NAME.toLowerCase().startsWith("win");
}
