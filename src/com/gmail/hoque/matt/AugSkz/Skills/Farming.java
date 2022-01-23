/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.hoque.matt.AugSkz.Skills;

import com.gmail.hoque.matt.AugSkz.ASPlayer;
import com.gmail.hoque.matt.AugSkz.AugSkz;
import static com.gmail.hoque.matt.AugSkz.AugSkz.updateQueue;
import com.gmail.hoque.matt.AugSkz.Blockdress;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Matthew Hoque
 */
public class Farming {

    AugSkz plugin;
    int[] stageLvlReqs = new int[]{1, 35, 70, 97}; // plant advanced 
    byte[] stageLvls = new byte[]{0, 1, 2, 3}; //plant advanced 1-7
    public int[] farmBlockLvlReqs = new int[]{1, 5, 25, 15, 10};
    public int[] farmBlockHarvestAmount = new int[]{3, 3, 3, 3, 3};
    public int[][] farmHarvestMultiplier = new int[][]{{2, 3, 4 ,5}, {2, 3}, {2,3}, {2, 3}, {2}};
    public int[][] farmMultiplierReqs = new int[][]{{40, 60, 80,90}, {50, 83}, {65,86}, {45, 73}, {93}};
    public short[] farmSpecifier = new short[]{7, 7, 7, -1, 3};
    public Material[] farmNodeMats = new Material[]{Material.CROPS, Material.CARROT, Material.POTATO, Material.COCOA, Material.NETHER_WARTS};
    public Material[] farmedMats = new Material[]{Material.WHEAT, Material.CARROT_ITEM, Material.POTATO_ITEM, Material.INK_SACK, Material.NETHER_STALK};
    public ArrayList<Material> underMats = new ArrayList<Material>();
    public Material[] underHarvest = new Material[]{Material.BROWN_MUSHROOM, Material.RED_MUSHROOM,Material.RED_ROSE};
    public int[][] underMultiplierReqs = new int[][]{{38,55,75,85,95}, {47, 68,88,96,98},{78,99}};
    public int[][] underMultiplier = new int[][]{{2,3,4,5,6}, {2,3,4,5,6},{2, 3}};
    public int[] underBlockLvlReqs = new int[]{20,27,30};
    public int[] underBlockHarvestAmount = new int[]{1, 1,1};

    public int[] farmXPTable = new int[farmNodeMats.length];
    public int ironDurabilityScalar = 5;

    public Farming(AugSkz plugin) {
        this.plugin = plugin;
        iniFarmXPTable();
        farmingMenu();
    }

    public void iniFarmXPTable() {
        try {
            plugin.cfg.load(plugin.cfgFile);
        } catch (Exception e) {
        }
        ArrayList<Integer> ints = (ArrayList<Integer>) plugin.cfg.getIntegerList("FarmXPTable");
        for (Integer i : ints) {
        }
        for (int i = 0; i < farmXPTable.length; i++) {
            farmXPTable[i] = ints.get(i);
        }

        //ATTACHED
        underMats.add(Material.HUGE_MUSHROOM_1);
        underMats.add(Material.HUGE_MUSHROOM_2);
        underMats.add(Material.REDSTONE_BLOCK);
    }

    public void farmingMenu() {
        Inventory template = Bukkit.createInventory(null, 36, ChatColor.DARK_BLUE + "Farming Recipes");
        ItemStack[] c = new ItemStack[36];
        c[0] = AugSkz.crafting.rec(ChatColor.WHITE + "Brown Blockshroom").menu();
        c[1] = AugSkz.crafting.rec(ChatColor.WHITE + "Red Blockshroom").menu();
        c[27] = AugSkz.crafting.ci("return").clone();
        template.setContents(c);
        AugSkz.crafting.menus.put(ChatColor.WHITE + "Farming", template);
    }

