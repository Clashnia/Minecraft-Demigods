package com.legit2.Demigods.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.legit2.Demigods.Demigods;
import com.legit2.Demigods.Utilities.DCharUtil;
import com.legit2.Demigods.Utilities.DDataUtil;
import com.legit2.Demigods.Utilities.DDeityUtil;
import com.legit2.Demigods.Utilities.DObjUtil;
import com.legit2.Demigods.Utilities.DPlayerUtil;
import com.legit2.Demigods.Utilities.DMiscUtil;
import com.legit2.Demigods.Utilities.DZoneUtil;

public class DAltarListener implements Listener
{
	static Demigods plugin;
	
	public DAltarListener(Demigods instance)
	{
		plugin = instance;
	}
	
	/* --------------------------------------------
	 *  Handle Altar Interactions
	 * --------------------------------------------
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void altarInteract(PlayerInteractEvent event)
	{
		// Define variables
		Player player = event.getPlayer();
		Location location = player.getLocation();

		// First we check if the player is in an Altar and return if not
		if(DZoneUtil.zoneAltar(location) != null)
		{
			// Player is in an altar, let's do this
			if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

			if(event.getClickedBlock().getType().equals(Material.ENCHANTMENT_TABLE) && !DPlayerUtil.isPraying(player))
			{
				DMiscUtil.togglePlayerChat(player, false);
				DMiscUtil.togglePlayerStuck(player, true);
				DPlayerUtil.togglePraying(player, true);
				
				// First we clear chat
				clearChat(player);
				
				// Tell nearby players that the user is praying
				for(Entity entity : player.getNearbyEntities(16, 16, 16))
				{
					if(entity instanceof Player) ((Player) entity).sendMessage(ChatColor.AQUA + player.getName() + " has knelt at an Altar");
				}
				
				player.sendMessage(" ");
				player.sendMessage(ChatColor.AQUA + "-- Now Praying ----------------------------------------");
				player.sendMessage(" ");
				player.sendMessage(ChatColor.GRAY + " While using an Altar you are unable to move or chat.");
				player.sendMessage(ChatColor.GRAY + " You can return to the main menu at anytime by typing \"menu\".");
				player.sendMessage(ChatColor.GRAY + " Right-click the Altar again to stop Praying.");
				player.sendMessage(" ");
				
				altarMenu(player);

				event.setCancelled(true);
				return;
			}
			else if(event.getClickedBlock().getType().equals(Material.ENCHANTMENT_TABLE) && DPlayerUtil.isPraying(player))
			{
				DMiscUtil.togglePlayerChat(player, true);
				DMiscUtil.togglePlayerStuck(player, false);
				DPlayerUtil.togglePraying(player, false);
				
				// Clear whatever is being worked on in this Pray session
				DDataUtil.removePlayerData(player, "temp_createchar");
				
				// First we clear chat
				clearChat(player);
				
				player.sendMessage(" ");
				player.sendMessage(ChatColor.GRAY + "         Your movement and chat have been re-enabled.");
				player.sendMessage(" ");
				player.sendMessage(" ");
				player.sendMessage(" ");
				player.sendMessage(ChatColor.AQUA + "-- No Longer Praying ----------------------------------");
				player.sendMessage(" ");

				event.setCancelled(true);
				return;
			}
		}
		return;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void altarChatEvent(AsyncPlayerChatEvent event)
	{
		// Define variables
		Player player = event.getPlayer();
		Location location = player.getLocation();
		
		// First we check if the player is in an Altar and currently praying, if not we'll return
		if(DZoneUtil.zoneAltar(location) != null && DPlayerUtil.isPraying(player))
		{
			// Cancel their chat
			event.setCancelled(true);
			
			// Define variables
			String message = event.getMessage();
			
			// Return to main menu
			if(message.equalsIgnoreCase("menu") || message.equalsIgnoreCase("exit"))
			{
				// Remove now useless data
				DDataUtil.removePlayerData(player, "temp_createchar");
				DDataUtil.removePlayerData(player, "temp_altar_previous");
				
				clearChat(player);
				
				player.sendMessage(ChatColor.YELLOW + " -> Main Menu ----------------------------------------");
				player.sendMessage(" ");

				altarMenu(player);
				return;	
			}
			
			// Create Character
			if(message.equals("1") || message.contains("create") && message.contains("character"))
			{
				clearChat(player);
				
				player.sendMessage(ChatColor.YELLOW + " -> Creating Character --------------------------------");
				player.sendMessage(" ");
				chooseName(player);
				return;
			}
						
				/*
				 *  Character creation sub-steps
				 */
				if(DDataUtil.hasPlayerData(player, "temp_createchar"))
				{
					// Step 1 of character creation
					if(DDataUtil.getPlayerData(player, "temp_createchar").equals("choose_name"))
					{
						confirmName(player, message);
						return;
					}
					
					// Step 2 of character creation
					if(DDataUtil.getPlayerData(player, "temp_createchar").equals("confirm_name"))
					{
						if(message.equalsIgnoreCase("y") || message.contains("yes"))
						{
							chooseDeity(player);
							return;
						}
						else
						{
							chooseName(player);
							return;
						}
					}
					
					// Step 3 of character creation
					if(DDataUtil.getPlayerData(player, "temp_createchar").equals("choose_deity"))
					{
						confirmDeity(player, message);
						return;
					}
					
					// Step 4 of character creation
					if(DDataUtil.getPlayerData(player, "temp_createchar").equals("confirm_deity"))
					{
						if(message.equalsIgnoreCase("y") || message.contains("yes"))
						{
							deityConfirmed(player);
							return;
						}
						else
						{
							chooseDeity(player);
							return;
						}
					}
	
					// Step 5 of character creation
					if(DDataUtil.getPlayerData(player, "temp_createchar").equals("confirm_all"))
					{
						if(message.equalsIgnoreCase("y") || message.contains("yes"))
						{
							Inventory ii = DMiscUtil.getPlugin().getServer().createInventory(player, 27, "Place Your Tributes Here");
							player.openInventory(ii);
						}
						else
						{
							player.sendMessage(ChatColor.AQUA + "  Once you have the items return here again.");
							return;
						}
					}
				}
			
