package org.sobadfish.gamedemo.thread;


import org.sobadfish.gamedemo.manager.ThreadManager;
import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.room.GameRoom;
import org.sobadfish.gamedemo.top.TopItemInfo;

public class TopRunnable extends ThreadManager.AbstractGameRunnable {

    @Override
    public GameRoom getRoom() {
        return null;
    }

    public long time = 0;

    @Override
    public String getThreadName() {
        String color = "&a";
        if(isClose){
            color = "&7";
        }
        return color+"排行榜更新 &7("+ TotalManager.getTopManager().topItemInfos.size()+") &a"+time+" ms";
    }

    @Override
    public void run() {
        try {
            long t1 = System.currentTimeMillis();
            if (isClose) {
                ThreadManager.cancel(this);
            }

            if (TotalManager.isDisabled()) {
                isClose = true;
                return;
            }
            if(TotalManager.getTopManager() == null){
                return;
            }
            for (TopItemInfo topItem : TotalManager.getTopManager().topItemInfos) {
                if (!TotalManager.getTopManager().dataList.contains(topItem.topItem)) {
                    topItem.floatText.toClose();
                    TotalManager.getTopManager().topItemInfos.remove(topItem);
                    continue;
                }
                if (topItem.floatText != null) {
                    if (topItem.floatText.player == null) {
                        continue;
                    }
                    topItem.floatText.setText(topItem.topItem.getListText());
                } else {
                    TotalManager.getTopManager().topItemInfos.remove(topItem);
                }
            }
            time = System.currentTimeMillis() - t1;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
