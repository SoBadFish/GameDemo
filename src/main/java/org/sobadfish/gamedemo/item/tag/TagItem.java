package org.sobadfish.gamedemo.item.tag;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author Sobadfish
 * 21:47
 */
public class TagItem {

    public String name;

    public String tag = "";

    /**
     * 用于载入配置
     * */
    public TagItem(){}

    /**
     * 用于比较
     * */
    private TagItem(String name){
        this.name = name;
    }

    public TagItem(String name, Item item){
        this.name = name;
        this.tag = itemToStr(item);
    }

    public static TagItem asNameTag(String name){
        return new TagItem(name);
    }

    public Item asItem(){
        if(tag != null && !"".equalsIgnoreCase(tag)){
            try {
                CompoundTag compoundTag = NBTIO.read(tag.getBytes(StandardCharsets.UTF_8));
                return NBTIO.getItemHelper(compoundTag);
            } catch (IOException ignore) {

            }
        }
        return Item.get(0);
    }

    /**
     * 将Item转为String
     * */
    private String itemToStr(Item item){
        if(item != null && item.getId() != 0){
            if(item.hasCompoundTag()) {
                try {
                    return new String(NBTIO.write(NBTIO.putItemHelper(item)), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    return "";
                }
            }
        }
        if(item != null) {
            return item.getId() + ":" + item.getDamage() + ":" + item.getCount();
        }
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if(o instanceof TagItem){
            TagItem that = (TagItem) o;
            return name.equalsIgnoreCase(that.name);
        }

        return false;
    }

}
