package org.sobadfish.gamedemo.manager;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import org.sobadfish.gamedemo.item.ICustomItem;
import org.sobadfish.gamedemo.item.button.FollowItem;
import org.sobadfish.gamedemo.item.button.OpenShopItem;
import org.sobadfish.gamedemo.item.button.RoomQuitItem;
import org.sobadfish.gamedemo.item.button.TeamChoseItem;

import java.util.LinkedHashMap;

/**
 * 获取按键物品的管理类
 * 如果要赋予玩家按键物品，需要通过getItem方法
 * @author Sobadfish
 * @date 2023/2/7
 */
public class ButtonItemManager {

    public static LinkedHashMap<String,Class<? extends ICustomItem>> BUTTON_ITEM = new LinkedHashMap<>();

    /**
     * 注册按键物品
     * @param item 物品类
     * */
    public static void registerItem(Class<? extends ICustomItem> item) {
        try {
            ICustomItem customItem = item.newInstance();
            String name = customItem.getName();
            if(name == null){
                name = item.getSimpleName();
            }
            BUTTON_ITEM.put(name, item);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }


    /**
     * 根据类名获取按键物品
     * @param name 物品类名
     * */
    public static ICustomItem getButtonItem(String name) {
        if(BUTTON_ITEM.containsKey(name)){
            Class<? extends ICustomItem> baseItem = BUTTON_ITEM.get(name);
            ICustomItem item;
            try {
                item = baseItem.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
            return item;
        }
        return null;
    }


    /**
     * 根据按键物品类获取{@link Item}实体类
     * @param i 按键物品类
     * @return {@link Item}
     * */
    public static Item getItem(Class<? extends ICustomItem> i) {
        ICustomItem iButtonItem = null;
        try {
            iButtonItem = i.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if(iButtonItem != null){
            Item item = iButtonItem.getItem();
            CompoundTag tag = new CompoundTag();
            if(item.hasCompoundTag()){
                tag = item.getNamedTag();
            }


            tag.putString(TotalManager.GAME_NAME,iButtonItem.getName());
            item.setNamedTag(tag);
            if(iButtonItem.getCustomName() != null){
                item.setCustomName(iButtonItem.getCustomName());
            }
            return item;
        }
        return Item.get(0);
    }

    /**
     * 注册游戏内按键物品的初始化
     *
     * */
    public static void init() {
        registerItem(FollowItem.class);
        registerItem(OpenShopItem.class);
        registerItem(RoomQuitItem.class);
        registerItem(TeamChoseItem.class);
    }
}
