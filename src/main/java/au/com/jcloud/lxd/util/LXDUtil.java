package au.com.jcloud.lxd.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import au.com.jcloud.lxd.model.StatusCode;
import au.com.jcloud.lxd.model.response.AbstractResponse;
import au.com.jcloud.lxd.model.response.CertificateResponse;
import au.com.jcloud.lxd.model.response.ContainerResponse;
import au.com.jcloud.lxd.model.response.FileResponse;
import au.com.jcloud.lxd.model.response.ImageAliasResponse;
import au.com.jcloud.lxd.model.response.ImageResponse;
import au.com.jcloud.lxd.model.response.ListOperationResponse;
import au.com.jcloud.lxd.model.response.ListResponse;
import au.com.jcloud.lxd.model.response.NetworkResponse;
import au.com.jcloud.lxd.model.response.OperationResponse;
import au.com.jcloud.lxd.model.response.ProfileResponse;
import au.com.jcloud.lxd.model.response.SnapshotResponse;
import au.com.jcloud.lxd.model.response.StateResponse;

/**
 * Created by david.vittor on 12/07/16.
 */
public class LXDUtil {

	public static final String CURL_URL_BASE_REMOTE = "curl -s -k --cert ~/.config/lxc/client.crt --key ~/.config/lxc/client.key https://${HOSTANDPORT}";
	public static final String CURL_URL_BASE_LOCAL = "curl -s --unix-socket /var/lib/lxd/unix.socket a";

	public static final Logger LOG = Logger.getLogger(LXDUtil.class);

	// Get commands
	public static final String URL_GET_CONTAINER = "/1.0/containers";
	public static final String URL_GET_IMAGE = "/1.0/images";
    public static final String URL_GET_IMAGEALIAS = URL_GET_IMAGE + "/aliases";
	public static final String URL_GET_CERTIFICATE = "/1.0/certificates";
	public static final String URL_GET_NETWORK = "/1.0/networks";
	public static final String URL_GET_OPERATION = "/1.0/operations";
	public static final String URL_GET_PROFILE = "/1.0/profiles";
	public static final String URL_GET_STATE = "/1.0/containers/${ID}/state";
    public static final String URL_GET_LOGS = "/1.0/containers/${ID}/logs";
    public static final String URL_GET_SNAPSHOTS = "/1.0/containers/${ID}/snapshots";
    public static final String URL_GET_FILE = "/1.0/containers/${ID}/files?path=${PATH}";
 

	// Post Commands
	public static final String URL_PUT_STATE_STOP = URL_GET_STATE + " -X PUT -d '{\"action\": \"stop\", \"force\": true}'";
	public static final String URL_PUT_STATE_START = URL_GET_STATE + " -X PUT -d '{\"action\": \"start\"}'";
	public static final String URL_POST_CONTAINER_CREATE = URL_GET_CONTAINER + " -X POST -d '{\"name\": \"${ID}\", \"source\": {\"type\": \"image\", \"protocol\": \"simplestreams\", \"server\": \"https://cloud-images.ubuntu.com/daily\", \"alias\": \"16.04\"}}'";
	public static final String URL_POST_CONTAINER_DELETE = URL_GET_CONTAINER + "/${ID} -X DELETE";
    public static final String URL_POST_FILES = "/1.0/containers/${ID}/files?path=${PATH} -X POST";
    public static final String URL_POST_EXEC = "/1.0/containers/${ID}/exec -X POST -d { \"command\": [\"${CMD}\"], \"environment\": {${ENV}}, \"wait-for-websocket\": ${WAIT}, \"interactive\": false }";

	public enum LxdCall {
		GET_CONTAINER(URL_GET_CONTAINER, ContainerResponse.class),
		GET_IMAGE(URL_GET_IMAGE, ImageResponse.class),
        GET_IMAGEALIAS(URL_GET_IMAGEALIAS, ImageAliasResponse.class),
		GET_CERTIFICATE(URL_GET_CERTIFICATE, CertificateResponse.class),
		GET_NETWORK(URL_GET_NETWORK, NetworkResponse.class),
		GET_OPERATION(URL_GET_OPERATION, OperationResponse.class),
		GET_PROFILE(URL_GET_PROFILE, ProfileResponse.class),
		GET_STATE(URL_GET_STATE, StateResponse.class),
        GET_SNAPSHOTS(URL_GET_SNAPSHOTS, SnapshotResponse.class),
        GET_FILE(URL_GET_FILE, FileResponse.class),
		PUT_STATE_START(URL_PUT_STATE_START, OperationResponse.class),
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
	public static <T> T executeCurlGetCmd(String remoteHostAndPort, LxdCall lxdCall, String id) throws IOException, InterruptedException {
		return executeCurlGetCmd(remoteHostAndPort, lxdCall, id, null);
	}

	/**
	 * Execute the curl command to get a single "LXD Object" e.g. Container, Image, Profile, etc
	 */
	public static <T> T executeCurlGetCmd(String remoteHostAndPort, LxdCall lxdCall, String id, String containerName, String... additionalParams) throws IOException, InterruptedException {
		String url = getBaseUrl(remoteHostAndPort) + lxdCall.command;
		if (containerName!=null) {
			url = getParameterisedUrl(url, containerName, null);
		}
		if (lxdCall.equals(LxdCall.GET_FILE) && additionalParams.length>0) {
			url = url.replaceAll("\\$\\{PATH\\}", additionalParams[0]);
			return (T) LinuxUtil.executeLinuxCmd(url);
		}
		if (lxdCall.equals(LxdCall.GET_STATE)) {
			url = getParameterisedUrl(url, id, null);
		} else {
			url += "/"+id;
		}
		LOG.debug("url=" + url);
		AbstractResponse response = LinuxUtil.executeLinuxCmdWithResultJsonObject(url, lxdCall.classType);
		if (response != null) {
			LOG.debug("statusCode=" + response.getStatusCode());
			if (StatusCode.SUCCESS.equals(StatusCode.parse(response.getStatusCode()))) {
				return (T) response.getMetadata(); 
			}
		}
		return null;
	}

