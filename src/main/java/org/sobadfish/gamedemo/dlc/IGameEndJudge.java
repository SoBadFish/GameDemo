package org.sobadfish.gamedemo.dlc;

import org.sobadfish.gamedemo.room.GameRoom;

/**
 * @author Sobadfish
 * 2023/2/22
 */
public interface IGameEndJudge {


    /**
     * 代替默认的结束判断条件
     * @param room 游戏房间
     * @return 是否结束游戏
     * */
    boolean judgeGameEnd(GameRoom room);
}
