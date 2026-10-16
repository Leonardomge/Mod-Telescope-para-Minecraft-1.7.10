package com.modtelescopio.block;

import com.modtelescopio.TelescopioMod;
import com.modtelescopio.common.TelescopeType;
import com.modtelescopio.registry.ModContent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/** Telescópio completo (tubo + montagem de fábrica). */
public class BlockTelescope extends BlockTelescopeBase {
    public final TelescopeType type;

    public BlockTelescope(TelescopeType type) {
        this.type = type;
        setBlockName(TelescopioMod.MODID + "." + type.id);
        setBlockTextureName("hardened_clay_stained_" + type.colorName);
        setCreativeTab(ModContent.TAB);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side,
                                    float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            return false;
        }
        if (world.isRemote) {
            TelescopioMod.proxy.toggleObservation(player, x, y, z);
        }
        return true;
    }
}
