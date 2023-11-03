package net.pixeldream.mythicmobs.entity.client;

import com.google.common.collect.Maps;
import mod.azure.azurelib.cache.object.GeoBone;
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
import net.pixeldream.mythicmobs.MythicMobs;
import net.pixeldream.mythicmobs.entity.KoboldWarriorEntity;
import net.pixeldream.mythicmobs.entity.KoboldWarriorVariant;

import java.util.Map;

public class KoboldWarriorRenderer extends ExtendedGeoEntityRenderer<KoboldWarriorEntity> {
    protected ItemStack mainHandItem;
    protected ItemStack offHandItem;
    public static final Map<KoboldWarriorVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(KoboldWarriorVariant.class), (map) -> {
                map.put(KoboldWarriorVariant.KOBOLD_WARRIOR_1,
                        new Identifier(MythicMobs.MOD_ID, "textures/entity/kobold_warrior/kobold_warrior_1.png"));
                map.put(KoboldWarriorVariant.KOBOLD_WARRIOR_2,
                        new Identifier(MythicMobs.MOD_ID, "textures/entity/kobold_warrior/kobold_warrior_2.png"));
                map.put(KoboldWarriorVariant.KOBOLD_WARRIOR_3,
                        new Identifier(MythicMobs.MOD_ID, "textures/entity/kobold_warrior/kobold_warrior_3.png"));
            });
    public KoboldWarriorRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new KoboldWarriorModel());
        this.shadowRadius = 0.4f;
    }

    @Override
    public Identifier getTextureResource(KoboldWarriorEntity instance) {
        return LOCATION_BY_VARIANT.get(instance.getVariant());
    }

    @Override
    public RenderLayer getRenderType(KoboldWarriorEntity animatable, float partialTicks, MatrixStack stack,
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
    protected Identifier getTextureForBone(String boneName, KoboldWarriorEntity currentEntity) {
        return null;
    }

    @Override
    protected ItemStack getHeldItemForBone(String boneName, KoboldWarriorEntity currentEntity) {
        if (boneName.equals("item"))
            return mainHandItem;
        else if (boneName.equals("item2"))
            return offHandItem;
        return null;
    }

    @Override
    protected ModelTransformation.Mode getCameraTransformForItemAtBone(ItemStack boneItem, String boneName) {
        if (boneName.equals("item"))
            return ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND;
        else if (boneName.equals("item2"))
            return ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND;
        return ModelTransformation.Mode.NONE;
    }

    @Override
    protected BlockState getHeldBlockForBone(String boneName, KoboldWarriorEntity currentEntity) {
        return null;
    }

    @Override
    protected void preRenderItem(MatrixStack stack, ItemStack item, String boneName, KoboldWarriorEntity currentEntity, IBone bone) {
        if (item == this.mainHandItem || item == this.offHandItem) {
            stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90f));
        }
    }

    @Override
    protected void preRenderBlock(MatrixStack PoseStack, BlockState block, String boneName, KoboldWarriorEntity currentEntity) {

    }

    @Override
    protected void postRenderItem(MatrixStack PoseStack, ItemStack item, String boneName, KoboldWarriorEntity currentEntity, IBone bone) {

    }

    @Override
    protected void postRenderBlock(MatrixStack PoseStack, BlockState block, String boneName, KoboldWarriorEntity currentEntity) {

    }
}
