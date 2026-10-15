package com.modtelescopio.client;

import com.modtelescopio.TelescopioMod;
import com.modtelescopio.block.BlockTelescope;
import com.modtelescopio.block.TileTelescope;
import com.modtelescopio.common.Optics;
import com.modtelescopio.common.TelescopeType;
import com.modtelescopio.network.MessageAim;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;

/**
 * Estado da observação no cliente: zoom (FOV), sensibilidade do mouse proporcional ao
 * aumento, ganho de luz, troca de oculares pela roda do mouse e sincronização da mira.
 *
 * <p>O 1.7.10 não tem evento de FOV, então FOV/sensibilidade/gamma são trocados só durante
 * o quadro (RenderTick START) e restaurados no fim (END) - nada é salvo no options.txt.</p>
 */
public final class ClientObservation {
    public static final ClientObservation INSTANCE = new ClientObservation();

    private static final double MAX_DISTANCE_SQ = 4.5 * 4.5;

    private boolean active;
    private int bx, by, bz;
    private TelescopeType type;
    private int[] eyepieces = new int[]{25};
    private int eyepieceIndex;
    private float zoom = 1.0F;
    private long lastFrameNanos;
    private int savedThirdPerson;

    private float lastSentYaw = Float.NaN, lastSentAlt;
    private int sendCooldown;

    private boolean swapped;
    /** Segurar o botão direito repete o clique a cada 4 ticks: só alterna de novo depois de soltar. */
    private boolean waitRelease;
    private float savedFov, savedSensitivity, savedGamma;

    private ClientObservation() {
    }

    public boolean isActive() {
        return active;
    }

    public TelescopeType getType() {
        return type;
    }

    public int[] getEyepieces() {
        return eyepieces;
    }

    public int getEyepieceIndex() {
        return eyepieceIndex;
    }

    public int currentEyepiece() {
        return eyepieces[eyepieceIndex];
    }

    public void toggle(int x, int y, int z) {
        Minecraft mc = Minecraft.getMinecraft();
        if (waitRelease) {
            return;
        }
        waitRelease = true;
        if (active && x == bx && y == by && z == bz) {
            stop();
            return;
        }
        Block block = mc.theWorld.getBlock(x, y, z);
        if (!(block instanceof BlockTelescope)) {
            return;
        }
        if (active) {
            stop();
        }
        type = ((BlockTelescope) block).type;
        bx = x;
        by = y;
        bz = z;
        eyepieces = Optics.eyepiecesFor(type);
        eyepieceIndex = 0;
        zoom = 1.0F;
        lastFrameNanos = System.nanoTime();
        lastSentYaw = Float.NaN;
        sendCooldown = 0;
        savedThirdPerson = mc.gameSettings.thirdPersonView;
        mc.gameSettings.thirdPersonView = 0;
        active = true;
        click(0.8F);
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
        if (tile == null || player.isSneaking() || player.isDead
                || player.getDistanceSq(bx + 0.5, by + 0.5, bz + 0.5) > MAX_DISTANCE_SQ) {
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
            float target = Optics.zoomFor(Optics.magnification(type, currentEyepiece()));
            zoom += (target - zoom) * Math.min(1.0F, dt * 10.0F);

            if (mc.currentScreen == null || mc.currentScreen instanceof GuiChat) {
                savedFov = gs.fovSetting;
                savedSensitivity = gs.mouseSensitivity;
                savedGamma = gs.gammaSetting;
                gs.fovSetting = savedFov / zoom;
                // sensibilidade efetiva do 1.7.10 = (s*0.6+0.2)^3: divide o giro pelo zoom
                float f = savedSensitivity * 0.6F + 0.2F;
                float fz = f / (float) Math.cbrt(zoom);
                gs.mouseSensitivity = (fz - 0.2F) / 0.6F;
                gs.gammaSetting = savedGamma + Optics.lightGatheringBoost(type);
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
        int next = eyepieceIndex + (event.dwheel > 0 ? 1 : -1);
        next = Math.max(0, Math.min(eyepieces.length - 1, next));
        if (next != eyepieceIndex) {
            eyepieceIndex = next;
            click(1.5F);
        }
        event.setCanceled(true);
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
