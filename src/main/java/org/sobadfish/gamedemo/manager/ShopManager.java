package org.sobadfish.gamedemo.manager;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.utils.Config;
import org.sobadfish.gamedemo.panel.DisPlayWindowsFrom;
import org.sobadfish.gamedemo.panel.from.GameFrom;
import org.sobadfish.gamedemo.panel.from.button.BaseIButton;
import org.sobadfish.gamedemo.player.PlayerInfo;
import org.sobadfish.gamedemo.room.GameRoom;
import org.sobadfish.gamedemo.shop.BaseShopButton;
import org.sobadfish.gamedemo.shop.IShopItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Sobadfish
 * 2023/2/6
 */
public class ShopManager {


    public String content;

    public String title;

    public List<IShopItem> shopItems;

    private ShopManager(String title,
                        String content,
                        List<IShopItem> shopItems) {

        this.title = title;
        this.content = content;
        this.shopItems = shopItems;
    }


    public static ShopManager init(Config config){
        String title = config.getString("shop-title","良心商店");
        String content = config.getString("shop-content","");
        List<IShopItem> shopItems = new ArrayList<>();
        TotalManager.sendMessageToConsole("&e&l正在加载商店物品..");
        if(config.exists("shop-menu")){
            List<Map> list = config.getMapList("shop-menu");
            for(Map<?,?> map:list){
                BaseShopButton shopButton = BaseShopButton.build(map);
                if(shopButton != null){
                    shopItems.add(shopButton);
                }

            }
        }
        TotalManager.sendMessageToConsole("&l&a成功加载 "+shopItems.size()+" 件商品");
        return new ShopManager(title,content,shopItems);
    }

    public static void addGameFromButton(GameRoom room,GameFrom gf,List<? extends IShopItem> shopItems){
        for (IShopItem shopItem : shopItems) {
            gf.add(new BaseIButton(new ElementButton(
                    shopItem.getButtonName(),
                    new ElementButtonImageData("path", shopItem.getButtonImg())
            )) {
                @Override
                public void onClick(Player player) {
                    shopItem.onClick(room, player);
                }
            });
        }

    }

    public void toDisPlay(GameRoom room,Player player){
        PlayerInfo playerInfo = room.getPlayerInfo(player);
        if(playerInfo != null) {
            GameFrom gf = new GameFrom(title, content.replace("%money%",String.format("%.1f",playerInfo.money)
                    ));
            addGameFromButton(room,gf,shopItems);
            DisPlayWindowsFrom.disPlayFrom(player, gf);
        }
    }
}
