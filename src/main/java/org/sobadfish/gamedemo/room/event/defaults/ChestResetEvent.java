package org.sobadfish.gamedemo.room.event.defaults;

import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.room.GameRoom;
import org.sobadfish.gamedemo.room.config.GameRoomEventConfig;
import org.sobadfish.gamedemo.room.event.IGameRoomEvent;

/**
 * @author Sobadfish
 * @date 2023/1/12
 */
public class ChestResetEvent extends IGameRoomEvent {

    public ChestResetEvent(GameRoomEventConfig.GameRoomEventItem item) {
        super(item);
    }

    @Override
    public void onStart(GameRoom room) {
        room.worldInfo.clickChest.clear();
        room.sendMessage(TotalManager.getLanguage().getLanguage("room-event-chest-reset","&a箱子全部刷新!"));
    }
}
