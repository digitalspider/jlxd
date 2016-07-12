package au.com.javacloud.lxd.util;

import java.io.IOException;

import au.com.javacloud.lxd.model.Container;
import au.com.javacloud.lxd.model.Image;
import au.com.javacloud.lxd.model.Model;
import au.com.javacloud.lxd.model.ResponseBase;
import au.com.javacloud.lxd.model.extra.NetworkInterface;

public class LXDUtil {

	public static final String CURL_URL_BASE = "curl -s --unix-socket /var/lib/lxd/unix.socket";

	public static final String URL_CONTAINERS_GET = "a/1.0/containers";
	public static final String URL_CONTAINER_GET = "a/1.0/container";
	public static final String URL_IMAGES_GET = "a/1.0/images";
	public static final String URL_IMAGE_GET = "a/1.0/image";
	public static final String URL_CERTIFICATES_GET = "a/1.0/certificates";
	public static final String URL_CERTIFICATE_GET = "a/1.0/certificate";
	public static final String URL_EVENTS_GET = "a/1.0/events";
	public static final String URL_NETWORKS_GET = "a/1.0/networks";
	public static final String URL_NETWORK_GET = "a/1.0/network";
	public static final String URL_OPERATIONS_GET = "a/1.0/operations";
	public static final String URL_OPERATION_GET = "a/1.0/operation";
	public static final String URL_PROFILES_GET = "a/1.0/profiles";
	public static final String URL_PROFILE_GET = "a/1.0/profile";
	public static final String STATUS_CODE_200 = "200";

	public enum LxdCall {
		CONTAINERS_GET(URL_CONTAINERS_GET, Container.class), CONTAINER_GET(URL_CONTAINER_GET, Container[].class), IMAGES_GET(URL_IMAGES_GET, Image[].class), IMAGE_GET(URL_IMAGE_GET, Image.class), CERTIFICATES_GET(URL_CERTIFICATES_GET, Object[].class), CERTIFICATE_GET(URL_CERTIFICATE_GET,
				Object.class), EVENTS_GET(URL_EVENTS_GET, Object[].class), NETWORKS_GET(URL_NETWORKS_GET, NetworkInterface[].class), NETWORK_GET(URL_NETWORK_GET, NetworkInterface.class), OPERATIONS_GET(URL_OPERATIONS_GET, Object[].class), OPERATION_GET(URL_OPERATION_GET,
						Object.class), PROFILES_GET(URL_PROFILES_GET, Object[].class), PROFILE_GET(URL_PROFILE_GET, Object.class);

		LxdCall(String command, Class classType) {
			this.command = command;
			this.classType = classType;
		}

		private String command;
		private Class classType;
	}

	/**
	 * NOT YET TESTED!
	 */
	public static <T extends Model> T executeCurlCmd(LxdCall lxdCall) throws IOException, InterruptedException, InstantiationException, IllegalAccessException {
		ResponseBase response = LinuxUtil.executeLinuxCmdWithResultJsonObject(CURL_URL_BASE + " " + lxdCall.command, ResponseBase.class);
		if (response.getStatusCode().equals(STATUS_CODE_200)) {
			Class<T> classType = lxdCall.classType;
			T object = classType.newInstance();
			object.load(response.getMetadata());
			return object;
		}
		return null;
	}
}
