package org.sobadfish.gamedemo.room;

import cn.nukkit.form.element.ElementButtonImageData;
import org.sobadfish.gamedemo.room.config.GameRoomConfig;

import java.util.ArrayList;

/**
 * @author SoBadFish
 * 2022/1/12
 */
public class WorldRoom {

    private final String name;

    private final ElementButtonImageData imageData;

    private final ArrayList<GameRoomConfig> roomConfigs;

    public WorldRoom(String name, ArrayList<GameRoomConfig> roomConfigs, ElementButtonImageData imageData){
        this.name = name;
        this.roomConfigs = roomConfigs;
        this.imageData = imageData;
    }

    public ElementButtonImageData getImageData() {
        return imageData;
    }

    public String getName() {
        return name;
    }

    public ArrayList<GameRoomConfig> getRoomConfigs() {
        return roomConfigs;
    }


}
