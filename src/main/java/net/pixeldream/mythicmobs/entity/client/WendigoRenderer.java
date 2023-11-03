package net.pixeldream.mythicmobs.entity.client;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.pixeldream.mythicmobs.MythicMobs;
import net.pixeldream.mythicmobs.entity.WendigoEntity;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class WendigoRenderer extends GeoEntityRenderer<WendigoEntity> {
    public WendigoRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new WendigoModel());
        this.shadowRadius = 1.5f;
    }

    @Override
    public Identifier getTextureResource(WendigoEntity instance) {
        return new Identifier(MythicMobs.MOD_ID, "textures/entity/wendigo.png");
    }

    @Override
    public RenderLayer getRenderType(WendigoEntity animatable, float partialTicks, MatrixStack stack,
                                     VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder,
                                     int packedLightIn, Identifier textureLocation) {
        return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }
}
