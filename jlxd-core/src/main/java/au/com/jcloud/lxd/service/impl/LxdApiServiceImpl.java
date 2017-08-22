package au.com.jcloud.lxd.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import au.com.jcloud.lxd.bean.LxdServerCredential;
import au.com.jcloud.lxd.enums.LxdCall;
import au.com.jcloud.lxd.enums.RemoteServer;
import au.com.jcloud.lxd.model.StatusCode;
import au.com.jcloud.lxd.model.response.AbstractResponse;
import au.com.jcloud.lxd.model.response.ListOperationResponse;
import au.com.jcloud.lxd.model.response.ListResponse;
import au.com.jcloud.lxd.service.ILinuxCliService;
import au.com.jcloud.lxd.service.ILxdApiService;

/**
 * Created by david.vittor on 12/07/16.
 */
@Named
public class LxdApiServiceImpl implements ILxdApiService {

	public static final Logger LOG = Logger.getLogger(LxdApiServiceImpl.class);

	private ILinuxCliService linuxCliService;
	
	/**
	 * Execute the curl command to get a single "LXD Object" e.g. Container, Image,
	 * Profile, etc
	 */
	@Override
	public <T> T executeCurlGetCmd(LxdServerCredential credential, LxdCall lxdCall, String id)
			throws IOException, InterruptedException {
		return executeCurlGetCmd(credential, lxdCall, id, null);
	}

	/**
	 * Execute the curl command to get a single "LXD Object" e.g. Container, Image,
	 * Profile, etc
	 */
	@Override
	public <T> T executeCurlGetCmd(LxdServerCredential credential, LxdCall lxdCall, String id, String containerName,
			String... additionalParams) throws IOException, InterruptedException {
		String url = getBaseUrl(credential) + lxdCall.getCommand();
		if (containerName != null) {
			url = getParameterisedUrl(url, containerName);
		}
		if (lxdCall.equals(LxdCall.GET_FILE) && additionalParams.length > 0) {
			url = url.replaceAll("\\$\\{PATH\\}", additionalParams[0]);
			return (T) linuxCliService.executeLinuxCmd(url);
		}
		if (lxdCall.equals(LxdCall.GET_STATE)) {
			url = getParameterisedUrl(url, id);
		} else {
			if (StringUtils.isNotBlank(id)) {
				url += "/" + id;
			}
		}
		LOG.debug("url=" + url);
		AbstractResponse response = linuxCliService.executeLinuxCmdWithResultJsonObject(url, lxdCall.getClassType());
		if (response != null) {
			LOG.debug("statusCode=" + response.getStatusCode());
			if (StatusCode.SUCCESS.equals(StatusCode.parse(response.getStatusCode()))) {
				return (T) response.getMetadata();
			}
		}
		return null;
	}

	/**
	 * Execute the base curl command to get a list of "LXD Objects" e.g. Containers,
	 * Images, Profiles, etc
	 * 
	 * @param remoteHostAndPort
	 *            see {@link #getBaseUrl(String, String)}
	 * @param lxdCall
	 *            the type of operation to perform
	 */
	@Override
	public <T> Map<String, T> executeCurlGetListCmd(LxdServerCredential credential, LxdCall lxdCall)
			throws IOException, InterruptedException {
		return executeCurlGetListCmd(credential, lxdCall, null);
	}

	/**
	 * Execute the base curl command to get a list of "LXD Objects" e.g. Containers,
	 * Images, Profiles, etc
	 * 
	 * @param remoteHostAndPort
	 *            see {@link #getBaseUrl(String, String)}
	 * @param keypath the path to the key for the remoteHostAndPort
	 * @param lxdCall
	 *            the type of operation to perform
	 */
	@Override
	public <T> Map<String, T> executeCurlGetListCmd(LxdServerCredential credential, LxdCall lxdCall, String containerName)
			throws IOException, InterruptedException {
		Class responseClassType = ListResponse.class;
		if (lxdCall.equals(LxdCall.GET_OPERATION)) {
			responseClassType = ListOperationResponse.class;
		}
		String url = getBaseUrl(credential) + lxdCall.getCommand();
		url = getParameterisedUrl(url, containerName);
		LOG.debug("url=" + url);
		AbstractResponse response = (AbstractResponse) linuxCliService.executeLinuxCmdWithResultJsonObject(url, responseClassType);
		Map<String, T> results = new HashMap<String, T>();
		if (response != null) {
			LOG.debug("statusCode=" + response.getStatusCode());
			if (StatusCode.SUCCESS.equals(StatusCode.parse(response.getStatusCode()))) {
				List<String> stringNames = new ArrayList<String>();
				if (lxdCall.equals(LxdCall.GET_OPERATION)) {
					Map<String, List<String>> responses = (Map<String, List<String>>) response.getMetadata();
					// TODO: Differentiate success, running and failure
					for (List<String> values : responses.values()) {
						stringNames.addAll(values);
					}
				} else {
					stringNames = (List<String>) response.getMetadata();
					if (stringNames == null) {
						stringNames = new ArrayList<String>();
					}
				}
				for (String stringName : stringNames) {
					int index = stringName.lastIndexOf("/");
					String id = stringName.substring(index + 1);
					T instance = executeCurlGetCmd(credential, lxdCall, id, containerName);
					if (instance != null) {
						results.put(id, instance);
					}
				}
			} else {
				LOG.warn("Invalid Response! response=" + response + " url=" + url);
			}
		}
		return results;
	}

