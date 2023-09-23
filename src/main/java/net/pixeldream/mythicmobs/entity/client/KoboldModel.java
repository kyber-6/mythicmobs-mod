package net.pixeldream.mythicmobs.entity.client;

import mod.azure.azurelib.model.data.EntityModelData;
import net.minecraft.util.Identifier;
import net.pixeldream.mythicmobs.MythicMobs;
import net.pixeldream.mythicmobs.entity.KoboldEntity;

public class KoboldModel extends AnimatedGeoModel<KoboldEntity> {
    @Override
    public Identifier getModelResource(KoboldEntity object) {
        return new Identifier(MythicMobs.MOD_ID, "geo/entity/kobold.geo.json");
    }

    @Override
    public Identifier getTextureResource(KoboldEntity object) {
        return KoboldRenderer.LOCATION_BY_VARIANT.get(object.getVariant());
    }

    @Override
    public Identifier getAnimationResource(KoboldEntity animatable) {
        return new Identifier(MythicMobs.MOD_ID, "animations/entity/kobold.animation.json");
    }

    @Override
    public void setLivingAnimations(KoboldEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 220F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 220F));
        }
    }
}
