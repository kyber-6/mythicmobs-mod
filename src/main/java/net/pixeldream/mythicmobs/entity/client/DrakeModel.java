package net.pixeldream.mythicmobs.entity.client;

import net.minecraft.util.Identifier;
import net.pixeldream.mythicmobs.MythicMobs;
import net.pixeldream.mythicmobs.entity.DrakeEntity;

public class DrakeModel extends AnimatedGeoModel<DrakeEntity> {

    @Override
    public Identifier getModelResource(DrakeEntity object) {
        return new Identifier(MythicMobs.MOD_ID, "geo/entity/drake.geo.json");
    }

    @Override
    public Identifier getTextureResource(DrakeEntity object) {
        return DrakeRenderer.LOCATION_BY_VARIANT.get(object.getVariant());
    }

    @Override
    public Identifier getAnimationResource(DrakeEntity animatable) {
        return new Identifier(MythicMobs.MOD_ID, "animations/entity/drake.animation.json");
    }
}
