package org.sobadfish.gamedemo.manager.data;


import org.sobadfish.gamedemo.entity.GameFloatText;
import org.sobadfish.gamedemo.manager.BaseDataWriterGetterManager;
import org.sobadfish.gamedemo.top.TopItem;
import org.sobadfish.gamedemo.top.TopItemInfo;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerTopManager extends BaseDataWriterGetterManager<TopItem> {


    public List<TopItemInfo> topItemInfos = new CopyOnWriteArrayList<>();


    public File file;

    public PlayerTopManager(List<TopItem> topItems,File file){
        super(topItems,file);
    }



    public void init(){
        for(TopItem topItem: dataList){
            GameFloatText floatText = GameFloatText.showFloatText(topItem.name,topItem.getPosition(),"");
            if(floatText == null){
                continue;
            }
            topItemInfos.add(new TopItemInfo(topItem,floatText));
        }

    }

    public boolean hasTop(String top){
        for(TopItem topItem: dataList){
            if(topItem.name.equals(top)){
                return true;
            }
        }
        return false;
    }

    public void removeTopItem(TopItem topItem){
        dataList.remove(topItem);
        TopItemInfo topItemInfo = new TopItemInfo(topItem,null);
        if(topItemInfos.contains(topItemInfo)){
            topItemInfo = topItemInfos.get(topItemInfos.indexOf(topItemInfo));
            GameFloatText floatText = topItemInfo.floatText;
            if(floatText != null){
                floatText.toClose();
            }
        }
        topItemInfos.remove(topItemInfo);
    }

    public void addTopItem(TopItem topItem){

        GameFloatText floatText = GameFloatText.showFloatText(topItem.name,topItem.getPosition(),"");
        if(floatText == null){
            return;
        }
        topItemInfos.add(new TopItemInfo(topItem,floatText));
        dataList.add(topItem);
    }

    public static PlayerTopManager asFile(File file){
        return (PlayerTopManager) BaseDataWriterGetterManager.asFile(file,"top.json",TopItem[].class,PlayerTopManager.class);
    }


    public TopItem getTop(String name) {
        for(TopItem topItem: dataList){
            if(topItem.name.equalsIgnoreCase(name)){
                return topItem;
            }
        }
        return null;
    }
}
