package com.censoredsoftware.Demigods.Engine.Runnable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.censoredsoftware.Demigods.Engine.PlayerCharacter.PlayerCharacter;
import com.censoredsoftware.Demigods.Engine.Tracked.TrackedPlayer;

public class FavorRunnable implements Runnable
{
	private double multiplier;

	public FavorRunnable(double multiplier)
	{
		this.multiplier = multiplier;
	}

	@Override
	public void run()
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			PlayerCharacter character = TrackedPlayer.getTracked(player).getCurrent();
			if(character == null || !character.isImmortal()) continue;
			int regenRate = (int) Math.ceil(multiplier * character.getMeta().getAscensions());
			if(regenRate < 5) regenRate = 5;
			character.getMeta().addFavor(regenRate);
		}
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}
}
