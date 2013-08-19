package com.censoredsoftware.demigods.command;

import com.censoredsoftware.demigods.Demigods;
import com.censoredsoftware.demigods.battle.Battle;
import com.censoredsoftware.demigods.helper.ColoredStringBuilder;
import com.censoredsoftware.demigods.helper.ListedCommand;
import com.censoredsoftware.demigods.player.DPlayer;
import com.censoredsoftware.demigods.util.Errors;
import com.censoredsoftware.demigods.util.Unicodes;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DevelopmentCommands extends ListedCommand
{
	@Override
	public Set<String> getCommands()
	{
		return Sets.newHashSet("test1", "test2", "test3", "hspawn");
	}

	@Override
	public boolean processCommand(CommandSender sender, Command command, String[] args)
	{
		if(command.getName().equalsIgnoreCase("test1")) return test1(sender, args);
		else if(command.getName().equalsIgnoreCase("test2")) return test2(sender, args);
		else if(command.getName().equalsIgnoreCase("test3")) return test3(sender, args);
		else if(command.getName().equalsIgnoreCase("hspawn")) return hspawn(sender);
		return false;
	}

	@Override
	public List<String> processTab(CommandSender sender, Command command, final String[] args)
	{
		return new ArrayList<String>()
		{
			{
				for(Player online : Bukkit.getOnlinePlayers())
				{
					DPlayer wrapper = DPlayer.Util.getPlayer(online);
					if(wrapper.canUseCurrent() && wrapper.getCurrent() != null && wrapper.getCurrent().getName().toLowerCase().startsWith(args[0].toLowerCase())) add(wrapper.getCurrent().getName());
					else if(online.getName().toLowerCase().startsWith(args[0].toLowerCase())) add(online.getName());
				}
			}
		};
	}

	private static boolean test1(CommandSender sender, final String[] args)
	{
		Player player = (Player) sender;

		for(Battle battle : Battle.Util.getAllActive())
			battle.end();

		player.sendMessage("All battles disabled!");

		return true;
	}

	private static boolean test2(CommandSender sender, final String[] args)
	{
		Player player = (Player) sender;

		if(Demigods.errorNoise) Errors.triggerError(ChatColor.GREEN + player.getName(), new ColoredStringBuilder().gray(" " + Unicodes.rightwardArrow() + " ").red("Test error.").build());

		return true;
	}

	private static boolean test3(CommandSender sender, final String[] args)
	{
		Player player = (Player) sender;

		player.getWorld().playEffect(player.getLocation(), Effect.RECORD_PLAY, Material.GREEN_RECORD.getId());
		player.sendMessage(ChatColor.YELLOW + "Now playing 'DVNO' by Justice...");

		return true;
	}

	private static boolean hspawn(CommandSender sender)
	{
		Player player = (Player) sender;

		// This SHOULD happen automatically, but bukkit doesn't do this, so we need to.

		if(player.isInsideVehicle() && player.getVehicle() instanceof Horse)
		{
			Horse horse = (Horse) player.getVehicle();
			horse.eject();
			horse.teleport(player.getWorld().getSpawnLocation());
			horse.setPassenger(player);
			player.sendMessage(ChatColor.YELLOW + "Teleported to spawn...");
		}

		return true;
	}
}
