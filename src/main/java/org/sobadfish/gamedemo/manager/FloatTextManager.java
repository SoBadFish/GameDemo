package org.sobadfish.gamedemo.manager;




import org.sobadfish.gamedemo.entity.GameFloatText;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 浮空字控制类
 * 最好不要调用这里的方法
 * 如果想要增加排行榜调用
 * {@link GameFloatText}内部的showFloatText方法
 *
 * 删除排行榜调用
 * {@link GameFloatText} 内部的toClose 方法
 * */
public class FloatTextManager {

    public static List<GameFloatText> floatTextList = new CopyOnWriteArrayList<>();


    public static void addFloatText(GameFloatText floatText){
        floatTextList.add(floatText);
    }



    public static void removeFloatText(GameFloatText floatText) {
        floatTextList.remove(floatText);
    }
}
