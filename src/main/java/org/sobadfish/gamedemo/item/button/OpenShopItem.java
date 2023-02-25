package org.sobadfish.gamedemo.item.button;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemEndCrystal;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.gamedemo.item.ICustomItem;
import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.player.PlayerInfo;
import org.sobadfish.gamedemo.room.GameRoom;

/**
 * @author Sobadfish
 *  2023/2/6
 */
public class OpenShopItem implements ICustomItem {
//    ItemEndCrystal


    @Override
    public void onClick(PlayerInfo player) {
        GameRoom room = player.getGameRoom();
        if(room != null){
            if(player.getPlayer() instanceof Player) {
                room.roomConfig.shopManager.toDisPlay(room,(Player) player.player);
            }
        }

    }

    @Override
    public boolean canBeUse() {
        return false;
    }

    @Override
    public Item getItem() {
        return new ItemEndCrystal();
    }

    @Override
    public String getName() {
        return "openShop";
    }

    @Override
    public String getCustomName() {
        return TextFormat.colorize('&',TotalManager.getLanguage().getLanguage("open-shop-button",
                "&r&l&e点我打开商店"));
    }
}
