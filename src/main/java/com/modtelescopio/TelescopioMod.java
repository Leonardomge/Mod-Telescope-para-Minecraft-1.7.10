package com.modtelescopio;

import com.modtelescopio.network.MessageAim;
import com.modtelescopio.proxy.CommonProxy;
import com.modtelescopio.registry.ModContent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = TelescopioMod.MODID, name = TelescopioMod.NAME, version = TelescopioMod.VERSION)
public class TelescopioMod {
    public static final String MODID = "telescopio";
    public static final String NAME = "Mod Telescópio";
    public static final String VERSION = "1.0.0";

    @Mod.Instance(MODID)
    public static TelescopioMod instance;

    @SidedProxy(clientSide = "com.modtelescopio.proxy.ClientProxy", serverSide = "com.modtelescopio.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModContent.registerAll();
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        network.registerMessage(MessageAim.Handler.class, MessageAim.class, 0, Side.SERVER);
        FMLCommonHandler.instance().bus().register(new MessageAim.ServerQueue());
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ModContent.registerRecipes();
        proxy.init();
    }
}
