package org.sobadfish.gamedemo.panel.from.button;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;

/**
 * GUI 按键
 * @author SoBadFish
 * 2022/1/12
 */
public abstract class BaseIButtom {

    private final ElementButton button;

    public BaseIButtom(ElementButton button) {
        this.button = button;
    }

    public ElementButton getButton() {
        return button;
    }

    /**
     * 按键被点击
     * @param player 玩家
     * */
    abstract public void onClick(Player player);

    @Override
    public String toString() {
        return button.getText()+" - img["+button.getImage().getType()+">"+button.getImage().getData()+"]";
    }
}
