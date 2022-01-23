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
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Matthew Hoque
 */
public class Fishing {

    AugSkz plugin;
    //====Fishing====
    boolean taggedFish = true;
    public int[] fishLvlReqs = new int[]{1, 10, 30, 20};
    public int[] fishMaterialAmount = new int[]{5, 5, 1, 1};
    public int[][] fishMultiplier = new int[][]{{2, 3, 4}, {2, 3}, {2}, {2, 3}};
    public int[][] fishMultiplierReqs = new int[][]{{40, 65, 90}, {50, 85}, {99}, {75, 95}};
    public short[] fishSpecifier = new short[]{0, 1, 3, 2};
    public Material[] fishNodeMats = new Material[]{Material.WOOL, Material.STAINED_GLASS, Material.LAPIS_BLOCK};
    public Material[] fishedMats = new Material[]{Material.RAW_FISH, Material.RAW_FISH, Material.RAW_FISH, Material.RAW_FISH};
    ///////                                         REGULAR FISH       SALMON             PUFFER             CLOWN

    public String[] fishedMatsNames = new String[]{
        "Raw Fish",
        "Raw Salmon",
        "Pufferfish",
        "Clownfish"
    };
    public int[] fishXPTable = new int[4];

    public Fishing(AugSkz plugin) {
        this.plugin = plugin;
        iniFishXPTable();
    }

    public void iniFishXPTable() {
        try {
            plugin.cfg.load(plugin.cfgFile);
        } catch (Exception e) {
        }
        ArrayList<Integer> ints = (ArrayList<Integer>) plugin.cfg.getIntegerList("FishXPTable");
        for (Integer i : ints) {
        }
        for (int i = 0; i < fishXPTable.length; i++) {
            fishXPTable[i] = ints.get(i);
        }

        //LATCHED ON
        taggedFish = plugin.cfg.getBoolean("FishTaggedDrops");
    }

    public void onPlayerFishEvent(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.IN_GROUND) {
            int type = -1;
            Location underHook = event.getHook().getLocation().getBlock().getRelative(0, -1, 0).getLocation();
            Block hookBlock = underHook.getBlock();
            for (int i = 0; i < plugin.fishing.fishNodeMats.length; i++) {
                Material mat = plugin.fishing.fishNodeMats[i];
                if (underHook.getBlock().getType().name().equals(mat.name())) {
                    type = i;
                    i = plugin.fishing.fishNodeMats.length;
                }
            }
            if (type == 0) {
                if (hookBlock.getData() != DyeColor.BLUE.getData()) {
                    type = -1;
                }
            } else if (type == 1) {
                if (hookBlock.getData() == DyeColor.BLUE.getData()) {
                    type = 3;
                } else if (hookBlock.getData() != DyeColor.CYAN.getData()) {
                    type = 1;
                }
            }
            if (type != -1) {
                Chunk tempc = event.getHook().getLocation().getChunk();
                if (plugin.cList.contains(tempc.toString())) {
                    if (plugin.fishing.fishLvlReqs[type] <= plugin.pList.get(event.getPlayer().getUniqueId().toString()).skillStats[AugSkz.skillz.skillTable.get("Fishing")][0]) {
                        if (event.getPlayer().getInventory().firstEmpty() != -1) {
                            Blockdress bl = new Blockdress(hookBlock.getType().name(), event.getHook().getLocation().getWorld().getName(),
                                    underHook.getX(), underHook.getY(), underHook.getZ(), System.currentTimeMillis() / 1000, hookBlock.getData());
                            plugin.qList.add(bl);
                            updateQueue(plugin.qList);
                            underHook.getBlock().setType(Material.WATER);
                            int multi = 1;
                            ASPlayer p = plugin.pList.get(event.getPlayer().getUniqueId().toString());
                            for (int i = plugin.fishing.fishMultiplierReqs[type].length; i > 0; i--) {
                                if (p.skillStats[AugSkz.skillz.skillTable.get("Fishing")][0] >= plugin.fishing.fishMultiplierReqs[type][i - 1]) {
                                    multi = plugin.fishing.fishMultiplier[type][i - 1];
                                    i = 0;
                                }
                            }
                            short specifier = 0;
                            if (plugin.fishing.fishedMats[type] == Material.RAW_FISH) {
                                specifier = plugin.fishing.fishSpecifier[type];
                            }
                            ItemStack i = new ItemStack(plugin.fishing.fishedMats[type], plugin.fishing.fishMaterialAmount[type] * multi, specifier);
                            if (AugSkz.fishing.taggedFish) {
                                long time = System.currentTimeMillis();
                                ItemMeta tag = i.getItemMeta();
                                tag.setDisplayName(plugin.timeToHidden(time) + "§~" + ChatColor.BLUE + fishedMatsNames[type]);
                                i.setItemMeta(tag);
                                event.getPlayer().getInventory().addItem(i);
                                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.WATER, 1, 1);
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
                            AugSkz.skillz.addXp(event.getPlayer(), p, "Fishing", plugin.fishing.fishXPTable[type]);
                            /*                  p.skillStats[AugSkz.skillz.skillTable.get("Fishing")][1] += plugin.fishing.fishXPTable[type];
                             if (p.skillStats[AugSkz.skillz.skillTable.get("Fishing")][0] < 99 && p.skillStats[AugSkz.skillz.skillTable.get("Fishing")][1] >= plugin.skillz.xpTable[p.skillStats[AugSkz.skillz.skillTable.get("Fishing")][0]]) {
                             p.skillStats[AugSkz.skillz.skillTable.get("Fishing")][1] -= plugin.skillz.xpTable[p.skillStats[AugSkz.skillz.skillTable.get("Fishing")][0]];
                             p.skillStats[AugSkz.skillz.skillTable.get("Fishing")][0]++;
                             event.getPlayer().sendMessage("Your fishing level increased to " + p.skillStats[AugSkz.skillz.skillTable.get("Fishing")][0] + "!");
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
                             if (plugin.pToggles.get(event.getPlayer().getName()) == 1) {
                             plugin.setScore(event.getPlayer().getName(), 1);
                             }*/
                        } else {
                            event.getPlayer().sendMessage("You can't carry anymore!");
                        }
                    } else {
                        event.getPlayer().sendMessage(plugin.tag + ChatColor.BLUE + " Required Fishing Level: " + plugin.fishing.fishLvlReqs[type]);
                    }

                }
            }
        } else if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            event.setCancelled(true);
        }
    }

}
