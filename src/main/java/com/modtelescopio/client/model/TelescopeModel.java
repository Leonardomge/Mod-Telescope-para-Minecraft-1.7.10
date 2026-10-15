package com.modtelescopio.client.model;

import com.modtelescopio.common.MountType;
import com.modtelescopio.common.OpticalDesign;
import com.modtelescopio.common.Optics;
import com.modtelescopio.common.TelescopeType;

/**
 * Modelo 3D procedural dos telescópios: montagem + tubo óptico, montados a partir das
 * medidas reais do catálogo. Origem no centro da base do bloco; com yaw 0 a frente do
 * telescópio aponta para +Z (sul, como no Minecraft).
 *
 * <p>Convenção dos tubos ("quadro canônico"): eixo óptico em +Z (frente = abertura),
 * montagem do lado -Y.</p>
 */
public final class TelescopeModel {
    /** Blocos por metro: os telescópios ficam um pouco menores que o real para caber no mundo. */
    public static final float SCALE = 0.8f;

    static final int GLASS = 0x1B2D4A;
    static final int GLASS_HI = 0x6F93C4;
    static final int MIRROR = 0xD6DADF;
    static final int BLACK = 0x161618;
    static final int DARK = 0x2A2B2F;
    static final int SILVER = 0xB9BDC3;
    static final int RUBBER = 0x0D0D0F;

    private TelescopeModel() {
    }

    /**
     * @param facingYaw yaw da frente do bloco (graus, convenção do Minecraft)
     * @param aimYaw    yaw para onde o tubo aponta
     * @param aimAlt    altura do tubo acima do horizonte (graus, positivo para cima)
     */
    public static void build(ModelBuilder mb, TelescopeType t, float facingYaw, float aimYaw, float aimAlt) {
        float rel = Optics.wrapDegrees(aimYaw - facingYaw);
        float alt = Optics.clampAltitude(aimAlt);
        mb.push();
        mb.rotateY(-facingYaw);
        switch (t.mount.kind) {
            case GEM:
                equatorial(mb, t, rel, alt);
                break;
            case DOBSONIAN:
                dobsonian(mb, t, rel, alt);
                break;
            case FORK_DOUBLE:
                forkDouble(mb, t, rel, alt);
                break;
            case FORK_SINGLE:
                forkSingle(mb, t, rel, alt);
                break;
            default:
                altAz(mb, t, rel, alt);
                break;
        }
        mb.pop();
    }

    static float radius(TelescopeType t) {
        return Math.max(0.035f, t.tubeDiameterM * SCALE * 0.5f);
    }

    static float length(TelescopeType t) {
        return t.tubeLengthM * SCALE;
    }

    // =====================================================================
    // Montagens
    // =====================================================================

