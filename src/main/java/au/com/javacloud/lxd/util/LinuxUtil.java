package au.com.javacloud.lxd.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

/**
 * Created by david.vittor on 12/07/16.
 */
public class LinuxUtil {
	private static final Logger LOG = Logger.getLogger(LinuxUtil.class);

	public static String executeLinuxCmd(String cmd) throws IOException, InterruptedException {
		String[] cmdArray = { "/bin/sh", "-c", cmd };
		StringBuffer result = new StringBuffer();
		BufferedReader in = null;
		LOG.debug("cmd=" + cmdArray[2]);
		try {
			Process process = Runtime.getRuntime().exec(cmdArray);
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;

			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			process.waitFor();
		} finally {
			if (in != null) {
				in.close();
			}
		}
		LOG.debug("result=" + result);
		return result.toString();
	}

	public static List<String> executeLinuxCmdWithResultLines(String cmd) throws IOException, InterruptedException {
		String[] cmdArray = { "/bin/sh", "-c", cmd };
		LOG.debug("cmd=" + cmdArray[2]);
		List<String> result = new ArrayList<String>();
		BufferedReader in = null;
		LOG.debug("cmd=" + cmdArray[2]);
		try {
			Process process = Runtime.getRuntime().exec(cmdArray);
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;

			while ((line = in.readLine()) != null) {
				result.add(line);
			}
			process.waitFor();
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return result;
	}

	/**
	 * Execute a linux command returning the text result as an integer
	 */
	public static int executeLinuxCmdWithResultint(String cmd) throws IOException, InterruptedException {
		String output = executeLinuxCmd(cmd);
		if (output.trim().length() > 0) {
			return Integer.parseInt(output.trim());
		}
		return 0;
	}

	/**
	 * Execute a linux command returning the result as a json object
	 */
	public static <T> T executeLinuxCmdWithResultJsonObject(String cmd, Class<T> classType) throws IOException, InterruptedException {
		String output = executeLinuxCmd(cmd);
		if (output.trim().length() > 0) {
			LOG.debug("output=" + output);
			Gson gson = new Gson();
			return gson.fromJson(output, classType);
		}
		return null;
	}

	/**
	 * Count number of lines in a file
	 */
	public static int getLinesCountForFile(File readFile) throws IOException, InterruptedException {
		String cmd = "wc -l " + readFile.getAbsolutePath() + " | cut -f 1 -d ' '";
		return executeLinuxCmdWithResultint(cmd);
	}

	/**
	 * Get the last n lines from a file
	 */
	public static List<String> getLastNFileLines(File readFile, int linesToRead) {
		return getLastNFileLines(readFile, linesToRead, 0);
	}

	/**
	 * Get last n lines from a file, returning only the first few lines from that set.
	 */
	public static List<String> getLastNFileLines(File readFile, int linesToRead, int headSize) {
		List<String> result = new ArrayList<String>();
		try {
			String cmd = "tail -" + linesToRead + " " + readFile.getAbsolutePath();
			if (headSize > 0) {
				cmd += " | head -" + headSize;
			}
			result = executeLinuxCmdWithResultLines(cmd);
		} catch (Exception e) {
			LOG.error(e, e);
		}
		return result;
	}

	public static String getFileNameWithoutExtension(String filename) {
		int index = filename.lastIndexOf(".");
		String filenameWithoutExtension = filename.substring(0, index);
		return filenameWithoutExtension;
	}

	public static String getFileExtension(String filename) {
		int index = filename.lastIndexOf(".");
		return filename.substring(index + 1);
	}

	public static String firstCharUpperCase(String input) {
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}

}