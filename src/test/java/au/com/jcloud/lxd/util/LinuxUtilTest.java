package au.com.jcloud.lxd.util;

import org.junit.Assert;
import org.junit.Test;

public class LinuxUtilTest {

	@Test
	public void getFileNameWithoutExtension() {
		String result = LinuxUtil.getFileNameWithoutExtension("test.two");
		Assert.assertEquals("test", result);
		
		result = LinuxUtil.getFileNameWithoutExtension("test");
		Assert.assertEquals("test", result);
	}
	
	@Test
	public void getFileExtension() {
		String result = LinuxUtil.getFileExtension("test.two");
		Assert.assertEquals("two", result);
		
		result = LinuxUtil.getFileExtension("try.test.two");
		Assert.assertEquals("two", result);
	}
	
	@Test
	public void firstCharUpperCase() {
		String result = LinuxUtil.firstCharUpperCase("testTwo");
		Assert.assertEquals("TestTwo", result);
		
		result = LinuxUtil.firstCharUpperCase("TestTwo");
		Assert.assertEquals("TestTwo", result);
	}
	

}