    public void onBlockBreakEvent(BlockBreakEvent event) {
        Material handType = event.getPlayer().getItemInHand().getType();
        if (handType == Material.IRON_HOE || handType == Material.DIAMOND_HOE || handType == Material.GOLD_HOE) {
            boolean matchesSpecific;
            int type = -1;
            int subtype = -1;
            int counter = 0;
            for (Material mat : farmNodeMats) {
                if (event.getBlock().getType().name().equals(mat.name())) {
                    type = counter;
                }
                counter++;
            }
            if (type != -1) {
                if (matchesSpecific(event.getBlock(), type)) {
                    if (farmBlockLvlReqs[type] <= plugin.pList.get(event.getPlayer().getUniqueId().toString()).skillStats[AugSkz.skillz.skillTable.get("Farming")][0]) {
                        if (event.getPlayer().getInventory().firstEmpty() != -1) {
                            Location a = event.getBlock().getLocation();
                            event.setCancelled(true);
                            event.getBlock().setType(Material.AIR);
                            int multi = 1;
                            boolean flag = false;
                            if (farmNodeMats[type] == Material.CROPS && underMats.contains(event.getBlock().getRelative(0, -2, 0).getType())) {
                                flag = true;
                                counter = 0;
                                for (Material mat : underMats) {
                                    if (event.getBlock().getRelative(0, -2, 0).getType().name().equals(mat.name())) {
                                        subtype = counter;
                                    }
                                    counter++;
                                }
                            }
                            ASPlayer p = plugin.pList.get(event.getPlayer().getUniqueId().toString());
                            ItemStack x;
                            if ((!flag) || !(underBlockLvlReqs[subtype] <= plugin.pList.get(event.getPlayer().getUniqueId().toString()).skillStats[AugSkz.skillz.skillTable.get("Farming")][0])) {
                                for (int i = farmMultiplierReqs[type].length; i > 0; i--) {
                                    if (p.skillStats[AugSkz.skillz.skillTable.get("Farming")][0] >= farmMultiplierReqs[type][i - 1]) {
                                        multi = farmHarvestMultiplier[type][i - 1];

                                        i = 0;
                                    }
                                }
                                x = new ItemStack(farmedMats[type], farmBlockHarvestAmount[type] * ((handType == Material.GOLD_HOE) ? multi : 1), (short) ((event.getBlock().getType() == Material.COCOA) ? 3 : 0));
                            } else {
                                for (int i = underMultiplierReqs[subtype].length; i > 0; i--) {
                                    if (p.skillStats[AugSkz.skillz.skillTable.get("Farming")][0] >= underMultiplierReqs[subtype][i - 1]) {
                                        multi = underMultiplier[subtype][i - 1];

                                        i = 0;
                                    }
                                }
                                x = new ItemStack(underHarvest[subtype], underBlockHarvestAmount[subtype] * ((handType == Material.GOLD_HOE) ? multi : 1), (short) ((event.getBlock().getType() == Material.COCOA) ? 3 : 0));
                            }
                            event.getPlayer().getInventory().addItem(x);
                            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_PICKUP, 1, 1);

                            short tempshort = event.getPlayer().getItemInHand().getDurability();
                            if (tempshort < event.getPlayer().getItemInHand().getType().getMaxDurability() - 1) {
                                event.getPlayer().getItemInHand().setDurability((short) (tempshort + ((handType == Material.IRON_HOE) ? AugSkz.farming.ironDurabilityScalar : 1)));
                            } else {
                                event.getPlayer().getInventory().remove(event.getPlayer().getItemInHand());
                                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_BREAK, 1, 1);
                            }

                            //ItemMeta itemMeta = i.getItemMeta();
                            //Arrays.asList("line1", "line2", "line3")
                            /*itemMeta.setDisplayName("Killer Fish Thingy");
                             itemMeta.setLore(Arrays.asList(ChatColor.RED + "Deadly Pooper"));
                             i.setItemMeta(itemMeta);*/
                            AugSkz.skillz.addXp(event.getPlayer(), p, "Farming", plugin.farming.farmXPTable[type]);

                        } else {
                            event.getPlayer().sendMessage("You can't carry anymore!");
                            event.setCancelled(true);
                        }
                    } else {
                        event.getBlock().setType(Material.AIR);
                        event.getPlayer().sendMessage(plugin.tag+ChatColor.BLUE + " Required Farming Level:" + plugin.mining.blockLvlReqs[type]);
                    }
                } else {
                    event.getBlock().setType(Material.AIR);
                    event.setCancelled(true);
                }

            }
        }
    }

    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.CROPS) {
            int counter = 0;
            for (int i = stageLvlReqs.length; i > 0; i--) { //NOTICEME Changed from stageLvlReqs.length -1 to stageLvlReqs.length
                if (stageLvlReqs[i-1] <= plugin.pList.get(event.getPlayer().getUniqueId().toString()).skillStats[AugSkz.skillz.skillTable.get("Farming")][0]) {//NOTICEME changed from i to i-1
                    counter = i-1;//NOTICEME changed from i to i-1
                    i = -1;
                }
            }
            event.getBlock().setData(stageLvls[counter]);
        }
    }

    public boolean matchesSpecific(Block block, int type) {
        boolean flag = false;
        if (farmSpecifier[type] == -1) {
            flag = true;
        } else if (block.getData() == farmSpecifier[type]) {
            flag = true;
        }
        if (block.getType() == Material.COCOA) {
            if (block.getData() > 7) {
                flag = true;
            } else {
                flag = false;
            }
        }
        return flag;
    }
}
