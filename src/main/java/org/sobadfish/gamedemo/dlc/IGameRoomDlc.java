package org.sobadfish.gamedemo.dlc;

import org.sobadfish.gamedemo.player.PlayerInfo;
import org.sobadfish.gamedemo.room.GameRoom;
import org.sobadfish.gamedemo.room.world.WorldInfo;

/**
 * @author Sobadfish
 *  2023/2/21
 */
public abstract class IGameRoomDlc {


    public IGameRoomDlc(){}
    /**
     * 在房间中启动
     * @param room 房间
     * */
    public void onEnable(GameRoom room){}
    /**
     * dlc的名称
     * @return Dlc的名称
     * */
    public abstract String getName();
    /**
     * 游戏房间更新
     * 仅自定义游戏房间内的 onStart
     * @param room 游戏房间
     * */
    public abstract void onGameUpdate(GameRoom room);

    /**
     * 玩家状态更新
     * @param player 玩家
     * */
    public abstract void onPlayerUpdate(PlayerInfo player);

    /**
     * 游戏地图内的房间更新
     * @param room 地图
     * */
    public abstract void onWorldUpdate(WorldInfo room);


    /**
     * 游戏房间结束
     * @param room 房间
     * */
    public void onDisable(GameRoom room){}








}
