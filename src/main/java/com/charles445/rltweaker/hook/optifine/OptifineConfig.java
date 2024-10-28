package com.charles445.rltweaker.hook.optifine;

import org.apache.logging.log4j.Logger;

import meldexun.reflectionutil.ReflectionField;

public class OptifineConfig {

	private static final Logger LOGGER = new ReflectionField<Logger>("Config", "LOGGER", "").get(null);
	private static final ReflectionField<Boolean> logDetail = new ReflectionField<>("Config", "logDetail", "");

	public static boolean equals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		} else {
			return o1 == null ? false : o1.equals(o2);
		}
	}

	public static void detail(String s) {
		if (logDetail.getBoolean(null)) {
			LOGGER.info("[OptiFine] " + s);
		}
	}

	public static void dbg(String s) {
		LOGGER.info("[OptiFine] " + s);
	}

	public static void warn(String s) {
		LOGGER.warn("[OptiFine] " + s);
	}

	public static void error(String s) {
		LOGGER.error("[OptiFine] " + s);
	}

	public static void log(String s) {
		dbg(s);
	}

	public static String arrayToString(Object[] arr) {
		return arrayToString(arr, ", ");
	}

	public static String arrayToString(Object[] arr, String separator) {
		if (arr == null) {
			return "";
		} else {
			StringBuffer buf = new StringBuffer(arr.length * 5);

			for (int i = 0; i < arr.length; ++i) {
				Object obj = arr[i];
				if (i > 0) {
					buf.append(separator);
				}

				buf.append(String.valueOf(obj));
			}

			return buf.toString();
		}
	}

}
