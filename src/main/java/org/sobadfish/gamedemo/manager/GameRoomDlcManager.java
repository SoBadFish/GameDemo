package org.sobadfish.gamedemo.manager;

import org.sobadfish.gamedemo.dlc.IGameRoomDlc;

import java.util.LinkedHashMap;

/**
 * @author Sobadfish
 * 2023/2/22
 */
public class GameRoomDlcManager {

    public static LinkedHashMap<String,Class<? extends IGameRoomDlc>> DLC_CLASS = new LinkedHashMap<>();


    /**
     * 注册DLC 但是这个得弃用...
     * @param name DLC名称
     * @param dlc dlc
     * */
    @Deprecated
    public static void register(String name,  IGameRoomDlc dlc){
        register(name,dlc.getClass());
    }

    /**
     * 注册DLC..
     * @param name DLC名称
     * @param dlc dlc
     * */
    public static void register(String name,  Class<? extends IGameRoomDlc> dlc){
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
