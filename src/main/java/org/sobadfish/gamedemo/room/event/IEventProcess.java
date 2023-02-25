package org.sobadfish.gamedemo.room.event;


import org.sobadfish.gamedemo.room.GameRoom;

/**
 * 通过接口解耦
 * @author Sobadfish
 *  2022/10/15
 */
public interface IEventProcess {

    /**
     * 启动事件,当事件启动时会调用这个方法
     * 当事件成功启动后才会被执行
     * @return 是否启动成功
     * */
    boolean onEnable();
    /**
     * 判断这个事件是否被成功启动
     * @return 是否启动
     * */
    boolean isEnable();
    /**
     * 下个执行的任务
     * 可以返回当前事件形成链表
     * @return 返回流程的事件
     * */
    IGameRoomEvent nextEvent();

    /**
     * 流程事件被触发
     * @param room 触发的游戏房间
     * */
    void doNextEvent(GameRoom room);
}
