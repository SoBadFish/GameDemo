package org.sobadfish.gamedemo.shop;

import cn.nukkit.Player;
import org.sobadfish.gamedemo.player.PlayerInfo;
import org.sobadfish.gamedemo.room.GameRoom;
import org.sobadfish.gamedemo.shop.items.ShopButton;
import org.sobadfish.gamedemo.shop.items.ShopCommand;
import org.sobadfish.gamedemo.shop.items.ShopItem;

import java.util.Map;

/**
 * @author Sobadfish
 *  2023/2/6
 */
public abstract class BaseShopButton implements IShopItem{

    public ShopButtonType buttonType;

    public ButtonInfo info;

    public BaseShopButton(ShopButtonType shopButtonType,
                          ButtonInfo info){
        this.buttonType = shopButtonType;
        this.info = info;
    }

    public ButtonInfo getInfo() {
        return info;
    }

    public ShopButtonType getButtonType() {
        return buttonType;
    }

    public boolean reduce(GameRoom room, Player player){
        PlayerInfo playerInfo = room.getPlayerInfo(player);
        if(playerInfo != null) {
            if (room.roomConfig.enableMoney) {
                return room.roomConfig.moneyConfig.reduce(playerInfo, getInfo().price);
            }
        }
        return true;
    }

    public static BaseShopButton build(Map<?,?> config){
        if(config.containsKey("type")){
            ShopButtonType type;
            try{
                type = ShopButtonType.valueOf(config.get("type")
                        .toString().toUpperCase());
            }catch (Exception e){
                return null;
            }
            switch (type){
                case ITEM:
                    return ShopItem.build(config);
                case BUTTON:
                    return ShopButton.build(config);
                case COMMAND:
                    return ShopCommand.build(config);
                default:break;
            }
        }
        return null;
    }

    @Override
    public String getButtonName() {
        return getInfo().displayName;
    }

    @Override
    public String getButtonImg() {
        return getInfo().buttonImg;
    }





}
