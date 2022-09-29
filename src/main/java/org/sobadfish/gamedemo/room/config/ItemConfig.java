package org.sobadfish.gamedemo.room.config;

import cn.nukkit.item.Item;

import java.util.List;

/**
 * @author Sobadfish
 * @date 2022/9/23
 */
public class ItemConfig {

    public String block;

    public String name;

    public List<Item> items;

    public ItemConfig(String block, String name, List<Item> items){
        this.block = block;
        this.name = name;
        this.items = items;
    }


}
