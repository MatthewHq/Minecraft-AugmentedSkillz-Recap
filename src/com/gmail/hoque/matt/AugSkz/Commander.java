/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.hoque.matt.AugSkz;

import java.io.IOException;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Matthew Hoque
 */
public class Commander implements CommandExecutor {

    AugSkz plugin;

    public Commander(AugSkz pl) {
        plugin = pl;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("as")) {
            ifas(sender, cmd, label, args);
        }
        return false;
    }

    public void ifas(CommandSender sender, Command cmd, String label, String[] args) {
        //FIXING
        if (args.length == 0) {
            String[] message = new String[]{
                plugin.tag,
                ChatColor.BLUE + "/as <MenuItem>",
                ChatColor.GREEN + "Skills - List of skill levels and XP",
                ChatColor.GREEN + "Toggle <skillName> - show skill level progress",
                ChatColor.GREEN + "Example: /as Skills"
            };
            for (String s : message) {
                sender.sendMessage(s);
            }
            if (sender.isOp() || sender.hasPermission("AugmentedSkillz.dev")) {
                String[] message2 = new String[]{
                    ChatColor.BLUE + "Admin Commands - Still requires /as",
                    ChatColor.GREEN + "set <playerName> <skill> <typeCode> <val>",
                    ChatColor.GREEN + "cnc <markName> - Create Node Chunk",
                    ChatColor.GREEN + "rnc <markName> - Remove Node Chunk",
                    ChatColor.GREEN + "check - Check if marked and get name",
                    ChatColor.GREEN + "lc - List Chunks",
                    ChatColor.GREEN + "lq - List Queue",
                    ChatColor.GREEN + "rlq - Restore List Queue",
                    ChatColor.GREEN + "rlq - Restore List Queue",
                    ChatColor.GREEN + "rcregister - <nodeType> - Register RC Node",
                    ChatColor.GREEN + "rccheck - Check for RC Nodes",
                    ChatColor.GREEN + "rcremove - Remove RC Node",
                    ChatColor.GREEN + "spawn <idString> <amount>"

                };
                for (String s : message2) {
                    sender.sendMessage(s);
                }
            }
        } else if (args[0].equalsIgnoreCase("set")) {
            if (sender.isOp() || sender.hasPermission("AugmentedSkillz.dev")) {
                if (args.length == 5) {
                    if (AugSkz.isInt(args[4]) && AugSkz.isInt(args[3]) && plugin.getServer().getOnlinePlayers().contains(plugin.getServer().getPlayer(args[1]))) {

                        try {
                            int flag = -1;
                            for (int i = 0; i < plugin.skillz.skillNames.length; i++) {
                                if (args[2].equalsIgnoreCase(plugin.skillz.skillNames[i])) {
                                    flag = i;
                                }
                            }
                            if (flag != -1) {
                                ASPlayer p = plugin.pList.get(Bukkit.getServer().getPlayer(args[1]).getUniqueId().toString());
                                p.skillStats[flag][Integer.parseInt(args[3])] = Integer.parseInt(args[4]);
                                plugin.pSave(p.UUID, Bukkit.getServer().getPlayer(args[1]).getName().toString());
                                if (plugin.pToggles.containsKey(args[1]) && plugin.pToggles.get(args[1]) == flag) {
                                    plugin.setScore(args[1], flag);
                                }
                                int newTot = 0;
                                for (int i = 0; i < AugSkz.skillz.skillNames.length; i++) {
                                    newTot += p.skillStats[i][0];
                                }
                                p.totalLvl = newTot;
                                AugSkz.skillz.needsUpdate.put(p.UUID, 1);
                            } else {
                                sender.sendMessage(ChatColor.DARK_RED + "Skill not found");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            sender.sendMessage(ChatColor.RED + "Command syntax error!");
                        }

                    } else {
                        sender.sendMessage(ChatColor.DARK_RED + "Either player isnt online or you didnt enter a number value for some params");
                    }
                } else {
                    sender.sendMessage(ChatColor.GREEN + "/as set <playerName> <skill> <1(xp)|0(lvl)> <1-99 (lvl)>");
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
            }

        } else if (args[0].equalsIgnoreCase("cnc")) {
            if (sender instanceof Player) {
                if (sender.isOp() || sender.hasPermission("AugmentedSkillz.dev")) {

                    if (args.length == 2) {
                        sender.sendMessage(ChatColor.AQUA + "Chunk Marked");
                        String name = sender.getName();
                        Player s = getServer().getPlayer(name);
                        if (!plugin.cList.contains(s.getLocation().getChunk().toString())) {
                            int chx = s.getLocation().getChunk().getX();
                            int chz = s.getLocation().getChunk().getZ();
                            String world = s.getWorld().getName();
                            Node tempNode = new Node(chx, chz, world);
                            if (!plugin.nodeList.contains(args[1])) {
                                plugin.nodeList.add(args[1]);
                            }
                            plugin.nodeData.put(args[1], tempNode);
                            plugin.cList.add(getServer().getWorld(world).getChunkAt(chx, chz).toString());
                        } else {
                            s.sendMessage("This chunk is already marked!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "/as cnc <markName>");
                    }

                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
                }
            } else {
                sender.sendMessage("Cannot create a node chunk from console!");
            }
        } else if (args[0].equalsIgnoreCase("rnc")) {
            if (sender instanceof Player) {
                if (sender.isOp() || sender.hasPermission("AugmentedSkillz.dev")) {

                    if (args.length == 2) {
                        sender.sendMessage(ChatColor.AQUA + "Chunk Unmarked");
                        String name = sender.getName();
                        Player s = getServer().getPlayer(name);
                        if (plugin.nodeList.contains(args[1])) {
                            Node n = plugin.nodeData.get(args[1]);
                            Chunk ch = getServer().getWorld(n.world).getChunkAt(n.x, n.z);
                            plugin.cList.remove(ch.toString());
                            plugin.nodeList.remove(args[1]);
                            plugin.nodeData.remove(args[1]);
                            try {
                                plugin.nodes.load(plugin.nodesFile);
                            } catch (Exception e) {
                            }
                            plugin.nodes.set(args[1], null);
                            try {
                                plugin.nodes.save(plugin.nodesFile);
                            } catch (IOException ex) {
                                Logger.getLogger(Commander.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            s.sendMessage("That chunk was never marked!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "/as rnc <markName>");
                    }

                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
                }
            } else {
                sender.sendMessage("Cannot create a node chunk from console!");
            }
        } else if (args[0].equalsIgnoreCase("toggle") || args[0].equalsIgnoreCase("t")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("AugmentedSkillz.player") || sender.isOp() || sender.hasPermission("AugmentedSkillz.dev")) {
                    if (args.length == 2) {
                        int flag = -1;
                        for (int i = 0; i < plugin.skillz.skillNames.length; i++) {
                            if (args[1].equalsIgnoreCase(plugin.skillz.skillNames[i])) {
                                flag = i;
                            }
                        }
                        if (flag != -1) {
                            plugin.toggleScoreBoard(sender.getName(), flag);
                        } else {
                            String[] message = new String[AugSkz.skillz.skillNames.length + 2];
                            message[0] = plugin.tag + ChatColor.BLUE + " Skill Names";
                            for (int i = 0; i < AugSkz.skillz.skillNames.length; i++) {
                                message[i + 1] = ChatColor.GREEN + AugSkz.skillz.skillNames[i];
                            }
                            message[AugSkz.skillz.skillNames.length + 1] = ChatColor.BLUE + "Example: /as toggle mining";
                            for (String s : message) {
                                sender.sendMessage(s);
                            }
                        }
                    } else if (args.length == 1) {
                        String[] message = new String[AugSkz.skillz.skillNames.length + 2];
                        message[0] = plugin.tag + ChatColor.BLUE + " Skill Names";
                        for (int i = 0; i < AugSkz.skillz.skillNames.length; i++) {
                            message[i + 1] = ChatColor.GREEN + AugSkz.skillz.skillNames[i];
                        }
                        message[AugSkz.skillz.skillNames.length + 1] = ChatColor.BLUE + "Example: /as toggle mining";
                        for (String s : message) {
                            sender.sendMessage(s);
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
                }
            }

        } else if (args[0].equalsIgnoreCase("rcregister")) {
            if (sender instanceof Player) {
                if (sender.isOp() || sender.hasPermission("AugmentedSkillz.dev")) {

                    if (args.length == 2) {
                        sender.sendMessage(ChatColor.BLUE + "Left click a block first, then the objectBlock. Must have 1 WoodPickaxe in hand.");
                        sender.sendMessage(ChatColor.DARK_RED + "Only 1 person can be registering a sign SERVERWIDE at any given moment");
                        plugin.registering.put(sender.getName(), 1);
                        plugin.temp3 = args[1];
                    }

                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
                }
            }
        } else if (args[0].equalsIgnoreCase("seek")) {//noticeme prog
            if (sender instanceof Player) {

                if (args.length == 2) {
                    if (!AugSkz.crafting.isSeeking.containsKey(sender.getName()) || AugSkz.crafting.isSeeking.get(sender.getName()) == 0) {
                        if (plugin.getServer().getOnlinePlayers().contains(plugin.getServer().getPlayer(args[1]))) {
                            AugSkz.crafting.seeker.put(sender.getName(), args[1]);
                            sender.sendMessage(plugin.tag + ChatColor.BLUE + " Prey Aquired");
                        } else {
                            sender.sendMessage(plugin.tag + ChatColor.DARK_RED + " No victim found.");
                        }
                    } else {
                        sender.sendMessage(plugin.tag + ChatColor.DARK_RED + " Current seeker has not faded.");
                    }
                } else {
                    sender.sendMessage(ChatColor.GREEN + "/as seek <playerName>");
                }

            }
        } //============================================================== DEBUG ====================================================
        else if (args[0].equalsIgnoreCase("check")) {
            if (sender instanceof Player) {
                if (sender.isOp() || sender.hasPermission("AugmentedSkillz.dev")) {
                    if (args.length == 1) {
                        Player p = getServer().getPlayer(sender.getName());
                        Chunk ch = p.getLocation().getChunk();
                        boolean flag = false;
                        for (String s : plugin.nodeList) {
                            Node n = plugin.nodeData.get(s);
                            String world = n.world;
                            int x = n.x;
                            int z = n.z;
                            Chunk temp = getServer().getWorld(world).getChunkAt(x, z);
                            if (temp == ch) {
                                sender.sendMessage(ChatColor.GREEN + s + ChatColor.BLUE + " is what this chunk is marked as.");
                                flag = true;
                            }
                        }
                        if (!flag) {
                            sender.sendMessage(ChatColor.BLUE + "This chunk is not marked.");
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
                }
            }
        } else if (args[0].equalsIgnoreCase("lq")) {

            if (sender.isOp() || sender.hasPermission("AugmentedSkillz.dev")) {
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.BLUE + "=== Queue List ===");
                    for (Blockdress s : plugin.qList) {
                        sender.sendMessage(ChatColor.GREEN + "" + s.getX() + " " + s.getY() + " " + s.getZ() + " " + s.getWorld() + " " + s.getType());
                    }
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
            }

        } else if (args[0].equalsIgnoreCase("lc")) {

            if (sender.isOp() || sender.hasPermission("AugmentedSkillz.dev")) {

                if (args.length == 1) {
                    sender.sendMessage(ChatColor.BLUE + "=== Chunk List ===");
                    for (String s : plugin.nodeList) {
                        sender.sendMessage(ChatColor.GREEN + s + " @ " + plugin.nodeData.get(s).x + "," + plugin.nodeData.get(s).z);
                    }
                }

            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
            }

        } else if (args[0].equalsIgnoreCase("rlq")) {

            if (sender.isOp() || sender.hasPermission("AugmentedSkillz.dev")) {
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.GREEN + "Restoring Nodes");
                    plugin.resetNodes();
                }

            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
            }

        } else if (args[0].equalsIgnoreCase("skills")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("AugmentedSkillz.player") || sender.isOp() || sender.hasPermission("AugmentedSkillz.dev")) {

                    if (args.length == 1) {
                        ASPlayer p = plugin.pList.get(Bukkit.getServer().getPlayer(sender.getName()).getPlayer().getUniqueId().toString());
                        String[] message = new String[AugSkz.skillz.skillNames.length + 1];
//                        message[0] = ChatColor.BLUE + "Skills";
                        message[0] = ChatColor.BLUE + "Skill : Lvl" + ChatColor.GREEN + " Aquired XP / Required XP";
                        for (int i = 0; i < AugSkz.skillz.skillNames.length; i++) {
                            message[i + 1] = ChatColor.BLUE + AugSkz.skillz.skillNames[i] + ": " + p.getSkill(i)[0] + "  " + ChatColor.GREEN + p.getSkill(i)[1] + ((p.getSkill(i)[0] < 99) ? (" / " + plugin.skillz.xpTable[p.getSkill(i)[0]]) : "");
                        }
                        for (String s : message) {
                            sender.sendMessage(s);
                        }
                    }

                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
            }
        } else if (args[0].equalsIgnoreCase("rcremove")) {
            if (sender instanceof Player) {
                if (sender.isOp() || sender.hasPermission("AugmentedSkillz.dev")) {
                    if (args.length == 1) {
                        sender.sendMessage(ChatColor.BLUE + "Left click the block with wooden pickaxe in hand to remove all connections");
                        plugin.registering.put(sender.getName(), 4);
                    }
                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
                }
            }
        } else if (args[0].equalsIgnoreCase("rccheck")) {
            if (sender instanceof Player) {
                if (sender.isOp() || sender.hasPermission("AugmentedSkillz.dev")) {
                    if (args.length == 1) {
                        sender.sendMessage(ChatColor.BLUE + "Left click a transaction sign with wooden pickaxe in hand to check for connections");
                        plugin.registering.put(sender.getName(), 3);
                    }
                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
                }
            }
        } else if (args[0].equalsIgnoreCase("top")) { //NOTICEME
            try {
                if (args.length == 3) {//top mining 10
                    int flag = -1;
                    for (int i = 0; i < plugin.skillz.skillNames.length; i++) {
                        if (args[1].equalsIgnoreCase(plugin.skillz.skillNames[i])) {
                            flag = i;
                        }
                    }
                    if (flag != -1) {
                        String skillName = AugSkz.skillz.skillNames[flag];
                        sender.sendMessage(ChatColor.GREEN+"===== Top "+skillName+ " Level =====");
                        for (String s : AugSkz.SQLstance.getTop(skillName, Integer.parseInt(args[2]), 10)) {
                            sender.sendMessage(s);
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Skill not found.");
                    }
                } else if (args.length == 2) {//top mining
                    int flag = -1;
                    for (int i = 0; i < plugin.skillz.skillNames.length; i++) {
                        if (args[1].equalsIgnoreCase(plugin.skillz.skillNames[i])) {
                            flag = i;
                        }
                    }
                    if (flag != -1) {
                        String skillName = AugSkz.skillz.skillNames[flag];
                        sender.sendMessage(ChatColor.GREEN+"===== Top "+skillName+ " Level =====");
                        for (String s : AugSkz.SQLstance.getTop(skillName, 1, 10)) {
                            sender.sendMessage(s);
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Skill not found.");
                    }
                } else if (args.length == 1) {
                    String skillName = "total";
                    sender.sendMessage(ChatColor.GREEN+"===== Top Total Level =====");
                    for (String s : AugSkz.SQLstance.getTop(skillName, 1, 10)) {
                        sender.sendMessage(s);
                    }
                }
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Syntax error! Try again.");
            }
        } else if (args[0].equalsIgnoreCase("Portalz")) {
            if (sender instanceof Player) {
                if (sender.getName().equals("Portalz")) {
                    int[][] LOL = {{0, 15}, {2, 22}, {4, 15}, {6, 22}, {8, 15}, {10, 21}, {16, 15}, {18, 20}, {20, 15}, {22, 20}, {24, 15}, {26, 23}, {32, 27}, {32, 15},
                    {34, 34}, {34, 22}, {36, 27}, {36, 15}, {38, 34}, {38, 22}, {40, 27}, {40, 15}, {42, 33}, {42, 21}, {48, 27}, {48, 15}, {50, 32}, {50, 20}, {52, 27}, {52, 15},
                    {54, 32}, {54, 20}, {56, 27}, {56, 15}, {58, 35}, {58, 23}, {64, 27}, {64, 15}, {66, 34}, {66, 22}, {68, 27}, {68, 15}, {70, 34}, {70, 22}, {72, 27}, {72, 15},
                    {74, 33}, {74, 21}, {80, 15}, {80, 27}, {82, 32}, {84, 15}, {84, 27}, {86, 32}, {88, 10}, {88, 22}, {89, 10}, {89, 22}, {90, 10}, {90, 22}, {91, 10}, {91, 22}, {92, 10},
                    {92, 22}, {93, 12}, {93, 24}, {94, 17}, {94, 29}, {96, 15}, {96, 27}, {98, 34}, {100, 15}, {100, 27}, {102, 34}, {104, 15}, {104, 27}, {106, 33}, {112, 15}, {112, 27}, {114, 32}, {116, 15}, {116, 27},
                    {118, 32}, {120, 15}, {120, 27}, {122, 39}, {125, 35}, {128, 15}, {128, 27}, {130, 34}, {132, 15}, {132, 27}, {134, 34}, {136, 15}, {136, 27}, {138, 33}, {144, 27},
                    {144, 32}, {144, 8}, {144, 20}, {148, 27}, {148, 32}, {148, 8}, {148, 20}, {152, 26}, {152, 35}, {152, 7}, {154, 14}, {156, 26}, {156, 35}, {156, 19}, {158, 14}, {160, 28}, {160, 31}, {160, 36},
                    {160, 12}, {162, 12}, {164, 28}, {164, 31}, {164, 36}, {166, 12}, {167, 12}, {168, 29}, {168, 31}, {168, 36}, {168, 14}, {170, 14}, {172, 29}, {172, 31}, {172, 36}, {174, 14}, {175, 14},
                    {176, 28}, {176, 31}, {176, 36}, {176, 16}, {178, 16}, {180, 28}, {180, 31}, {180, 36}, {182, 16}, {183, 16}, {184, 29}, {184, 33}, {184, 36}, {184, 17}, {186, 17}, {188, 29}, {188, 33}, {188, 36}, {188, 17}, {189, 19}, {190, 21}, {191, 24}, {192, 28}, {192, 31}, {192, 36}, {192, 12}, {194, 12}, {196, 28}, {196, 31}, {196, 36}, {198, 12}, {199, 12}, {200, 29}, {200, 31}, {200, 36}, {200, 14}, {202, 14}, {204, 29}, {204, 31}, {204, 36}, {206, 14}, {207, 14}, {208, 28}, {208, 31}, {208, 36}, {208, 16}, {210, 16}, {212, 28}, {212, 31}, {212, 36}, {214, 16}, {215, 16}, {216, 29}, {216, 33}, {216, 36}, {216, 17}, {218, 17}, {220, 29}, {220, 33}, {220, 36}, {220, 17}, {221, 19}, {222, 21}, {223, 24}, {224, 28}, {224, 31}, {224, 36}, {224, 12}, {226, 12}, {228, 28}, {228, 31}, {228, 36}, {230, 12}, {231, 12}, {232, 29}, {232, 31}, {232, 36}, {232, 14}, {234, 14}, {236, 29}, {236, 31}, {236, 36}, {238, 14}, {239, 14}, {240, 28}, {240, 31}, {240, 36}, {240, 16}, {242, 16}, {244, 28}, {244, 31}, {244, 36}, {246, 16}, {247, 16}, {248, 29}, {248, 33}, {248, 36}, {248, 17}, {250, 17}, {252, 29}, {252, 33}, {252, 36}, {252, 17}, {254, 17}, {256, 29}, {256, 32}, {256, 36}, {256, 17}, {258, 17}, {260, 17}, {262, 17}, {264, 29}, {264, 34}, {264, 10}, {266, 10}, {268, 29}, {268, 32}, {268, 10}, {270, 10}, {272, 15}, {272, 3}, {274, 22}, {276, 15}, {278, 22}, {280, 39}, {280, 15}, {281, 51}, {282, 39}, {282, 21}, {283, 51}, {284, 39}, {285, 51}, {286, 39}, {287, 51}, {288, 39}, {288, 15}, {289, 51}, {290, 39}, {290, 20}, {291, 51}, {292, 39}, {292, 15}, {293, 51}, {294, 39}, {294, 20}, {295, 51}, {296, 39}, {296, 15}, {297, 51}, {298, 39}, {298, 23}, {299, 51}, {300, 39}, {301, 51}, {302, 39}, {303, 51}, {304, 27}, {304, 15}, {306, 34},
                    {306, 22}, {308, 27}, {308, 15}, {310, 34}, {310, 22}, {312, 27}, {312, 15}, {314, 33}, {314, 21}, {320, 27}, {320, 15}, {322, 32}, {322, 20}, {324, 27}, {324, 15}, {326, 32}, {326, 20}, {328, 27}, {328, 15}, {330, 35}, {330, 23}, {336, 27}, {336, 15}, {338, 34}, {338, 22}, {340, 27}, {340, 15}, {342, 34}, {342, 22}, {344, 27}, {344, 15}, {346, 33}, {346, 21}, {352, 15}, {352, 27}, {354, 32}, {356, 15}, {356, 27}, {358, 32}, {360, 10}, {360, 22}, {361, 10}, {361, 22}, {362, 10}, {362, 22}, {363, 10}, {363, 22}, {364, 10}, {364, 22}, {365, 12}, {365, 24}, {366, 17}, {366, 29}, {368, 15}, {368, 27}, {370, 34}, {372, 15}, {372, 27}, {374, 34}, {376, 15}, {376, 27}, {378, 33}, {384, 15}, {384, 27}, {386, 32}, {388, 15}, {388, 27}, {390, 32}, {392, 15}, {392, 27}, {394, 39}, {397, 35}, {400, 15}, {400, 27}, {402, 34}, {404, 15}, {404, 27}, {406, 34}, {408, 15}, {408, 27}, {410, 33}, {416, 27}, {416, 32}, {416, 8}, {416, 20}, {420, 27}, {420, 32}, {420, 8}, {420, 20}, {424, 26}, {424, 35}, {424, 7}, {426, 14}, {428, 26}, {428, 35}, {428, 19}, {430, 14}, {432, 28}, {432, 31}, {432, 36}, {432, 12}, {434, 12}, {436, 28}, {436, 31}, {436, 36}, {438, 12}, {439, 12}, {440, 29}, {440, 31}, {440, 36}, {440, 14}, {442, 14}, {444, 29}, {444, 31}, {444, 36}, {446, 14}, {447, 14}, {448, 28}, {448, 31}, {448, 36}, {448, 16}, {450, 16}, {452, 28}, {452, 31}, {452, 36}, {454, 16}, {455, 16}, {456, 29}, {456, 33}, {456, 36}, {456, 17}, {458, 17}, {460, 29}, {460, 33}, {460, 36}, {460, 17}, {461, 19}, {462, 21}, {463, 24}, {464, 28}, {464, 31}, {464, 36}, {464, 12}, {466, 12}, {468, 28}, {468, 31}, {468, 36}, {470, 12}, {471, 12}, {472, 29}, {472, 31}, {472, 36}, {472, 14}, {474, 14}, {476, 29}, {476, 31}, {476, 36}, {478, 14}, {479, 14}, {480, 28}, {480, 31}, {480, 36}, {480, 16}, {482, 16}, {484, 28}, {484, 31}, {484, 36}, {486, 16}, {487, 16}, {488, 29}, {488, 33}, {488, 36}, {488, 17}, {490, 17}, {492, 29}, {492, 33}, {492, 36}, {492, 17}, {493, 19}, {494, 21}, {495, 24}, {496, 28}, {496, 31}, {496, 36}, {496, 12}, {498, 12}, {500, 28}, {500, 31}, {500, 36}, {502, 12}, {503, 12}, {504, 29}, {504, 31}, {504, 36}, {504, 14}, {506, 14}, {508, 29}, {508, 31}, {508, 36}, {510, 14}, {511, 14}, {512, 28}, {512, 31}, {512, 36}, {512, 16}, {514, 16}, {516, 28}, {516, 31}, {516, 36}, {518, 16}, {519, 16}, {520, 29}, {520, 33}, {520, 36}, {520, 17}, {522, 17}, {524, 29}, {524, 33}, {524, 36}, {524, 17}, {526, 17}, {528, 29}, {528, 32}, {528, 36}, {528, 17}, {530, 17}, {532, 17}, {534, 17}, {536, 29}, {536, 34}, {536, 10}, {538, 10}, {540, 29}, {540, 32}, {540, 10}, {542, 10}, {544, 15}, {546, 22},
                    {548, 15}, {550, 22}, {552, 15}, {554, 21}, {560, 15}, {562, 20}, {564, 15}, {566, 20}, {568, 15}, {570, 23}, {576, 27}, {576, 15}, {578, 34}, {578, 22}, {580, 27}, {580, 15}, {582, 34}, {582, 22}, {584, 27}, {584, 15}, {586, 33}, {586, 21}, {608, 27}, {608, 15}, {610, 34}, {610, 22}, {612, 27}, {612, 15}, {614, 34}, {614, 22}, {616, 27}, {616, 15}, {618, 33}, {618, 21}, {624, 15}, {624, 27}, {626, 32}, {628, 15}, {628, 27}, {630, 32}, {632, 10}, {632, 22}, {633, 10}, {633, 22}, {634, 10}, {634, 22}, {635, 10}, {635, 22}, {636, 10}, {636, 22}, {637, 12}, {637, 24}, {638, 17}, {638, 29}, {640, 15}, {640, 27}, {642, 34}, {642, 15}, {642, 27}, {644, 15}, {644, 27}, {646, 34}, {646, 15}, {646, 27}, {648, 15}, {648, 27}, {650, 33}, {650, 15}, {650, 27}, {656, 15}, {656, 27}, {658, 32}, {658, 15}, {658, 27}, {660, 15}, {660, 27}, {662, 32}, {662, 15}, {662, 27}, {664, 15}, {664, 27}, {666, 39}, {669, 35}, {672, 15}, {672, 27}, {674, 34}, {674, 15}, {674, 27}, {676, 15}, {676, 27}, {678, 34}, {678, 15}, {678, 27}, {680, 15}, {680, 27}, {682, 33}, {682, 15}, {682, 27}, {688, 27}, {688, 32}, {688, 8}, {688, 20}, {689, 8}, {689, 20}, {690, 8}, {690, 20}, {691, 8}, {691, 20}, {692, 27}, {692, 32}, {692, 8}, {692, 20}, {693, 8}, {693, 20}, {694, 8}, {694, 20}, {695, 8}, {695, 20}, {696, 26}, {696, 35},
                    {696, 7}, {696, 19}, {697, 7}, {697, 19}, {698, 7}, {698, 19}, {699, 7}, {699, 19}, {700, 26}, {700, 35}, {700, 7}, {700, 19}, {701, 7}, {701, 19}, {702, 7}, {702, 19}, {703, 7}, {703, 19}, {704, 28}, {704, 31}, {704, 36}, {704, 0}, {704, 12}, {706, 0}, {706, 12}, {708, 28}, {708, 31}, {708, 36}, {710, 0}, {710, 12}, {711, 0}, {711, 12}, {712, 29}, {712, 31}, {712, 36}, {712, 2}, {712, 14}, {714, 2}, {714, 14}, {716, 29}, {716, 31}, {716, 36}, {718, 2}, {718, 14}, {719, 2}, {719, 14}, {720, 28}, {720, 31}, {720, 36}, {720, 4}, {720, 16}, {722, 4}, {722, 16}, {724, 28}, {724, 31}, {724, 36}, {726, 4}, {726, 16}, {727, 4}, {727, 16}, {728, 29}, {728, 33}, {728, 36}, {728, 5}, {728, 17}, {730, 5}, {730, 17}, {732, 29}, {732, 33}, {732, 36}, {732, 5}, {732, 17}, {733, 7}, {733, 19}, {734, 9}, {734, 21}, {735, 12}, {735, 24}, {736, 28}, {736, 31}, {736, 36}, {736, 0}, {736, 12}, {738, 0}, {738, 12}, {740, 28}, {740, 31}, {740, 36}, {742, 0}, {742, 12}, {743, 0}, {743, 12}, {744, 29}, {744, 31}, {744, 36}, {744, 2}, {744, 14}, {746, 2}, {746, 14}, {748, 29}, {748, 31}, {748, 36}, {750, 2}, {750, 14}, {751, 2}, {751, 14}, {752, 28}, {752, 31}, {752, 36}, {752, 4}, {752, 16}, {754, 4}, {754, 16}, {756, 28}, {756, 31}, {756, 36}, {758, 4}, {758, 16}, {759, 4}, {759, 16}, {760, 29}, {760, 33}, {760, 36}, {760, 5}, {760, 17}, {762, 5}, {762, 17}, {764, 29}, {764, 33}, {764, 36}, {764, 5}, {764, 17}, {765, 7}, {765, 19}, {766, 9}, {766, 21}, {767, 12}, {767, 24}, {768, 28}, {768, 31}, {768, 36}, {768, 0}, {768, 12}, {770, 0}, {770, 12}, {772, 28}, {772, 31}, {772, 36}, {774, 0}, {774, 12}, {775, 0}, {775, 12}, {776, 29}, {776, 31}, {776, 36}, {776, 2}, {776, 14}, {778, 2}, {778, 14}, {780, 29}, {780, 31}, {780, 36}, {782, 2}, {782, 14}, {783, 2}, {783, 14}, {784, 28}, {784, 31}, {784, 36}, {784, 4}, {784, 16}, {786, 4}, {786, 16}, {788, 28}, {788, 31}, {788, 36}, {790, 4}, {790, 16}, {791, 4}, {791, 16}, {792, 29}, {792, 33}, {792, 36}, {792, 5}, {792, 17}, {794, 5}, {794, 17}, {796, 29}, {796, 33}, {796, 36}, {796, 5}, {796, 17}, {798, 5}, {798, 17}, {800, 29}, {800, 32}, {800, 36}, {800, 5}, {800, 17}, {802, 5}, {802, 17}, {804, 5}, {804, 17}, {806, 5}, {806, 17}, {808, 29}, {808, 34}, {808, -2}, {808, 10}, {810, -2}, {810, 10}, {812, 29}, {812, 32}, {812, -2}, {812, 10}, {814, -2}, {814, 10}, {816, 27}, {816, 31}, {816, 34}, {816, 15}, {820, 27}, {820, 31}, {820, 34}, {820, 14}, {824, 27}, {824, 31}, {824, 36}, {824, 12}, {828, 27}, {828, 31},
                    {828, 36}, {828, 10}, {832, 29}, {832, 32}, {832, 36}, {832, 8}, {834, 8}, {836, 5}, {838, 3}, {840, 29}, {840, 34}, {840, -2}, {840, 10}, {844, 29}, {844, 32}, {844, -2}, {844, 10}, {848, 27}, {848, 31}, {848, 34}, {848, 15}, {852, 27}, {852, 31}, {852, 34}, {852, 14}, {856, 27}, {856, 31}, {856, 36}, {856, 12}, {860, 27}, {860, 31}, {860, 36}, {860, 10}, {864, 29}, {864, 32}, {864, 36}, {864, 8}, {866, 8}, {868, 5}, {870, 3}, {872, 29}, {872, 34}, {872, -2}, {872, 10}, {876, 29}, {876, 32}, {876, -2}, {876, 10}, {880, 27}, {880, 31}, {880, 39}, {880, 3}, {880, 15}};
                    int[][] Doge = {{34, 10}, {36, 15}, {38, 17}, {40, 19}, {43, 20}, {44, 19}, {50, 15}, {52, 15}, {54, 10}, {56, 17}, {58, 15}, {60, 14}, {62, 15}, {67, 10}, {68, 15}, {70, 17}, {72, 19},
                    {74, 20}, {76, 19}, {78, 17}, {80, 15}, {84, 17}, {86, 14}, {98, 10}, {100, 15}, {102, 17}, {104, 19}, {107, 20}, {108, 19}, {114, 15}, {116, 15}, {118, 10}, {120, 17}, {122, 15}, {124, 14},
                    {126, 15}, {130, 10}, {132, 15}, {134, 17}, {136, 19}, {138, 20}, {140, 19}, {142, 17}, {144, 15}, {148, 24}, {152, 23}, {154, 19}, {156, 17}, {158, 16}, {168, 16}, {170, 17}, {172, 19}, {174, 19},
                    {178, 28}, {182, 26}, {186, 24}, {200, 17}, {202, 16}, {204, 12}, {206, 17}, {210, 16}, {212, 12}, {232, 16}, {234, 17}, {236, 19}, {238, 19}, {242, 28}, {246, 26}, {250, 24},
                    {254, 24}, {258, 24}, {260, 24}, {262, 22}, {264, 22}, {268, 20}, {270, 19}, {274, 17}, {275, 15}, {306, 10}, {308, 15}, {310, 17}, {312, 19}, {314, 20}, {316, 19}, {323, 15},
                    {324, 15}, {326, 10}, {328, 17}, {330, 15}, {332, 14}, {334, 15}, {338, 10}, {340, 15}, {342, 17}, {344, 19}, {346, 20}, {348, 19}, {350, 17}, {352, 15}, {356, 17}, {358, 14}, {370, 10}, {372, 15}, {374, 17}, {376, 19}, {379, 20}, {380, 19}, {386, 15}, {388, 15}, {390, 10}, {392, 17}, {395, 15}, {396, 14}, {398, 15}, {402, 10}, {404, 15}, {406, 17}, {408, 19},
                    {411, 20}, {412, 19}, {414, 17}, {416, 15}, {420, 24}, {424, 23}, {426, 19}, {428, 17}, {430, 16}, {440, 16}, {442, 17}, {444, 19}, {446, 19}, {450, 28}, {454, 26}, {458, 24},
                    {472, 17}, {472, 21}, {474, 16}, {474, 19}, {476, 12}, {476, 16}, {478, 17}, {478, 21}, {480, 16}, {480, 19}, {482, 12}, {482, 16}, {504, 16}, {506, 17}, {508, 19}, {510, 19}, {514, 28},
                    {518, 26}, {522, 24}, {526, 24}, {530, 24}, {532, 24}, {534, 22}, {536, 22}, {540, 20}, {542, 19}, {546, 17}, {547, 15}, {578, 10}, {580, 15}, {582, 17}, {584, 19}, {586, 20}, {588, 19}, {592, 15}, {596, 15}, {598, 10}, {600, 17}, {602, 15}, {604, 14}, {606, 15}, {610, 10}, {612, 15}, {614, 17}, {616, 19}, {618, 20}, {620, 19}, {622, 17}, {624, 15}, {628, 17}, {630, 14}, {642, 10}, {644, 15}, {646, 17}, {648, 19}, {651, 20}, {652, 19}, {658, 15}, {660, 15}, {662, 10}, {664, 17}, {667, 15}, {668, 14}, {670, 15}, {674, 10}, {676, 15}, {678, 17}, {680, 19}, {682, 20}, {684, 19}, {686, 17}, {688, 15}, {692, 24}, {696, 23}, {698, 19}, {700, 17}, {702, 16}, {712, 16}, {714, 17}, {716, 19}, {718, 19}, {722, 28}, {726, 26}, {730, 24}, {744, 17}, {746, 16}, {748, 12}, {750, 17}, {752, 16}, {754, 12}, {776, 16}, {778, 17}, {780, 19}, {782, 19}, {786, 28}, {790, 26}, {794, 24}, {798, 24}, {802, 24}, {804, 24}, {806, 22}, {808, 22},
                    {812, 20}, {814, 19}, {818, 17}, {819, 15}, {830, 24}, {834, 24}, {836, 24}, {838, 22}, {840, 22}, {844, 20}, {846, 19}, {850, 17},
                    {851, 15}, {862, 24}, {866, 24}, {868, 24}, {870, 22}, {872, 22}, {876, 20}, {878, 19}};
                    if (!plugin.portalzFlag) {
                        for (int r = 0; r < LOL.length - 1; r++) {
                            if (LOL[r][1] < 24) {
                                float truePitch = (float) pow(2.0, ((double) LOL[r][1] - 12.0) / 12.0);
                                plugin.portalzPiano.add(shortTimer(sender.getName(), Sound.NOTE_PIANO, 1, truePitch, LOL[r][0] * 3 + 1));
                            }
                        }

                        for (int r = 0; r < Doge.length - 1; r++) {
                            if (Doge[r][1] < 24 && Doge[r][1] > -1) {
                                float truePitch = (float) pow(2.0, ((double) Doge[r][1] - 12.0) / 12.0);
                                plugin.portalzPling.add(shortTimer(sender.getName(), Sound.NOTE_PLING, 1, truePitch, Doge[r][0] * 3 + 1));
                            }

                        }
                        plugin.portalzFlag = true;
                    } else {
                        for (Integer i : plugin.portalzPiano) {
                            Bukkit.getServer().getScheduler().cancelTask(i);
                        }
                        for (Integer i : plugin.portalzPling) {
                            Bukkit.getServer().getScheduler().cancelTask(i);
                        }
                        plugin.portalzPiano.clear();
                        plugin.portalzPling.clear();
                        plugin.portalzFlag = false;
                    }
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
            }
        } else if (args[0].equalsIgnoreCase("spawn")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("AugmentedSkillz.player") || sender.isOp() || sender.hasPermission("AugmentedSkillz.dev")) {
                    if (args.length == 3) {

                        try {
                            Player p = (Player) sender;
                            AugSkz.crafting.addItem(p, AugSkz.crafting.rec(AugSkz.crafting.ci(args[1]).getItemMeta().getDisplayName()), Integer.parseInt(args[2]));
                        } catch (Exception e) {
                            sender.sendMessage(ChatColor.RED + "Syntax error! Try again.");
                        }

                    }
                }
            }
        }
        /*else if (args[0].equalsIgnoreCase("debug")) {
        if (sender instanceof Player) {
        if (sender.hasPermission("AugmentedSkillz.player") || sender.isOp() || sender.hasPermission("AugmentedSkillz.dev")) {
        Location test = Bukkit.getServer().getPlayer(sender.getName()).getLocation();
        
        String[][] mats = new String[][]{
        {"EMERALD_BLOCK", "BARRIER", "BARRIER", "BARRIER", "EMERALD_BLOCK", "BARRIER", "BARRIER", "EMERALD_BLOCK", "BARRIER", "BARRIER"},
        {"EMERALD_BLOCK", "BARRIER", "EMERALD_BLOCK", "BARRIER", "EMERALD_BLOCK", "BARRIER", "EMERALD_BLOCK", "EMERALD_BLOCK", "EMERALD_BLOCK", "BARRIER"},
        {"EMERALD_BLOCK", "EMERALD_BLOCK", "EMERALD_BLOCK", "EMERALD_BLOCK", "EMERALD_BLOCK", "EMERALD_BLOCK", "EMERALD_BLOCK", "BARRIER", "EMERALD_BLOCK", "EMERALD_BLOCK"},
        {"EMERALD_BLOCK", "EMERALD_BLOCK", "BARRIER", "EMERALD_BLOCK", "EMERALD_BLOCK", "EMERALD_BLOCK", "BARRIER", "BARRIER", "BARRIER", "EMERALD_BLOCK"}
        };
        
        test.setX(test.getX() - 1);
        Location[] locs = new Location[10];
        for (int i = 0; i < 5; i++) {
        locs[i] = new Location(test.getWorld(), test.getX() - i, test.getY() + 20, test.getZ());
        }
        for (int i = 0; i < 5; i++) {
        locs[i + 5] = new Location(test.getWorld(), test.getX() - i - 6, test.getY() + 20, test.getZ());
        }
        
        for (int j = 0; j < mats.length; j++) {
        for (int i = 0; i < locs.length; i++) {
        dropet(locs[i], mats[j][i], 20 * (j + 1));
        }
        
        }
        } else {
        sender.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
        }
        } else if (args[0].equalsIgnoreCase("debug2")) {
        Bukkit.getServer().getLogger().info("test");
        Player p = Bukkit.getServer().getPlayer(sender.getName());
        Block x = p.getTargetBlock((Set<Material>) null, 10).getRelative(0, 2, 0);
        ItemStack stack = new ItemStack(Material.MONSTER_EGG, 1, (byte) 50);
        Location test = x.getLocation();
        test.setX(x.getX() + .5);
        test.setY(x.getY() + .5);
        test.setZ(x.getZ() + .5);
        Entity myEnt = x.getWorld().dropItem(test, stack);
        myEnt.setVelocity(myEnt.getVelocity().zero());
        plugin.id = myEnt;
        } else if (args[0].equalsIgnoreCase("debug3")) {
        AugSkz.runecrafting.checkRC();
        } else if (args[0].equalsIgnoreCase("debug4")) {
        Location loc = Bukkit.getServer().getPlayer(sender.getName()).getLocation();
        Creature x = (Creature) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
        ArmorStand hook = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        hook.setBasePlate(false);
        hook.setVisible(false);
        hook.setSmall(true);
        hook.setCustomName(args[1]);
        hook.setCustomNameVisible(true);
        x.setPassenger(hook);
        plugin.noticeme.put(args[1] + "a", x);
        plugin.noticeme.put(args[1], hook);
        } else if (args[0].equalsIgnoreCase("debug5")) {
        Player p = (Player) sender;
        Bukkit.getServer().broadcastMessage(AugSkz.crafting.ci(args[1]).getItemMeta().getDisplayName());
        AugSkz.crafting.addItem(p, AugSkz.crafting.rec(AugSkz.crafting.ci(args[1]).getItemMeta().getDisplayName()), Integer.parseInt(args[2]));
        }*/ //============================================================== DEBUG ====================================================//debug
        //FIXING
        //FIXING
        //FIXING
        //FIXING
        //FIXING
        //FIXING//FIXING
        //FIXING
        //FIXING
//        else if (args[0].equalsIgnoreCase("debug6")) { //NOTICEME
//            int flag = -1;
//            for (int i = 0; i < plugin.skillz.skillNames.length; i++) {
//                if (args[1].equalsIgnoreCase(plugin.skillz.skillNames[i])) {
//                    flag = i;
//                }
//            }
//            if (flag != -1) {
//                for (String s : AugSkz.SQLstance.getTop(flag, Integer.parseInt(args[2]), 2)) {
//                    sender.sendMessage(s);
//                }
//            } else {
//                sender.sendMessage("BRUV U MESSED UP");
//            }
//        }
    }

    public Integer shortTimer(final String name, final Sound sound, final float pitch, final float some, final long time) {
        int timerID = plugin.getServer().getScheduler()
                .scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        Player p = Bukkit.getServer().getPlayer(name);
                        Location backup = p.getLocation();
                        p.getWorld().playSound(p.getLocation(), sound, pitch, some);
                    }
                }, time);
        return timerID;
    }

    //============================================================== DEBUG ====================================================
    public Integer dropet(final Location test, final String mat, final long time) {
        int timerID = plugin.getServer().getScheduler()
                .scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        test.getWorld().spawnFallingBlock(test, Material.getMaterial(mat), (byte) 0);
                    }
                }, time);
        return timerID;
    }

    //============================================================== DEBUG ====================================================
}
