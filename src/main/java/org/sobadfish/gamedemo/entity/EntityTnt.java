package org.sobadfish.gamedemo.entity;

import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.sobadfish.gamedemo.manager.FunctionManager;

/**
 * @author Sobadfish
 * 14:11
 */
public class EntityTnt extends EntityPrimedTNT {

    private int tick = 0;

    public EntityTnt(FullChunk fullChunk, CompoundTag compoundTag) {
        super(fullChunk, compoundTag);
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        resetNameTag();
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    @Override
    public boolean onUpdate(int i) {
        int var2 = i - this.lastUpdate;
        if(tick > 0) {
            tick -= var2;
        }else{
            tick = 0;
        }
        resetNameTag();
        return super.onUpdate(i);
    }

    private void resetNameTag(){
        int time = 0;
        if(tick > 0){
            time = tick / 20;
        }
        setNameTag(FunctionManager.formatTime(time));


    }
}
