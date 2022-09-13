package org.sobadfish.gamedemo.top;


import org.sobadfish.gamedemo.entity.GameFloatText;

import java.util.Objects;

public class TopItemInfo {

    public GameFloatText floatText;

    public TopItem topItem;


    public TopItemInfo(TopItem topItem, GameFloatText floatText){
        this.floatText = floatText;
        this.topItem = topItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TopItemInfo that = (TopItemInfo) o;
        return Objects.equals(topItem, that.topItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topItem);
    }
}