    /** Montagem equatorial alemã com cinemática real: eixo polar + eixo de declinação + contrapesos. */
    private static void equatorial(ModelBuilder mb, TelescopeType t, float rel, float alt) {
        MountType m = t.mount;
        int body = t.mountColor;
        int bodyDark = shade(body, 0.78f);
        tripod(mb, m.headY, m.spread, m.legT, t.legColor, DARK, true);

        float baseY = m.headY;
        mb.cylinderY(m.rRA * 1.4f, baseY, baseY + 0.035f, 18, body);
        // parafusos de ajuste de azimute
        mb.box(-m.rRA * 0.9f, baseY + 0.008f, -m.rRA * 1.75f, -m.rRA * 0.6f, baseY + 0.028f, -m.rRA * 1.2f, BLACK);
        mb.box(m.rRA * 0.6f, baseY + 0.008f, -m.rRA * 1.75f, m.rRA * 0.9f, baseY + 0.028f, -m.rRA * 1.2f, BLACK);

        float qY = baseY + 0.035f + m.rRA * 1.15f;
        float bx0 = m.rRA * 1.02f, bx1 = m.rRA * 1.3f;
        mb.box(-bx1, baseY + 0.03f, -m.rRA, -bx0, qY + m.rRA * 0.5f, m.rRA, body);
        mb.box(bx0, baseY + 0.03f, -m.rRA, bx1, qY + m.rRA * 0.5f, m.rRA, body);
        mb.push();
        mb.translate(0f, qY, 0f);
        mb.cylinderX(m.rRA * 0.32f, -bx1 - 0.012f, bx1 + 0.012f, 10, SILVER);
        mb.pop();

        // eixo polar inclinado para a frente/cima: +Y local aponta para o polo
        float beta = 90f - Optics.LATITUDE;
        double r = Math.toRadians(rel), a = Math.toRadians(alt);
        double vx = -Math.sin(r) * Math.cos(a), vy = Math.sin(a), vz = Math.cos(r) * Math.cos(a);
        double b = Math.toRadians(beta), cb = Math.cos(b), sb = Math.sin(b);
        // direção do alvo no referencial do eixo polar
        double px = vx, py = vy * cb + vz * sb, pz = -vy * sb + vz * cb;
        double gamma = Math.acos(Math.max(-1.0, Math.min(1.0, py)));
        // apontando para o polo o ângulo horário fica indefinido: usa a posição "home" (contrapeso para baixo)
        double ra = Math.hypot(px, pz) < 1e-4 ? Math.PI : Math.atan2(pz, -px);
        // das duas soluções, usa a que deixa os contrapesos para baixo (como no "meridian flip")
        if (Math.cos(ra) * sb > 0) {
            ra += Math.PI;
            gamma = -gamma;
        }

        mb.push();
        mb.translate(0f, qY, 0f);
        mb.rotateX(beta);
        float raBottom = -m.lRA * 0.4f, raTop = m.lRA * 0.6f;
        mb.cylinderY(m.rRA, raBottom, raTop, 18, body);
        mb.cylinderY(m.rRA * 1.07f, raTop - 0.014f, raTop, 18, bodyDark);
        mb.cylinderY(m.rRA * 0.42f, raBottom - 0.025f, raBottom, 10, BLACK);
        mb.box(-m.rRA * 1.75f, -m.lRA * 0.25f, -m.rRA * 0.65f, -m.rRA * 0.85f, m.lRA * 0.25f, m.rRA * 0.65f, bodyDark);

        // eixo de declinação
        mb.translate(0f, raTop + m.rDec, 0f);
        mb.rotateY((float) Math.toDegrees(ra));
        float d0 = -m.lDec * 0.5f, d1 = m.lDec * 0.5f;
        mb.cylinderZ(m.rDec, d0, d1, 18, body, true, true);
        mb.box(m.rDec * 0.8f, -m.rDec * 0.65f, -m.lDec * 0.28f, m.rDec * 1.65f, m.rDec * 0.65f, m.lDec * 0.22f, bodyDark);

        // haste e contrapesos
        float shaftEnd = d0 - m.shaft;
        mb.cylinderZ(m.shaftR, shaftEnd, d0, 10, SILVER, true, false);
        for (int k = 0; k < m.nCW; k++) {
            float zc = d0 - m.shaft * (0.55f + 0.24f * k);
            mb.cylinderZ(m.rCW, zc - m.tCW * 0.5f, zc + m.tCW * 0.5f, 20, t.legColor, true, true);
            mb.cylinderZ(m.shaftR * 1.6f, zc - m.tCW * 0.5f - 0.004f, zc + m.tCW * 0.5f + 0.004f, 8, BLACK, true, true);
        }
        mb.cylinderZ(m.shaftR * 2f, shaftEnd - 0.012f, shaftEnd, 10, BLACK, true, true);

        // sela (dovetail) e tubo
        mb.translate(0f, 0f, d1);
        mb.rotateZ((float) Math.toDegrees(gamma));
        float saddleL = clamp(length(t) * 0.25f, 0.08f, 0.16f);
        mb.box(-0.03f, -saddleL * 0.5f, 0f, 0.03f, saddleL * 0.5f, 0.022f, bodyDark);
        mb.box(0.03f, -0.015f, 0.004f, 0.046f, 0.015f, 0.018f, BLACK);
        // do referencial da declinação (tubo em +Y) para o quadro canônico (tubo em +Z)
        mb.rotateX(-90f);
        mb.rotateZ(180f);
        ota(mb, t, 0.034f + radius(t), 0.5f);
        mb.pop();
    }

