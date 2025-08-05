package com.cu6.avaritia_expand.item.armor;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

public class CrystalHelmet extends ArmorItem {
    public CrystalHelmet(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    public boolean canDeflectArrows() {
        return true;
    }
}
