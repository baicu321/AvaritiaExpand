package com.cu6.avaritia_expand.item.tool;


import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;


public class InfinityFishingRod extends FishingRodItem
 {
     public InfinityFishingRod(Properties pProperties) {
        super(pProperties);
     }

     @Override
     public boolean isDamageable(ItemStack stack) {
         return false;
     }



 }
