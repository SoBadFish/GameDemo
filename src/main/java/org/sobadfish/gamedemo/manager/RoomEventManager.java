package org.sobadfish.gamedemo.manager;




import org.sobadfish.gamedemo.room.config.GameRoomEventConfig;
import org.sobadfish.gamedemo.room.event.IGameRoomEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;

public class RoomEventManager {

    public static LinkedHashMap<String, Class<? extends IGameRoomEvent>> EVENT = new LinkedHashMap<>();

    public static void register(String name,Class<? extends IGameRoomEvent> event){
        EVENT.put(name, event);
    }


    public static IGameRoomEvent getEventByType(GameRoomEventConfig.GameRoomEventItem item){
        if(EVENT.containsKey(item.eventType)){
            Class<? extends IGameRoomEvent> e = EVENT.get(item.eventType);
            try {

                return e.getConstructor(GameRoomEventConfig.GameRoomEventItem.class).newInstance(item);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e1) {
                e1.printStackTrace();
            }
        }
        return null;

    }

}
