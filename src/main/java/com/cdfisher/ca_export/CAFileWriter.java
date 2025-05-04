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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import static net.runelite.client.RuneLite.RUNELITE_DIR;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;

@Slf4j
public class CAFileWriter
{
	private String fileName;
	private static final File CA_EXPORT_DIR = new File(RUNELITE_DIR, "ca_exporter");
	private Gson gson;
	private ChatMessageManager chatMessageManager;

	public CAFileWriter(Gson gson, ChatMessageManager chatMessageManager)
	{
		this.gson = gson;
		this.chatMessageManager = chatMessageManager;
	}

	public void writeGSON(String username, List<CAEntry> caEntries, boolean printPath)
	{
		try
		{
			CA_EXPORT_DIR.mkdir();
			//set file name to <username>.json
			fileName = username.toLowerCase().trim() + ".json";
			// write gson to CA_EXPORT_DIR/filename
			final BufferedWriter writer = new BufferedWriter(new FileWriter(new File(CA_EXPORT_DIR, fileName), false));
			final String caString = this.gson.toJson(caEntries);
			writer.append(caString);
			writer.close();
		}
		catch (IOException e)
		{
			log.warn("CA Exporter: Error writing combat achievements to file: {}", e.getMessage());
		}
		log.info("Wrote Combat Achievement JSON to {}/{}", CA_EXPORT_DIR.getName().replace("\\", "/"), fileName);

		if (printPath)
		{
			ChatMessageBuilder message = new ChatMessageBuilder()
				.append("[Combat Achievement Exporter] Wrote Combat Achievements to ")
				.append(CA_EXPORT_DIR.getAbsolutePath().replace("\\", "/"))
				.append("/")
				.append(fileName);

			this.chatMessageManager.queue(QueuedMessage.builder()
				.type(ChatMessageType.GAMEMESSAGE)
				.runeLiteFormattedMessage(message.build())
				.build());
		}

	}

}