    /** Base Dobsoniana (rocker box) sobre a placa de chão. */
    private static void dobsonian(ModelBuilder mb, TelescopeType t, float rel, float alt) {
        float R = radius(t), L = length(t);
        float bal = 0.36f;
        float hp = L * bal + 0.08f;
        int base = t.mountColor;
        float sx0 = R + 0.03f, sx1 = sx0 + 0.025f;
        float d = Math.max(R * 0.95f + 0.05f, 0.14f);
        float gr = (float) Math.sqrt(sx1 * sx1 + d * d) + 0.02f;

        mb.cylinderY(gr, 0.012f, 0.032f, 28, base);
        for (int k = 0; k < 3; k++) {
            double ang = Math.toRadians(60 + k * 120);
            float fx = gr * 0.8f * (float) Math.sin(ang), fz = gr * 0.8f * (float) Math.cos(ang);
            mb.box(fx - 0.025f, 0f, fz - 0.025f, fx + 0.025f, 0.012f, fz + 0.025f, RUBBER);
        }

        mb.push();
        mb.rotateY(-rel);
        mb.box(-sx1, 0.032f, -d, sx1, 0.058f, d, base);
        mb.box(-sx1, 0.058f, -d, -sx0, hp + 0.02f, d, base);
        mb.box(sx0, 0.058f, -d, sx1, hp + 0.02f, d, base);
        mb.box(-sx0, 0.058f, d - 0.022f, sx0, 0.058f + hp * 0.45f, d, base);
        // alças
        mb.box(sx1, hp * 0.55f, -0.04f, sx1 + 0.012f, hp * 0.55f + 0.02f, 0.04f, DARK);
        mb.box(-sx1 - 0.012f, hp * 0.55f, -0.04f, -sx1, hp * 0.55f + 0.02f, 0.04f, DARK);
        if (t.goTo) {
            mb.box(sx1, 0.07f, -0.06f, sx1 + 0.05f, 0.15f, 0.06f, BLACK);
            mb.box(-sx1 - 0.05f, hp - 0.06f, -0.05f, -sx1, hp + 0.02f, 0.05f, BLACK);
        }

        mb.translate(0f, hp, 0f);
        mb.rotateX(-alt);
        float br = Math.min(0.09f, R * 0.55f + 0.02f);
        mb.cylinderX(br, R * 0.9f, sx0 - 0.002f, 20, DARK);
        mb.cylinderX(br, -(sx0 - 0.002f), -R * 0.9f, 20, DARK);
        ota(mb, t, 0f, bal);
        mb.pop();
    }

    /** Garfo de dois braços (Meade LX200/LX90/ETX, Celestron CPC). */
    private static void forkDouble(ModelBuilder mb, TelescopeType t, float rel, float alt) {
        MountType m = t.mount;
        float R = radius(t), L = length(t);
        boolean small = m == MountType.FORK_DOUBLE_SMALL;
        float bal = 0.42f;
        tripod(mb, m.headY, m.spread, m.legT, t.legColor, DARK, true);

        mb.push();
        mb.rotateY(-rel);
        int body = t.mountColor;
        float baseY = m.headY;
        float gap = small ? 0.012f : 0.02f;
        float armT = small ? 0.028f : 0.045f;
        float ax0 = R + gap, ax1 = ax0 + armT;
        float hb = small ? 0.055f : 0.08f;
        float rb = ax1 + 0.01f;
        mb.cylinderY(rb, baseY, baseY + hb, 28, body);
        mb.cylinderY(rb * 1.015f, baseY + hb - 0.01f, baseY + hb, 28, shade(body, 1.3f));
        mb.box(-0.045f, baseY + 0.014f, rb * 0.82f, 0.045f, baseY + hb - 0.016f, rb + 0.006f, DARK);

        float clear = L * bal + (small ? 0.06f : 0.09f);
        float hp = baseY + hb + clear;
        float armD = Math.max(0.08f, R * 0.95f);
        mb.box(-ax1, baseY + hb, -armD * 0.5f, -ax0, hp, armD * 0.5f, body);
        mb.box(ax0, baseY + hb, -armD * 0.5f, ax1, hp, armD * 0.5f, body);

        mb.translate(0f, hp, 0f);
        mb.cylinderX(armD * 0.5f, -ax1, -ax0, 18, body);
        mb.cylinderX(armD * 0.5f, ax0, ax1, 18, body);
        mb.cylinderX(0.02f, ax1, ax1 + 0.016f, 12, BLACK);
        mb.cylinderX(0.02f, -ax1 - 0.016f, -ax1, 12, BLACK);
        mb.rotateX(-alt);
        mb.cylinderX(0.028f, -ax0, -R * 0.96f, 14, SILVER);
        mb.cylinderX(0.028f, R * 0.96f, ax0, 14, SILVER);
        ota(mb, t, 0f, bal);
        mb.pop();
    }

