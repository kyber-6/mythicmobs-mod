package net.pixeldream.mythicmobs.entity.client;

import net.minecraft.util.Identifier;
import net.pixeldream.mythicmobs.MythicMobs;
import net.pixeldream.mythicmobs.entity.AutomatonEntity;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class AutomatonModel extends AnimatedGeoModel<AutomatonEntity> {

    @Override
    public Identifier getModelResource(AutomatonEntity object) {
        return new Identifier(MythicMobs.MOD_ID, "geo/entity/automaton.geo.json");
    }

    @Override
    public Identifier getTextureResource(AutomatonEntity object) {
        return new Identifier(MythicMobs.MOD_ID, "textures/entity/automaton.png");
    }

    @Override
    public Identifier getAnimationResource(AutomatonEntity animatable) {
        return new Identifier(MythicMobs.MOD_ID, "animations/entity/automaton.animation.json");
    }

    @Override
    public void setLivingAnimations(AutomatonEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 220F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 220F));
        }
    }
}