			// Finish Create Character
			if(message.equals("1a") || message.contains("confirm") && message.contains("character") && DDataUtil.hasPlayerData(player, "temp_createchar_finalstep"))
			{
				clearChat(player);
				
				finalConfirmDeity(player);
				return;
			}
						
			// Remove Character
			else if(message.equals("2") || message.contains("remove") && message.contains("character"))
			{
				clearChat(player);
				
				player.sendMessage(ChatColor.RED + " -> Removing Characters -------------------------------");
				player.sendMessage(" ");
				player.sendMessage(ChatColor.GRAY + "  Currently Unavailable. Use /removechar <name>");
				player.sendMessage(" ");
				return;	
			}
			
			// Remove Character
			else if(message.equals("3") || message.contains("view") && message.contains("characters"))
			{
				clearChat(player);
				
				player.sendMessage(ChatColor.YELLOW + " -> Viewing Characters --------------------------------");
				player.sendMessage(" ");

				viewChars(player);
				return;	
			}
			else if(message.contains("info"))
			{
				clearChat(player);

				// Define variables
				String charName = message.replace(" info", "").trim();
				int charID = DCharUtil.getCharByName(player, charName);
				
				viewChar(player, charID);
				return;	
			}
			
			// Switch Character
			else if(message.equals("4") || message.contains("switch") && message.contains("character"))
			{
				clearChat(player);

				player.sendMessage(ChatColor.YELLOW + " -> Switch Characters ---------------------------------");
				player.sendMessage(" ");
				player.sendMessage(ChatColor.GRAY + "  Currently Unavailable. Use /switchchar <name>");
				player.sendMessage(" ");
				return;	
			}
		}
		return;
	}
	
	// Method for use within Altars
	private void altarMenu(Player player)
	{
		player.sendMessage(ChatColor.GRAY + " To begin, choose an option by entering it's number in the chat:");
		player.sendMessage(" ");
		if(DDataUtil.hasPlayerData(player, "temp_createchar_finalstep") && DDataUtil.getPlayerData(player, "temp_createchar_finalstep").equals(true))
		{
			player.sendMessage(ChatColor.GRAY + "   [1a.] " + ChatColor.GREEN + "Confirm New Character");	
		}
		else player.sendMessage(ChatColor.GRAY + "   [1.] " + ChatColor.GREEN + "Create New Character");
		
		player.sendMessage(ChatColor.GRAY + "   [2.] " + ChatColor.RED + "Remove Character");
		player.sendMessage(ChatColor.GRAY + "   [3.] " + ChatColor.GOLD + "View Characters");
		player.sendMessage(ChatColor.GRAY + "   [4.] " + ChatColor.GOLD + "Switch Character");
		player.sendMessage(" ");
	}
	
	// View characters
	@SuppressWarnings("unused")
	private void viewChars(Player player)
	{
		DDataUtil.savePlayerData(player, "temp_altar_previous", "view_chars");

		player.sendMessage(ChatColor.LIGHT_PURPLE + "  Light purple " + ChatColor.GRAY + "represents your current character.");
		player.sendMessage(" ");
		
		List<Integer> chars = DPlayerUtil.getChars(player);
		
		for(Integer charID : chars)
		{
			String color = "";
			String name = DCharUtil.getName(charID);
			String deity = DCharUtil.getDeity(charID);
			int favor = DCharUtil.getFavor(charID);
			int maxFavor = DCharUtil.getMaxFavor(charID);
			ChatColor favorColor = DCharUtil.getFavorColor(charID);
			int devotion = DCharUtil.getDevotion(charID);
			int ascensions = DCharUtil.getAscensions(charID);
			
			if(DPlayerUtil.getCurrentChar(player) == charID) color = ChatColor.LIGHT_PURPLE + "";

			player.sendMessage(ChatColor.GRAY + "  " + ChatColor.GRAY + color + name + ChatColor.GRAY + " [" + DDeityUtil.getDeityColor(deity) + deity + ChatColor.GRAY + " / Favor: " + favorColor + favor + ChatColor.GRAY + " (of " + ChatColor.GREEN + maxFavor + ChatColor.GRAY + ") / Ascensions: " + ChatColor.GREEN + ascensions + ChatColor.GRAY + "]");
		}
		
		player.sendMessage(" ");
		player.sendMessage(ChatColor.GRAY + "  Type" + ChatColor.YELLOW + " <character name> info" + ChatColor.GRAY + " for detailed information.");
		player.sendMessage(" ");
		return;
	}
	
	private void viewChar(Player player, int charID)
	{
		player.sendMessage(ChatColor.YELLOW + " -> Viewing Character ---------------------------------");
		player.sendMessage(" ");

		String currentCharMsg = ChatColor.RED + "" + ChatColor.ITALIC + "(Inactive) " + ChatColor.RESET;
		String name = DCharUtil.getName(charID);
		String deity = DCharUtil.getDeity(charID);
		ChatColor deityColor = DDeityUtil.getDeityColor(deity);
		String alliance = DCharUtil.getAlliance(charID);
		int hp = DCharUtil.getHP(charID);
		int maxHP = DCharUtil.getMaxHP(charID);
		ChatColor hpColor = DCharUtil.getHPColor(charID);
		int exp = Math.round(DCharUtil.getExp(charID));
		int favor = DCharUtil.getFavor(charID);
		int maxFavor = DCharUtil.getMaxFavor(charID);
		ChatColor favorColor = DCharUtil.getFavorColor(charID);
		int devotion = DCharUtil.getDevotion(charID);
		int devotionGoal = DCharUtil.getDevotionGoal(charID);
		int ascensions = DCharUtil.getAscensions(charID);
		double lastX = Math.floor(DCharUtil.getX(charID));
		double lastY = Math.floor(DCharUtil.getY(charID));
		double lastZ = Math.floor(DCharUtil.getZ(charID));
		String locString = "(" + lastX + ", " + lastY + ", " + lastZ + ")";
		
		if(DPlayerUtil.getCurrentChar(player) == charID) currentCharMsg = ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + "(Active) " + ChatColor.RESET;

		player.sendMessage("    " + currentCharMsg + ChatColor.YELLOW + name + ChatColor.GRAY + " > Allied to " + deityColor + deity + ChatColor.GRAY + " of the " + ChatColor.GOLD + alliance + "s");
		player.sendMessage(ChatColor.GRAY + "  --------------------------------------------------");
		player.sendMessage(ChatColor.GRAY + "    Health: " + ChatColor.WHITE + hpColor + hp + ChatColor.GRAY + " (of " + ChatColor.GREEN + maxHP + ChatColor.GRAY + ")");
		player.sendMessage(ChatColor.GRAY + "    Experience: " + ChatColor.WHITE + exp);
		player.sendMessage(ChatColor.GRAY + "    Location: " + ChatColor.WHITE + locString);
		player.sendMessage(" ");
		player.sendMessage(ChatColor.GRAY + "    Ascensions: " + ChatColor.GREEN + ascensions);
		player.sendMessage(ChatColor.GRAY + "    Devotion: " + ChatColor.WHITE + devotion + ChatColor.GRAY + " (" + ChatColor.YELLOW + (devotionGoal - devotion) + ChatColor.GRAY + " until next Ascension)");		
		player.sendMessage(ChatColor.GRAY + "    Favor: " + favorColor + favor + ChatColor.GRAY + " (of " + ChatColor.GREEN + maxFavor + ChatColor.GRAY + ")");
		player.sendMessage(" ");

		return;
	}
	
	// Choose name
	private void chooseName(Player player)
	{
		DDataUtil.savePlayerData(player, "temp_createchar", "choose_name");
		player.sendMessage(ChatColor.AQUA + "  Enter a name: " + ChatColor.GRAY + "(Alpha-Numeric Only)");
		player.sendMessage(" ");
		return;
	}
	
	// Name confirmation
	private void confirmName(Player player, String message)
	{
		if(message.length() >= 15 || !StringUtils.isAlphanumeric(message) || DPlayerUtil.hasCharName(player, message))
		{
			// Validate the name
			DDataUtil.savePlayerData(player, "temp_createchar", "choose_name");
			if(message.length() >= 15) player.sendMessage(ChatColor.RED + "  That name is too long.");
			if(!StringUtils.isAlphanumeric(message)) player.sendMessage(ChatColor.RED + "  You can only use Alpha-Numeric characters.");
			if(DPlayerUtil.hasCharName(player, message)) player.sendMessage(ChatColor.RED + "  You already have a character with that name.");
			player.sendMessage(ChatColor.AQUA + "  Enter a different name: " + ChatColor.GRAY + "(Alpha-Numeric Only)");
			player.sendMessage(" ");
			return;
		}
		else
		{
			DDataUtil.savePlayerData(player, "temp_createchar", "confirm_name");
			String chosenName = message.replace(" ", "");
			player.sendMessage(ChatColor.AQUA + "  Are you sure you want to use " + ChatColor.YELLOW + chosenName + ChatColor.AQUA + "?" + ChatColor.GRAY + " (y/n)");
			player.sendMessage(" ");
			DDataUtil.savePlayerData(player, "temp_createchar_name", chosenName);
			return;
		}
	}
	
	// Choose deity
	private void chooseDeity(Player player)
	{
		player.sendMessage(ChatColor.AQUA + "  Please choose a Deity: " + ChatColor.GRAY + "(Type in the name of the Deity)");
		for(String alliance : DDeityUtil.getLoadedDeityAlliances())
		{
			for(String deity : DDeityUtil.getAllDeitiesInAlliance(alliance)) player.sendMessage(ChatColor.GRAY + "  -> " + ChatColor.YELLOW + DObjUtil.capitalize(deity)  + ChatColor.GRAY + " (" + alliance + ")");	
		}
		player.sendMessage(" ");

		DDataUtil.savePlayerData(player, "temp_createchar", "choose_deity");
	}
	
	// Deity confirmation
	private void confirmDeity(Player player, String message)
	{
		// Check their chosen Deity
		for(String alliance : DDeityUtil.getLoadedDeityAlliances())
		{
			for(String deity : DDeityUtil.getAllDeitiesInAlliance(alliance))
			{
				if(message.equalsIgnoreCase(deity))
				{
					// Their chosen deity matches an existing deity, ask for confirmation
					String chosenDeity = message.replace(" ", "");
					player.sendMessage(ChatColor.AQUA + "  Are you sure you want to use " + ChatColor.YELLOW + DObjUtil.capitalize(chosenDeity) + ChatColor.AQUA + "?" + ChatColor.GRAY + " (y/n)");
					player.sendMessage(" ");
					DDataUtil.savePlayerData(player, "temp_createchar_deity", chosenDeity);
					DDataUtil.savePlayerData(player, "temp_createchar", "confirm_deity");
					return;
				}
			}
		}
		if(message.equalsIgnoreCase("_Alex"))
		{
			player.sendMessage(ChatColor.AQUA + "  Well you can't be _Alex... but he is awesome!");
			player.sendMessage(" ");

			// They can't be _Alex silly! Make them re-choose
			chooseDeity(player);
			return;
		}
	}
	
	// Confirmed deity
	@SuppressWarnings("unchecked")
	private void deityConfirmed(Player player)
	{
		// Define variables
		String chosenDeity = (String) DDataUtil.getPlayerData(player, "temp_createchar_deity");
		
		// They accepted the Deity choice, now ask them to input their items so they can be accepted
		player.sendMessage(ChatColor.AQUA + "  Before you can confirm your lineage with " + ChatColor.YELLOW + chosenDeity + ChatColor.AQUA + ", you must");
		player.sendMessage(ChatColor.AQUA + "  first sacrifice the following items:");
		player.sendMessage(" ");
		for(Material item : (ArrayList<Material>) DDataUtil.getPluginData("temp_deity_claim_items", chosenDeity))
		{
			player.sendMessage(ChatColor.GRAY + "  -> " + ChatColor.YELLOW + item.name());
		}
		player.sendMessage(" ");
		player.sendMessage(ChatColor.GRAY + "  After you obtain these items, return to an Altar and select");
		player.sendMessage(ChatColor.GRAY + "  the option to confirm your new character.");
		player.sendMessage(" ");

		DDataUtil.savePlayerData(player, "temp_createchar_finalstep", true);
		return;
	}
	
	// Final confirmation of deity
	@SuppressWarnings("unchecked")
	private void finalConfirmDeity(Player player)
	{
		// Define variables
		String chosenDeity = (String) DDataUtil.getPlayerData(player, "temp_createchar_deity");

		// Save data
		DDataUtil.savePlayerData(player, "temp_createchar_finalstep", true);
		DDataUtil.savePlayerData(player, "temp_createchar", "confirm_all");
		
		// Send them the chat
		player.sendMessage(ChatColor.GREEN + " -> Confirming Character -------------------------------");
		player.sendMessage(" ");
		player.sendMessage(ChatColor.AQUA + "  Do you have the following items in your inventory?" + ChatColor.GRAY + " (y/n)");
		player.sendMessage(" ");
		for(Material item : (ArrayList<Material>) DDataUtil.getPluginData("temp_deity_claim_items", chosenDeity))
		{
			player.sendMessage(ChatColor.GRAY + "  -> " + ChatColor.YELLOW + item.name());
		}
		player.sendMessage(" ");
	}
	
	@SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.MONITOR)
	public void createCharacter(InventoryCloseEvent event)
	{
		try
		{
			if(!(event.getPlayer() instanceof Player)) return;
			Player player = (Player) event.getPlayer();

			// If it isn't a confirmation chest then exit
			if(!event.getInventory().getName().contains("Place Your Tributes Here")) return;
						
			// Exit if this isn't for character creation
			if(!DPlayerUtil.isPraying(player) || !DDataUtil.hasPlayerData(player, "temp_createchar_finalstep") || DDataUtil.getPlayerData(player, "temp_createchar_finalstep").equals(false))
			{
				player.sendMessage(ChatColor.RED + "(ERR: 2003) Please report this to an admin immediately.");
				return;
			}
			
			// Define variables
			String chosenName = (String) DDataUtil.getPlayerData(player, "temp_createchar_name");
			String chosenDeity = (String) DDataUtil.getPlayerData(player, "temp_createchar_deity");
			String deityAlliance = DObjUtil.capitalize(DDeityUtil.getDeityAlliance(chosenDeity));
			
			// Check the chest items
			int items = 0;
			int neededItems = ((ArrayList<Material>) DDataUtil.getPluginData("temp_deity_claim_items", chosenDeity)).size();
		
			for(ItemStack ii : event.getInventory().getContents())
			{
				if(ii != null)
				{
					for(Material item : (ArrayList<Material>) DDataUtil.getPluginData("temp_deity_claim_items", chosenDeity))
					{
						if(ii.getType().equals(item))
						{
							items++;
						}
					}
				}
			}
			
			player.sendMessage(ChatColor.YELLOW + "The " + deityAlliance + "s are pondering your offerings...");
			if(neededItems == items)
			{
				// They were accepted, finish everything up!
				DCharUtil.createChar(player, chosenName, chosenDeity);				
				DDataUtil.removePlayerData(player, "temp_createchar");
				player.sendMessage(ChatColor.GREEN + "You have been accepted into the lineage of " + chosenDeity + "!");
				player.getWorld().strikeLightningEffect(player.getLocation());
				for (int i=0;i<20;i++) player.getWorld().spawn(player.getLocation(), ExperienceOrb.class);
				
				// Stop their praying, enable movement, enable chat
				DMiscUtil.togglePlayerChat(player, true);
				DMiscUtil.togglePlayerStuck(player, false);
				DPlayerUtil.togglePraying(player, false);
				
				// Give them their Shrine Instructional Booklet(C)
				ItemStack shrineBook = new ItemStack(Material.WRITTEN_BOOK, 1);

				//String shrineBookName = "The Book of " + chosenDeity;
				ArrayList<String> shrineBookLore = new ArrayList<String>();
				shrineBookLore.add("Use this to create a Shrine.");
				
				ItemMeta shrineBookMeta = shrineBook.getItemMeta();
				shrineBookMeta.setLore(shrineBookLore);
				shrineBook.setItemMeta(shrineBookMeta);
				
				//BookMeta bookMeta = shrineBook;

				player.getInventory().addItem(shrineBook);
				
				// Remove old data now
				DDataUtil.removePlayerData(player, "temp_createchar_finalstep");
				DDataUtil.removePlayerData(player, "temp_createchar_name");
				DDataUtil.removePlayerData(player, "temp_createchar_deity");
			}
			else
			{
				player.sendMessage(ChatColor.RED + "You have been denied entry into the lineage of " + chosenDeity + "!");
			}
			
			// Clear the confirmation case
			event.getInventory().clear();
		}
		catch(Exception e)
		{
			// Print error for debugging
			e.printStackTrace();
		}
	}
	
	/* --------------------------------------------
	 *  Miscellaneous Methods
	 * --------------------------------------------
	 */
	private void clearChat(Player player)
	{
		for(int x = 0; x < 120; x++) player.sendMessage(" ");
	}
}
