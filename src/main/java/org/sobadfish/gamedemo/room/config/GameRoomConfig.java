package org.sobadfish.gamedemo.room.config;

import cn.nukkit.utils.Config;
import org.sobadfish.gamedemo.manager.FunctionManager;
import org.sobadfish.gamedemo.manager.ShopManager;
import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.player.team.config.TeamConfig;
import org.sobadfish.gamedemo.player.team.config.TeamInfoConfig;
import org.sobadfish.gamedemo.room.floattext.FloatTextInfoConfig;
import org.sobadfish.gamedemo.tools.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 房间的基本配置，可以根据自身的需求修改
 * @author Sobadfish
 * 2022/9/9
 */
public class GameRoomConfig {

    /**
     * 房间名
     * */
    public String name;

    /**
     * 地图配置
     * */
    public WorldInfoConfig worldInfo;

    /**
     * 游戏时长
     * */
    public int time;

    /**
     * 无限时间
     * */
    public boolean infiniteTime = false;

    /**
     * 是否允许队伍内PVP
     * */
    public boolean pvp;

    /**
     * 等待时长
     * */
    public int waitTime;
    /**
     * 游戏内等待时长
     * */
    public int gameInWait;

    /**
     * 复活时长
     * */
    public int reSpawnTime = 0;
    /**
     * 玩家复活次数
     * */
    public int reSpawnCount = 0;

    /**
     * 满人等待时长
     * */
    private int maxWaitTime;

    /**
     * 是否禁止合成
     * */
    public boolean banCraft;
    /**
     * 是否掉落物品
     * */
    public boolean deathDrop;

    /**
     * 最低人数
     * */
    public int minPlayerSize;

    /**
     * 最大人数
     * */
    private int maxPlayerSize;


    //自动进入下一局
    public boolean isAutomaticNextRound;

    /**
     * 队伍数据信息
     * */
    public LinkedHashMap<String, TeamConfig> teamCfg = new LinkedHashMap<>();

    /**
     * 队伍
     * */
    public ArrayList<TeamInfoConfig> teamConfigs;


    /**
     * 是否允许旁观
     * */
    public boolean hasWatch = true;

    /**
     * 是否减少饥饿值
     * */
    public boolean enableFood = false;

    /**
     * TNT伤害
     * */
    public int tntDamage = 8;

    /**
     * TNT爆炸时间
     * */
    public int tntExplodeTime = 5;

    /**
     * 是否允许玩家中途加入房间
     * */
    public boolean playerCutIn = false;



    /**
     * 等待大厅拉回坐标
     * */
    public int callbackY = 17;

    /**
     * 击退设置
     * */
    public KnockConfig knockConfig = new KnockConfig();

    /**
     * 游戏浮空字
     * */
    public List<FloatTextInfoConfig> floatTextInfoConfigs = new CopyOnWriteArrayList<>();
    /**
     * 禁用指令
     * */
    public ArrayList<String> banCommand = new ArrayList<>();

    /**
     * 退出房间执行指令
     * */
    public ArrayList<String> quitRoomCommand = new ArrayList<>();


    /**
     * 玩家胜利执行命令
     * */
    public ArrayList<String> victoryCommand = new ArrayList<>();

    /**
     * 玩家失败执行命令
     * */
    public ArrayList<String> defeatCommand = new ArrayList<>();

    /**
     * 游戏开始的一些介绍
     * */
    public ArrayList<String> gameStartMessage = new ArrayList<>();
    /**
     * 可以破坏的一些方块
     * 在阻止破坏的时候允许破坏的方块
     * */
    public ArrayList<String> canBreak = new ArrayList<>();

    /**
     * 不能破坏的一些方块
     * 如果这个数量大于0 则全地图可破坏
     * 除了列表内的方块
     * */
    public ArrayList<String> banBreak = new ArrayList<>();

    /**
     * 游戏的事件
     * */
    public GameRoomEventConfig eventConfig;

    /**
     * 游戏备选事件列表
     * */
    public GameRoomEventConfig eventListConfig;


