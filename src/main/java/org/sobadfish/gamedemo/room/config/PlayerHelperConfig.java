package org.sobadfish.gamedemo.room.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Sobadfish
 * @date 2023/4/6
 */
public class PlayerHelperConfig {

    /**
     * 是否启用
     * */
    public boolean enable = true;

    /**
     * 一段时间未被救起时的死亡时间
     * */
    public int finalDeathTime = 60;

    /**
     * 救起时间
     * */
    public int helperTime = 10;

    /**
     * 救起后的血量
     * */
    public int respawnHealth = 10;

    /**
     * 倒地的血量值
     * */
    public int collapseHealth = 10;

    /**
     * 是否允许补刀
     * */
    public boolean canLast = true;


    public Map<String, Object> saveConfig(){
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("enable",enable);
        config.put("final-death-time",finalDeathTime);
        config.put("help-time",helperTime);
        config.put("respawn-health",respawnHealth);
        config.put("collapse-health",collapseHealth);
        config.put("can-last",canLast);
        return config;

    }





}
