package org.sobadfish.gamedemo.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.gamedemo.manager.FloatTextManager;
import org.sobadfish.gamedemo.room.GameRoom;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 浮空字实体类
 * 测试的基本没有问题，不建议修改
 *
 * @author Sobadfish
 * */
public class GameFloatText extends Entity {

    public String name;

    public boolean isFinalClose;

    public String text = "";

    //如果不为null 就是房间内的浮空字 到时候需要移除
    public GameRoom room;

    public List<String> player = new CopyOnWriteArrayList<>();

    public GameFloatText(String name, FullChunk fullChunk, CompoundTag compoundTag) {
        super(fullChunk, compoundTag);
        this.name = name;
        this.setNameTagAlwaysVisible(true);
        this.setNameTagVisible(true);
        this.setNameTag(text);
    }

    @Override
    protected float getGravity() {
        return 0;
    }


    @Override
    public int getNetworkId() {
        return 64;
    }

    @Override
    public boolean attack(EntityDamageEvent entityDamageEvent) {
        return false;
    }


    /**
     * 设置浮空字显示内容
     * @param text 显示的文本
     * */
    public void setText(String text) {
        this.text = text;
        this.setNameTag(TextFormat.colorize('&',text));
    }

    @Override
    public void close() {
        super.close();
        if(isFinalClose){
            FloatTextManager.removeFloatText(this);

        }

    }

    public void toClose(){
        isFinalClose = true;
        close();
    }

    @Override
    public void saveNBT() {
    }

    @Override
    public boolean onUpdate(int i) {

        return super.onUpdate(i);
    }


    /**
     * 在固定的位置显示浮空字信息
     * 如果想动态更新文本信息，就将返回的浮空字实体缓存
     * 调用setText方法即可
     * @param name 浮空字的名称
     * @param position 浮空字位置
     * @param text 浮空字显示的文本信息
     *
     * @return {@link GameFloatText} 浮空字实体
     * */
    public static GameFloatText showFloatText(String name, Position position, String text){
        GameFloatText text1;
        try {
            text1 = new GameFloatText(name, position.getChunk(), Entity.getDefaultNBT(position));
        }catch (Exception e){
            return null;
        }
        text1.setText(text);
        FloatTextManager.addFloatText(text1);
        text1.toDisplay();
        return text1;
    }

    /**
     * 显示给玩家，不过这个已经在
     * {@link org.sobadfish.gamedemo.thread.PluginMasterRunnable}
     * 写好调用了，不需要再重复调用
     * */
    public void disPlayers(){
        for(String player: player){
            Player player1 = Server.getInstance().getPlayer(player);
            if(player1 == null){
                this.player.remove(player);
            }else {
                if (player1.getLevel().getFolderName().equalsIgnoreCase(getLevel().getFolderName())) {
                    if (!this.hasSpawned.containsValue(player1)) {
//                        this.despawnFrom(player1);
                        spawnTo(player1);
                    }
//                    spawnTo(player1);
                } else {
                    this.despawnFrom(player1);
                    this.player.remove(player);
                    close();

                }
            }
        }
    }

    private void toDisplay(){
        for(Player player: Server.getInstance().getOnlinePlayers().values()){
            if(!this.player.contains(player.getName())) {
                if(player.getLevel().getFolderName().equalsIgnoreCase(getLevel().getFolderName())){
                    this.player.add(player.getName());
                    spawnTo(player);
                }
            }

        }
    }
}
