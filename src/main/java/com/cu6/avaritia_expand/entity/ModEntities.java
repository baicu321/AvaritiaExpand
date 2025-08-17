package com.cu6.avaritia_expand.entity;

import com.cu6.avaritia_expand.AvaritiaExpand;
import com.cu6.avaritia_expand.entity.custom.CrystalTNTEntity;
import com.cu6.avaritia_expand.entity.custom.InfinityFishingHook;
import com.cu6.avaritia_expand.entity.custom.InfinityTNTEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AvaritiaExpand.MOD_ID);

    public static final RegistryObject<EntityType<InfinityTNTEntity>> INFINITY_TNT_ENTITY =
            ENTITY_TYPE.register("infinity_tnt_entity", () -> EntityType.Builder.<InfinityTNTEntity>of(InfinityTNTEntity::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .build("infinity_tnt_entity"));
    public static final RegistryObject<EntityType<CrystalTNTEntity>> CRYSTAL_TNT_ENTITY =
            ENTITY_TYPE.register("crystal_tnt_entity", () -> EntityType.Builder.<CrystalTNTEntity>of(CrystalTNTEntity::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .build("crystal_tnt_entity"));
    public static final RegistryObject<EntityType<InfinityFishingHook>> INFINITY_FISHING_HOOK =
            ENTITY_TYPE.register("infinity_fishing_hook", () -> EntityType.Builder.<InfinityFishingHook>of(InfinityFishingHook::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F)
                    .clientTrackingRange(10)
                    .build("infinity_fishing_hook"));
    public static void register(IEventBus eventBus){
        ENTITY_TYPE.register(eventBus);
    }

}