    /** Braço único lateral (Celestron NexStar SE/SLT, Sky-Watcher AZ-GTi). */
    private static void forkSingle(ModelBuilder mb, TelescopeType t, float rel, float alt) {
        MountType m = t.mount;
        float R = radius(t), L = length(t);
        float bal = 0.45f;
        tripod(mb, m.headY, m.spread, m.legT, t.legColor, DARK, true);

        mb.push();
        mb.rotateY(-rel);
        int body = t.mountColor;
        float baseY = m.headY;
        float rb = 0.07f, hb = 0.05f;
        mb.cylinderY(rb, baseY, baseY + hb, 20, body);
        float armT = 0.042f, armD = 0.1f;
        float hp = baseY + hb + Math.max(L * bal + 0.07f, 0.16f);
        float armH = hp - baseY - hb;
        mb.box(-armT * 0.5f, baseY + hb, -armD * 0.5f, armT * 0.5f, hp, armD * 0.5f, body);
        mb.push();
        mb.translate(0f, hp, 0f);
        mb.cylinderX(armD * 0.5f, -armT * 0.5f, armT * 0.5f, 16, body);
        mb.pop();
        // controle de mão no suporte do braço
        float hy = baseY + hb + armH * 0.35f;
        mb.box(armT * 0.5f, hy, -0.022f, armT * 0.5f + 0.022f, hy + 0.075f, 0.022f, DARK);
        mb.box(armT * 0.5f + 0.022f, hy + 0.045f, -0.014f, armT * 0.5f + 0.024f, hy + 0.065f, 0.014f, 0x5FD07A);

        float tubeX = -(armT * 0.5f + 0.014f + R);
        mb.translate(tubeX, hp, 0f);
        mb.rotateX(-alt);
        float armX = -tubeX;
        mb.box(R * 0.8f, -0.022f, -0.06f, armX - armT * 0.5f, 0.022f, 0.06f, shade(body, 0.8f));
        mb.cylinderX(0.026f, armX + armT * 0.5f, armX + armT * 0.5f + 0.01f, 14, SILVER);
        mb.box(R - 0.005f, -0.018f, -L * 0.3f, R + 0.012f, 0.018f, L * 0.3f, DARK);
        ota(mb, t, 0f, bal);
        mb.pop();
    }

    /** Montagem altazimutal simples de tripé fotográfico (refratores de entrada). */
    private static void altAz(ModelBuilder mb, TelescopeType t, float rel, float alt) {
        MountType m = t.mount;
        float R = radius(t);
        tripod(mb, m.headY, m.spread, m.legT, t.legColor, DARK, true);

        mb.push();
        mb.rotateY(-rel);
        int body = t.mountColor;
        float y0 = m.headY;
        mb.cylinderY(0.04f, y0, y0 + 0.022f, 14, body);
        float hp = y0 + 0.085f;
        mb.box(-0.034f, y0 + 0.022f, -0.022f, -0.022f, hp + 0.018f, 0.022f, body);
        mb.box(0.022f, y0 + 0.022f, -0.022f, 0.034f, hp + 0.018f, 0.022f, body);
        mb.translate(0f, hp, 0f);
        mb.rotateX(-alt);
        mb.cylinderX(0.01f, -0.046f, 0.046f, 8, SILVER);
        mb.cylinderX(0.016f, 0.046f, 0.056f, 10, BLACK);
        mb.box(-0.02f, -0.012f, -0.05f, 0.02f, 0.012f, 0.05f, body);
        // cabo de movimento lento
        mb.rodRound(0.02f, -0.004f, -0.03f, 0.05f, -0.11f, -0.3f, 0.007f, 8, SILVER);
        mb.rodRound(0.045f, -0.092f, -0.25f, 0.05f, -0.11f, -0.3f, 0.011f, 8, BLACK);
        ota(mb, t, 0.024f + R, 0.45f);
        mb.pop();
    }

