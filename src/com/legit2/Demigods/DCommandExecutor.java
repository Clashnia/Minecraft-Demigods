package com.legit2.Demigods;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.legit2.Demigods.Libraries.ReflectCommand;

public class DCommandExecutor
{
	static Demigods plugin;
	
	public DCommandExecutor(Demigods instance)
	{
		plugin = instance;
	}
	
	/*
	 *  Command: "test"
	 */
	@ReflectCommand.Command(name = "test", sender = ReflectCommand.Sender.EVERYONE, permission = "demigods.basic")
	public static boolean test(CommandSender sender)
	{
		HashMap<String, Object> player_data = DSave.getPlayerData(sender.getName());	

		// Loop through player data entry set and add to database
		for(Map.Entry<String, Object> entry : player_data.entrySet())
		{
			String id = entry.getKey();
			Object data = entry.getValue();

			sender.sendMessage(id + ": " + data.toString());
		}
		
		return true;
	}
	
	/*
	 *  Command: "givealliance"
	 */
	@ReflectCommand.Command(name = "givealliance", sender = ReflectCommand.Sender.PLAYER, permission = "demigods.basic")
	public static boolean giveAlliance(CommandSender sender, String arg1, String arg2)
	{
		if(arg1 == "noargs") return false;
		
		DUtil.setAlliance(arg1, arg2);
		sender.sendMessage(ChatColor.GREEN + "You've given " + arg2 + " to " + arg1 + "!");
		
		return true;
	}
	
	/*
	 *  Command: "givedeity"
	 */
	@ReflectCommand.Command(name = "givedeity", sender = ReflectCommand.Sender.PLAYER, permission = "demigods.basic")
	public static boolean giveDeity(CommandSender sender, String arg1, String arg2)
	{
		if(arg1 == "noargs") return false;
		
		DUtil.giveDeity(arg1, arg2);
		DUtil.setFavor(arg1, 9999);
		DUtil.setAscensions(arg1, 9999);
		DUtil.setDevotion(arg1, arg2, 9999);
		DUtil.setKills(arg1, 9999);
		
		sender.sendMessage(ChatColor.GREEN + "You've given " + arg2 + " to " + arg1 + "!");
		
		return true;
	}
	
	/*
	 *  Command: "dg"
	 */
	@ReflectCommand.Command(name = "dg", sender = ReflectCommand.Sender.PLAYER, permission = "demigods.basic")
	public static boolean dg(CommandSender sender, String arg1)
	{
		if(arg1 != "noargs")
		{
			dg_info(sender, arg1);
			return true;
		}
				
		// Define Player
		Player player = DUtil.definePlayer(sender);
		
		// Check Permissions
		if(!DUtil.hasPermissionOrOP(player, "demigods.basic")) return DUtil.noPermission(player);
		
		DUtil.taggedMessage(sender, "Information Directory");
		sender.sendMessage(ChatColor.GRAY + "/dg god");
		sender.sendMessage(ChatColor.GRAY + "/dg titan");
		sender.sendMessage(ChatColor.GRAY + "/dg claim");
		sender.sendMessage(ChatColor.GRAY + "/dg shrine");
		sender.sendMessage(ChatColor.GRAY + "/dg tribute");
		sender.sendMessage(ChatColor.GRAY + "/dg player");
		sender.sendMessage(ChatColor.GRAY + "/dg pvp");
		sender.sendMessage(ChatColor.GRAY + "/dg rankings");
		sender.sendMessage(ChatColor.WHITE + "Use " + ChatColor.YELLOW + "/check" + ChatColor.WHITE + " to see your player information.");
		return true;
	}
	
