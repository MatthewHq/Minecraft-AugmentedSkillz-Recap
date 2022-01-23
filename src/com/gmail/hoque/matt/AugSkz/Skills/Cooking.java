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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Matthew Hoque
 */
public class Cooking {

    AugSkz plugin;

    public Cooking(AugSkz plugin) {
        this.plugin = plugin;
        cookingMenu();
    }

    public void cookingMenu() {
        Inventory template = Bukkit.createInventory(null, 36, ChatColor.DARK_BLUE + "Cooking Recipes");
        ItemStack[] c = new ItemStack[36];
        c[0] = AugSkz.crafting.rec(ChatColor.YELLOW + "Bread Loaf").menu();
        c[1] = AugSkz.crafting.rec(ChatColor.GOLD + "Cook-E").menu();
        c[9] = AugSkz.crafting.rec(ChatColor.WHITE + "Steak").menu();
        c[10] = AugSkz.crafting.rec(ChatColor.WHITE + "Cooked Porkchop").menu();
        c[11] = AugSkz.crafting.rec(ChatColor.WHITE + "Cooked Chicken").menu();
        c[12] = AugSkz.crafting.rec(ChatColor.WHITE + "Cooked Fish").menu();
        c[13] = AugSkz.crafting.rec(ChatColor.WHITE + "Cooked Salmon").menu();
        c[14] = AugSkz.crafting.rec(ChatColor.WHITE + "Cooked Rabbit").menu();
        c[15] = AugSkz.crafting.rec(ChatColor.WHITE + "Cooked Mutton").menu();
        c[16] = AugSkz.crafting.rec(ChatColor.WHITE + "Baked Potato").menu();
        c[17] = AugSkz.crafting.rec(ChatColor.GOLD + "DebugCookie").menu();//noticeme
        /*        c[18] = AugSkz.crafting.rec("§ø" + ChatColor.WHITE + "Steak").menu();//noticeme
        c[19] = AugSkz.crafting.rec("§ø" + ChatColor.WHITE + "Cooked Porkchop").menu();
        c[20] = AugSkz.crafting.rec("§ø"+ChatColor.WHITE + "Cooked Chicken").menu();
        c[21] = AugSkz.crafting.rec("§ø"+ChatColor.WHITE + "Cooked Fish").menu();
        c[22] = AugSkz.crafting.rec("§ø"+ChatColor.WHITE + "Cooked Salmon").menu();
        c[23] = AugSkz.crafting.rec("§ø"+ChatColor.WHITE + "Cooked Rabbit").menu();
        c[24] = AugSkz.crafting.rec("§ø"+ChatColor.WHITE + "Cooked Mutton").menu();
        c[25] = AugSkz.crafting.rec("§ø"+ChatColor.WHITE + "Baked Potato").menu();*/

        c[27] = AugSkz.crafting.ci("return").clone();
        template.setContents(c);
        AugSkz.crafting.menus.put(ChatColor.WHITE + "Cooking", template);
    }

}
