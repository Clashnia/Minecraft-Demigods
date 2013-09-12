package com.censoredsoftware.demigods;

import com.censoredsoftware.demigods.ability.Ability;
import com.censoredsoftware.demigods.command.DevelopmentCommands;
import com.censoredsoftware.demigods.command.GeneralCommands;
import com.censoredsoftware.demigods.command.MainCommand;
import com.censoredsoftware.demigods.conversation.Prayer;
import com.censoredsoftware.demigods.data.ThreadManager;
import com.censoredsoftware.demigods.helper.QuitReasonHandler;
import com.censoredsoftware.demigods.helper.WrappedCommand;
import com.censoredsoftware.demigods.helper.WrappedConversation;
import com.censoredsoftware.demigods.item.DivineItem;
import com.censoredsoftware.demigods.language.Translation;
import com.censoredsoftware.demigods.listener.*;
import com.censoredsoftware.demigods.player.DCharacter;
import com.censoredsoftware.demigods.structure.Structure;
import com.censoredsoftware.demigods.util.Configs;
import com.censoredsoftware.demigods.util.ItemValues;
import com.censoredsoftware.demigods.util.Messages;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.mcstats.MetricsLite;

import java.util.Set;

public class Demigods
{
	// Constants
	public static String SAVE_PATH;

	// Public Static Access
	public static final DemigodsPlugin PLUGIN;
	public static final ConversationFactory CONVERSATION_FACTORY;
	public static final Translation LANGUAGE;

	// Disabled Stuff
	public static ImmutableSet<String> DISABLED_WORLDS;
	public static ImmutableSet<String> COMMANDS;

	// Load what is possible to load right away.
	static
	{
		// Allow static access.
		PLUGIN = (DemigodsPlugin) Bukkit.getServer().getPluginManager().getPlugin("Demigods");
		CONVERSATION_FACTORY = new ConversationFactory(PLUGIN);
		LANGUAGE = new Translation();

		// Initialize metrics
		try
		{
			(new MetricsLite(PLUGIN)).start();
		}
		catch(Exception ignored)
		{}
	}

	// Load everything else.
	protected static void load()
	{
		// Start the data
		SAVE_PATH = PLUGIN.getDataFolder() + "/data/"; // Don't change this.

		// Check if there are no enabled worlds
		if(!loadWorlds())
		{
			Messages.severe("Demigods was unable to load any worlds.");
			Messages.severe("Please enable at least 1 world.");
			PLUGIN.getServer().getPluginManager().disablePlugin(PLUGIN);
		}

		// Load listeners and commands
		loadListeners();
		loadCommands();

		// Update usable characters
		DCharacter.Util.updateUsableCharacters();

		// Start threads
		ThreadManager.startThreads();

		// Regenerate structures
		Structure.Util.regenerateStructures();

		// Initialize tribute tracking
		ItemValues.initializeTributeTracking();

		if(MiscUtil.isRunningSpigot()) Messages.info(("Spigot found, will use extra API features."));
		else Messages.warning(("Without Spigot, some features may not work."));
	}

	private static boolean loadWorlds()
	{
		Set<String> disabledWorlds = Sets.newHashSet();
		for(String world : Collections2.filter(Configs.getSettingArrayListString("restrictions.disabled_worlds"), new Predicate<String>()
		{
			@Override
			public boolean apply(String world)
			{
				return PLUGIN.getServer().getWorld(world) != null;
			}
		}))
			if(PLUGIN.getServer().getWorld(world) != null) disabledWorlds.add(world);
		DISABLED_WORLDS = ImmutableSet.copyOf(disabledWorlds);
		return PLUGIN.getServer().getWorlds().size() != DISABLED_WORLDS.size();
	}

