package com.modtelescopio.client;

import com.modtelescopio.common.Optics;
import com.modtelescopio.common.TelescopeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

import java.util.Locale;

/**
 * Máscara da ocular (círculo) ou da câmera (retângulo do sensor) + informações na tela.
 * Precisa ser chamada com a projeção 2D da interface já configurada.
 */
final class ObservationOverlay {
    private static final int SEGMENTS = 96;

    private ObservationOverlay() {
    }

    static void draw(ClientObservation obs, ScaledResolution res) {
        Minecraft mc = Minecraft.getMinecraft();
        int w = res.getScaledWidth(), h = res.getScaledHeight();

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_LIGHTING_BIT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        if (obs.isCamera()) {
            cameraMask(obs.getType(), w, h);
        } else {
            eyepieceMask(w, h);
        }
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glPopAttrib();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        drawInfo(obs, mc, w, h);
    }

    /** Tudo fora do campo circular fica preto, com a borda escurecendo suavemente. */
    private static void eyepieceMask(int w, int h) {
        float cx = w * 0.5F, cy = h * 0.5F;
        float r = Math.min(w, h) * 0.46F;
        float outer = (float) Math.sqrt((double) w * w + (double) h * h);
        ring(cx, cy, r, outer, 1.0F, 1.0F);
        ring(cx, cy, r * 0.86F, r, 0.0F, 1.0F);
    }

    private static void ring(float cx, float cy, float rIn, float rOut, float aIn, float aOut) {
        Tessellator t = Tessellator.instance;
        t.startDrawing(GL11.GL_TRIANGLE_STRIP);
        for (int i = 0; i <= SEGMENTS; i++) {
            double a = 2.0 * Math.PI * i / SEGMENTS;
            float c = (float) Math.cos(a), s = (float) Math.sin(a);
            t.setColorRGBA_F(0.0F, 0.0F, 0.0F, aIn);
            t.addVertex(cx + c * rIn, cy + s * rIn, 0.0);
            t.setColorRGBA_F(0.0F, 0.0F, 0.0F, aOut);
            t.addVertex(cx + c * rOut, cy + s * rOut, 0.0);
        }
        t.draw();
    }

    /** Quadro do sensor (proporção real), bordas finas, cantos e mira central. */
    private static void cameraMask(TelescopeType type, int w, int h) {
        float aspect = type.sensorW / type.sensorH;
        float fh = h * 0.84F, fw = fh * aspect;
        if (fw > w * 0.94F) {
            fw = w * 0.94F;
            fh = fw / aspect;
        }
        float x0 = (w - fw) * 0.5F, y0 = (h - fh) * 0.5F, x1 = x0 + fw, y1 = y0 + fh;
        rect(0, 0, w, y0, 0x000000, 1.0F);
        rect(0, y1, w, h, 0x000000, 1.0F);
        rect(0, y0, x0, y1, 0x000000, 1.0F);
        rect(x1, y0, w, y1, 0x000000, 1.0F);
        // borda do quadro
        rect(x0, y0, x1, y0 + 1, 0xFFFFFF, 0.35F);
        rect(x0, y1 - 1, x1, y1, 0xFFFFFF, 0.35F);
        rect(x0, y0, x0 + 1, y1, 0xFFFFFF, 0.35F);
        rect(x1 - 1, y0, x1, y1, 0xFFFFFF, 0.35F);
        // cantos
        float c = Math.min(fw, fh) * 0.08F;
        corner(x0 + 4, y0 + 4, c, 1, 1);
        corner(x1 - 4, y0 + 4, c, -1, 1);
        corner(x0 + 4, y1 - 4, c, 1, -1);
        corner(x1 - 4, y1 - 4, c, -1, -1);
        // mira central
        float cx = w * 0.5F, cy = h * 0.5F;
        rect(cx - 6, cy - 0.5F, cx - 2, cy + 0.5F, 0xFFFFFF, 0.6F);
        rect(cx + 2, cy - 0.5F, cx + 6, cy + 0.5F, 0xFFFFFF, 0.6F);
        rect(cx - 0.5F, cy - 6, cx + 0.5F, cy - 2, 0xFFFFFF, 0.6F);
        rect(cx - 0.5F, cy + 2, cx + 0.5F, cy + 6, 0xFFFFFF, 0.6F);
        // indicador "ao vivo"
        rect(x0 + 8, y0 + 8, x0 + 13, y0 + 13, 0xE53935, 0.9F);
    }

    private static void corner(float x, float y, float len, int sx, int sy) {
        rect(Math.min(x, x + sx * len), y - 0.5F * sy - 0.5F, Math.max(x, x + sx * len), y + 0.5F * sy + 0.5F, 0xFFFFFF, 0.8F);
        rect(x - 0.5F, Math.min(y, y + sy * len), x + 1.0F, Math.max(y, y + sy * len), 0xFFFFFF, 0.8F);
    }

