package org.sobadfish.gamedemo.panel.items;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.item.Item;
import org.sobadfish.gamedemo.panel.ChestInventoryPanel;
import org.sobadfish.gamedemo.panel.from.GameFrom;
import org.sobadfish.gamedemo.player.PlayerInfo;


/**
 * @author Sobadfish
 * @date 2022/9/9
 */
public abstract class BasePlayPanelItemInstance {

    /**
     * 消费数量
     * @return 数量
     * */
    public abstract int getCount();
    /**
     * 游戏内物品
     * @return 物品
     * */
    public abstract Item getItem();
    /**
     * 当玩家触发
     *
     * @param inventory 商店
     * @param player 玩家
     *
     * */
    public abstract void onClick(ChestInventoryPanel inventory, Player player);
    /**
     * 当玩家触发GUI的button
     *
     * @param player 玩家
     * @param from 按键GUI
     *
     * */
    public abstract void onClickButton(Player player, GameFrom from);

    /**
     * 箱子菜单展示物品
     * @param index 位置
     * @param info 玩家信息
     * @return 物品
     *
     * */
    public abstract Item getPanelItem(PlayerInfo info, int index);

    /**
     * GUI按键button
     * @param info 玩家信息
     * @return 物品
     *
     * */
    public abstract ElementButton getGUIButton(PlayerInfo info);


    @Override
    public String toString() {
        return getItem()+" count: "+getCount();
    }
}
