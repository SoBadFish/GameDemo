package org.sobadfish.gamedemo.room.event.defaults;


import org.sobadfish.gamedemo.manager.RoomEventManager;
import org.sobadfish.gamedemo.room.GameRoom;
import org.sobadfish.gamedemo.room.config.GameRoomEventConfig;
import org.sobadfish.gamedemo.room.event.IEventProcess;
import org.sobadfish.gamedemo.room.event.IGameRoomEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Sobadfish
 */
public class CustomEvent extends IGameRoomEvent implements IEventProcess {

    public int position = 0;

    public EventRunType runType;

    public IGameRoomEvent random;

    public boolean isRun;

    private final List<IGameRoomEvent> gameRoomEvents = new ArrayList<>();

    @Override
    public void onCreate(GameRoom room) {
        super.onCreate(room);
        init(room);
    }

    public void init(GameRoom room){
        this.room = room;
        String[] type = getEventItem().value.toString().split(":");
        for(EventRunType runType: EventRunType.values()){
            if(runType.name().toLowerCase().equalsIgnoreCase(type[0])){
                this.runType = runType;
                break;
            }
        }
        if(type.length > 1){
            String[] v = type[1].split(",");
            for(String obj: v){
                String[] v2 = obj.split("-");
                if(v2.length > 1){
                    int pos = Integer.parseInt(v2[0]);
                    int pos1 = Integer.parseInt(v2[1]);
                    if(pos > pos1){
                        continue;
                    }
                    for(int i = pos;i <= pos1;i++){
                        if(room.getEventControl().eventItems.size() > i){
                            IGameRoomEvent event = RoomEventManager.getEventByType(room.getEventControl().eventItems.get(i));
                            if(event != null){
                                event.onCreate(room);
                                gameRoomEvents.add(event);
                            }
                        }
                    }
                }else{
                    int i = Integer.parseInt(v2[0]);
                    if(room.getEventControl().eventItems.size() > i){
                        IGameRoomEvent event = RoomEventManager.getEventByType(room.getEventControl().eventItems.get(i));
                        if(event != null){
                            event.onCreate(room);
                            gameRoomEvents.add(event);
                        }
                    }
                }
            }
        }
    }

    public enum EventRunType{
        /**循环 随机 遍历*/
        WHILE,RANDOM,FOREACH
    }


    public CustomEvent(GameRoomEventConfig.GameRoomEventItem item) {
        super(item);

    }


    public boolean isEnable;


    @Override
    public boolean enable() {
        isEnable = true;
        return true;
    }

    @Override
    public boolean isEnable() {
        return isEnable;
    }

    @Override
    public IGameRoomEvent nextEvent(){
        IGameRoomEvent event = null;
        switch (runType){
            case WHILE:
                if(position >= gameRoomEvents.size()){
                    position = 0;
                }
                event = gameRoomEvents.get(position);
                break;
            case RANDOM:
                if(!isRun){
                    if(random == null){
                        random = gameRoomEvents.get(new Random().nextInt(gameRoomEvents.size()));
                    }
                    event = random;
                }

                break;
            case FOREACH:
                if(position < gameRoomEvents.size()){
                    event = gameRoomEvents.get(position);
                }
                break;
            default:break;
        }
        return event;

    }

    @Override
    public void doNextEvent(GameRoom room) {
        onStart(room);
        this.position++;
    }

    @Override
    public int getEventTime(){
        IGameRoomEvent event = nextEvent();
        if(event != null){
            return event.getEventItem().eventTime;
        }
        return item.eventTime;
    }

    @Override
    public void onStart(GameRoom room) {
        IGameRoomEvent event = nextEvent();
        if(runType == EventRunType.RANDOM){
            isRun = true;
        }
        if(event != null){
            event.onStart(room);
            if(event instanceof CustomEvent){
                ((CustomEvent) event).isRun = false;
                ((CustomEvent) event).random = null;
            }
        }
    }

    @Override
    public String display() {
        IGameRoomEvent event = nextEvent();
        if(event != null){
            if(event instanceof CustomEvent && ((CustomEvent) event).runType == EventRunType.RANDOM){
                return getEventItem().display+event.getEventItem().display;
            }

            return getEventItem().display+event.display();

        }
        return getEventItem().display;
    }
}
