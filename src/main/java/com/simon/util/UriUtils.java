package com.simon.util;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URLDecoder;

/**
 * URI处理工具
 */
public class UriUtils {

	/**
	 * 获取请求的uri，会自动将连续多个的斜线（/）转化为单个斜线（/）
	 */
	public static String getUri(HttpServletRequest request) {
		if (request == null) {
			return null;
		}
		return fixSeparator(request.getRequestURI());
	}

	/**
	 * 修复uri中的连续斜线
	 */
	public static String fixSeparator(String uri) {
		if (uri == null) {
			return null;
		}
		try {
			uri = URLDecoder.decode(uri, "UTF-8").trim();
		} catch (Exception e) {
		}
		if (uri.contains("//")) {
			return uri.replaceAll("/+", "/");
		}
		return uri;
	}

	/**
	 * 获取classes目录完整路径
	 */
	public static String getRealPath(HttpServletRequest request) {
		String path = request.getServletContext().getRealPath("/WEB-INF/classes/");
		if (path == null) {
			return null;
		}
		path = path.trim();
		if (!path.endsWith(File.separator)) {
			path = path + File.separator;
		}
		return path;
	}

}