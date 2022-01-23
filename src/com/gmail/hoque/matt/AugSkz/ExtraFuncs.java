/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.hoque.matt.AugSkz;

import static com.gmail.hoque.matt.AugSkz.NodeTimerRC.plugin;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Matthew Hoque
 */
public class ExtraFuncs {

    AugSkz plugin;
    ArrayList<Material> blockTypes = new ArrayList<Material>();
    
    HashMap<String, Long> enderPearl = new HashMap<String, Long>();
    HashMap<String, Long> godApple = new HashMap<String, Long>();
    
    String main="Timeless-Isle";
    String nether = "Xoroth";
    String end= "The-Void";

    public ExtraFuncs(AugSkz plugin) {
        this.plugin = plugin;

        Bukkit.getServer().getScheduler()
                .scheduleSyncRepeatingTask(plugin, new Runnable() {

                    public void run() {

                        checkPlayers();
                    }
                }, 30, 30);

        blockTypes.add(Material.COAL_ORE);
        blockTypes.add(Material.IRON_ORE);
        blockTypes.add(Material.CLAY);
        blockTypes.add(Material.GOLD_ORE);
        blockTypes.add(Material.LAPIS_ORE);
        blockTypes.add(Material.REDSTONE_ORE);
        blockTypes.add(Material.DIAMOND_ORE);
        blockTypes.add(Material.EMERALD_ORE);

    }

    public void ProjectileHitEvent(ProjectileHitEvent event) {
        Location loc = event.getEntity().getLocation();
        if (loc.getWorld().getName().equals(nether)) {
            float power = 1;
            boolean flag = true;
            if (event.getEntityType() == EntityType.FIREBALL) {
                power = 4;
            } else if (event.getEntityType() == EntityType.SMALL_FIREBALL) {
                power = (float) .1;
            } else {
                flag = false;
            }
            if (flag) {
                event.getEntity().getLocation().getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, false, false);
            }

        }

    }

    public void onEntitySpawnEvent(EntitySpawnEvent event) {
        if (event.getLocation().getWorld().getName().equals(nether)) {
            if (event.getEntityType() == EntityType.PIG_ZOMBIE) {
                event.setCancelled(true);
            }
        }
    }

    public void onEntityDeathEvent(EntityDeathEvent event) {
        String world = event.getEntity().getLocation().getWorld().getName();
        try {
            Creature isCreature = (Creature) event.getEntity();
            if (world.equals(main) || world.equals(nether) || world.equals(end)) {
                event.setDroppedExp(0);
                event.getDrops().clear();
            }
        } catch (Exception e) {

        }

    }

    public void onPlayerInteractEvent(PlayerInteractEvent event) {

        if (event.getAction() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (event.getItem() != null && event.getItem().getType() == Material.ENDER_PEARL) {
                if (event.getPlayer().getLocation().getY() > 190) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED+"You cannot use that item in this world at that altitude!");
                } else if (enderPearl.containsKey(event.getPlayer().getName())) {
                    if (((System.currentTimeMillis() - enderPearl.get(event.getPlayer().getName())) / 1000) < 120) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.RED + "You have to wait "
                                + (120 - ((System.currentTimeMillis() - enderPearl.get(event.getPlayer().getName())) / 1000)) + " seconds before you use that again!");
                    } else {
                        enderPearl.put(event.getPlayer().getName(), System.currentTimeMillis());
                    }
                } else {
                    enderPearl.put(event.getPlayer().getName(), System.currentTimeMillis());
                }

            }
        }
    }

    public void onEntityExplodeEvent(EntityExplodeEvent event) {
        Location loc = event.getEntity().getLocation();
        if (loc.getWorld().getName().equals(main)) {
            if (event.getEntityType() == EntityType.CREEPER) {
                event.setCancelled(true);
                event.getEntity().getLocation().getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 10, false, false);
            }
        }
    }

    /*  public void onBlockBreakEvent(BlockBreakEvent event){
    if(blockTypes.contains(event.getBlock().getType())){
    event.setCancelled(true);
    event.getBlock().setType(Material.AIR);
    
    }
    }*/
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.GOLDEN_APPLE) {
            if (godApple.containsKey(event.getPlayer().getName())) {
                if (((System.currentTimeMillis() - godApple.get(event.getPlayer().getName())) / 1000) < 120) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "You have to wait "
                            + (120 - ((System.currentTimeMillis() - godApple.get(event.getPlayer().getName())) / 1000)) + " seconds before you use that again!");
                } else {
                    godApple.put(event.getPlayer().getName(), System.currentTimeMillis());
                }
            } else {
                godApple.put(event.getPlayer().getName(), System.currentTimeMillis());
            }

            /*            ItemStack x =event.getPlayer().getItemInHand().clone();
            event.setItem(new ItemStack(Material.AIR));
            event.getPlayer().setItemInHand(x);*/
        }
    }

    public void checkPlayers() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.getLocation().getWorld().getName().equals(main)) {
                double y = p.getLocation().getY();
                if (y > 225) {
                    p.setHealth(0);
                } else if (y > 215 && y < 226) {
                    p.damage(5);
                } else if (y > 210 && y < 16) {
                    p.damage(3);
                }
            }
        }
    }

}
