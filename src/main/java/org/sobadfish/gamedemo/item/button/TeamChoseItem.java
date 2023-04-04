package org.sobadfish.gamedemo.item.button;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.gamedemo.item.ICustomItem;
import org.sobadfish.gamedemo.item.ItemIDSunName;
import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.player.PlayerInfo;
import org.sobadfish.gamedemo.player.team.TeamInfo;
import org.sobadfish.gamedemo.proxy.ItemProxy;
import org.sobadfish.gamedemo.room.GameRoom;

/**
 * 玩家选择队伍物品
 * @author SoBadFish
 * 2022/1/3
 */
public class TeamChoseItem implements ICustomItem {


    @Override
    public void onClick(PlayerInfo player) {
        if(player.getPlayer() instanceof Player) {
            GameRoom room = player.getGameRoom();
            if(room != null) {
                FormWindowSimple simple = new FormWindowSimple(TotalManager.getLanguage().getLanguage("player-chose-team", "请选择队伍"), "");
                for (TeamInfo teamInfoConfig : room.getTeamInfos()) {
                    Item wool = teamInfoConfig.getTeamConfig().getTeamConfig().getBlockWoolColor();
                    //得随时翻译回来
                    String ws = ItemProxy.asNewWool(wool.getId()+"");
                    int id,damage;
                    if(ws != null){
                        id = Integer.parseInt(ws.split(":")[0]);
                        damage = Integer.parseInt(ws.split(":")[1]);
                    }else{
                        id = wool.getId();
                        damage = wool.getDamage();
                    }

                    simple.addButton(new ElementButton(TextFormat.colorize('&', teamInfoConfig + " &r" + teamInfoConfig.getTeamPlayers().size() + " / " + (room.getRoomConfig().getMaxPlayerSize() / room.getTeamInfos().size())),
                            new ElementButtonImageData("path",
                                    ItemIDSunName.getIDByPath(id,damage))));
                }
                ((Player)player.getPlayer()).showFormWindow(simple, 102);
            }
        }
    }

    @Override
    public boolean canBeUse() {
        return false;
    }

    @Override
    public Item getItem() {
        Item i = Item.get(69);
        i.addEnchantment(Enchantment.getEnchantment(0));
        return i;
    }


    @Override
    public String getName() {
        return "teamChose";
    }

    @Override
    public String getCustomName() {
        return TextFormat.colorize('&',TotalManager.getLanguage().getLanguage("chose-team-button",
                "&r&l&e点我选择队伍"));
    }
}
