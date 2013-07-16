package com.censoredsoftware.Demigods.Engine.Runnable;

import org.bukkit.scheduler.BukkitRunnable;

import com.censoredsoftware.Demigods.Engine.Object.Battle.Battle;
import com.censoredsoftware.Demigods.Engine.Utility.BattleUtility;

public class BattleRunnable extends BukkitRunnable
{
	@Override
	public void run()
	{
		for(Battle battle : Battle.getAll())
		{
			if(battle.getStartTime() + (battle.getDuration()) <= System.currentTimeMillis()) battle.delete();
			else BattleUtility.battleBorder(battle);
		}
	}
}