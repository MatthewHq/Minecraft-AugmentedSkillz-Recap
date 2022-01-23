/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.hoque.matt.AugSkz;

import org.bukkit.Bukkit;

/**
 *
 * @author Matthew Hoque
 */
public class NodeTimerRC {

    static AugSkz plugin;

    public NodeTimerRC(AugSkz instance) {
        this.plugin = instance;
    }

    public void timerStart() {
        Bukkit.getServer().getScheduler()
                .scheduleSyncRepeatingTask(plugin, new Runnable() {

                    public void run() {

                        AugSkz.runecrafting.checkRC();
                    }
                }, 20 , 1180);
    }

}
