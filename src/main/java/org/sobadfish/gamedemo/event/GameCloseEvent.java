package org.sobadfish.gamedemo.event;

import cn.nukkit.plugin.Plugin;
import org.sobadfish.gamedemo.room.GameRoom;


/**
 * 房间关闭事件
 * @author SoBadFish
 * 2022/1/15
 */
public class GameCloseEvent extends GameRoomEvent{

    public GameCloseEvent(GameRoom room, Plugin plugin) {
        super(room, plugin);
    }
}
