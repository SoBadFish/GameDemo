package org.sobadfish.gamedemo.room.config;

import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.player.PlayerInfo;

/**
 * @author Sobadfish
 * @date 2023/2/6
 */
public class MoneyConfig {

    public String moneyUnit = "$";

    public double defaultValue = 1000.0;

    public void add(PlayerInfo playerInfo, double value) {
        playerInfo.money += value;
        TotalManager.getLanguage().getLanguage("player-money-add",
                "&e&l+ [1][2]",
                moneyUnit, String.format("%.1f", value));
    }

    public boolean reduce(PlayerInfo playerInfo, double value){
        if(playerInfo.money > value) {
            playerInfo.money -= value;
            TotalManager.getLanguage().getLanguage("player-money-reduce",
                    "&c&l- [1][2]",
                    moneyUnit, String.format("%.1f", value));
            return true;
        }else{
            TotalManager.getLanguage().getLanguage("player-money-lack",
                    "&c您的金钱不足");
        }
        return false;
    }


}
