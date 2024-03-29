package org.sobadfish.gamedemo.panel.from;

import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.gamedemo.panel.DisPlayWindowsFrom;
import org.sobadfish.gamedemo.panel.from.button.BaseIButton;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI菜单
 * @author SoBadFish
 * 2022/1/12
 */
public class GameFrom {

    private final int id;


    private List<BaseIButton> baseIButtons = new ArrayList<>();

    private final String title;

    private final String context;
    public GameFrom(String title, String context, int id){
        this.title = title;
        this.context = context;
        this.id = id;
    }

    public GameFrom(String title, String context){
        this.title = title;
        this.context = context;
        this.id = DisPlayWindowsFrom.getId();
    }

    public List<BaseIButton> getBaseIButtons() {
        return baseIButtons;
    }

    public void setBaseIButtons(List<BaseIButton> baseIButtons) {
        this.baseIButtons = baseIButtons;
    }

    public int getId() {
        return id;
    }


    public void add(BaseIButton baseIButtom){
        baseIButtons.add(baseIButtom);
    }

    public void disPlay(Player player){
        FormWindowSimple simple = new FormWindowSimple(TextFormat.colorize('&',title), TextFormat.colorize('&', context));
        for(BaseIButton baseIButtom : baseIButtons){
            simple.addButton(baseIButtom.getButton());
        }
        player.showFormWindow(simple, getId());
    }

    @Override
    public String toString() {
        return id+" -> "+ baseIButtons;
    }
}
