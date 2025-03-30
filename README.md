# Combat Achievement Exporter
A small plugin to easily export a player's Combat Achievements to a JSON file.

## Using

### Exporting
To export your combat achievements, use the command `::caexport` in the in-game chatbox. This will ***not*** send 
a message in game.

### Options
By default, the plugin doesn't export descriptions of the combat achievements. To change this, check the
`Include CA descriptions` box in the plugin config.

### Finding the file
The JSON file is written in a `ca_exporter` folder in the `.runelite` folder. The full path would be 
`path/to/.runelite/ca_exporter/<username>.json`. If you're having trouble finding your `.runelite` folder, right click 
the camera icon in the upper right hand corner of the RuneLite client window, click "Open screenshot folder", and then
navigate up one level.