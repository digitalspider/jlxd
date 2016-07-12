package au.com.javacloud.lxd.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import au.com.javacloud.lxd.model.response.ContainerResponse;
import au.com.javacloud.lxd.model.response.ImageResponse;
import au.com.javacloud.lxd.model.response.ListResponse;
import au.com.javacloud.lxd.model.response.ResponseBase;

public class LXDUtil {

	public static final String CURL_URL_BASE = "curl -s --unix-socket /var/lib/lxd/unix.socket";

	public static final Logger LOG = Logger.getLogger(LXDUtil.class);

	public static final String URL_CONTAINER_GET = "a/1.0/containers";
	public static final String URL_IMAGE_GET = "a/1.0/images";
	public static final String URL_CERTIFICATE_GET = "a/1.0/certificates";
	public static final String URL_NETWORK_GET = "a/1.0/networks";
	public static final String URL_OPERATION_GET = "a/1.0/operations";
	public static final String URL_PROFILE_GET = "a/1.0/profiles";
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
	 * Execute the curl command to get a single "LXD Object" e.g. Container, Image, Profile, etc
	 */
	public static <T> T executeCurlGetCmd(LxdCall lxdCall, String id) throws IOException, InterruptedException {
		ResponseBase response = LinuxUtil.executeLinuxCmdWithResultJsonObject(CURL_URL_BASE + " " + lxdCall.command + "/" + id, lxdCall.classType);
		if (response != null) {
			LOG.debug("statusCode=" + response.getStatusCode());
			if (STATUS_CODE_200.equals(response.getStatusCode())) {
				return (T) response.getMetadata();
			}
		}
		return null;
	}

	/**
	 * Execute the base curl command to get a list of "LXD Objects" e.g. Containers, Images, Profiles, etc
	 */
	public static <T> List<T> executeCurlGetListCmd(LxdCall lxdCall) throws IOException, InterruptedException {
		ResponseBase response = LinuxUtil.executeLinuxCmdWithResultJsonObject(CURL_URL_BASE + " " + lxdCall.command, ListResponse.class);
		List<T> results = new ArrayList<T>();
		if (response != null) {
			LOG.debug("statusCode=" + response.getStatusCode());
			if (STATUS_CODE_200.equals(response.getStatusCode())) {
				List<String> stringNames = (List<String>) response.getMetadata();
				for (String stringName : stringNames) {
					int index = stringName.lastIndexOf("/");
					String id = stringName.substring(index + 1);
					T instance = executeCurlGetCmd(lxdCall, id);
					if (instance != null) {
						results.add(instance);
					}
				}
			}
		}
		return results;
	}
}

