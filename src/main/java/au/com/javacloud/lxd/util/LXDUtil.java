package au.com.javacloud.lxd.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.javacloud.lxd.model.response.ContainerResponse;
import au.com.javacloud.lxd.model.response.ImageResponse;
import au.com.javacloud.lxd.model.response.ListResponse;
import au.com.javacloud.lxd.model.response.ResponseBase;

public class LXDUtil {

	public static final String CURL_URL_BASE = "curl -s --unix-socket /var/lib/lxd/unix.socket";

	public static final String URL_CONTAINER_GET = "a/1.0/container";
	public static final String URL_IMAGE_GET = "a/1.0/image";
	public static final String URL_CERTIFICATE_GET = "a/1.0/certificate";
	public static final String URL_NETWORK_GET = "a/1.0/network";
	public static final String URL_OPERATION_GET = "a/1.0/operation";
	public static final String URL_PROFILE_GET = "a/1.0/profile";
	public static final String STATUS_CODE_200 = "200"; // SUCCESS
	public static final String STATUS_CODE_400 = "400"; // ERROR

	public enum LxdCall {
		CONTAINER_GET(URL_CONTAINER_GET, ContainerResponse.class), IMAGE_GET(URL_IMAGE_GET, ImageResponse.class), CERTIFICATE_GET(URL_CERTIFICATE_GET, ResponseBase.class), NETWORK_GET(URL_NETWORK_GET, ResponseBase.class), OPERATION_GET(URL_OPERATION_GET,
				ResponseBase.class), PROFILE_GET(URL_PROFILE_GET, ResponseBase.class);

		LxdCall(String command, Class<? extends ResponseBase> classType) {
			this.command = command;
			this.classType = classType;
		}

		private String command;
		private Class<? extends ResponseBase> classType;
	}

	/**
	 * NOT YET TESTED!
	 */
	public static <T> T executeCurlGetCmd(LxdCall lxdCall, String id) throws IOException, InterruptedException {
		ResponseBase response = LinuxUtil.executeLinuxCmdWithResultJsonObject(CURL_URL_BASE + " " + lxdCall.command, lxdCall.classType);
		return (T) response.getMetadata();
	}

	/**
	 * NOT YET TESTED!
	 */
	public static <T> List<T> executeCurlGetListCmd(LxdCall lxdCall) throws IOException, InterruptedException, InstantiationException, IllegalAccessException {
		ResponseBase response = LinuxUtil.executeLinuxCmdWithResultJsonObject(CURL_URL_BASE + " " + lxdCall.command + "s", ListResponse.class);
		List<T> results = new ArrayList<T>();
		if (response != null && response.getStatusCode().equals(STATUS_CODE_200)) {
			List<String> stringNames = (List<String>) response.getMetadata();
			for (String stringName : stringNames) {
				int index = stringName.lastIndexOf("/");
				String id = stringName.substring(index);
				T instance = executeCurlGetCmd(lxdCall, id);
				results.add(instance);
			}
		}
		return results;
	}
}
