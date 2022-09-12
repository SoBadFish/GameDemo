package org.sobadfish.gamedemo.item.button;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.gamedemo.manager.TotalManager;

import java.util.ArrayList;

/**
 * @author SoBadFish
 * 2022/1/3
 */
public class TeamChoseItem {

    public static ArrayList<Player> clickAgain = new ArrayList<>();

    public static int getIndex(){
        return 6;
    }

    public static Item get(){
        Item item = Item.get(69);
        item.setCustomName(TextFormat.colorize('&',"&r&l&e点我选择队伍"));
        CompoundTag tag = item.getNamedTag();
        tag.putString(TotalManager.GAME_NAME,"choseTeam");
        item.setNamedTag(tag);
        return item;
    }
}
