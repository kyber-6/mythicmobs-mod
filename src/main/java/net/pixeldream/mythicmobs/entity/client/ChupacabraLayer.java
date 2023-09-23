package net.pixeldream.mythicmobs.entity.client;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.pixeldream.mythicmobs.MythicMobs;
import net.pixeldream.mythicmobs.entity.ChupacabraEntity;

public class ChupacabraLayer<T extends ChupacabraEntity> extends GeoLayerRenderer<T> {
    protected static final Identifier CHUPACABRA_EYES_TEXTURE = new Identifier(MythicMobs.MOD_ID, "textures/entity/chupacabra_eyes.png");
    protected static final Identifier CHUPACABRA_GEO = new Identifier(MythicMobs.MOD_ID, "geo/entity/chupacabra.geo.json");

    public ChupacabraLayer(IGeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, T entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderLayer glow = RenderLayer.getEntityTranslucentEmissive(CHUPACABRA_EYES_TEXTURE);
        this.getRenderer().render(this.getEntityModel().getModel(CHUPACABRA_GEO), entityLivingBaseIn, partialTicks, glow, matrixStackIn, bufferIn, bufferIn.getBuffer(glow), packedLightIn, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
    }
}
