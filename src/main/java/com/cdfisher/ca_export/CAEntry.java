package com.cdfisher.ca_export;

class CAEntry
{
	private final String name;
	private final String description;
	private final int id;
	private final String tier;
	private final boolean completed;

	//name is struct param 1308 (str)
	//description is 1309 (str)
	//id is 1306 (int)
	//completed bool
	CAEntry(String name, String description, int id, String tier, boolean completed)
	{
		this.name = name;
		this.description = description;
		this.id = id;
		this.tier = tier;
		this.completed = completed;
	}
}