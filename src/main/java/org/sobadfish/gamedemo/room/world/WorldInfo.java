package org.sobadfish.gamedemo.room.world;


import cn.nukkit.block.Block;
import cn.nukkit.level.Position;
import org.sobadfish.gamedemo.dlc.IGameRoomDlc;
import org.sobadfish.gamedemo.entity.GameFloatText;
import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.player.PlayerInfo;
import org.sobadfish.gamedemo.room.GameRoom;
import org.sobadfish.gamedemo.room.config.WorldInfoConfig;

import java.util.*;

/**
 * 地图的实例化方法，当房间启动后，这个方法也随之启动
 * @author Sobadfish
 *  2022/9/9
 */
public class WorldInfo {

    private GameRoom room;


    private boolean isClose;

    public boolean isStart;

    private WorldInfoConfig config;

    public LinkedHashMap<Position,Long> clickChest = new LinkedHashMap<>();

    private LinkedHashMap<Position,GameFloatText> resetChestFloat = new LinkedHashMap<>();

    public List<Block> placeBlock = new ArrayList<>();

    public WorldInfo(GameRoom room,WorldInfoConfig config){
        this.config = config;
        this.room = room;

    }

    /**
     * 增加浮空字刷新
     * @param position 浮空字的位置
     * */
    public void clickChest(Position position){
        clickChest.put(position,System.currentTimeMillis());
        if(room.roomConfig.chestCanReset){
            long time = clickChest.get(position) + (room.getRoomConfig().chestResetTime * 1000L);
            resetChestFloat.put(position,GameFloatText.showFloatText(
                    WorldInfoConfig.positionToString(position),
                    position.add(0.5,1.25,0.5),
                    TotalManager.getLanguage().getLanguage("chest-reset-title","&7[&a[1]&7]",
                            PlayerInfo.formatTime1((int) ((time - System.currentTimeMillis()) / 1000)))
            ));
        }
    }



    public WorldInfoConfig getConfig() {
        return config;
    }

    public boolean isClose() {
        return isClose;
    }

    public void setClose(boolean close) {
        isClose = close;
        clickChest.clear();
        for(GameFloatText gameFloatText: resetChestFloat.values()){
            gameFloatText.toClose();
        }
        resetChestFloat.clear();
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
        ///////////////////DO Something////////////
        if(room.isStart()){
            for(IGameRoomDlc dlc: room.getGameRoomDlc()){
                dlc.onWorldUpdate(this);
            }
        }
        //TODO 地图更新 每秒更新一次 可实现一些定制化功能

        if(room.close){

            return;
        }
        //更新浮空字与箱子刷新
        if(room.roomConfig.roundChest && room.roomConfig.chestCanReset){
            //TODO 迭代器防止空指针异常
            Set<Map.Entry<Position, Long>> set= clickChest.entrySet();
            Iterator<Map.Entry<Position, Long>> iterator=set.iterator();

            while (iterator.hasNext()) {
                Map.Entry<Position, Long> chest = iterator.next();
                if(chest.getValue() + room.getRoomConfig().chestResetTime * 1000L <= System.currentTimeMillis()){
                    if(resetChestFloat.containsKey(chest.getKey())){
                        GameFloatText floatText = resetChestFloat.remove(chest.getKey());
                        floatText.toClose();
                    }
                    iterator.remove();
                }else{
                    if(resetChestFloat.containsKey(chest.getKey())){
                        int time = (int) ((chest.getValue() + room.getRoomConfig().chestResetTime * 1000L
                                - System.currentTimeMillis()) / 1000);
                        String title = TotalManager.getLanguage().getLanguage("chest-reset-title","&7[&a[1]&7]",
                                PlayerInfo.formatTime1(time));
                        GameFloatText gameFloatText = resetChestFloat.get(chest.getKey());
                        gameFloatText.setText(title);
                    }
                }
            }
        }
    }
}
