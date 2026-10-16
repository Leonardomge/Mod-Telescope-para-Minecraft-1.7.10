package com.modtelescopio.item;

import com.modtelescopio.block.BlockMount;
import com.modtelescopio.common.MountBlockType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.List;

/** Item da montagem em tripé/pilar (renderizado em 3D, sem tubo). */
public class ItemMountBlock extends ItemBlock {
    public ItemMountBlock(Block block) {
        super(block);
        setMaxStackSize(1);
    }

    public MountBlockType getMount() {
        return ((BlockMount) Block.getBlockFromItem(this)).mount;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean advanced) {
        MountBlockType m = getMount();
        lines.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal(m.preset.langKey()));
        lines.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted("telescopio.tooltip.capacity",
                TooltipHelper.fmt1(m.capacityKg)));
        if (m.goTo) {
            lines.add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("telescopio.tooltip.goto"));
        }
        TooltipHelper.hint(lines, "telescopio.tooltip.mount_use");
        TooltipHelper.hint(lines, "telescopio.tooltip.mount_remove");
    }
}