	/*
	 *  Command: "dg_info"
	 */
	public static boolean dg_info(CommandSender sender, String category)
	{
		// Define Player
		Player player = DUtil.definePlayer(sender);
		
		// Check Permissions
		if(!DUtil.hasPermissionOrOP(player, "demigods.basic")) return DUtil.noPermission(player);
		
		if(category.equalsIgnoreCase("save"))
		{
			if(DUtil.hasPermissionOrOP(player, "demigods.admin"))
			{
				DUtil.serverMsg(ChatColor.YELLOW + sender.getName() + ChatColor.GREEN + " is saving all Demigods data...");
				if(DDatabase.saveAllData())
				{
					DUtil.serverMsg(ChatColor.GREEN + "Save complete!");
				}
				else
				{
					DUtil.serverMsg(ChatColor.RED + "There was a problem with saving...");
					DUtil.serverMsg(ChatColor.RED + "An admin should check the log.");
				}
			}
			else DUtil.noPermission(player);
		}
		else if(category.equalsIgnoreCase("god"))
		{
			DUtil.taggedMessage(sender, "Gods");
			sender.sendMessage(ChatColor.GRAY + " This is some info about Gods.");
		}
		else if(category.equalsIgnoreCase("titan"))
		{
			DUtil.taggedMessage(sender, "Titans");
			sender.sendMessage(ChatColor.GRAY + " This is some info about Titans.");
		}
		else if(category.equalsIgnoreCase("claim"))
		{
			DUtil.taggedMessage(sender, "Claiming");
			sender.sendMessage(ChatColor.GRAY + " This is some info about Claiming.");
		}
		else if(category.equalsIgnoreCase("shrine"))
		{
			DUtil.taggedMessage(sender, "Shrines");
			sender.sendMessage(ChatColor.GRAY + " This is some info about Shrines.");
		}
		else if(category.equalsIgnoreCase("tribute"))
		{
			DUtil.taggedMessage(sender, "Tributes");
			sender.sendMessage(ChatColor.GRAY + " This is some info about Tributes.");
		}
		else if(category.equalsIgnoreCase("player"))
		{
			DUtil.taggedMessage(sender, "Players");
			sender.sendMessage(ChatColor.GRAY + " This is some info about Players.");
		}
		else if(category.equalsIgnoreCase("pvp"))
		{
			DUtil.taggedMessage(sender, "PVP");
			sender.sendMessage(ChatColor.GRAY + " This is some info about PVP.");
		}
		else if(category.equalsIgnoreCase("stats"))
		{
			DUtil.taggedMessage(sender, "Stats");
			sender.sendMessage(ChatColor.GRAY + " These are some stats for Demigods.");
		}
		else if(category.equalsIgnoreCase("rankings"))
		{
			DUtil.taggedMessage(sender, "Rankings");
			sender.sendMessage(ChatColor.GRAY + " This is some ranking info about Demigods.");
		}	
		
		return true;
	}
	
	/*
	 *  Command: "check"
	 */
	@ReflectCommand.Command(name = "check", sender = ReflectCommand.Sender.EVERYONE, usage = "/check", permission = "demigods.basic")
	public static boolean check(CommandSender sender)
	{
		// Define Player and Username
		Player player = DUtil.definePlayer(sender);
		String username = player.getName();
				
		try
		{
			ResultSet player_info = DDatabase.getPlayerInfo(username);
			
			// Send the user their info via chat
			DUtil.taggedMessage(sender, "Player check: " + ChatColor.YELLOW + username);
			//sender.sendMessage("com.legit2.Demigods.Deities: " + ChatColor.GREEN + player_info.getString("deities"));
			sender.sendMessage("Deities: " + ChatColor.DARK_GREEN + player_info.getString("deities"));
			sender.sendMessage("Favor: " + ChatColor.GREEN + player_info.getString("favor"));
			sender.sendMessage("Ascensions: " + ChatColor.GREEN + player_info.getString("ascensions"));
			sender.sendMessage(" ");
			sender.sendMessage("Kills: " + ChatColor.GREEN + player_info.getString("kills") + ChatColor.WHITE + " / Deaths: " + ChatColor.RED + player_info.getString("deaths"));
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
}