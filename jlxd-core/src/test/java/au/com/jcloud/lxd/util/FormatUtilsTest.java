package au.com.jcloud.lxd.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FormatUtilsTest {

	@Test
	public void convertIntegerToMB_shouldHandle0() {
		assertEquals("0.00",FormatUtils.convertIntegerToMB(0));
	}
	
	@Test
	public void convertIntegerToMB_shouldHandleSmallIntegers() {
		assertEquals("0.00",FormatUtils.convertIntegerToMB(1024));
		assertEquals("0.98",FormatUtils.convertIntegerToMB(1024000));
	}
	
	@Test
	public void convertIntegerToMB_shouldHandleLargeInteger() {
		assertEquals("8.78",FormatUtils.convertIntegerToMB(9207808));
		assertEquals("9.11",FormatUtils.convertIntegerToMB(9555968));
	}
	
	@Test
	public void convertIntegerToMB_shouldHandleRoundUp() {
		assertEquals("0.00",FormatUtils.convertIntegerToMB(5*1024));
		assertEquals("0.01",FormatUtils.convertIntegerToMB(6*1024));
	}
	
	@Test
	public void convertIntegerToKB_shouldHandle0() {
		assertEquals("0.00",FormatUtils.convertIntegerToKB(0));
	}
	
	@Test
	public void convertIntegerToKB_shouldHandleSmallIntegers() {
		assertEquals("0.25",FormatUtils.convertIntegerToKB(256));
		assertEquals("0.50",FormatUtils.convertIntegerToKB(513));
	}
	
	@Test
	public void convertIntegerToKB_shouldHandleLargeInteger() {
		assertEquals("90.63",FormatUtils.convertIntegerToKB(92808));
		assertEquals("9332.00",FormatUtils.convertIntegerToKB(9555968));
	}
	
	@Test
	public void convertIntegerToKB_shouldHandleRoundUp() {
		assertEquals("0.00",FormatUtils.convertIntegerToKB(5));
		assertEquals("0.01",FormatUtils.convertIntegerToKB(6));
	}
}
