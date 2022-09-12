package org.sobadfish.gamedemo.manager;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.gamedemo.GameDemoMain;
import org.sobadfish.gamedemo.panel.from.GameFrom;
import org.sobadfish.gamedemo.panel.lib.AbstractFakeInventory;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * 插件启动控制器
 * @author Sobadfish
 * @date 2022/9/12
 */
public class TotalManager {

    public static PluginBase plugin = null;

    public static LinkedHashMap<String, GameFrom> FROM = new LinkedHashMap<>();

    public static void init(PluginBase pluginBase){
        TotalManager.plugin = pluginBase;
        loadConfig();
        ThreadManager.init();
    }

    public static GameDemoMain gameDemoMain;

    private static RoomManager roomManager;

    private static MenuRoomManager menuRoomManager;

    public static Boolean isDisabled(){
        if(plugin != null){
            return plugin.isDisabled();
        }
        return true;
    }

    public static PluginBase getPlugin() {
        return plugin;
    }

    public static Config getConfig(){
        if(plugin == null){
            return new Config();
        }
        return plugin.getConfig();
    }

    public static void sendMessageToConsole(String msg){
        sendMessageToObject(msg,null);
    }

    public static String getTitle(){
        return TextFormat.colorize('&', gameDemoMain.getConfig().getString("title"));
    }

    public static String getScoreBoardTitle(){
        return TextFormat.colorize('&', gameDemoMain.getConfig().getString("scoreboard-title","&f[&a迷你战墙&f]"));
    }

    public static void sendTipMessageToObject(String msg,Object o){
        String message = TextFormat.colorize('&',msg);
        if(o != null){
            if(o instanceof Player){
                if(((Player) o).isOnline()) {
                    ((Player) o).sendMessage(message);
                    return;
                }
            }
        }
        gameDemoMain.getLogger().info(message);

    }

    /**
     * 加载配置文件
     */
    public static void loadConfig(){
        if(plugin == null){
            return;
        }
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        File mainFileDir = new File(plugin.getDataFolder()+File.separator+"rooms");
        if(!mainFileDir.exists()){
            if(!mainFileDir.mkdirs()){
                sendMessageToConsole("&c创建文件夹 rooms失败");
            }
        }

        roomManager = RoomManager.initGameRoomConfig(mainFileDir);
        sendMessageToConsole("&a房间数据全部加载完成");
        plugin.getServer().getPluginManager().registerEvents(roomManager,plugin);
        if(plugin.getConfig().getAll().size() == 0) {
            plugin.saveResource("config.yml", true);
            plugin.reloadConfig();
        }
        menuRoomManager = new MenuRoomManager(plugin.getConfig());

    }

    public static RoomManager getRoomManager() {
        return roomManager;
    }

    public static MenuRoomManager getMenuRoomManager() {
        return menuRoomManager;
    }

    public static GameDemoMain getGameDemoMain() {
        return gameDemoMain;
    }

    public static void sendMessageToObject(String msg, Object o){
        String message = TextFormat.colorize('&',getTitle()+" &b>>&r "+msg);
        sendTipMessageToObject(message,o);
    }

    public static void sendSubTitle(String msg,Player o){
        String message = TextFormat.colorize('&',msg);
        if(o != null){
            if(o.isOnline()) {
                o.setSubtitle(message);
            }
        }else{
            gameDemoMain.getLogger().info(message);
        }
    }

    public static void sendTitle(String msg,int time,Player o){
        String message = TextFormat.colorize('&',msg);
        if(o != null){
            if(o.isOnline()) {
                o.sendTitle(message,null,0,time,0);
            }
        }else{
            gameDemoMain.getLogger().info(message);
        }
    }

    public static void sendTip(String msg,Player o){
        String message = TextFormat.colorize('&',msg);
        if(o != null){
            if(o.isOnline()) {
                o.sendTip(message);
            }
        }else{
            gameDemoMain.getLogger().info(message);
        }
    }

    public static void saveResource(String s, String s1, boolean b) {
        if(!isDisabled()){
            plugin.saveResource(s, s1, b);
        }
    }

    public static File getDataFolder() {
        if(isDisabled()){
            return new File("");
        }
        return plugin.getDataFolder();
    }


    private void checkServer(){
        boolean ver = false;
        //双核心兼容
        try {
            Class<?> c = Class.forName("cn.nukkit.Nukkit");
            c.getField("NUKKIT_PM1E");
            ver = true;

        } catch (ClassNotFoundException | NoSuchFieldException ignore) { }
        try {
            Class<?> c = Class.forName("cn.nukkit.Nukkit");
            c.getField("NUKKIT").get(c).toString().equalsIgnoreCase("Nukkit PetteriM1 Edition");
            ver = true;
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignore) {
        }

        AbstractFakeInventory.IS_PM1E = ver;
        if(ver){
            sendMessageToConsole("&e当前核心为 Nukkit PM1E");
        }else{
            sendMessageToConsole("&e当前核心为 Nukkit");
        }
    }


}
