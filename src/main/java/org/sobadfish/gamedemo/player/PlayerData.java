package org.sobadfish.gamedemo.player;

import cn.nukkit.Player;
import cn.nukkit.Server;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.sobadfish.gamedemo.event.PlayerGetExpEvent;
import org.sobadfish.gamedemo.event.PlayerLevelChangeEvent;
import org.sobadfish.gamedemo.manager.FunctionManager;
import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.player.data.IDataValue;
import org.sobadfish.gamedemo.player.data.IntegerDataValue;
import org.sobadfish.gamedemo.player.data.RoomData;

import java.util.*;

/**
 * 玩家数据
 *
 * @author Sobadfish
 * */
public class PlayerData{

    //玩家名称
    public String name = "";

    //玩家经验
    public int exp;

    //玩家等级
    public int level;

    public PlayerData(String name){
        this.name = name;
    }

    public PlayerData(){}

    public Map<String,IDataValue<?>> data = new LinkedHashMap<>();

    public List<RoomData> roomData = new ArrayList<>();



    public IDataValue<?> getDataByRoom(String room,String dataType){
        for(RoomData data: roomData){
            if(data.roomName.equalsIgnoreCase(room)){
                return data.data.get(dataType);
            }
        }
        return new IntegerDataValue(0);
    }

    public int getFinalData(String dataType){
        int c = 0;
        for(RoomData data: roomData){

            c += data.getInt(dataType);
        }
        if(data.containsKey(dataType)){
            IDataValue<?> dataValue = data.get(dataType);
            if(dataValue instanceof IntegerDataValue) {
                c += ((IntegerDataValue) dataValue).getValue();
            }
        }
        return c;
    }

    public int getExp() {
        if(exp < 0){
            exp = 0;
        }
        return exp;
    }



    public List<RoomData> getRoomData() {
        return roomData;
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
     * @return 当前经验到下一级经验的百分比
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
     * @param size 经验条长度
     * @return 获取经验条的字符串
     * */
    public String getExpLine(int size){
        return FunctionManager.drawLine((float) getExpPercent(),size,"&b■","&7■");
    }

    /**
     * 根据等级获取颜色
     * @param level 等级
     * @return 等级对应的颜色字符
     * */
    public String getColorByLevel(int level){
        String[] color = new String[]{"&7","&f","&6","&b","&2","&3","&4","&d","&6","&e"};
        if(level < 100){
            return color[0];
        }else{
            return color[(level / 100) % 10];
        }

    }

    /**
     * 根据等级的星级图标
     * @return 等级的星级
     * */
    public String getLevelString(){
        String str = "✫";
        if(level > 1000){
            str = "✪";
        }
        return getColorByLevel(level)+level+str;
    }

    /**
     * 将数据过长的数值转换为单位显示
     * @param exp 经验
     * @return 单位
     * */
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

    /**
     * 获取下一个等级的经验
     * @return 经验
     * */
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

    /**
     * 添加属性
     * */
    public void addData(String key,IDataValue<?> dataValue){
        if(data.containsKey(key)){
            IDataValue<?> value = data.get(key);
            value.addValue(dataValue);
        }else{
            data.put(key,dataValue);
        }

    }

    /**
     * 设置属性
     * */
    public void setData(String key,IDataValue<?> dataValue){
        if(data.containsKey(key)){
            IDataValue<?> value = data.get(key);
            value.setValue(dataValue);
        }else{
            data.put(key,dataValue);
        }

    }
    /**
     * 移除属性
     * */
    public void removeData(String key,IDataValue<?> dataValue){
        if(data.containsKey(key)){
            IDataValue<?> value = data.get(key);
            value.removeValue(dataValue);
        }else{
            data.put(key,dataValue);
        }

    }

    /**
     * 移除属性
     * */
    public void removeData(String key){
        data.remove(key);

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
        if(info.getPlayer() instanceof Player) {
            for (String dataType : info.statistics.keySet()) {
                if(data.data.containsKey(dataType)){
                    IDataValue<?> dataValue =  data.data.get(dataType);
                    IDataValue<?> pValue = info.statistics.get(dataType);
                    if(pValue.asAppend()){
                        dataValue.addValue((IDataValue<?>) pValue.getValue());
                    }

                    data.data.put(dataType,dataValue);
                }else{
                    data.data.put(dataType, info.statistics.get(dataType));
                }

            }
        }
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

        private final String name;

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

    public void save(){
        TotalManager.getDataManager().save();
    }

    public static PlayerData asJsonObject(JsonObject jsonObject){
        PlayerData data = new PlayerData(jsonObject.get("name").getAsString());
        Map<String,IDataValue<?>> dataValueMap = new HashMap<>();
        JsonObject sub = jsonObject.getAsJsonObject("data");
        for(String sk: sub.keySet()){
            JsonObject sub2 = sub.getAsJsonObject(sk);
            dataValueMap.put(sk,IDataValue.jsonObjectAsDataValue(sub2));

        }
        data.data = dataValueMap;
        List<RoomData> roomData = new ArrayList<>();
        JsonArray roomJson = jsonObject.getAsJsonArray("roomData");
        for(int i = 0; i < roomJson.size(); i++){
            JsonObject jsb = (JsonObject) roomJson.get(i);
            roomData.add(RoomData.asRoomDataByJson(jsb));
        }
        data.roomData = roomData;
        data.exp = jsonObject.get("exp").getAsInt();
        data.level = jsonObject.get("level").getAsInt();
        return data;
    }


    @Override
    public String toString() {
        return "PlayerData{" +
                "name='" + name + '\'' +
                ", exp=" + exp +
                ", level=" + level +
                ", data=" + data +
                ", roomData=" + roomData +
                '}';
    }
}
