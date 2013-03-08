/*
	Copyright (c) 2013 The Demigods Team
	
	Demigods License v1
	
	This plugin is provided "as is" and without any warranty.  Any express or
	implied warranties, including, but not limited to, the implied warranties
	of merchantability and fitness for a particular purpose are disclaimed.
	In no event shall the authors be liable to any party for any direct,
	indirect, incidental, special, exemplary, or consequential damages arising
	in any way out of the use or misuse of this plugin.
	
	Definitions
	
	 1. This Plugin is defined as all of the files within any archive
	    file or any group of files released in conjunction by the Demigods Team,
	    the Demigods Team, or a derived or modified work based on such files.
	
	 2. A Modification, or a Mod, is defined as this Plugin or a derivative of
	    it with one or more Modification applied to it, or as any program that
	    depends on this Plugin.
	
	 3. Distribution is defined as allowing one or more other people to in
	    any way download or receive a copy of this Plugin, a Modified
	    Plugin, or a derivative of this Plugin.
	
	 4. The Software is defined as an installed copy of this Plugin, a
	    Modified Plugin, or a derivative of this Plugin.
	
	 5. The Demigods Team is defined as Alex Bennett and Alexander Chauncey
	    of http://www.censoredsoftware.com/.
	
	Agreement
	
	 1. Permission is hereby granted to use, copy, modify and/or
	    distribute this Plugin, provided that:
	
	    a. All copyright notices within source files and as generated by
	       the Software as output are retained, unchanged.
	
	    b. Any Distribution of this Plugin, whether as a Modified Plugin
	       or not, includes this license and is released under the terms
	       of this Agreement. This clause is not dependant upon any
	       measure of changes made to this Plugin.
	
	    c. This Plugin, Modified Plugins, and derivative works may not
	       be sold or released under any paid license without explicit 
	       permission from the Demigods Team. Copying fees for the 
	       transport of this Plugin, support fees for installation or
	       other services, and hosting fees for hosting the Software may,
	       however, be imposed.
	
	    d. Any Distribution of this Plugin, whether as a Modified
	       Plugin or not, requires express written consent from the
	       Demigods Team.
	
	 2. You may make Modifications to this Plugin or a derivative of it,
	    and distribute your Modifications in a form that is separate from
	    the Plugin. The following restrictions apply to this type of
	    Modification:
	
	    a. A Modification must not alter or remove any copyright notices
	       in the Software or Plugin, generated or otherwise.
	
	    b. When a Modification to the Plugin is released, a
	       non-exclusive royalty-free right is granted to the Demigods Team
	       to distribute the Modification in future versions of the
	       Plugin provided such versions remain available under the
	       terms of this Agreement in addition to any other license(s) of
	       the initial developer.
	
	    c. Any Distribution of a Modified Plugin or derivative requires
	       express written consent from the Demigods Team.
	
	 3. Permission is hereby also granted to distribute programs which
	    depend on this Plugin, provided that you do not distribute any
	    Modified Plugin without express written consent.
	
	 4. The Demigods Team reserves the right to change the terms of this
	    Agreement at any time, although those changes are not retroactive
	    to past releases, unless redefining the Demigods Team. Failure to
	    receive notification of a change does not make those changes invalid.
	    A current copy of this Agreement can be found included with the Plugin.
	
	 5. This Agreement will terminate automatically if you fail to comply
	    with the limitations described herein. Upon termination, you must
	    destroy all copies of this Plugin, the Software, and any
	    derivatives within 48 hours.
 */

package com.censoredsoftware.Demigods.API;

import com.censoredsoftware.Demigods.Demigods;
import com.censoredsoftware.Demigods.Handlers.Database.DFlatFile;
import com.censoredsoftware.Demigods.Libraries.Objects.PlayerCharacter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class CharAPI
{
	private static final Demigods API = Demigods.INSTANCE;

	/*
	 * createChar() : Creates a character for (Player)player if it doesn't currently exist.
	 */
	public PlayerCharacter createChar(OfflinePlayer player, String charName, String charDeity)
	{
		if(getCharByName(charName) == null)
		{
			// Define variables
			int charID = API.object.generateInt(5);

			PlayerCharacter character = new PlayerCharacter(player, charID, charName, charDeity);

			// Add character to player's character list
			List<Integer> chars = API.player.getChars(player);
			chars.add(charID);
			API.data.savePlayerData(player, "player_characters", chars);

			// Save data
			// TEMP -------------------------------
			DFlatFile.savePlayer(player);

			return character;
		}
		return null;
	}

	/*
	 * getChar() : Returns the Character object for (int)charID.
	 */
	public PlayerCharacter getChar(int charID)
	{
		HashMap<Integer, HashMap<String, Object>> characters = API.data.getAllChars();

		if(characters.containsKey(charID)) return (PlayerCharacter) characters.get(charID).get("char_object");
		else return null;
	}

	/*
	 * getCharByName() : Returns the Character object for (String)charName.
	 */
	public PlayerCharacter getCharByName(String charName)
	{
		for(Entry<Integer, HashMap<String, Object>> character : API.data.getAllChars().entrySet())
		{
			HashMap<String, Object> charData = character.getValue();

			if(charData.containsKey("char_object") && ((PlayerCharacter) charData.get("char_object")).getName().equalsIgnoreCase(charName))
			{
				return (PlayerCharacter) charData.get("char_object");
			}
		}
		return null;
	}

	/*
	 * getOwner() : Returns the (OfflinePlayer)player who owns (int)charID.
	 */
	public OfflinePlayer getOwner(int charID)
	{
		for(Entry<Integer, HashMap<String, Object>> character : API.data.getAllChars().entrySet())
		{
			HashMap<String, Object> charData = character.getValue();

			if(charData.containsKey("char_object") && ((PlayerCharacter) charData.get("char_object")).getID() == charID)
			{
				return ((PlayerCharacter) charData.get("char_object")).getOwner();
			}
		}
		return null;
	}

	/*
	 * isCooledDown() : Checks if the passed in ability has cooled down or not.
	 */
	public boolean isCooledDown(Player player, String ability, long ability_time, boolean sendMsg)
	{
		if(ability_time > System.currentTimeMillis())
		{
			if(sendMsg) player.sendMessage(ChatColor.RED + ability + " has not cooled down!");
			return false;
		}
		else return true;
	}

	/*
	 * getImmortalList() : Gets list of currently immortal players.
	 */
	public ArrayList<PlayerCharacter> getImmortalList()
	{
		// Define variables
		ArrayList<PlayerCharacter> immortalList = new ArrayList<PlayerCharacter>();
		HashMap<Integer, HashMap<String, Object>> characters = API.data.getAllChars();

		for(Entry<Integer, HashMap<String, Object>> character : characters.entrySet())
		{
			int charID = character.getKey();
			HashMap<String, Object> data = character.getValue();
			PlayerCharacter dataChar = getChar(charID);

			if(data.get("char_immortal") != null && API.object.toBoolean(data.get("char_immortal"))) immortalList.add(dataChar);
		}

		return immortalList;
	}
}