    /** Tripé de três pernas com bandeja de acessórios. */
    static void tripod(ModelBuilder mb, float headY, float spread, float legT, int legColor, int headColor, boolean tray) {
        float r0 = 0.05f + legT * 0.4f;
        float topY = headY - 0.035f;
        int lower = shade(legColor, 1.12f);
        mb.cylinderY(r0 + legT * 0.6f, topY, headY, 16, headColor);
        float trayY = headY * 0.42f;
        float[] tx = new float[3], tz = new float[3];
        for (int k = 0; k < 3; k++) {
            double phi = Math.toRadians(k * 120.0);
            float sx = (float) Math.sin(phi), sz = (float) Math.cos(phi);
            float ax = sx * r0, az = sz * r0, fx = sx * spread, fz = sz * spread;
            float f = 0.55f;
            float mx = ax + (fx - ax) * f, my = topY * (1f - f), mz = az + (fz - az) * f;
            mb.rod(ax, topY, az, mx, my, mz, legT, legColor);
            mb.rod(mx, my, mz, fx, 0f, fz, legT * 0.72f, lower);
            mb.box(mx - legT * 0.7f, my - 0.018f, mz - legT * 0.7f, mx + legT * 0.7f, my + 0.018f, mz + legT * 0.7f, BLACK);
            mb.box(fx - legT * 0.55f, 0f, fz - legT * 0.55f, fx + legT * 0.55f, 0.014f, fz + legT * 0.55f, RUBBER);
            float tt = (topY - trayY) / topY;
            tx[k] = ax + (fx - ax) * tt;
            tz[k] = az + (fz - az) * tt;
        }
        if (tray) {
            for (int k = 0; k < 3; k++) {
                mb.rod(0f, trayY, 0f, tx[k], trayY, tz[k], legT * 0.45f, DARK);
            }
            mb.cylinderY(legT * 0.9f + 0.015f, trayY - 0.01f, trayY + 0.01f, 12, DARK);
        }
    }

    // =====================================================================
    // Tubos ópticos (quadro canônico)
    // =====================================================================

    /**
     * Desenha o tubo óptico. {@code yOff} é a distância do ponto de fixação até o eixo do tubo
     * (0 quando o eixo de altura passa pelo próprio tubo); {@code bal} é a fração do comprimento
     * atrás do ponto de fixação.
     */
    static void ota(ModelBuilder mb, TelescopeType t, float yOff, float bal) {
        float R = radius(t), L = length(t);
        float zr = -L * bal, zf = L * (1f - bal);
        mb.push();
        mb.translate(0f, yOff, 0f);
        switch (t.design) {
            case REFRACTOR_ACHROMAT:
            case REFRACTOR_APO:
                refractor(mb, t, R, L, zr, zf);
                break;
            case NEWTONIAN:
                newtonian(mb, t, R, L, zr, zf);
                break;
            case NEWTONIAN_TRUSS:
                truss(mb, t, R, L, zr, zf);
                break;
            case MAKSUTOV:
                maksutov(mb, t, R, L, zr, zf);
                break;
            default:
                sct(mb, t, R, L, zr, zf);
                break;
        }
        if (yOff > 0.001f) {
            saddleHardware(mb, t, R, L, zr, zf);
        }
        mb.pop();
    }

    /** Barra dovetail e anéis do tubo (quando o tubo fica apoiado sobre a montagem). */
    private static void saddleHardware(ModelBuilder mb, TelescopeType t, float R, float L, float zr, float zf) {
        float zc = (zr + zf) * 0.5f;
        float half = Math.max(0.06f, L * 0.28f);
        mb.box(-0.022f, -R - 0.013f, zc - half, 0.022f, -R + 0.004f, zc + half, DARK);
        boolean rings = t.design != OpticalDesign.SCHMIDT_CASSEGRAIN && t.design != OpticalDesign.MAKSUTOV;
        if (rings) {
            float ringR = R * 1.06f + 0.004f;
            int ringColor = t.accentColor;
            for (int s = -1; s <= 1; s += 2) {
                float z = zc + s * half * 0.75f;
                mb.tubeZ(ringR, R * 0.99f, z - 0.013f, z + 0.013f, 22, ringColor, ringColor, true, true);
                mb.box(-0.014f, -ringR - 0.006f, z - 0.013f, 0.014f, -R * 0.96f, z + 0.013f, ringColor);
            }
        }
    }