	private static void loadListeners()
	{
		PluginManager register = Bukkit.getServer().getPluginManager();

		// Engine
		for(ListedListener listener : ListedListener.values())
			register.registerEvents(listener.getListener(), PLUGIN);

		// Disabled worlds
		if(!DISABLED_WORLDS.isEmpty()) register.registerEvents(new ZoneListener(), PLUGIN);

		// Abilities
		for(Ability ability : Ability.Util.getLoadedAbilities())
			if(ability.getListener() != null) register.registerEvents(ability.getListener(), PLUGIN);

		// Structures
		for(Structure.Type structure : Sets.filter(Sets.newHashSet(Structure.Type.values()), new Predicate<Structure.Type>()
		{
			@Override
			public boolean apply(Structure.Type structure)
			{
				return structure.getUniqueListener() != null;
			}
		}))
			if(structure.getUniqueListener() != null) register.registerEvents(structure.getUniqueListener(), PLUGIN);

		// Conversations
		for(WrappedConversation conversation : Collections2.filter(Collections2.transform(Sets.newHashSet(ListedConversation.values()), new Function<ListedConversation, WrappedConversation>()
		{
			@Override
			public WrappedConversation apply(ListedConversation conversation)
			{
				return conversation.getConversation();
			}
		}), new Predicate<WrappedConversation>()
		{
			@Override
			public boolean apply(WrappedConversation conversation)
			{
				return conversation.getUniqueListener() != null;
			}
		}))
			if(conversation.getUniqueListener() != null) register.registerEvents(conversation.getUniqueListener(), PLUGIN);

		// Special Items
		for(DivineItem divineItem : DivineItem.values())
		{
			if(divineItem.getSpecialItem().getUniqueListener() != null) register.registerEvents(divineItem.getSpecialItem().getUniqueListener(), PLUGIN);
			if(divineItem.getSpecialItem().getRecipe() != null) PLUGIN.getServer().addRecipe(divineItem.getSpecialItem().getRecipe());
		}

		// Quit reason.
		Bukkit.getServer().getLogger().addHandler(new QuitReasonHandler());
	}

	private static void loadCommands()
	{
		Set<String> commands = Sets.newHashSet();
		for(ListedCommand command : ListedCommand.values())
			commands.addAll(command.getCommand().getCommands());
		commands.add("demigod");
		commands.add("dg");
		commands.add("c");
		commands.add("o");
		commands.add("l");
		commands.add("a");
		commands.add("v");
		COMMANDS = ImmutableSet.copyOf(commands);
	}

	public static class MiscUtil
	{
		public static boolean isRunningSpigot()
		{
			try
			{
				Bukkit.getServer().getWorlds().get(0).spigot();
				return true;
			}
			catch(Throwable ignored)
			{}
			return false;
		}

		public static boolean isDemigodsCommand(String command)
		{
			return COMMANDS.contains(command);
		}
	}

	// Conversations
	public enum ListedConversation
	{
		PRAYER(new Prayer());

		private final WrappedConversation conversationInfo;

		private ListedConversation(WrappedConversation conversationInfo)
		{
			this.conversationInfo = conversationInfo;
		}

		public WrappedConversation getConversation()
		{
			return this.conversationInfo;
		}

		// Can't touch this. Naaaaaa na-na-na.. Ba-dum, ba-dum.
		public static interface Category extends Prompt
		{
			public String getChatName(ConversationContext context);

			public boolean canUse(ConversationContext context);
		}
	}

	// Listeners
	public enum ListedListener
	{
		BATTLE(new BattleListener()), CHAT(new ChatListener()), ENTITY(new EntityListener()), FLAG(new FlagListener()), GRIEF(new GriefListener()), PLAYER(new PlayerListener()), TRIBUTE(new TributeListener());

		private Listener listener;

		private ListedListener(Listener listener)
		{
			this.listener = listener;
		}

		public Listener getListener()
		{
			return listener;
		}
	}

	// Commands
	public enum ListedCommand
	{
		MAIN(new MainCommand()), GENERAL(new GeneralCommands()), DEVELOPMENT(new DevelopmentCommands());

		private WrappedCommand command;

		private ListedCommand(WrappedCommand command)
		{
			this.command = command;
		}

		public WrappedCommand getCommand()
		{
			return command;
		}
	}
}
