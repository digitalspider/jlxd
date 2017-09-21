package au.com.jcloud.lxd.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FormatUtils {

	public static String convertIntegerToMB(Integer input) {
		return String.valueOf(new BigDecimal(Double.valueOf(input) / 1024 / 1024).setScale(2, RoundingMode.HALF_UP));
	}
	
	public static String convertIntegerToKB(Integer input) {
		return String.valueOf(new BigDecimal(Double.valueOf(input) / 1024).setScale(2, RoundingMode.HALF_UP));
	}

}
