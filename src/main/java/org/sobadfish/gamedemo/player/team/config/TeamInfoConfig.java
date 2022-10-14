package org.sobadfish.gamedemo.player.team.config;

import cn.nukkit.utils.BlockColor;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * @author SoBadFish
 * 2022/1/3
 */
@Data
public class TeamInfoConfig {

    //队伍的配置
    private TeamConfig teamConfig;

    /**
     * 出生坐标
     * */
    private List<String> spawnPosition;



    public TeamInfoConfig(TeamConfig teamConfig, List<String> spawnPosition){
        this.teamConfig = teamConfig;
        this.spawnPosition = spawnPosition;

    }


    public static TeamInfoConfig getInfoByMap(TeamConfig teamConfig, Map<?,?> map){

        List<?> l = (List<?>)map.get("position");
        List<String> positions = new ArrayList<>();
        for(Object ob: l){
            positions.add(ob.toString());
        }
        return new TeamInfoConfig(teamConfig,positions);
    }


    public String getName(){
        return teamConfig.getName();
    }

    public String getNameColor(){
        return teamConfig.getNameColor();
    }

    public BlockColor getRgb(){
        return teamConfig.getRgb();
    }

    public LinkedHashMap<String, Object> save(){
        LinkedHashMap<String, Object> config = new LinkedHashMap<>();
        config.put("name",teamConfig.getName());
        config.put("position",spawnPosition);
        return config;
    }
}
