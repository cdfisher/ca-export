/*
 * Copyright (c) 2025, cdfisher <https://github.com/cdfisher>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.cdfisher.ca_export;

import com.google.gson.Gson;
import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.CommandExecuted;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Combat Achievement Exporter",
	description = "Export Combat Achievements to JSON",
	tags = {"combat", "achievements", "export", "json"}
)
public class CAExportPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private ScheduledExecutorService executor;

	@Inject
	private CAExportConfig config;

	@Inject
	private Gson gson;

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

int[] varpIds = new int[]{VarPlayerID.CA_TASK_COMPLETED_0,
		VarPlayerID.CA_TASK_COMPLETED_1,
		VarPlayerID.CA_TASK_COMPLETED_2,
		VarPlayerID.CA_TASK_COMPLETED_3,
		VarPlayerID.CA_TASK_COMPLETED_4,
		VarPlayerID.CA_TASK_COMPLETED_5,
		VarPlayerID.CA_TASK_COMPLETED_6,
		VarPlayerID.CA_TASK_COMPLETED_7,
		VarPlayerID.CA_TASK_COMPLETED_8,
		VarPlayerID.CA_TASK_COMPLETED_9,
		VarPlayerID.CA_TASK_COMPLETED_10,
		VarPlayerID.CA_TASK_COMPLETED_11,
		VarPlayerID.CA_TASK_COMPLETED_12,
		VarPlayerID.CA_TASK_COMPLETED_13,
		VarPlayerID.CA_TASK_COMPLETED_14,
		VarPlayerID.CA_TASK_COMPLETED_15,
		VarPlayerID.CA_TASK_COMPLETED_16,
		VarPlayerID.CA_TASK_COMPLETED_17,
		VarPlayerID.CA_TASK_COMPLETED_18,
	};

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
				String desc;

				var struct = client.getStructComposition(structId);
				var name = struct.getStringValue(1308);
				desc = (config.includeDescriptions() ? struct.getStringValue(1309) : null);
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
			if (config.printFilePath())
			{
				executor.submit(() -> fileWriter.writeGSON(client.getLocalPlayer().getName(), caEntries,
					config.includeDescriptions(), gson, chatMessageManager));
			} else {
				executor.submit(() -> fileWriter.writeGSON(client.getLocalPlayer().getName(), caEntries,
					config.includeDescriptions(), gson));
			}
		}
	}


	@Provides
	CAExportConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CAExportConfig.class);
	}
}
