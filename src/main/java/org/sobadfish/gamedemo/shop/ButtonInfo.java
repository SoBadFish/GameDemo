package org.sobadfish.gamedemo.shop;

import cn.nukkit.utils.TextFormat;

import java.util.Map;

/**
 * @author Sobadfish
 *2023/2/6
 */
public class ButtonInfo {

    public double price;

    public String displayName;

    public String buttonImg;

    public ButtonInfo(double price,
                      String displayName,
                      String buttonImg){
        this.price = price;
        this.displayName = displayName;
        this.buttonImg = buttonImg;
    }

    public static ButtonInfo asInfo(Map<?,?> config){
        double price = 0;
        if(config.containsKey("money")){
            price = Double.parseDouble(config.get("money").toString());
        }
        String display = "";
        if(config.containsKey("display")){
            display = TextFormat.colorize('&',config.get("display").toString());
        }
        String buttonImg = "";
        if(config.containsKey("buttonImg")){
            buttonImg = config.get("buttonImg").toString();
        }
        return new ButtonInfo(price,display,buttonImg);
    }
}
