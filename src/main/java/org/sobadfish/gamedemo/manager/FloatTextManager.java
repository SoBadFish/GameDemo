package org.sobadfish.gamedemo.manager;




import org.sobadfish.gamedemo.entity.GameFloatText;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FloatTextManager {

    public static List<GameFloatText> floatTextList = new CopyOnWriteArrayList<>();


    public static void addFloatText(GameFloatText floatText){
        floatTextList.add(floatText);
    }



    public static void removeFloatText(GameFloatText floatText) {
        floatTextList.remove(floatText);
    }
}
