package org.sobadfish.gamedemo.room.world;


import cn.nukkit.block.Block;
import org.sobadfish.gamedemo.room.GameRoom;
import org.sobadfish.gamedemo.room.config.WorldInfoConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图的实例化方法，当房间启动后，这个方法也随之启动
 * @author Sobadfish
 * @date 2022/9/9
 */
public class WorldInfo {

    private GameRoom room;


    private boolean isClose;

    public boolean isStart;

    private WorldInfoConfig config;

    public List<Block> placeBlock = new ArrayList<>();

    public WorldInfo(GameRoom room,WorldInfoConfig config){
        this.config = config;
        this.room = room;

    }

    public WorldInfoConfig getConfig() {
        return config;
    }

    public boolean isClose() {
        return isClose;
    }

    public void setClose(boolean close) {
        isClose = close;
    }

    public void onChangeBlock(Block block, boolean isPlace){

        if(isPlace){
            placeBlock.add(block);
        }else{
            placeBlock.remove(block);
        }
    }

    public List<Block> getPlaceBlock() {
        return placeBlock;
    }

    public void onUpdate() {
        //TODO 地图更新 每秒更新一次 可实现一些定制化功能


        ///////////////////DO Something////////////
    }
}
