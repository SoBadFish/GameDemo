package org.sobadfish.gamedemo.item.button;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.gamedemo.manager.TotalManager;

import java.util.ArrayList;

/**
 * 玩家选择队伍物品
 * @author SoBadFish
 * 2022/1/3
 */
public class TeamChoseItem {

    public static ArrayList<Player> clickAgain = new ArrayList<>();

    /**
     * 在物品栏的位置
     * @return 位置
     * */
    public static int getIndex(){
        return 6;
    }
    /**
     * 显示给玩家的物品，可以自定义修改
     * @return 在物品栏的物品
     * */
    public static Item get(){
        Item item = Item.get(69);
        item.setCustomName(TextFormat.colorize('&',"&r&l&e点我选择队伍"));
        CompoundTag tag = item.getNamedTag();
        tag.putString(TotalManager.GAME_NAME,"choseTeam");
        item.setNamedTag(tag);
        return item;
    }
}
