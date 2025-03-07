package com.cdfisher.ca_export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import static net.runelite.client.RuneLite.RUNELITE_DIR;
import net.runelite.http.api.RuneLiteAPI;

@Slf4j
public class CAFileWriter
{
	private static final File CA_EXPORT_DIR = new File(RUNELITE_DIR, "ca_exporter");
	public void writeGSON(String username, List<CAEntry> caEntries)
	{
		try
		{
			CA_EXPORT_DIR.mkdir();
			//set file name to <username>.json
			final String name = username.toLowerCase().trim() + ".json";
			// write gson to CA_EXPORT_DIR/filename
			final BufferedWriter writer = new BufferedWriter(new FileWriter(new File(CA_EXPORT_DIR, name), false));
			final String caString = RuneLiteAPI.GSON.toJson(caEntries);
			writer.append(caString);
			writer.close();
		}
		catch (IOException e)
		{
			log.warn("CA Exporter: Error writing combat achievements to file: {}", e.getMessage());
		}
	}


}
