package au.com.jcloud.lxd.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import au.com.jcloud.lxd.model.response.ContainerResponse;
import au.com.jcloud.lxd.model.response.ListOperationResponse;
import au.com.jcloud.lxd.model.response.NetworkResponse;
import au.com.jcloud.lxd.model.response.ProfileResponse;
import au.com.jcloud.lxd.model.response.StateResponse;
import au.com.jcloud.lxd.model.response.ImageResponse;
import au.com.jcloud.lxd.model.response.ListResponse;
import au.com.jcloud.lxd.model.response.OperationResponse;
import au.com.jcloud.lxd.model.response.AbstractResponse;

/**
 * Created by david.vittor on 12/07/16.
 */
public class LXDUtil {

	public static final String CURL_URL_BASE = "curl -s --unix-socket /var/lib/lxd/unix.socket";

	public static final Logger LOG = Logger.getLogger(LXDUtil.class);

	// Get commands
	public static final String URL_GET_CONTAINER = "a/1.0/containers";
	public static final String URL_GET_IMAGE = "a/1.0/images";
	public static final String URL_GET_CERTIFICATE = "a/1.0/certificates";
	public static final String URL_GET_NETWORK = "a/1.0/networks";
	public static final String URL_GET_OPERATION = "a/1.0/operations";
	public static final String URL_GET_PROFILE = "a/1.0/profiles";
	public static final String URL_GET_STATE = "a/1.0/containers/${ID}/state";

	// Post Commands
	public static final String URL_PUT_STATE_STOP = URL_GET_STATE + " -X PUT -d '{\"action\": \"stop\", \"force\": true}'";
	public static final String URL_POST_STATE_START = URL_GET_STATE + " -X PUT -d '{\"action\": \"start\"}'";
	public static final String URL_POST_CONTAINER_CREATE = URL_GET_CONTAINER + " -X POST -d '{\"name\": \"${ID}\", \"source\": {\"type\": \"image\", \"protocol\": \"simplestreams\", \"server\": \"https://cloud-images.ubuntu.com/daily\", \"alias\": \"16.04\"}}'";
	public static final String URL_POST_CONTAINER_DELETE = URL_GET_CONTAINER + "/${ID} -X DELETE";

	public static final String STATUS_CODE_SUCCESS = "200";
	public static final String STATUS_CODE_ERROR = "400";
	public static final String STATUS_CODE_100 = "100";

	public enum LxdCall {
		GET_CONTAINER(URL_GET_CONTAINER, ContainerResponse.class),
		GET_IMAGE(URL_GET_IMAGE, ImageResponse.class),
		GET_CERTIFICATE(URL_GET_CERTIFICATE, AbstractResponse.class),
		GET_NETWORK(URL_GET_NETWORK, NetworkResponse.class),
		GET_OPERATION(URL_GET_OPERATION, OperationResponse.class),
		GET_PROFILE(URL_GET_PROFILE, ProfileResponse.class),
		GET_STATE(URL_GET_STATE, StateResponse.class),
		PUT_STATE_START(URL_POST_STATE_START, OperationResponse.class),
		PUT_STATE_STOP(URL_PUT_STATE_STOP, OperationResponse.class),
		POST_CONTAINER_CREATE(URL_POST_CONTAINER_CREATE, OperationResponse.class),
		POST_CONTAINER_DELETE(URL_POST_CONTAINER_DELETE, OperationResponse.class);

		LxdCall(String command, Class<? extends AbstractResponse> classType) {
			this.command = command;
			this.classType = classType;
		}

		private String command;
		private Class<? extends AbstractResponse> classType;
	}

	/**
	 * Execute the curl command to get a single "LXD Object" e.g. Container, Image, Profile, etc
	 */
	public static <T> T executeCurlGetCmd(LxdCall lxdCall, String id) throws IOException, InterruptedException {
		String url = CURL_URL_BASE + " " + lxdCall.command;
		if (lxdCall.equals(LxdCall.GET_STATE)) {
			url = getParameterisedUrl(url, id, null);
		} else {
			url += "/"+id;
		}
		AbstractResponse response = LinuxUtil.executeLinuxCmdWithResultJsonObject(url, lxdCall.classType);
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
	public static <T> Map<String,T> executeCurlGetListCmd(LxdCall lxdCall) throws IOException, InterruptedException {
		Class responseClassType = ListResponse.class;
		if (lxdCall.equals(LxdCall.GET_OPERATION)) {
			responseClassType = ListOperationResponse.class;
		}
		AbstractResponse response = (AbstractResponse) LinuxUtil.executeLinuxCmdWithResultJsonObject(CURL_URL_BASE + " " + lxdCall.command, responseClassType);
		Map<String,T> results = new HashMap<String,T>();
		if (response != null) {
			LOG.debug("statusCode=" + response.getStatusCode());
			if (STATUS_CODE_SUCCESS.equals(response.getStatusCode())) {
				List<String> stringNames = new ArrayList<String>();
				if (lxdCall.equals(LxdCall.GET_OPERATION)) {
					Map<String,List<String>> responses = (Map<String, List<String>>) response.getMetadata();
					// TODO: Differentiate success, running and failure
					for (List<String> values : responses.values()) {
						stringNames.addAll(values);
					}
				} else {
					stringNames = (List<String>) response.getMetadata();
				}
				for (String stringName : stringNames) {
					int index = stringName.lastIndexOf("/");
					String id = stringName.substring(index + 1);
					T instance = executeCurlGetCmd(lxdCall, id);
					if (instance != null) {
						results.put(id,instance);
					}
				}
			}
		}
		return results;
	}

	/**
	 * Execute the curl command to start, stop, create or delete a container
	 */
	public static void executeCurlPostOrPutCmd(LxdCall lxdCall, String containerName) throws IOException, InterruptedException {
		executeCurlPostOrPutCmd(lxdCall, containerName, null);
	}

	/**
	 * Execute the curl command to start, stop, create or delete a container
	 */
	public static void executeCurlPostOrPutCmd(LxdCall lxdCall, String containerName, String imageNameOrId) throws IOException, InterruptedException {
		String url = CURL_URL_BASE + " " + lxdCall.command;
		if (lxdCall.equals(LxdCall.PUT_STATE_START) || lxdCall.equals(LxdCall.PUT_STATE_STOP) ||
				lxdCall.equals(LxdCall.POST_CONTAINER_CREATE) || lxdCall.equals(LxdCall.POST_CONTAINER_DELETE)) {
			url = getParameterisedUrl(url, containerName, imageNameOrId);
		} else {
			throw new IOException("This call is not implemented! "+lxdCall);
		}
		LOG.info("url="+url);
		AbstractResponse response = LinuxUtil.executeLinuxCmdWithResultJsonObject(url, lxdCall.classType);
		LOG.info("repsonse="+response);
		if (response != null) {
			LOG.debug("statusCode=" + response.getStatusCode());
			if (STATUS_CODE_100.equals(response.getStatusCode())) {
				return;
			}
		}
	}

	private static String getParameterisedUrl(String url, String id, String alias) {
		url = url.replaceAll("\\$\\{ID\\}", id);
		url = url.replaceAll("\\$\\{ALIAS\\}", alias);
		return url;
	}
}

