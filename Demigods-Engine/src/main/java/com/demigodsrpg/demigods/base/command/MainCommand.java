package com.demigodsrpg.demigods.base.command;

import com.demigodsrpg.demigods.engine.Demigods;
import com.demigodsrpg.demigods.engine.DemigodsPlugin;
import com.demigodsrpg.demigods.engine.conversation.Administration;
import com.demigodsrpg.demigods.engine.data.DataManager;
import com.demigodsrpg.demigods.engine.data.TempDataManager;
import com.demigodsrpg.demigods.engine.deity.Ability;
import com.demigodsrpg.demigods.engine.deity.Alliance;
import com.demigodsrpg.demigods.engine.deity.Deity;
import com.demigodsrpg.demigods.engine.entity.player.DemigodsCharacter;
import com.demigodsrpg.demigods.engine.entity.player.DemigodsPlayer;
import com.demigodsrpg.demigods.engine.helper.CommandManager;
import com.demigodsrpg.demigods.engine.language.English;
import com.demigodsrpg.demigods.engine.language.Symbol;
import com.demigodsrpg.demigods.engine.util.Configs;
import com.demigodsrpg.demigods.engine.util.Messages;
import com.demigodsrpg.demigods.engine.util.Strings;
import com.demigodsrpg.demigods.engine.util.Times;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.nio.file.AccessDeniedException;
import java.util.Collection;
import java.util.Map;

// TODO Convert this over to the sub-command format.

public class MainCommand extends CommandManager {
    @Override
    public ImmutableSet<String> getCommandNames() {
        return ImmutableSet.of("demigods", "deity");
    }

    @Override
    public ImmutableList<CommandManager.Sub> getSubCommands() {
        return ImmutableList.of();
    }

    @Override
    public boolean always(CommandSender sender, Command command, String label, String[] args) {
        // Commands able to be run by the console
        if ("demigods".equals(command.getName())) {
            if (args.length == 2 && "admin".equalsIgnoreCase(args[0]) && "reload".equalsIgnoreCase(args[1])) {
                PluginManager pluginManager = DemigodsPlugin.getInst().getServer().getPluginManager();
                pluginManager.disablePlugin(DemigodsPlugin.getInst());
                pluginManager.enablePlugin(DemigodsPlugin.getInst());
                sender.sendMessage(ChatColor.GREEN + English.RELOAD_COMPLETE.getLine());
                return false;
            }
        }

        // No console below this point
        if (sender instanceof ConsoleCommandSender) return !Messages.noConsole((ConsoleCommandSender) sender);

        // Define Player
        Player player = (Player) sender;

        // Check args and pass onto appropriate method
        if (args.length > 0 && "admin".equalsIgnoreCase(args[0])) {
            dg_admin(player, args);
            return false;
        } else if (args.length > 0) {
            dg_extended(player, args);
            return false;
        }

        // Check Permissions
        if (!player.hasPermission("demigods.basic")) return !Messages.noPermission(player);

        if ("deity".equals(command.getName()) && DemigodsPlayer.of(player).isACharacter() && DemigodsCharacter.of(player).isUsable()) {
            Deity deity = DemigodsCharacter.of(player).getDeity();
            player.chat("/dg " + deity.getAlliance().getName().toLowerCase() + " " + deity.getName().toLowerCase());
            return false;
        } else if ("deity".equals(command.getName())) {
            player.sendMessage(ChatColor.RED + "This command requires you to have a character.");
            return false;
        }

        Messages.tagged(sender, "Documentation");
        for (Alliance alliance : Demigods.getMythos().getAlliances())
            if (sender.hasPermission(alliance.getPermission()))
                sender.sendMessage(ChatColor.GRAY + " /dg " + alliance.getName().toLowerCase());
        sender.sendMessage(ChatColor.GRAY + " /dg info");
        if (player.hasPermission("demigods.admin")) sender.sendMessage(ChatColor.RED + " /dg admin");
        sender.sendMessage(" ");
        sender.sendMessage(ChatColor.WHITE + " Use " + ChatColor.YELLOW + "/check" + ChatColor.WHITE + " to see your player information.");
        return false;
    }