    /**
     * 游戏房间拓展Dlc
     * */
    public List<String> roomDlc = new ArrayList<String>();


    /**
     * 箱子物品
     * */
    public Map<String,ItemConfig> items = new LinkedHashMap<>();
    /**
     * 刷新箱子物品概率
     * */
    private int round = 15;
    /**
     * 打开箱子是否刷新物品
     * */
    public boolean roundChest = false;
    /**
     * 箱子刷新时间
     * */
    public int chestResetTime = 120;

    /**
     * 箱子是否允许重置
     * */
    public boolean chestCanReset = true;

    /**
     * 游戏结束是否重置地图
     * */
    public boolean resetWorld = true;

    /**
     * 房间内置商店
     * */
    public ShopManager shopManager;
    /**
     * 是否开启商店功能
     * */
    public boolean enableShop;

    /**
     * 是否启用经济
     * */
    public boolean enableMoney;
    /**
     * 经济配置
     * */
    public MoneyConfig moneyConfig = new MoneyConfig();




    private GameRoomConfig(String name,
                           WorldInfoConfig worldInfo,
                           int time,
                           int waitTime,
                           int maxWaitTime,
                           int minPlayerSize,
                           int maxPlayerSize,
                           ArrayList<TeamInfoConfig> teamConfigs){
        this.name = name;
        this.worldInfo = worldInfo;
        this.time = time;
        this.waitTime = waitTime;
        this.maxWaitTime = maxWaitTime;
        this.minPlayerSize = minPlayerSize;
        this.maxPlayerSize = maxPlayerSize;
        this.teamConfigs = teamConfigs;

    }


    public LinkedHashMap<String, TeamConfig> getTeamCfg() {
        return teamCfg;
    }

    public ArrayList<TeamInfoConfig> getTeamConfigs() {
        return teamConfigs;
    }

    public int getMaxWaitTime() {
        return maxWaitTime;
    }

    public boolean isDeathDrop() {
        return deathDrop;
    }

    public String getName() {
        return name;
    }

    public int getMaxPlayerSize() {
        return maxPlayerSize;
    }

    public int getRound() {
        return round;
    }

