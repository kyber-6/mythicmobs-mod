package net.pixeldream.mythicmobs.entity.client;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.pixeldream.mythicmobs.MythicMobs;
import net.pixeldream.mythicmobs.entity.DrakeEntity;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class DrakeLayer<T extends DrakeEntity> extends GeoLayerRenderer<T> {
    protected final Identifier eyeTexture;
    protected static final Identifier DRAKE_GEO = new Identifier(MythicMobs.MOD_ID, "geo/entity/drake.geo.json");


    public DrakeLayer(IGeoRenderer<T> entityRendererIn, Identifier eyeTexture) {
        super(entityRendererIn);
        this.eyeTexture = eyeTexture;
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, T entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderLayer glow = RenderLayer.getEntityTranslucentEmissive(eyeTexture);
        this.getRenderer().render(this.getEntityModel().getModel(DRAKE_GEO), entityLivingBaseIn, partialTicks, glow, matrixStackIn, bufferIn, bufferIn.getBuffer(glow), packedLightIn, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
    }
}
