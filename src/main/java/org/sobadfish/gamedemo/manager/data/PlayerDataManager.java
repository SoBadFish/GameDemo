package org.sobadfish.gamedemo.manager.data;


import org.sobadfish.gamedemo.manager.BaseDataWriterGetterManager;
import org.sobadfish.gamedemo.player.PlayerData;

import java.io.File;
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
        return (PlayerDataManager) BaseDataWriterGetterManager.asFile(file,"player.json", PlayerData[].class,PlayerDataManager.class);
    }

}
