package org.sobadfish.gamedemo.dlc;

import org.sobadfish.gamedemo.player.PlayerInfo;

import java.util.ArrayList;

/**
 * @author Sobadfish
 *  2023/2/22
 */
public interface IGamePlayerScoreBoard {


    /**
     * 玩家在等待游戏的时候计分板显示内容
     * @param info 玩家
     * @return 计分板显示的内容
     * */
    ArrayList<String> displayPlayerWaitGameScoreBoard(PlayerInfo info);


    /**
     * 玩家在游戏开始的时候计分板显示内容
     * @param info 玩家
     * @return 计分板显示的内容
     * */
    ArrayList<String> displayPlayerGameStartScoreBoard(PlayerInfo info);

}
