package org.sobadfish.gamedemo.item.button;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemEndCrystal;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.gamedemo.manager.TotalManager;

/**
 * @author Sobadfish
 * @date 2023/2/6
 */
public class OpenShopItem {
//    ItemEndCrystal

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
        Item item = new ItemEndCrystal();
        item.addEnchantment(Enchantment.get(9));
        CompoundTag tag = item.getNamedTag();
        tag.putString(TotalManager.GAME_NAME,"openShop");
        item.setNamedTag(tag);
        item.setCustomName(TextFormat.colorize('&',TotalManager.getLanguage().getLanguage("open-shop-button",
                "&r&l&e点我打开商店")));
        return item;

    }
}
