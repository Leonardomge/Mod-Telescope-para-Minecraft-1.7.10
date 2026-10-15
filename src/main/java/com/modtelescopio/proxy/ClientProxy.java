package com.modtelescopio.proxy;

import com.modtelescopio.block.BlockTelescope;
import com.modtelescopio.block.TileTelescope;
import com.modtelescopio.client.ClientObservation;
import com.modtelescopio.client.ItemRendererTelescope;
import com.modtelescopio.client.RenderTelescope;
import com.modtelescopio.registry.ModContent;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileTelescope.class, new RenderTelescope());
        ItemRendererTelescope itemRenderer = new ItemRendererTelescope();
        for (BlockTelescope block : ModContent.TELESCOPES.values()) {
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(block), itemRenderer);
        }
        MinecraftForge.EVENT_BUS.register(ClientObservation.INSTANCE);
        FMLCommonHandler.instance().bus().register(ClientObservation.INSTANCE);
    }

    @Override
    public void toggleObservation(EntityPlayer player, int x, int y, int z) {
        if (player == Minecraft.getMinecraft().thePlayer) {
            ClientObservation.INSTANCE.toggle(x, y, z);
        }
    }
}
