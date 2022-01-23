/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.hoque.matt.AugSkz.Skills;

import com.gmail.hoque.matt.AugSkz.AugSkz;
import com.gmail.hoque.matt.AugSkz.Blockdress;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Matthew Hoque
 */
public class Runecrafting {

    AugSkz plugin;
    HashMap<String, Player> mobLinker = new HashMap<String, Player>();
    HashMap<Entity, Player> pals = new HashMap<Entity, Player>();
    HashMap<String, Entity> rcEnts = new HashMap<String, Entity>();
    String[] xpTableNames = {"viscous","undead", "blast", "fire", "ghost", "nerubian", "ender"};

    int[][] amountTable = {{1, 2, 3, 4}, {1, 2, 3, 4}, {1, 2, 3}, {1, 2, 3}, {1, 2, 3}, {1, 2}, {1, 2}};
    int[][] lvlTable = {{1,40,60,75}, {5,45,65,90}, {10, 50, 70}, {15, 55, 80}, {20, 85, 98}, {25, 95}, {30, 99}};
    int[] xpTable = {7, 12, 14, 15, 17, 26, 27};
    //HashMap<String, Integer> xpTable = new HashMap<String, Integer>();

    public Runecrafting(AugSkz plugin) {
        this.plugin = plugin;
        runecraftingMenu();
    }

    public void runecraftingMenu() {
        Inventory template = Bukkit.createInventory(null, 36, ChatColor.DARK_BLUE + "Runecrafting Recipes");
        ItemStack[] c = new ItemStack[36];
        c[0] = AugSkz.crafting.rec(ChatColor.WHITE + "Rune Essence").menu();
        c[1] = AugSkz.crafting.rec(ChatColor.DARK_RED + "Bleeding Hollow").menu();
        c[2] = AugSkz.crafting.rec(ChatColor.DARK_RED + "Seeker of the Damned").menu();
        c[9] = AugSkz.crafting.rec(ChatColor.WHITE + "Ender Pearl").menu();
        c[10] = AugSkz.crafting.rec(ChatColor.WHITE + "Cobweb").menu();
        c[11] = AugSkz.crafting.rec(ChatColor.WHITE + "Slime Block").menu();
        c[12] = AugSkz.crafting.rec(ChatColor.WHITE + "Gunpowder").menu();
        c[13] = AugSkz.crafting.rec(ChatColor.WHITE + "Blaze Rod").menu();
        
        
        c[8] = AugSkz.crafting.rec(ChatColor.BLUE + "Essence Binder").menu();
        c[27] = AugSkz.crafting.ci("return").clone();
        template.setContents(c);
        AugSkz.crafting.menus.put(ChatColor.WHITE + "Runecrafting", template);

        //pigyback xptable
        /*        xpTable.put("undead", 1);
         xpTable.put("nerubian", 1);
         xpTable.put("blast", 10);
         xpTable.put("ghost", 1);
         xpTable.put("fire", 1);
         xpTable.put("viscious", 1);
         xpTable.put("ender", 1);*/
    }

    public void rc(Player p, String rune, int type) {
        int multiInd = 1;
        for (int i = lvlTable[type].length; i > 0; i--) {
            if (plugin.pList.get(p.getUniqueId().toString()).skillStats[AugSkz.skillz.skillTable.get("Runecrafting")][0] >= lvlTable[type][i - 1]) {
                multiInd = i;
                i = 0;
            }
        }

        int counter = 0;
        for (ItemStack i : p.getInventory().getContents()) {
            if (i != null && i.getItemMeta().hasDisplayName() && plugin.hiddenExtractRaw(i.getItemMeta().getDisplayName()).equals(ChatColor.WHITE + "Rune Essence")) {
                p.getInventory().remove(i);
                counter++;
            }
        }
        if (counter > 0) {
            ItemStack rc = AugSkz.crafting.ci(rune + "rune");
            rc.setAmount(counter * multiInd);
            AugSkz.skillz.addXp(p, plugin.pList.get(p.getUniqueId().toString()), "Runecrafting", xpTable[type] * counter);
            p.getInventory().addItem(rc);
        }
    }

