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
	public static final String URL_STATE_GET = "a/1.0/containers/${ID}/state";
	public static final String STATUS_CODE_SUCCESS = "200";
	public static final String STATUS_CODE_ERROR = "400";
	public static final String STATUS_CODE_100 = "100";
	public static final String STATUS_CODE_RUNNING = "103";
	public static final String STATUS_CODE_STOPPED = "102";

	public enum LxdCall {
		CONTAINER_GET(URL_CONTAINER_GET, ContainerResponse.class),
		IMAGE_GET(URL_IMAGE_GET, ImageResponse.class),
		CERTIFICATE_GET(URL_CERTIFICATE_GET, ResponseBase.class),
		NETWORK_GET(URL_NETWORK_GET, ResponseBase.class),
		OPERATION_GET(URL_OPERATION_GET, ResponseBase.class),
		PROFILE_GET(URL_PROFILE_GET, ResponseBase.class),
		STATE_GET(URL_STATE_GET, ResponseBase.class),
		STATE_PUT_START(URL_STATE_GET, ResponseBase.class),
		STATE_PUT_STOP(URL_STATE_GET, ResponseBase.class);

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
		String url = CURL_URL_BASE + " " + lxdCall.command;
		if (lxdCall.equals(LxdCall.STATE_GET)) {
			url = url.replaceAll("\\$\\{ID\\}",id);
		} else {
			url += "/"+id;
		}
		ResponseBase response = LinuxUtil.executeLinuxCmdWithResultJsonObject(url, lxdCall.classType);
		if (response != null) {
			LOG.debug("statusCode=" + response.getStatusCode());
			if (STATUS_CODE_SUCCESS.equals(response.getStatusCode())) {
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
			if (STATUS_CODE_SUCCESS.equals(response.getStatusCode())) {
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

	/**
	 * Execute the curl command to start or stop a container
	 */
	public static void executeCurlPutCmd(LxdCall lxdCall, String id) throws IOException, InterruptedException {
		String url = CURL_URL_BASE + " " + lxdCall.command;
		if (lxdCall.equals(LxdCall.STATE_PUT_START)) {
			url = url.replaceAll("\\$\\{ID\\}", id);
			url += " -X PUT -d '{\"action\": \"stop\", \"force\": true}'";
		} else if (lxdCall.equals(LxdCall.STATE_PUT_STOP)) {
			url = url.replaceAll("\\$\\{ID\\}",id);
			url += " -X PUT -d '{\"action\": \"start\"}'";
		} else {
			throw new IOException("This call is not implemented! "+lxdCall);
		}
		LOG.info("url="+url);
		ResponseBase response = LinuxUtil.executeLinuxCmdWithResultJsonObject(url, lxdCall.classType);
		LOG.info("repsonse="+response);
		if (response != null) {
			LOG.debug("statusCode=" + response.getStatusCode());
			if (STATUS_CODE_100.equals(response.getStatusCode())) {
				return;
			}
		}
	}
}

