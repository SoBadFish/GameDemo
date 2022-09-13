package org.sobadfish.gamedemo.manager.data;


import org.sobadfish.gamedemo.manager.BaseDataWriterGetterManager;
import org.sobadfish.gamedemo.player.PlayerData;

import java.io.File;
import java.util.List;

public class PlayerDataManager extends BaseDataWriterGetterManager<PlayerData> {


    public PlayerDataManager(List<PlayerData> dataList, File file) {
        super(dataList, file);
    }

    public PlayerData getData(String player){
        PlayerData data = new PlayerData(player);

        if(!dataList.contains(data)){
            dataList.add(data);
        }
        return dataList.get(dataList.indexOf(data));
    }

    public static PlayerDataManager asFile(File file){
        return (PlayerDataManager) BaseDataWriterGetterManager.asFile(file,"player.json", PlayerData[].class,PlayerDataManager.class);
    }

}