    public boolean isPvp() {
        return pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public boolean isBanCraft() {
        return banCraft;
    }

    public void setWorldInfo(WorldInfoConfig worldInfo) {
        this.worldInfo = worldInfo;
    }

    public void setTeamConfigs(ArrayList<TeamInfoConfig> teamConfigs) {
        this.teamConfigs = teamConfigs;
    }

    public WorldInfoConfig getWorldInfo() {
        return worldInfo;
    }

    public void setTeamCfg(LinkedHashMap<String, TeamConfig> teamCfg) {
        this.teamCfg = teamCfg;
    }

    public static GameRoomConfig getGameRoomConfigByFile(String name, File file) {
        //TODO 构建房间配置逻辑
        if(file.isDirectory()) {
            try {
                Config team = new Config(file + "/team.yml", Config.YAML);
                LinkedHashMap<String, TeamConfig> teamConfigs = new LinkedHashMap<>();
                for (Map<?, ?> map : team.getMapList("team")) {
                    TeamConfig teamConfig = TeamConfig.getInstance(map);
                    teamConfigs.put(teamConfig.getName(), teamConfig);
                }
                if(!new File(file+"/room.yml").exists()){
                    TotalManager.sendMessageToConsole(TotalManager.getLanguage().getLanguage("chunk-room-template-unfinished","&e检测到未完成房间模板"));
                    Utils.toDelete(file);
                    TotalManager.sendMessageToConsole(TotalManager.getLanguage().getLanguage("chunk-room-template-clean-success","&a成功清除未完成的房间模板"));
                    return null;
                }


                Config room = new Config(file+"/room.yml",Config.YAML);
                WorldInfoConfig worldInfoConfig = WorldInfoConfig.getInstance(name,room);
                if(worldInfoConfig == null){
                    TotalManager.sendMessageToConsole(TotalManager.getLanguage().getLanguage("load-game-room-world-error","&c未成功加载 &a"+name+"&c 的游戏地图"));
                    return null;
                }

                int time = room.getInt("gameTime");
                int waitTime = room.getInt("waitTime");

                int maxWaitTime = room.getInt("max-player-waitTime");
                int minPlayerSize = room.getInt("minPlayerSize");
                int maxPlayerSize =  room.getInt("maxPlayerSize");
                ArrayList<TeamInfoConfig> teamInfoConfigs = new ArrayList<>();
                for(Map<?,?> map: room.getMapList("teamSpawn")){
                    teamInfoConfigs.add(TeamInfoConfig.getInfoByMap(
                            teamConfigs.get(map.get("name").toString()),map));
                }
                GameRoomConfig roomConfig = new GameRoomConfig(name,worldInfoConfig,time,waitTime,maxWaitTime,minPlayerSize,maxPlayerSize,teamInfoConfigs);
                roomConfig.hasWatch = room.getBoolean("hasWatch",true);
                roomConfig.gameInWait = room.getInt("gameInWait",10);
                roomConfig.reSpawnTime = room.getInt("reSpawnTime",0);
                roomConfig.reSpawnCount = room.getInt("reSpawnCount",0);
                roomConfig.pvp = room.getBoolean("pvp",true);
                roomConfig.banCommand = new ArrayList<>(room.getStringList("ban-command"));
                roomConfig.isAutomaticNextRound = room.getBoolean("AutomaticNextRound",true);
                roomConfig.quitRoomCommand = new ArrayList<>(room.getStringList("QuitRoom"));
                roomConfig.victoryCommand = new ArrayList<>(room.getStringList("victoryCmd"));
                roomConfig.defeatCommand = new ArrayList<>(room.getStringList("defeatCmd"));
                roomConfig.deathDrop = room.getBoolean("deathDrop",false);
                roomConfig.canBreak = new ArrayList<>(room.getStringList("can-break"));
                roomConfig.banBreak = new ArrayList<>(room.getStringList("ban-break"));
                roomConfig.roundChest = room.getBoolean("roundChest",false);
                roomConfig.resetWorld = room.getBoolean("resetWorld",true);
                roomConfig.enableFood = room.getBoolean("enable-food",false);
                roomConfig.chestResetTime = room.getInt("chest-reset-time",120);
                roomConfig.chestCanReset = room.getBoolean("chest-can-reset",true);
                roomConfig.enableMoney = room.getBoolean("money.enable",false);
                roomConfig.enableShop = room.getBoolean("enable-shop",false);
                roomConfig.banCraft = room.getBoolean("ban-craft",false);
                roomConfig.tntDamage = room.getInt("tntDamage",8);
                roomConfig.tntExplodeTime = room.getInt("tntExplodeTime",5);
                roomConfig.knockConfig.enable = room.getBoolean("kb-setting.enable",true);
                roomConfig.knockConfig.force = (float) room.getDouble("kb-setting.force",0.4f);
                roomConfig.knockConfig.speed = (float) room.getDouble("kb-setting.speed",0.5f);
                roomConfig.knockConfig.motionY = (float) room.getDouble("kb-setting.motionY",0.1f);
                roomConfig.roomDlc = room.getStringList("enableDlc");
                roomConfig.infiniteTime = room.getBoolean("infinite-time",false);
                roomConfig.playerCutIn = room.getBoolean("player-cut-in",false);
                if(roomConfig.roundChest) {
                    //TODO 如果小游戏需要使用箱子内随机刷新物品 就解开这个配置
                    //////////////////////////////////////////////////////////
                    if (!new File(file + "/items.yml").exists()) {
                        TotalManager.saveResource("items.yml", "/rooms/" + name + "/items.yml", false);
                    }

                    Config item = new Config(file + "/items.yml", Config.YAML);
                    List<Map> strings = item.getMapList("chests");
                    roomConfig.items = FunctionManager.buildItem(strings);
                    roomConfig.round = room.getInt("round", 15);
                }
                if(roomConfig.enableMoney){
                    MoneyConfig moneyConfig = new MoneyConfig();
                    moneyConfig.moneyUnit = room.getString("money.unit","$");
                    moneyConfig.defaultValue = room.getDouble("money.default-money",1000d);
                    roomConfig.moneyConfig = moneyConfig;
                }
                if(roomConfig.enableShop){
                    if (!new File(file + "/shop.yml").exists()) {
                        TotalManager.saveResource("shop.yml", "/rooms/" + name + "/shop.yml", false);
                    }
                    Config shop = new Config(file + "/shop.yml", Config.YAML);
                    roomConfig.shopManager = ShopManager.init(shop);
                }

                ////////////////////////////////////////////////////////////////


                List<FloatTextInfoConfig> configs = new ArrayList<>();
                if(room.exists("floatSpawnPos")){
                    for(Map<?,?> map: room.getMapList("floatSpawnPos")){
                        FloatTextInfoConfig config = FloatTextInfoConfig.build(map);
                        if(config != null){
                            configs.add(config);
                        }
                    }
                    roomConfig.floatTextInfoConfigs = configs;
                }
                if(room.exists("roomStartMessage")){
                    roomConfig.gameStartMessage = new ArrayList<>(room.getStringList("roomStartMessage"));
                }else{
                    roomConfig.gameStartMessage = defaultGameStartMessage();
                }
                if (!new File(file + "/event.yml").exists()) {
                    TotalManager.saveResource("event.yml", "/rooms/" + name + "/event.yml", false);
                }
                if (!new File(file + "/roomEventList.yml").exists()) {
                    TotalManager.saveResource("roomEventList.yml", "/rooms/" + name + "/roomEventList.yml", false);
                }
                TotalManager.sendMessageToConsole(TotalManager.getLanguage().getLanguage("load-game-room-master-event","&e开始加载 房间主事件"));
                roomConfig.eventConfig = GameRoomEventConfig.getGameRoomEventConfigByFile(new File(file+"/event.yml"));
                TotalManager.sendMessageToConsole(TotalManager.getLanguage().getLanguage("load-game-room-reserve-event","&e开始加载 房间备选事件"));
                roomConfig.eventListConfig = GameRoomEventConfig.getGameRoomEventConfigByFile(new File(file+"/roomEventList.yml"));

                return roomConfig;

            }catch (Exception e){
                TotalManager.sendMessageToConsole(TotalManager.getLanguage().getLanguage("load-room-error","加载房间出错: ")+e.getMessage());
                e.printStackTrace();

                return null;

            }
        }

       return null;

    }



    public boolean notHasFloatText(String name){
        for(FloatTextInfoConfig config: floatTextInfoConfigs){
            if(config.name.equalsIgnoreCase(name)){
                return false;
            }
        }
        return true;
    }

    public void saveFloatText(){
        List<Map<String,Object>> pos = new ArrayList<>();
        for(FloatTextInfoConfig floatTextInfoConfig: floatTextInfoConfigs){
            pos.add(floatTextInfoConfig.toConfig());
        }
        Config config = new Config(TotalManager.getDataFolder()+"/rooms/"+getName()+"/room.yml",Config.YAML);
        config.set("floatSpawnPos",pos);
        config.save();
    }


    public void removeFloatText(String name){
        floatTextInfoConfigs.removeIf(config -> config.name.equalsIgnoreCase(name));
        saveFloatText();
    }

    public static ArrayList<String> defaultGameStartMessage(){
        ArrayList<String> strings = new ArrayList<>();

        strings.add("&a■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
        strings.add("&f小游戏");
        strings.add("&e");
        strings.add("&e小游戏介绍");
        strings.add("&e");
        strings.add("&a■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
        return strings;
    }

    public void save(){
        if(!new File(TotalManager.getDataFolder()+"/rooms/"+getName()+"/room.yml").exists()){
            TotalManager.saveResource("room.yml","/rooms/"+name+"/room.yml",false);
        }
        //TODO 保存配置逻辑
        Config config = new Config(TotalManager.getDataFolder()+"/rooms/"+getName()+"/room.yml",Config.YAML);
        config.set("world",worldInfo.getLevel());
        config.set("gameTime",time);
        config.set("infinite-time",infiniteTime);
        config.set("callbackY",callbackY);
        config.set("waitTime",waitTime);
        config.set("gameInWait",gameInWait);
        config.set("pvp",pvp);
        config.set("reSpawnTime",reSpawnTime);
        LinkedHashMap<String,Object> moneyConfig = new LinkedHashMap<>();
        moneyConfig.put("enable",enableMoney);
        moneyConfig.put("default-money",this.moneyConfig.defaultValue);
        moneyConfig.put("unit",this.moneyConfig.moneyUnit);
        config.set("money",moneyConfig);
        config.set("reSpawnCount",reSpawnCount);
        config.set("deathDrop",deathDrop);
        config.set("hasWatch",hasWatch);
        config.set("ban-craft",banCraft);
        config.set("player-cut-in",playerCutIn);
        config.set("enable-food",enableFood);
        config.set("enable-shop",enableShop);
        config.set("tntDamage",tntDamage);

        config.set("max-player-waitTime",maxWaitTime);
        config.set("minPlayerSize",minPlayerSize);
        config.set("maxPlayerSize",maxPlayerSize);
        ArrayList<LinkedHashMap<String, Object>> teamSpawn = new ArrayList<>();
        for(TeamInfoConfig infoConfig: teamConfigs) {
            teamSpawn.add(infoConfig.save());
        }
        config.set("teamSpawn",teamSpawn);


        config.set("waitPosition",WorldInfoConfig.positionToString(worldInfo.getWaitPosition()));
        config.set("ban-command",banCommand);
        config.set("QuitRoom",quitRoomCommand);
        config.set("hasWatch", hasWatch);
        config.set("AutomaticNextRound",isAutomaticNextRound);
        config.set("defeatCmd",defeatCommand);
        config.set("deathDrop",deathDrop);
        config.set("victoryCmd",victoryCommand);
        config.set("roundChest",roundChest);
        config.set("resetWorld",resetWorld);
        config.set("chest-reset-time",chestResetTime);
        config.set("chest-can-reset",chestCanReset);
        config.set("kb-setting",knockConfig.saveConfig());
        config.set("enableDlc",roomDlc);
        config.set("roomStartMessage",gameStartMessage);
        List<Map<String,Object>> pos = new ArrayList<>();
        for(FloatTextInfoConfig floatTextInfoConfig: floatTextInfoConfigs){
            pos.add(floatTextInfoConfig.toConfig());
        }
        config.set("floatSpawnPos",pos);
        config.save();

    }

    @Override
    public GameRoomConfig clone() {
        try {
            return (GameRoomConfig) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof GameRoomConfig){
            return name.equalsIgnoreCase(((GameRoomConfig) obj).name);
        }
        return false;
    }

    public static GameRoomConfig createGameRoom(String name,int size,int maxSize){
        GameRoomConfig roomConfig = new GameRoomConfig(name,null,300,120,20,size,maxSize,new ArrayList<>());
        TotalManager.saveResource("team.yml","/rooms/"+name+"/team.yml",false);
        loadTeamConfig(roomConfig);
        return roomConfig;

    }

    public static void loadTeamConfig(GameRoomConfig roomConfig) {
        Config team = new Config(TotalManager.getDataFolder() + "/rooms/" + roomConfig.name + "/team.yml", Config.YAML);
        LinkedHashMap<String, TeamConfig> teamConfigs = new LinkedHashMap<>();
        for (Map<?, ?> map : team.getMapList("team")) {
            TeamConfig teamConfig = TeamConfig.getInstance(map);
            teamConfigs.put(teamConfig.getName(), teamConfig);
        }
        roomConfig.setTeamCfg(teamConfigs);
    }
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
