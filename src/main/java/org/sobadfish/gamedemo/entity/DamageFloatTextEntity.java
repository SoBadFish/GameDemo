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

    private int tick = 12;

    public DamageFloatTextEntity(String damage, FullChunk fullChunk, CompoundTag compoundTag) {
        super(fullChunk, compoundTag);
        this.setNameTagAlwaysVisible(true);
        this.setNameTagVisible(true);
        this.setNameTag(damage);


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
        return 1.2f;
    }

    @Override
    public boolean onUpdate(int i) {

        if(tick > 0){
            tick -= (i - lastUpdate);
        }else{
            this.close();
            return true;
        }

        if (this.isAlive()) {
            if (!this.isCollided) {
                this.motionY -= 0.03;
            }else{
                this.close();
                return true;
            }
            this.move(this.motionX, this.motionY, this.motionZ);

            this.updateMovement();
        }

        return super.onUpdate(i);
    }
}
