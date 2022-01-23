/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.hoque.matt.AugSkz.Skills;

import com.gmail.hoque.matt.AugSkz.AugSkz;
import com.gmail.hoque.matt.AugSkz.Blockdress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Matthew Hoque
 */
public class Crafting {

    AugSkz plugin;
    int craftingInventoryRows = 4;
    HashMap<String, Integer> crafting = new HashMap<String, Integer>();
    HashMap<String, Integer> craftXPTable = new HashMap<String, Integer>();
    HashMap<String, ItemStack> cutomItems = new HashMap<String, ItemStack>();
    HashMap<String, Recipez> recipeList = new HashMap<String, Recipez>();
    ArrayList<String> craftInventoryBan = new ArrayList<String>();
    HashMap<String, Integer> furnaceInventoryBan = new HashMap<String, Integer>();
    ArrayList<String> placeBan = new ArrayList<String>();
    HashMap<String, Inventory> menus = new HashMap<String, Inventory>();
    HashMap<String, String> oneBack = new HashMap<String, String>();
    ArrayList<String> skillMenus = new ArrayList<String>();
    public HashMap<String, String> seeker = new HashMap<String, String>();
    HashMap<String, Integer> seekTimer = new HashMap<String, Integer>();
    public HashMap<String, Integer> isSeeking = new HashMap<String, Integer>();
    HashMap<String, Integer> seekWorlds = new HashMap<String, Integer>();
    HashMap<String, Integer> seekLogout = new HashMap<String, Integer>();
    HashMap<ItemStack, ArrayList<ItemStack>> matChes = new HashMap<ItemStack, ArrayList<ItemStack>>();

