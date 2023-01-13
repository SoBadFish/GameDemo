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

    @Override
    public void saveNBT() {}
}
