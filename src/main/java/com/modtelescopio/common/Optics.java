package com.modtelescopio.common;

import java.util.ArrayList;
import java.util.List;

/** Contas de óptica visual usadas no HUD e no zoom da observação. */
public final class Optics {
    /** Inclinação do eixo polar das montagens equatoriais (latitude do "observatório"). */
    public static final float LATITUDE = 35f;
    /** Altura padrão do tubo ao colocar o telescópio: apontando para o polo celeste. */
    public static final float DEFAULT_ALTITUDE = LATITUDE;
    public static final float MIN_ALTITUDE = -10f;
    public static final float MAX_ALTITUDE = 90f;
    /** Campo aparente de uma ocular Plössl comum. */
    public static final double APPARENT_FIELD = 52.0;

    private static final int[] EYEPIECES_MM = {40, 32, 25, 20, 15, 12, 9, 6, 4, 3};

    private Optics() {
    }

    /**
     * Oculares que fazem sentido para o telescópio: pupila de saída de no máximo 7 mm
     * e aumento até 2x a abertura em milímetros (limite útil clássico).
     */
    public static int[] eyepiecesFor(TelescopeType t) {
        double minMag = t.aperture / 7.0;
        double maxMag = t.aperture * 2.0;
        List<Integer> list = new ArrayList<Integer>();
        for (int fe : EYEPIECES_MM) {
            double mag = magnification(t, fe);
            if (mag >= minMag * 0.85 && mag <= maxMag) {
                list.add(fe);
            }
        }
        if (list.isEmpty()) {
            list.add(25);
        }
        int[] out = new int[list.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = list.get(i);
        }
        return out;
    }

    public static double magnification(TelescopeType t, int eyepieceMm) {
        return (double) t.focal / eyepieceMm;
    }

    /** Converte o aumento real em zoom de câmera do Minecraft (o mundo do jogo é bem menor que o céu). */
    public static float zoomFor(double magnification) {
        return (float) Math.max(2.5, Math.min(64.0, magnification / 9.0));
    }

    public static double trueField(double magnification) {
        return APPARENT_FIELD / magnification;
    }

    public static double exitPupil(TelescopeType t, int eyepieceMm) {
        return eyepieceMm / t.focalRatio();
    }

    public static double limitingMagnitude(TelescopeType t) {
        return 2.7 + 5.0 * Math.log10(t.aperture);
    }

    /** Limite de Dawes em segundos de arco. */
    public static double dawesLimit(TelescopeType t) {
        return 116.0 / t.aperture;
    }

    /** Ganho de brilho (gamma) enquanto observa: telescópios maiores captam mais luz. */
    public static float lightGatheringBoost(TelescopeType t) {
        float k = (t.aperture - 50f) / 350f;
        k = Math.max(0f, Math.min(1f, k));
        return 0.15f + 0.6f * k;
    }

    /** Azimute de bússola (0 = norte, 90 = leste) a partir do yaw do Minecraft. */
    public static float compassAzimuth(float yaw) {
        float az = (yaw + 180f) % 360f;
        return az < 0 ? az + 360f : az;
    }

    public static float wrapDegrees(float deg) {
        deg %= 360f;
        if (deg >= 180f) {
            deg -= 360f;
        }
        if (deg < -180f) {
            deg += 360f;
        }
        return deg;
    }

    public static float clampAltitude(float alt) {
        return Math.max(MIN_ALTITUDE, Math.min(MAX_ALTITUDE, alt));
    }
}
