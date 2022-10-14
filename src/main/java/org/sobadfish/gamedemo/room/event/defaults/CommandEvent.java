package org.sobadfish.gamedemo.room.event.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;

import org.sobadfish.gamedemo.player.PlayerInfo;
import org.sobadfish.gamedemo.room.GameRoom;
import org.sobadfish.gamedemo.room.config.GameRoomEventConfig;
import org.sobadfish.gamedemo.room.event.IGameRoomEvent;

/**
 * @author Sobadfish
 */
public class CommandEvent extends IGameRoomEvent {

    public CommandEvent(GameRoomEventConfig.GameRoomEventItem item) {
        super(item);
    }

    @Override
    public void onStart(GameRoom room) {
        for(PlayerInfo info: room.getLivePlayers()){
            Server.getInstance().getCommandMap().dispatch(new ConsoleCommandSender(),getEventItem().value.toString().replace("@p","'"+info.getName()+"'"));
        }
    }
}
