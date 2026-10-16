package com.modtelescopio.autotest;

import com.modtelescopio.block.BlockTelescopeBase;
import com.modtelescopio.block.TileTelescope;
import com.modtelescopio.client.ClientObservation;
import com.modtelescopio.client.model.ModelBounds;
import com.modtelescopio.common.MountBlockType;
import com.modtelescopio.common.Optics;
import com.modtelescopio.common.TelescopeType;
import com.modtelescopio.registry.ModContent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Teste automático (só desenvolvimento, não vai para o .jar): cria um mundo plano, monta
 * todos os telescópios e montagens, tira capturas de tela, testa a observação (ocular e
 * câmera) e fecha o jogo. Ativado por {@code gradlew runClient -Pautotest=true}.
 */
public final class AutoTest {
    private static final String WORLD = "telescopio_autotest";

    /** Um passo do roteiro: espera {@code delay} ticks e executa a ação no cliente. */
    private static final class Step {
        final int delay;
        final Runnable action;

        Step(int delay, Runnable action) {
            this.delay = delay;
            this.action = action;
        }
    }

    private final Minecraft mc = Minecraft.getMinecraft();
    private final ConcurrentLinkedQueue<Runnable> serverTasks = new ConcurrentLinkedQueue<Runnable>();
    private final List<Step> steps = new ArrayList<Step>();
    private final Map<String, int[]> placed = new HashMap<String, int[]>();
    private int stage;
    private int stepIndex;
    private int wait;
    private int shot;
    private int ox, oy, oz;

    // estado imposto a cada tick (protege o roteiro de cliques/teclas na janela)
    private double[] hold;
    private int thirdPerson;
    private boolean hideGui;
    private int[] observing;

    public static void start() {
        AutoTest t = new AutoTest();
        FMLCommonHandler.instance().bus().register(t);
        System.out.println("[AutoTest] ativado");
    }