    public Crafting(AugSkz plugin) {
        this.plugin = plugin;
        iniCustomItems();
        iniRecipesCrafting();
        iniRecipes();
        iniBans();
        craftingMenu();
        skillMenu();
        iniMatches();
    }

    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        ItemStack inHand = event.getPlayer().getItemInHand();
        if (inHand != null && inHand.getType() == Material.WORKBENCH && event.getAction() == Action.RIGHT_CLICK_BLOCK && !event.getPlayer().isSneaking()
                && event.getClickedBlock().getType() != null
                && event.getClickedBlock().getType() == Material.WORKBENCH) {
            event.setCancelled(true);
            event.getPlayer().openInventory(iClone("Skills"));
        } else if (event.getPlayer().getItemInHand().getType() == Material.EYE_OF_ENDER) {
            if (inHand.getItemMeta().hasDisplayName() && plugin.hiddenExtractRaw(inHand.getItemMeta().getDisplayName()).equals(ChatColor.DARK_RED + "Seeker of the Damned")) {
                event.setCancelled(true);
                if (seeker.containsKey(event.getPlayer().getName())) {

                    if (plugin.getServer().getOnlinePlayers().contains(plugin.getServer().getPlayer(seeker.get(event.getPlayer().getName())))) {//here
                        Location firstLoc = plugin.getServer().getPlayer((seeker.get(event.getPlayer().getName()))).getLocation();
//                      event.getPlayer().getItemInHand().setType(Material.COMPASS);
//                      event.getPlayer().getItemInHand().setAmount(0);
                        event.getPlayer().getInventory().removeItem(new ItemStack[]{event.getPlayer().getInventory().getItemInHand()});
//                      event.getPlayer().setCompassTarget(Bukkit.getServer().getPlayer(seeker.get(event.getPlayer().getName())).getLocation());
                        if (!isSeeking.containsKey(event.getPlayer().getName())) {
                            isSeeking.put(event.getPlayer().getName(), 0);
                        }
                        if (!seekWorlds.containsKey(event.getPlayer().getName())) {
                            seekWorlds.put(event.getPlayer().getName(), 0);
                        }
                        if (!seekLogout.containsKey(event.getPlayer().getName())) {
                            seekLogout.put(event.getPlayer().getName(), 0);
                        }
                        if (isSeeking.get(event.getPlayer().getName()) == 0) {
                            if (seekTimer.containsKey(event.getPlayer().getName())) {
                                seekTimer.put(event.getPlayer().getName(), seekTimer.get(event.getPlayer().getName()) + 30);
                            } else {
                                seekTimer.put(event.getPlayer().getName(), 30);
                            }
                            seeking(event.getPlayer().getName(), seeker.get(event.getPlayer().getName()), 20, firstLoc);
                            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENDERDRAGON_GROWL, (float) .5, 5);
                            isSeeking.put(event.getPlayer().getName(), 1);
                        } else if (isSeeking.get(event.getPlayer().getName()) == 1) {
                            seekTimer.put(event.getPlayer().getName(), seekTimer.get(event.getPlayer().getName()) + 30);
                            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENDERDRAGON_GROWL, (float) .5, 5);
                        }

                    } else {
                        event.getPlayer().sendMessage(plugin.tag + ChatColor.RED + "Your prey is not online");
                    }//here

                } else {
                    event.getPlayer().sendMessage(plugin.tag + ChatColor.RED + "You need to define your prey first!");
                    event.getPlayer().sendMessage(ChatColor.RED + "                          type /as seek <playername>");
                }
            }
        }
    }

    public void onBlockPlaceEvent(BlockPlaceEvent event) {

        if (placeBan.contains(event.getItemInHand().getItemMeta().getDisplayName())) {
            event.setCancelled(true);
        }

    }

    public void onInventoryClickEvent(InventoryClickEvent event) {
//        Bukkit.getServer().broadcastMessage("test " + event.getSlotType().name() + " " + event.getInventory().getType().name());//noticeme
        //Bukkit.getServer().getLogger().info("a");//NOTICEME
        //========================CRAFTINVENTORY SYSTEM BAN START============================
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            if (event.getInventory().getName().equals(ChatColor.DARK_BLUE + "Skills")) {
                if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
                    event.setCancelled(true);
                    event.getWhoClicked().openInventory(iClone(event.getCurrentItem().getItemMeta().getDisplayName()));

                } else {
                    event.setCancelled(true);
                }

            } else if (skillMenus.contains(event.getInventory().getName())) {
                if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
                    event.setCancelled(true);
                    if (event.getCurrentItem().getType() != Material.BARRIER) {
                        oneBack.put(event.getWhoClicked().getName(), event.getInventory().getName());//NOTICEME
                        Inventory crafter = craftingInventory(event.getCurrentItem().getItemMeta().getDisplayName());
                        event.getWhoClicked().openInventory(crafter);
                    } else {
                        event.setCancelled(true);
                        event.getWhoClicked().openInventory(iClone("Skills"));
                    }

                } else {
                    event.setCancelled(true);
                }

            } else if (event.getInventory().getName().equals(ChatColor.DARK_BLUE + "Amount       Materials for 1")) {
                if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
                    if (event.getCurrentItem().getItemMeta().hasDisplayName()
                            && event.getCurrentItem().getItemMeta().getDisplayName().contains(ChatColor.GREEN + "Confirm")) {
                        Recipez toCraft = rec(event.getCurrentItem().getItemMeta().getLore().get(0));
                        int index = toCraft.levels.length;
                        for (int i = 0; i < toCraft.levels.length; i++) {
                            if (toCraft.levels[i] == event.getCurrentItem().getAmount()) {
                                index = i;
                            }
                        }
                        if (hasLvls(toCraft, (Player) event.getWhoClicked(), index)) {//craftLvl >= toCraft.levels[1][index]
                            boolean hasMats = true;
                            for (ItemStack stax : toCraft.ings) {
                                if (!containsAtLeastx((Player) event.getWhoClicked(), stax, stax.getAmount() * event.getCurrentItem().getAmount())/*event.getWhoClicked().getInventory().containsAtLeast(stax, stax.getAmount() * event.getCurrentItem().getAmount())*/) {
                                    hasMats = false;
                                }
                            }
                            if (hasMats) {
                                event.setCancelled(true);
                                Inventory toOpen = craftingAction();
                                event.getWhoClicked().openInventory(toOpen);
                                crafting.put(event.getWhoClicked().getName(), 0);
                                keepCrafting((Player) event.getWhoClicked(), toOpen, toCraft.prod.getItemMeta().getDisplayName(), 5, toCraft.craftTime, event.getCurrentItem().getAmount());
                            } else {
                                event.setCancelled(true);
                                event.getWhoClicked().sendMessage(ChatColor.RED + "You do not have the right ingredients!");
                            }
                        } else {
                            event.setCancelled(true);
                            event.getWhoClicked().sendMessage(ChatColor.RED + "You do not have the required skills for that!");
                        }
                    } else {
                        event.setCancelled(true);
                        if (event.getCurrentItem().getType() == Material.BARRIER) {
                            event.getWhoClicked().openInventory(iClone(ChatColor.WHITE + extract(oneBack.get(event.getWhoClicked().getName()))));
                        }
                    }

                } else {
                    event.setCancelled(true);
                }
            } else if (event.getInventory().getName().equals(ChatColor.DARK_BLUE + "In Progress")) {
                event.setCancelled(true);
            }
        }

        //========================CRAFTINVENTORY SYSTEM BAN END INCOMEPLETE?============================NOTICEME
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            if ((event.getInventory().getType() == InventoryType.WORKBENCH
                    || event.getInventory().getType() == InventoryType.ANVIL)
                    && event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (craftInventoryBan.contains(event.getCurrentItem().getItemMeta().getDisplayName())) {
                    event.setCancelled(true);
                }
            }
        }

        /*        if ((event.getInventory().getType() == InventoryType.CRAFTING && event.getSlotType() == SlotType.CRAFTING)) {
         if (event.getCurrentItem() != null) {
         if (event.getCurrentItem().hasItemMeta()) {
         if (craftInventoryBan.contains(event.getCurrentItem().getItemMeta().getDisplayName())
         || craftInventoryBan.contains(plugin.hiddenExtractRaw(event.getCurrentItem().getItemMeta().getDisplayName()))) {
         event.setCancelled(true);
         }
         }
         }
         }*/
        if (event.getCursor() != null && event.getInventory().getType() == InventoryType.CRAFTING && event.getSlotType() == SlotType.CRAFTING) {
            if (event.getCursor().hasItemMeta()) {
                if (craftInventoryBan.contains(event.getCursor().getItemMeta().getDisplayName())
                        || craftInventoryBan.contains(plugin.hiddenExtractRaw(event.getCursor().getItemMeta().getDisplayName()))) {
                    event.setCancelled(true);
                } else if ((ChatColor.BLUE + "Clay Ball").equals(event.getCursor().getItemMeta().getDisplayName())
                        || (ChatColor.BLUE + "Clay Ball").equals(plugin.hiddenExtractRaw(event.getCursor().getItemMeta().getDisplayName()))) {
                    event.setCancelled(true);
                }
            }
        }

    }
    //========================CRAFTINVENTORY SYSTEM BAN END INCOMPLETE?============================ NOTICEME

    public void onInventoryDragEvent(InventoryDragEvent event) {
        if (event.getInventory().getType() == InventoryType.WORKBENCH) {
            if (event.getOldCursor().hasItemMeta()
                    && (craftInventoryBan.contains(event.getOldCursor().getItemMeta().getDisplayName())
                    || craftInventoryBan.contains(plugin.hiddenExtractRaw(event.getOldCursor().getItemMeta().getDisplayName())))) {
                event.setCancelled(true);
            }
        } else if (event.getInventory().getType() == InventoryType.CRAFTING) {
            if (event.getOldCursor().hasItemMeta()
                    && (craftInventoryBan.contains(event.getOldCursor().getItemMeta().getDisplayName())
                    || craftInventoryBan.contains(plugin.hiddenExtractRaw(event.getOldCursor().getItemMeta().getDisplayName())))) {
                event.setCancelled(true);
            } else if (event.getOldCursor().hasItemMeta()
                    && ((ChatColor.BLUE + "Clay Ball").equals(event.getOldCursor().getItemMeta().getDisplayName()) || (ChatColor.BLUE + "Clay Ball").equals(plugin.hiddenExtractRaw(event.getOldCursor().getItemMeta().getDisplayName())))) {
                event.setCancelled(true);
            }
        }
    }

    public void onFurnaceBurnEvent(FurnaceBurnEvent event) {
        if (event.getFuel().getItemMeta().hasDisplayName() && furnaceInventoryBan.containsKey(event.getFuel().getItemMeta().getDisplayName())) {
            event.setBurnTime(furnaceInventoryBan.get(event.getFuel().getItemMeta().getDisplayName()));
        }
    }

    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        event.getPlayer().closeInventory();
    }

    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        crafting.put(event.getPlayer().getName(), -1);
    }

    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        if (event.getItem().getItemMeta().hasDisplayName()) {
            if ((plugin.hiddenExtractRaw(event.getItem().getItemMeta().getDisplayName())).equals(ChatColor.GOLD + "Cook-E")) {
                ItemStack sub = event.getItem().clone();
                event.setItem(new ItemStack(Material.BREAD));
                event.getPlayer().setItemInHand(new ItemStack(Material.STONE));
                event.getPlayer().setItemInHand(sub);
                event.getPlayer().setFoodLevel(20);
                event.getPlayer().setSaturation(20);
            } else if ((event.getItem().getItemMeta().getDisplayName()).equals(ChatColor.YELLOW + "Bread Loaf")) {
                ItemStack sub = event.getItem().clone();
                event.getPlayer().setFoodLevel(20);
                event.getPlayer().setSaturation(20);
            } else if (plugin.hiddenExtractPoison(event.getItem().getItemMeta().getDisplayName())) {
                PotionEffect pe = new PotionEffect(PotionEffectType.POISON, 200, 1);
                event.getPlayer().addPotionEffect(pe);
            }
        }
    }

    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        if (event.getPlayer().getOpenInventory().getTitle().equals(ChatColor.DARK_BLUE + "In Progress")) {
            event.setCancelled(true);
        }
    }

    public Inventory craftingAction() {
        Inventory template = Bukkit.createInventory(null, 9, ChatColor.DARK_BLUE + "In Progress");
        ItemStack[] c = new ItemStack[9];
        return template;
    }

    public void craftingMenu() {
        Inventory template = Bukkit.createInventory(null, 36, ChatColor.DARK_BLUE + "Crafting Recipes");
        ItemStack[] c = new ItemStack[36];
        c[1] = rec(ChatColor.YELLOW + "True Gold").menu();
        c[0] = rec(ChatColor.YELLOW + "True Iron").menu();
        c[2] = rec(ChatColor.YELLOW + "True Diamond").menu();
        c[7] = rec(ChatColor.YELLOW + "Augmented Hoe").menu();
        c[8] = rec(ChatColor.YELLOW + "Augmented Pickaxe").menu();
        c[9] = rec(ChatColor.GRAY + "Diffused Coal").menu();
        c[10] = rec(ChatColor.WHITE + "Arrow").menu();
        c[27] = ci("return").clone();
        template.setContents(c);
        menus.put(ChatColor.WHITE + "Crafting", template);
    }

    public void skillMenu() {
        Inventory templatezz = Bukkit.createInventory(null, 9, ChatColor.DARK_BLUE + "Skills");
        ItemStack[] c = new ItemStack[9];
        ItemStack x = new ItemStack(Material.WORKBENCH);
        ItemMeta meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Crafting");
        skillMenus.add(ChatColor.DARK_BLUE + "Crafting Recipes");
        x.setItemMeta(meta);
        c[0] = x.clone();

        x.setType(Material.BREAD);
        meta.setDisplayName(ChatColor.WHITE + "Cooking");
        skillMenus.add(ChatColor.DARK_BLUE + "Cooking Recipes");
        x.setItemMeta(meta);
        c[1] = x.clone();

        x.setType(Material.GOLD_HOE);
        meta.setDisplayName(ChatColor.WHITE + "Farming");
        skillMenus.add(ChatColor.DARK_BLUE + "Farming Recipes");
        x.setItemMeta(meta);
        c[2] = x.clone();
        templatezz.setContents(c);

        x = new ItemStack(Material.INK_SACK, 1, (byte) 7);
        //MaterialData dat=new MaterialData(Material.INK_SACK,(byte)7);
        //x.setData(dat);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Runecrafting");
        skillMenus.add(ChatColor.DARK_BLUE + "Runecrafting Recipes");
        x.setItemMeta(meta);
        c[3] = x.clone();
        templatezz.setContents(c);

        menus.put("Skills", templatezz);
    }

    public void iniRecipes() {
        Iterator<Recipe> it = plugin.getServer().recipeIterator();
        Recipe recipe;
        while (it.hasNext()) {
            recipe = it.next();
            if (recipe != null && (recipe.getResult().getType() == Material.GOLD_PICKAXE || recipe.getResult().getType() == Material.GOLD_HOE)) {
                it.remove();
            }
        }

        /*ItemStack goldPick = new ItemStack(Material.GOLD_PICKAXE);
         ItemMeta gold = goldPick.getItemMeta();
         gold.setDisplayName(ChatColor.YELLOW + "TEST LEL");
         goldPick.setItemMeta(gold);
         ShapedRecipe goldPickShaped = new ShapedRecipe(goldPick);
         goldPickShaped.shape("ABA", " B ", " B ");
         goldPickShaped.setIngredient('A', Material.GOLD_BLOCK);
         //goldPickShaped.setIngredient('N', Material.AIR);
         goldPickShaped.setIngredient('B', Material.IRON_BLOCK);
         plugin.getServer().addRecipe(goldPickShaped);*/
    }

    public Inventory craftingInventory(String name) {
        Inventory template = Bukkit.createInventory(null, craftingInventoryRows * 9, ChatColor.DARK_BLUE + "Amount       Materials for 1");
        ItemStack temp;
        Recipez toCraft = rec(name);
        ItemStack[] ings = toCraft.ings;
        ItemStack[] c = new ItemStack[craftingInventoryRows * 9];
        ItemStack Divider = new ItemStack(Material.BANNER, 1, (short) 16);
        ItemMeta divMeta = Divider.getItemMeta();
        divMeta.setDisplayName(ChatColor.GRAY + "Divider (ignore)");
        Divider.setItemMeta(divMeta);
        c[3] = Divider;
        c[12] = c[3];
        c[21] = c[3];
        c[30] = c[3];
        c[27] = ci("return").clone();

        temp = rec(name).prod.clone();
        ItemMeta meta = temp.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Confirm");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(name);
        meta.setLore(lore);
        temp.setItemMeta(meta);
        c[0] = temp;

        int counter = 0;
        int j = 0;
        for (int i = 0; i < toCraft.levels.length; i++) {
            c[(counter * 9) + j] = toCraft.cInv(i);
            if (j == 2) {
                counter++;
                j = 0;
            } else {
                j++;
            }
        }
        counter = 0;
        j = 0;
        for (int i = 0; i < ings.length; i++) {
            c[(counter * 9) + 4 + j] = ings[i];
            if (j == 4) {
                counter++;
                j = 0;
            } else {
                j++;
            }
        }

        template.setContents(c);
        return template;
    }

    public ItemStack ci(String name) {
        return cutomItems.get(name);
    }

    public Recipez rec(String name) {
        return recipeList.get(name);
    }

    public void iniBans() {
        craftInventoryBan.add(ChatColor.YELLOW + "True Iron");
        craftInventoryBan.add(ChatColor.YELLOW + "True Gold");
        craftInventoryBan.add(ChatColor.YELLOW + "True Diamond");
        craftInventoryBan.add(ChatColor.GRAY + "Diffused Coal");

        furnaceInventoryBan.put(ChatColor.GRAY + "Diffused Coal", 200);

        placeBan.add(ChatColor.YELLOW + "True Iron");
        placeBan.add(ChatColor.YELLOW + "True Gold");
        placeBan.add(ChatColor.YELLOW + "True Diamond");
    }

    //IMPORTANT NOTE
    //IMPORTANT NOTE
    //IMPORTANT NOTE
    //IMPORTANT NOTE
    //IMPORTANT NOTE
    //IMPORTANT NOTE
    //IMPORTANT NOTE
    //IMPORTANT NOTE
    //IMPORTANT NOTE
    //IMPORTANT NOTE
    //IMPORTANT NOTE


    // The following repetitive code was due to time constraints and availability 
    // of assistants who were not familiar with coding. This is the only section
    // with outside contributions. 
    // iniMatches, iniCustomItems, and iniRecipesCrafting

    //IMPORTANT NOTE
    //IMPORTANT NOTE
    //IMPORTANT NOTE
    //IMPORTANT NOTE
    //IMPORTANT NOTE
    //IMPORTANT NOTE
    //IMPORTANT NOTE
    //IMPORTANT NOTE
    //IMPORTANT NOTE

    public void iniMatches() {
        ArrayList<ItemStack> stax;
        ItemStack key;
        ItemStack i;
        ItemMeta meta;
        ItemMeta keyMeta;

        key = new ItemStack(Material.COAL);
        keyMeta = key.getItemMeta();
        //keyMeta.setDisplayName(ChatColor.WHITE + "Coal");
        key.setItemMeta(keyMeta);
        stax = new ArrayList<ItemStack>();
        i = new ItemStack(Material.COAL);
        meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Coal");
        i.setItemMeta(meta);
        stax.add(i);
        matChes.put(key, stax);

        key = new ItemStack(Material.CLAY_BALL);
        keyMeta = key.getItemMeta();
        //keyMeta.setDisplayName(ChatColor.WHITE + "Clay Ball");
        key.setItemMeta(keyMeta);
        stax = new ArrayList<ItemStack>();
        i = new ItemStack(Material.CLAY_BALL);
        meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Clay Ball");
        i.setItemMeta(meta);
        stax.add(i);
        matChes.put(key, stax);

        key = new ItemStack(Material.IRON_ORE);
        keyMeta = key.getItemMeta();
//        keyMeta.setDisplayName(ChatColor.WHITE + "Iron Ore");
        key.setItemMeta(keyMeta);
        stax = new ArrayList<ItemStack>();
        i = new ItemStack(Material.IRON_ORE);
        meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Iron Ore");
        i.setItemMeta(meta);
        stax.add(i);
        matChes.put(key, stax);

        key = new ItemStack(Material.REDSTONE);
        keyMeta = key.getItemMeta();
//        keyMeta.setDisplayName(ChatColor.WHITE + "Redstone");
        key.setItemMeta(keyMeta);
        stax = new ArrayList<ItemStack>();
        i = new ItemStack(Material.REDSTONE);
        meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Redstone");
        i.setItemMeta(meta);
        stax.add(i);
        matChes.put(key, stax);

        key = new ItemStack(Material.GOLD_ORE);
        keyMeta = key.getItemMeta();
//        keyMeta.setDisplayName(ChatColor.WHITE + "Gold Ore");
        key.setItemMeta(keyMeta);
        stax = new ArrayList<ItemStack>();
        i = new ItemStack(Material.GOLD_ORE);
        meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Gold Ore");
        i.setItemMeta(meta);
        stax.add(i);
        matChes.put(key, stax);

        key = new ItemStack(Material.LAPIS_BLOCK);
        keyMeta = key.getItemMeta();
//        keyMeta.setDisplayName(ChatColor.WHITE + "Lapis Block");
        key.setItemMeta(keyMeta);
        stax = new ArrayList<ItemStack>();
        i = new ItemStack(Material.LAPIS_BLOCK);
        meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Lapis Block");
        i.setItemMeta(meta);
        stax.add(i);
        matChes.put(key, stax);

        key = new ItemStack(Material.DIAMOND);
        keyMeta = key.getItemMeta();
//        keyMeta.setDisplayName(ChatColor.WHITE + "Diamond");
        key.setItemMeta(keyMeta);
        stax = new ArrayList<ItemStack>();
        i = new ItemStack(Material.DIAMOND);
        meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Diamond");
        i.setItemMeta(meta);
        stax.add(i);
        matChes.put(key, stax);

        key = new ItemStack(Material.EMERALD);
        keyMeta = key.getItemMeta();
//        keyMeta.setDisplayName(ChatColor.WHITE + "Emerald");
        key.setItemMeta(keyMeta);
        stax = new ArrayList<ItemStack>();
        i = new ItemStack(Material.EMERALD);
        meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Emerald");
        i.setItemMeta(meta);
        stax.add(i);
        matChes.put(key, stax);

        key = new ItemStack(Material.RAW_FISH);
        keyMeta = key.getItemMeta();
//        keyMeta.setDisplayName(ChatColor.WHITE + "Raw Fish");
        key.setItemMeta(keyMeta);
        stax = new ArrayList<ItemStack>();
        i = new ItemStack(Material.RAW_FISH);
        meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Raw Fish");
        i.setItemMeta(meta);
        stax.add(i);
        matChes.put(key, stax);

        key = new ItemStack(Material.RAW_FISH, 1, (byte) 1);
        keyMeta = key.getItemMeta();
//        keyMeta.setDisplayName(ChatColor.WHITE + "Raw Salmon");
        key.setItemMeta(keyMeta);
        stax = new ArrayList<ItemStack>();
        i = new ItemStack(Material.RAW_FISH, 1, (byte) 1);
        meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Raw Salmon");
        i.setItemMeta(meta);
        stax.add(i);
        matChes.put(key, stax);

        key = new ItemStack(Material.RAW_FISH, 1, (byte) 3);
        keyMeta = key.getItemMeta();
//        keyMeta.setDisplayName(ChatColor.WHITE + "Raw Pufferfish");
        key.setItemMeta(keyMeta);
        stax = new ArrayList<ItemStack>();
        i = new ItemStack(Material.RAW_FISH, 1, (byte) 3);
        meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Pufferfish");
        i.setItemMeta(meta);
        stax.add(i);
        matChes.put(key, stax);

        key = new ItemStack(Material.RAW_FISH, 1, (byte) 2);
        keyMeta = key.getItemMeta();
//        keyMeta.setDisplayName(ChatColor.WHITE + "Clownfish");
        key.setItemMeta(keyMeta);
        stax = new ArrayList<ItemStack>();
        i = new ItemStack(Material.RAW_FISH, 1, (byte) 2);
        meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Clownfish");
        i.setItemMeta(meta);
        stax.add(i);
        matChes.put(key, stax);
    }

    public void iniCustomItems() {
        ItemStack x = new ItemStack(Material.BARRIER);
        ItemMeta meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Return");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("return", x);

        x = new ItemStack(Material.GOLD_PICKAXE);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Augmented Pickaxe");
//        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
//        meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("augmentedpickaxe", x);

        x = new ItemStack(Material.GOLD_HOE);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Augmented Hoe");
//        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
//        meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("augmentedhoe", x);

        x = new ItemStack(Material.ARROW, 8);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Arrow");
//        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
//        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
//        meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("arrow", x);

        x = new ItemStack(Material.GOLD_BLOCK);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "True Gold");
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("truegold", x);

        x = new ItemStack(Material.IRON_BLOCK);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "True Iron");
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("trueiron", x);

        x = new ItemStack(Material.DIAMOND_BLOCK);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "True Diamond");
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("truediamond", x);

        x = new ItemStack(Material.BREAD);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Bread Loaf");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.BLUE + "Crafting Ingredient");
        meta.setLore(lore);
        x.setItemMeta(meta);
        cutomItems.put("breadloaf", x);

        x = new ItemStack(Material.COAL, 8, (byte) 1);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "Diffused Coal");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        lore = new ArrayList<String>();
        lore.add(ChatColor.BLUE + "Ingredient");
        meta.setLore(lore);
        x.setItemMeta(meta);
        cutomItems.put("diffusedcoal", x);

        x = new ItemStack(Material.COOKIE);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Cook-E");
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("cooke", x);

        x = new ItemStack(Material.ENDER_PEARL);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Ender Pearl");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("enderpearl", x);

        x = new ItemStack(Material.WEB);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Cobweb");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("cobweb", x);

        x = new ItemStack(Material.SLIME_BLOCK);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Slime Block");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("slimeblock", x);

        x = new ItemStack(Material.SULPHUR);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Gunpowder");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("gunpowder", x);

        x = new ItemStack(Material.BLAZE_ROD);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Blaze Rod");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("blazerod", x);

        x = new ItemStack(Material.RED_ROSE);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "Bleeding Hollow");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("bleedinghollow", x);

        //=================POISONED FOODS ITEMS======================
        x = new ItemStack(Material.COOKED_BEEF);
        meta = x.getItemMeta();
        meta.setDisplayName("§ø" + ChatColor.WHITE + "Steak");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("poisonsteak", x);

        x = new ItemStack(Material.GRILLED_PORK);
        meta = x.getItemMeta();
        meta.setDisplayName("§ø" + ChatColor.WHITE + "Cooked Porkchop");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("poisoncookedporkchop", x);

        x = new ItemStack(Material.COOKED_CHICKEN);
        meta = x.getItemMeta();
        meta.setDisplayName("§ø" + ChatColor.WHITE + "Cooked Chicken");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("poisoncookedchicken", x);

        x = new ItemStack(Material.COOKED_FISH);
        meta = x.getItemMeta();
        meta.setDisplayName("§ø" + ChatColor.WHITE + "Cooked Fish");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("poisoncookedfish", x);

        x = new ItemStack(Material.COOKED_FISH, 1, (byte) 1);
        meta = x.getItemMeta();
        meta.setDisplayName("§ø" + ChatColor.WHITE + "Cooked Salmon");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("poisoncookedsalmon", x);

        x = new ItemStack(Material.COOKED_RABBIT);
        meta = x.getItemMeta();
        meta.setDisplayName("§ø" + ChatColor.WHITE + "Cooked Rabbit");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("poisoncookedrabbit", x);

        x = new ItemStack(Material.COOKED_MUTTON);
        meta = x.getItemMeta();
        meta.setDisplayName("§ø" + ChatColor.WHITE + "Cooked Mutton");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("poisoncookedmutton", x);

        x = new ItemStack(Material.BAKED_POTATO);
        meta = x.getItemMeta();
        meta.setDisplayName("§ø" + ChatColor.WHITE + "Baked Potato");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("poisonbakedpotato", x);
        //=================POISONED FOODS======================
        x = new ItemStack(Material.COOKED_BEEF);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Steak");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("steak", x);

        x = new ItemStack(Material.GRILLED_PORK);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Cooked Porkchop");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("cookedporkchop", x);

        x = new ItemStack(Material.COOKED_CHICKEN);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Cooked Chicken");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("cookedchicken", x);

        x = new ItemStack(Material.COOKED_FISH);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Cooked Fish");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("cookedfish", x);

        x = new ItemStack(Material.COOKED_FISH, 1, (byte) 1);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Cooked Salmon");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("cookedsalmon", x);

        x = new ItemStack(Material.COOKED_RABBIT);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Cooked Rabbit");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("cookedrabbit", x);

        x = new ItemStack(Material.COOKED_MUTTON);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Cooked Mutton");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("cookedmutton", x);

        x = new ItemStack(Material.BAKED_POTATO);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Baked Potato");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("bakedpotato", x);

        x = new ItemStack(Material.HUGE_MUSHROOM_2);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Red Blockshroom");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("redblockshroom", x);

        x = new ItemStack(Material.HUGE_MUSHROOM_1);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Brown Blockshroom");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("brownblockshroom", x);

        x = new ItemStack(Material.INK_SACK, 1, (byte) 7);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Rune Essence");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("runeessence", x);

        x = new ItemStack(Material.BLAZE_ROD);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Essence Binder");
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("essencebinder", x);

        x = new ItemStack(Material.EYE_OF_ENDER);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "Seeker of the Damned");;
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        x.setItemMeta(meta);
        cutomItems.put("seekerofthedamned", x);

        //==============RUNES====================
        x = new ItemStack(Material.MONSTER_EGG, 1, (byte) 50);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Blast Rune");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        lore = new ArrayList<String>();
        lore.add(ChatColor.BLUE + "Volatile");
        meta.setLore(lore);
        x.setItemMeta(meta);
        cutomItems.put("blastrune", x);

        x = new ItemStack(Material.MONSTER_EGG, 1, (byte) 58);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Ender Rune");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        lore = new ArrayList<String>();
        lore.add(ChatColor.BLUE + "Volatile");
        meta.setLore(lore);
        x.setItemMeta(meta);
        cutomItems.put("enderrune", x);

        x = new ItemStack(Material.MONSTER_EGG, 1, (byte) 54);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Undead Rune");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        lore = new ArrayList<String>();
        lore.add(ChatColor.BLUE + "Volatile");
        meta.setLore(lore);
        x.setItemMeta(meta);
        cutomItems.put("undeadrune", x);

        x = new ItemStack(Material.MONSTER_EGG, 1, (byte) 52);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Nerubian Rune");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        lore = new ArrayList<String>();
        lore.add(ChatColor.BLUE + "Volatile");
        meta.setLore(lore);
        x.setItemMeta(meta);
        cutomItems.put("nerubianrune", x);

        x = new ItemStack(Material.MONSTER_EGG, 1, (byte) 56);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Ghost Rune");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        lore = new ArrayList<String>();
        lore.add(ChatColor.BLUE + "Volatile");
        meta.setLore(lore);
        x.setItemMeta(meta);
        cutomItems.put("ghostrune", x);

        x = new ItemStack(Material.MONSTER_EGG, 1, (byte) 61);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Fire Rune");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        lore = new ArrayList<String>();
        lore.add(ChatColor.BLUE + "Volatile");
        meta.setLore(lore);
        x.setItemMeta(meta);
        cutomItems.put("firerune", x);

        x = new ItemStack(Material.MONSTER_EGG, 1, (byte) 55);
        meta = x.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Viscous Rune");
        //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        //meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        lore = new ArrayList<String>();
        lore.add(ChatColor.BLUE + "Volatile");
        meta.setLore(lore);
        x.setItemMeta(meta);
        cutomItems.put("viscousrune", x);

        //==============RUNES====================
    }

    public void iniRecipesCrafting() {
        ItemStack temp;
        int cTime;
        String[] skillReqs;
        int[][] levelReqs;
        int[] levels;
        int xp[];
        ArrayList<ItemStack> ings;
        Recipez x;
        int stack;

        //DEBUG
        levels = new int[]{1, 5};
        skillReqs = new String[]{"Crafting"};
        levelReqs = new int[][]{{20, 50}};
        xp = new int[]{5000};
        cTime = 300;
        ings = new ArrayList<ItemStack>();
        temp = ci("diffusedcoal").clone();
        temp.setAmount(3);
        ings.add(temp);
        temp = ci("truediamond").clone();
        temp.setAmount(1);
        ings.add(temp);
        ItemStack lolwut = ci("cooke").clone();
        ItemMeta xmeta = lolwut.getItemMeta();
        xmeta.setDisplayName(ChatColor.GOLD + "DebugCookie");
        lolwut.setItemMeta(xmeta);
        x = new Recipez(ings, lolwut, cTime, xp, levels, skillReqs, levelReqs, 1);
        recipeList.put(ChatColor.GOLD + "DebugCookie", x);
        //DEBUG

        levels = new int[]{1};
        skillReqs = new String[]{"Crafting"};
        levelReqs = new int[][]{{1}};
        xp = new int[]{5};
        cTime = 400;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.GOLD_INGOT, 6));
        ings.add(new ItemStack(Material.IRON_INGOT, 5));
        x = new Recipez(ings, ci("augmentedpickaxe").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.YELLOW + "Augmented Pickaxe", x);

        levels = new int[]{1};
        skillReqs = new String[]{"Crafting"};
        levelReqs = new int[][]{{1}};
        xp = new int[]{5};
        cTime = 400;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.GOLD_INGOT, 6));
        ings.add(new ItemStack(Material.IRON_INGOT, 5));
        x = new Recipez(ings, ci("augmentedhoe").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.YELLOW + "Augmented Hoe", x);

        levels = new int[]{1, 5, 10, 20, 30, 64};
        skillReqs = new String[]{"Crafting"};
        levelReqs = new int[][]{{1, 10, 20, 40, 60, 80}};
        xp = new int[]{5};
        cTime = 200;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.STICK, 1));
        ings.add(new ItemStack(Material.IRON_INGOT, 2));
        x = new Recipez(ings, ci("arrow").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.WHITE + "Arrow", x);

        levels = new int[]{1, 5, 10, 20, 30, 64};
        skillReqs = new String[]{"Crafting"};
        levelReqs = new int[][]{{1, 30, 40, 60, 80, 99}};
        xp = new int[]{70};
        cTime = 400;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.IRON_BLOCK, 4));
        ings.add(new ItemStack(Material.COAL_BLOCK, 2));
        x = new Recipez(ings, ci("trueiron").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.YELLOW + "True Iron", x);

        levels = new int[]{1, 5, 10, 20, 30, 64};
        skillReqs = new String[]{"Crafting"};
        levelReqs = new int[][]{{1, 30, 40, 60, 80, 99}};
        xp = new int[]{100};
        cTime = 400;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.GOLD_BLOCK, 4));
        ings.add(new ItemStack(Material.COAL_BLOCK, 4));
        x = new Recipez(ings, ci("truegold").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.YELLOW + "True Gold", x);

        levels = new int[]{1, 5, 10, 20, 30, 64};
        skillReqs = new String[]{"Crafting"};
        levelReqs = new int[][]{{1, 30, 40, 60, 80, 99}};
        xp = new int[]{150};
        cTime = 400;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.DIAMOND_BLOCK, 4));
        ings.add(new ItemStack(Material.COAL_BLOCK, 4));
        x = new Recipez(ings, ci("truediamond").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.YELLOW + "True Diamond", x);

        levels = new int[]{1, 5, 10, 20, 30, 64};
        skillReqs = new String[]{"Crafting"};
        levelReqs = new int[][]{{1, 10, 20, 40, 60, 80}};
        xp = new int[]{10};
        cTime = 200;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.COAL, 1));
        ings.add(new ItemStack(Material.STONE, 1));
        x = new Recipez(ings, ci("diffusedcoal").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.GRAY + "Diffused Coal", x);

        levels = new int[]{1, 5, 10, 20, 32};
        skillReqs = new String[]{"Cooking"};
        levelReqs = new int[][]{{30, 50, 70, 90, 99}};
        xp = new int[]{40};
        cTime = 2400;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.BREAD, 64));
        ings.add(new ItemStack(Material.COAL_BLOCK, 1));
        x = new Recipez(ings, ci("breadloaf").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.YELLOW + "Bread Loaf", x);

        levels = new int[]{1, 5};
        skillReqs = new String[]{"Cooking"};
        levelReqs = new int[][]{{60, 99}};
        xp = new int[]{6000};
        cTime = 6000;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.COOKIE, 1));
        temp = ci("breadloaf").clone();
        temp.setAmount(10);
        ings.add(temp);
        ings.add(new ItemStack(Material.INK_SACK, 64, (short) 3));
        temp = ci("truediamond").clone();
        temp.setAmount(9);
        ings.add(temp);
        ings.add(new ItemStack(Material.REDSTONE_BLOCK, 10));
        ings.add(new ItemStack(Material.PISTON_STICKY_BASE, 4));
        ings.add(new ItemStack(Material.REDSTONE_COMPARATOR, 4));
        x = new Recipez(ings, ci("cooke").clone(), cTime, xp, levels, skillReqs, levelReqs, 1);
        recipeList.put(ChatColor.GOLD + "Cook-E", x);

        levels = new int[]{1, 5, 8, 16};
        skillReqs = new String[]{"Runecrafting"};
        levelReqs = new int[][]{{10, 40, 60, 80}};
        xp = new int[]{1};
        cTime = 200;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.SNOW_BALL, 1));
        temp = ci("enderrune").clone();
        temp.setAmount(1);
        ings.add(temp);
        temp = ci("truediamond").clone();
        temp.setAmount(1);
        ings.add(temp);
        x = new Recipez(ings, ci("enderpearl").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.WHITE + "Ender Pearl", x);

        levels = new int[]{1, 5, 10, 30, 64};
        skillReqs = new String[]{"Runecrafting"};
        levelReqs = new int[][]{{10, 40, 50, 70, 90}};
        xp = new int[]{1};
        cTime = 100;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.COAL, 1, (byte) 1));
        temp = ci("blastrune").clone();
        temp.setAmount(3);
        ings.add(temp);
        x = new Recipez(ings, ci("gunpowder").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.WHITE + "Gunpowder", x);

        levels = new int[]{1, 5, 10, 30, 64};
        skillReqs = new String[]{"Runecrafting"};
        levelReqs = new int[][]{{30, 40, 50, 70, 99}};
        xp = new int[]{1};
        cTime = 100;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.STRING, 1));
        temp = ci("nerubianrune").clone();
        temp.setAmount(1);
        ings.add(temp);
        x = new Recipez(ings, ci("cobweb").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.WHITE + "Cobweb", x);

        levels = new int[]{1, 5, 10, 30, 64};
        skillReqs = new String[]{"Runecrafting"};
        levelReqs = new int[][]{{20, 40, 50, 70, 90}};
        xp = new int[]{1};
        cTime = 100;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.WATER_BUCKET, 1));
        ings.add(new ItemStack(Material.CACTUS));
        temp = ci("viscousrune").clone();
        temp.setAmount(10);
        ings.add(temp);
        x = new Recipez(ings, ci("slimeblock").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.WHITE + "Slime Block", x);

        levels = new int[]{1, 5, 10};
        skillReqs = new String[]{"Runecrafting"};
        levelReqs = new int[][]{{30, 60, 80}};
        xp = new int[]{1};
        cTime = 400;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.INK_SACK, 5, (byte) 1));
        temp = ci("nerubianrune").clone();
        temp.setAmount(1);
        ings.add(temp);
        x = new Recipez(ings, ci("bleedinghollow").clone(), cTime, xp, levels, skillReqs, levelReqs, 1);
        recipeList.put(ChatColor.DARK_RED + "Bleeding Hollow", x);

        levels = new int[]{1, 5, 10, 30, 64};
        skillReqs = new String[]{"Runecrafting"};
        levelReqs = new int[][]{{20, 40, 50, 70, 99}};
        xp = new int[]{100};
        cTime = 1;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.GOLD_INGOT, 1));
        ings.add(new ItemStack(Material.LAVA_BUCKET, 1));
        temp = ci("firerune").clone();
        temp.setAmount(3);
        ings.add(temp);
        x = new Recipez(ings, ci("blazerod").clone(), cTime, xp, levels, skillReqs, levelReqs, 1);
        recipeList.put(ChatColor.WHITE + "Blaze Rod", x);

        levels = new int[]{1, 5, 10, 20};
        skillReqs = new String[]{"Runecrafting"};
        levelReqs = new int[][]{{30, 60, 80, 99}};
        xp = new int[]{20};
        cTime = 2400;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.EYE_OF_ENDER, 1));
        temp = ci("bleedinghollow").clone();
        temp.setAmount(1);
        ings.add(temp);
        temp = ci("ghostrune").clone();
        temp.setAmount(6);
        ings.add(temp);
        x = new Recipez(ings, ci("seekerofthedamned").clone(), cTime, xp, levels, skillReqs, levelReqs, 1);
        recipeList.put(ChatColor.DARK_RED + "Seeker of the Damned", x);

        levels = new int[]{1, 5, 10, 32};
        skillReqs = new String[]{"Farming"};
        levelReqs = new int[][]{{30, 40, 60, 80}};
        xp = new int[]{5};
        cTime = 100;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.RED_MUSHROOM, 48));
        ings.add(new ItemStack(Material.BROWN_MUSHROOM, 16));
        ings.add(new ItemStack(Material.COAL, 16));
        x = new Recipez(ings, ci("redblockshroom").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.WHITE + "Red Blockshroom", x);

        levels = new int[]{1, 5, 10, 32};
        skillReqs = new String[]{"Farming"};
        levelReqs = new int[][]{{10, 20, 30, 40}};
        xp = new int[]{100};
        cTime = 600;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.BROWN_MUSHROOM, 48));
        ings.add(new ItemStack(Material.RED_MUSHROOM, 16));
        ings.add(new ItemStack(Material.COAL, 16));
        x = new Recipez(ings, ci("brownblockshroom").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.WHITE + "Brown Blockshroom", x);

        //=================POISONED FOODS RECIPES======================
        levels = new int[]{1, 5, 10, 20};
        skillReqs = new String[]{"Cooking"};
        levelReqs = new int[][]{{1, 80, 90, 99}};
        xp = new int[]{20};
        cTime = 3600;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.RAW_BEEF, 1));
        ings.add(new ItemStack(Material.COAL, 1));
        ings.add(new ItemStack(Material.RAW_FISH, 1, (byte) 3));
        x = new Recipez(ings, ci("poisonsteak").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put("§ø" + ChatColor.WHITE + "Steak", x);

        levels = new int[]{1, 5, 10, 20};
        skillReqs = new String[]{"Cooking"};
        levelReqs = new int[][]{{1, 80, 90, 99}};
        xp = new int[]{20};
        cTime = 3600;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.PORK, 1));
        ings.add(new ItemStack(Material.COAL, 1));
        ings.add(new ItemStack(Material.RAW_FISH, 1, (byte) 3));
        x = new Recipez(ings, ci("poisoncookedporkchop").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put("§ø" + ChatColor.WHITE + "Cooked Porkchop", x);

        levels = new int[]{1, 5, 10, 20};
        skillReqs = new String[]{"Cooking"};
        levelReqs = new int[][]{{1, 80, 90, 99}};
        xp = new int[]{20};
        cTime = 3600;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.RAW_CHICKEN, 1));
        ings.add(new ItemStack(Material.COAL, 1));
        ings.add(new ItemStack(Material.RAW_FISH, 1, (byte) 3));
        x = new Recipez(ings, ci("poisoncookedchicken").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put("§ø" + ChatColor.WHITE + "Cooked Chicken", x);

        levels = new int[]{1, 5, 10, 20};
        skillReqs = new String[]{"Cooking"};
        levelReqs = new int[][]{{1, 80, 90, 99}};
        xp = new int[]{20};
        cTime = 3600;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.RAW_FISH, 1));
        ings.add(new ItemStack(Material.COAL, 1));
        ings.add(new ItemStack(Material.RAW_FISH, 1, (byte) 3));
        x = new Recipez(ings, ci("poisoncookedfish").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put("§ø" + ChatColor.WHITE + "Cooked Fish", x);

        levels = new int[]{1, 5, 10, 20};
        skillReqs = new String[]{"Cooking"};
        levelReqs = new int[][]{{1, 80, 90, 99}};
        xp = new int[]{20};
        cTime = 3600;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.RAW_FISH, 1, (byte) 1));
        ings.add(new ItemStack(Material.COAL, 1));
        ings.add(new ItemStack(Material.RAW_FISH, 1, (byte) 3));
        x = new Recipez(ings, ci("poisoncookedsalmon").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put("§ø" + ChatColor.WHITE + "Cooked Salmon", x);

        levels = new int[]{1, 5, 10, 20};
        skillReqs = new String[]{"Cooking"};
        levelReqs = new int[][]{{1, 80, 90, 99}};
        xp = new int[]{20};
        cTime = 3600;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.RABBIT, 1));
        ings.add(new ItemStack(Material.COAL, 1));
        ings.add(new ItemStack(Material.RAW_FISH, 1, (byte) 3));
        x = new Recipez(ings, ci("poisoncookedrabbit").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put("§ø" + ChatColor.WHITE + "Cooked Rabbit", x);

        levels = new int[]{1, 5, 10, 20};
        skillReqs = new String[]{"Cooking"};
        levelReqs = new int[][]{{1, 80, 90, 99}};
        xp = new int[]{20};
        cTime = 3600;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.MUTTON, 1));
        ings.add(new ItemStack(Material.COAL, 1));
        ings.add(new ItemStack(Material.RAW_FISH, 1, (byte) 3));
        x = new Recipez(ings, ci("poisoncookedmutton").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put("§ø" + ChatColor.WHITE + "Cooked Mutton", x);

        levels = new int[]{1, 5, 10, 20};
        skillReqs = new String[]{"Cooking"};
        levelReqs = new int[][]{{1, 80, 90, 99}};
        xp = new int[]{20};
        cTime = 3600;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.POTATO_ITEM));
        ings.add(new ItemStack(Material.COAL, 1));
        ings.add(new ItemStack(Material.RAW_FISH, 1, (byte) 3));
        x = new Recipez(ings, ci("poisonbakedpotato").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put("§ø" + ChatColor.WHITE + "Baked Potato", x);
        //=================POISONED FOODS ENDS======================

        levels = new int[]{1, 5, 10, 20, 32, 64};
        skillReqs = new String[]{"Cooking"};
        levelReqs = new int[][]{{1, 30, 50, 70, 90, 99}};
        xp = new int[]{10};
        cTime = 400;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.RAW_BEEF, 1));
        temp = ci("diffusedcoal").clone();
        temp.setAmount(1);
        ings.add(temp);
        x = new Recipez(ings, ci("steak").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.WHITE + "Steak", x);

        levels = new int[]{1, 5, 10, 20, 32, 64};
        skillReqs = new String[]{"Cooking"};
        levelReqs = new int[][]{{1, 30, 50, 70, 90, 99}};
        xp = new int[]{10};
        cTime = 400;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.PORK, 1));
        temp = ci("diffusedcoal").clone();
        temp.setAmount(1);
        ings.add(temp);
        x = new Recipez(ings, ci("cookedporkchop").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.WHITE + "Cooked Porkchop", x);

        levels = new int[]{1, 5, 10, 20, 32, 64};
        skillReqs = new String[]{"Cooking"};
        levelReqs = new int[][]{{1, 20, 40, 60, 80, 99}};
        xp = new int[]{10};
        cTime = 300;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.RAW_CHICKEN, 1));
        temp = ci("diffusedcoal").clone();
        temp.setAmount(1);
        ings.add(temp);
        x = new Recipez(ings, ci("cookedchicken").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.WHITE + "Cooked Chicken", x);

        levels = new int[]{1, 5, 10, 20, 32, 64};
        skillReqs = new String[]{"Cooking"};
        levelReqs = new int[][]{{1, 10, 40, 60, 80, 99}};
        xp = new int[]{20};
        cTime = 300;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.RAW_FISH, 1));
        temp = ci("diffusedcoal").clone();
        temp.setAmount(1);
        ings.add(temp);
        x = new Recipez(ings, ci("cookedfish").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.WHITE + "Cooked Fish", x);

        levels = new int[]{1, 5, 10, 20, 32, 64};
        skillReqs = new String[]{"Cooking"};
        levelReqs = new int[][]{{1, 10, 40, 60, 80, 99}};
        xp = new int[]{25};
        cTime = 300;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.RAW_FISH, 1, (byte) 1));
        temp = ci("diffusedcoal").clone();
        temp.setAmount(1);
        ings.add(temp);
        x = new Recipez(ings, ci("cookedsalmon").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.WHITE + "Cooked Salmon", x);

        levels = new int[]{1, 5, 10, 20, 32, 64};
        skillReqs = new String[]{"Cooking"};
        levelReqs = new int[][]{{1, 20, 40, 60, 80, 99}};
        xp = new int[]{10};
        cTime = 300;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.RABBIT, 1));
       temp = ci("diffusedcoal").clone();
        temp.setAmount(1);
        ings.add(temp);
        x = new Recipez(ings, ci("cookedrabbit").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.WHITE + "Cooked Rabbit", x);

        levels = new int[]{1, 5, 10, 20, 32, 64};
        skillReqs = new String[]{"Cooking"};
        levelReqs = new int[][]{{1, 20, 40, 60, 80, 99}};
        xp = new int[]{10};
        cTime = 300;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.MUTTON, 1));
        temp = ci("diffusedcoal").clone();
        temp.setAmount(1);
        ings.add(temp);
        x = new Recipez(ings, ci("cookedmutton").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.WHITE + "Cooked Mutton", x);

        levels = new int[]{1, 5, 10, 20, 32, 64};
        skillReqs = new String[]{"Cooking"};
        levelReqs = new int[][]{{1, 20, 40, 60, 80, 99}};
        xp = new int[]{6};
        cTime = 300;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.POTATO_ITEM));
       temp = ci("diffusedcoal").clone();
        temp.setAmount(1);
        ings.add(temp);
        x = new Recipez(ings, ci("bakedpotato").clone(), cTime, xp, levels, skillReqs, levelReqs, -1);
        recipeList.put(ChatColor.WHITE + "Baked Potato", x);

        levels = new int[]{1, 5, 10, 20, 30};
        skillReqs = new String[]{"Runecrafting", "Crafting"};
        levelReqs = new int[][]{{1, 10, 20, 40, 60}, {1, 5, 10, 15, 20}};
        xp = new int[]{5, 5};
        cTime = 60;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.CLAY, 1));
        ings.add(new ItemStack(Material.INK_SACK, 1, (byte) 4));
        ings.add(new ItemStack(Material.COAL, 1));
        x = new Recipez(ings, ci("runeessence").clone(), cTime, xp, levels, skillReqs, levelReqs, 1);
        recipeList.put(ChatColor.WHITE + "Rune Essence", x);

        levels = new int[]{1};
        skillReqs = new String[]{"Runecrafting"};
        levelReqs = new int[][]{{1}};
        xp = new int[]{15};
        cTime = 1200;
        ings = new ArrayList<ItemStack>();
        ings.add(new ItemStack(Material.BLAZE_ROD, 3));
        ings.add(new ItemStack(Material.CLAY, 32));
        ings.add(new ItemStack(Material.REDSTONE_BLOCK, 1));
        temp = ci("trueiron").clone();
        temp.setAmount(4);
        ings.add(temp);
        temp = ci("truediamond").clone();
        temp.setAmount(1);
        ings.add(temp);
        x = new Recipez(ings, ci("essencebinder").clone(), cTime, xp, levels, skillReqs, levelReqs, 1);
        recipeList.put(ChatColor.BLUE + "Essence Binder", x);

        /*       //NOTICE ME DEMOUNDER HERE
         levels = new int[]{1, 5, 10, 16};
         skillReqs = new String[]{"Crafting", "Cooking"};
         levelReqs = new int[][]{{5, 20, 30, 40}, {5, 20, 30, 40}};
         xp =new int[]{10,20};
         cTime = 60;
         ings = new ArrayList<ItemStack>();
         ings.add(new ItemStack(Material.SLIME_BALL, 1));
         ings.add(new ItemStack(Material.SNOW_BALL, 1));
         temp = ci("truediamond");
         temp.setAmount(1);
         ings.add(temp);
         x = new Recipez(ings, ci("enderpearl"), cTime, 100, levels);
         i.put(ChatColor.WHITE + "Ender Pearl", x);*/
    }

    public Inventory iClone(String s) {
        Inventory x = menus.get(s);
        Inventory clone = Bukkit.createInventory(null, x.getSize(), x.getName());
        clone.setContents(x.getContents());
        return clone;
    }

    public String extract(String InventoryName) {
        char[] x = InventoryName.toCharArray();
        String skill = "";
        for (int i = 2; i < x.length; i++) {
            if (x[i] == ' ') {
                i = x.length;
            } else {
                skill += (x[i] + "");
            }
        }
        return skill;
    }

    /*    public void iniCraftXPTable() {
     try {
     plugin.cfg.load(plugin.cfgFile);
     } catch (Exception e) {
     }
    
     }*/
    public void removeFromInventory(ItemStack[] ings, Player p, int amount) {
        ItemStack[] c = p.getInventory().getContents();
        int count;
        for (ItemStack ing : ings) {
            count = ing.getAmount() * amount;
            ItemStack a = ing.clone();
            a.setAmount(1);
            int slotIndex = 0;
            for (ItemStack own : c) {
                if (own != null) {
                    ItemStack cCheck = own.clone();
                    cCheck.setAmount(1);
                    if (cCheck.getItemMeta().hasDisplayName()) {
                        ItemMeta cMeta = cCheck.getItemMeta();
                        cMeta.setDisplayName(plugin.hiddenExtractRaw(cMeta.getDisplayName()));
                        cCheck.setItemMeta(cMeta);
                    }
                    ItemStack b = own.clone();
                    b.setAmount(1);
                    if (a.equals(b) || a.equals(cCheck)) {
                        if (own.getAmount() <= count) {
                            count -= own.getAmount();
                            p.getInventory().setItem(slotIndex, new ItemStack(Material.AIR));
                        } else if (count != 0) {
                            own.setAmount(own.getAmount() - count);
                            count = 0;
                        }
                    } else if (own.getItemMeta().hasDisplayName() && matChes.containsKey(a)) {
                        for (ItemStack x : matChes.get(a)) {
                            if (x.getItemMeta().getDisplayName().equals(plugin.hiddenExtractRaw(own.getItemMeta().getDisplayName())) || x.getItemMeta().getDisplayName().equals(own.getItemMeta().getDisplayName())) {
                                if (own.getAmount() <= count) {
                                    count -= own.getAmount();
                                    p.getInventory().setItem(slotIndex, new ItemStack(Material.AIR));
                                } else if (count != 0) {
                                    own.setAmount(own.getAmount() - count);
                                    count = 0;
                                }
                            }
                        }
                    }
                    /*else if (own.getItemMeta().hasDisplayName() && matChes.containsKey(own)) {
                     for (ItemStack x : matChes.get(own)) {
                     Bukkit.getServer().getLogger().info("The Type is " + own.getType().name());
                     if (x.getItemMeta().getDisplayName().equals(plugin.hiddenExtractRaw(own.getItemMeta().getDisplayName())) || x.getItemMeta().getDisplayName().equals(own.getItemMeta().getDisplayName())) {
                     if (own.getAmount() <= count) {
                     count -= own.getAmount();
                     p.getInventory().setItem(slotIndex, new ItemStack(Material.AIR));
                     } else if (count != 0) {
                     own.setAmount(own.getAmount() - count);
                     count = 0;
                     }
                     }
                     }
                     }*/

                }
                slotIndex++;
            }
        }
    }

    public int freeSlots(Inventory i) {
        int counter = 0;
        for (ItemStack x : i) {
            if (x == null) {
                counter++;
            }
        }
        return counter;
    }

    public void addItem(Player p, Recipez toCraft, int amount) {//it has already been determined that they ahve space
        int tot = amount;
        ItemStack stack;
        int stackcount = amount / toCraft.stackSize;
        if (toCraft.stackSize != -1) {
            stack = toCraft.prod.clone();
            for (int i = 0; i < stackcount; i++) {
                stack.setAmount(toCraft.stackSize);
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(plugin.timeToHidden(System.currentTimeMillis() + i) + "§~" + toCraft.prod.getItemMeta().getDisplayName());
                stack.setItemMeta(meta);
                p.getInventory().addItem(stack);
                tot -= toCraft.stackSize;
            }
            if (tot != 0) {
                stack.setAmount(tot);
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(plugin.timeToHidden(System.currentTimeMillis()) + "§~" + toCraft.prod.getItemMeta().getDisplayName());
                stack.setItemMeta(meta);
            }
        } else {
            stack = toCraft.prod.clone();
            stack.setAmount(amount * toCraft.prod.getAmount());
            p.getInventory().addItem(stack);
        }
    }

    public boolean hasLvls(Recipez r, Player p, int index) {
        boolean has = true;
        for (int i = 0; i < r.skillReqs.length; i++) {
            if (plugin.pList.get(p.getUniqueId().toString()).skillStats[AugSkz.skillz.skillTable.get(r.skillReqs[i])][0] < r.levelReqs[i][index]) {
                has = false;
            }
        }
        return has;
    }

    public boolean containsAtLeastx(Player p, ItemStack stax, int amount) {
        boolean hasAMatch = false;
        int num = amount;
        Inventory pInv = p.getInventory();
        ItemStack[] conts = pInv.getContents();
        for (ItemStack i : conts) {
            if (i != null) {
                ItemStack cCheck = i.clone();
                cCheck.setAmount(1);
                if (cCheck.getItemMeta().hasDisplayName()) {
                    ItemMeta cMeta = cCheck.getItemMeta();
                    cMeta.setDisplayName(plugin.hiddenExtractRaw(cMeta.getDisplayName()));
                    cCheck.setItemMeta(cMeta);
                }
                ItemStack toCompare = stax.clone();
                toCompare.setAmount(1);
                if (i.isSimilar(stax) || cCheck.isSimilar(stax)) {
                    num -= i.getAmount();
                } else if (i.getItemMeta().hasDisplayName() && matChes.containsKey(toCompare)) {
                    for (ItemStack x : matChes.get(toCompare)) {
                        if (x.getItemMeta().getDisplayName().equals(plugin.hiddenExtractRaw(i.getItemMeta().getDisplayName())) || x.getItemMeta().getDisplayName().equals(i.getItemMeta().getDisplayName())) {
                            num -= i.getAmount();
                        }
                    }
                }
            }
        }
        if (num > 0) {
            return false;
        }
        return true;
    }

    //craftingTime is in ticks must be divisible by 5 , intiate time to 5
    public void keepCrafting(final Player p, final Inventory inv, final String name, final long time, final int craftTime, final int amount) {//hmmmm
        int timerID = plugin.getServer().getScheduler()
                .scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        if ((crafting.get(p.getName()) / (craftTime / 5.0)) > 1) {
                            Recipez toCraft = rec(name);
                            crafting.put(p.getName(), -1);
                            boolean hasMats = true;
                            for (ItemStack stax : toCraft.ings) {
                                if (!containsAtLeastx(p, stax, stax.getAmount() * amount)/*p.getInventory().containsAtLeast(stax, stax.getAmount() * amount)*/) {
                                    hasMats = false;
                                }
                            }
                            if (hasMats) {
                                /*ItemStack crafted = rec(name).prod;
                                 crafted.setAmount(crafted.getAmount() * amount);
                                 p.getInventory().addItem(crafted);*/
                                addItem(p, toCraft, amount);
                                removeFromInventory(toCraft.ings, p, amount);
                                for (int i = 0; i < toCraft.skillReqs.length; i++) {
                                    AugSkz.skillz.addXp(p, plugin.pList.get(p.getUniqueId().toString()), toCraft.skillReqs[i], toCraft.xp[i] * amount);
                                }
                                //AugSkz.skillz.addXp(p, plugin.pList.get(p.getUniqueId().toString()), "Crafting", rec(name).xp * amount);
                                p.closeInventory();
                                p.playSound(p.getLocation(), Sound.ANVIL_USE, (float) .3, 1);
                            } else {
                                p.closeInventory();
                                p.sendMessage(ChatColor.RED + "You do not have the right ingredients!");
                            }
                        }
                        if (crafting.containsKey(p.getName()) && (crafting.get(p.getName()) != -1)) {
                            int percentToNine = (int) Math.round(7.0 * (crafting.get(p.getName()) / (craftTime / 5.0)));
                            inv.setItem(percentToNine, new ItemStack(Material.WORKBENCH));
                            crafting.put(p.getName(), crafting.get(p.getName()) + 1);
                            keepCrafting(p, inv, name, time, craftTime, amount);
                        }
                    }
                }, time);
    }

    //seekTime is in seconds, intiate time to 5
    public void seeking(final String pName, final String preyName, final long time, final Location loc) {//hmmmm
        int timerID = plugin.getServer().getScheduler()
                .scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        Player p = Bukkit.getServer().getPlayer(pName);
                        Player prey = Bukkit.getServer().getPlayer(preyName);

                        if (seekTimer.get(pName) < 1) {
                            isSeeking.put(pName, 0);
                            seekWorlds.put(pName, 0);
                            seekLogout.put(pName, 0);
                            if (p != null) {
                                if (p != null) {
                                    p.sendMessage(plugin.tag + ChatColor.GREEN + " Your seeker has faded.");
                                }
                            }
                        } else if (seekTimer.containsKey(pName) && (seekTimer.get(pName) > 0)) {
                            Location preyAt;
                            if (prey != null) {
                                preyAt = prey.getLocation();
                                if (seekLogout.get(pName) == 1) {
                                    if (p != null) {
                                        p.sendMessage(plugin.tag + ChatColor.GREEN + " Your prey has reconnected.");
                                        seekLogout.put(pName, 0);
                                    }
                                }
                            } else {
                                preyAt = loc;
                                if (seekLogout.get(pName) == 0) {
                                    if (p != null) {
                                        p.sendMessage(plugin.tag + ChatColor.GREEN + " Your prey has disconnected.");
                                        seekLogout.put(pName, 1);
                                    }
                                }
                            }
                            if (p != null) {
                                if (!p.getLocation().getWorld().getName().equals(preyAt.getWorld().getName())) {
                                    if (seekWorlds.get(pName) == 0) {
                                        p.sendMessage(plugin.tag + ChatColor.GREEN + " Prey in a different world: " + preyAt.getWorld().getName());
                                        seekWorlds.put(pName, 1);
                                    }
                                } else if (seekWorlds.get(pName) == 1) {
                                    p.sendMessage(plugin.tag + ChatColor.GREEN + " Prey in your world: " + preyAt.getWorld().getName());
                                    seekWorlds.put(pName, 0);
                                }
                                p.setCompassTarget(preyAt);
                            }
                            seekTimer.put(pName, seekTimer.get(pName) - 1);
                            seeking(pName, preyName, time, preyAt);
                        }
                        //p.sendMessage(seekTimer.get(pName) + "");//NOTICEME
                    }
                }, time);

    }

}

