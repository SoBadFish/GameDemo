package org.sobadfish.gamedemo.entity;

import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.sobadfish.gamedemo.manager.SkinManager;
import org.sobadfish.gamedemo.tools.Utils;

/**
 * @author Sobadfish
 * @date 2023/4/8
 */
public class DeathBodyEntity extends EntityHuman {

    public DeathBodyEntity(FullChunk fullChunk, CompoundTag compoundTag) {
        super(fullChunk, compoundTag);
        setHealth(20);
        setMaxHealth(20);
    }


    public static DeathBodyEntity spawnBody(EntityHuman entityHuman,Position location, String skinName){
        Skin skin = null;
        boolean isFly = true;
        if(skinName != null && !"".equalsIgnoreCase(skinName)){
           skin = SkinManager.getSkinByName(skinName);
           isFly = false;
        }
        if(skin == null){
            if(entityHuman.getSkin() != null){
                skin = entityHuman.getSkin();
            }else {
                skin = Utils.getDefaultSkin();
            }
        }
        CompoundTag tag = EntityHuman.getDefaultNBT(location);
        tag.putCompound("Skin",new CompoundTag()
                .putByteArray("Data", skin.getSkinData().data)
                .putString("ModelId",skin.getSkinId())
        );
        DeathBodyEntity deathBodyEntity = new DeathBodyEntity(location.getChunk(),tag);
        deathBodyEntity.setSkin(skin);
        if(isFly){
            deathBodyEntity.setGliding(true);
            deathBodyEntity.setScale(-1f);
        }
        deathBodyEntity.spawnToAll();
        return deathBodyEntity;

    }
}
