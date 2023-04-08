package org.sobadfish.gamedemo.room.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Sobadfish
 * @date 2023/4/8
 */
public class DeathBodyConfig {

    public boolean enable = false;

    public String skin = "";

    public Map<String, Object> saveConfig(){
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("enable", enable);
        config.put("skin", skin);
        return config;

    }
}
