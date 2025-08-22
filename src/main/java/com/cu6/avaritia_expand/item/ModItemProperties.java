package com.cu6.avaritia_expand.item;


import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public class ModItemProperties {

    public static void register(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            registerFishingRodCast(ModItems.INFINITY_FISHINGROD.get());
            registerShieldBlocking(ModItems.BLAZE_SHIELD.get());
            registerShieldBlocking(ModItems.CRYSTAL_SHIELD.get());
            registerBowProperties(ModItems.BLAZE_BOW.get());
            registerBowProperties(ModItems.CRYSTAL_BOW.get());
        });
    }

    private static void registerFishingRodCast(Item item) {
        ItemProperties.register(item,
                new ResourceLocation("cast"),
                (stack, world, entity, seed) -> {
                    if (entity == null) {
                        return 0.0F;
                    }
                    // 检查实体是否是玩家，并且当前正在使用这个鱼竿
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        // 确保玩家主手或副手持有了这个鱼竿
                        if (player.getMainHandItem() == stack || player.getOffhandItem() == stack) {
                            return player.fishing != null ? 1.0F : 0.0F;
                        }
                    }
                    return 0.0F;
                });
    }
    public static void registerShieldBlocking(Item item){
        ItemProperties.register(item,
                new ResourceLocation("blocking"),
                (stack, world, entity, seed) ->{
                       return entity != null &&
                                entity.isUsingItem() &&
                                entity.getUseItem() == stack ? 1.0F : 0.0F;
                });
    }
    private static void registerBowProperties(Item item) {
        // 拉弓进度属性
        ItemProperties.register(item,
                new ResourceLocation("pull"),
                (stack, world, entity, seed) -> {
                    if (entity == null) {
                        return 0.0F;
                    }
                    // 检查玩家是否正在使用这个物品
                    if (entity.getUseItem() != stack) {
                        return 0.0F;
                    }
                    // 计算拉弓进度
                    return (float) (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;
                });

        // 是否正在拉弓
        ItemProperties.register(item,
                new ResourceLocation("pulling"),
                (stack, world, entity, seed) -> {
                    return entity != null &&
                            entity.isUsingItem() &&
                            entity.getUseItem() == stack ? 1.0F : 0.0F;
                });

    }
}
