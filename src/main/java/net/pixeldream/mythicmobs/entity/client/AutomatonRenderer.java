package net.pixeldream.mythicmobs.entity.client;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.pixeldream.mythicmobs.MythicMobs;
import net.pixeldream.mythicmobs.entity.AutomatonEntity;

public class AutomatonRenderer extends GeoEntityRenderer<AutomatonEntity> {
    public AutomatonRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new AutomatonModel());
        this.shadowRadius = 0.85f;
    }

    @Override
    public Identifier getTextureResource(AutomatonEntity instance) {
        return new Identifier(MythicMobs.MOD_ID, "textures/entity/automaton.png");
    }

    @Override
    public RenderLayer getRenderType(AutomatonEntity animatable, float partialTicks, MatrixStack stack,
                                     VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder,
                                     int packedLightIn, Identifier textureLocation) {
        return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }
}