    private static void refractor(ModelBuilder mb, TelescopeType t, float R, float L, float zr, float zf) {
        boolean apo = t.design == OpticalDesign.REFRACTOR_APO;
        int tube = t.tubeColor, acc = t.accentColor;
        float dew = L * 0.24f, rDew = R * 1.14f;
        float zObj = zf - dew;
        mb.cylinderZ(R, zr + 0.025f, zObj - 0.02f, 20, tube, false, false);
        // célula da objetiva e para-sol
        mb.cylinderZ(R * 1.07f, zObj - 0.03f, zObj + 0.004f, 20, acc, true, false);
        mb.tubeZ(rDew, rDew * 0.93f, zObj, zf - 0.012f, 20, tube, BLACK, true, false);
        mb.tubeZ(rDew * 1.015f, rDew * 0.93f, zf - 0.012f, zf, 20, acc, BLACK, false, true);
        mb.discZ(R * 1.0f, zObj + 0.005f, 20, GLASS, true);
        glint(mb, R, zObj + 0.0065f);
        // traseira e focalizador
        mb.cylinderZ(R * 1.03f, zr, zr + 0.025f, 20, acc, true, false);
        float fr = clamp(R * 0.55f, 0.018f, 0.045f);
        mb.cylinderZ(fr, zr - 0.05f, zr, 14, apo ? acc : BLACK, true, false);
        mb.push();
        mb.translate(0f, -fr * 0.2f, zr - 0.028f);
        mb.cylinderX(fr * 0.32f, -fr * 2.1f, fr * 2.1f, 10, BLACK);
        mb.cylinderX(fr * 0.55f, -fr * 2.1f, -fr * 1.6f, 12, BLACK);
        mb.cylinderX(fr * 0.55f, fr * 1.6f, fr * 2.1f, 12, BLACK);
        mb.pop();
        mb.cylinderZ(fr * 0.72f, zr - 0.09f, zr - 0.05f, 12, SILVER, true, false);
        diagonal(mb, zr - 0.09f, fr * 0.8f);
        finder(mb, R, zr + L * 0.16f, L);
    }

    private static void newtonian(ModelBuilder mb, TelescopeType t, float R, float L, float zr, float zf) {
        int tube = t.tubeColor, acc = t.accentColor;
        float rIn = R * 0.93f;
        mb.tubeZ(R, rIn, zr + 0.022f, zf - 0.014f, 24, tube, BLACK, false, false);
        mb.tubeZ(R * 1.025f, rIn, zf - 0.014f, zf, 24, acc, BLACK, true, true);
        mirrorCell(mb, R, rIn, zr, acc);
        float zs = zf - Math.max(R * 1.1f, 0.06f);
        spider(mb, rIn, zs, R * 0.24f);
        float fr = clamp(R * 0.2f, 0.018f, 0.04f);
        mb.push();
        mb.translate(0f, 0f, zs);
        mb.rotateZ(35f);
        focuserRadial(mb, R, fr, acc);
        mb.pop();
        mb.push();
        mb.rotateZ(-40f);
        finder(mb, R, zf - L * 0.2f, L);
        mb.pop();
    }

    private static void truss(ModelBuilder mb, TelescopeType t, float R, float L, float zr, float zf) {
        int tube = t.tubeColor, acc = t.accentColor;
        float rIn = R * 0.93f;
        float zLow = zr + L * 0.36f, zCage = zf - L * 0.2f;
        mb.tubeZ(R, rIn, zr + 0.022f, zLow, 24, tube, BLACK, false, true);
        mirrorCell(mb, R, rIn, zr, tube);
        mb.tubeZ(R * 1.03f, rIn, zLow - 0.03f, zLow, 24, DARK, BLACK, true, true);
        mb.tubeZ(R, rIn, zCage, zf, 24, tube, BLACK, true, true);
        // três pares de tubos paralelos (o "Flex" do Flextube)
        float rr = R + 0.006f;
        float strut = Math.max(0.007f, R * 0.045f);
        for (int k = 0; k < 3; k++) {
            for (int s = -1; s <= 1; s += 2) {
                double ang = Math.toRadians(90 + k * 120 + s * 10);
                mb.push();
                mb.translate(rr * (float) Math.cos(ang), rr * (float) Math.sin(ang), 0f);
                mb.cylinderZ(strut, zLow - 0.02f, zCage + 0.02f, 8, acc, false, false);
                mb.pop();
            }
            double ang = Math.toRadians(90 + k * 120);
            mb.push();
            mb.translate((R + 0.012f) * (float) Math.cos(ang), (R + 0.012f) * (float) Math.sin(ang), zLow - 0.03f);
            mb.box(-0.02f, -0.02f, -0.02f, 0.02f, 0.02f, 0.03f, BLACK);
            mb.pop();
        }
        float zs = zf - Math.max(R * 0.9f, 0.06f);
        spider(mb, rIn, zs, R * 0.24f);
        float fr = clamp(R * 0.17f, 0.02f, 0.04f);
        mb.push();
        mb.translate(0f, 0f, zs);
        mb.rotateZ(35f);
        focuserRadial(mb, R, fr, DARK);
        mb.pop();
        mb.push();
        mb.rotateZ(-40f);
        finder(mb, R, zf - L * 0.1f, L);
        mb.pop();
    }

