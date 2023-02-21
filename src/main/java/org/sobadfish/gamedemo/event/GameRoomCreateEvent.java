package org.sobadfish.gamedemo.event;

import cn.nukkit.plugin.Plugin;
import org.sobadfish.gamedemo.dlc.IGameRoomDlc;
import org.sobadfish.gamedemo.room.GameRoom;

/**
 * @author Sobadfish
 * @date 2023/2/21
 */
public class GameRoomCreateEvent extends GameRoomEvent{

    public GameRoomCreateEvent(GameRoom room, Plugin plugin) {
        super(room, plugin);
    }

    public void addDlc(IGameRoomDlc dlc){
        getRoom().getGameRoomDlc().add(dlc);
    }
}
