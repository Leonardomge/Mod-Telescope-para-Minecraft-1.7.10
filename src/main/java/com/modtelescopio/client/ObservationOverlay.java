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

/** Máscara circular da ocular + informações ópticas na tela. */
final class ObservationOverlay {
    private static final int SEGMENTS = 96;

    private ObservationOverlay() {
    }

    static void draw(ClientObservation obs, ScaledResolution res) {
        Minecraft mc = Minecraft.getMinecraft();
        int w = res.getScaledWidth(), h = res.getScaledHeight();
        float cx = w * 0.5F, cy = h * 0.5F;
        float r = Math.min(w, h) * 0.46F;
        float outer = (float) Math.sqrt((double) w * w + (double) h * h);

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_LIGHTING_BIT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator t = Tessellator.instance;

        // tudo fora do campo da ocular fica preto
        t.startDrawing(GL11.GL_TRIANGLE_STRIP);
        for (int i = 0; i <= SEGMENTS; i++) {
            double a = 2.0 * Math.PI * i / SEGMENTS;
            float c = (float) Math.cos(a), s = (float) Math.sin(a);
            t.setColorRGBA_F(0.0F, 0.0F, 0.0F, 1.0F);
            t.addVertex(cx + c * r, cy + s * r, 0.0);
            t.addVertex(cx + c * outer, cy + s * outer, 0.0);
        }
        t.draw();

        // borda escurecendo suavemente (vinheta do diafragma de campo)
        t.startDrawing(GL11.GL_TRIANGLE_STRIP);
        for (int i = 0; i <= SEGMENTS; i++) {
            double a = 2.0 * Math.PI * i / SEGMENTS;
            float c = (float) Math.cos(a), s = (float) Math.sin(a);
            t.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.0F);
            t.addVertex(cx + c * r * 0.86F, cy + s * r * 0.86F, 0.0);
            t.setColorRGBA_F(0.0F, 0.0F, 0.0F, 1.0F);
            t.addVertex(cx + c * r, cy + s * r, 0.0);
        }
        t.draw();
        GL11.glPopAttrib();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        drawInfo(obs, mc, w, h);
    }

    private static void drawInfo(ClientObservation obs, Minecraft mc, int w, int h) {
        FontRenderer font = mc.fontRenderer;
        TelescopeType t = obs.getType();
        int fe = obs.currentEyepiece();
        double mag = Optics.magnification(t, fe);

        int y = 6;
        font.drawStringWithShadow(t.displayName, 6, y, 0xFFD27F);
        y += 11;
        font.drawStringWithShadow(I18n.format(t.design.langKey()), 6, y, 0xA0A0A0);

        String[] bottom = {
                eyepieceStrip(obs),
                I18n.format("telescopio.hud.eyepiece", fe, fmt0(mag)),
                I18n.format("telescopio.hud.field", fmt2(Optics.trueField(mag)), fmt1(Optics.exitPupil(t, fe))),
                I18n.format("telescopio.hud.specs", t.aperture, t.focal, fmt1(t.focalRatio())),
                I18n.format("telescopio.hud.limits", fmt1(Optics.limitingMagnitude(t)), fmt2(Optics.dawesLimit(t))),
        };
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
        String help = I18n.format("telescopio.hud.help");
        font.drawStringWithShadow(help, w - 6 - font.getStringWidth(help), 6, 0x9A9A9A);
    }

    /** Lista das oculares com a atual destacada, ex.: "32 [25] 15 9 6". */
    private static String eyepieceStrip(ClientObservation obs) {
        StringBuilder sb = new StringBuilder();
        int[] list = obs.getEyepieces();
        for (int i = 0; i < list.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            if (i == obs.getEyepieceIndex()) {
                sb.append("§e[").append(list[i]).append("]§r");
            } else {
                sb.append("§7").append(list[i]).append("§r");
            }
        }
        sb.append(" mm");
        return sb.toString();
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
