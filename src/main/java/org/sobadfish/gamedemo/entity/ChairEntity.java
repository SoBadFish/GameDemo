package org.sobadfish.gamedemo.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

/**
 * @author  Champrin
 */
public class ChairEntity extends Entity {
    public float length = 1.0F;

    public ChairEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.id = Entity.entityCount++;
    }


    @Override
    public String getName() {
        return "Chair";
    }

    @Override
    public void spawnTo(Player player) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.entityUniqueId = this.getId();
        pk.entityRuntimeId = this.id;
        pk.type = 64;
        pk.x = (float) this.x + 0.1F;
        pk.y = (float) this.y + 0.5F;
        pk.z = (float) this.z + 0.1F;
        pk.speedX = 0.0F;
        pk.speedY = 0.0F;
        pk.speedZ = 0.0F;
        pk.yaw = (float) this.yaw;
        pk.pitch = (float) this.pitch;

        pk.metadata = (new EntityMetadata()).putLong(0, 114688L).putString(4, "");
        player.dataPacket(pk);

        this.setDataFlag(DATA_FLAGS, DATA_FLAG_INVISIBLE, true);
        this.setImmobile(true);
    }

    @Override
    public int getNetworkId() {
        return 64;
    }

    @Override
    public void saveNBT() {

    }
}
