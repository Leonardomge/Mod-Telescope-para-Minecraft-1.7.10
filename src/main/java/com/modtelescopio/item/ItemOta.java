package com.modtelescopio.item;

import com.modtelescopio.TelescopioMod;
import com.modtelescopio.common.TelescopeType;
import com.modtelescopio.registry.ModContent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.List;

/** Tubo óptico avulso: vai em cima de uma montagem-bloco. */
public class ItemOta extends Item {
    public final TelescopeType type;

    public ItemOta(TelescopeType type) {
        this.type = type;
        setUnlocalizedName(TelescopioMod.MODID + "." + type.otaId());
        setTextureName("iron_block");
        setMaxStackSize(1);
        setCreativeTab(ModContent.TAB_PARTS);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean advanced) {
        TooltipHelper.optics(lines, type);
        lines.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted("telescopio.tooltip.weight",
                TooltipHelper.fmt1(type.weightKg)));
        TooltipHelper.extras(lines, type);
        TooltipHelper.hint(lines, "telescopio.tooltip.ota_use");
    }
}
