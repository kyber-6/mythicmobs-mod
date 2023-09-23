package net.pixeldream.mythicmobs.entity.client.model.entity;

import mod.azure.azurelib.model.GeoModel;
import net.minecraft.util.Identifier;
import net.pixeldream.mythicmobs.MythicMobs;
import net.pixeldream.mythicmobs.entity.WendigoEntity;

public class WendigoModel extends GeoModel<WendigoEntity> {

    @Override
    public Identifier getModelResource(WendigoEntity object) {
        return new Identifier(MythicMobs.MOD_ID, "geo/entity/wendigo.geo.json");
    }

    @Override
    public Identifier getTextureResource(WendigoEntity object) {
        return new Identifier(MythicMobs.MOD_ID, "textures/entity/wendigo.png");
    }

    @Override
    public Identifier getAnimationResource(WendigoEntity animatable) {
        return new Identifier(MythicMobs.MOD_ID, "animations/entity/wendigo.animation.json");
    }
}
