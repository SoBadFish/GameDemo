package org.sobadfish.gamedemo.dlc;

import org.sobadfish.gamedemo.player.PlayerInfo;
import org.sobadfish.gamedemo.room.GameRoom;
import org.sobadfish.gamedemo.room.world.WorldInfo;

/**
 * @author Sobadfish
 * @date 2023/2/21
 */
public interface IGameRoomDlc {


    /**
     * 游戏房间更新
     * 仅自定义游戏房间内的 onStart
     * @param room 游戏房间
     * @return 是否执行默认房间结束逻辑
     * */
    boolean onGameUpdate(GameRoom room);

    /**
     * 玩家状态更新
     * @param player 玩家
     * */
    void onPlayerUpdate(PlayerInfo player);

    /**
     * 游戏地图内的房间更新
     * @param room 地图
     * */
    void onWorldUpdate(WorldInfo room);


}
