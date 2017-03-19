package au.com.jcloud.lxd.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by david.vittor on 12/07/16.
 */
public class LXDUtilTest {

	@Test
	public void getBaseUrl() {
		String result = LXDUtil.getBaseUrl(null);
		Assert.assertEquals(LXDUtil.CURL_URL_BASE_LOCAL, result);
		
		result = LXDUtil.getBaseUrl("");
		Assert.assertEquals(LXDUtil.CURL_URL_BASE_LOCAL, result);
		
		result = LXDUtil.getBaseUrl("test");
		Assert.assertEquals("curl -s -k --cert ~/.config/lxc/client.crt --key ~/.config/lxc/client.key https://test:8443", result);
		
		result = LXDUtil.getBaseUrl("10.1.1.5:8080");
		Assert.assertEquals("curl -s -k --cert ~/.config/lxc/client.crt --key ~/.config/lxc/client.key https://10.1.1.5:8080", result);
	}
	
	@Test
	public void getParameterisedUrl() {
		String url = "a${ID}:${ID}";
		String id = "b";
		String result = LXDUtil.getParameterisedUrl(url,id);
		url = url.replaceAll("\\$\\{ID\\}", id);
		Assert.assertEquals("ab:b", result);
	}
	
}

