package org.sobadfish.gamedemo.player.team.config;


import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

import java.util.Map;

/** 团队的基本数据
 * @author SoBadFish
 * 2022/1/2
 */

public class TeamConfig {

    //团队的名称
    private String name;

    //团队的颜色符号
    private String nameColor;

    //团队的代表方块物品(不给玩家也行，这个可以用作GUI)
    private Item blockWoolColor;

    //团队皮革衣服的颜色
    private BlockColor rgb;

    private TeamConfig(String name, String nameColor, Item blockWoolColor, BlockColor rgb){
        this.name = name;
        this.nameColor = nameColor;
        this.blockWoolColor = blockWoolColor;
        this.rgb = rgb;
    }

    public String getName() {
        return name;
    }

    public Item getBlockWoolColor() {
        return blockWoolColor;
    }

    public BlockColor getRgb() {
        return rgb;
    }

    public String getNameColor() {
        return nameColor;
    }

    public static TeamConfig getInstance(Map<?,?> map){
        String name = map.get("name").toString();
        String nameColor = map.get("nameColor").toString();
        Map<?,?> m = (Map<?,?>) map.get("rgb");
        int r = Integer.parseInt(m.get("r").toString());
        int g = Integer.parseInt(m.get("g").toString());
        int b = Integer.parseInt(m.get("b").toString());
        return new TeamConfig(name,nameColor, Item.fromString(map.get("blockWoolColor")
                .toString()),new BlockColor(r,g,b));
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof TeamConfig){
            return ((TeamConfig) obj).getName().equalsIgnoreCase(getName());
        }
        return false;
    }

}