    private static void maksutov(ModelBuilder mb, TelescopeType t, float R, float L, float zr, float zf) {
        int tube = t.tubeColor, acc = t.accentColor;
        mb.cylinderZ(R, zr + 0.03f, zf - 0.028f, 22, tube, false, false);
        mb.tubeZ(R * 1.045f, R * 0.84f, zf - 0.032f, zf, 22, acc, acc, true, true);
        mb.discZ(R * 0.86f, zf - 0.012f, 22, GLASS, true);
        mb.discZ(R * 0.27f, zf - 0.0105f, 14, MIRROR, true);
        glint(mb, R * 0.86f, zf - 0.0105f);
        mb.cylinderZ(R * 1.025f, zr, zr + 0.03f, 22, acc, true, false);
        float vb = Math.max(0.016f, R * 0.3f);
        mb.cylinderZ(vb, zr - 0.035f, zr, 12, BLACK, true, false);
        diagonal(mb, zr - 0.035f, clamp(R * 0.24f, 0.016f, 0.03f));
        mb.push();
        mb.translate(R * 0.55f, -R * 0.5f, zr);
        mb.cylinderZ(0.011f, -0.022f, 0f, 10, BLACK, true, false);
        mb.pop();
        finder(mb, R, zr + L * 0.4f, L);
    }

    private static void sct(ModelBuilder mb, TelescopeType t, float R, float L, float zr, float zf) {
        int tube = t.tubeColor, acc = t.accentColor;
        mb.cylinderZ(R, zr + 0.04f, zf - 0.036f, 24, tube, false, false);
        mb.tubeZ(R * 1.035f, R * 0.86f, zf - 0.04f, zf, 24, acc, acc, true, true);
        mb.discZ(R * 0.88f, zf - 0.014f, 24, GLASS, true);
        glint(mb, R * 0.88f, zf - 0.0125f);
        // suporte do espelho secundário
        mb.cylinderZ(R * 0.3f, zf - 0.016f, zf + 0.012f, 18, BLACK, false, true);
        mb.discZ(R * 0.13f, zf + 0.0135f, 12, SILVER, true);
        // célula traseira, visual back, diagonal e foco
        mb.cylinderZ(R * 1.035f, zr, zr + 0.044f, 24, acc, true, false);
        float vb = Math.max(0.02f, R * 0.24f);
        mb.cylinderZ(vb, zr - 0.03f, zr, 14, BLACK, true, false);
        diagonal(mb, zr - 0.03f, clamp(R * 0.2f, 0.018f, 0.034f));
        mb.push();
        mb.translate(R * 0.5f, -R * 0.45f, zr);
        mb.cylinderZ(0.013f, -0.024f, 0f, 10, BLACK, true, false);
        mb.pop();
        if (R > 0.1f) {
            // alças traseiras dos SCTs grandes (C11, C14, LX200 12")
            mb.box(R * 1.035f, -0.04f, zr + 0.008f, R * 1.035f + 0.014f, 0.04f, zr + 0.036f, BLACK);
            mb.box(-R * 1.035f - 0.014f, -0.04f, zr + 0.008f, -R * 1.035f, 0.04f, zr + 0.036f, BLACK);
        }
        finder(mb, R, zr + L * 0.32f, L);
    }

    // =====================================================================
    // Peças
    // =====================================================================

    /** Célula traseira com o espelho primário visível de dentro do tubo. */
    private static void mirrorCell(ModelBuilder mb, float R, float rIn, float zr, int color) {
        mb.cylinderZ(R * 1.03f, zr, zr + 0.026f, 24, color, true, false);
        mb.discZ(rIn, zr + 0.026f, 24, BLACK, true);
        mb.discZ(R * 0.84f, zr + 0.0275f, 24, MIRROR, true);
        for (int k = 0; k < 3; k++) {
            double ang = Math.toRadians(90 + k * 120);
            mb.push();
            mb.translate(R * 0.6f * (float) Math.cos(ang), R * 0.6f * (float) Math.sin(ang), zr);
            mb.cylinderZ(0.011f, -0.02f, 0f, 8, BLACK, true, false);
            mb.pop();
        }
    }

