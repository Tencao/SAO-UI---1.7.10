package com.tencao.saoui.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.tencao.saoui.SAOLogger;

/**
 * Helper class for File IO of any sort. Might be unneeded.
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 * Special thanks and credit to ProjectE
 */
public final class SAOFileHelper
{

	public static void closeStream(Closeable c)
	{
		if (c != null)
		{
			try
			{
				c.close();
			}
			catch (IOException e)
			{
				SAOLogger.logFatal("IO Error: couldn't close stream!");
				e.printStackTrace();
			}
		}
	}

	public static void writeDefaultFile(String filename, String directory, List<String> lines)
	{
		File folder = new File(directory);
		File f = new File(folder, filename);
		PrintWriter writer = null;

		if (!folder.isDirectory())
		{
			folder.mkdir();
		}

		if (f.exists())
		{
			return;
		}
		try
		{
			if (f.createNewFile() && f.canWrite())
			{
				writer = new PrintWriter(f);

				for (String line : lines)
				{
					writer.println(line);
				}

			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			closeStream(writer);
		}

	}

}
