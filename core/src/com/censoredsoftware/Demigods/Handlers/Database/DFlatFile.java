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

package com.censoredsoftware.Demigods.Handlers.Database;

import com.censoredsoftware.Demigods.Demigods;
import com.censoredsoftware.Demigods.Libraries.Objects.Altar;
import com.censoredsoftware.Demigods.Libraries.Objects.Shrine;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.*;
import java.util.HashMap;
import java.util.Map.Entry;

public class DFlatFile
{
	private static final Demigods API = Demigods.INSTANCE;
	static final String path = "plugins/Demigods/data/";
	static File DemigodsDir, PlayerDir, CharacterDir, BattleDir, QuestDir, BlockDir, MetricsDir, PublicMetricsDir, PrivateMetricsDir;

	public static void start()
	{
		DemigodsDir = new File(path + "demigods/");
		if(!DemigodsDir.exists())
		{
			DemigodsDir.mkdirs();
			API.misc.info("New Demigods inline data save created.");
		}

		PlayerDir = new File(path + "players/");
		if(!PlayerDir.exists())
		{
			PlayerDir.mkdirs();
			API.misc.info("New player data save created.");
		}

		CharacterDir = new File(path + "characters/");
		if(!CharacterDir.exists())
		{
			CharacterDir.mkdirs();
			API.misc.info("New character data save created.");
		}

		BattleDir = new File(path + "battles/");
		if(!BattleDir.exists())
		{
			BattleDir.mkdirs();
			API.misc.info("New Battle data save created.");
		}

		QuestDir = new File(path + "quests/");
		if(!QuestDir.exists())
		{
			QuestDir.mkdirs();
			API.misc.info("New Quest data save created.");
		}

		BlockDir = new File(path + "blocks/");
		if(!BlockDir.exists())
		{
			BlockDir.mkdirs();
			API.misc.info("New Divine Block data save created.");
		}

        MetricsDir = new File(path + "metrics/");
        PublicMetricsDir = new File(path + "metrics/public");
        PrivateMetricsDir = new File(path + "metrics/private");
        if(!MetricsDir.exists())
        {
            MetricsDir.mkdirs();
            API.misc.info("New Metrics data save created.");
        }
        if(!PublicMetricsDir.exists()) PublicMetricsDir.mkdirs();
        if(!PrivateMetricsDir.exists()) PrivateMetricsDir.mkdirs();
	}

	/*
	 * save : Saves itself, but must be loaded elsewhere (main plugin).
	 */
	public static boolean save()
	{
		start();

		try
		{
			// TODO: Make this work for when we don't load all data.
			// Clear files first
			for(File file : DemigodsDir.listFiles())
				file.delete();
			for(File file : PlayerDir.listFiles())
				file.delete();
			for(File file : CharacterDir.listFiles())
				file.delete();
			for(File file : BlockDir.listFiles())
				file.delete();
			for(File file : QuestDir.listFiles())
				file.delete();
			for(File file : BattleDir.listFiles())
				file.delete();
            for(File file : MetricsDir.listFiles())
                file.delete();

			// Start the timer
			long startTimer = System.currentTimeMillis();

			saveDemigods();
			int playerCount = savePlayers();
			int battleCount = saveBattles();
			int questCount = saveQuests();
			int blockCount = saveBlocks();
            int metricCount = saveMetrics();

			// Stop the timer
			long stopTimer = System.currentTimeMillis();
			double totalTime = (double) (stopTimer - startTimer);

			API.misc.info(playerCount + " players, " + battleCount + " battles, " + questCount + " quests, " + metricCount + " metrics, and " + blockCount + " blocks saved in " + (totalTime / 1000) + " seconds.");

			return true;
		}
		catch(Exception e)
		{
			API.misc.severe("Something went wrong while saving.");
			e.printStackTrace();

			return false;
		}
	}

