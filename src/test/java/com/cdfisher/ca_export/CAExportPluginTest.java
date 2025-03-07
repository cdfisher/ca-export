package com.cdfisher.ca_export;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class CAExportPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(CAExportPlugin.class);
		RuneLite.main(args);
	}
}