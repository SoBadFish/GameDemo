package org.sobadfish.gamedemo.room.event;


import org.sobadfish.gamedemo.room.GameRoom;
import org.sobadfish.gamedemo.room.config.GameRoomEventConfig;

/**
 * @author Sobadfish
 * 13:37
 */
public abstract class IGameRoomEvent {

    public GameRoom room;

    public GameRoomEventConfig.GameRoomEventItem item;

    public IGameRoomEvent(GameRoomEventConfig.GameRoomEventItem item){
        this.item = item;
    }

    /**
     * 事件配置
     * @return 事件内容
     * */
    public GameRoomEventConfig.GameRoomEventItem getEventItem(){
        return item;
    }

    public int getEventTime(){
        return item.eventTime;
    }
    /**
     * 事件启动
     * @param room 游戏房间
     * */
    abstract public void onStart(GameRoom room);

    /**
     * 事件被创建
     * @param room 游戏房间
     * */
    public void onCreate(GameRoom room){
        this.room = room;
    }

    /**
     * 事件显示的名称
     * @return 显示名称
     * */
    public String display(){
        return item.display;
    }



}
