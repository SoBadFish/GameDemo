package org.sobadfish.gamedemo.event;


import cn.nukkit.plugin.Plugin;
import org.sobadfish.gamedemo.room.GameRoom;

/**
 * @author SoBadFish
 * 2022/1/15
 */
public class GameRoomStartEvent extends GameRoomEvent {

    public GameRoomStartEvent(GameRoom room, Plugin plugin) {
        super(room, plugin);
    }
}
