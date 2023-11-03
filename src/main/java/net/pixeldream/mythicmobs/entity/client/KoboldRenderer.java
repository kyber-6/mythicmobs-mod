package net.pixeldream.mythicmobs.entity.client;

import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3f;
import net.pixeldream.mythicmobs.MythicMobs;
import net.pixeldream.mythicmobs.entity.KoboldEntity;
import net.pixeldream.mythicmobs.entity.KoboldVariant;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

import java.util.Map;

public class KoboldRenderer extends ExtendedGeoEntityRenderer<KoboldEntity> {
    protected ItemStack mainHandItem;
    protected ItemStack offHandItem;
    public static final Map<KoboldVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(KoboldVariant.class), (map) -> {
                map.put(KoboldVariant.KOBOLD,
                        new Identifier(MythicMobs.MOD_ID, "textures/entity/kobold/kobold.png"));
                map.put(KoboldVariant.KOBOLD_CLOTHED,
                        new Identifier(MythicMobs.MOD_ID, "textures/entity/kobold/kobold_cloth.png"));
            });

    public KoboldRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new KoboldModel());
        this.shadowRadius = 0.4f;
    }

    @Override
    public Identifier getTextureResource(KoboldEntity instance) {
        return LOCATION_BY_VARIANT.get(instance.getVariant());
    }

    @Override
    public RenderLayer getRenderType(KoboldEntity animatable, float partialTicks, MatrixStack stack,
                                     VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder,
                                     int packedLightIn, Identifier textureLocation) {
        mainHandItem = animatable.getEquippedStack(EquipmentSlot.MAINHAND);
        offHandItem = animatable.getEquippedStack(EquipmentSlot.OFFHAND);
        return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }

    @Override
    protected boolean isArmorBone(GeoBone bone) {
        return false;
    }

    @Override
    protected Identifier getTextureForBone(String boneName, KoboldEntity currentEntity) {
        return null;
    }

    @Override
    protected ItemStack getHeldItemForBone(String boneName, KoboldEntity currentEntity) {
        if (boneName.equals("hand"))
            return mainHandItem;
        else if (boneName.equals("hand2"))
            return offHandItem;
        return null;
    }

    @Override
    protected ModelTransformation.Mode getCameraTransformForItemAtBone(ItemStack boneItem, String boneName) {
        if (boneName.equals("hand"))
            return ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND;
        else if (boneName.equals("hand2"))
            return ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND;
        return ModelTransformation.Mode.NONE;
    }

    @Override
    protected BlockState getHeldBlockForBone(String boneName, KoboldEntity currentEntity) {
        return null;
    }

    @Override
    protected void preRenderItem(MatrixStack stack, ItemStack item, String boneName, KoboldEntity currentEntity, IBone bone) {
        if (item == this.mainHandItem || item == this.offHandItem) {
            stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90f));
        }
    }

    @Override
    protected void preRenderBlock(MatrixStack PoseStack, BlockState block, String boneName, KoboldEntity currentEntity) {

    }

    @Override
    protected void postRenderItem(MatrixStack PoseStack, ItemStack item, String boneName, KoboldEntity currentEntity, IBone bone) {

    }

    @Override
    protected void postRenderBlock(MatrixStack PoseStack, BlockState block, String boneName, KoboldEntity currentEntity) {

    }
}
