package org.sobadfish.gamedemo.panel;

import cn.nukkit.Player;
import cn.nukkit.utils.Utils;
import org.sobadfish.gamedemo.panel.from.GameFrom;
import org.sobadfish.gamedemo.panel.from.button.BaseIButtom;


import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author SoBadFish
 * 2022/1/11
 */
public class DisPlayWindowsFrom {

    public static int FROM_ID = 155;

    public static int FROM_MAX_ID = 105478;

    public static LinkedHashMap<String, GameFrom> FROM = new LinkedHashMap<>();


    public static void disPlayerCustomMenu(Player player, String tag, List<BaseIButtom> from){
        GameFrom bedWarFrom = new GameFrom(tag,"",getId());
        bedWarFrom.setBaseIButtoms(from);
        FROM.put(player.getName(), bedWarFrom);
        bedWarFrom.disPlay(player);
    }


    public static int getId(){
        return Utils.rand(FROM_ID,FROM_MAX_ID);
    }

    public static int getId(int min,int max){
        return Utils.rand(min,max);
    }

}
