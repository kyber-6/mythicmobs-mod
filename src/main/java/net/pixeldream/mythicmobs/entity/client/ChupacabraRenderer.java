package net.pixeldream.mythicmobs.entity.client;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.pixeldream.mythicmobs.MythicMobs;
import net.pixeldream.mythicmobs.entity.ChupacabraEntity;

public class ChupacabraRenderer extends GeoEntityRenderer<ChupacabraEntity> {
    public ChupacabraRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ChupacabraModel());
        this.shadowRadius = 0.65f;
        this.addLayer(new ChupacabraLayer<>(this)); //GLOW LAYER
    }

    @Override
    public Identifier getTextureResource(ChupacabraEntity instance) {
        return new Identifier(MythicMobs.MOD_ID, "textures/entity/chupacabra.png");
    }

    @Override
    public RenderLayer getRenderType(ChupacabraEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }
}