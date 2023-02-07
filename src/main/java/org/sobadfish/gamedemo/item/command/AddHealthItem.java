package org.sobadfish.gamedemo.item.command;

import cn.nukkit.item.Item;
import org.sobadfish.gamedemo.item.ICustomItem;
import org.sobadfish.gamedemo.item.IItemObjectValue;
import org.sobadfish.gamedemo.player.PlayerInfo;

/**
 * 增加玩家生命
 * @author Sobadfish
 * @date 2023/2/7
 */
public class AddHealthItem implements ICustomItem, IItemObjectValue {

    public int health = 1;

    @Override
    public void onClick(PlayerInfo player) {
        player.health += (int)getValue();
    }

    @Override
    public boolean canBeUse() {
        return true;
    }


    @Override
    public Item getItem() {
        return Item.get(331);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getCustomName() {
        return null;
    }

    @Override
    public void setValue(Object value) {
        this.health = (int)value;
    }

    @Override
    public Object getValue() {
        return health;
    }
}
