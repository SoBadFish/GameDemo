package org.sobadfish.gamedemo.room;


import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.player.PlayerInfo;
import org.sobadfish.gamedemo.player.team.config.TeamInfoConfig;
import org.sobadfish.gamedemo.room.config.GameRoomConfig;
import org.sobadfish.gamedemo.room.config.WorldInfoConfig;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 房间创建器，可以根据自身需求定制化创建房间配置
 * @author SoBadFish
 * 2022/1/13
 */
public class GameRoomCreator {

    private final PlayerInfo creator;

    private boolean isCreate;

    private boolean isRoomCreate;

    public int flag = 1;

    public int setFlag = 1;

    //出生点标志位
    public int spawnFlag = 0;

    private String roomName = null;


    private int min;


    //队伍出生点数量
    private int spawnSize = 1;

    private GameRoomConfig roomConfig;

    private WorldInfoConfig worldInfoConfig;

    /**
     * 队伍出生点
     * */
    private final LinkedHashMap<String, List<String>> team = new LinkedHashMap<>();



    public GameRoomCreator(PlayerInfo player){
        this.creator = player;
    }



    public void onCreatePreset(String value){
        if(flag !=  1){
            creator.sendForceMessage(TotalManager.getLanguage().getLanguage("go-on-default-create-not-use","&c你正在进行默认创建，无法使用预设"));
            return;
        }
        switch (setFlag){
            case 1:
                String name = getRoomName(value);
                creator.sendForceMessage(TotalManager.getLanguage().getLanguage("create-room-template","&2正在创建 名称为 &r[1] &2的房间模板",name));
                creator.sendForceMessage(TotalManager.getLanguage().getLanguage("create-room-run-command-min-player",
                        "&e继续执行 &r/[1] set &a[最低玩家数]&e 执行下一步操作",
                        TotalManager.COMMAND_ADMIN_NAME));
                roomName = name;
                setFlag++;
                break;
            case 2:
                creator.sendForceMessage(TotalManager.getLanguage().getLanguage("create-room-setting-min-player","&2设置最低人数 [1]",value));
                creator.sendForceMessage(TotalManager.getLanguage().getLanguage("create-room-run-command-spawn-size",
                        "&e继续执行 &r/[1] set &2[出生点数量]&e 执行下一步操作",
                        TotalManager.COMMAND_ADMIN_NAME));
                min = Integer.parseInt(value);
                setFlag++;
                break;
            case 3:
                creator.sendForceMessage(TotalManager.getLanguage().getLanguage("create-room-setting-team-spawn-size","&2设置每个队伍的出生点数量 [1]",value+""));
                creator.sendForceMessage(TotalManager.getLanguage().getLanguage("create-room-run-command-max-player","&e继续执行 &r/[1] set &2[最大玩家数]&e 执行下一步操作",
                        TotalManager.COMMAND_ADMIN_NAME));
                spawnSize = Integer.parseInt(value);
                setFlag++;
                break;
            case 4:
                int max = Integer.parseInt(value);
                roomConfig = GameRoomConfig.createGameRoom(roomName,min, max);
                creator.sendForceMessage(TotalManager.getLanguage().getLanguage("create-room-setting-max-player","&2设置最大人数:&b [1]",value));
                creator.sendForceMessage(TotalManager.getLanguage().getLanguage("create-room-default-setting-success","&a预设完成"));
                //TODO 到这了模板创建完成
                isRoomCreate = true;
                setFlag = 1;
                break;
            default: break;
        }

    }

    public void stopInit(){
        if(setFlag >= 4) {
            setFlag = 1;
            creator.sendForceMessage(TotalManager.getLanguage().getLanguage("create-room-default-setting-cancel","&c终止预设"));
        }else{
            creator.sendForceMessage(TotalManager.getLanguage().getLanguage("create-room-default-setting-cancel-error","&c无法终止预设"));
        }
    }