    private void log(String s) {
        System.out.println("[AutoTest] " + s);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.START) {
            return;
        }
        Runnable r;
        while ((r = serverTasks.poll()) != null) {
            try {
                r.run();
            } catch (Throwable t) {
                log("ERRO no servidor: " + t);
                t.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        try {
            tick();
        } catch (Throwable t) {
            log("ERRO: " + t);
            t.printStackTrace();
            mc.shutdown();
        }
    }

    private void enforce() {
        mc.gameSettings.pauseOnLostFocus = false;
        if (mc.currentScreen instanceof GuiIngameMenu) {
            mc.displayGuiScreen(null);
        }
        // solta o mouse: movimentos do mouse no PC não giram a câmera do teste
        if (mc.inGameHasFocus) {
            mc.setIngameNotInFocus();
        }
        mc.gameSettings.thirdPersonView = thirdPerson;
        mc.gameSettings.hideGUI = hideGui;
        if (hold != null) {
            mc.thePlayer.capabilities.isFlying = true;
            // no 1.7.10 o posY do jogador do cliente fica na altura dos olhos (yOffset = 1,62)
            mc.thePlayer.setPositionAndRotation(hold[0], hold[1] + mc.thePlayer.yOffset, hold[2], (float) hold[3],
                    (float) hold[4]);
            mc.thePlayer.rotationYawHead = (float) hold[3];
            mc.thePlayer.renderYawOffset = (float) hold[3];
            mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0;
        }
        if (observing != null && !ClientObservation.INSTANCE.isActive()) {
            log("iniciando observacao (parada anterior: " + ClientObservation.INSTANCE.lastStopReason + ")");
            ClientObservation.INSTANCE.releaseForTest();
            ClientObservation.INSTANCE.toggle(observing[0], observing[1], observing[2]);
            ClientObservation.INSTANCE.releaseForTest();
        }
    }

    private void tick() {
        switch (stage) {
            case 0:
                if (mc.currentScreen instanceof GuiMainMenu) {
                    deleteDir(new File(mc.mcDataDir, "saves/" + WORLD));
                    WorldSettings ws = new WorldSettings(4242L, WorldSettings.GameType.CREATIVE, false, false, WorldType.FLAT);
                    ws.enableCommands();
                    ws.func_82750_a("2;7,2x3,2;1;");
                    log("criando mundo");
                    mc.launchIntegratedServer(WORLD, WORLD, ws);
                    stage = 1;
                    wait = 0;
                }
                break;
            case 1:
                if (mc.thePlayer != null && mc.theWorld != null) {
                    if (mc.currentScreen instanceof GuiIngameMenu) {
                        mc.displayGuiScreen(null);
                    }
                    if (++wait > 60) {
                        ChunkCoordinates sp = mc.theWorld.getSpawnPoint();
                        ox = sp.posX;
                        oz = sp.posZ;
                        oy = mc.theWorld.getTopSolidOrLiquidBlock(ox, oz);
                        log("no mundo, origem " + ox + " " + oy + " " + oz);
                        buildScript();
                        stage = 2;
                        wait = 0;
                    }
                }
                break;
            case 2:
                enforce();
                if (stepIndex >= steps.size()) {
                    log("fim, fechando o jogo");
                    stage = 3;
                    mc.shutdown();
                    return;
                }
                Step s = steps.get(stepIndex);
                if (++wait >= s.delay) {
                    wait = 0;
                    stepIndex++;
                    s.action.run();
                }
                break;
            default:
                break;
        }
    }

    // ------------------------------------------------------------------ roteiro

    private void step(int delay, Runnable action) {
        steps.add(new Step(delay, action));
    }

    private void server(final Runnable r) {
        serverTasks.add(r);
    }

    private WorldServer world() {
        return MinecraftServer.getServer().worldServerForDimension(0);
    }

    private void place(final int x, final int y, final int z, final Block block, final int meta,
                       final float alt, final TelescopeType tube) {
        server(new Runnable() {
            @Override
            public void run() {
                WorldServer w = world();
                w.setBlock(x, y, z, block, meta, 3);
                TileEntity te = w.getTileEntity(x, y, z);
                if (te instanceof TileTelescope) {
                    ((TileTelescope) te).setAim(BlockTelescopeBase.facingYaw(meta), alt);
                    ((TileTelescope) te).setTube(tube);
                    w.markBlockForUpdate(x, y, z);
                }
            }
        });
    }

    private void setTime(final long time) {
        server(new Runnable() {
            @Override
            public void run() {
                for (WorldServer w : MinecraftServer.getServer().worldServers) {
                    w.setWorldTime(time);
                }
            }
        });
    }

    /** Agenda: posiciona a câmera (mantida a cada tick), espera e tira uma captura. */
    private void shotAt(final double x, final double y, final double z, final float yaw, final float pitch,
                        final int view, final boolean gui, final String name) {
        step(5, new Runnable() {
            @Override
            public void run() {
                hold = new double[]{x, y, z, yaw, pitch};
                thirdPerson = view;
                hideGui = gui;
            }
        });
        step(70, new Runnable() {
            @Override
            public void run() {
                screenshot(name);
            }
        });
    }

    private void screenshot(String name) {
        shot++;
        String file = String.format("telescopio_%02d_%s.png", shot, name);
        ScreenShotHelper.saveScreenshot(mc.mcDataDir, file, mc.displayWidth, mc.displayHeight, mc.getFramebuffer());
        log("captura " + file);
    }

    private void buildScript() {
        final int y = oy;
        server(new Runnable() {
            @Override
            public void run() {
                for (WorldServer w : MinecraftServer.getServer().worldServers) {
                    w.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
                    w.getGameRules().setOrCreateGameRule("doMobSpawning", "false");
                    w.getWorldInfo().setRaining(false);
                    w.getWorldInfo().setThundering(false);
                }
            }
        });
        setTime(6000);

        // fileiras de telescópios virados para leste (+X), vistos de lado a partir do sul
        List<TelescopeType> small = new ArrayList<TelescopeType>(), big = new ArrayList<TelescopeType>(),
                giant = new ArrayList<TelescopeType>();
        for (TelescopeType t : TelescopeType.values()) {
            float h = ModelBounds.of(t).height();
            if (h > 4.5F) {
                giant.add(t);
            } else if (h > 2.05F) {
                big.add(t);
            } else {
                small.add(t);
            }
        }
        row(small, oz, y, 1.5F, 16F, "pequenos");
        row(big, oz + 14, y, 2.0F, 20F, "grandes");
        row(giant, oz + 36, y, 4.0F, 60F, "gigantes");

        // escala: o jogador (visto em terceira pessoa) ao lado do Dobsoniano de 600 mm e do CDK2000
        int[] dob = placed.get("dob_flextube_600");
        int[] cdk = placed.get("planewave_cdk2000");
        // jogador ao lado do dobsoniano, câmera de frente (terceira pessoa invertida)
        shotAt(dob[0] + 0.5 - 2.6, y, dob[2] + 0.5, 0F, 8F, 2, true, "escala_dobsoniano_600");
        // coluna de 2 blocos (altura de um jogador) ao lado do CDK2000
        final int mx = cdk[0] - 6, mzz = cdk[2];
        server(new Runnable() {
            @Override
            public void run() {
                world().setBlock(mx, y, mzz, Blocks.wool, 14, 3);
                world().setBlock(mx, y + 1, mzz, Blocks.wool, 0, 3);
            }
        });
        shotAt(cdk[0] - 1.5, y + 3.0, cdk[2] + 19.0, 180F, -6F, 0, true, "escala_cdk2000");

        // closes
        int[] pds = placed.get("skywatcher_250pds_eq6r");
        shotAt(pds[0] + 0.5, y + 1.6, pds[2] + 3.2, 180F, 10F, 0, true, "close_250pds");
        int[] cdk24 = placed.get("planewave_cdk24");
        shotAt(cdk24[0] + 0.5, y + 2.4, cdk24[2] + 5.0, 180F, 8F, 0, true, "close_cdk24");

        // montagens: vazias (atrás) e com tubo (na frente)
        String[][] combos = {
                {"mount_az_gti", "skywatcher_skymax_127"}, {"mount_star_adventurer_gti", "william_optics_redcat_51"},
                {"mount_eq5", "skywatcher_evostar_80ed"}, {"mount_heq5_pro", "gso_rc6"},
                {"mount_eq6r_pro", "skywatcher_250pds_eq6r"}, {"mount_eq8r_pro", "newton_carbon_300_eq8r"},
                {"mount_avx", "celestron_rasa8"}, {"mount_cgx", "celestron_c11_cgx"},
                {"mount_cgx_l", "celestron_c14_cgxl"}, {"mount_planewave_l350", "planewave_cdk14"},
                {"mount_planewave_l600", "planewave_cdk24"}};
        int mz = oz - 14;
        int x = ox - 20;
        for (String[] c : combos) {
            MountBlockType m = MountBlockType.byId(c[0]);
            place(x, y, mz, ModContent.MOUNTS.get(m), 3, Optics.DEFAULT_ALTITUDE, TelescopeType.byId(c[1]));
            place(x, y, mz - 4, ModContent.MOUNTS.get(m), 3, Optics.DEFAULT_ALTITUDE, null);
            x += 4;
        }
        for (int i = 0; i < 3; i++) {
            shotAt(ox - 16.5 + i * 15, y + 1.2, mz + 6.0, 180F, 14F, 0, true, "montagens_" + i);
        }

        // observação: alvo xadrez a 84 blocos ao norte
        final int tx = ox + 40, tz = oz - 90;
        server(new Runnable() {
            @Override
            public void run() {
                WorldServer w = world();
                for (int dx = -6; dx <= 6; dx++) {
                    for (int dy = 0; dy <= 12; dy++) {
                        int color = ((dx + 6) / 2 + dy / 2) % 2 == 0 ? 14 : 0;
                        if (Math.abs(dx) <= 1 || dy == 6) {
                            color = 4;
                        }
                        w.setBlock(tx + dx, y + dy, tz, Blocks.wool, color, 3);
                    }
                }
            }
        });
        int px = ox + 40, pz = oz - 6;
        place(px, y, pz, ModContent.TELESCOPES.get(TelescopeType.SKYWATCHER_250PDS_EQ6R), 2, 10F, null);
        place(px + 6, y, pz, ModContent.TELESCOPES.get(TelescopeType.ZWO_SEESTAR_S50), 2, 10F, null);
        place(px + 12, y, pz, ModContent.MOUNTS.get(MountBlockType.MOUNT_EQ6R_PRO), 2, 10F, TelescopeType.CELESTRON_C11_CGX);
        observe(px, y, pz, tx, y + 6.5, tz, "ocular", new int[]{0, 2, 4});
        observe(px + 6, y, pz, tx, y + 6.5, tz, "camera", new int[]{0, 3});
        observe(px + 12, y, pz, tx, y + 6.5, tz, "montagem_com_tubo", new int[]{1});

        // noite: a Lua no zênite pelo dobsoniano
        final int nx = px - 8;
        place(nx, y, pz, ModContent.TELESCOPES.get(TelescopeType.SKYWATCHER_FLEXTUBE_400P), 2, 60F, null);
        step(10, new Runnable() {
            @Override
            public void run() {
                setTime(18000);
            }
        });
        observe(nx, y, pz, Double.NaN, 0, 0, "noite_lua", new int[]{0, 3});
        step(10, new Runnable() {
            @Override
            public void run() {
                setTime(6000);
            }
        });

        // ícones 3D no inventário criativo
        step(20, new Runnable() {
            @Override
            public void run() {
                hideGui = false;
                creativeTab(ModContent.TAB);
            }
        });
        step(40, new Runnable() {
            @Override
            public void run() {
                screenshot("inventario_telescopios");
                creativeTab(ModContent.TAB_PARTS);
            }
        });
        step(40, new Runnable() {
            @Override
            public void run() {
                screenshot("inventario_pecas");
                mc.displayGuiScreen(null);
            }
        });
        step(10, new Runnable() {
            @Override
            public void run() {
                log("roteiro concluido");
            }
        });
    }

    /** Coloca uma fileira e agenda capturas de tela em trechos de até {@code segment} blocos. */
    private void row(List<TelescopeType> list, int z, int y, float gap, float segment, String name) {
        float x = ox - 30;
        float segStart = x;
        List<float[]> segments = new ArrayList<float[]>();
        float maxH = 0;
        for (TelescopeType t : list) {
            ModelBounds b = ModelBounds.of(t);
            float w = Math.max(1F, b.width());
            if (x + w - segStart > segment && x > segStart) {
                segments.add(new float[]{segStart, x - gap, maxH});
                segStart = x;
                maxH = 0;
            }
            int bx = (int) Math.floor(x + w * 0.5F);
            place(bx, y, z, ModContent.TELESCOPES.get(t), 3, Optics.DEFAULT_ALTITUDE, null);
            placed.put(t.id, new int[]{bx, y, z});
            maxH = Math.max(maxH, b.height());
            x += w + gap;
        }
        segments.add(new float[]{segStart, x - gap, maxH});
        for (int i = 0; i < segments.size(); i++) {
            float[] s = segments.get(i);
            float width = s[1] - s[0];
            double dist = Math.max(width / 2.2, s[2] * 1.35) + 3;
            shotAt((s[0] + s[1]) / 2.0, y + s[2] * 0.55 + 1.0 - 1.62, z + dist, 180F, 12F, 0, true, name + "_" + i);
        }
    }

    /**
     * Observa pelo telescópio em (x,y,z), mirando o ponto (tx,ty,tz) - ou o zênite quando tx é NaN -
     * e tira uma captura para cada passo de zoom.
     */
    private void observe(final int x, final int y, final int z, final double tx, final double ty, final double tz,
                         final String name, final int[] zoomSteps) {
        final double ex = x + 0.5, ey = y, ez = z + 2.5;
        float yaw = 180F, pitch = -89.9F;
        if (!Double.isNaN(tx)) {
            double dx = tx + 0.5 - ex, dy = ty - (ey + 1.62), dz = tz + 0.5 - ez;
            yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
            pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
        }
        final float fy = yaw, fp = pitch;
        step(10, new Runnable() {
            @Override
            public void run() {
                hold = new double[]{ex, ey, ez, fy, fp};
                thirdPerson = 0;
                hideGui = false;
                observing = new int[]{x, y, z};
            }
        });
        for (final int zs : zoomSteps) {
            step(40, new Runnable() {
                @Override
                public void run() {
                    ClientObservation.INSTANCE.setStepForTest(zs);
                }
            });
            step(50, new Runnable() {
                @Override
                public void run() {
                    screenshot(name + "_passo" + zs);
                }
            });
        }
        step(5, new Runnable() {
            @Override
            public void run() {
                observing = null;
                ClientObservation.INSTANCE.stop();
                ClientObservation.INSTANCE.releaseForTest();
            }
        });
    }

    private void creativeTab(CreativeTabs tab) {
        GuiContainerCreative gui = new GuiContainerCreative(mc.thePlayer);
        mc.displayGuiScreen(gui);
        try {
            Method m = ReflectionHelper.findMethod(GuiContainerCreative.class, gui,
                    new String[]{"setCurrentCreativeTab", "func_147050_b"}, CreativeTabs.class);
            m.invoke(gui, tab);
        } catch (Exception e) {
            log("nao consegui trocar a aba: " + e);
        }
    }

    private static void deleteDir(File f) {
        if (!f.exists()) {
            return;
        }
        File[] children = f.listFiles();
        if (children != null) {
            for (File c : children) {
                deleteDir(c);
            }
        }
        f.delete();
    }
}