	public static void saveDemigods()
	{
		start();

		try
		{
			for(String data : API.data.getAllPluginData().keySet())
			{
				if(data.startsWith("temp_")) continue;
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DemigodsDir.getPath() + File.separator + data + ".demi"));
				oos.writeObject(API.data.getAllPluginData().get(data));
				oos.flush();
				oos.close();
			}
		}
		catch(Exception e)
		{
			API.misc.severe("Something went wrong while saving inline Demigods data.");
			e.printStackTrace();
		}
	}

	public static int savePlayers()
	{
		start();

		int count = 0;

		try
		{
			for(OfflinePlayer player : API.player.getAllPlayers())
			{
				count++;

				savePlayer(player);
			}
		}
		catch(Exception e)
		{
			API.misc.severe("Something went wrong while saving players.");
			e.printStackTrace();
		}

		return count;
	}

	public static int savePlayer(OfflinePlayer player)
	{
		start();

		int charCount = 0;

		try
		{
			// Define variables
			String playerName = player.getName();

			// Create a temporary player data HashMap to save
			HashMap<String, Object> playerDataMap = new HashMap<String, Object>(API.data.getAllPlayerData(Bukkit.getOfflinePlayer(playerName)));

			for(Entry<String, Object> data : API.data.getAllPlayerData(Bukkit.getOfflinePlayer(playerName)).entrySet())
			{
				if(data.getKey().contains("temp_")) playerDataMap.remove(data.getKey());
			}

			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PlayerDir.getPath() + File.separator + playerName + ".demi"));
			oos.writeObject(playerDataMap);
			oos.flush();
			oos.close();
			playerDataMap.clear();

			// Save their characters
			for(Entry<Integer, HashMap<String, Object>> character : API.data.getAllPlayerChars(player).entrySet())
			{
				charCount++;
				int charID = character.getKey();

				// Create a temporary character data HashMap to save
				HashMap<String, Object> charDataMap = new HashMap<String, Object>(character.getValue());

				for(Entry<String, Object> data : character.getValue().entrySet())
				{
					if(data.getKey().contains("temp_"))
					{
						charDataMap.remove(data.getKey());
					}
				}

				ObjectOutputStream oos2 = new ObjectOutputStream(new FileOutputStream(CharacterDir.getPath() + File.separator + charID + ".demi"));
				oos2.writeObject(charDataMap);
				oos2.flush();
				oos2.close();

				charDataMap.clear();
			}
		}
		catch(Exception e)
		{
			API.misc.severe("Something went wrong while saving players.");
			e.printStackTrace();
		}

		return charCount;
	}

	public static int saveBattles()
	{
		start();

		int count = 0;

		try
		{
			for(Integer battleID : API.data.getAllBattles().keySet())
			{
				count++;

				HashMap<String, Object> battle = API.data.getAllBattles().get(battleID);
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BattleDir.getPath() + File.separator + battleID + ".demi"));
				oos.writeObject(battle);
				oos.flush();
				oos.close();
			}
		}
		catch(Exception e)
		{
			API.misc.severe("Something went wrong while saving battles.");
			e.printStackTrace();
		}

		return count;
	}

	public static int saveQuests()
	{
		start();

		int count = 0;

		try
		{
			for(Integer taskID : API.data.getAllTasks().keySet())
			{
				count++;

				HashMap<String, Object> task = API.data.getAllTasks().get(taskID);
				String quest = task.get("task_quest").toString().toLowerCase().replace(" ", "_");
				File questDir = new File(path + "quests" + File.separator + quest + File.separator);
				if(!questDir.exists()) questDir.mkdirs();
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(QuestDir.getPath() + File.separator + quest + File.separator + taskID + ".demi"));
				oos.writeObject(task);
				oos.flush();
				oos.close();
			}
		}
		catch(Exception e)
		{
			API.misc.severe("Something went wrong while saving quests.");
			e.printStackTrace();
		}

		return count;
	}

	public static int saveBlocks()
	{
		start();

		int count = 0;

		try
		{
			for(String blockType : API.data.getAllBlockData().keySet())
			{
				for(Entry<Integer, Object> block : API.data.getAllBlockData().get(blockType).entrySet())
				{
					count++;

					int blockID = block.getKey();

					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BlockDir.getPath() + File.separator + blockID + "_" + blockType.toLowerCase() + ".demi"));
					oos.writeObject(block.getValue());
					oos.flush();
					oos.close();
				}

			}
		}
		catch(Exception e)
		{
			API.misc.severe("Something went wrong while saving divine blocks.");
			e.printStackTrace();
		}

		return count;
	}

    public static int saveMetrics()
    {
        start();

        int count = 0;

        try
        {
            for(String metric : API.metrics.getAllPublic().keySet())
            {
                count++;

                HashMap<Object, Object> data = API.metrics.getPublicFor(metric);
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MetricsDir.getPath() + File.separator + "public" + File.separator + metric + ".demi"));
                oos.writeObject(data);
                oos.flush();
                oos.close();
            }

            for(String metric : API.metrics.getAllPrivate().keySet())
            {
                if(metric.startsWith("temp_")) continue;

                count++;

                HashMap<Object, Object> data = API.metrics.getPrivateFor(metric);
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MetricsDir.getPath() + File.separator + "private" + File.separator + metric + ".demi"));
                oos.writeObject(data);
                oos.flush();
                oos.close();
            }
        }
        catch(Exception e)
        {
            API.misc.severe("Something went wrong while saving Metrics.");
            e.printStackTrace();
        }

        return count;
    }

	/*
	 * load() : Loads all Flat File data to HashMaps.
	 */
	public static void load()
	{
		start();

		API.misc.info("Loading all data...");

		loadDemigods(true);
		loadPlayers(true);
		loadCharacters(true);
		loadBattles(true);
		loadQuests(true);
		loadBlocks(true);
        loadMetrics(true);
	}

	@SuppressWarnings("unchecked")
	public static void loadDemigods(boolean msgBool)
	{
		start();

		File[] fileList = DemigodsDir.listFiles();
		if(fileList != null)
		{
			for(File element : fileList)
			{
				String load = element.getName();
				if(load.endsWith(".demi"))
				{
					load = load.substring(0, load.length() - 5);

					try
					{
						ObjectInputStream ois = new ObjectInputStream(new FileInputStream(element));
						Object result = ois.readObject();
						API.data.getAllPluginData().put(load, (HashMap<String, Object>) result);
						ois.close();
					}
					catch(Exception error)
					{
						API.misc.severe("Could not load data: " + load);
						error.printStackTrace();
						API.misc.severe("End stack trace for " + load);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void loadPlayers(boolean msgBool)
	{
		start();

		File[] fileList = PlayerDir.listFiles();
		if(fileList != null)
		{
			for(File element : fileList)
			{
				String load = element.getName();
				if(load.endsWith(".demi"))
				{
					load = load.substring(0, load.length() - 5);

					try
					{
						ObjectInputStream ois = new ObjectInputStream(new FileInputStream(element));
						Object result = ois.readObject();
						API.data.getAllPlayers().put(load, (HashMap<String, Object>) result);
						ois.close();
					}
					catch(Exception error)
					{
						API.misc.severe("Could not load player: " + load);
						error.printStackTrace();
						API.misc.severe("End stack trace for " + load);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void loadCharacters(boolean msgBool)
	{
		start();

		File[] fileList = CharacterDir.listFiles();
		if(fileList != null)
		{
			for(File element : fileList)
			{
				String load = element.getName();
				if(load.endsWith(".demi"))
				{
					load = load.substring(0, load.length() - 5);

					Integer intLoad = API.object.toInteger(load);

					try
					{
						ObjectInputStream ois = new ObjectInputStream(new FileInputStream(element));
						Object result = ois.readObject();
						API.data.getAllChars().put(intLoad, (HashMap<String, Object>) result);
						ois.close();
					}
					catch(Exception error)
					{
						API.misc.severe("Could not load character: " + load);
						error.printStackTrace();
						API.misc.severe("End stack trace for " + load);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void loadBattles(boolean msgBool)
	{
		start();

		File[] fileList = BattleDir.listFiles();
		if(fileList != null)
		{
			for(File element : fileList)
			{
				String load = element.getName();
				if(load.endsWith(".demi"))
				{
					load = load.substring(0, load.length() - 5);

					Integer intLoad = API.object.toInteger(load);

					try
					{
						ObjectInputStream ois = new ObjectInputStream(new FileInputStream(element));
						Object result = ois.readObject();
						API.data.getAllBattles().put(intLoad, (HashMap<String, Object>) result);
						ois.close();
					}
					catch(Exception error)
					{
						API.misc.severe("Could not load battle: " + load);
						error.printStackTrace();
						API.misc.severe("End stack trace for " + load);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void loadQuests(boolean msgBool)
	{
		start();

		File[] fileList = QuestDir.listFiles();
		if(fileList != null)
		{
			for(File element : fileList)
			{
				if(element.isDirectory())
				{
					File[] elementList = element.listFiles();
					assert elementList != null;
					for(File task : elementList)
					{
						String load = task.getName();
						if(load.endsWith(".demi"))
						{
							load = load.substring(0, load.length() - 5);

							Integer intLoad = API.object.toInteger(load);

							try
							{
								ObjectInputStream ois = new ObjectInputStream(new FileInputStream(task));
								Object result = ois.readObject();
								API.data.getAllTasks().put(intLoad, (HashMap<String, Object>) result);
								ois.close();
							}
							catch(Exception error)
							{
								API.misc.severe("Could not load task: " + load);
								error.printStackTrace();
								API.misc.severe("End stack trace for " + load);
							}
						}
					}
				}
			}
		}
	}

	public static void loadBlocks(boolean msgBool)
	{
		API.data.getAllBlockData().put("altars", new HashMap<Integer, Object>());
		API.data.getAllBlockData().put("shrines", new HashMap<Integer, Object>());

		start();

		File[] fileList = BlockDir.listFiles();
		if(fileList != null)
		{
			for(File element : fileList)
			{
				String load = element.getName();
				if(load.endsWith(".demi"))
				{
					load = load.substring(0, load.length() - 5);

					try
					{
						ObjectInputStream ois = new ObjectInputStream(new FileInputStream(element));
						Object block = ois.readObject();

						if(load.contains("_altars"))
						{
							int blockID = API.object.toInteger(element.getName().replace("_altars.demi", ""));

							API.data.getAllBlockData().get("altars").put(blockID, block);

							Altar altar = (Altar) block;
							altar.generate();
						}
						else if(load.contains("_shrines"))
						{
							int blockID = API.object.toInteger(element.getName().replace("_shrines.demi", ""));
							API.data.getAllBlockData().get("shrines").put(blockID, block);

							Shrine shrine = (Shrine) block;
							shrine.generate();
						}

						ois.close();
					}
					catch(Exception error)
					{
						API.misc.severe("Could not load divine block: " + load);
						error.printStackTrace();
						API.misc.severe("End stack trace for " + load);
					}
				}
			}
		}
	}

    @SuppressWarnings("unchecked")
    public static void loadMetrics(boolean msgBool)
    {
        start();

        File[] publicFileList = PublicMetricsDir.listFiles();
        File[] privateFileList = PrivateMetricsDir.listFiles();

        if(publicFileList != null)
        {
            for(File element : publicFileList)
            {
                String load = element.getName();
                if(load.endsWith(".demi"))
                {
                    load = load.substring(0, load.length() - 5);

                    String stringLoad = load.toString();

                    try
                    {
                        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(element));
                        Object result = ois.readObject();
                        API.metrics.getAllPublic().put(stringLoad, (HashMap<Object, Object>) result);
                        ois.close();
                    }
                    catch(Exception error)
                    {
                        API.misc.severe("Could not load metric: " + load);
                        error.printStackTrace();
                        API.misc.severe("End stack trace for " + load);
                    }
                }
            }
        }
        if(privateFileList != null)
        {
            for(File element : privateFileList)
            {
                String load = element.getName();
                if(load.endsWith(".demi"))
                {
                    load = load.substring(0, load.length() - 5);

                    String stringLoad = load.toString();

                    try
                    {
                        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(element));
                        Object result = ois.readObject();
                        API.metrics.getAllPrivate().put(stringLoad, (HashMap<Object, Object>) result);
                        ois.close();
                    }
                    catch(Exception error)
                    {
                        API.misc.severe("Could not load metric: " + load);
                        error.printStackTrace();
                        API.misc.severe("End stack trace for " + load);
                    }
                }
            }
        }
    }
}
