package org.sobadfish.gamedemo.item.button;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.gamedemo.manager.TotalManager;

import java.util.ArrayList;

/**
 * 玩家离开房间的物品
 * @author SoBadFish
 * 2022/1/3
 */
public class RoomQuitItem {


    public static ArrayList<Player> clickAgain = new ArrayList<>();



    /**
     * 在物品栏的位置
     * @return 位置
     * */
    public static int getIndex(){
        return 8;
    }

    /**
     * 显示给玩家的物品，可以自定义修改
     * @return 在物品栏的物品
     * */
    public static Item get(){
        Item item = Item.get(324);
        item.addEnchantment(Enchantment.get(9));
        CompoundTag tag = item.getNamedTag();
        tag.putString(TotalManager.GAME_NAME,"quitItem");
        item.setNamedTag(tag);
        item.setCustomName(TextFormat.colorize('&',TotalManager.getLanguage().getLanguage("quit-room-button","&r&l&e点我退出游戏")));
        return item;

    }
}
