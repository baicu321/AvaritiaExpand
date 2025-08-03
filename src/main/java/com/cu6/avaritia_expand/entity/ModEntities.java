package com.cu6.avaritia_expand.entity;

import com.cu6.avaritia_expand.AvaritiaExpand;
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

    public static final RegistryObject<EntityType<InfinityTNTEntity>> INFINTITY_TNT_ENTITY =
            ENTITY_TYPE.register("infinity_tnt_entity", () -> EntityType.Builder.<InfinityTNTEntity>of(InfinityTNTEntity::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .build("infinity_tnt_entity"));

    public static void register(IEventBus eventBus){
        ENTITY_TYPE.register(eventBus);
    }

}
