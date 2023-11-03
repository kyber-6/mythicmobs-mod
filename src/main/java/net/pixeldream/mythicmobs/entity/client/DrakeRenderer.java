package net.pixeldream.mythicmobs.entity.client;

import com.google.common.collect.Maps;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.pixeldream.mythicmobs.MythicMobs;
import net.pixeldream.mythicmobs.entity.DrakeEntity;
import net.pixeldream.mythicmobs.entity.DrakeVariant;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import java.util.Map;

public class DrakeRenderer extends GeoEntityRenderer<DrakeEntity> {
    public static final Map<DrakeVariant, Identifier> LOCATION_BY_VARIANT = Util.make(Maps.newEnumMap(DrakeVariant.class), (map) -> {
        map.put(DrakeVariant.DRAKE_1, new Identifier(MythicMobs.MOD_ID, "textures/entity/drake/drake_1.png"));
        map.put(DrakeVariant.DRAKE_2, new Identifier(MythicMobs.MOD_ID, "textures/entity/drake/drake_2.png"));
        map.put(DrakeVariant.DRAKE_3, new Identifier(MythicMobs.MOD_ID, "textures/entity/drake/drake_3.png"));
        map.put(DrakeVariant.DRAKE_4, new Identifier(MythicMobs.MOD_ID, "textures/entity/drake/drake_4.png"));
        map.put(DrakeVariant.DRAKE_5, new Identifier(MythicMobs.MOD_ID, "textures/entity/drake/drake_5.png"));
        map.put(DrakeVariant.DRAKE_6, new Identifier(MythicMobs.MOD_ID, "textures/entity/drake/drake_6.png"));
        map.put(DrakeVariant.DRAKE_7, new Identifier(MythicMobs.MOD_ID, "textures/entity/drake/drake_7.png"));
    });
    public static final Map<DrakeVariant, Identifier> EYES_LOCATION_BY_VARIANT = Util.make(Maps.newEnumMap(DrakeVariant.class), (map) -> {
        map.put(DrakeVariant.DRAKE_1, new Identifier(MythicMobs.MOD_ID, "textures/entity/drake/drake_1_eyes.png"));
        map.put(DrakeVariant.DRAKE_2, new Identifier(MythicMobs.MOD_ID, "textures/entity/drake/drake_2_eyes.png"));
        map.put(DrakeVariant.DRAKE_3, new Identifier(MythicMobs.MOD_ID, "textures/entity/drake/drake_3_eyes.png"));
        map.put(DrakeVariant.DRAKE_4, new Identifier(MythicMobs.MOD_ID, "textures/entity/drake/drake_4_eyes.png"));
        map.put(DrakeVariant.DRAKE_5, new Identifier(MythicMobs.MOD_ID, "textures/entity/drake/drake_5_eyes.png"));
        map.put(DrakeVariant.DRAKE_6, new Identifier(MythicMobs.MOD_ID, "textures/entity/drake/drake_6_eyes.png"));
        map.put(DrakeVariant.DRAKE_7, new Identifier(MythicMobs.MOD_ID, "textures/entity/drake/drake_7_eyes.png"));
    });

    public DrakeRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new DrakeModel());
        this.shadowRadius = 0.75f;
        if (animatable != null) {
            this.addLayer(new DrakeLayer<>(this, EYES_LOCATION_BY_VARIANT.get(animatable.getVariant())));  //GLOW LAYER
        }
    }

    @Override
    public Identifier getTextureResource(DrakeEntity instance) {
        return LOCATION_BY_VARIANT.get(instance.getVariant());
    }

    @Override
    public RenderLayer getRenderType(DrakeEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        if (animatable.isBaby()) {
            stack.scale(0.5f, 0.5f, 0.5f);
        } else {
            stack.scale(1f, 1f, 1f);
        }
        return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }
}