	/**
	 * Execute the curl command to start, stop, create or delete a container
	 * 
	 * @param remoteHostAndPort
	 *            see {@link #getBaseUrl(String)}
	 * @param lxdCall
	 *            the type of operation to perform
	 */
	@Override
	public void executeCurlPostOrPutCmd(LxdServerCredential credential, LxdCall lxdCall, String containerName)
			throws IOException, InterruptedException {
		String url = getBaseUrl(credential) + lxdCall.getCommand();

		if (lxdCall.equals(LxdCall.PUT_STATE_START) || lxdCall.equals(LxdCall.PUT_STATE_STOP)
				|| lxdCall.equals(LxdCall.POST_CONTAINER_CREATE) || lxdCall.equals(LxdCall.POST_CONTAINER_DELETE)) {
			url = getParameterisedUrl(url, containerName);
		} else {
			throw new IOException("This call is not implemented! " + lxdCall);
		}
		LOG.debug("url=" + url);
		AbstractResponse response = linuxCliService.executeLinuxCmdWithResultJsonObject(url, lxdCall.getClassType());
		LOG.info("repsonse=" + response);
		if (response != null) {
			LOG.debug("statusCode=" + response.getStatusCode());
			if (StatusCode.OPERATION_CREATED.equals(StatusCode.parse(response.getStatusCode()))) {
				return;
			}
		}
	}

	/**
	 * Execute the curl command to start, stop, create or delete a container
	 * 
	 * @param remoteHostAndPort
	 *            see {@link #getBaseUrl(String)}
	 * @param lxdCall
	 *            the type of operation to perform
	 */
	@Override
	public void executeCurlPostCmdToCreateNewContainerFromImage(LxdServerCredential credential, RemoteServer remoteServer,
			String containerName, String imageAlias) throws IOException, InterruptedException {
		if (remoteServer == null) {
			throw new IOException("Cannot create a container without a remoteServer.");
		}
		LxdCall lxdCall = LxdCall.POST_CONTAINER_CREATE;
		String url = getBaseUrl(credential) + lxdCall.getCommand();
		url = getParameterisedUrl(url, containerName);
		url = url.replaceAll("\\$\\{ALIAS\\}", imageAlias);
		url = url.replaceAll("\\$\\{PROTOCOL\\}", remoteServer.getProtocol());
		url = url.replaceAll("\\$\\{SERVERURL\\}", remoteServer.getUrl());

		LOG.debug("url=" + url);
		AbstractResponse response = linuxCliService.executeLinuxCmdWithResultJsonObject(url, lxdCall.getClassType());
		LOG.info("repsonse=" + response);
		if (response != null) {
			LOG.debug("statusCode=" + response.getStatusCode());
			if (StatusCode.OPERATION_CREATED.equals(StatusCode.parse(response.getStatusCode()))) {
				return;
			}
		}
	}

	/**
	 * Perform a replace all on the url replacing ${ID}.
	 * 
	 * @param url
	 *            the url string
	 * @param id
	 *            the id replacement value
	 * @return the parameterised url
	 */
	@Override
	public String getParameterisedUrl(String url, String id) {
		url = url.replaceAll("\\$\\{ID\\}", id);
		return url;
	}

	/**
	 * The default base url is {@link #CURL_URL_BASE_LOCAL}, but if
	 * remoteHostAndPort is provided will use the base url
	 * {@link #CURL_URL_BASE_REMOTE}
	 * 
	 * @param remoteHostAndPort
	 *            format should be <host>:<port>, if no port provided will default
	 *            to 8443
	 * @param keypath format "~/path/cert.crt|~/path/cert.key", pipe separated certificate and key to be used for the remoteHostAndPort
	 * 
	 * @return return the base url for curl calls
	 */
	@Override
	public String getBaseUrl(LxdServerCredential credential) {
		String url = CURL_URL_BASE_LOCAL;
		if (credential != null && StringUtils.isNotBlank(credential.getRemoteHostAndPort())) {
			String remoteHostAndPort = credential.getRemoteHostAndPort();
			if (!remoteHostAndPort.contains(":")) {
				remoteHostAndPort += ":8443";
			}
			url = CURL_URL_BASE_REMOTE.replaceAll("\\$\\{HOSTANDPORT}", remoteHostAndPort);
			if (StringUtils.isNotBlank(credential.getRemoteCert())) {
				url = url.replaceAll("\\$\\{KEYPATHCERT}", "--cert "+credential.getRemoteCert());
			} else {
				url = url.replaceAll("\\$\\{KEYPATHCERT}", "");
			}
			if (StringUtils.isNotBlank(credential.getRemoteKey())) {
				url = url.replaceAll("\\$\\{KEYPATHKEY}", "--key "+credential.getRemoteKey());
			} else {
				url = url.replaceAll("\\$\\{KEYPATHKEY}", "");
			}
			
		}
		return url;
	}

	@Override
	@Inject
	public void setLinuxCliService(ILinuxCliService linuxCliService) {
		this.linuxCliService = linuxCliService;
	}
}
