package net.pixeldream.mythicmobs.entity.client;

import net.minecraft.util.Identifier;
import net.pixeldream.mythicmobs.MythicMobs;
import net.pixeldream.mythicmobs.entity.KoboldWarriorEntity;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class KoboldWarriorModel extends AnimatedGeoModel<KoboldWarriorEntity> {
    @Override
    public Identifier getModelResource(KoboldWarriorEntity object) {
        return new Identifier(MythicMobs.MOD_ID, "geo/entity/kobold_warrior.geo.json");
    }

    @Override
    public Identifier getTextureResource(KoboldWarriorEntity object) {
        return KoboldWarriorRenderer.LOCATION_BY_VARIANT.get(object.getVariant());
    }

    @Override
    public Identifier getAnimationResource(KoboldWarriorEntity animatable) {
        return new Identifier(MythicMobs.MOD_ID, "animations/entity/kobold_warrior.animation.json");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setLivingAnimations(KoboldWarriorEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 220F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 220F));
        }
    }
}