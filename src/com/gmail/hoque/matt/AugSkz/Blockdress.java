/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.hoque.matt.AugSkz;

import java.io.Serializable;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.DyeColor;
import org.bukkit.Location;

/**
 *
 * @author Matthew Hoque
 */
public class Blockdress implements Serializable {

    public String world;
    public String type;
    public double x;
    public double y;
    public double z;
    public long time;
    public byte color;
    public String rc;

    AugSkz plugin;

    public Blockdress(AugSkz instance) {
        plugin = instance;
    }

    public Blockdress(String type, String world, double x, double y, double z,long time) {
        this.world = world;
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.time=time;

    }
    
    public Blockdress(String type, String world, double x, double y, double z,long time, byte color) {
        this.world = world;
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.time=time;
        this.color=color;

    }
    
    public Blockdress(Location l) {
        this.world = l.getWorld().getName();
        this.type = type;
        this.x = l.getX();
        this.y = l.getY();
        this.z = l.getZ();
        this.time=time;
        this.color=color;

    }
    
    public Blockdress(Location l,String rc) {
        this.world = l.getWorld().getName();
        this.type = type;
        this.x = l.getX();
        this.y = l.getY();
        this.z = l.getZ();
        this.time=time;
        this.color=color;
        this.rc=rc;

    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public String getType() {
        return type;
    }

    public Location getLocation() {
        Location l = new Location(Bukkit.getServer().getWorld(world), x, y, z);
        return l;
    }
    
    public String locS(){
        return world+x+y+z;
    }
}
