package org.sobadfish.gamedemo.room.event;


import org.sobadfish.gamedemo.manager.RoomEventManager;
import org.sobadfish.gamedemo.room.GameRoom;
import org.sobadfish.gamedemo.room.config.GameRoomEventConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 事件控制器
 * @author Sobadfish
 * */
public class EventControl {

    public GameRoomEventConfig eventConfig;

    public int loadTime = 0;

    public int position = 0;

    private final GameRoom room;

    public EventStatus status;


    /**
     * 备选事件
     * */
    public List<GameRoomEventConfig.GameRoomEventItem> eventItems = new ArrayList<>();



    private final List<IGameRoomEvent> events = new ArrayList<>();

    public EventControl(GameRoom room, GameRoomEventConfig eventConfig){
        this.eventConfig = eventConfig;
        this.room = room;
        status = new EventStatus();
        for(GameRoomEventConfig.GameRoomEventItem item : eventConfig.getItems()){
            IGameRoomEvent event = RoomEventManager.getEventByType(item);
            if(event != null){
                events.add(event);
            }

        }
        eventItems.addAll(room.getRoomConfig().eventListConfig.getItems());

    }

    public void initAll(GameRoom room){
        for(IGameRoomEvent event: events){
            event.onCreate(room);
        }
    }

    public boolean enable;

    public void run(){
        if(enable){
            loadTime++;
            if(position < events.size()){
                IGameRoomEvent event = events.get(position);
                if(event instanceof IEventProcess){

                    if(((IEventProcess) event).isEnable()){
                        IGameRoomEvent event1 = ((IEventProcess) event).nextEvent();
                        if(event1 == null ){
                            loadTime = 0;
                            status.lastEvent = event;
                            position++;
                            status.thisEvent = null;
                        }else{
                            if(loadTime >= event.getEventTime()){
                                loadTime = 0;
                                ((IEventProcess) event).doNextEvent(room);
                                status.thisEvent = null;
                                status.successCount++;
                            }
                        }
                    }else{
                        if(loadTime >= event.getEventTime()) {
                            loadTime = 0;
                            if(((IEventProcess) event).enable()){
                                ((IEventProcess) event).doNextEvent(room);
                                status.successCount++;
                            }else{
                                //事件没有成功启动，跳过执行下一个事件
                                position++;
                                status.defeatCount++;
                            }
                            status.thisEvent = null;
                        }
                    }
                }else{
                    if(loadTime >= event.getEventTime()){
                        loadTime = 0;
                        status.lastEvent = event;
                        position++;
                        status.thisEvent = null;
                        event.onStart(room);
                        status.successCount++;
                    }

                }

            }else{
                loadTime = 0;
            }
            if(status.thisEvent != null && status.thisEvent instanceof IEventDurationTime){
                ((IEventDurationTime) status.thisEvent).update();
                if(((IEventDurationTime) status.thisEvent).isOutTime()){
                    status.thisEvent = null;
                }
            }
            for(IGameRoomEvent event: new ArrayList<>(status.residentEvent)){
                if(event instanceof IEventDurationTime){
                    ((IEventDurationTime) event).update();
                    if(((IEventDurationTime) event).isOutTime()){
                        status.residentEvent.remove(event);
                    }
                }
            }
        }
    }

    /**
     * 配置 roomEventList 内部事件
     * */
    public List<GameRoomEventConfig.GameRoomEventItem> getEventConfigList() {
        return eventItems;
    }



    public IGameRoomEvent getNextEvent() {
        if(hasEvent()){
            return events.get(position);
        }
        return null;

    }

    public boolean hasEvent(){
        if(room.getType() == GameRoom.GameType.START){
            return position < events.size();
        }
        return false;

    }

    public GameRoomEventConfig.GameRoomEventItem getEventConfig() {
        if(position < events.size()) {
            return  events.get(position).item;
        }
        return null;
    }

    public void addResidentEvent(IGameRoomEvent event){
        status.residentEvent.add(event);
    }

    public void removeResidentEvent(IGameRoomEvent event){
        status.residentEvent.remove(event);
    }

    public boolean hasResidentEvent(String name){
        for(IGameRoomEvent event: status.residentEvent){
            if(event.getEventItem().eventType.equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }

    /**
     * 事件控制器状态
     * */
    private static class EventStatus{
        /**
         * 成功执行的事件数量
         * */
        public int successCount;
        /**
         * 成功失败的事件数量
         * */
        public int defeatCount;
        /**
         * 上次执行的事件
         * */
        private IGameRoomEvent lastEvent;

        /**
         * 当前正在倒计时的事件
         * */
        private IGameRoomEvent thisEvent;

        /**
         * 长时间保持的事件
         * */
        private List<IGameRoomEvent> residentEvent = new ArrayList<>();

        public void setLastEvent(IGameRoomEvent lastEvent) {
            this.lastEvent = lastEvent;
        }

        public void setDefeatCount(int defeatCount) {
            this.defeatCount = defeatCount;
        }

        public void setSuccessCount(int successCount) {
            this.successCount = successCount;
        }

        public void setThisEvent(IGameRoomEvent thisEvent) {
            this.thisEvent = thisEvent;
        }

        public IGameRoomEvent getThisEvent() {
            return thisEvent;
        }

        public int getDefeatCount() {
            return defeatCount;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public IGameRoomEvent getLastEvent() {
            return lastEvent;
        }

        public List<IGameRoomEvent> getResidentEvent() {
            return residentEvent;
        }

        public void setResidentEvent(List<IGameRoomEvent> residentEvent) {
            this.residentEvent = residentEvent;
        }
    }
}
