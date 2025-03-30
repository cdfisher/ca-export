package com.cdfisher.ca_export;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.CommandExecuted;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Combat Achievement Exporter"
)
public class CAExportPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ScheduledExecutorService executor;

	@Inject
	private CAExportConfig config;

	private static final Map<Integer, String> tierMap = Map.of(
		3981, "Easy",
		3982, "Medium",
		3983, "Hard",
		3984, "Elite",
		3985, "Master",
		3986, "Grandmaster"
	);

	private String caTier;

	private List<CAEntry> caEntries = new ArrayList<>();

	private CAFileWriter fileWriter = new CAFileWriter();

	int[] varpIds = new int[]{3116, 3117, 3118, 3119, 3120, 3121, 3122, 3123, 3124, 3125, 3126, 3127, 3128, 3387, 3718,
		3773, 3774, 4204, 4496};

	@Override
	protected void startUp() throws Exception
	{
		log.debug("CA Exporter started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.debug("CA Exporter stopped!");
	}

	private void getCAEntries()
	{
		caEntries.clear();
		for (int enumId : new int[]{3981, 3982, 3983, 3984, 3985, 3986})
		{
			caTier = tierMap.get(enumId);
			var e = client.getEnum(enumId);
			for (int structId : e.getIntVals())
			{
				var struct = client.getStructComposition(structId);
				var name = struct.getStringValue(1308);
				var desc = struct.getStringValue(1309);
				int id = struct.getIntValue(1306);
				boolean completed = (client.getVarpValue(varpIds[id / 32]) & (1 << (id % 32))) != 0;

				caEntries.add(new CAEntry(name, desc, id, caTier, completed));
			}
		}
	}

	@Subscribe
	private void onCommandExecuted(CommandExecuted commandExecuted)
	{
		if (commandExecuted.getCommand().equalsIgnoreCase("caexport"))
		{
			getCAEntries();
			executor.submit(() -> fileWriter.writeGSON(client.getLocalPlayer().getName(), caEntries,
				config.includeDescriptions()));
		}
	}


	@Provides
	CAExportConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CAExportConfig.class);
	}
}
