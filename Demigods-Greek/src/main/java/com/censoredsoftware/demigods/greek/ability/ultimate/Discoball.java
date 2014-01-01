package com.censoredsoftware.demigods.greek.ability.ultimate;

import java.util.List;
import java.util.Set;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.censoredsoftware.censoredlib.util.Randoms;
import com.censoredsoftware.censoredlib.util.Spigots;
import com.censoredsoftware.demigods.engine.Demigods;
import com.censoredsoftware.demigods.engine.DemigodsPlugin;
import com.censoredsoftware.demigods.engine.data.CLocationManager;
import com.censoredsoftware.demigods.engine.data.Skill;
import com.censoredsoftware.demigods.engine.mythos.Ability;
import com.censoredsoftware.demigods.engine.util.Zones;
import com.censoredsoftware.demigods.greek.ability.GreekAbility;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class Discoball extends GreekAbility
{
	private static Set<FallingBlock> discoBalls = Sets.newHashSet();

	private final static String name = "Discoball of Doom", command = "discoball";
	private final static int cost = 30, delay = 30, repeat = 4;
	private final static List<String> details = Lists.newArrayList("Spread the music while causing destruction.");
	private final static Skill.Type type = Skill.Type.ULTIMATE;

	public Discoball(String deity)
	{
		super(name, command, deity, cost, delay, repeat, details, type, null, new Predicate<Player>()
		{
			@Override
			public boolean apply(final Player player)
			{
				if(!Ability.Util.preProcessAbility(player, cost)) return false;

				balls(player);

				player.sendMessage(ChatColor.YELLOW + "Dance!");

				Bukkit.getScheduler().scheduleSyncDelayedTask(DemigodsPlugin.plugin(), new Runnable()
				{
					@Override
					public void run()
					{
						player.sendMessage(ChatColor.RED + "B" + ChatColor.GOLD + "o" + ChatColor.YELLOW + "o" + ChatColor.GREEN + "g" + ChatColor.AQUA + "i" + ChatColor.LIGHT_PURPLE + "e" + ChatColor.DARK_PURPLE + " W" + ChatColor.BLUE + "o" + ChatColor.RED + "n" + ChatColor.GOLD + "d" + ChatColor.YELLOW + "e" + ChatColor.GREEN + "r" + ChatColor.AQUA + "l" + ChatColor.LIGHT_PURPLE + "a" + ChatColor.DARK_PURPLE + "n" + ChatColor.BLUE + "d" + ChatColor.RED + "!");
					}
				}, 40);

				return true;
			}
		}, new Listener()
		{
			@EventHandler(priority = EventPriority.HIGHEST)
			public void onBlockChange(EntityChangeBlockEvent changeEvent)
			{
				if(Zones.inNoDemigodsZone(changeEvent.getBlock().getLocation())) return;

				if(changeEvent.getEntityType() != EntityType.FALLING_BLOCK) return;
				changeEvent.getBlock().setType(Material.AIR);
				FallingBlock block = (FallingBlock) changeEvent.getEntity();
				if(discoBalls.contains(block))
				{
					discoBalls.remove(block);
					block.remove();
				}
			}
		}, new Runnable()
		{
			@Override
			public void run()
			{
				for(FallingBlock block : discoBalls)
				{
					if(block != null)
					{
						Location location = block.getLocation();
						if(Zones.inNoDemigodsZone(location)) return;
						playRandomNote(location, 2F);
						sparkleSparkle(location);
						destoryNearby(location);
					}
				}
			}
		});
	}

	public static void balls(Player player)
	{
		for(Location location : CLocationManager.getCirclePoints(new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() + 30 < 256 ? player.getLocation().getBlockY() + 30 : 256, player.getLocation().getBlockZ()), 3.0, 50))
			spawnBall(location);
	}

	public static void spawnBall(Location location)
	{
		final FallingBlock discoBall = location.getWorld().spawnFallingBlock(location, Material.GLOWSTONE, (byte) 0);
		discoBalls.add(discoBall);
		Bukkit.getScheduler().scheduleSyncDelayedTask(DemigodsPlugin.plugin(), new BukkitRunnable()
		{
			@Override
			public void run()
			{
				discoBalls.remove(discoBall);
				discoBall.remove();
			}
		}, 600);
	}

	public static void rainbow(Player disco, Player player)
	{
		player.sendBlockChange(disco.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation(), Material.WOOL, (byte) Randoms.generateIntRange(0, 15));
		if(Demigods.Util.isRunningSpigot()) Spigots.playParticle(disco.getLocation(), Effect.COLOURED_DUST, 1, 0, 1, 10F, 100, 30);
	}

	public static void playRandomNote(Location location, float volume)
	{
		location.getWorld().playSound(location, Sound.NOTE_BASS_GUITAR, volume, (float) ((double) Randoms.generateIntRange(5, 10) / 10.0));
	}

	public static void sparkleSparkle(Location location)
	{
		if(Demigods.Util.isRunningSpigot()) Spigots.playParticle(location, Effect.CRIT, 1, 1, 1, 10F, 1000, 30);
	}

	public static void destoryNearby(Location location)
	{
		location.getWorld().createExplosion(location, 2F);
	}
}
