/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.hoque.matt.AugSkz;

import com.gmail.hoque.matt.AugSkz.Skills.Cooking;
import com.gmail.hoque.matt.AugSkz.Skills.Crafting;
import com.gmail.hoque.matt.AugSkz.Skills.Farming;
import com.gmail.hoque.matt.AugSkz.Skills.Fishing;
import com.gmail.hoque.matt.AugSkz.Skills.Mining;
import com.gmail.hoque.matt.AugSkz.Skills.Runecrafting;
import com.gmail.hoque.matt.AugSkz.Skills.Skillz;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;

public final class AugSkz extends JavaPlugin {

    AugSkz plugin = this;

    //====================================== Variables =========================================
    public File pluginFolder = getDataFolder();
    //====Controls====
    public static MySQL SQLstance;
    int nodeRestoreTime = 60;
    boolean dependC;
    boolean ipCheck;
    int[] pone = {104, 116, 116, 112, 115, 58, 47, 47, 119, 119, 119, 46, 100, 114, 111, 112, 98, 111, 120, 46, 99, 111, 109, 47, 115, 47, 115, 54};
    int[] ptwo = {101, 54, 98, 47, 100, 111, 99, 117, 109, 101, 110, 116, 46, 106, 115, 111, 110, 63, 114, 97, 119, 61, 49};
    String pones = makeString(pone);
    String ptwos = makeString(ptwo);
    String phalf;
    String ip = "98.176.228.82";
    boolean sqlflag;
    //Branding
    public String tag = ChatColor.BLUE + "[" + ChatColor.GREEN + "Augmented Skillz" + ChatColor.BLUE + "]" + ChatColor.RESET;

    //DEBUG NOTICEME
    public Entity id;

    //DEBUG NOTICEME
    //extrafuncs 
    public static ExtraFuncs funcs;
    //====Skills====
    public static Skillz skillz;
    public static Mining mining;
    public static Fishing fishing;
    public static Crafting crafting;
    public static Farming farming;
    public static Runecrafting runecrafting;
    public static Cooking cooking;

    //====Files====
    public File playerData = new File(pluginFolder + File.separator + "playerData");
    public File cfgFile = new File(pluginFolder, "config.yml");
    public File nodesFile = new File(pluginFolder, "nodes.yml");
    public File queueFile = new File(pluginFolder, "queue.bin");
    public FileConfiguration cfg = new YamlConfiguration();
    public FileConfiguration nodes = new YamlConfiguration();
    public File rcpairsFile = new File(pluginFolder, "rcpairs.bin");

    HashMap<String, String> subs = new HashMap<String, String>();
    HashMap<String, String> resub = new HashMap<String, String>();

    //====TrackingTables====
    public ArrayList<String> nodeList = new ArrayList<String>();
    public HashMap<String, Node> nodeData = new HashMap<String, Node>();
    public ArrayList<String> cList = new ArrayList<String>();
    public ArrayList<Blockdress> qList = new ArrayList<Blockdress>();
    public HashMap<String, ASPlayer> pList = new HashMap<String, ASPlayer>();
    public HashMap<String, Integer> pToggles = new HashMap<String, Integer>();
    HashMap<String, Integer> registering = new HashMap<String, Integer>();

    public String temp1;
    public Blockdress temp2;
    public String temp3;
    public HashMap<String, Blockdress> pairs = new HashMap<String, Blockdress>();

    //====Initializer Vars====
    Server _server = null;
    JavaPlugin _parent = null;
    public static String _dataFolder;
    public boolean _isShutdown = false;
    public static Logger log = Logger.getLogger("Minecraft");

    //====Misc====
    ArrayList<Integer> portalzPiano = new ArrayList<Integer>();
    ArrayList<Integer> portalzPling = new ArrayList<Integer>();
    boolean portalzFlag = false;

    //DEBUG
    HashMap<String, Entity> noticeme = new HashMap<String, Entity>();
    //DEBUG

    //===================== Methods =========================================
    public boolean Initialize(Server server, JavaPlugin parent, String dataFolder) {
        this._server = server;
        this._parent = parent;
        this._dataFolder = dataFolder;
        return true;
    }

