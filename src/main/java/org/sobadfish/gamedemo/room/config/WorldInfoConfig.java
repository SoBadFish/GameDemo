package org.sobadfish.gamedemo.room.config;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.tools.Utils;

import java.io.File;

/**
 * 地图的一些配置信息
 * 可以增加游戏地图内的一些坐标点
 * @author Sobadfish
 *  2022/9/9
 */
public class WorldInfoConfig {

    private final String level;


    public Level cacheWorld;

    /**
     * 等待大厅坐标
     * */
    private String waitPosition;



    private WorldInfoConfig(String gameWorld,
                            String waitPosition
    ){
        this.level = gameWorld;
        this.waitPosition = waitPosition;



    }

    public static WorldInfoConfig createWorldConfig(String gameWorld){
        return new WorldInfoConfig(gameWorld,null);
    }


    public Level getGameWorld() {
        Level l = null;
        if(Server.getInstance().isLevelLoaded(level)){
            l = Server.getInstance().getLevelByName(level);
        }
//        Level l =  Server.getInstance().isLevelLoaded(level) && Server.getInstance().getLevelByName(level) != null
//                && Server.getInstance().getLevelByName(level).getSafeSpawn() != null?Server.getInstance().getLevelByName(level):null;
        return l;
    }


    public String getLevel() {
        return level;
    }


    public Position getWaitPosition() {
        return getPositionByString(waitPosition);
    }

    public void setWaitPosition(Position waitPosition) {
        this.waitPosition = positionToString(waitPosition);
    }

    /**
     * 如果插件内不存在地图，则从worlds文件夹中备份，反之写入worlds文件夹
     * @param roomName 房间名称
     * @param levelName 地图名称
     * @return 是否完成初始化地图
     * */
    public static boolean initWorld(String roomName,String levelName){
        //插件的地图
        File nameFile = new File(TotalManager.getDataFolder()+File.separator+"rooms"+File.separator+roomName);
        //主世界地图
        File world = new File(nameFile+File.separator+"world"+File.separator+levelName);
        if(world.exists() && world.isDirectory()){
            if(toPathWorld(roomName, levelName,true)){
                Server.getInstance().loadLevel(levelName);
                TotalManager.sendMessageToConsole(TotalManager.getLanguage().getLanguage("world-init-success","&a地图 &e[1] &a初始化完成",
                        levelName));
            }else{
                TotalManager.sendMessageToConsole(TotalManager.getLanguage().getLanguage("world-init-error","&c地图 &e[1] &c初始化失败,无法完成房间的加载",
                        levelName));
                return false;
            }
        }
        if(!world.exists()){
            if(toBackUpWorld(roomName, levelName)){
                TotalManager.sendMessageToConsole(TotalManager.getLanguage().getLanguage("world-backup-success","&a地图 &e[1] &a备份完成",
                        levelName));
            }else{
                TotalManager.sendMessageToConsole(TotalManager.getLanguage().getLanguage("world-backup-success","&a地图 &e[1] &a备份完成",
                        levelName));
                return false;
            }
        }

        return true;
    }

    public static boolean toBackUpWorld(String roomName,String levelName){
        File nameFile = new File(TotalManager.getDataFolder()+File.separator+"rooms"+File.separator+roomName);
        File world = new File(nameFile+File.separator+"world"+File.separator+levelName);
        if(!world.exists()){
            if(!world.mkdirs()){
                TotalManager.sendMessageToConsole(TotalManager.getLanguage().getLanguage("world-create-folder-error","&c创建地图文件夹失败"));
            }
        }

        //
        return Utils.copyFiles(new File(Server.getInstance().getFilePath()+File.separator + "worlds" +File.separator+ levelName), world);
    }


    /**
     * 还原地图核心算法
     * @param roomName 房间名称
     * @param levelName 地图名称
     * @param isInit 是否是初始化状态
     *
     * @return 是否还原成功
     * */
    public static boolean toPathWorld(String roomName,String levelName,boolean isInit){
        try {

            File nameFile = new File(TotalManager.getDataFolder() + File.separator + "rooms" + File.separator + roomName);
            if (!nameFile.exists()) {
                return false;
            }
            File world = new File(nameFile + File.separator + "world" + File.separator + levelName);
            File[] files = world.listFiles();
            File f2 = new File(Server.getInstance().getFilePath() + File.separator + "worlds" + File.separator + levelName);
            if (!f2.exists()) {
                if(!f2.mkdirs()){
                    TotalManager.sendMessageToConsole(TotalManager.getLanguage().getLanguage("world-create-folder-error","&c创建地图文件夹失败"));
                }
            }
            if (files != null && files.length > 0) {
                //扔到主线程
                if(!isInit) {
                    Server.getInstance().getScheduler().scheduleTask(TotalManager.getPlugin(), () -> {
                        if (Server.getInstance().isLevelLoaded(levelName)) {
                            Server.getInstance().unloadLevel(Server.getInstance().getLevelByName(levelName), true);
                        }
                    });
                }
                Utils.toDelete(f2);
                if(!f2.exists() && !f2.mkdirs()){
                    TotalManager.sendMessageToConsole(TotalManager.getLanguage().getLanguage("world-create-folder-error","&c创建地图文件夹失败"));
                }
                Utils.copyFiles(world, f2);
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
        //载入地图 删掉之前的地图文件
    }




    public static WorldInfoConfig getInstance(String roomName, Config config){
        if(!initWorld(roomName,config.getString("world"))){
            return null;
        }



        return new WorldInfoConfig(config.getString("world"),config.getString("waitPosition"));
    }

    public static String positionToString(Position position){
        return position.getFloorX() + ":"+position.getFloorY()+":"+position.getFloorZ()+":"+position.getLevel().getFolderName();
    }

    public static String locationToString(Location position){
        return position.getFloorX() + ":"+position.getFloorY()+":"+position.getFloorZ()+":"+position.getLevel().getFolderName()+":"+position.yaw;
    }

    public static Position getPositionByString(String str){
        String[] pos = str.split(":");
        Level level = Server.getInstance().getLevelByName(pos[3]);

        return new Position(
                Integer.parseInt(pos[0]),
                Integer.parseInt(pos[1]),
                Integer.parseInt(pos[2]),
                level

        );

    }
    public static Location getLocationByString(String str){
        String[] pos = str.split(":");
        Level level = Server.getInstance().getLevelByName(pos[3]);
//        if(level == null){
//            if(Server.getInstance().loadLevel(pos[3])){
//                level = Server.getInstance().getLevelByName(pos[3]);
//            }
//        }
        return new Location(
                Integer.parseInt(pos[0]),
                Integer.parseInt(pos[1]),
                Integer.parseInt(pos[2]),
                Double.parseDouble(pos[4]),
                0, level

        );

    }


}
