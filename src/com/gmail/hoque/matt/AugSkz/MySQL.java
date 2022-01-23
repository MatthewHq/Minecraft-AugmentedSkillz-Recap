/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.hoque.matt.AugSkz;

import static com.gmail.hoque.matt.AugSkz.AugSkz.SQLstance;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 *
 * @author Matthew Hoque
 */
public class MySQL {

    AugSkz plugin;

    public ArrayList<String> skillsColumns = new ArrayList<String>();
    public String[] columns = {"Lvl", "Xp"};

    String host;
    String user;
    String pass;

    public MySQL(AugSkz instance) {
        this.plugin = instance;
        loadSQLData();
        tableExists();
        skillsColumns.add("UUID");
        updateCycleTimer();
        createSkillRows();
    }

    public void loadSQLData() {
        try {
            plugin.cfg.load(plugin.cfgFile);
        } catch (Exception e) {
        }
        host = plugin.cfg.getString("MySQLhost");
        user = plugin.cfg.getString("MySQLuser");
        pass = plugin.cfg.getString("MySQLpass");
    }

    public void updateCycleTimer() {
        Bukkit.getServer().getScheduler()
                .scheduleSyncRepeatingTask(plugin, new Runnable() {
                    public void run() {
                        saveAllPlayers();
                    }
                }, 20, 580);
    }

