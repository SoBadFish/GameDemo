package org.sobadfish.gamedemo.player;

import cn.nukkit.Server;
import org.sobadfish.gamedemo.event.PlayerGetExpEvent;
import org.sobadfish.gamedemo.event.PlayerLevelChangeEvent;
import org.sobadfish.gamedemo.manager.FunctionManager;
import org.sobadfish.gamedemo.manager.TotalManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 玩家数据
 * */
public class PlayerData {

    //玩家名称
    private String name = "";

    //玩家经验
    private int exp;

    //玩家等级
    private int level;

    public PlayerData(String name){
        this.name = name;
    }

    public PlayerData(){}


    public List<RoomData> roomData = new ArrayList<>();

    public int getFinalData(DataType dataType){
        int c = 0;
        for(RoomData data: roomData){
            c += data.getInt(dataType);
        }
        return c;
    }

    public int getExp() {
        if(exp < 0){
            exp = 0;
        }
        return exp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void addExp(int exp, String cause){
        addExp(exp,cause,true);
    }


    public void addExp(int exp,String cause,boolean event){
        if(this.exp < 0){
            this.exp = 0;
        }
        this.exp += exp;
        if(this.exp >= getNextLevelExp()){
            this.exp -= getNextLevelExp();
            PlayerLevelChangeEvent event1 = new PlayerLevelChangeEvent(name,level,1);
            Server.getInstance().getPluginManager().callEvent(event1);
            if(event1.isCancelled()){
                return;
            }
            level += event1.getNewLevel();

            int nExp = this.exp - getNextLevelExp();
            if(nExp > 0){
                this.exp -= getNextLevelExp();
                addExp(nExp,null,false);
            }
        }
        if(event) {
            PlayerGetExpEvent expEvent = new PlayerGetExpEvent(name, exp,this.exp,cause);
            Server.getInstance().getPluginManager().callEvent(expEvent);
        }
    }

    /**
     * 获取等级百分比
     * */
    public double getExpPercent(){
        double r = 0;
        if(this.exp > 0){
            r = (double) this.exp / (double) getNextLevelExp();
        }
        return r;
    }

    /**
     * 获取等级条
     * */
    public String getExpLine(int size){
        return FunctionManager.drawLine((float) getExpPercent(),size,"&b■","&7■");
    }

    /**
     * 根据等级获取颜色
     * */
    public String getColorByLevel(int level){
        String[] color = new String[]{"&7","&f","&6","&b","&2","&3","&4","&d","&6","&e"};
        if(level < 100){
            return color[0];
        }else{
            return color[(level / 100) % 10];
        }

    }

    public String getLevelString(){
        String str = "✫";
        if(level > 1000){
            str = "✪";
        }
        return getColorByLevel(level)+level+str;
    }

    public String getExpString(int exp){
        double e = exp;
        e /= 1000;
        if(e < 10 && e >= 1){
            return String.format("%.1f",e)+"k";
        }else if(e > 10){
            e /= 10;
            if(e < 1000){
                return String.format("%.1f",e)+"w";
            }else{
                return String.format("%.1f",e)+"bill";
            }
        }else{
            return String.format("%.1f",(double)exp);
        }
    }

    public int getNextLevelExp(){
        double l = level;
         l+= 1;
        if(l > 100){
            l = l / 100.0;
            l = l - (int) l;
            l *= 100;
            if(l <= 0){
                l = 1;
            }
        }
       return (int)l * TotalManager.getUpExp();
    }



    public static class RoomData{

        public String roomName = "";

        //击杀数量
        public int killCount = 0;

        //死亡数量
        public int deathCount = 0;
        //游戏次数
        public int gameCount = 0;

        //失败次数
        public int defeatCount = 0;

        //胜利次数
        public int victoryCount = 0;

        //助攻次数
        public int assist = 0;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            RoomData roomData = (RoomData) o;
            return Objects.equals(roomName, roomData.roomName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(roomName);
        }

        public int getInt(DataType type){
            int c = 0;
            switch (type){
                case VICTORY:
                    c += victoryCount;
                    break;
                case DEFEAT:
                    c += defeatCount;
                    break;
                case DEATH:
                    c += deathCount;
                    break;
                case KILL:
                    c += killCount;
                    break;
                case GAME:
                    c += gameCount;
                    break;
                case ASSISTS:
                    c += assist;
                    break;
                default:break;

            }

            return c;


        }
    }

    public RoomData getRoomData(String room){
        RoomData roomData = new RoomData();
        roomData.roomName = room;
        if(!this.roomData.contains(roomData)){
            this.roomData.add(roomData);
        }else{
            roomData = this.roomData.get(this.roomData.indexOf(roomData));
        }
        return roomData;
    }

    public void setInfo(PlayerInfo info){
        RoomData data = getRoomData(info.getGameRoom().getRoomConfig().name);
        data.deathCount += info.deathCount;
        data.killCount += info.killCount;
        data.assist += info.assists;
    }

    @Override
    public boolean equals(Object o) {
       if(o instanceof PlayerData){
           return ((PlayerData) o).name.equalsIgnoreCase(name);
       }
       return false;
    }

    public enum DataType{
        /**
         * 击杀
         * */
        KILL("击杀"),
        /**
         * 死亡
         * */
        DEATH("死亡"),
        /**
         * 胜利
         * */
        VICTORY("胜利"),
        /**
         * 失败
         * */
        DEFEAT("失败"),
        /**
         * 助攻
         * */
        ASSISTS("助攻"),

        /**
         * 游戏次数
         * */
        GAME("游戏次数");

        protected String name;

        DataType(String name){
            this.name = name;
        }


        public String getName() {
            return name;
        }

        public static DataType byName(String name){
            for(DataType type: values()){
                if(type.getName().equalsIgnoreCase(name)){
                    return type;
                }
            }
            return null;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
