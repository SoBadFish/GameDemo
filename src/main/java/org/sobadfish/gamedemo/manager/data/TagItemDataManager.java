package org.sobadfish.gamedemo.manager.data;

import cn.nukkit.item.Item;
import org.sobadfish.gamedemo.item.tag.TagItem;
import org.sobadfish.gamedemo.manager.BaseDataWriterGetterManager;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Sobadfish
 * 21:46
 */
public class TagItemDataManager extends BaseDataWriterGetterManager<TagItem> {

    private static final LinkedHashMap<String,Item> cacheItem = new LinkedHashMap<>();

    public TagItemDataManager(List<TagItem> dataList, File file) {
        super(dataList, file);
    }


    public boolean hasItem(String name){
        return dataList.contains(TagItem.asNameTag(name));
    }

    public Item getItem(String name){
        if(cacheItem.containsKey(name)){
            return cacheItem.get(name);
        }
        Item item = null;
        if(dataList.contains(TagItem.asNameTag(name))){
            item = dataList.get(dataList.indexOf(TagItem.asNameTag(name))).asItem();
        }
        if(item != null && item.getId() > 0){
            cacheItem.put(name,item);
        }
        return item;
    }

    /**
     * 读取配置文件
     * @param file 配置文件
     * @return tag物品控制类
     * */
    public static TagItemDataManager asFile(File file){
        return (TagItemDataManager) BaseDataWriterGetterManager.asFile(file,"tag.json", TagItem[].class,TagItemDataManager.class);
    }


}
