/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.hoque.matt.AugSkz;

/**
 *
 * @author Matthew Hoque
 */
public class ASPlayer {
    AugSkz plugin;
    public int totalLvl;
    public String UUID;
    public int[][]skillStats;

    public ASPlayer(String UUID, int totalLvl,int[][] skillStats) {
        this.UUID = UUID;
        this.skillStats=skillStats;
        this.totalLvl=totalLvl;
    }

    public ASPlayer(String UUID) {
        this.UUID = UUID;
        iniSkillStats();
    }
    
    public void iniSkillStats(){
        totalLvl=AugSkz.skillz.skillNames.length;
        skillStats=new int[AugSkz.skillz.skillNames.length][2];
        for(int i=0;i<AugSkz.skillz.skillNames.length;i++){
            skillStats[i]=new int[]{1,0};
        }
    }
    
    public int[] getSkill(int skill){
        return skillStats[skill];
    }


}
