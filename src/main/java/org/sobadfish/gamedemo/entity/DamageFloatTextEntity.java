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

    private int tick = 20 * 2;

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
        super.onUpdate(i);
        if(tick <= 0){
            this.close();
        }else{
            tick -= i - lastUpdate;
        }
        boolean hasUpdate = this.entityBaseTick(i);
        if (this.isAlive()) {

            if (!this.isCollided) {
                this.motionY -= 0.03;
            }

            this.move(this.motionX, this.motionY, this.motionZ);

            if (!this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001) {
                double f = Math.sqrt((this.motionX * this.motionX) + (this.motionZ * this.motionZ));
                this.yaw = (Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI);
                this.pitch = (Math.atan2(this.motionY, f) * 180 / Math.PI);
                hasUpdate = true;
            }

            this.updateMovement();
        }

        return hasUpdate;
    }
}