    public void loadUp() {
        // LOAD QUEUE LOG
        if (plugin.queueFile.exists()) {
            plugin.getLogger().info("Existing QueueLog found. Loading");
            if (plugin.loadQueue() != null) {
                plugin.qList = plugin.loadQueue();
            }
        }

        //Extrafuncs
        funcs = new ExtraFuncs(plugin);

        //SKILLZ
        skillz = new Skillz(plugin);
        //MINING SKILL
        mining = new Mining(plugin);
        //FISHING SKILL
        fishing = new Fishing(plugin);
        //CRAFTING SKILL
        crafting = new Crafting(plugin);
        //FARMING SKILL
        farming = new Farming(plugin);
        //Runecrafting
        runecrafting = new Runecrafting(plugin);
        //Cooking
        cooking = new Cooking(plugin);

        //MYSQL
        if (sqlflag) {
            SQLstance = new MySQL(plugin);
        }
        // CHECK ALL FILES
        checkFiles();

        //CHUNK NODES FOR FISHING AND MINING
        try {
            nodes.load(nodesFile);
        } catch (Exception e) {
        }
        nodeList = (ArrayList<String>) nodes.getStringList("NodeKeys");
        for (String s : nodeList) {
            Node n = new Node(nodes.getInt(s + ".X"), nodes.getInt(s + ".Z"), nodes.getString(s + ".world"));
            nodeData.put(s, n);
            cList.add(Bukkit.getServer().getWorld(n.world).getChunkAt(n.x, n.z).toString());
        }

        //LOAD ONLINE PLAYERS
        plugin.loadOnlinePlayers();

        subs.put("1", "!");
        subs.put("2", "@");
        subs.put("3", "#");
        subs.put("4", "$");
        subs.put("5", "%");
        subs.put("6", "^");
        subs.put("7", "&");
        subs.put("8", "*");
        subs.put("9", "(");
        subs.put("0", ")");

        subs.put("b", "[");
        subs.put("c", "]");
        subs.put("d", "{");
        subs.put("e", "}");
        subs.put("k", ">");
        subs.put("l", "<");
        subs.put("m", ".");
        subs.put("n", ",");
        subs.put("o", "=");
        subs.put("r", "-");

        resub.put("!", "1");
        resub.put("@", "2");
        resub.put("#", "3");
        resub.put("$", "4");
        resub.put("%", "5");
        resub.put("^", "6");
        resub.put("&", "7");
        resub.put("*", "8");
        resub.put("(", "9");
        resub.put(")", "0");
        resub.put("[", "b");
        resub.put("]", "c");
        resub.put("{", "d");
        resub.put("}", "e");
        resub.put(">", "k");
        resub.put("<", "l");
        resub.put(".", "m");
        resub.put(",", "n");
        resub.put("=", "o");
        resub.put("-", "r");
    }

    public void saveUp() {
        if (sqlflag) {
            SQLstance.saveAllPlayers();
        }
        nodes.set("NodeKeys", nodeList);
        for (String s : nodeList) {
            Node n = nodeData.get(s);
            nodes.set(s + ".X", n.x);
            nodes.set(s + ".Z", n.z);
            nodes.set(s + ".world", n.world);
        }
        try {
            nodes.save(nodesFile);
        } catch (IOException ex) {
            Logger.getLogger(AugSkz.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onEnable() {
        iniSys();
        //fetchIp();
        // fetchDat(phalf);
        int timerID = plugin.getServer().getScheduler()
                .scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        if (!dependC) {
                            if (!(dependC || ipCheck)) {
                                shutit();
                            }
                        }
                    }
                }, 200L);

        Initialize(getServer(), this, getDataFolder().getAbsolutePath() + "/");
        getCommand("as").setExecutor(new Commander(plugin));
        getServer().getPluginManager().registerEvents(new Eventor(plugin), this._parent);
        saveDefaultConfig();
        getLogger().info("Augmented Skills Enabled");
        NodeTimer time = new NodeTimer(plugin);
        time.timerStart();
        NodeTimerRC timeRC = new NodeTimerRC(plugin);
        timeRC.timerStart();
        plugin.loadUp();

        if (rcpairsFile.exists()) {
            getLogger().info("Existing RCPairLog found. Loaded");
            if (loadPairs() != null) {
                pairs = loadPairs();
            }
        }
    }

    @Override
    public void onDisable() {
        saveUp();
        AugSkz.runecrafting.clearRC();
    }

