package com.modtelescopio.block;

import com.modtelescopio.TelescopioMod;
import com.modtelescopio.common.Optics;
import com.modtelescopio.common.TelescopeType;
import com.modtelescopio.registry.ModContent;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Bloco do telescópio. Não tem modelo de bloco: quem desenha é o {@code RenderTelescope}.
 * O metadado guarda a direção (0 = sul, 1 = oeste, 2 = norte, 3 = leste).
 */
public class BlockTelescope extends BlockContainer {
    public final TelescopeType type;

    public BlockTelescope(TelescopeType type) {
        super(Material.iron);
        this.type = type;
        setBlockName(TelescopioMod.MODID + "." + type.id);
        setBlockTextureName("hardened_clay_stained_" + type.colorName);
        setHardness(1.5F);
        setResistance(6.0F);
        setStepSound(soundTypeMetal);
        setCreativeTab(ModContent.TAB);
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
