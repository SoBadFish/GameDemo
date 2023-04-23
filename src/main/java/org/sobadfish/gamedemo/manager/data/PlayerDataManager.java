package org.sobadfish.gamedemo.manager.data;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.sobadfish.gamedemo.manager.BaseDataWriterGetterManager;
import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.player.PlayerData;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * 玩家在游戏中产生数据的管理类
 * 通过这个类可以获取玩家的数据信息 {@link PlayerData}
 * */
public class PlayerDataManager extends BaseDataWriterGetterManager<PlayerData> {


    public PlayerDataManager(List<PlayerData> dataList, File file) {
        super(dataList, file);
    }

    /**
     * 根据玩家名称获取玩家的数据
     * @param player 玩家名称
     * @return 玩家数据
     * */
    public PlayerData getData(String player){
        PlayerData data = new PlayerData(player);

        if(!dataList.contains(data)){
            dataList.add(data);
        }
        return dataList.get(dataList.indexOf(data));
    }

    /**
     * 读取配置文件
     * @param file 配置文件
     * @return 玩家控制类
     * */
    public static PlayerDataManager asFile(File file){
        Gson gson = new Gson();
        InputStreamReader reader = null;
        try{
            if(!file.exists()){
                TotalManager.saveResource("player.json",false);
            }
            reader = new InputStreamReader(new FileInputStream(file));

            JsonArray map =gson.fromJson(reader, JsonArray.class);
            List<PlayerData> data = new ArrayList<>();
            for(Object o: map){
                String json = o.toString();
                JsonObject jsb = new Gson().fromJson(json, JsonObject.class);
                PlayerData d = PlayerData.asJsonObject(jsb);
                data.add(d);
            }
            Constructor<?> constructor = PlayerDataManager.class.getConstructor(List.class,File.class);
            return (PlayerDataManager) constructor.newInstance(data,file);
        }catch (Exception e){
            e.printStackTrace();

        }
        return new PlayerDataManager(new ArrayList<>(),file);

    }


}
