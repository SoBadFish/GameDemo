package org.sobadfish.gamedemo.entity;

import cn.nukkit.entity.EntityHuman;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Sobadfish
 * @date 2023/1/13
 */
public class RobotEntity extends EntityHuman {
    public RobotEntity(FullChunk fullChunk, CompoundTag compoundTag) {
        super(fullChunk, compoundTag);
    }

    /**
     * 增加点移动逻辑
     * */
    @Override
    public boolean onUpdate(int currentTick) {
        super.onUpdate(currentTick);
        int tickDiff = currentTick - this.lastUpdate;
        boolean hasUpdate = this.entityBaseTick(tickDiff);
        if (tickDiff <= 0) {
            return false;
        } else {
            this.lastUpdate = currentTick;

            if (!this.isImmobile()) {
                this.updateMovement();
            }
        }

        return hasUpdate;
    }

    @Override
    public void addMotion(double v, double v1, double v2) {
        super.addMotion(v, v1, v2);
        this.motionX = v;
    }

    @Override
    public void saveNBT() {}
}
