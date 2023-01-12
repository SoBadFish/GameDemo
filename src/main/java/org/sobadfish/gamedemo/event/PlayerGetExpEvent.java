package org.sobadfish.gamedemo.event;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

/**
 * 玩家获取经验的事件
 * */
public class PlayerGetExpEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlers() {
        return HANDLERS;
    }

    private final String playerName;

    private int exp;

    private final int newExp;

    private String cause;

    public PlayerGetExpEvent(String playerName, int exp, int newExp, String cause){
        this.playerName = playerName;
        this.exp = exp;
        this.newExp = newExp;
        this.cause = cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public String getCause() {
        return cause;
    }

    public int getExp() {
        return exp;
    }

    public int getNewExp() {
        return newExp;
    }

    public String getPlayerName() {
        return playerName;
    }



}
