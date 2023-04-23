package org.sobadfish.gamedemo.player.data;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 玩家房间数据
 * @author Sobadfish
 * @date 2023/4/22
 */
public class RoomData implements Serializable {
    public String roomName = "";

    public LinkedHashMap<String, IDataValue<?>> data = new LinkedHashMap<>();

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

    public void add(String type,IDataValue<? extends Objects> value){
        if(data.containsKey(type)){
            IDataValue<?> dataValue = data.get(type);
            dataValue.addValue(value);
        }else{
            data.put(type,value);
        }
    }

    public void remove(String type,IDataValue<? extends Objects> value){
        if(data.containsKey(type)){
            IDataValue<?> dataValue = data.get(type);
            dataValue.removeValue(value);
        }else{
            data.put(type,value);
        }
    }

    public void remove(String type){
       data.remove(type);
    }


    public void set(String type,IDataValue<Objects> value){
        if(data.containsKey(type)){
            IDataValue<?> dataValue = data.get(type);
            dataValue.setValue(value);
        }else{
            data.put(type,value);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomName);
    }

    public int getInt(String type){
        int c = 0;
        if(type == null){
            return c;
        }
        if(data.containsKey(type)){
            IDataValue<?> dataValue = data.get(type);
            if(dataValue instanceof IntegerDataValue){
                return (Integer) dataValue.getValue();
            }

        }
        return c;
    }


    public static RoomData asRoomDataByJson(JsonObject json){
        RoomData  roomData = new RoomData();
        roomData.roomName = json.get("roomName").toString();
        Map<String,IDataValue<?>> dataValueMap = new HashMap<>();
        JsonObject sub = json.getAsJsonObject("data");
        for(String sk: sub.keySet()){
            JsonObject sub2 = sub.getAsJsonObject(sk);

            dataValueMap.put(sk,IDataValue.jsonObjectAsDataValue(sub2));

        }
        roomData.data = new LinkedHashMap<>(dataValueMap);

        return roomData;
    }


}