    private static void rect(float x0, float y0, float x1, float y1, int rgb, float alpha) {
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.setColorRGBA_F(((rgb >> 16) & 255) / 255F, ((rgb >> 8) & 255) / 255F, (rgb & 255) / 255F, alpha);
        t.addVertex(x0, y1, 0.0);
        t.addVertex(x1, y1, 0.0);
        t.addVertex(x1, y0, 0.0);
        t.addVertex(x0, y0, 0.0);
        t.draw();
    }

    private static void drawInfo(ClientObservation obs, Minecraft mc, int w, int h) {
        FontRenderer font = mc.fontRenderer;
        TelescopeType t = obs.getType();

        int y = 6;
        font.drawStringWithShadow(obs.getTitle(), 6, y, 0xFFD27F);
        y += 11;
        font.drawStringWithShadow(I18n.format(t.design.langKey()), 6, y, 0xA0A0A0);

        String[] bottom;
        if (obs.isCamera()) {
            float dz = obs.currentDigitalZoom();
            double secs = obs.secondsObserving();
            bottom = new String[]{
                    zoomStrip(obs),
                    I18n.format("telescopio.hud.sensor", fmt1(t.sensorW), fmt1(t.sensorH), fmtZoom(dz)),
                    I18n.format("telescopio.hud.camfield", fmt2(Optics.cameraFieldW(t) / dz),
                            fmt2(Optics.cameraFieldH(t) / dz)),
                    I18n.format("telescopio.hud.specs", t.aperture, t.focal, fmt1(t.focalRatio())),
                    I18n.format("telescopio.hud.stack", (int) (secs / Optics.STACK_EXPOSURE_S), fmt0(secs)),
            };
        } else {
            int fe = obs.currentEyepiece();
            double mag = Optics.magnification(t, fe);
            bottom = new String[]{
                    eyepieceStrip(obs),
                    I18n.format("telescopio.hud.eyepiece", fe, fmt0(mag)),
                    I18n.format("telescopio.hud.field", fmt2(Optics.trueField(mag)), fmt1(Optics.exitPupil(t, fe))),
                    I18n.format("telescopio.hud.specs", t.aperture, t.focal, fmt1(t.focalRatio())),
                    I18n.format("telescopio.hud.limits", fmt1(Optics.limitingMagnitude(t)), fmt2(Optics.dawesLimit(t))),
            };
        }
        y = h - 8 - bottom.length * 11;
        for (String line : bottom) {
            font.drawStringWithShadow(line, 6, y, 0xFFFFFF);
            y += 11;
        }

        if (mc.thePlayer != null) {
            String aim = I18n.format("telescopio.hud.aim", fmt0(Optics.compassAzimuth(mc.thePlayer.rotationYaw)),
                    fmt0(-mc.thePlayer.rotationPitch));
            font.drawStringWithShadow(aim, w - 6 - font.getStringWidth(aim), h - 19, 0xDDDDDD);
        }
        String help = I18n.format(obs.isCamera() ? "telescopio.hud.help_camera" : "telescopio.hud.help");
        font.drawStringWithShadow(help, w - 6 - font.getStringWidth(help), 30, 0x9A9A9A);
    }

    /** Lista das oculares com a atual destacada, ex.: "32 [25] 15 9 6 mm". */
    private static String eyepieceStrip(ClientObservation obs) {
        StringBuilder sb = new StringBuilder();
        int[] list = obs.getEyepieces();
        for (int i = 0; i < list.length; i++) {
            append(sb, i, obs.getStepIndex(), String.valueOf(list[i]));
        }
        sb.append(" mm");
        return sb.toString();
    }

    /** Passos de zoom digital com o atual destacado, ex.: "1x [2x] 3x". */
    private static String zoomStrip(ClientObservation obs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Optics.DIGITAL_ZOOM.length; i++) {
            append(sb, i, obs.getStepIndex(), fmtZoom(Optics.DIGITAL_ZOOM[i]) + "x");
        }
        return sb.toString();
    }

    private static void append(StringBuilder sb, int i, int selected, String text) {
        if (i > 0) {
            sb.append(' ');
        }
        if (i == selected) {
            sb.append("§e[").append(text).append("]§r");
        } else {
            sb.append("§7").append(text).append("§r");
        }
    }

    private static String fmtZoom(float z) {
        return z == (int) z ? String.valueOf((int) z) : String.format(Locale.ROOT, "%.1f", z);
    }

    private static String fmt0(double v) {
        return String.format(Locale.ROOT, "%.0f", v);
    }

    private static String fmt1(double v) {
        return String.format(Locale.ROOT, "%.1f", v);
    }

    private static String fmt2(double v) {
        return String.format(Locale.ROOT, "%.2f", v);
    }
}
