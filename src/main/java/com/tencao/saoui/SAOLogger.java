package com.tencao.saoui;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public final class SAOLogger {

	private static Logger logger = LogManager.getLogger(SAOMod.MODID);

	public static void log(Level level, String msg)
	{
		logger.log(level, msg);
	}

	public static void logInfo(String msg)
	{
		logger.info(msg);
	}

	public static void logWarn(String msg)
	{
		logger.warn(msg);
	}

	public static void logFatal(String msg)
	{
		logger.fatal(msg);
	}
	public static void logDebug(String msg)
	{
		if (SAOMod.DEBUG) // visible in main console
		{
			logger.info(msg);
		}
		else
		{
			logger.debug(msg); // fml log
		}
	}

	public static void log(Level level, String msg, Object... args)
	{
		logger.log(level, String.format(msg, args));
	}

	public static void logInfo(String msg, Object... args)
	{
		logger.info(String.format(msg, args));
	}

	public static void logWarn(String msg, Object... args)
	{
		logger.warn(String.format(msg, args));
	}

	public static void logFatal(String msg, Object... args)
	{
		logger.fatal(String.format(msg, args));
	}

	public static void logDebug(String msg, Object... args)
	{
		if (SAOMod.DEBUG) // visible in main console
		{
			logger.info(String.format(msg, args));
		}
		else
		{
			logger.debug(String.format(msg, args)); // fml log
		}
	}
}
