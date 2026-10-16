package com.modtelescopio.block;

import com.modtelescopio.common.Optics;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Base dos blocos desenhados pelo {@code RenderTelescope} (telescópios completos e montagens).
 * O metadado guarda a direção (0 = sul, 1 = oeste, 2 = norte, 3 = leste).
 */
public abstract class BlockTelescopeBase extends BlockContainer {
    protected BlockTelescopeBase() {
        super(Material.iron);
        setHardness(1.5F);
        setResistance(6.0F);
        setStepSound(soundTypeMetal);
        setHarvestLevel("pickaxe", 0);
        setBlockBounds(0.125F, 0.0F, 0.125F, 0.875F, 1.0F, 0.875F);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileTelescope();
    }

    public static float facingYaw(int meta) {
        return (meta & 3) * 90.0F;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        int dir = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, dir, 2);
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileTelescope) {
            ((TileTelescope) te).setAim(facingYaw(dir), Optics.DEFAULT_ALTITUDE);
        }
    }
}
