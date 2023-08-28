package org.sobadfish.gamedemo.room.config;

import cn.nukkit.level.Position;
import org.sobadfish.gamedemo.room.area.GameArea;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 游戏地图区域的配置
 * @author Sobadfish
 * @date 2023/8/28
 */
public class GameAreaConfig {


    public String name;

    public List<GameArea> gameAreaList;



    private GameAreaConfig(String name, List<GameArea> gameAreas) {
        this.name = name;
        this.gameAreaList = gameAreas;
    }


    public static GameAreaConfig loadByConfigMap(String name, List<?> stringObjectMap){
        List<GameArea> gameArea = new ArrayList<>();

        for(Object strMap : stringObjectMap){
            if(strMap instanceof Map){
                GameArea area = mapToGameArea((Map<?, ?>) strMap);
                if(area != null){
                    gameArea.add(area);
                }
            }


        }



        return new GameAreaConfig(name, gameArea);

    }

    public List<Map<?,?>> gameAreaToMap(){
        List<Map<?,?>> maps = new ArrayList<>();

        for(GameArea area :gameAreaList){
            maps.add(area.asConfigMap());
        }
        return maps;
    }

    private static GameArea mapToGameArea(Map<?,?> map){
        try{
            int x = Integer.parseInt(map.get("minX").toString());
            int x1 = Integer.parseInt(map.get("maxX").toString());

            int y = Integer.parseInt(map.get("minY").toString());
            int y1 = Integer.parseInt(map.get("maxY").toString());

            int z = Integer.parseInt(map.get("minZ").toString());
            int z1 = Integer.parseInt(map.get("maxZ").toString());

            String levelName = map.get("level").toString();
            return new GameArea(new Position(x,y,z),new Position(x1,y1,z1),levelName);

        }catch (Exception e){
            return null;
        }
    }
}