    /** Aranha de 4 hastes com o suporte do espelho secundário (Newtonianos). */
    private static void spider(ModelBuilder mb, float rIn, float z, float rSec) {
        float th = 0.0035f;
        mb.box(-rIn, -th, z - th, rIn, th, z + th, DARK);
        mb.box(-th, -rIn, z - th, th, rIn, z + th, DARK);
        mb.cylinderZ(rSec, z - rSec * 1.3f, z + 0.006f, 12, BLACK, true, true);
    }

    /** Focalizador saindo radialmente do tubo (direção +Y local), com ocular. */
    private static void focuserRadial(ModelBuilder mb, float R, float fr, int baseColor) {
        mb.box(-fr * 1.4f, R * 0.9f, -fr * 1.4f, fr * 1.4f, R + 0.016f, fr * 1.4f, baseColor);
        mb.cylinderY(fr, R + 0.016f, R + 0.05f, 12, BLACK);
        mb.push();
        mb.translate(0f, R + 0.032f, 0f);
        mb.cylinderZ(fr * 0.38f, -fr * 2.3f, fr * 2.3f, 8, BLACK, true, true);
        mb.pop();
        mb.cylinderY(fr * 0.72f, R + 0.05f, R + 0.075f, 12, SILVER);
        mb.cylinderY(fr * 0.95f, R + 0.075f, R + 0.12f, 14, BLACK);
        mb.cylinderY(fr * 0.85f, R + 0.12f, R + 0.132f, 14, RUBBER);
    }

    /** Diagonal estelar na traseira com a ocular apontando para +Y. */
    private static void diagonal(ModelBuilder mb, float z, float s) {
        mb.box(-s, -s, z - 2f * s, s, s, z, DARK);
        mb.push();
        mb.translate(0f, 0f, z - s);
        mb.cylinderY(s * 0.72f, s, s + 0.025f, 12, SILVER);
        mb.cylinderY(s * 0.95f, s + 0.025f, s + 0.07f, 14, BLACK);
        mb.cylinderY(s * 0.85f, s + 0.07f, s + 0.082f, 14, RUBBER);
        mb.pop();
    }

    /** Luneta buscadora sobre o tubo. */
    private static void finder(ModelBuilder mb, float R, float zc, float L) {
        float fr = clamp(R * 0.17f, 0.011f, 0.028f);
        float fl = clamp(L * 0.2f, 0.07f, 0.17f);
        float x = -R * 0.3f;
        float y = R + fr + 0.022f;
        mb.box(x - 0.007f, R * 0.85f, zc - 0.018f, x + 0.007f, y - fr * 0.6f, zc + 0.018f, BLACK);
        mb.push();
        mb.translate(x, y, 0f);
        float front = zc + fl * 0.5f;
        mb.cylinderZ(fr, zc - fl * 0.5f, front, 12, BLACK, true, false);
        mb.cylinderZ(fr * 1.18f, front - fl * 0.25f, front, 12, BLACK, false, false);
        mb.ringZ(fr * 1.18f, fr * 0.88f, front, 12, BLACK, true);
        mb.discZ(fr * 0.9f, front - 0.001f, 12, GLASS, true);
        mb.cylinderZ(fr * 0.55f, zc - fl * 0.5f - 0.03f, zc - fl * 0.5f, 10, DARK, true, false);
        mb.pop();
    }

    /** Pequeno reflexo de luz na lente/corretora. */
    private static void glint(ModelBuilder mb, float r, float z) {
        mb.push();
        mb.translate(-r * 0.38f, r * 0.38f, 0f);
        mb.discZ(r * 0.15f, z + 0.0012f, 10, GLASS_HI, true);
        mb.pop();
    }

    static int shade(int rgb, float k) {
        int r = Math.min(255, (int) (((rgb >> 16) & 255) * k));
        int g = Math.min(255, (int) (((rgb >> 8) & 255) * k));
        int b = Math.min(255, (int) ((rgb & 255) * k));
        return (r << 16) | (g << 8) | b;
    }

    static float clamp(float v, float lo, float hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }
}
