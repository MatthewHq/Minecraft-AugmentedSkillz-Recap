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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Matthew Hoque
 */
public class Mining {

    AugSkz plugin;
    public int ironDurabilityScalar = 5;

    public int[] blockLvlReqs = new int[]{1,5,10,15,20,25,28,30};
    public int[] blockMaterialAmount = new int[]{3,3,3,4,3,1,1,1};
    public int[][] blockMultiplier = new int[][]{{2, 3, 4, 5}, {2, 3, 4}, {2, 3}, {2, 3}, {2, 3}, {2, 3, 4}, {2}, {2}};
    public int[][] blockMultiplierReqs = new int[][]{{40, 60, 80, 95}, {50, 30, 96}, {65, 87}, {45, 70}, {75, 90}, {55, 77, 92}, {98}, {99}};
    public Material[] blockMats = new Material[]{
        Material.CLAY,
        Material.COAL_ORE,
        Material.IRON_ORE,
        Material.REDSTONE_ORE,
        Material.GOLD_ORE,
        Material.LAPIS_ORE,
        Material.DIAMOND_ORE,
        Material.EMERALD_ORE,
        Material.GLOWING_REDSTONE_ORE};

    public Material[] minedBlockMats = new Material[]{
        Material.CLAY_BALL,
        Material.COAL,
        Material.IRON_ORE,
        Material.REDSTONE,
        Material.GOLD_ORE,
        Material.LAPIS_BLOCK,
        Material.DIAMOND,
        Material.EMERALD};

    public String[] minedBlockMatsName = new String[]{
        "Clay Ball",
        "Coal",
        "Iron Ore",
        "Redstone",
        "Gold Ore",
        "Lapis Block",
        "Diamond",
        "Emerald"
    };

    public int[] blockXPTable = new int[8];
    public boolean taggedOres = true;

    public Mining(AugSkz plugin) {
        this.plugin = plugin;
        iniBlockXPTable();
    }

    public void iniBlockXPTable() {
        try {
            plugin.cfg.load(plugin.cfgFile);
        } catch (Exception e) {
            plugin.getLogger().info(e.toString());
        }
        ArrayList<Integer> ints = (ArrayList<Integer>) plugin.cfg.getIntegerList("BlockXPTable");
        // for (Integer i : ints) {
        // }
        for (int i = 0; i < blockXPTable.length; i++) {
            blockXPTable[i] = ints.get(i);
        }

        //LATCHED ON
        taggedOres = plugin.cfg.getBoolean("OreTaggedDrops");
    }

