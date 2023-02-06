package org.sobadfish.gamedemo.shop;

import org.sobadfish.gamedemo.shop.items.ShopButton;
import org.sobadfish.gamedemo.shop.items.ShopItem;

import java.util.Map;

/**
 * @author Sobadfish
 * @date 2023/2/6
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
