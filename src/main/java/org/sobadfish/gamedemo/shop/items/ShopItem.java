package org.sobadfish.gamedemo.shop.items;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.player.PlayerInfo;
import org.sobadfish.gamedemo.room.GameRoom;
import org.sobadfish.gamedemo.shop.BaseShopButton;
import org.sobadfish.gamedemo.shop.ButtonInfo;
import org.sobadfish.gamedemo.shop.ShopButtonType;

import java.util.Map;

/**
 * @author Sobadfish
 * @date 2023/2/6
 */
public class ShopItem extends BaseShopButton {

    public Item item;

    public ShopItem(ButtonInfo info) {
        super(ShopButtonType.ITEM, info);
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public boolean onClick(GameRoom room, Player player){
        PlayerInfo playerInfo = room.getPlayerInfo(player);
        if(playerInfo != null){
            if(!room.roomConfig.moneyConfig.reduce(playerInfo,info.price)){
                return false;
            }
            player.getInventory().addItem(getItem());
            return true;

        }
        return false;

    }



    public static ShopItem build(Map<?,?> config){
        String itemStr = "";
        if(config.containsKey("item")){
            itemStr = config.get("item").toString();
        }
        if("".equalsIgnoreCase(itemStr)){
            return null;
        }
        String[] items = itemStr.split(":");
        Item item;
        try{
            int id = Integer.parseInt(items[0]);
            int damage = 0;
            int count = 1;
            if(items.length > 1){
                try {
                    damage = Integer.parseInt(items[1]);
                }catch (Exception ignore){}
                if(items.length > 2){
                    try {
                        count = Integer.parseInt(items[2]);
                    }catch (Exception ignore){}
                    if(count < 0){
                        count = 1;
                    }
                }
            }
            item = Item.get(id,damage,count);
        }catch (Exception e){
            Item i = Item.fromString(items[0]);
            if(i.getId() == 0){
                if(TotalManager.getTagItemDataManager().hasItem(items[0])){
                    i = TotalManager.getTagItemDataManager().getItem(items[0]);
                }
            }
            if(i.getId() == 0){
                return null;
            }
            int count = 1;
            if(items.length > 1){
                try {
                    count = Integer.parseInt(items[1]);
                }catch (Exception ignore){}
                if(count <= 0){
                    count = 1;
                }
            }
            i.setCount(count);
            item = i;
        }
        if(item.getId() == 0){
            return null;
        }
        ShopItem shopItem = new ShopItem(ButtonInfo.asInfo(config));
        shopItem.setItem(item);
        return shopItem;

    }

}