    private static boolean dg_extended(Player player, String[] args) {
        // Define args
        String category = args[0];
        String option1 = null;
        if (args.length >= 2) option1 = args[1];

        // Check Permissions
        if (!player.hasPermission("demigods.basic")) return Messages.noPermission(player);

        if ("info".equalsIgnoreCase(category)) {
            if (option1 == null) {
                Messages.tagged(player, "Information Directory");
                player.sendMessage(ChatColor.GRAY + " /dg info characters");
                player.sendMessage(ChatColor.GRAY + " /dg info shrines");
                player.sendMessage(ChatColor.GRAY + " /dg info obelisks");
                player.sendMessage(ChatColor.GRAY + " /dg info players");
                player.sendMessage(ChatColor.GRAY + " /dg info pvp");
                player.sendMessage(ChatColor.GRAY + " /dg info skills");
                player.sendMessage(ChatColor.GRAY + " /dg info demigods");
            } else if ("characters".equalsIgnoreCase(option1)) {
                // TODO
                Messages.tagged(player, "Characters");
                player.sendMessage(ChatColor.GRAY + " This is some info about Characters.");
            } else if ("shrine".equalsIgnoreCase(option1)) {
                // TODO
                Messages.tagged(player, "Shrines");
                player.sendMessage(ChatColor.GRAY + " This is some info about Shrines.");
            } else if ("player".equalsIgnoreCase(option1)) {
                // TODO
                Messages.tagged(player, "Players");
                player.sendMessage(ChatColor.GRAY + " This is some info about Players.");
            } else if ("skills".equalsIgnoreCase(option1)) {
                // TODO
                Messages.tagged(player, "Skills");
                player.sendMessage(ChatColor.GRAY + " This is some info about Skills.");
            } else if ("pvp".equalsIgnoreCase(option1)) {
                // TODO
                Messages.tagged(player, "PVP");
                player.sendMessage(ChatColor.GRAY + " This is some info about PVP.");
            } else if ("demigods".equalsIgnoreCase(option1)) {
                Messages.tagged(player, "About the Plugin");
                player.sendMessage(ChatColor.WHITE + " Not to be confused with other RPG plugins that focus on skills and classes alone, " + ChatColor.GREEN + "Demigods" + ChatColor.WHITE + " adds culture and conflict that will keep players coming back even after they've maxed out their levels and found all of the diamonds in a 50km radius.");
                player.sendMessage(" ");
                player.sendMessage(ChatColor.GREEN + " Demigods" + ChatColor.WHITE + " is unique in its system of rewarding players for both adventuring (tributes) and conquering (PvP) with a wide array of fun and useful skills.");
                player.sendMessage(" ");
                player.sendMessage(ChatColor.WHITE + " Re-enact mythological battles and rise from a mere player to a full-fledged Olympian as you form new Alliances with mythical groups and battle to the bitter end.");
                player.sendMessage(" ");
                player.sendMessage(ChatColor.GRAY + " Developed by: " + ChatColor.GREEN + "_Alex" + ChatColor.GRAY + " and " + ChatColor.GREEN + "HmmmQuestionMark");
                player.sendMessage(ChatColor.GRAY + " Website: " + ChatColor.YELLOW + "http://demigodsrpg.com");
                player.sendMessage(ChatColor.GRAY + " Source: " + ChatColor.YELLOW + "http://github.com/CensoredSoftware/Demigods");
            }

            return true;
        }

        for (Alliance alliance : Demigods.getMythos().getAlliances()) {
            if (!player.hasPermission(alliance.getPermission())) continue;

            if (alliance.getName().equalsIgnoreCase(category)) {
                if (args.length < 2) {
                    Messages.tagged(player, alliance + " Directory");
                    for (Deity deity : Alliance.Util.getLoadedPlayableDeitiesInAlliance(alliance))
                        player.sendMessage(ChatColor.GRAY + " /dg " + alliance.getName().toLowerCase() + " " + deity.getColor() + deity.getName().toLowerCase());
                } else {
                    for (final Deity deity : Alliance.Util.getLoadedMajorPlayableDeitiesInAllianceWithPerms(alliance, player)) {
                        if (deity.getName().equalsIgnoreCase(option1)) {
                            try {
                                Collection<String> claimItems = Collections2.transform(deity.getClaimItems().entrySet(), new Function<Map.Entry<Material, Integer>, String>() {
                                    @Override
                                    public String apply(Map.Entry<Material, Integer> entry) {
                                        return entry.getValue() + " " + Strings.beautify(Strings.plural(entry.getKey().name(), entry.getValue())).toLowerCase();
                                    }
                                });

                                // Header information
                                Messages.tagged(player, "Deity Information");
                                player.sendMessage(" ");
                                player.sendMessage("  " + ChatColor.GRAY + Symbol.RIGHTWARD_ARROW + " " + deity.getColor() + deity.getName() + ChatColor.GRAY + " - " + deity.getShortDescription() + (deity.getFlags().contains(Deity.Flag.DIFFICULT) ? ChatColor.RED + " (" + Symbol.CAUTION + " DIFFICULT)" : ""));
                                player.sendMessage(" ");

                                // Claim items
                                if (claimItems.size() == 1) {
                                    player.sendMessage("   " + ChatColor.GRAY + Symbol.RIGHTWARD_ARROW_HOLLOW + " Claim Item: " + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + StringUtils.join(claimItems, ""));
                                } else {
                                    player.sendMessage("   " + ChatColor.GRAY + Symbol.RIGHTWARD_ARROW_HOLLOW + " Claim Items: " + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + StringUtils.join(claimItems, ChatColor.GRAY + ", " + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC));
                                }

                                // Abilities
                                for (Ability ability : deity.getAbilities()) {
                                    player.sendMessage(" ");
                                    player.sendMessage("   " + ChatColor.GRAY + Symbol.RIGHTWARD_ARROW_HOLLOW + ChatColor.YELLOW + " " + ability.getName() + (ability.getCommand() != null ? ChatColor.GRAY + " (" + ChatColor.GREEN + "/" + ability.getCommand() + ChatColor.GRAY + ")" : ChatColor.GRAY) + " (" + ChatColor.GOLD + ability.getType().getName() + ChatColor.GRAY + ")" + (ability.getCost() > 0 ? ChatColor.GRAY + " (" + ChatColor.RED + ability.getCost() + ChatColor.GRAY + " favor per use)" : ""));
                                    for (String detail : ability.getDetails()) {
                                        player.sendMessage(ChatColor.GRAY + "      " + Symbol.RIGHTWARD_ARROW_SWOOP + ChatColor.ITALIC + " " + detail);
                                    }
                                }

                                player.sendMessage(" ");

                                return true;
                            } catch (Exception errored) {
                                errored.printStackTrace();
                                return true;
                            }
                        }
                    }
                    player.sendMessage(ChatColor.DARK_RED + " No such deity, please try again.");
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean dg_admin(final Player player, String[] args) {
        if (!player.hasPermission("demigods.admin")) return Messages.noPermission(player);

        // Display main admin options menu
        if (args.length < 2) {
            Messages.tagged(player, "Admin Directory");
            if (player.hasPermission("demigods.admin.menu")) player.sendMessage(ChatColor.GRAY + " /dg admin menu");
            if (player.hasPermission("demigods.admin.wand")) player.sendMessage(ChatColor.GRAY + " /dg admin wand");
            player.sendMessage(ChatColor.GRAY + " /dg admin debug");
            for (AdminCommand command : AdminCommand.values()) {
                player.sendMessage(ChatColor.GRAY + " " + command.getCommand().getName());
            }
            player.sendMessage(ChatColor.DARK_RED + " /dg admin clear data yesdoitforsurepermanently");
            player.sendMessage(ChatColor.DARK_RED + " /dg admin compress");
            return true;
        }

        // Handle automatic commands
        for (AdminCommand command : AdminCommand.values()) {
            if (command.getCommand().getParentCommand().equalsIgnoreCase(args[1]))
                return command.getCommand().process(player, args);
        }

        // Handle manual commands
        if ("menu".equalsIgnoreCase(args[1])) {
            // Check for wand permission
            if (!player.hasPermission("demigods.admin.menu")) return Messages.noPermission(player);

            // Start their administration menu
            Administration.Util.toggleAdministration(player, true, true);
        }
        if ("wand".equalsIgnoreCase(args[1])) {
            // Check for wand permission
            if (!player.hasPermission("demigods.admin.wand")) return Messages.noPermission(player);

            // Do checking
            if (!Administration.Util.wandEnabled(player)) {
                Administration.Util.toggleWand(player, true);
                player.sendMessage(ChatColor.RED + "Your admin wand has been enabled for " + Material.getMaterial(Configs.getSettingInt("admin.wand_tool")));
            } else if (Administration.Util.wandEnabled(player)) {
                Administration.Util.toggleWand(player, false);
                player.sendMessage(ChatColor.RED + "You have disabled your admin wand.");
            }
            return true;
        } else if ("debug".equalsIgnoreCase(args[1])) {
            if (!TempDataManager.hasKeyTemp(player.getName(), "temp_admin_debug") || !Boolean.parseBoolean(TempDataManager.getValueTemp(player.getName(), "temp_admin_debug").toString())) {
                Administration.Util.togglePlayerDebug(player, true);
                player.sendMessage(ChatColor.RED + "You have enabled debugging.");
            } else if (TempDataManager.hasKeyTemp(player.getName(), "temp_admin_debug") && Boolean.parseBoolean(TempDataManager.getValueTemp(player.getName(), "temp_admin_debug").toString())) {
                Administration.Util.togglePlayerDebug(player, false);
                player.sendMessage(ChatColor.RED + "You have disabled debugging.");
            }
        } else if ("clear".equalsIgnoreCase(args[1]) && args[2].equalsIgnoreCase("data") && args[3].equalsIgnoreCase("yesdoitforsurepermanently")) {
            player.sendMessage(ChatColor.RED + English.ADMIN_CLEAR_DATA_STARTING.getLine());
            try {
                DataManager.flushAllData();
            } catch (AccessDeniedException e) {
                e.printStackTrace();
            }
            player.sendMessage(ChatColor.GREEN + English.ADMIN_CLEAR_DATA_FINISHED.getLine());
            return true;
        }

        return true;
    }

    static class Check implements SubCommand {
        @Override
        public String getName() {
            return "/dg admin check [player|character] <name>";
        }

        @Override
        public String getParentCommand() {
            return "check";
        }

        @Override
        public boolean process(Player sender, String[] args) {
            if (args.length < 4) {
                // Not enough parameters, return
                sender.sendMessage(ChatColor.RED + "You didn't specify enough parameters.");
                return true;
            }

            if ("player".equalsIgnoreCase(args[2])) {
                // Define the player
                DemigodsPlayer player = DemigodsPlayer.of(Bukkit.getOfflinePlayer(args[3]));

                // Display the information
                if (player != null) {
                    Messages.tagged(sender, "Player Information");
                    sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW + " " + ChatColor.RESET + "Name: " + player.getPlayerName());
                    sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW + " " + ChatColor.RESET + "Last Login: " + Times.getTimeTagged(player.getLastLoginTime(), true) + " ago");
                    sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW + " " + ChatColor.RESET + "Last Logout: " + Times.getTimeTagged(player.getLastLogoutTime(), true) + " ago");
                    sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW + " " + ChatColor.RESET + "Can PvP? " + (player.canPvp() ? ChatColor.GREEN : ChatColor.RED) + Strings.beautify("" + player.canPvp()));
                    if (player.isACharacter())
                        sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW + " " + ChatColor.RESET + "Current Character: " + player.getCharacter().getDeity().getColor() + player.getCharacter().getName());
                    if (player.getCharacters() != null && !player.getCharacters().isEmpty()) {
                        sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW + " " + ChatColor.RESET + "Characters:");
                        for (DemigodsCharacter character : player.getCharacters())
                            sender.sendMessage(ChatColor.GRAY + "   - " + ChatColor.WHITE + character.getName() + ChatColor.RESET + " (" + character.getDeity().getColor() + character.getDeity().getName() + ChatColor.RESET + ")");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "No player found with that name.");
                }
            } else if ("character".equalsIgnoreCase(args[2])) {
                // Define the character
                DemigodsCharacter character = Demigods.getServer().getCharacter(args[3]);

                // Display the information
                if (character != null) {
                    Messages.tagged(sender, "Character Information");
                    sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW + " " + ChatColor.RESET + "Name: " + character.getDeity().getColor() + character.getName());
                    sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW + " " + ChatColor.RESET + "Deity: " + character.getDeity().getColor() + character.getDeity().getName());
                    sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW + " " + ChatColor.RESET + "Alliance: " + ChatColor.WHITE + character.getAlliance());
                    sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW + " " + ChatColor.RESET + "Favor: " + Strings.getColor(character.getMeta().getFavor(), character.getMeta().getMaxFavor()) + character.getMeta().getFavor() + ChatColor.WHITE + " (of " + ChatColor.GREEN + character.getMeta().getMaxFavor() + ChatColor.WHITE + ")");
                    sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW + " " + ChatColor.RESET + "Ascensions: " + ChatColor.GREEN + character.getMeta().getAscensions());
                    sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW + " " + ChatColor.RESET + "Available Skill Points: " + ChatColor.GREEN + character.getMeta().getSkillPoints());
                    sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW + " " + ChatColor.RESET + "Kills: " + ChatColor.GREEN + character.getKillCount() + ChatColor.WHITE + " / Deaths: " + ChatColor.RED + character.getDeathCount());
                    sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW + " " + ChatColor.RESET + "Owner: " + ChatColor.WHITE + character.getPlayerName() + " (" + (character.getBukkitOfflinePlayer().isOnline() ? ChatColor.GREEN + "online" : ChatColor.RED + "offline") + ChatColor.WHITE + ")");
                    sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW + " " + ChatColor.RESET + "Active? " + (character.isActive() ? ChatColor.GREEN : ChatColor.RED) + Strings.beautify("" + character.isActive()));
                    sender.sendMessage(ChatColor.GRAY + " " + Symbol.RIGHTWARD_ARROW + " " + ChatColor.RESET + "Usable? " + (character.isUsable() ? ChatColor.GREEN : ChatColor.RED) + Strings.beautify("" + character.isUsable()));
                    sender.sendMessage(" ");
                } else {
                    sender.sendMessage(ChatColor.RED + "No character found with that name.");
                }
            }

            return true;
        }
    }

    static class Remove implements SubCommand {
        @Override
        public String getName() {
            return "/dg admin remove [player|character] <name>";
        }

        @Override
        public String getParentCommand() {
            return "remove";
        }

        @Override
        public boolean process(Player sender, String[] args) {
            if (args.length < 4) {
                // Not enough parameters, return
                sender.sendMessage(ChatColor.RED + "You didn't specify enough parameters.");
                return true;
            }

            if ("player".equalsIgnoreCase(args[2])) {
                // Define the player
                DemigodsPlayer player = DemigodsPlayer.of(Bukkit.getOfflinePlayer(args[3]));

                // Remove their data if not null
                if (player != null) {
                    // Remove them
                    player.remove();

                    // Send success message
                    sender.sendMessage(ChatColor.RED + player.getPlayerName() + " has been removed successfully!");
                } else {
                    sender.sendMessage(ChatColor.RED + "No player found with that name.");
                }
            } else if ("character".equalsIgnoreCase(args[2])) {
                // Define the character
                DemigodsCharacter character = Demigods.getServer().getCharacter(args[3]);

                // Remove their data if not null
                if (character != null) {
                    // Remove them
                    character.remove();

                    // Send success message
                    sender.sendMessage(ChatColor.RED + character.getName() + " has been removed successfully!");
                } else {
                    sender.sendMessage(ChatColor.RED + "No player found with that name.");
                }
            }

            return true;
        }
    }

    static class Set implements SubCommand {
        @Override
        public String getName() {
            return "/dg admin set [fav|maxfav|sp] <character> <amt>";
        }

        @Override
        public String getParentCommand() {
            return "set";
        }

        @Override
        public boolean process(Player sender, String[] args) {
            if (args.length < 5) {
                // Not enough parameters, return
                sender.sendMessage(ChatColor.RED + "You didn't specify enough parameters.");
                return true;
            }

            DemigodsCharacter character = Demigods.getServer().getCharacter(args[3]);
            int amount = Integer.parseInt(args[4]);
            String updatedValue;

            if (character != null) {
                Player owner = character.getBukkitOfflinePlayer().getPlayer();

                if ("fav".equalsIgnoreCase(args[2])) {
                    // Update the amount
                    character.getMeta().setFavor(amount);

                    // Set what was updated
                    updatedValue = "favor";
                } else if ("maxfav".equalsIgnoreCase(args[2])) {
                    // Update the amount
                    character.getMeta().setMaxFavor(amount);

                    // Set what was updated
                    updatedValue = "max favor";
                } else if ("sp".equalsIgnoreCase(args[2])) {
                    // Update the amount
                    character.getMeta().setSkillPoints(amount);

                    // Set what was updated
                    updatedValue = "skill points";
                } else {
                    // Nothing was edited
                    owner.sendMessage(ChatColor.RED + "Invalid value to update specified.");
                    return true;
                }

                // Message the administrator to confirm
                sender.sendMessage(ChatColor.GREEN + Strings.beautify(updatedValue) + " updated for " + character.getName() + ".");

                // Message the edited player
                if (character.getBukkitOfflinePlayer().isOnline()) {
                    owner.sendMessage(ChatColor.GREEN + "Your character's (" + character.getName() + ") " + updatedValue + " has been set to " + amount + ".");
                    owner.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "This was performed by " + sender.getName() + ".");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "No character could be found.");
            }

            return true;
        }
    }

    static class Add implements SubCommand {
        @Override
        public String getName() {
            return "/dg admin add [fav|maxfav|sp] <character> <amt>";
        }

        @Override
        public String getParentCommand() {
            return "add";
        }

        @Override
        public boolean process(Player sender, String[] args) {
            if (args.length < 5) {
                // Not enough parameters, return
                sender.sendMessage(ChatColor.RED + "You didn't specify enough parameters.");
                return true;
            }

            DemigodsCharacter character = Demigods.getServer().getCharacter(args[3]);
            int amount = Integer.parseInt(args[4]);
            String updatedValue = null;

            if (character != null) {
                Player owner = character.getBukkitOfflinePlayer().getPlayer();

                if ("fav".equalsIgnoreCase(args[2])) {
                    // Update the amount
                    character.getMeta().addFavor(amount);

                    // Set what was updated
                    updatedValue = "favor";
                } else if ("maxfav".equalsIgnoreCase(args[2])) {
                    // Update the amount
                    character.getMeta().addMaxFavor(amount);

                    // Set what was updated
                    updatedValue = "max favor";
                } else if ("sp".equalsIgnoreCase(args[2])) {
                    // Update the amount
                    character.getMeta().addSkillPoints(amount);

                    // Set what was updated
                    updatedValue = "skill points";
                } else {
                    // Nothing was edited
                    owner.sendMessage(ChatColor.RED + "Invalid value to update specified.");
                }

                // Message the administrator to confirm
                sender.sendMessage(ChatColor.GREEN + Strings.beautify(updatedValue) + " updated for " + character.getName() + ".");

                // Message them
                if (character.getBukkitOfflinePlayer().isOnline()) {
                    owner.sendMessage(ChatColor.GREEN + "Your character's (" + character.getName() + ") " + updatedValue + " has been increased to " + amount + ".");
                    owner.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "This was performed by " + sender.getName() + ".");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "No character could be found.");
            }

            return true;
        }
    }

    static class Sub implements SubCommand {
        @Override
        public String getName() {
            return "/dg admin sub [fav|maxfav|sp] <character> <amt>";
        }

        @Override
        public String getParentCommand() {
            return "sub";
        }

        @Override
        public boolean process(Player sender, String[] args) {
            if (args.length < 5) {
                // Not enough parameters, return
                sender.sendMessage(ChatColor.RED + "You didn't specify enough parameters.");
                return true;
            }

            DemigodsCharacter character = Demigods.getServer().getCharacter(args[3]);
            int amount = Integer.parseInt(args[4]);
            String updatedValue = null;

            if (character != null) {
                Player owner = character.getBukkitOfflinePlayer().getPlayer();

                if ("fav".equalsIgnoreCase(args[2])) {
                    // Update the amount
                    character.getMeta().subtractFavor(amount);

                    // Set what was updated
                    updatedValue = "favor";
                } else if ("maxfav".equalsIgnoreCase(args[2])) {
                    // Update the amount
                    character.getMeta().subtractMaxFavor(amount);

                    // Set what was updated
                    updatedValue = "max favor";
                } else if ("sp".equalsIgnoreCase(args[2])) {
                    // Update the amount
                    character.getMeta().subtractSkillPoints(amount);

                    // Set what was updated
                    updatedValue = "skill points";
                } else {
                    // Nothing was edited
                    owner.sendMessage(ChatColor.RED + "Invalid value to update specified.");
                }

                // Message the administrator to confirm
                sender.sendMessage(ChatColor.GREEN + Strings.beautify(updatedValue) + " updated for " + character.getName() + ".");

                // Message them
                if (character.getBukkitOfflinePlayer().isOnline()) {
                    owner.sendMessage(ChatColor.GREEN + "Your character's (" + character.getName() + ") " + updatedValue + " has been decreased to " + amount + ".");
                    owner.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "This was performed by " + sender.getName() + ".");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "No character could be found.");
            }

            return true;
        }
    }

    public enum AdminCommand {
        CHECK(new Check()), REMOVE(new Remove()), SET(new Set()), ADD(new Add()), SUBTRACT(new Sub());

        private SubCommand command;

        private AdminCommand(SubCommand command) {
            this.command = command;
        }

        public SubCommand getCommand() {
            return this.command;
        }
    }

    interface SubCommand {
        public String getName();

        public String getParentCommand();

        public boolean process(Player sender, String[] args);
    }
}