class Recipez {

    ItemStack[] ings;
    ItemStack prod;
    int craftTime;
    String[] skillReqs;
    int[][] levelReqs;
    int xp[];
    int[] levels;
    int stackSize;

    Recipez(ArrayList<ItemStack> ings, ItemStack prod, int craftTime, int xp[], int[] levels, String[] skillReqs, int[][] levelReqs, int stackSize) {
        ItemStack[] transfer = new ItemStack[ings.size()];
        for (int i = 0; i < transfer.length; i++) {
            transfer[i] = ings.get(i);
        }
        this.levels = levels;
        this.xp = xp;
        this.craftTime = craftTime;
        this.ings = transfer;
        this.prod = prod;
        this.skillReqs = skillReqs;
        this.levelReqs = levelReqs;
        this.stackSize = stackSize;
    }

    public ItemStack menu() {
        ItemStack menu = prod.clone();
        ItemMeta meta = menu.getItemMeta();
        ArrayList<String> lore = new ArrayList<String>();
        for (int i = 0; i < skillReqs.length; i++) {
            lore.add(ChatColor.BLUE + "Lvl " + levelReqs[i][0] + " " + skillReqs[i]);
        }
        //lore.add(ChatColor.BLUE + "Lvl " + levels[1][0] + " " + "Crafting");
        meta.setLore(lore);
        menu.setItemMeta(meta);
        return menu;
    }

    public ItemStack cInv(int ind) {
        ItemStack menu = prod.clone();
        menu.setAmount(levels[ind]);
        ItemMeta meta = menu.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Confirm x" + levels[ind]);
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(prod.getItemMeta().getDisplayName());
        for (int i = 0; i < skillReqs.length; i++) {
            lore.add(ChatColor.BLUE + "Lvl " + levelReqs[i][ind] + " " + skillReqs[i]);//NOTICEME
        }
        //lore.add(ChatColor.BLUE + "Lvl " + levels[1][ind] + " " + "Crafting");
        meta.setLore(lore);
        menu.setItemMeta(meta);
        return menu;
    }
}
