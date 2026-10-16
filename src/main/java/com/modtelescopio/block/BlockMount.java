package com.modtelescopio.block;

import com.modtelescopio.TelescopioMod;
import com.modtelescopio.common.MountBlockType;
import com.modtelescopio.common.TelescopeType;
import com.modtelescopio.item.ItemOta;
import com.modtelescopio.registry.ModContent;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import java.util.Locale;

/**
 * Montagem em tripé/pilar. Clique segurando um tubo óptico para instalar; com tubo, o
 * botão direito observa; agachado com a mão vazia, retira o tubo.
 */
public class BlockMount extends BlockTelescopeBase {
    public final MountBlockType mount;

    public BlockMount(MountBlockType mount) {
        this.mount = mount;
        setBlockName(TelescopioMod.MODID + "." + mount.id);
        setBlockTextureName("hardened_clay_stained_" + mount.colorName);
        setCreativeTab(ModContent.TAB_PARTS);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side,
                                    float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileTelescope)) {
            return false;
        }
        TileTelescope tile = (TileTelescope) te;
        ItemStack held = player.getCurrentEquippedItem();

        if (held != null && held.getItem() instanceof ItemOta) {
            TelescopeType tube = ((ItemOta) held.getItem()).type;
            if (!world.isRemote) {
                if (tile.getTube() != null) {
                    player.addChatMessage(new ChatComponentTranslation("telescopio.msg.has_tube"));
                } else if (!mount.canCarry(tube)) {
                    player.addChatMessage(new ChatComponentTranslation("telescopio.msg.too_heavy",
                            kg(tube.weightKg), kg(mount.capacityKg)));
                } else {
                    tile.setTube(tube);
                    world.markBlockForUpdate(x, y, z);
                    if (!player.capabilities.isCreativeMode) {
                        held.stackSize--;
                        if (held.stackSize <= 0) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        }
                    }
                    world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.click", 0.6F, 1.4F);
                    player.addChatMessage(new ChatComponentTranslation("telescopio.msg.mounted",
                            new ChatComponentTranslation("item." + TelescopioMod.MODID + "." + tube.otaId() + ".name")));
                }
            }
            return true;
        }

        if (player.isSneaking()) {
            if (held == null && tile.getTube() != null) {
                if (!world.isRemote) {
                    TelescopeType tube = tile.getTube();
                    tile.setTube(null);
                    world.markBlockForUpdate(x, y, z);
                    ItemStack stack = new ItemStack(ModContent.OTAS.get(tube));
                    if (!player.inventory.addItemStackToInventory(stack)) {
                        player.dropPlayerItemWithRandomChoice(stack, false);
                    }
                    world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.click", 0.6F, 0.9F);
                    player.addChatMessage(new ChatComponentTranslation("telescopio.msg.removed",
                            new ChatComponentTranslation("item." + TelescopioMod.MODID + "." + tube.otaId() + ".name")));
                }
                return true;
            }
            return false;
        }

        if (tile.getTube() == null) {
            if (!world.isRemote) {
                player.addChatMessage(new ChatComponentTranslation("telescopio.msg.no_tube"));
            }
            return true;
        }
        if (world.isRemote) {
            TelescopioMod.proxy.toggleObservation(player, x, y, z);
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!world.isRemote && te instanceof TileTelescope && ((TileTelescope) te).getTube() != null) {
            ItemStack stack = new ItemStack(ModContent.OTAS.get(((TileTelescope) te).getTube()));
            world.spawnEntityInWorld(new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, stack));
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    private static String kg(float v) {
        return String.format(Locale.ROOT, "%.1f", v);
    }
}
