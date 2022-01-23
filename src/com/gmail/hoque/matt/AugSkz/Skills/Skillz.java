/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.hoque.matt.AugSkz.Skills;

import com.gmail.hoque.matt.AugSkz.ASPlayer;
import com.gmail.hoque.matt.AugSkz.AugSkz;
import static java.lang.Math.pow;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 *
 * @author Matthew Hoque
 */
public class Skillz {

    AugSkz plugin;

    public String[] skillNames = new String[]{
        "Mining",
        "Crafting",
        "Farming",
        "Cooking",
        "Fishing",
        "Runecrafting"
    };

    public HashMap<String, Integer> skillTable = new HashMap<String, Integer>();
    public HashMap<String, Integer> needsUpdate = new HashMap<String, Integer>();
    public int[] xpTable;

    public Skillz(AugSkz plugin) {
        this.plugin = plugin;
        // HARDCODED XP TABLE
        iniXPTable();
        iniSkillTable();

    }

    public void iniXPTable() {
        xpTable = new int[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 120, 140, 160, 180, 200, 220, 240, 260, 280, 300, 340, 380, 420,
            460, 500, 540, 580, 620, 660, 700, 780, 860, 940, 1020, 1100, 1180, 1260, 1340, 1420, 1500, 1600, 1700, 1800, 1900,
            2000, 2100, 2200, 2300, 2400, 2500, 2620, 2740, 2860, 2980, 3100, 3220, 3340, 3460, 3580, 3680, 3780, 3880, 3980,
            4080, 4180, 4280, 4380, 4480, 4580, 4680, 4730, 4780, 4830, 4880, 4930, 4980, 5030, 5080, 5130, 5180, 5230, 5280,
            5330, 5380, 5430, 5480, 5530, 5580, 5630, 5680, 5730, 5780, 5830, 5880, 5930, 5980, 6030, 6080, 6130};
    }

    public void iniSkillTable() {
        for (int i = 0; i < skillNames.length; i++) {
            skillTable.put(skillNames[i], i);
        }
    }

    public void levelUpSound(final Player p, final Sound sound, final float pitch, final float some, final long time) {
        int timerID = plugin.getServer().getScheduler()
                .scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        p.playSound(p.getLocation(), sound, pitch, some);
                    }
                }, time);
    }

    public void addXp(Player pl, ASPlayer p, String skill, int amount) {
        p.skillStats[AugSkz.skillz.skillTable.get(skill)][1] += amount;
        int currentLvl = p.skillStats[AugSkz.skillz.skillTable.get(skill)][0];
        while (p.skillStats[AugSkz.skillz.skillTable.get(skill)][0] < 99
                && p.skillStats[AugSkz.skillz.skillTable.get(skill)][1] >= plugin.skillz.xpTable[p.skillStats[AugSkz.skillz.skillTable.get(skill)][0]]) {
            p.skillStats[AugSkz.skillz.skillTable.get(skill)][1] -= plugin.skillz.xpTable[p.skillStats[AugSkz.skillz.skillTable.get(skill)][0]];
            p.skillStats[AugSkz.skillz.skillTable.get(skill)][0]++;
            p.totalLvl++;
            if (!(p.skillStats[AugSkz.skillz.skillTable.get(skill)][0] < 99
                    && p.skillStats[AugSkz.skillz.skillTable.get(skill)][1] >= plugin.skillz.xpTable[p.skillStats[AugSkz.skillz.skillTable.get(skill)][0]])) {
                pl.sendMessage(plugin.tag + ChatColor.GREEN + " " + skill + " level increased from " + currentLvl + " to " + p.skillStats[AugSkz.skillz.skillTable.get(skill)][0] + "!");
                pl.playSound(pl.getLocation(), Sound.LEVEL_UP, 1, 1);
                //float[] pitchs=new float[]{8,5,3,-1,-1,-1,8,5,3,-1,-1,-1,8,5,3,-1,5,-1,3};
                float[] pitchs = new float[]{11, -1, 18};
                for (int r = 0; r < pitchs.length; r++) {
                    if (pitchs[r] != -1) {
                        float truePitch = (float) pow(2.0, ((double) pitchs[r] - 12.0) / 12.0);
                        levelUpSound(pl, Sound.NOTE_PIANO, 1, truePitch, r * 3);
                    }
                }

            }
        }
        if (amount > 2000) {
            plugin.pSave(p.UUID, pl.getName().toString());//NOTICEME
        }
        needsUpdate.put(pl.getUniqueId().toString(), 1);
        if (plugin.pToggles.get(pl.getName()) == AugSkz.skillz.skillTable.get(skill)) {
            plugin.setScore(pl.getName(), AugSkz.skillz.skillTable.get(skill));
        }
    }
}
