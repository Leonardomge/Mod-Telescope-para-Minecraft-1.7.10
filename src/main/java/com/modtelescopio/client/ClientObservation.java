package com.modtelescopio.client;

import com.modtelescopio.TelescopioMod;
import com.modtelescopio.block.BlockMount;
import com.modtelescopio.block.BlockTelescope;
import com.modtelescopio.block.TileTelescope;
import com.modtelescopio.client.model.Assembly;
import com.modtelescopio.client.model.ModelBounds;
import com.modtelescopio.common.Optics;
import com.modtelescopio.common.TelescopeType;
import com.modtelescopio.network.MessageAim;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;

/**
 * Estado da observação no cliente: zoom (FOV), sensibilidade do mouse proporcional ao
 * aumento, ganho de luz, troca de oculares (ou zoom digital da câmera) pela roda do mouse e
 * sincronização da mira.
 *
 * <p>O 1.7.10 não tem evento de FOV, então FOV/sensibilidade/gamma são trocados só durante
 * o quadro (RenderTick START) e restaurados no fim (END) - nada é salvo no options.txt.</p>
 */
public final class ClientObservation {
    public static final ClientObservation INSTANCE = new ClientObservation();

    private boolean active;
    private int bx, by, bz;
    /** Tubo usado na observação e o conjunto desenhado (telescópio completo ou montagem + tubo). */
    private TelescopeType type;
    private Assembly assembly;
    private String title = "";
    private boolean camera;
    private int[] eyepieces = new int[]{25};
    private int stepIndex;
    private float zoom = 1.0F;
    private long lastFrameNanos, startNanos;
    private double maxDistanceSq = 4.5 * 4.5;
    private int savedThirdPerson;

    private float lastSentYaw = Float.NaN, lastSentAlt;
    private int sendCooldown;

    private boolean swapped;
    /** Segurar o botão direito repete o clique a cada 4 ticks: só alterna de novo depois de soltar. */
    private boolean waitRelease;
    private float savedFov, savedSensitivity, savedGamma;
    /** Por que a última observação terminou (diagnóstico do teste automático). */
    public String lastStopReason = "";

    private ClientObservation() {
    }

    public boolean isActive() {
        return active;
    }

    public TelescopeType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    /** true = telescópio que só usa câmera (inteligentes e RASA): zoom digital em vez de oculares. */
    public boolean isCamera() {
        return camera;
    }

    public int[] getEyepieces() {
        return eyepieces;
    }

    public int getStepIndex() {
        return stepIndex;
    }

    public int currentEyepiece() {
        return eyepieces[Math.min(stepIndex, eyepieces.length - 1)];
    }

    public float currentDigitalZoom() {
        return Optics.DIGITAL_ZOOM[Math.min(stepIndex, Optics.DIGITAL_ZOOM.length - 1)];
    }

    /** Segundos desde que a observação começou (empilhamento ao vivo das câmeras). */
    public double secondsObserving() {
        return (System.nanoTime() - startNanos) / 1.0e9;
    }

    /** Só para o teste automático: escolhe a ocular/zoom digital. */
    public void setStepForTest(int index) {
        int count = camera ? Optics.DIGITAL_ZOOM.length : eyepieces.length;
        stepIndex = Math.max(0, Math.min(count - 1, index));
    }

    /** Só para o teste automático: o teste não segura o botão direito. */
    public void releaseForTest() {
        waitRelease = false;
    }

    public void toggle(int x, int y, int z) {
        Minecraft mc = Minecraft.getMinecraft();
        if (waitRelease || mc.theWorld == null) {
            return;
        }
        waitRelease = true;
        if (active && x == bx && y == by && z == bz) {
            stop();
            return;
        }
        TileEntity te = mc.theWorld.getTileEntity(x, y, z);
        if (!(te instanceof TileTelescope)) {
            return;
        }
        TileTelescope tile = (TileTelescope) te;
        TelescopeType tube = tile.opticalTube();
        Assembly a = tile.assembly();
        if (tube == null || a == null) {
            return;
        }
        if (active) {
            stop();
        }
        type = tube;
        assembly = a;
        title = titleOf(mc.theWorld.getBlock(x, y, z), tube);
        camera = tube.design.usesCamera();
        bx = x;
        by = y;
        bz = z;
        eyepieces = Optics.eyepiecesFor(type);
        stepIndex = 0;
        zoom = 1.0F;
        // telescópios gigantes: dá para observar de mais longe (a ocular fica lá no alto)
        double reach = Math.max(4.5, ModelBounds.of(a).height() * 0.6 + 3.0);
        maxDistanceSq = reach * reach;
        lastFrameNanos = startNanos = System.nanoTime();
        lastSentYaw = Float.NaN;
        sendCooldown = 0;
        savedThirdPerson = mc.gameSettings.thirdPersonView;
        mc.gameSettings.thirdPersonView = 0;
        active = true;
        click(0.8F);
    }

    private static String titleOf(Block block, TelescopeType tube) {
        String tubeName = I18n.format("tile." + TelescopioMod.MODID + "." + tube.id + ".name");
        if (block instanceof BlockMount) {
            return tubeName + " · " + I18n.format("tile." + TelescopioMod.MODID + "." + ((BlockMount) block).mount.id + ".name");
        }
        return tubeName;
    }

