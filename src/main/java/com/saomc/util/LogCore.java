package com.saomc.util;

import com.saomc.SAOCore;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogCore {

    private static Logger logger = LogManager.getLogger(SAOCore.MODID);

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
        if (OptionCore.DEBUG_MODE.getValue()) // visible in main console
        {
            logger.info(msg);
        }
        else
        {
            logger.debug(msg); // fml log
        }
    }
}
