/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.hoque.matt.AugSkz;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Matthew Hoque
 */
public class Eventor implements Listener {
    AugSkz plugin;
    
    public Eventor(AugSkz instance) {
        plugin = instance;
    }
    
    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        //AugSkz.funcs.onBlockBreakEvent(event);
        AugSkz.mining.onBlockBreakEvent(event);
        AugSkz.farming.onBlockBreakEvent(event);
        
    }
    
    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        AugSkz.crafting.onBlockPlaceEvent(event);
        AugSkz.farming.onBlockPlaceEvent(event);
    }
    
    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        
    }
    
//    @EventHandler
//    public void onEntityTargetLivingEvent(EntityTargetLivingEntityEvent event) {
//        AugSkz.runecrafting.onEntityTargetLivingEntityEvent(event);
//    }
    
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        plugin.pLoad(event.getPlayer().getUniqueId().toString(), event.getPlayer().getName().toString());
        plugin.pToggles.put(event.getPlayer().getName(), -1);
    }
    
    @EventHandler
    public void onPlayerFishEvent(PlayerFishEvent event) {
        AugSkz.fishing.onPlayerFishEvent(event);
    }
    
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        AugSkz.crafting.onPlayerQuitEvent(event);
        if (event.getPlayer().getName().equals("Portalz") && plugin.portalzFlag) {
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
    
//    @EventHandler
//    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
//        AugSkz.runecrafting.onCreatureSpawnEvent(event);
//    }
    
    
    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        /*        if (event.getItem().equals((Item) plugin.id)) {
         event.setCancelled(true);
         event.getPlayer().damage(1);
         }*/
        AugSkz.crafting.onPlayerPickupItemEvent(event);
    }
    
    @EventHandler
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        AugSkz.crafting.onPlayerItemConsumeEvent(event);
        AugSkz.funcs.onPlayerItemConsumeEvent(event);
    }
    
    @EventHandler
    public void onFurnaceBurnEvent(FurnaceBurnEvent event) {
        AugSkz.crafting.onFurnaceBurnEvent(event);
    }
    
    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent event) {
        AugSkz.crafting.onInventoryDragEvent(event);
    }
    
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        AugSkz.funcs.onPlayerInteractEvent(event);
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            AugSkz.crafting.onPlayerInteractEvent(event);
            AugSkz.runecrafting.onPlayerInteractEvent(event);
            
            if (event.getPlayer().getItemInHand().getType() == Material.WOOD_PICKAXE) {
                if ((this.plugin.registering.get(event.getPlayer().getName()) != null)) {
                    if ((this.plugin.registering.get(event.getPlayer().getName())) == 1) {
                        Blockdress sign = new Blockdress(event.getClickedBlock().getLocation());
                        plugin.temp1 = sign.locS();
                        this.plugin.registering.put(event.getPlayer().getName(), 2);
                        event.getPlayer().sendMessage(ChatColor.BLUE + "ClickBlock Registered now select ObjectBlock");
                    } else if ((this.plugin.registering.get(event.getPlayer().getName())) == 3) {
                        Blockdress sign = new Blockdress(event.getClickedBlock().getLocation());
                        if (this.plugin.pairs.containsKey(sign.locS())) {
                            event.getPlayer().sendMessage(ChatColor.BLUE + "This ClickBlock is paired to an ObjectBlock");
                        } else {
                            event.getPlayer().sendMessage(ChatColor.BLUE + "This block is" + ChatColor.DARK_RED + "NOT" + ChatColor.BLUE + "paired");
                        }
                        Integer put = this.plugin.registering.put(event.getPlayer().getName(), 0);
                    } else if ((this.plugin.registering.get(event.getPlayer().getName())) == 4) {
                        Blockdress sign = new Blockdress(event.getClickedBlock().getLocation());
                        this.plugin.pairs.remove(sign.locS());
                        this.plugin.updatePairs(plugin.pairs);
                        this.plugin.registering.put(event.getPlayer().getName(), 0);
                        event.getPlayer().sendMessage(ChatColor.BLUE + "Block Cleared");
                    } else if (((this.plugin.registering.get(event.getPlayer().getName())) == 2)) {
                        Blockdress bl = new Blockdress(event.getClickedBlock().getLocation(), plugin.temp3);
                        this.plugin.temp2 = bl;
                        this.plugin.pairs.put(this.plugin.temp1, this.plugin.temp2);
                        this.plugin.registering.put(event.getPlayer().getName(), 0);
                        this.plugin.updatePairs(this.plugin.pairs);
                        event.getPlayer().sendMessage(ChatColor.BLUE + "Registration Complete");
                    }
                }
            }
        }
    }

    //NOTICEME DEBUG
    
    
    @EventHandler
    public void ProjectileHitEvent(ProjectileHitEvent event){
        AugSkz.funcs.ProjectileHitEvent(event);
    }
    
    @EventHandler
    public void onEntitySpawnEvent(EntitySpawnEvent event){
        AugSkz.funcs.onEntitySpawnEvent(event);
    }
    
    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event){
    AugSkz.funcs.onEntityDeathEvent(event);
    
    }
    
    @EventHandler
    public void onEntityExplodeEvent(EntityExplodeEvent event){
        AugSkz.funcs.onEntityExplodeEvent(event);
    }
    
    /*@EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
    //Bukkit.getServer().broadcastMessage(event.getMessage());
    if (plugin.noticeme.containsKey(event.getMessage())) {
    killarino(event.getMessage());
    }
    
    }*/
    
    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        AugSkz.crafting.onInventoryCloseEvent(event);
    }
    
    @EventHandler
    public void onInventoryOpenEvent(InventoryOpenEvent event) {
    }
    
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        
        AugSkz.crafting.onInventoryClickEvent(event);
    }
    
    @EventHandler
    public void CraftItemEvent(CraftItemEvent event) {//NOTICEMENOTDONE

        //event.setCancelled(true);
    }

    //NOTICME DARKSIDE
    public Integer killarino(final String x) {
        int timerID = plugin.getServer().getScheduler()
                .scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        Entity e = plugin.noticeme.get(x);
                        e.remove();
                        Creature test = (Creature) plugin.noticeme.get(x + "a");
                        test.damage(20);
                        plugin.noticeme.remove(x);
                        plugin.noticeme.remove(x + "a");
                    }
                }, 1);
        return timerID;
    }
    //NOTICME DARKSIDE
}
