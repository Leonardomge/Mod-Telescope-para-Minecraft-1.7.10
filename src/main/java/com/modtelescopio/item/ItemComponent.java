package com.modtelescopio.item;

import com.modtelescopio.TelescopioMod;
import com.modtelescopio.common.ComponentType;
import com.modtelescopio.registry.ModContent;
import net.minecraft.item.Item;

/** Peça de crafting (lente, espelho, montagem...). */
public class ItemComponent extends Item {
    public final ComponentType type;

    public ItemComponent(ComponentType type) {
        this.type = type;
        setUnlocalizedName(TelescopioMod.MODID + "." + type.id);
        setTextureName(TelescopioMod.MODID + ":" + type.id);
        setCreativeTab(ModContent.TAB);
    }
}