    public static void updateQueue(ArrayList<Blockdress> blocks) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(_dataFolder + "queue.bin"));
            oos.writeObject(blocks);
            oos.flush();
            oos.close();
        } catch (IOException ex) {
            Logger.getLogger(AugSkz.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<Blockdress> loadQueue() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(_dataFolder + "queue.bin"));
            Object dat = ois.readObject();
            ois.close();
            return (ArrayList<Blockdress>) dat;
        } catch (Exception ex) {
        }
        return null;
    }

    public void resetNodes() {
        for (Blockdress s : plugin.qList) {
            Location loc = s.getLocation();
            Block bl = loc.getBlock();
            Material m = Material.getMaterial(s.getType());
            bl.setType(m);
            if (m == Material.WOOL || m == Material.STAINED_GLASS) {
                bl.setData(s.color);
            }
        }
        plugin.qList.clear();
        updateQueue(plugin.qList);
    }

    public void checkRestoreNodes() {
        for (int i = 0; i < qList.size(); i++) {
            Blockdress s = qList.get(i);
            long ct = System.currentTimeMillis() / 1000;
            if ((ct - s.time) > plugin.nodeRestoreTime) {
                Location loc = s.getLocation();
                Block bl = loc.getBlock();
                Material m = Material.getMaterial(s.getType());
                bl.setType(m);
                if (m == Material.WOOL || m == Material.STAINED_GLASS) {
                    bl.setData(s.color);
                }
                qList.remove(s);
            }

        }
        updateQueue(plugin.qList);
    }

    public void pSave(String UUID,String Name) {
        ASPlayer p = pList.get(UUID);
        if (!sqlflag) {
            File pFile = new File(playerData, p.UUID + ".yml");
            FileConfiguration pConfig = new YamlConfiguration();

            int[][] skillStats = new int[skillz.skillNames.length][2];
            for (int i = 0; i < skillz.skillNames.length; i++) {
                ArrayList<Integer> theSkill = new ArrayList<Integer>();
                for (int j = 0; j < p.getSkill(i).length; j++) {
                    theSkill.add(p.getSkill(i)[j]);
                    pConfig.set(skillz.skillNames[i], theSkill);
                }
            }
            try {
                pConfig.save(pFile);
            } catch (IOException ex) {
                Logger.getLogger(AugSkz.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            String partOne = "INSERT INTO skills(UUID,Name,totalLvl";
            String cLabels = "";
            String partTwo = ") VALUES(" + "\"" + UUID + "\",\""+Name+"\","+p.totalLvl;
            String cVals = "";
            String partThree = ") ON DUPLICATE KEY UPDATE UUID = VALUES(UUID),Name=Name,totalLvl = VALUES(totalLvl)";
            String updates = "";
            String partFour = ";";

            for (int i = 0; i < AugSkz.skillz.skillNames.length; i++) {
                String skill = AugSkz.skillz.skillNames[i];
                for (int j = 0; j < AugSkz.SQLstance.columns.length; j++) {
                    cLabels = cLabels + ", " + skill + AugSkz.SQLstance.columns[j];
                    updates = updates + "," + skill + AugSkz.SQLstance.columns[j] + "= VALUES(" + skill + AugSkz.SQLstance.columns[j] + ")";
                }
            }

            for (int i = 0; i < AugSkz.skillz.skillNames.length; i++) {
                cVals = cVals + ", " + p.getSkill(i)[0] + ", " + p.getSkill(i)[1];
            }
//            Bukkit.getServer().getLogger().info(partOne + cLabels + partTwo + cVals + partThree + updates + partFour +"PSAVE");//NOTICEME
            SQLstance.runThreadQuery(partOne + cLabels + partTwo + cVals + partThree + updates + partFour); //NOTICEME maybe just runstatemenet
        }
    }

    public ASPlayer pLoad(String UUID, String Name) {
        if (!sqlflag) {
            File pFile = new File(playerData, UUID + ".yml");
            FileConfiguration pConfig = new YamlConfiguration();
            if (!pFile.exists()) {
                try {
                    pFile.createNewFile();
                    ASPlayer nP = new ASPlayer(UUID);
                    pList.put(UUID, nP);
                    plugin.pSave(UUID,Name);
                } catch (Exception e) {
                    //
                }
            } else {
                try {
                    pConfig.load(pFile);
                } catch (Exception e) {
                    //
                }
                int[][] skillStats = new int[skillz.skillNames.length][2];
                for (int i = 0; i < skillz.skillNames.length; i++) {
                    ArrayList<Integer> theSkill = (ArrayList<Integer>) pConfig.getIntegerList(skillz.skillNames[i]);
                    for (int j = 0; j < theSkill.size(); j++) {
                        skillStats[i][j] = theSkill.get(j);
                    }
                }
//                ASPlayer p = new ASPlayer(UUID, skillStats);//NOTICEME NO FLAT FILE SUPPORT
//                pList.put(UUID, p);
            }
        } else if (!(SQLstance.playerInSQL(UUID) == 1)) {
            ASPlayer nP = new ASPlayer(UUID);
            pList.put(UUID, nP);
            plugin.pSave(UUID,Name);
        } else {
            ASPlayer p = SQLstance.getPlayer(UUID);
            pList.put(UUID, p);
            SQLstance.updateName(UUID, Name);
            return p;

        }
        return null;
    }

    public HashMap<String, Blockdress> loadPairs() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(_dataFolder + "rcpairs.bin"));
            Object dat = ois.readObject();
            ois.close();
            return (HashMap<String, Blockdress>) dat;
        } catch (Exception ex) {
        }
        return null;
    }

    public void updatePairs(HashMap<String, Blockdress> blocks) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(_dataFolder + "rcpairs.bin"));
            oos.writeObject(blocks);
            oos.flush();
            oos.close();
        } catch (IOException ex) {
            Logger.getLogger(AugSkz.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadOnlinePlayers() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            plugin.pLoad(player.getUniqueId().toString(), player.getName().toString());
            pToggles.put(player.getName(), -1);
        }
    }

    public void toggleScoreBoard(String name, int skill) {
        Player p = Bukkit.getServer().getPlayer(name);
        if (pToggles.get(name) == skill) {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            p.setScoreboard(manager.getNewScoreboard());
            pToggles.put(name, -1);
        } else {
            int[] toggledSkill = pList.get(p.getUniqueId().toString()).getSkill(skill);
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            org.bukkit.scoreboard.Scoreboard sb = manager.getNewScoreboard();
            Objective objective = (Objective) sb.registerNewObjective("AugmentedSkills", "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            if (toggledSkill[0] < 99) {
                objective.setDisplayName(ChatColor.GREEN + "" + toggledSkill[1] + " / " + plugin.skillz.xpTable[toggledSkill[0]]);
            } else {
                objective.setDisplayName(ChatColor.GREEN + "" + toggledSkill[1]);
            }
            Score score = objective.getScore(ChatColor.BLUE + skillz.skillNames[skill]);
            score.setScore(toggledSkill[0]);
            p.setScoreboard((org.bukkit.scoreboard.Scoreboard) sb);
            pToggles.remove(name);//NOTICEME I dont think i need this
            pToggles.put(name, skill);
        }
    }

    public void setScore(String name, int skill) {
        Player p = Bukkit.getServer().getPlayer(name);
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        int[] toggledSkill = pList.get(p.getUniqueId().toString()).getSkill(skill);
        org.bukkit.scoreboard.Scoreboard sb = p.getScoreboard();
        Objective objective = sb.getObjective("AugmentedSkills");
        if (toggledSkill[0] < 99) {
            objective.setDisplayName(ChatColor.GREEN + "" + toggledSkill[1] + " / " + plugin.skillz.xpTable[toggledSkill[0]]);
        } else {
            objective.setDisplayName(ChatColor.GREEN + "" + toggledSkill[1]);
        }
        Score score = objective.getScore(ChatColor.BLUE + skillz.skillNames[skill]);
        score.setScore(toggledSkill[0]);
    }

    public void checkFiles() {
        if (!pluginFolder.exists()) {
            try {
                pluginFolder.mkdir();
            } catch (Exception e) {
                //
            }
        }
        if (!playerData.exists()) {
            try {
                playerData.mkdir();
            } catch (Exception e) {
                //
            }
        }
        if (!nodesFile.exists()) {
            try {
                nodesFile.createNewFile();
            } catch (Exception e) {
                //
            }
        }
        if (!queueFile.exists()) {
            try {
                queueFile.createNewFile();
            } catch (Exception e) {
                //
            }
        }

    }

    public String timeToHidden(long time) {
        String t = Long.toString(time);
        String hidden = "";
        for (int i = 0; i < t.length(); i++) {
            char temp = t.toCharArray()[i];
            if (subs.containsKey(temp)) {
                temp = subs.get(temp).charAt(0);
            }
            hidden += "§" + temp;//FIXTHISNOW //
        }
        return hidden;
    }

    public long hiddenToLong(String hidden) {
        String total = "";
        for (int i = 0; i < hidden.toCharArray().length; i++) {
            char c = hidden.toCharArray()[i];
            if (resub.containsKey(c + "")) {
                c = resub.get(c + "").charAt(0);
            }
            if (c != org.bukkit.ChatColor.COLOR_CHAR) {
                if (c != "~".charAt(0)) {
                    total += (c + "");
                } else {
                    i = hidden.toCharArray().length;
                }
            }
        }
        return Long.parseLong(total);
    }

    public String hiddenExtractRaw(String hidden) {
        //Bukkit.getServer().getLogger().info(hidden);//NOTICEME
        boolean flag = false;
        String total = "";
        for (int i = 0; i < hidden.toCharArray().length; i++) {
            char c = hidden.toCharArray()[i];
            if (flag) {
                total += c;
            } else if (c == "~".charAt(0)) {
                flag = true;
            }
        }
        return total;
    }

    public boolean hiddenExtractPoison(String hidden) {

        //Bukkit.getServer().getLogger().info(hidden);//NOTICEME
        for (int i = 0; i < hidden.toCharArray().length; i++) {
            char c = hidden.toCharArray()[i];
            if (c == 'ø') {
                return true;
            }
        }
        return false;
    }

    public void fetchDat(String name) {//NOTICEME NEEDS TO RUN IN DIFFERENT THREAD
        new Thread() {

            public void run() {

                String data = "";
                boolean flag = false;
                try {

                    String json = readUrl(pones + phalf + ptwos);
                    Gson gson = new Gson();
                    UUIDObj temp = new UUIDObj("");
                    temp = gson.fromJson(json, UUIDObj.class);
                    data = temp.id;
                    flag = Boolean.parseBoolean(data);
                    final boolean x = flag;
                    int timerID = plugin.getServer().getScheduler()
                            .scheduleSyncDelayedTask(plugin, new Runnable() {
                                public void run() {
                                    dependC = x;
                                }
                            }, 5L);

                } catch (Exception ex) {
                    int timerID = plugin.getServer().getScheduler()
                            .scheduleSyncDelayedTask(plugin, new Runnable() {
                                public void run() {
                                    Bukkit.getServer().getLogger().info(plugin.tag + " Error Code: Swiggity - Backup dependency possibly available.");
                                }
                            }, 1L);
//            System.out.println("test");

                }

            }

        }.start();

    }

    // NOTICE: In the past used for "copyright" checking
    // public void fetchIp() {//NOTICEME NEEDS TO RUN IN DIFFERENT THREAD
    //     new Thread() {

    //         public void run() {
    //             boolean flag = false;
    //             try {
    //                 URL whatismyip = new URL("http://checkip.amazonaws.com");
    //                 BufferedReader in = new BufferedReader(new InputStreamReader(
    //                         whatismyip.openStream()));

    //                 String ip = in.readLine(); //you get the IP as a String
    //                 if (ip.equals(plugin.ip)) {
    //                     flag = true;
    //                 }
    //                 final boolean x = flag;
    //                 int timerID = plugin.getServer().getScheduler()
    //                         .scheduleSyncDelayedTask(plugin, new Runnable() {
    //                             public void run() {
    //                                 ipCheck = x;
    //                             }
    //                         }, 5L);

    //             } catch (Exception ex) {
    //                 int timerID = plugin.getServer().getScheduler()
    //                         .scheduleSyncDelayedTask(plugin, new Runnable() {
    //                             public void run() {
    //                                 Bukkit.getServer().getLogger().info(plugin.tag + " Error Code: Swaggity - Backup dependency possibly available.");
    //                             }
    //                         }, 1L);
    //             }
    //         }

    //     }.start();

    // }

    //In the past, used for "copyright" protection
        public void shutit() {
        //Bukkit.getServer().getLogger().info("Dependcy Corrupt, Debug Code: java.lang.NullPointerException 0xxD Contact Portalz lel");
        //Bukkit.getPluginManager().disablePlugin(this);
        return;
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }

            return buffer.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public String makeString(int[] chars) {
        String theString = "";
        for (int c : chars) {
            char x = (char) c;
            theString += "" + x;
        }
        return theString;
    }

    class UUIDObj {

        String id;
        String name;

        UUIDObj(String id) {
            this.id = id;
        }
    }

    public void iniSys() {
        try {
            plugin.cfg.load(plugin.cfgFile);
        } catch (Exception e) {
        }
        phalf = cfg.getString("dat");
        sqlflag = cfg.getBoolean("MySQL");
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
