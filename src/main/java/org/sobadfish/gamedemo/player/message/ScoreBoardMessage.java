package org.sobadfish.gamedemo.player.message;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SoBadFish
 * 2022/1/3
 */

public class ScoreBoardMessage {

    private final String title;

    private List<String> lore;

    public ScoreBoardMessage(String title){
        this.title = title;
    }

    public ScoreBoardMessage(String title, ArrayList<String> lore){
        this.title = title;
        this.lore = lore;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }
}