    public boolean dbExists() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(host, user, pass);
            //Attempt to open a connection to the database, if success return true
            return true;
        } catch (SQLException ex) {
            //connection to database failed, user/password/path is wrong, or Database does not exist.
            Bukkit.getServer().getLogger().info(ex.getMessage());
            return false;

        } finally {
            //we need to close the connection when we are done with it
            try {
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                //Could put something here like "Failed to close connection"
                Bukkit.getServer().getLogger().info(ex.getMessage());
            }
        }
    }

    public void tableExists() {
        Connection con = null;
        PreparedStatement pst = null;

        

        try { //try to get the table, catch failure and output to log
            con = DriverManager.getConnection(host, user, pass); //open connection to database
            //create a the table if it does not exist, table name here will be testtable table format will be
            //Auto-Increment ID, Player UUID (60 character, Player Name (20 Chatacter), Player IP (20 Character,
            // Time last on (in milliseconds) this is a "bigInt" and will size itself
            pst = con.prepareStatement(
                    //"CREATE TABLE IF NOT EXISTS testtable (id MEDIUMINT NOT NULL AUTO_INCREMENT,uuid VARCHAR(60),name VARCHAR(20), ip VARCHAR(20), laston BIGINT, PRIMARY KEY (id))");
                    "CREATE TABLE IF NOT EXISTS `augmentedskillz`.`skills` ( `UUID` VARCHAR(36) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL , `Name` VARCHAR(16) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL , `totalLvl` INT NOT NULL , PRIMARY KEY (`UUID`(36))) ENGINE = InnoDB;");

            pst.executeUpdate();
        } catch (SQLException ex) { //Different kinds of exceptions that can occur, just do the same thing every time
            Bukkit.getServer().getLogger().info(ex.getMessage());
//			log.severe(ex.getMessage());  //if something goes terrible log it
            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ey) { //Different kinds of exceptions that can occur, just do the same thing every time
                Bukkit.getServer().getLogger().info(ex.getMessage());
//				log.severe(ey.getMessage());  //if something goes terrible log it
            }
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {  //Different kinds of exceptions that can occur, just do the same thing every time
//				log.severe(ex.getMessage());  //if something goes terrible log it
                Bukkit.getServer().getLogger().info(ex.getMessage());
            }
        }
    }

    public void runStatement(String statement) {
        Connection con = null;
        PreparedStatement pst = null;

        try { //try to get the table, catch failure and output to log

            con = DriverManager.getConnection(host, user, pass); //open connection to database
            //create a the table if it does not exist, table name here will be testtable table format will be
            //Auto-Increment ID, Player UUID (60 character, Player Name (20 Chatacter), Player IP (20 Character,
            // Time last on (in milliseconds) this is a "bigInt" and will size itself
            pst = con.prepareStatement(
                    //"CREATE TABLE IF NOT EXISTS testtable (id MEDIUMINT NOT NULL AUTO_INCREMENT,uuid VARCHAR(60),name VARCHAR(20), ip VARCHAR(20), laston BIGINT, PRIMARY KEY (id))");
                    statement);

            pst.executeUpdate();
        } catch (SQLException ex) { //Different kinds of exceptions that can occur, just do the same thing every time
            Bukkit.getServer().getLogger().info(ex.getMessage());
//			log.severe(ex.getMessage());  //if something goes terrible log it
//            System.out.println("Issue /w statement on " + statement + " : " + ex.getMessagze());
            Bukkit.getServer().getLogger().info(ex.getMessage());
            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ey) { //Different kinds of exceptions that can occur, just do the same thing every time
                Bukkit.getServer().getLogger().info(ex.getMessage());
//				log.severe(ey.getMessage());  //if something goes terrible log it
            }
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {  //Different kinds of exceptions that can occur, just do the same thing every time
//				log.severe(ex.getMessage());  //if something goes terrible log it
                Bukkit.getServer().getLogger().info(ex.getMessage());
            }
        }
    }

    public void createSkillRows() {
        String before = "ALTER TABLE `skills` ADD `";
        String after = "` INT NOT NULL AFTER `totalLvl`;";
        for (int i = 0; i < AugSkz.skillz.skillNames.length; i++) {
            String skill = AugSkz.skillz.skillNames[i];
            for (int j = 0; j < columns.length; j++) {
                String toRun = before + skill + columns[j] + after;
                skillsColumns.add(skill + columns[j]);
                runStatement(toRun);
            }
        }

    }

    public int playerInSQL(String uuid) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        int rowCount = -1;
        try {
            con = DriverManager.getConnection(host, user, pass);
            pst = con.prepareStatement("SELECT COUNT(*) FROM skills WHERE uuid=?");
            pst.setString(1, uuid);
            rs = pst.executeQuery();
            rs.next();
            rowCount = rs.getInt(1);
        } catch (SQLException ex) {
//			log.severe(ex.getMessage());
//            System.out.println(ex.getMessage());
            Bukkit.getServer().getLogger().info(ex.getMessage());
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ey) {
                Bukkit.getServer().getLogger().info(ex.getMessage());
//                System.out.println(ex.getMessage());
//				log.severe(ey.getMessage());
            }
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Bukkit.getServer().getLogger().info(ex.getMessage());
//                System.out.println(ex.getMessage());
//				log.severe(ex.getMessage());
            }
        }
        return rowCount;
    }

    public ASPlayer getPlayer(String uuid) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = DriverManager.getConnection(host, user, pass);
            pst = con.prepareStatement("SELECT * FROM skills Where UUID = ?");
            pst.setString(1, uuid);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int[][] skillStats = new int[AugSkz.skillz.skillNames.length][columns.length];
                int totalLvl;
                for (int i = 0; i < AugSkz.skillz.skillNames.length; i++) {
                    String skill = AugSkz.skillz.skillNames[i];
                    for (int j = 0; j < columns.length; j++) {
                        skillStats[i][j] = rs.getInt(AugSkz.skillz.skillNames[i] + columns[j]);
                    }
                }
                totalLvl = rs.getInt("totalLvl");
//                Bukkit.getServer().getLogger().info(totalLvl+"");
                ASPlayer loadedP = new ASPlayer(uuid, totalLvl, skillStats);
