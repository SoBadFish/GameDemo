package org.sobadfish.gamedemo.panel;

import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import org.sobadfish.gamedemo.panel.items.BasePlayPanelItemInstance;
import org.sobadfish.gamedemo.panel.lib.AbstractFakeInventory;
import org.sobadfish.gamedemo.player.PlayerInfo;


import java.util.Map;

/**
 * 将箱子菜单展示给用户
 * @author SoBadFish
 * 2022/1/2
 */
public class DisPlayerPanel implements InventoryHolder {

    private AbstractFakeInventory inventory;


    /**
     * 将箱子菜单展示给用户
     * @param player 玩家对象 {@link PlayerInfo}
     * @param itemMap 菜单内物品摆放的位置
     * @param name 菜单名称
     *
     */
    public void displayPlayer(PlayerInfo player, Map<Integer, BasePlayPanelItemInstance> itemMap, String name){
        ChestInventoryPanel panel = new ChestInventoryPanel(player,this,name);
        panel.setPanel(itemMap);
        panel.id = ++Entity.entityCount;
        inventory = panel;
        panel.getPlayer().addWindow(panel);

    }



    @Override
    public Inventory getInventory() {
        return inventory;
    }


}
