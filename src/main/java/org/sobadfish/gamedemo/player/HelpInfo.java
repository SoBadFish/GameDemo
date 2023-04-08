package org.sobadfish.gamedemo.player;

/**
 * @author Sobadfish
 * @date 2023/4/7
 */
public class HelpInfo {
    //拉起的信息类
    public PlayerInfo helpPlayer = null;

    public int loadTime = 0;


    public void clear(PlayerInfo playerInfo){
        playerInfo.sendTitle("",1);
        playerInfo.sendSubTitle("");
        helpPlayer.sendTitle("",1);
        helpPlayer.sendSubTitle("");
        helpPlayer = null;
        loadTime = 0;
    }



}
