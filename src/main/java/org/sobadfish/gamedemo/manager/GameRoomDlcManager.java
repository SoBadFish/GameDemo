package org.sobadfish.gamedemo.manager;

import org.sobadfish.gamedemo.dlc.IGameRoomDlc;

import java.util.LinkedHashMap;

/**
 * @author Sobadfish
 * @date 2023/2/22
 */
public class GameRoomDlcManager {

    public static LinkedHashMap<String,Class<? extends IGameRoomDlc>> DLC_CLASS = new LinkedHashMap<>();


    public static void register(String name, Class<? extends IGameRoomDlc> dlc){
        DLC_CLASS.put(name, dlc);
        TotalManager.sendMessageToConsole("&aLoad &7"+name);
    }


    public static IGameRoomDlc loadDlc(String name){
        if(DLC_CLASS.containsKey(name)){
            Class<? extends IGameRoomDlc> dlc = DLC_CLASS.get(name);
            try {
                return dlc.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
