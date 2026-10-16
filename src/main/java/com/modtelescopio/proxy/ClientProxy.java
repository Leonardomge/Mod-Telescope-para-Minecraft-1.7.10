package com.modtelescopio.proxy;

import com.modtelescopio.block.BlockMount;
import com.modtelescopio.block.BlockTelescope;
import com.modtelescopio.block.TileTelescope;
import com.modtelescopio.client.ClientObservation;
import com.modtelescopio.client.ItemRendererTelescope;
import com.modtelescopio.client.RenderTelescope;
import com.modtelescopio.item.ItemOta;
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
        for (BlockMount block : ModContent.MOUNTS.values()) {
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(block), itemRenderer);
        }
        for (ItemOta item : ModContent.OTAS.values()) {
            MinecraftForgeClient.registerItemRenderer(item, itemRenderer);
        }
        MinecraftForge.EVENT_BUS.register(ClientObservation.INSTANCE);
        FMLCommonHandler.instance().bus().register(ClientObservation.INSTANCE);
        startAutotestIfRequested();
    }

    @Override
    public void toggleObservation(EntityPlayer player, int x, int y, int z) {
        if (player == Minecraft.getMinecraft().thePlayer) {
            ClientObservation.INSTANCE.toggle(x, y, z);
        }
    }

    /**
     * Teste automático com capturas de tela, só no ambiente de desenvolvimento
     * ({@code gradlew runClient -Pautotest=true}). A classe não vai para o .jar final.
     */
    private static void startAutotestIfRequested() {
        if (!Boolean.getBoolean("telescopio.autotest")) {
            return;
        }
        try {
            Class.forName("com.modtelescopio.autotest.AutoTest").getMethod("start").invoke(null);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao iniciar o teste automático", e);
        }
    }
}
