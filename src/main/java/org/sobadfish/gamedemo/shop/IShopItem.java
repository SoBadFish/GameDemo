package org.sobadfish.gamedemo.shop;

import cn.nukkit.Player;
import org.sobadfish.gamedemo.room.GameRoom;

/**
 * @author Sobadfish
 *  2023/2/6
 */
public interface IShopItem {

    /**
     * 被玩家点击
     * @param player 点击的玩家
     * @param room 游戏房间
     * @return 是否成功触发
     * */
    boolean onClick(GameRoom room,Player player);

    /**
     * 需要重写按键名称
     * @return 按键名称
     * */
    String getButtonName();

    /**
     * 需要重写按键图标
     * @return 按键图片路径
     * */
    String getButtonImg();



}