    public boolean onCreateNext(){
        if(setFlag != 1){
            creator.sendForceMessage(TotalManager.getLanguage().getLanguage("create-room-must-success-setting","&c请先完成预设"));
            return true;
        }
        //测试创建
        switch (flag) {
            case 1:
                if(roomConfig == null) {
                    roomConfig = GameRoomConfig.createGameRoom(getRoomName(TotalManager.getLanguage().getLanguage("create-room=default-room-name","游戏房间")), 4, 16);
                    isRoomCreate = true;
                    creator.sendForceMessage(TotalManager.getLanguage().getLanguage("create-room-default-success","&2成功创建一个 名字已经固定为 &r“[1]”&2的游戏房间模板 已设定最低玩家为 &b4&2 最大玩家为 &b16&r",roomConfig.name));
                    creator.sendForceMessage("继续执行/"+ TotalManager.COMMAND_ADMIN_NAME+" 进行下一步 [进入游戏地图设置]");
                }else{
                    creator.sendForceMessage("&2成功预设房间设置");
                    creator.sendForceMessage("&e继续执行 &r/"+ TotalManager.COMMAND_ADMIN_NAME+" &r进行下一步 &b[进入游戏地图设置]");
                }
                flag++;
                break;
            case 2:
                worldInfoConfig = WorldInfoConfig.createWorldConfig(creator.getLevel().getFolderName());
                creator.sendForceMessage("&2成功设定游戏地图");
                creator.sendForceMessage("&e继续执行 &r/"+ TotalManager.COMMAND_ADMIN_NAME+" &e进行下一步 &b[设置等待大厅]");
                flag++;
                break;
            case 3:
                worldInfoConfig.setWaitPosition(creator.getPosition());
                creator.sendForceMessage("&2成功等待大厅");
                creator.sendForceMessage("&e继续执行 &r/"+ TotalManager.COMMAND_ADMIN_NAME+" &e进行下一步 &r[&b设置"+(new ArrayList<>(roomConfig.teamCfg.keySet()).get(team.size()))+"出生点 &21&b /&d "+roomConfig.teamCfg.size()+"&r]");
                flag++;
                break;
            case 4:
                createSpawnPos();
                flag++;
                /*
                 * 可根据自身需求更改
                 * 如有其他功能需要创建，增加case并修改提示即可
                 * */
                creator.sendForceMessage("&e继续执行 &r/"+ TotalManager.COMMAND_ADMIN_NAME+" &e进行下一步 &b[结束房间设置]");
                break;
            default:
                //TODO 到这里房间创建完成
                flag = 1;
                isCreate = true;
                creator.sendForceMessage("&a游戏房间创建完成 &c(重启生效配置)");
                return false;
        }

        return true;

    }

    public int spawnSizeFlag = 0;
    /**
     * 创建出生点
     * */
    private void createSpawnPos(){
        String teamName = new ArrayList<>(roomConfig.teamCfg.keySet()).get(spawnFlag);
        if(!team.containsKey(teamName)){
            team.put(teamName,new ArrayList<>());
        }
        ArrayList<String> positions = (ArrayList<String>) team.get(teamName);
        if(spawnSizeFlag < spawnSize){
            spawnSizeFlag++;
            if(positions.size() > spawnSizeFlag){
                positions.set(spawnSizeFlag,WorldInfoConfig.positionToString(creator.getPosition()));
            }else{
                positions.add(WorldInfoConfig.positionToString(creator.getPosition()));
            }

            creator.sendForceMessage("&2设置&r "+teamName+" &2出生点坐标&r [&2"+spawnSizeFlag+" &b/&d "+spawnSize+"&r]");
            if(spawnSizeFlag != spawnSize) {
                creator.sendForceMessage("&e继续执行 &r/"+ TotalManager.COMMAND_ADMIN_NAME+" &e进行下一步 &r[&b设置队伍出生点&r " + teamName + " [&2" + (positions.size() + 1) + " &b/&d "+spawnSize+"&r]");
            }else{
                if(new ArrayList<>(roomConfig.teamCfg.keySet()).size() > spawnFlag+1){
                    creator.sendForceMessage("&2设置 &r" + teamName + " &2出生点坐标完成");
                    creator.sendForceMessage("&e继续执行 &r/"+ TotalManager.COMMAND_ADMIN_NAME+" &e进行下一步 &r[&b设置 &r"+ new ArrayList<>(roomConfig.teamCfg.keySet()).get(spawnFlag + 1) + " &2出生点 &r [&21 &b/&d "+spawnSize+"&r]");
                }
                spawnSizeFlag = 0;
                spawnFlag++;
                if (spawnFlag >= new ArrayList<>(roomConfig.teamCfg.keySet()).size()) {
                    creator.sendForceMessage("&2设置所有出生点坐标完成");
                    spawnFlag = 0;
                    ArrayList<TeamInfoConfig> teamInfoConfigs = new ArrayList<>();
                    for(String ta : team.keySet()){
                        TeamInfoConfig teamInfoConfig = new TeamInfoConfig(roomConfig.teamCfg.get(ta),team.get(ta));

                        teamInfoConfigs.add(teamInfoConfig);
                    }
                    roomConfig.setTeamConfigs(teamInfoConfigs);
                    roomConfig.setWorldInfo(worldInfoConfig);
                }
            }
        }
    }






    /**
     * 避免出现重复的名称
     * @param name 名称
     * */
    public String getRoomName(String name){
        return getRoomName(name,0);
    }

    private String getRoomName(String name,int num){
        String rn = name;
        if(num > 0){
            rn += "("+num+")";
        }
        if(TotalManager.getRoomManager().hasRoom(rn)){
            return getRoomName(name,++num);
        }
        return rn;
    }



    public boolean createRoom(){
        if(isCreate) {
            roomConfig.save();
            creator.sendForceMessage("&a游戏已创建");
            return true;
        }else{
            creator.sendForceMessage("&c游戏未创建");
        }
        return false;
    }

    public GameRoomConfig getRoomConfig(){
        if(isRoomCreate) {
            return roomConfig;
        }
        return null;
    }

    public PlayerInfo getCreator() {
        return creator;
    }
}