	/**
	 * Execute the base curl command to get a list of "LXD Objects" e.g. Containers, Images, Profiles, etc
	 * 
	 * @param remoteHostAndPort see {@link #getBaseUrl(String)}
	 * @param lxdCall the type of operation to perform
     */
	public static <T> Map<String,T> executeCurlGetListCmd(String remoteHostAndPort, LxdCall lxdCall) throws IOException, InterruptedException {
		return executeCurlGetListCmd(remoteHostAndPort, lxdCall, null);
	}

	/**
	 * Execute the base curl command to get a list of "LXD Objects" e.g. Containers, Images, Profiles, etc
	 * 
	 * @param remoteHostAndPort see {@link #getBaseUrl(String)}
	 * @param lxdCall the type of operation to perform
	 */
	public static <T> Map<String,T> executeCurlGetListCmd(String remoteHostAndPort, LxdCall lxdCall, String containerName) throws IOException, InterruptedException {
		Class responseClassType = ListResponse.class;
		if (lxdCall.equals(LxdCall.GET_OPERATION)) {
			responseClassType = ListOperationResponse.class;
		}
		String url = getBaseUrl(remoteHostAndPort) + lxdCall.command;
		url = getParameterisedUrl(url, containerName, null);
		LOG.debug("url=" + url);
		AbstractResponse response = (AbstractResponse) LinuxUtil.executeLinuxCmdWithResultJsonObject(url, responseClassType);
		Map<String,T> results = new HashMap<String,T>();
		if (response != null) {
			LOG.debug("statusCode=" + response.getStatusCode());
            if (StatusCode.SUCCESS.equals(StatusCode.parse(response.getStatusCode()))) {
				List<String> stringNames = new ArrayList<String>();
				if (lxdCall.equals(LxdCall.GET_OPERATION)) {
					Map<String,List<String>> responses = (Map<String, List<String>>) response.getMetadata();
					// TODO: Differentiate success, running and failure
					for (List<String> values : responses.values()) {
						stringNames.addAll(values);
					}
				} else {
					stringNames = (List<String>) response.getMetadata();
					if (stringNames==null) {
						stringNames = new ArrayList<String>();
					}
				}
				for (String stringName : stringNames) {
					int index = stringName.lastIndexOf("/");
					String id = stringName.substring(index + 1);
					T instance = executeCurlGetCmd(remoteHostAndPort, lxdCall, id, containerName);
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
	 * 
	 * @param remoteHostAndPort see {@link #getBaseUrl(String)}
	 * @param lxdCall the type of operation to perform
	 */
	public static void executeCurlPostOrPutCmd(String remoteHostAndPort, LxdCall lxdCall, String containerName) throws IOException, InterruptedException {
		executeCurlPostOrPutCmd(remoteHostAndPort, lxdCall, containerName, null);
	}

	/**
	 * Execute the curl command to start, stop, create or delete a container
	 * 
	 * @param remoteHostAndPort see {@link #getBaseUrl(String)}
	 * @param lxdCall the type of operation to perform
	 */
	public static void executeCurlPostOrPutCmd(String remoteHostAndPort, LxdCall lxdCall, String containerName, String imageNameOrId) throws IOException, InterruptedException {
		String url = getBaseUrl(remoteHostAndPort) + lxdCall.command;

		if (lxdCall.equals(LxdCall.PUT_STATE_START) || lxdCall.equals(LxdCall.PUT_STATE_STOP) ||
				lxdCall.equals(LxdCall.POST_CONTAINER_CREATE) || lxdCall.equals(LxdCall.POST_CONTAINER_DELETE)) {
			url = getParameterisedUrl(url, containerName, imageNameOrId);
		} else {
			throw new IOException("This call is not implemented! "+lxdCall);
		}
		LOG.debug("url="+url);
		AbstractResponse response = LinuxUtil.executeLinuxCmdWithResultJsonObject(url, lxdCall.classType);
		LOG.info("repsonse="+response);
		if (response != null) {
			LOG.debug("statusCode=" + response.getStatusCode());
            if (StatusCode.OPERATION_CREATED.equals(StatusCode.parse(response.getStatusCode()))) {
				return;
			}
		}
	}

	/**
	 * Perform a replace all on the url replacing ${ID} and ${ALIAS}.
	 * 
	 * @param url the url string
	 * @param id the id replacement value
	 * @param alias the alias replacement value
	 * @return the parameterised url
	 */
	static String getParameterisedUrl(String url, String id, String alias) {
		url = url.replaceAll("\\$\\{ID\\}", id);
		url = url.replaceAll("\\$\\{ALIAS\\}", alias);
		return url;
	}
	
	/**
	 * The default base url is {@link #CURL_URL_BASE_LOCAL}, but if remoteHostAndPort is 
	 * provided will use the base url {@link #CURL_URL_BASE_REMOTE}
	 * 
	 * @param remoteHostAndPort format should be <host>:<port>, if no port provided will default to 8443
	 * 
	 * @return return the base url for curl calls
	 */
	static String getBaseUrl(String remoteHostAndPort) {
		String url = CURL_URL_BASE_LOCAL;
		if (StringUtils.isNotBlank(remoteHostAndPort)) {
			if (!remoteHostAndPort.contains(":")) {
				remoteHostAndPort += ":8443";
			}
			url = CURL_URL_BASE_REMOTE.replaceAll("\\$\\{HOSTANDPORT}", remoteHostAndPort);
		}
		return url;
	}
}

