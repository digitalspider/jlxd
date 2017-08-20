package au.com.jcloud.lxd.service;

import java.io.IOException;
import java.util.Map;

import au.com.jcloud.lxd.enums.LxdCall;
import au.com.jcloud.lxd.enums.RemoteServer;

/**
 * Created by david.vittor on 21/08/17.
 */
public interface ILxdApiService {

	public static final String CURL_URL_BASE_REMOTE = "curl -s -k --cert ${KEYPATH}client.crt --key ${KEYPATH}client.key https://${HOSTANDPORT}";
	public static final String CURL_URL_BASE_LOCAL = "curl -s --unix-socket /var/lib/lxd/unix.socket a";

	// Get commands
	public static final String URL_GET_SERVERINFO = "/1.0";
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
	public static final String URL_POST_CONTAINER_CREATE = URL_GET_CONTAINER + " -X POST -d '{\"name\": \"${ID}\", \"source\": {\"type\": \"image\", \"protocol\": \"${PROTOCOL}\", \"server\": \"${SERVERURL}\", \"alias\": \"${ALIAS}\"}}'";
	public static final String URL_POST_CONTAINER_DELETE = URL_GET_CONTAINER + "/${ID} -X DELETE";
    public static final String URL_POST_FILES = "/1.0/containers/${ID}/files?path=${PATH} -X POST";
    public static final String URL_POST_EXEC = "/1.0/containers/${ID}/exec -X POST -d { \"command\": [\"${CMD}\"], \"environment\": {${ENV}}, \"wait-for-websocket\": ${WAIT}, \"interactive\": false }";

    /**
     * Set the linuxCliService used by this lxdApiService
     */
	void setLinuxCliService(ILinuxCliService linuxCliService);

	/**
	 * Execute the curl command to get a single "LXD Object" e.g. Container, Image, Profile, etc
	 */
	<T> T executeCurlGetCmd(String remoteHostAndPort, LxdCall lxdCall, String id) throws IOException, InterruptedException;

	/**
	 * Execute the curl command to get a single "LXD Object" e.g. Container, Image, Profile, etc
	 */
	<T> T executeCurlGetCmd(String remoteHostAndPort, LxdCall lxdCall, String id, String containerName, String... additionalParams) throws IOException, InterruptedException;

	/**
	 * Execute the base curl command to get a list of "LXD Objects" e.g. Containers, Images, Profiles, etc
	 * 
	 * @param remoteHostAndPort see {@link #getBaseUrl(String)}
	 * @param lxdCall the type of operation to perform
     */
	<T> Map<String,T> executeCurlGetListCmd(String remoteHostAndPort, LxdCall lxdCall) throws IOException, InterruptedException;

	/**
	 * Execute the base curl command to get a list of "LXD Objects" e.g. Containers, Images, Profiles, etc
	 * 
	 * @param remoteHostAndPort see {@link #getBaseUrl(String)}
	 * @param lxdCall the type of operation to perform
	 */
	<T> Map<String,T> executeCurlGetListCmd(String remoteHostAndPort, LxdCall lxdCall, String containerName) throws IOException, InterruptedException;

	/**
	 * Execute the curl command to start, stop, create or delete a container
	 * 
	 * @param remoteHostAndPort see {@link #getBaseUrl(String)}
	 * @param lxdCall the type of operation to perform
	 */
	void executeCurlPostOrPutCmd(String remoteHostAndPort, LxdCall lxdCall, String containerName) throws IOException, InterruptedException;
	
	/**
	 * Execute the curl command to start, stop, create or delete a container
	 * 
	 * @param remoteHostAndPort see {@link #getBaseUrl(String)}
	 * @param lxdCall the type of operation to perform
	 */
	void executeCurlPostCmdToCreateNewContainerFromImage(String remoteHostAndPort, RemoteServer remoteServer, String containerName, String imageAlias) throws IOException, InterruptedException;

	/**
	 * Perform a replace all on the url replacing ${ID}.
	 * 
	 * @param url the url string
	 * @param id the id replacement value
	 * @return the parameterised url
	 */
	String getParameterisedUrl(String url, String id);
	
	/**
	 * The default base url is {@link #CURL_URL_BASE_LOCAL}, but if remoteHostAndPort is 
	 * provided will use the base url {@link #CURL_URL_BASE_REMOTE}
	 * 
	 * @param remoteHostAndPort format should be <host>:<port>, if no port provided will default to 8443
	 * 
	 * @return return the base url for curl calls
	 */
	String getBaseUrl(String remoteHostAndPort);

	String getKeypath();

	void setKeypath(String keypath);
}

