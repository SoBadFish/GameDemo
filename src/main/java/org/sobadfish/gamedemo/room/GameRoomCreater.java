package org.sobadfish.gamedemo.room;



import org.sobadfish.gamedemo.player.PlayerInfo;
import org.sobadfish.gamedemo.player.team.config.TeamInfoConfig;
import org.sobadfish.gamedemo.room.config.GameRoomConfig;
import org.sobadfish.gamedemo.room.config.WorldInfoConfig;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 房间创建器，可以根据自身需求定制化创建房间配置
 * @author SoBadFish
 * 2022/1/13
 */
public class GameRoomCreater {

    private final PlayerInfo creater;

    private boolean isCreate;

    private boolean isRoomCreate;

    private int flag = 1;

    private int setFlag = 1;

    private String roomName = null;

    private int inflag = 0;

    private int min;

    private int itemFlag = 0;

    private GameRoomConfig roomConfig;

    private WorldInfoConfig worldInfoConfig;


    /**
     * 队伍出生点
     * */
    private LinkedHashMap<String, String> team = new LinkedHashMap<>();
    /**
     * 队伍得分点
     * */
    private LinkedHashMap<String, String> teamScore = new LinkedHashMap<>();



    public GameRoomCreater(PlayerInfo player){
        this.creater = player;
    }



    public void onCreatePreset(String value){
        if(flag !=  1){
            creater.sendForceMessage("&c你正在进行默认创建，无法使用预设");
            return;
        }
        switch (setFlag){
            case 1:
                creater.sendForceMessage("&2正在创建 名称为 &r"+value+" &2的房间模板");
                creater.sendForceMessage("&e继续执行 &r/wba set &a[最低玩家数]&e 执行下一步操作");
                roomName = value;
                setFlag++;
                break;
            case 2:
                creater.sendForceMessage("&2设置最低人数 "+value);
                creater.sendForceMessage("&e继续执行 &r/wba set &2[最大玩家数]&e 执行下一步操作");
                min = Integer.parseInt(value);
                setFlag++;
                break;
            case 3:
                int max = Integer.parseInt(value);
                roomConfig = GameRoomConfig.createGameRoom(roomName,min, max);
                creater.sendForceMessage("&2设置最大人数:&b "+value);
                creater.sendForceMessage("&a预设完成");
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
            creater.sendForceMessage("&c终止预设");
        }else{
            creater.sendForceMessage("&c无法终止预设");
        }
    }

    public boolean onCreateNext(){
        if(setFlag != 1){
            creater.sendForceMessage("&c请先完成预设");
            return true;
        }
        //测试创建
        switch (flag) {
            case 1:
                if(roomConfig == null) {
                    roomConfig = GameRoomConfig.createGameRoom("测试房间", 4, 16);
                    isRoomCreate = true;
                    creater.sendForceMessage("&2成功创建一个 名字已经固定为 &r“测试房间”&2的游戏房间模板 已设定最低玩家为 &b4&2 最大玩家为 &b16&r");
                    creater.sendForceMessage("继续执行/gda 进行下一步 [进入游戏地图设置]");
                }else{
                    creater.sendForceMessage("&2成功预设房间设置");
                    creater.sendForceMessage("&e继续执行 &r/gda &r进行下一步 &b[进入游戏地图设置]");
                }
                flag++;
                break;
            case 2:
                worldInfoConfig = WorldInfoConfig.createWorldConfig(creater.getLevel().getFolderName());
                creater.sendForceMessage("&2成功设定游戏地图");
                creater.sendForceMessage("&e继续执行 &r/gda &e进行下一步 &b[设置等待大厅]");
                flag++;
                break;
            case 3:
                worldInfoConfig.setWaitPosition(creater.getPosition());
                creater.sendForceMessage("&2成功等待大厅");
                creater.sendForceMessage("&e继续执行 &r/gda &e进行下一步 &r[&b设置"+(new ArrayList<>(roomConfig.teamCfg.keySet()).get(team.size()))+"出生点 &21&b /&d "+roomConfig.teamCfg.size()+"&r]");
                flag++;
                break;
            case 6:
                team.put(new ArrayList<>(roomConfig.teamCfg.keySet()).get(team.size()),WorldInfoConfig.positionToString(creater.getPosition()));
                int index = 0;
                if(team.size() > 0){
                    index = team.size() - 1;
                }
                creater.sendForceMessage("&2设置"+(new ArrayList<>(roomConfig.teamCfg.keySet()).get(index))+"出生点 &r[&2"+team.size()+"&b/&d"+roomConfig.getTeamCfg().size()+"&r]");
                if(team.size() == roomConfig.getTeamCfg().size()){

                    creater.sendForceMessage("&2队伍出生点设置完成");
                    ArrayList<TeamInfoConfig> teamInfoConfigs = new ArrayList<>();
                    for(String teamName : team.keySet()){
                        TeamInfoConfig teamInfoConfig = new TeamInfoConfig(roomConfig.teamCfg.get(teamName),team.get(teamName));

                        teamInfoConfigs.add(teamInfoConfig);
                    }
                    roomConfig.setTeamConfigs(teamInfoConfigs);
                    roomConfig.setWorldInfo(worldInfoConfig);
                    //TODO 到这里房间创建完成
                    flag = 1;
                    isCreate = true;
                    creater.sendForceMessage("&a游戏房间创建完成 &c(重启生效配置)");
                    return false;
                }
                creater.sendForceMessage("&e继续执行 &r/gda &e进行下一步 &r[&b设置"+(new ArrayList<>(roomConfig.teamCfg.keySet()).get(team.size()))+"出生点 &r[&2"+(team.size() + 1)+" &b/&d "+roomConfig.getTeamCfg().size()+"&r]");
                break;
            default:
                break;
        }

        return true;

    }



    public boolean createRoom(){
        if(isCreate) {
            roomConfig.save();
            creater.sendForceMessage("&a游戏已创建");
            return true;
        }else{
            creater.sendForceMessage("&c游戏未创建");
        }
        return false;
    }

    public GameRoomConfig getRoomConfig(){
        if(isRoomCreate) {
            return roomConfig;
        }
        return null;
    }

    public PlayerInfo getCreater() {
        return creater;
    }
}
