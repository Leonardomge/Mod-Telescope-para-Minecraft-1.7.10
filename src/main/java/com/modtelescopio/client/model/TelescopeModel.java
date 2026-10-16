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
 * <p>Todas as medidas abaixo estão na escala base (0,8 bloco por metro); o multiplicador de
 * tamanho de cada conjunto ({@link Assembly#scale}) é aplicado por cima, então telescópios de
 * abertura maior ficam proporcionalmente maiores.</p>
 *
 * <p>Convenção dos tubos ("quadro canônico"): eixo óptico em +Z (frente = abertura),
 * montagem do lado -Y.</p>
 */
public final class TelescopeModel {
    /** Escala base: blocos por metro antes do multiplicador de tamanho. */
    public static final float SCALE = 0.8f;

    static final int GLASS = 0x1B2D4A;
    static final int GLASS_HI = 0x6F93C4;
    static final int MIRROR = 0xD6DADF;
    static final int BLACK = 0x161618;
    static final int DARK = 0x2A2B2F;
    static final int SILVER = 0xB9BDC3;
    static final int RUBBER = 0x0D0D0F;
    static final int CARBON = 0x26282C;
    static final int ZWO_RED = 0xC4232D;
    static final int CAMERA_DARK = 0x2C2E33;
    static final int PW_BLUE = 0x2E6DB4;
    static final int LED_GREEN = 0x4FD07A;

    private TelescopeModel() {
    }

    /** Telescópio completo, como vem de fábrica. */
    public static void build(ModelBuilder mb, TelescopeType t, float facingYaw, float aimYaw, float aimAlt) {
        build(mb, Assembly.of(t), facingYaw, aimYaw, aimAlt);
    }

    /**
     * @param facingYaw yaw da frente do bloco (graus, convenção do Minecraft)
     * @param aimYaw    yaw para onde o tubo aponta
     * @param aimAlt    altura do tubo acima do horizonte (graus, positivo para cima)
     */
    public static void build(ModelBuilder mb, Assembly a, float facingYaw, float aimYaw, float aimAlt) {
        float rel = Optics.wrapDegrees(aimYaw - facingYaw);
        float alt = Optics.clampAltitude(aimAlt);
        mb.push();
        mb.rotateY(-facingYaw);
        mb.scale(a.scale);
        if (a.preset == null) {
            // item "tubo óptico": só o tubo, inclinado
            if (a.tube != null) {
                mb.rotateX(-alt);
                ota(mb, a, a.tube.design == OpticalDesign.SMART ? 0f : radius(a.tube) + 0.034f, 0.5f);
            }
        } else {
            switch (a.preset.kind) {
                case GEM:
                    equatorial(mb, a, rel, alt);
                    break;
                case DOBSONIAN:
                    dobsonian(mb, a, rel, alt);
                    break;
                case FORK_DOUBLE:
                    forkDouble(mb, a, rel, alt);
                    break;
                case FORK_SINGLE:
                    forkSingle(mb, a, rel, alt);
                    break;
                case OBSERVATORY:
                    observatoryFork(mb, a, rel, alt);
                    break;
                case SMART:
                    smart(mb, a, rel, alt);
                    break;
                default:
                    altAz(mb, a, rel, alt);
                    break;
            }
        }
        mb.pop();
    }

    static float radius(TelescopeType t) {
        return Math.max(0.035f, t.tubeDiameterM * SCALE * 0.5f);
    }

    static float length(TelescopeType t) {
        return t.tubeLengthM * SCALE;
    }

    /** Raio do tubo, ou um valor nominal quando a montagem está vazia. */
    private static float radiusOr(Assembly a, float nominal) {
        return a.tube != null ? radius(a.tube) : nominal;
    }

    private static float lengthOr(Assembly a, float nominal) {
        return a.tube != null ? length(a.tube) : nominal;
    }

    // =====================================================================
    // Apoios (tripé, tripé-pilar, pilar)
    // =====================================================================

    /** Desenha o apoio da montagem e devolve a altura onde a cabeça da montagem começa. */
    static float support(ModelBuilder mb, Assembly a) {
        MountType m = a.preset;
        switch (m.support) {
            case TRIPOD:
                tripod(mb, m.headY, m.spread, m.legT, a.legColor, DARK, true);
                return m.headY;
            case MINI_TRIPOD:
                tripod(mb, m.headY, m.spread, m.legT, a.legColor, DARK, false);
                return m.headY;
            case PIER_TRIPOD:
                pierTripod(mb, m.headY, m.spread, m.legT, a.legColor);
                return m.headY;
            case PIER:
                pier(mb, m.headY, m.spread, m.legT, a.legColor);
                return m.headY;
            default:
                return 0f;
        }
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

    /** Tripé-pilar (EQ8-R Pro): coluna de aço com três pés curtos. */
    static void pierTripod(ModelBuilder mb, float topY, float spread, float legT, int color) {
        float colR = legT * 1.5f;
        float baseY = topY * 0.3f;
        mb.cylinderY(colR, baseY, topY - 0.03f, 20, color);
        mb.cylinderY(colR * 1.35f, topY - 0.035f, topY, 20, DARK);
        mb.cylinderY(colR * 1.25f, baseY - 0.03f, baseY + 0.045f, 18, DARK);
        for (int k = 0; k < 3; k++) {
            double phi = Math.toRadians(k * 120.0);
            float sx = (float) Math.sin(phi), sz = (float) Math.cos(phi);
            float fx = sx * spread, fz = sz * spread;
            mb.rod(sx * colR, baseY, sz * colR, fx, 0.02f, fz, legT, color);
            mb.box(fx - legT * 0.7f, 0f, fz - legT * 0.7f, fx + legT * 0.7f, 0.02f, fz + legT * 0.7f, RUBBER);
        }
    }

    /** Pilar fixo de observatório: placa de base, coluna e flange. */
    static void pier(ModelBuilder mb, float topY, float baseR, float colR, int color) {
        mb.cylinderY(baseR, 0f, 0.035f, 28, DARK);
        for (int k = 0; k < 4; k++) {
            double phi = Math.toRadians(45 + k * 90.0);
            float bx = baseR * 0.82f * (float) Math.sin(phi), bz = baseR * 0.82f * (float) Math.cos(phi);
            mb.push();
            mb.translate(bx, 0f, bz);
            mb.cylinderY(baseR * 0.06f, 0.035f, 0.06f, 8, SILVER);
            mb.pop();
        }
        mb.cylinderY(colR, 0.035f, topY - 0.03f, 28, color);
        mb.cylinderY(colR * 1.2f, topY - 0.035f, topY, 28, DARK);
        for (int k = 0; k < 4; k++) {
            // reforços triangulares na base da coluna
            double phi = Math.toRadians(k * 90.0);
            float sx = (float) Math.sin(phi), sz = (float) Math.cos(phi);
            mb.rod(sx * colR, 0.035f + colR * 1.2f, sz * colR, sx * (colR + baseR) * 0.5f, 0.035f, sz * (colR + baseR) * 0.5f,
                    colR * 0.18f, color);
        }
    }

    /** Escada de alumínio encostada (para os dobsonianos gigantes). */
    static void ladder(ModelBuilder mb, float x, float z, float h) {
        float lean = h * 0.16f, w = 0.11f, rail = 0.022f;
        mb.rod(x, 0f, z - w, x - lean, h, z - w, rail, SILVER);
        mb.rod(x, 0f, z + w, x - lean, h, z + w, rail, SILVER);
        int n = Math.max(2, (int) (h / 0.14f));
        for (int i = 1; i <= n; i++) {
            float f = i / (n + 0.5f);
            float rx = x - lean * f, ry = h * f;
            mb.box(rx - 0.012f, ry - 0.009f, z - w, rx + 0.012f, ry + 0.009f, z + w, shade(SILVER, 0.85f));
        }
        mb.box(x - 0.02f, 0f, z - w - 0.02f, x + 0.02f, 0.015f, z - w + 0.02f, RUBBER);
        mb.box(x - 0.02f, 0f, z + w - 0.02f, x + 0.02f, 0.015f, z + w + 0.02f, RUBBER);
    }

    // =====================================================================
    // Montagens
    // =====================================================================

    /** Montagem equatorial alemã com cinemática real: eixo polar + eixo de declinação + contrapesos. */
    private static void equatorial(ModelBuilder mb, Assembly a, float rel, float alt) {
        MountType m = a.preset;
        int body = a.mountColor;
        int bodyDark = shade(body, 0.78f);
        float baseY = support(mb, a);

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
        double r = Math.toRadians(rel), aa = Math.toRadians(alt);
        double vx = -Math.sin(r) * Math.cos(aa), vy = Math.sin(aa), vz = Math.cos(r) * Math.cos(aa);
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
            mb.cylinderZ(m.rCW, zc - m.tCW * 0.5f, zc + m.tCW * 0.5f, 20, a.legColor, true, true);
            mb.cylinderZ(m.shaftR * 1.6f, zc - m.tCW * 0.5f - 0.004f, zc + m.tCW * 0.5f + 0.004f, 8, BLACK, true, true);
        }
        mb.cylinderZ(m.shaftR * 2f, shaftEnd - 0.012f, shaftEnd, 10, BLACK, true, true);

        // sela (dovetail) e tubo
        mb.translate(0f, 0f, d1);
        mb.rotateZ((float) Math.toDegrees(gamma));
        float saddleL = clamp(lengthOr(a, m.lDec * 1.2f) * 0.25f, 0.08f, 0.22f);
        mb.box(-0.03f, -saddleL * 0.5f, 0f, 0.03f, saddleL * 0.5f, 0.022f, bodyDark);
        mb.box(0.03f, -0.015f, 0.004f, 0.046f, 0.015f, 0.018f, BLACK);
        if (a.tube != null) {
            // do referencial da declinação (tubo em +Y) para o quadro canônico (tubo em +Z)
            mb.rotateX(-90f);
            mb.rotateZ(180f);
            ota(mb, a, 0.034f + radius(a.tube), 0.5f);
        }
        mb.pop();
    }

    /** Base Dobsoniana (rocker box) sobre a placa de chão. */
    private static void dobsonian(ModelBuilder mb, Assembly a, float rel, float alt) {
        TelescopeType t = a.tube;
        float R = radiusOr(a, 0.12f), L = lengthOr(a, 0.96f);
        float bal = 0.36f;
        float hp = L * bal + 0.08f;
        int base = a.mountColor;
        float sx0 = R + 0.03f, sx1 = sx0 + 0.025f + R * 0.04f;
        float d = Math.max(R * 0.95f + 0.05f, 0.14f);
        float gr = (float) Math.sqrt(sx1 * sx1 + d * d) + 0.02f;

        mb.cylinderY(gr, 0.012f, 0.032f, 28, base);
        for (int k = 0; k < 3; k++) {
            double ang = Math.toRadians(60 + k * 120);
            float fx = gr * 0.8f * (float) Math.sin(ang), fz = gr * 0.8f * (float) Math.cos(ang);
            mb.box(fx - 0.025f, 0f, fz - 0.025f, fx + 0.025f, 0.012f, fz + 0.025f, RUBBER);
        }
        if (t != null && t.has(TelescopeType.FLAG_LADDER)) {
            float topEyepiece = hp + L * (1f - bal) * 0.8f;
            ladder(mb, gr + 0.14f + topEyepiece * 0.16f, -d * 0.3f, Math.max(0.5f, topEyepiece * 0.72f));
        }

        mb.push();
        mb.rotateY(-rel);
        float bottom = 0.058f + R * 0.03f;
        mb.box(-sx1, 0.032f, -d, sx1, bottom, d, base);
        mb.box(-sx1, bottom, -d, -sx0, hp + 0.02f, d, base);
        mb.box(sx0, bottom, -d, sx1, hp + 0.02f, d, base);
        mb.box(-sx0, bottom, d - 0.022f, sx0, bottom + hp * 0.45f, d, base);
        // alças e reforços
        mb.box(sx1, hp * 0.55f, -0.04f, sx1 + 0.012f, hp * 0.55f + 0.02f, 0.04f, DARK);
        mb.box(-sx1 - 0.012f, hp * 0.55f, -0.04f, -sx1, hp * 0.55f + 0.02f, 0.04f, DARK);
        if (a.goTo) {
            mb.box(sx1, 0.07f, -0.06f, sx1 + 0.05f, 0.15f, 0.06f, BLACK);
            mb.box(-sx1 - 0.05f, hp - 0.06f, -0.05f, -sx1, hp + 0.02f, 0.05f, BLACK);
        }

        mb.translate(0f, hp, 0f);
        mb.rotateX(-alt);
        float br = Math.min(0.09f + R * 0.2f, R * 0.55f + 0.02f);
        mb.cylinderX(br, R * 0.9f, sx0 - 0.002f, 24, DARK);
        mb.cylinderX(br, -(sx0 - 0.002f), -R * 0.9f, 24, DARK);
        if (t != null) {
            ota(mb, a, 0f, bal);
        }
        mb.pop();
    }

    /** Garfo de dois braços (Meade LX200/LX90/ETX, Celestron CPC). */
    private static void forkDouble(ModelBuilder mb, Assembly a, float rel, float alt) {
        MountType m = a.preset;
        float R = radiusOr(a, 0.12f), L = lengthOr(a, 0.45f);
        boolean small = m == MountType.FORK_DOUBLE_SMALL;
        float bal = 0.42f;
        float baseY = support(mb, a);

        mb.push();
        mb.rotateY(-rel);
        int body = a.mountColor;
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
        if (a.tube != null) {
            ota(mb, a, 0f, bal);
        }
        mb.pop();
    }

    /**
     * Braço único lateral: leve (Celestron NexStar SE/SLT, Sky-Watcher AZ-GTi) ou pesado em
     * pilar (PlaneWave L-350/L-500/L-600, direct drive).
     */
    private static void forkSingle(ModelBuilder mb, Assembly a, float rel, float alt) {
        MountType m = a.preset;
        boolean heavy = m.support == MountType.Support.PIER;
        float R = radiusOr(a, heavy ? 0.18f : 0.06f), L = lengthOr(a, heavy ? 0.8f : 0.3f);
        float bal = 0.45f;
        float baseY = support(mb, a);

        mb.push();
        mb.rotateY(-rel);
        int body = a.mountColor;
        float rb = heavy ? 0.17f : 0.07f, hb = heavy ? 0.1f : 0.05f;
        mb.cylinderY(rb, baseY, baseY + hb, 24, body);
        if (heavy) {
            mb.cylinderY(rb * 1.03f, baseY + hb * 0.25f, baseY + hb * 0.4f, 24, SILVER);
        }
        float armT = heavy ? 0.1f : 0.042f, armD = heavy ? 0.24f : 0.1f;
        float hp = baseY + hb + Math.max(L * bal + (heavy ? 0.12f : 0.07f), heavy ? 0.4f : 0.16f);
        float armH = hp - baseY - hb;
        mb.box(-armT * 0.5f, baseY + hb, -armD * 0.5f, armT * 0.5f, hp, armD * 0.5f, body);
        mb.push();
        mb.translate(0f, hp, 0f);
        mb.cylinderX(armD * 0.5f, -armT * 0.5f, armT * 0.5f, 20, body);
        if (heavy) {
            // motor direct drive do eixo de altura, do lado de fora do braço
            mb.cylinderX(armD * 0.62f, armT * 0.5f, armT * 0.5f + 0.05f, 28, DARK);
            mb.cylinderX(armD * 0.3f, armT * 0.5f + 0.05f, armT * 0.5f + 0.065f, 20, SILVER);
        }
        mb.pop();
        if (heavy) {
            mb.box(armT * 0.5f, baseY + hb + armH * 0.15f, -0.05f, armT * 0.5f + 0.03f, baseY + hb + armH * 0.45f, 0.05f, DARK);
        } else {
            // controle de mão no suporte do braço
            float hy = baseY + hb + armH * 0.35f;
            mb.box(armT * 0.5f, hy, -0.022f, armT * 0.5f + 0.022f, hy + 0.075f, 0.022f, DARK);
            mb.box(armT * 0.5f + 0.022f, hy + 0.045f, -0.014f, armT * 0.5f + 0.024f, hy + 0.065f, 0.014f, LED_GREEN);
        }

        float tubeX = -(armT * 0.5f + (heavy ? 0.03f : 0.014f) + R);
        mb.translate(tubeX, hp, 0f);
        mb.rotateX(-alt);
        float armX = -tubeX;
        float clampHalf = heavy ? Math.max(0.12f, L * 0.22f) : 0.06f;
        float clampH = heavy ? 0.07f : 0.022f;
        mb.box(R * 0.8f, -clampH, -clampHalf, armX - armT * 0.5f, clampH, clampHalf, shade(body, 0.8f));
        if (!heavy) {
            mb.cylinderX(0.026f, armX + armT * 0.5f, armX + armT * 0.5f + 0.01f, 14, SILVER);
        }
        if (a.tube != null) {
            float dv = heavy ? 0.035f : 0.018f;
            mb.box(R - 0.005f, -dv, -L * 0.3f, R + 0.012f, dv, L * 0.3f, DARK);
            ota(mb, a, 0f, bal);
        }
        mb.pop();
    }

    /** Garfo de observatório (PlaneWave CDK700/CDK1000): pilar, base de azimute, dois braços e portas Nasmyth. */
    private static void observatoryFork(ModelBuilder mb, Assembly a, float rel, float alt) {
        float R = radiusOr(a, 0.4f), L = lengthOr(a, 1.4f);
        float bal = 0.42f;
        int body = a.mountColor;
        float colR = R * 0.5f, plateR = R * 1.15f, pierTop = R * 1.1f;
        pier(mb, pierTop, plateR, colR, a.legColor);

        mb.push();
        mb.rotateY(-rel);
        float gap = R * 0.1f, armT = R * 0.3f, armD = R;
        float ax0 = R + gap, ax1 = ax0 + armT;
        float baseR = ax1 + R * 0.08f, hb = R * 0.32f;
        float baseTop = pierTop + hb;
        mb.cylinderY(baseR, pierTop, baseTop, 36, body);
        mb.cylinderY(baseR * 1.02f, pierTop, pierTop + hb * 0.18f, 36, DARK);
        mb.cylinderY(baseR * 0.6f, baseTop, baseTop + hb * 0.12f, 28, shade(body, 0.85f));
        float hp = baseTop + L * bal + R * 0.4f;
        for (int s = -1; s <= 1; s += 2) {
            float x0 = s > 0 ? ax0 : -ax1, x1 = s > 0 ? ax1 : -ax0;
            mb.box(x0, baseTop, -armD * 0.5f, x1, hp, armD * 0.5f, body);
            // pé do braço mais largo
            mb.box(x0, baseTop, -armD * 0.68f, x1, baseTop + (hp - baseTop) * 0.3f, armD * 0.68f, body);
        }
        mb.translate(0f, hp, 0f);
        mb.cylinderX(armD * 0.5f, -ax1, -ax0, 28, body);
        mb.cylinderX(armD * 0.5f, ax0, ax1, 28, body);
        // portas Nasmyth: rotador + instrumento de cada lado
        float ns = R * 0.42f, fl = R * 0.12f;
        mb.cylinderX(R * 0.22f, ax1, ax1 + fl, 24, SILVER);
        mb.box(ax1 + fl, -ns * 0.5f, -ns * 0.5f, ax1 + fl + ns, ns * 0.5f, ns * 0.5f, CAMERA_DARK);
        mb.cylinderX(R * 0.22f, -ax1 - fl, -ax1, 24, SILVER);
        mb.cylinderX(ns * 0.45f, -ax1 - fl - ns * 0.9f, -ax1 - fl, 24, ZWO_RED);
        mb.rotateX(-alt);
        // mancais do eixo de altura
        mb.cylinderX(R * 0.5f, -ax0, -R * 0.95f, 28, SILVER);
        mb.cylinderX(R * 0.5f, R * 0.95f, ax0, 28, SILVER);
        if (a.tube != null) {
            ota(mb, a, 0f, bal);
        }
        mb.pop();
    }

    /** Montagem altazimutal simples de tripé fotográfico (refratores de entrada). */
    private static void altAz(ModelBuilder mb, Assembly a, float rel, float alt) {
        float R = radiusOr(a, 0.04f);
        float y0 = support(mb, a);

        mb.push();
        mb.rotateY(-rel);
        int body = a.mountColor;
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
        if (a.tube != null) {
            ota(mb, a, 0.024f + R, 0.45f);
        }
        mb.pop();
    }

    /** Telescópio inteligente (Seestar, DWARF, Vespera, eVscope): corpo compacto num mini tripé. */
    private static void smart(ModelBuilder mb, Assembly a, float rel, float alt) {
        TelescopeType t = a.tube;
        float top = support(mb, a);
        if (t == null) {
            return;
        }
        float L = length(t), W = t.tubeDiameterM * SCALE;
        float H = W * 0.9f;
        mb.push();
        mb.rotateY(-rel);
        mb.cylinderY(W * 0.42f, top, top + 0.025f, 20, DARK);
        float base = top + 0.025f;
        if (t.has(TelescopeType.FLAG_TUBE_BODY)) {
            // eVscope: tubo num braço lateral alto
            float R = W * 0.5f, armT = W * 0.2f;
            float hp = base + L * 0.42f;
            mb.box(R + 0.004f, base, -R * 0.6f, R + armT, hp, R * 0.6f, a.mountColor);
            mb.push();
            mb.translate(0f, hp, 0f);
            mb.cylinderX(R * 0.6f, R + 0.004f, R + armT, 18, a.mountColor);
            mb.pop();
            mb.translate(0f, hp, 0f);
            mb.rotateX(-alt);
            float zr = -L * 0.42f, zf = L * 0.58f;
            mb.tubeZ(R, R * 0.9f, zr + 0.02f, zf, 24, t.tubeColor, BLACK, false, true);
            mb.cylinderZ(R * 1.02f, zr, zr + 0.02f, 24, t.accentColor, true, false);
            mb.discZ(R * 0.9f, zr + 0.021f, 24, BLACK, true);
            mb.discZ(R * 0.8f, zr + 0.023f, 24, MIRROR, true);
            mb.tubeZ(R * 1.03f, R * 0.9f, zf - 0.03f, zf, 24, t.accentColor, BLACK, true, true);
            // ocular eletrônica na lateral traseira
            mb.push();
            mb.translate(-R * 0.75f, R * 0.4f, zr + L * 0.15f);
            mb.rotateZ(40f);
            mb.cylinderY(R * 0.22f, 0f, R * 0.7f, 14, BLACK);
            mb.pop();
            mb.box(R - 0.002f, -R * 0.2f, zr + L * 0.3f, R + 0.004f, -R * 0.05f, zr + L * 0.36f, LED_GREEN);
        } else {
            // corpo em forma de caixa preso num braço lateral (Seestar, DWARF, Vespera)
            float hw = W * 0.5f, hh = H * 0.5f, armT = W * 0.2f;
            float hp = base + H * 0.78f;
            mb.box(hw + 0.004f, base, -L * 0.17f, hw + armT, hp, L * 0.17f, a.mountColor);
            mb.push();
            mb.translate(0f, hp, 0f);
            mb.cylinderX(L * 0.17f, hw + 0.004f, hw + armT, 20, a.mountColor);
            mb.cylinderX(L * 0.08f, hw + armT, hw + armT + 0.008f, 16, t.accentColor);
            mb.pop();
            mb.translate(0f, hp, 0f);
            mb.rotateX(-alt);
            mb.box(-hw, -hh, -L * 0.5f, hw, hh, L * 0.5f, t.tubeColor);
            // bordas arredondadas
            mb.cylinderZ(hh, -L * 0.5f, L * 0.5f, 16, t.tubeColor, false, false);
            float lensR = Math.max(0.012f, t.aperture * 0.001f * SCALE * 0.55f);
            float zf = L * 0.5f;
            if (t.has(TelescopeType.FLAG_DUAL_LENS)) {
                lens(mb, -hw * 0.25f, 0f, zf, lensR, t.accentColor);
                lens(mb, hw * 0.6f, hh * 0.45f, zf, Math.max(0.006f, lensR * 0.32f), t.accentColor);
            } else {
                lens(mb, 0f, 0f, zf, lensR, t.accentColor);
            }
            mb.box(hw, hh * 0.35f, L * 0.18f, hw + 0.003f, hh * 0.6f, L * 0.28f, LED_GREEN);
        }
        mb.pop();
    }

    /** Lente frontal com anel, usada nos telescópios inteligentes. */
    private static void lens(ModelBuilder mb, float x, float y, float z, float r, int rim) {
        mb.push();
        mb.translate(x, y, z);
        mb.cylinderZ(r * 1.3f, -0.004f, 0.012f, 18, rim, false, true);
        mb.discZ(r, 0.0135f, 18, GLASS, true);
        mb.push();
        mb.translate(-r * 0.35f, r * 0.35f, 0f);
        mb.discZ(r * 0.2f, 0.015f, 10, GLASS_HI, true);
        mb.pop();
        mb.pop();
    }

    // =====================================================================
    // Tubos ópticos (quadro canônico)
    // =====================================================================

    /**
     * Desenha o tubo óptico. {@code yOff} é a distância do ponto de fixação até o eixo do tubo
     * (0 quando o eixo de altura passa pelo próprio tubo); {@code bal} é a fração do comprimento
     * atrás do ponto de fixação.
     */
    static void ota(ModelBuilder mb, Assembly a, float yOff, float bal) {
        TelescopeType t = a.tube;
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
                if (t.has(TelescopeType.FLAG_OPEN_TRUSS)) {
                    openTrussNewtonian(mb, t, R, L, zr, zf);
                } else {
                    truss(mb, t, R, L, zr, zf);
                }
                break;
            case MAKSUTOV:
                maksutov(mb, t, R, L, zr, zf);
                break;
            case RITCHEY_CHRETIEN:
                rcSolid(mb, t, R, L, zr, zf);
                break;
            case RITCHEY_CHRETIEN_TRUSS:
                rcTruss(mb, t, R, L, zr, zf);
                break;
            case CDK:
                cdk(mb, a, t, R, L, zr, zf);
                break;
            case RASA:
                rasa(mb, t, R, L, zr, zf);
                break;
            case SMART:
                // tubo de telescópio inteligente sozinho não existe como item; desenha o corpo simples
                mb.box(-R, -R, zr, R, R, zf, t.tubeColor);
                break;
            default:
                sct(mb, t, R, L, zr, zf);
                break;
        }
        if (yOff > 0.001f && t.design != OpticalDesign.SMART) {
            saddleHardware(mb, t, R, L, zr, zf);
        }
        mb.pop();
    }

    /** Barra dovetail e anéis do tubo (quando o tubo fica apoiado sobre a montagem). */
    private static void saddleHardware(ModelBuilder mb, TelescopeType t, float R, float L, float zr, float zf) {
        float zc = (zr + zf) * 0.5f;
        float half = Math.max(0.06f, L * 0.28f);
        mb.box(-0.022f, -R - 0.013f, zc - half, 0.022f, -R + 0.004f, zc + half, DARK);
        boolean rings = t.design.isRefractor() || t.design == OpticalDesign.NEWTONIAN;
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
        if (t.has(TelescopeType.FLAG_CARBON)) {
            carbonBands(mb, R, zr + 0.022f, zf - 0.014f);
        }
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

    /** Faixas sutis que lembram a trama do tubo de carbono. */
    private static void carbonBands(ModelBuilder mb, float R, float z0, float z1) {
        int n = 6;
        float step = (z1 - z0) / n;
        for (int i = 0; i < n; i += 2) {
            mb.cylinderZ(R * 1.003f, z0 + step * i, z0 + step * (i + 1), 24, shade(CARBON, 1.25f), false, false);
        }
    }

    /** Flextube: tubo do espelho, gaiola superior e três pares de tubos paralelos. */
    private static void truss(ModelBuilder mb, TelescopeType t, float R, float L, float zr, float zf) {
        int tube = t.tubeColor, acc = t.accentColor;
        float rIn = R * 0.93f;
        float zLow = zr + L * 0.36f, zCage = zf - L * 0.2f;
        mb.tubeZ(R, rIn, zr + 0.022f, zLow, 24, tube, BLACK, false, true);
        mirrorCell(mb, R, rIn, zr, tube);
        mb.tubeZ(R * 1.03f, rIn, zLow - 0.03f, zLow, 24, DARK, BLACK, true, true);
        mb.tubeZ(R, rIn, zCage, zf, 24, tube, BLACK, true, true);
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
        cageFittings(mb, t, R, L, zf);
    }

    /** Dobsoniano de treliça aberta (Stargate): caixa do espelho, 8 tubos e gaiola superior. */
    private static void openTrussNewtonian(ModelBuilder mb, TelescopeType t, float R, float L, float zr, float zf) {
        int tube = t.tubeColor;
        float rIn = R * 0.93f;
        float zBox = zr + L * 0.28f, zCage = zf - L * 0.18f;
        mb.tubeZ(R, rIn, zr + 0.022f, zBox, 28, tube, BLACK, false, true);
        mirrorCell(mb, R, rIn, zr, tube);
        mb.tubeZ(R, rIn, zCage, zf, 28, tube, BLACK, true, true);
        serrurier(mb, R * 0.95f, zBox, R * 0.95f, zCage, 4, Math.max(0.008f, R * 0.035f), SILVER);
        cageFittings(mb, t, R, L, zf);
    }

    /** Aranha, focalizador e buscadora da gaiola superior dos dobsonianos de treliça. */
    private static void cageFittings(ModelBuilder mb, TelescopeType t, float R, float L, float zf) {
        float rIn = R * 0.93f;
        float zs = zf - Math.max(R * 0.9f, 0.06f);
        spider(mb, rIn, zs, R * 0.24f);
        float fr = clamp(R * 0.17f, 0.02f, 0.05f);
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

    /** Ritchey-Chrétien de tubo fechado (GSO): frente aberta, secundário com aranha e câmera atrás. */
    private static void rcSolid(ModelBuilder mb, TelescopeType t, float R, float L, float zr, float zf) {
        int tube = t.tubeColor, acc = t.accentColor;
        float rIn = R * 0.94f;
        mb.tubeZ(R, rIn, zr + 0.03f, zf - 0.02f, 28, tube, BLACK, false, false);
        if (t.has(TelescopeType.FLAG_CARBON)) {
            carbonBands(mb, R, zr + 0.03f, zf - 0.02f);
        }
        mb.tubeZ(R * 1.03f, rIn, zf - 0.02f, zf, 28, acc, BLACK, true, true);
        mb.cylinderZ(R * 1.03f, zr, zr + 0.03f, 28, acc, true, false);
        mb.discZ(rIn, zr + 0.03f, 28, BLACK, true);
        mb.discZ(R * 0.86f, zr + 0.032f, 28, MIRROR, true);
        mb.cylinderZ(R * 0.18f, zr + 0.03f, zr + L * 0.33f, 14, BLACK, false, true);
        spider(mb, rIn, zf - 0.045f, R * 0.32f);
        // barra dovetail de cima (os RC da GSO têm duas)
        mb.box(-0.02f, R * 0.98f, -L * 0.3f, 0.02f, R + 0.016f, L * 0.3f, DARK);
        rearImaging(mb, t, R, zr);
    }

    /** Ritchey-Chrétien de treliça (GSO 10"/12"/16"). */
    private static void rcTruss(ModelBuilder mb, TelescopeType t, float R, float L, float zr, float zf) {
        int ring = t.tubeColor, rod = t.accentColor;
        float zBox = zr + L * 0.22f, zFront = zf - L * 0.14f;
        mb.cylinderZ(R, zr, zBox, 28, ring, true, false);
        mb.ringZ(R, R * 0.9f, zBox, 28, ring, true);
        mb.discZ(R * 0.9f, zBox - 0.004f, 28, MIRROR, true);
        mb.cylinderZ(R * 0.18f, zBox - 0.01f, zBox + L * 0.25f, 14, BLACK, false, true);
        serrurier(mb, R * 0.9f, zBox, R * 0.9f, zFront, 4, Math.max(0.008f, R * 0.032f), rod);
        mb.tubeZ(R, R * 0.9f, zFront, zf, 28, ring, BLACK, true, true);
        spider(mb, R * 0.9f, zf - (zf - zFront) * 0.5f, R * 0.3f);
        rearImaging(mb, t, R, zr);
    }

    /** PlaneWave CDK: célula com ventoinhas, treliça de carbono, anel do secundário e câmera. */
    private static void cdk(ModelBuilder mb, Assembly a, TelescopeType t, float R, float L, float zr, float zf) {
        int ring = t.tubeColor, acc = t.accentColor;
        float cellLen = L * 0.17f;
        float zCell = zr + cellLen;
        mb.cylinderZ(R, zr, zCell, 32, ring, true, false);
        mb.cylinderZ(R * 1.008f, zr + cellLen * 0.35f, zr + cellLen * 0.5f, 32, PW_BLUE, false, false);
        mb.ringZ(R, R * 0.88f, zCell, 32, ring, true);
        mb.discZ(R * 0.88f, zCell - 0.006f, 32, MIRROR, true);
        mb.cylinderZ(R * 0.2f, zCell - 0.01f, zCell + L * 0.28f, 16, BLACK, false, true);
        // ventoinhas de refrigeração do espelho
        for (int k = 0; k < 3; k++) {
            double ang = Math.toRadians(90 + k * 120);
            mb.push();
            mb.translate(R * 0.58f * (float) Math.cos(ang), R * 0.58f * (float) Math.sin(ang), zr);
            mb.ringZ(R * 0.17f, R * 0.13f, -0.002f, 18, SILVER, false);
            mb.discZ(R * 0.13f, -0.001f, 18, BLACK, false);
            mb.pop();
        }
        float zFront = zf - L * 0.13f;
        serrurier(mb, R * 0.92f, zCell, R * 0.92f, zFront, 4, Math.max(0.008f, R * 0.035f), CARBON);
        mb.tubeZ(R, R * 0.9f, zFront, zf, 32, ring, BLACK, true, true);
        mb.cylinderZ(R * 1.006f, zFront + (zf - zFront) * 0.4f, zFront + (zf - zFront) * 0.55f, 32, acc, false, false);
        spider(mb, R * 0.9f, zf - (zf - zFront) * 0.5f, R * 0.38f);
        if (a.preset == MountType.FORK_OBSERVATORY) {
            // nos grandes, a luz sai pelas portas Nasmyth: atrás só a tampa do terciário
            mb.cylinderZ(R * 0.3f, zr - 0.03f, zr, 24, acc, true, false);
        } else {
            mb.cylinderZ(R * 0.3f, zr - 0.05f, zr, 24, acc, true, false);
            camera(mb, zr - 0.05f, R * 0.27f, CAMERA_DARK);
        }
    }

    /** Celestron RASA: corretora Schmidt na frente com a câmera no foco primário. */
    private static void rasa(ModelBuilder mb, TelescopeType t, float R, float L, float zr, float zf) {
        int tube = t.tubeColor, acc = t.accentColor;
        mb.cylinderZ(R, zr + 0.045f, zf - 0.04f, 28, tube, false, false);
        mb.tubeZ(R * 1.035f, R * 0.86f, zf - 0.045f, zf, 28, acc, acc, true, true);
        mb.discZ(R * 0.88f, zf - 0.015f, 28, GLASS, true);
        glint(mb, R * 0.88f, zf - 0.0135f);
        // câmera na frente da corretora
        mb.cylinderZ(R * 0.25f, zf - 0.02f, zf + 0.035f, 20, BLACK, false, true);
        mb.cylinderZ(R * 0.24f, zf + 0.035f, zf + 0.035f + R * 0.42f, 20, ZWO_RED, false, true);
        mb.discZ(R * 0.16f, zf + 0.036f + R * 0.42f, 16, BLACK, true);
        mb.rod(R * 0.2f, R * 0.12f, zf + 0.035f + R * 0.3f, R * 0.95f, R * 0.4f, zf - 0.03f, 0.008f, BLACK);
        mb.cylinderZ(R * 1.035f, zr, zr + 0.045f, 28, acc, true, false);
        mb.push();
        mb.translate(0f, 0f, zr);
        mb.ringZ(R * 0.3f, R * 0.24f, -0.002f, 20, SILVER, false);
        mb.discZ(R * 0.24f, -0.001f, 20, BLACK, false);
        mb.pop();
        finder(mb, R, zr + L * 0.35f, L);
    }

    // =====================================================================
    // Peças
    // =====================================================================

    /**
     * Treliça Serrurier: {@code pairs} pares de tubos em "V" ligando o anel traseiro ao dianteiro.
     */
    private static void serrurier(ModelBuilder mb, float rRear, float zRear, float rFront, float zFront, int pairs,
                                  float rodR, int color) {
        double half = Math.PI / pairs * 0.5;
        for (int k = 0; k < pairs; k++) {
            double base = 2.0 * Math.PI * k / pairs + Math.PI / 4;
            float xF = rFront * (float) Math.cos(base), yF = rFront * (float) Math.sin(base);
            for (int s = -1; s <= 1; s += 2) {
                double ang = base + s * half;
                float xR = rRear * (float) Math.cos(ang), yR = rRear * (float) Math.sin(ang);
                mb.rodRound(xR, yR, zRear, xF, yF, zFront, rodR, 6, color);
            }
            // conectores nas pontas
            mb.push();
            mb.translate(xF, yF, zFront);
            mb.box(-rodR * 2.2f, -rodR * 2.2f, -rodR * 3f, rodR * 2.2f, rodR * 2.2f, 0f, BLACK);
            mb.pop();
        }
    }

    /** Focalizador + câmera (astrógrafos) ou diagonal + ocular na traseira de um Cassegrain. */
    private static void rearImaging(ModelBuilder mb, TelescopeType t, float R, float zr) {
        float fr = Math.max(0.02f, R * 0.26f);
        mb.cylinderZ(fr, zr - 0.05f, zr, 18, SILVER, true, false);
        if (t.has(TelescopeType.FLAG_CAMERA)) {
            camera(mb, zr - 0.05f, fr * 0.95f, ZWO_RED);
        } else {
            diagonal(mb, zr - 0.05f, clamp(R * 0.2f, 0.018f, 0.034f));
        }
    }

    /** Câmera astronômica refrigerada (cilindro com aletas e ventoinha), crescendo para -Z. */
    private static void camera(ModelBuilder mb, float z, float s, int color) {
        float len = s * 1.3f;
        mb.cylinderZ(s * 0.7f, z - s * 0.25f, z, 16, BLACK, false, false);
        mb.cylinderZ(s, z - s * 0.25f - len, z - s * 0.25f, 22, color, true, true);
        for (int i = 1; i <= 3; i++) {
            float zc = z - s * 0.25f - len * i / 4f;
            mb.cylinderZ(s * 1.04f, zc - s * 0.04f, zc + s * 0.04f, 22, shade(color, 0.7f), false, false);
        }
        mb.push();
        mb.translate(0f, 0f, z - s * 0.25f - len);
        mb.ringZ(s * 0.7f, s * 0.55f, -0.002f, 18, SILVER, false);
        mb.discZ(s * 0.55f, -0.001f, 18, BLACK, false);
        mb.pop();
    }

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

    /** Aranha de 4 hastes com o suporte do espelho secundário. */
    private static void spider(ModelBuilder mb, float rIn, float z, float rSec) {
        float th = Math.max(0.0035f, rIn * 0.012f);
        mb.box(-rIn, -th, z - th, rIn, th, z + th, DARK);
        mb.box(-th, -rIn, z - th, th, rIn, z + th, DARK);
        mb.cylinderZ(rSec, z - rSec * 1.3f, z + 0.006f, 14, BLACK, true, true);
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
