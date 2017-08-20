package au.com.jcloud.lxd.service.impl;

import org.junit.Assert;
import org.junit.Test;

import au.com.jcloud.lxd.service.impl.LxdApiServiceImpl;

/**
 * Created by david.vittor on 12/07/16.
 */
public class LxdApiServiceImplTest {

	private LxdApiServiceImpl lxdApiService = new LxdApiServiceImpl();
	
	@Test
	public void getBaseUrl() {
		String result = lxdApiService.getBaseUrl(null);
		Assert.assertEquals(lxdApiService.CURL_URL_BASE_LOCAL, result);
		
		result = lxdApiService.getBaseUrl("");
		Assert.assertEquals(lxdApiService.CURL_URL_BASE_LOCAL, result);
		
		result = lxdApiService.getBaseUrl("test");
		Assert.assertEquals("curl -s -k --cert ~/.config/lxc/client.crt --key ~/.config/lxc/client.key https://test:8443", result);
		
		result = lxdApiService.getBaseUrl("10.1.1.5:8080");
		Assert.assertEquals("curl -s -k --cert ~/.config/lxc/client.crt --key ~/.config/lxc/client.key https://10.1.1.5:8080", result);
	}
	
	@Test
	public void getParameterisedUrl() {
		String url = "a${ID}:${ID}";
		String id = "b";
		String result = lxdApiService.getParameterisedUrl(url,id);
		url = url.replaceAll("\\$\\{ID\\}", id);
		Assert.assertEquals("ab:b", result);
	}
	
}

