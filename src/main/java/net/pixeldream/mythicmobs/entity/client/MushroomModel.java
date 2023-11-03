package net.pixeldream.mythicmobs.entity.client;

import net.minecraft.util.Identifier;
import net.pixeldream.mythicmobs.MythicMobs;
import net.pixeldream.mythicmobs.entity.MushroomEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MushroomModel extends AnimatedGeoModel<MushroomEntity> {

    @Override
    public Identifier getModelResource(MushroomEntity object) {
        return new Identifier(MythicMobs.MOD_ID, "geo/entity/mushroom_thing.geo.json");
    }

    @Override
    public Identifier getTextureResource(MushroomEntity object) {
        return MushroomRenderer.LOCATION_BY_VARIANT.get(object.getVariant());
    }

    @Override
    public Identifier getAnimationResource(MushroomEntity animatable) {
        return new Identifier(MythicMobs.MOD_ID, "animations/entity/mushroom_thing.animation.json");
    }
}