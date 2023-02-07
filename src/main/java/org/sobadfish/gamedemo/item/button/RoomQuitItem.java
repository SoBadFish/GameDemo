package org.sobadfish.gamedemo.item.button;

import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.gamedemo.item.ICustomItem;
import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.player.PlayerInfo;
import org.sobadfish.gamedemo.room.GameRoom;

/**
 * 玩家离开房间的物品
 * @author SoBadFish
 * 2022/1/3
 */
public class RoomQuitItem implements ICustomItem {


    @Override
    public void onClick(PlayerInfo player) {
        GameRoom room = player.getGameRoom();
        if(room != null) {
            if (room.quitPlayerInfo(player, true)) {
                player.sendMessage(TotalManager.getLanguage().getLanguage("player-quit-room-success", "你成功离开房间 [1]", room.roomConfig.getName()));
            }
        }
    }

    @Override
    public boolean canBeUse() {
        return false;
    }

    @Override
    public Item getItem() {
        return Item.get(324);
    }

    @Override
    public String getName() {
        return "quitRoom";
    }

    @Override
    public String getCustomName() {
        return TextFormat.colorize('&',TotalManager.getLanguage().getLanguage("quit-room-button","&r&l&e点我退出游戏"));
    }
}
