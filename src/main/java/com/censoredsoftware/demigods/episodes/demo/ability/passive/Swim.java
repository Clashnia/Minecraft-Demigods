package com.censoredsoftware.demigods.episodes.demo.ability.passive;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.censoredsoftware.core.util.StopWatches;
import com.censoredsoftware.demigods.engine.Demigods;
import com.censoredsoftware.demigods.engine.element.Ability;
import com.censoredsoftware.demigods.engine.element.Deity;

public class Swim extends Ability
{
	private final static String name = "Swim", command = null;
	private final static int cost = 0, delay = 0, repeat = 20;
	private static Info info;
	private final static Devotion.Type type = Devotion.Type.PASSIVE;
	private final static List<String> details = new ArrayList<String>(1)
	{
		{
			add("Crouch while in water to swim very fast.");
		}
	};

	public Swim(final String deity, String permission)
	{
		super(info = new Info(deity, name, command, permission, cost, delay, repeat, details, type), new Listener()
		{
			@EventHandler(priority = EventPriority.HIGHEST)
			private void onPlayerMoveEvent(PlayerMoveEvent event)
			{
				StopWatch stopWatch = StopWatches.start(); // TODO

				Player player = event.getPlayer();

				if(!player.isSneaking() || !Deity.Util.canUseDeitySilent(player, deity)) return;

				// PHELPS SWIMMING
				if((player.getLocation().getBlock().getType().equals(Material.STATIONARY_WATER) || player.getLocation().getBlock().getType().equals(Material.WATER)))
				{
					Vector direction = player.getLocation().getDirection().normalize().multiply(1.3D);
					Vector victor = new Vector(direction.getX(), direction.getY(), direction.getZ());
					player.setVelocity(victor);
				}

				Demigods.message.broadcast("Swimming:" + StopWatches.end(stopWatch)); // TODO
			}
		}, null);
	}
}