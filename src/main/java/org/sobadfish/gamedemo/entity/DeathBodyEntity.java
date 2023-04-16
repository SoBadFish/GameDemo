package org.sobadfish.gamedemo.entity;

import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.SerializedImage;
import org.sobadfish.gamedemo.manager.SkinManager;
import org.sobadfish.gamedemo.player.PlayerInfo;
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

    public PlayerInfo bindPlayer;

    public boolean isDeath;


    public static DeathBodyEntity spawnBody(PlayerInfo playerInfo,Position location, String skinName,boolean isDeath){
        Skin skin = null;
        boolean isFly = true;
        if(skinName != null && !"".equalsIgnoreCase(skinName)){
           skin = SkinManager.getSkinByName(skinName);
           isFly = false;
        }
        if(skin == null){
            if(playerInfo.player.getSkin() != null){
                skin = playerInfo.player.getSkin();
            }else {

                skin = Utils.getDefaultSkin();
            }
        }
        try{
            //NK核心奇怪的问题 从生物上获取到的皮肤没法用作生物上
            SerializedImage img = SerializedImage.fromLegacy(skin.getSkinData().data);
        }catch (Exception e){
            skin = Utils.getDefaultSkin();
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
        if(!isDeath){
            deathBodyEntity.setNameTag(playerInfo.player.getNameTag());
            deathBodyEntity.setNameTagVisible(true);
            deathBodyEntity.setNameTagAlwaysVisible(true);
        }
        deathBodyEntity.isDeath = isDeath;
        deathBodyEntity.bindPlayer = playerInfo;
        deathBodyEntity.spawnToAll();
        return deathBodyEntity;

    }
}