//                 Bukkit.getServer().getLogger().info(loadedP.totalLvl+"");
                return loadedP;

            }
        } catch (SQLException ex) {
            Bukkit.getServer().getLogger().info(ex.getMessage());
//			log.severe(ex.getMessage());
            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ey) {
                Bukkit.getServer().getLogger().info(ex.getMessage());
//				log.severe(ey.getMessage());
            }
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Bukkit.getServer().getLogger().info(ex.getMessage());
//				log.severe(ex.getMessage());
            }
        }
        return null; //if it all went to crap return a zero to indicate that the time was not located
    }

    public void runThreadQuery(final String toRun) {
        new Thread() {
            public void run() {

                Connection con = null;
                PreparedStatement pst = null;

                try { //try to get the table, catch failure and output to log

                    con = DriverManager.getConnection(host, user, pass); //open connection to database

//                    Bukkit.getServer().getLogger().info(toRun);//NOTICEME
                    pst = con.prepareStatement(toRun);

                    pst.executeUpdate();
                } catch (SQLException ex) {
                    Bukkit.getServer().getLogger().info(ex.getMessage() + " runThreadQuery");
                    try {
                        if (pst != null) {
                            pst.close();
                        }
                        if (con != null) {
                            con.close();
                        }
                    } catch (SQLException ey) {
                        Bukkit.getServer().getLogger().info(ex.getMessage() + " runThreadQuery");
                    }
                } finally {
                    try {
                        if (pst != null) {
                            pst.close();
                        }
                        if (con != null) {
                            con.close();
                        }
                    } catch (SQLException ex) {
                        Bukkit.getServer().getLogger().info(ex.getMessage() + " runThreadQuery");
                    }
                }
            }
        }.start();
    }

    public void saveAllPlayers() {

        ArrayList<ASPlayer> toUpdate = new ArrayList<ASPlayer>();
        for (String s : AugSkz.skillz.needsUpdate.keySet()) {
            if (AugSkz.skillz.needsUpdate.get(s) == 1) {
                toUpdate.add(plugin.pList.get(s));
                AugSkz.skillz.needsUpdate.put(s, 0);
            }
        }

        if (toUpdate.size() != 0) {

            String partOne = "INSERT INTO skills(UUID,Name,totalLvl";
            String cLabels = "";
            String partTwo = ") VALUES";
            String cVals = "";
            String partThree = "ON DUPLICATE KEY UPDATE UUID = VALUES(UUID),Name=Name,totalLvl=VALUES(totalLvl)";
            String updates = "";
            String partFour = ";";

            for (int x = 0; x < toUpdate.size(); x++) {
                if (x != 0) {
                    cVals += ",";
                }
                cVals += "(\"" + toUpdate.get(x).UUID + "\"," + "\"NameHere\"," + toUpdate.get(x).totalLvl;
                for (int i = 0; i < AugSkz.skillz.skillNames.length; i++) {
                    cVals += ", " + toUpdate.get(x).getSkill(i)[0] + ", " + toUpdate.get(x).getSkill(i)[1];
                }
                cVals += ")";
                if (x == toUpdate.size() - 1) {
                    cVals += " ";
                }
            }

            for (int i = 0; i < AugSkz.skillz.skillNames.length; i++) {
                String skill = AugSkz.skillz.skillNames[i];
                for (int j = 0; j < AugSkz.SQLstance.columns.length; j++) {
                    cLabels = cLabels + ", " + skill + AugSkz.SQLstance.columns[j];
                    updates = updates + "," + skill + AugSkz.SQLstance.columns[j] + "= VALUES(" + skill + AugSkz.SQLstance.columns[j] + ")";
                }
            }
//            Bukkit.getServer().getLogger().info(partOne + cLabels + partTwo + cVals + partThree + updates + partFour + " IS FOR SAVEALL");//NOTICEME
            plugin.SQLstance.runThreadQuery(partOne + cLabels + partTwo + cVals + partThree + updates + partFour); //NOTICEME maybe just runstatemenet
        }
    }

    public void updateName(String UUID, String Name) {
        ASPlayer p = plugin.pList.get(UUID);
        String partOne = "INSERT INTO skills(UUID,Name,totalLvl";
        String cLabels = "";
        String partTwo = ") VALUES(" + "\"" + UUID + "\",\"" + Name + "\"," + p.totalLvl;
        String cVals = "";
        String partThree = ") ON DUPLICATE KEY UPDATE UUID = UUID,Name=VALUES(Name),totalLvl = totalLvl";
        String updates = "";
        String partFour = ";";

        for (int i = 0; i < AugSkz.skillz.skillNames.length; i++) {
            String skill = AugSkz.skillz.skillNames[i];
            for (int j = 0; j < AugSkz.SQLstance.columns.length; j++) {
                cLabels = cLabels + ", " + skill + AugSkz.SQLstance.columns[j];
                updates = updates + "," + skill + AugSkz.SQLstance.columns[j] + "= " + skill + AugSkz.SQLstance.columns[j] + "";
            }
        }

        for (int i = 0; i < AugSkz.skillz.skillNames.length; i++) {
            cVals = cVals + ", " + p.getSkill(i)[0] + ", " + p.getSkill(i)[1];
        }
//            Bukkit.getServer().getLogger().info(partOne + cLabels + partTwo + cVals + partThree + updates + partFour +"PSAVE");//NOTICEME
        SQLstance.runThreadQuery(partOne + cLabels + partTwo + cVals + partThree + updates + partFour); //NOTICEME maybe just runstatemenet
    }

    public ArrayList<String> getTop(String skill, int page, int pageSize) {
        ArrayList<String> ret = new ArrayList<String>();
//        String skillName = AugSkz.skillz.skillNames[skill];
        String skillName=skill;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = DriverManager.getConnection(host, user, pass);

            pst = con.prepareStatement("SELECT UUID, name," + skillName + "Lvl" + " FROM skills ORDER BY " + skillName + "Lvl DESC");
            ResultSet rs = pst.executeQuery();

            int index = 0;
            int atPage = 1;
            boolean flag = true;
            while (rs.next() && flag) {
//                Bukkit.getServer().getLogger().info(rs.getString("Name") + "  " + rs.getInt(skillName + "Lvl"));
                index++;
                    if (index <= pageSize) {
                        ret.add(ChatColor.GREEN+""+((atPage-1)*pageSize+index)+". "+rs.getString("Name") + " - "+ChatColor.BLUE + rs.getInt(skillName + "Lvl"));
                    } else if (!(atPage + 1 > page)) {
                        index = 1;
                        atPage++;
                        ret.clear();
                        ret.add(ChatColor.GREEN+""+((atPage-1)*pageSize+index)+". "+rs.getString("Name") + " - "+ChatColor.BLUE + rs.getInt(skillName + "Lvl"));
                    } else {
                        flag = false;
                    }
//                for (int i = 0; i < AugSkz.skillz.skillNames.length; i++) {
//                    String skill = AugSkz.skillz.skillNames[i];
//                    for (int j = 0; j < columns.length; j++) {
//                        skillStats[i][j] = rs.getInt(AugSkz.skillz.skillNames[i] + columns[j]);
//                    }
//                }
//                ret.add(rs.getString("Name") + "  " + rs.getInt(skillName + "Lvl"));
            }
            ret.add(ChatColor.GREEN+"=======   Page "+ChatColor.RED+atPage+ChatColor.GREEN+"  =======");
            return ret;
        } catch (SQLException ex) {
            Bukkit.getServer().getLogger().info(ex.getMessage());
//			log.severe(ex.getMessage());
            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ey) {
                Bukkit.getServer().getLogger().info(ex.getMessage());
//				log.severe(ey.getMessage());
            }
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Bukkit.getServer().getLogger().info(ex.getMessage());
//				log.severe(ex.getMessage());
            }
        }
        return null; //if it all went to crap return a zero to indicate that the time was not located
    }

}