    public void onBlockBreakEvent(BlockBreakEvent event) {
        /*Bukkit.getServer().broadcastMessage(event.getBlock().getType().toString());
         event.setCancelled(true);*/
        Material handType = event.getPlayer().getItemInHand().getType();
        if (handType == Material.IRON_PICKAXE || handType == Material.DIAMOND_PICKAXE || handType == Material.GOLD_PICKAXE) {
            int type = -1;
            int counter = 0;
            for (Material mat : plugin.mining.blockMats) {
                if (event.getBlock().getType().name().equals(mat.name())) {
                    type = counter;
                    //If it = activated redstone then it equals redstone
                    if (type == 8) {
                        type = 3;
                    }
                }
                counter++;
            }
            if (type != -1) {
                Chunk tempc = event.getBlock().getChunk();
                if (plugin.cList.contains(tempc.toString())) {
                    if (plugin.mining.blockLvlReqs[type] <= plugin.pList.get(event.getPlayer().getUniqueId().toString()).skillStats[AugSkz.skillz.skillTable.get("Mining")][0]) {
                        if (event.getPlayer().getInventory().firstEmpty() != -1) {
                            /*int freeSlots=0;
                             for (ItemStack stack : event.getPlayer().getInventory().getContents()) {
                             if(stack==null){
                             freeSlots++;
                             }
                             }
                             Bukkit.getServer().broadcastMessage("YOU HAVE "+freeSlots+" free slots!");*/
                            Location a = event.getBlock().getLocation();
                            Blockdress bl = new Blockdress(event.getBlock().getType().name(), event.getBlock().getWorld().getName(), a.getX(), a.getY(), a.getZ(), System.currentTimeMillis() / 1000);
                            plugin.qList.add(bl);
                            updateQueue(plugin.qList);
                            event.setCancelled(true);
                            event.getBlock().setType(Material.COBBLESTONE);
                            int multi = 1;
                            ASPlayer p = plugin.pList.get(event.getPlayer().getUniqueId().toString());
                            for (int i = plugin.mining.blockMultiplierReqs[type].length; i > 0; i--) {
                                if (p.skillStats[AugSkz.skillz.skillTable.get("Mining")][0] >= plugin.mining.blockMultiplierReqs[type][i - 1]) {
                                    multi = plugin.mining.blockMultiplier[type][i - 1];

                                    i = 0;
                                }
                            }
                            ItemStack i = new ItemStack(plugin.mining.minedBlockMats[type], plugin.mining.blockMaterialAmount[type] * ((handType == Material.GOLD_PICKAXE) ? multi : 1));
                            if (AugSkz.mining.taggedOres) {
                                long time = System.currentTimeMillis();
                                ItemMeta tag = i.getItemMeta();
                                tag.setDisplayName(plugin.timeToHidden(time) + "§~" + ChatColor.BLUE + plugin.mining.minedBlockMatsName[type]);
                                i.setItemMeta(tag);
                                event.getPlayer().getInventory().addItem(i);
                                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_PICKUP, 1, 1);
                                /*ItemStack subItem=i.clone();
                                 for(int x=0;x<i.getAmount();x++){
                                 subItem.setAmount(1);
                                 tag.setDisplayName(plugin.timeToHidden(time+x) + "§~" +ChatColor.BLUE+plugin.mining.minedBlockMats[type].name());
                                 subItem.setItemMeta(tag);
                                 event.getPlayer().getInventory().addItem(subItem);
                                 }*/

                            } else {
                                event.getPlayer().getInventory().addItem(i);
                                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_PICKUP, 1, 1);
                            }
                            short tempshort = event.getPlayer().getItemInHand().getDurability();
                            if (tempshort < event.getPlayer().getItemInHand().getType().getMaxDurability() - 1) {
                                event.getPlayer().getItemInHand().setDurability((short) (tempshort + ((handType == Material.IRON_PICKAXE) ? AugSkz.mining.ironDurabilityScalar : 1)));
                            } else {
                                event.getPlayer().getInventory().remove(event.getPlayer().getItemInHand());
                                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_BREAK, 1, 1);
                            }

                            //ItemMeta itemMeta = i.getItemMeta();
                            //Arrays.asList("line1", "line2", "line3")
                            /*itemMeta.setDisplayName("Killer Fish Thingy");
                             itemMeta.setLore(Arrays.asList(ChatColor.RED + "Deadly Pooper"));
                             i.setItemMeta(itemMeta);*/
                            AugSkz.skillz.addXp(event.getPlayer(), p, "Mining", plugin.mining.blockXPTable[type]);
                            /*p.skillStats[AugSkz.skillz.skillTable.get("Mining")][1] += plugin.mining.blockXPTable[type];

                             if (p.skillStats[AugSkz.skillz.skillTable.get("Mining")][0] < 99
                             && p.skillStats[AugSkz.skillz.skillTable.get("Mining")][1] >= plugin.skillz.xpTable[p.skillStats[AugSkz.skillz.skillTable.get("Mining")][0]]) {
                             p.skillStats[AugSkz.skillz.skillTable.get("Mining")][1] -= plugin.skillz.xpTable[p.skillStats[AugSkz.skillz.skillTable.get("Mining")][0]];
                             p.skillStats[AugSkz.skillz.skillTable.get("Mining")][0]++;
                             event.getPlayer().sendMessage("Your mining level increased to " + p.skillStats[AugSkz.skillz.skillTable.get("Mining")][0] + "!");
                             event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.LEVEL_UP, 1, 1);
                             //float[] pitchs=new float[]{8,5,3,-1,-1,-1,8,5,3,-1,-1,-1,8,5,3,-1,5,-1,3};
                             float[] pitchs = new float[]{11, -1, 18};
                             for (int r = 0; r < pitchs.length; r++) {
                             if (pitchs[r] != -1) {
                             float truePitch = (float) pow(2.0, ((double) pitchs[r] - 12.0) / 12.0);
                             AugSkz.skillz.levelUpSound(event.getPlayer(), Sound.NOTE_PIANO, 1, truePitch, r * 3);
                             }
                             }
                             }
                            
                             plugin.pSave(p.UUID);
                             if (plugin.pToggles.get(event.getPlayer().getName()) == 0) {
                             plugin.setScore(event.getPlayer().getName(), 0);
                             }*/
                        } else {
                            event.getPlayer().sendMessage("You can't carry anymore!");
                            event.setCancelled(true);
                        }
                    } else {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(plugin.tag + ChatColor.BLUE + " Required Mining Level:" + plugin.mining.blockLvlReqs[type]);
                    }

                }
                //debug
                else{
                    event.setCancelled(true);
                    event.getBlock().setType(Material.AIR);
                }
                //debug
            }
        }
    }

}
