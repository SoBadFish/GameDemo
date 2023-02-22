package org.sobadfish.gamedemo.manager;

import org.sobadfish.gamedemo.dlc.IGameRoomDlc;

import java.util.LinkedHashMap;

/**
 * @author Sobadfish
 * @date 2023/2/22
 */
public class GameRoomDlcManager {

    public static LinkedHashMap<String,IGameRoomDlc> DLC_CLASS = new LinkedHashMap<>();


    public static void register(String name,  IGameRoomDlc dlc){
        DLC_CLASS.put(name, dlc);
        TotalManager.sendMessageToConsole("&aLoad &7"+name);
    }


    public static IGameRoomDlc loadDlc(String name){
        if(DLC_CLASS.containsKey(name)){
            return DLC_CLASS.get(name);
        }
        return null;
    }
}
