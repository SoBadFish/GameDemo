package org.sobadfish.gamedemo.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Sobadfish
 * 12:57
 */
public class DamageFloatTextEntity extends Entity {

    private int tick;

    public DamageFloatTextEntity(String damage, FullChunk fullChunk, CompoundTag compoundTag) {
        super(fullChunk, compoundTag);
        this.setNameTagAlwaysVisible(true);
        this.setNameTagVisible(true);
        this.setNameTag(damage);
        addMotion(0.1,0.2,0.1);
        tick = 20 * 3;
    }

    @Override
    public int getNetworkId() {
        return 64;
    }

    @Override
    public boolean attack(EntityDamageEvent entityDamageEvent) {
        return false;
    }

    @Override
    protected float getGravity() {
        return 0.2f;
    }

    @Override
    public boolean onUpdate(int i) {
        if(tick <= 0){
            this.close();
        }else{
            tick -= i - lastUpdate;
        }
        return super.onUpdate(i);
    }
}