    public void stop() {
        if (!active) {
            return;
        }
        active = false;
        Minecraft mc = Minecraft.getMinecraft();
        TileTelescope tile = tile();
        if (tile != null) {
            tile.liveAim = false;
            if (mc.thePlayer != null) {
                sendAim(mc.thePlayer.rotationYaw, -mc.thePlayer.rotationPitch);
            }
        }
        mc.gameSettings.thirdPersonView = savedThirdPerson;
        click(0.6F);
    }

    private TileTelescope tile() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) {
            return null;
        }
        TileEntity te = mc.theWorld.getTileEntity(bx, by, bz);
        return te instanceof TileTelescope ? (TileTelescope) te : null;
    }

    private void sendAim(float yaw, float alt) {
        lastSentYaw = yaw;
        lastSentAlt = alt;
        TelescopioMod.network.sendToServer(new MessageAim(bx, by, bz, yaw, alt));
    }

    private static void click(float pitch) {
        Minecraft.getMinecraft().getSoundHandler().playSound(
                PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), pitch));
    }

    private float targetZoom() {
        if (camera) {
            return Optics.cameraZoom(type, currentDigitalZoom());
        }
        return Optics.zoomFor(Optics.magnification(type, currentEyepiece()));
    }

    // ------------------------------------------------------------------ eventos

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (waitRelease && !mc.gameSettings.keyBindUseItem.getIsKeyPressed()) {
            waitRelease = false;
        }
        if (!active) {
            return;
        }
        EntityPlayer player = mc.thePlayer;
        if (player == null || mc.theWorld == null) {
            active = false;
            mc.gameSettings.thirdPersonView = savedThirdPerson;
            return;
        }
        TileTelescope tile = tile();
        Block block = mc.theWorld.getBlock(bx, by, bz);
        boolean valid = tile != null && (block instanceof BlockTelescope || block instanceof BlockMount)
                && tile.opticalTube() == type;
        if (!valid || player.isSneaking() || player.isDead
                || player.getDistanceSq(bx + 0.5, by + 0.5, bz + 0.5) > maxDistanceSq) {
            lastStopReason = !valid ? "bloco" : player.isSneaking() ? "agachou" : player.isDead ? "morreu" : "distancia";
            stop();
            return;
        }
        tile.liveAim = true;
        float yaw = player.rotationYaw, alt = -player.rotationPitch;
        if (--sendCooldown <= 0 && (Float.isNaN(lastSentYaw)
                || Math.abs(Optics.wrapDegrees(yaw - lastSentYaw)) > 0.3F || Math.abs(alt - lastSentAlt) > 0.3F)) {
            sendAim(yaw, alt);
            sendCooldown = 2;
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        GameSettings gs = mc.gameSettings;
        if (event.phase == TickEvent.Phase.START) {
            if (!active || mc.thePlayer == null) {
                return;
            }
            long now = System.nanoTime();
            float dt = Math.min(0.1F, (now - lastFrameNanos) / 1.0e9F);
            lastFrameNanos = now;
            zoom += (targetZoom() - zoom) * Math.min(1.0F, dt * 10.0F);

            if (mc.currentScreen == null || mc.currentScreen instanceof GuiChat) {
                savedFov = gs.fovSetting;
                savedSensitivity = gs.mouseSensitivity;
                savedGamma = gs.gammaSetting;
                gs.fovSetting = savedFov / zoom;
                // sensibilidade efetiva do 1.7.10 = (s*0.6+0.2)^3: divide o giro pelo zoom
                float f = savedSensitivity * 0.6F + 0.2F;
                float fz = f / (float) Math.cbrt(zoom);
                gs.mouseSensitivity = (fz - 0.2F) / 0.6F;
                float boost = Optics.lightGatheringBoost(type);
                if (camera) {
                    boost += Optics.stackingBoost(secondsObserving());
                }
                gs.gammaSetting = savedGamma + boost;
                swapped = true;
            }
        } else if (swapped) {
            gs.fovSetting = savedFov;
            gs.mouseSensitivity = savedSensitivity;
            gs.gammaSetting = savedGamma;
            swapped = false;
        }
    }

    @SubscribeEvent
    public void onMouse(MouseEvent event) {
        if (!active || event.dwheel == 0 || Minecraft.getMinecraft().currentScreen != null) {
            return;
        }
        int count = camera ? Optics.DIGITAL_ZOOM.length : eyepieces.length;
        int next = stepIndex + (event.dwheel > 0 ? 1 : -1);
        next = Math.max(0, Math.min(count - 1, next));
        if (next != stepIndex) {
            stepIndex = next;
            click(1.5F);
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onBlockHighlight(DrawBlockHighlightEvent event) {
        // o contorno do bloco mirado ficaria enorme na imagem ampliada
        if (active) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if (active) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Pre event) {
        if (!active) {
            return;
        }
        switch (event.type) {
            case ALL:
                // O Forge dispara o evento ALL ANTES de trocar para a projeção 2D da interface.
                // Sem isto a máscara era desenhada dentro do mundo 3D e às vezes cobria a tela toda.
                Minecraft.getMinecraft().entityRenderer.setupOverlayRendering();
                ObservationOverlay.draw(this, event.resolution);
                break;
            case HELMET:
            case PORTAL:
            case CROSSHAIRS:
            case BOSSHEALTH:
            case ARMOR:
            case HEALTH:
            case FOOD:
            case AIR:
            case HOTBAR:
            case EXPERIENCE:
            case HEALTHMOUNT:
            case JUMPBAR:
                event.setCanceled(true);
                break;
            default:
                break;
        }
    }
}