    public void checkRC() {
        if (rcEnts != null) {
            for (String s : plugin.pairs.keySet()) {
                Entity e = rcEnts.get(s);
                /*               if (e != null && !e.isValid()) {
                 Bukkit.getServer().broadcastMessage("e!-null and e.isdead");
                 Location loc = plugin.pairs.get(s).getLocation();
                 loc.setX(loc.getBlockX() + .5);
                 loc.setZ(loc.getBlockZ() + .5);
                 ItemStack stack = AugSkz.crafting.ci(plugin.pairs.get(s).rc + "rune").clone();
                 stack.setAmount(0);
                 ItemMeta meta = stack.getItemMeta();
                 meta.setDisplayName(System.currentTimeMillis() + "");
                 stack.setItemMeta(meta);
                 Entity x = loc.getWorld().dropItem(loc, stack);
                 x.setVelocity(x.getVelocity().zero());
                 rcEnts.put(s, x);
                 } else if (e == null) {
                 Bukkit.getServer().broadcastMessage("e is null");
                 Location loc = plugin.pairs.get(s).getLocation();
                 loc.setX(loc.getBlockX() + .5);
                 loc.setZ(loc.getBlockZ() + .5);
                 ItemStack stack = AugSkz.crafting.ci(plugin.pairs.get(s).rc + "rune").clone();
                 stack.setAmount(0);
                 ItemMeta meta = stack.getItemMeta();
                 meta.setDisplayName(System.currentTimeMillis() + "");
                 stack.setItemMeta(meta);
                 Entity x = loc.getWorld().dropItem(loc, stack);
                 x.setVelocity(x.getVelocity().zero());
                 rcEnts.put(s, x);
                 }*/
                if (e != null) {
                    e.remove();
                }
                Location loc = plugin.pairs.get(s).getLocation();
                loc.setX(loc.getBlockX() + .5);
                loc.setZ(loc.getBlockZ() + .5);
                ItemStack stack = AugSkz.crafting.ci(plugin.pairs.get(s).rc + "rune").clone();
                stack.setAmount(0);
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(System.currentTimeMillis() + "");
                stack.setItemMeta(meta);
                Entity x = loc.getWorld().dropItem(loc, stack);
                x.setVelocity(x.getVelocity().zero());
                rcEnts.put(s, x);
            }
        }
    }

    public void clearRC() {
        for (String s : rcEnts.keySet()) {
            Entity e = rcEnts.get(s);
            if (e != null) {
                e.remove();
            }
        }
    }

    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getPlayer().getItemInHand().getType() == Material.MONSTER_EGG) {
            if (false) {//NOTICEME
                Blockdress loc = new Blockdress(event.getClickedBlock().getLocation());
                mobLinker.put(loc.locS(), event.getPlayer());
            } else if (event.getItem() != null && event.getItem().getItemMeta().hasLore() && event.getItem().getItemMeta().getLore().size() > 0 && event.getItem().getItemMeta().getLore().get(0).equals(ChatColor.BLUE + "Volatile")) {
                event.setCancelled(true);
                int runeCount = event.getPlayer().getItemInHand().getAmount();
                ItemStack inHand = event.getPlayer().getInventory().getItemInHand();
                if (runeCount > 1) {
                    event.getPlayer().getItemInHand().setAmount(runeCount - 1);
                } else if (runeCount == 1) {
                    event.getPlayer().getInventory().removeItem(new ItemStack[]{event.getPlayer().getInventory().getItemInHand()});
                }
            }
        }
        if (event.getClickedBlock() != null && event.getItem() != null && event.getItem().getType() == Material.BLAZE_ROD
                && event.getItem().getItemMeta().hasDisplayName()
                && (plugin.hiddenExtractRaw(event.getItem().getItemMeta().getDisplayName())).equals(ChatColor.BLUE + "Essence Binder")) {
            Blockdress bl = new Blockdress(event.getClickedBlock().getLocation());
            if (plugin.pairs.containsKey(bl.locS())) {
                int type = xpTableNames.length;
                for (int i = 0; i < xpTableNames.length; i++) {
                    if (plugin.pairs.get(bl.locS()).rc.equals(xpTableNames[i])) {
                        type = i;
                    }
                }
                if (lvlTable[type][0] <= plugin.pList.get(event.getPlayer().getUniqueId().toString()).skillStats[AugSkz.skillz.skillTable.get("Runecrafting")][0]) {
                    rc(event.getPlayer(), plugin.pairs.get(bl.locS()).rc, type);
                } else {
                    event.getPlayer().sendMessage(plugin.tag + ChatColor.BLUE + " Required Runecrafting Level:" + lvlTable[type][0]);
                }
            }

        }
    }

    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            //event.setCancelled(true); //NOTICEME
            Blockdress loc = new Blockdress(event.getLocation().getBlock().getRelative(0, -1, 0).getLocation());
            Bukkit.getServer().broadcastMessage(loc.locS() + "CREATURE"); //NOTICEME
            Player p = mobLinker.get(loc.locS());
            mobLinker.remove(loc.locS());
            pals.put(event.getEntity(), p);
            //event.getEntity().setPassenger(p);
            Creature drink = (Creature) event.getEntity();
            PotionEffect pot = new PotionEffect(PotionEffectType.SPEED, 600, 2);
            drink.addPotionEffect(pot);
            //drink.setLeashHolder(p);
            //drink.setTarget(p);
        }
    }

    public void onEntityTargetLivingEntityEvent(EntityTargetLivingEntityEvent event) {
        if (event.getEntityType().isAlive()) {
            Creature ent = (Creature) event.getEntity();
            if (pals.containsKey(event.getEntity()) && pals.get(event.getEntity()) == event.getTarget()) {
                //Bukkit.getServer().getLogger().info(ent.getTarget().toString());//NOTICEME;
                event.setCancelled(true);
            }
        }
    }

}
