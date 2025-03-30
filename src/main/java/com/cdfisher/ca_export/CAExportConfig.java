package com.cdfisher.ca_export;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("CAExport")
public interface CAExportConfig extends Config
{
/*	@ConfigItem(
		keyName = "printFilePath",
		name = "Print file path",
		description = "Print the path to the exported JSON file as a game message."
	)
	default boolean printFilePath()
	{
		return true;
	}*/

	@ConfigItem(
		keyName = "includeDescriptions",
		name = "Include CA descriptions",
		description = "Include the description of each combat achievement in the exported file."
	)
	default boolean includeDescriptions()
	{
		return false;
	}
}
