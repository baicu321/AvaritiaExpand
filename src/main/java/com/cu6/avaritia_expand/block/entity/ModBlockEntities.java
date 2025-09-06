package com.cu6.avaritia_expand.block.entity;

import com.cu6.avaritia_expand.AvaritiaExpand;
import com.cu6.avaritia_expand.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AvaritiaExpand.MOD_ID);

    public static final RegistryObject<BlockEntityType<BlazeFurnaceBlockEntity>> BLAZE_FURNACE_BE =
            BLOCK_ENTITIES.register("blaze_furnace_be", ()->
                    BlockEntityType.Builder.of(BlazeFurnaceBlockEntity::new,
                            ModBlocks.BLAZE_FURNACE.get()).build(null));
    public static final RegistryObject<BlockEntityType<CrystalFurnaceBlockEntity>> CRYSTAL_FURNACE_CE =
            BLOCK_ENTITIES.register("crystal_furnace_be", ()->
                    BlockEntityType.Builder.of(CrystalFurnaceBlockEntity::new,
                            ModBlocks.CRYSTAL_FURNACE.get()).build(null));
    public static final RegistryObject<BlockEntityType<NeutronDecomposeBlockEntity>> NEUTRON_DECOMPOSE_BE =
            BLOCK_ENTITIES.register("neutron_decompose_be", ()->
                    BlockEntityType.Builder.of(NeutronDecomposeBlockEntity::new,
                            ModBlocks.NEUTRON_DECOMPOSE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
