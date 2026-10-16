package com.modtelescopio.item;

import com.modtelescopio.block.BlockTelescope;
import com.modtelescopio.common.TelescopeType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.List;

/** Item do telescópio completo no inventário (renderizado em 3D pelo ItemRendererTelescope). */
public class ItemTelescope extends ItemBlock {
    public ItemTelescope(Block block) {
        super(block);
        setMaxStackSize(1);
    }

    public TelescopeType getType() {
        return ((BlockTelescope) Block.getBlockFromItem(this)).type;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean advanced) {
        TelescopeType t = getType();
        TooltipHelper.optics(lines, t);
        String mount = StatCollector.translateToLocal(t.mount.langKey());
        if (!t.mountName.isEmpty()) {
            mount += " (" + t.mountName + ")";
        }
        lines.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted("telescopio.tooltip.mount", mount));
        if (t.goTo) {
            lines.add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("telescopio.tooltip.goto"));
        }
        TooltipHelper.extras(lines, t);
        TooltipHelper.hint(lines, "telescopio.tooltip.use");
    }
}
