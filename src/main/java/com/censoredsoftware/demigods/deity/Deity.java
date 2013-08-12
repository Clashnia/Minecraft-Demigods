package com.censoredsoftware.demigods.deity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.censoredsoftware.demigods.Elements;
import com.censoredsoftware.demigods.ability.Ability;
import com.censoredsoftware.demigods.player.DCharacter;
import com.censoredsoftware.demigods.player.DPlayer;

public interface Deity
{
	public String getName();

	public String getAlliance();

	public String getPermission();

	public ChatColor getColor();

	public Set<Material> getClaimItems();

	public List<String> getLore();

	public Deity.Type getType();

	public Set<Ability> getAbilities();

	public enum Type
	{
		TIER1, TIER2, TIER3
	}

	@Override
	public String toString();

	public static class Util
	{
		public static Set<String> getLoadedDeityAlliances()
		{
			return new HashSet<String>()
			{
				{
					for(Elements.ListedDeity deity : Elements.Deities.values())
					{
						if(!contains(deity.getDeity().getAlliance())) add(deity.getDeity().getAlliance());
					}
				}
			};
		}

		public static Set<Deity> getAllDeitiesInAlliance(final String alliance)
		{
			return new HashSet<Deity>()
			{
				{
					for(Elements.ListedDeity deity : Elements.Deities.values())
					{
						if(deity.getDeity().getAlliance().equalsIgnoreCase(alliance)) add(deity.getDeity());
					}
				}
			};
		}

		public static Deity getDeity(String deity)
		{
			return Elements.Deities.get(deity);
		}

		public static boolean canUseDeity(Player player, String deity)
		{
			DCharacter character = DPlayer.Util.getPlayer(player).getCurrent();
			if(character == null || !character.isImmortal())
			{
				player.sendMessage(ChatColor.RED + "You can't do that, mortal!");
				return false;
			}
			else if(!character.isDeity(deity))
			{
				player.sendMessage(ChatColor.RED + "You haven't claimed " + deity + "! You can't do that!");
				return false;
			}
			return true;
		}

		public static boolean canUseDeitySilent(Player player, String deity)
		{
			DCharacter character = DPlayer.Util.getPlayer(player).getCurrent();
			return character != null && character.isImmortal() && character.isDeity(deity);
		}
	}
}