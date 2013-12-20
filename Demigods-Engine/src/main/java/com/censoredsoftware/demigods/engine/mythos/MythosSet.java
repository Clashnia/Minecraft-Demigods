package com.censoredsoftware.demigods.engine.mythos;

import com.censoredsoftware.censoredlib.trigger.Trigger;
import com.censoredsoftware.demigods.engine.deity.Alliance;
import com.censoredsoftware.demigods.engine.deity.Deity;
import com.censoredsoftware.demigods.engine.structure.Structure;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;

import java.util.Collection;
import java.util.Set;

public class MythosSet implements Mythos
{
	private final Mythos PRIMARY;
	private final ImmutableSet<Mythos> SET;
	private final String[] INCOMPATIBLE;
	private final ImmutableSet<Alliance> ALLIANCES;
	private final ImmutableSet<Deity> DEITIES;
	private final ImmutableSet<Structure> STRUCTURES;
	private final ImmutableSet<Listener> LISTENERS;
	private final ImmutableSet<Permission> PERMISSIONS;
	private final ImmutableSet<Trigger> TRIGGERS;

	public MythosSet(Mythos primaryMythos, Set<Mythos> mythosSet)
	{
		PRIMARY = primaryMythos;
		SET = ImmutableSet.copyOf(Collections2.transform(mythosSet, new Function<Mythos, Mythos>()
		{
			@Override
			public Mythos apply(Mythos mythos)
			{
				mythos.setSecondary();
				return mythos;
			}
		}));

		Set<String> incompatibleSet = Sets.newHashSet();

		Set<Alliance> alliance = Sets.newHashSet();
		Set<Deity> deity = Sets.newHashSet();
		Set<Structure> structure = Sets.newHashSet();
		Set<Listener> listener = Sets.newHashSet();
		Set<Permission> permission = Sets.newHashSet();
		Set<Trigger> trigger = Sets.newHashSet();

		for(Mythos mythos : SET)
		{
			for(String incomp : mythos.getIncompatible())
				incompatibleSet.add(incomp);

			alliance.addAll(mythos.getAlliances());
			deity.addAll(mythos.getDeities());
			structure.addAll(mythos.getStructures());
			listener.addAll(mythos.getListeners());
			permission.addAll(mythos.getPermissions());
			trigger.addAll(mythos.getTriggers());
		}

		String[] incompatibleWorking = new String[incompatibleSet.size()];
		int count = 0;
		for(String incompatible : incompatibleSet)
		{
			incompatibleWorking[count] = incompatible;
			count++;
		}
		INCOMPATIBLE = incompatibleWorking;

		ALLIANCES = ImmutableSet.copyOf(alliance);
		DEITIES = ImmutableSet.copyOf(deity);
		STRUCTURES = ImmutableSet.copyOf(structure);
		LISTENERS = ImmutableSet.copyOf(listener);
		PERMISSIONS = ImmutableSet.copyOf(permission);
		TRIGGERS = ImmutableSet.copyOf(trigger);
	}

	@Override
	public String getTitle()
	{
		return PRIMARY.getTitle() + " and additional Mythos";
	}

	@Override
	public String getTagline()
	{
		return "A generated Mythos made to combine all secondary Mythos.";
	}

	@Override
	public String getAuthor()
	{
		return "Generated by Demigods";
	}

	@Override
	public boolean isPrimary()
	{
		return true;
	}

	@Override
	public boolean allowSecondary()
	{
		return PRIMARY.allowSecondary();
	}

	@Override
	public String[] getIncompatible()
	{
		return INCOMPATIBLE;
	}

	@Override
	public boolean useBaseGame()
	{
		return PRIMARY.useBaseGame();
	}

	@Override
	public Collection<Alliance> getAlliances()
	{
		return ALLIANCES;
	}

	@Override
	public Collection<Deity> getDeities()
	{
		return DEITIES;
	}

	@Override
	public Collection<Structure> getStructures()
	{
		return STRUCTURES;
	}

	@Override
	public boolean levelSeperateSkills()
	{
		return PRIMARY.levelSeperateSkills();
	}

	@Override
	public Collection<Listener> getListeners()
	{
		return LISTENERS;
	}

	@Override
	public Collection<Permission> getPermissions()
	{
		return PERMISSIONS;
	}

	@Override
	public Collection<Trigger> getTriggers()
	{
		return TRIGGERS;
	}

	@Override
	public void setSecondary()
	{}

	public boolean contains(final String mythosTitle)
	{
		return PRIMARY.getTitle().equals(mythosTitle) || Iterables.any(SET, new Predicate<Mythos>()
		{
			@Override
			public boolean apply(Mythos mythos)
			{
				return mythos.getTitle().equals(mythosTitle);
			}
		});
	}
}