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
    /** Passos de zoom digital dos telescópios que só usam câmera. */
    public static final float[] DIGITAL_ZOOM = {1.0F, 1.5F, 2.0F, 3.0F, 4.0F, 6.0F};
    /** Tempo de exposição de cada quadro do empilhamento ao vivo, em segundos. */
    public static final int STACK_EXPOSURE_S = 10;

    private Optics() {
    }

    /** Campo da câmera (largura) em graus. */
    public static double cameraFieldW(TelescopeType t) {
        return Math.toDegrees(2.0 * Math.atan(t.sensorW / (2.0 * t.focal)));
    }

    /** Campo da câmera (altura) em graus. */
    public static double cameraFieldH(TelescopeType t) {
        return Math.toDegrees(2.0 * Math.atan(t.sensorH / (2.0 * t.focal)));
    }

    /**
     * Zoom de câmera do jogo para um telescópio que só usa câmera. Usa a mesma relação das
     * oculares (campo da tela ≈ 12x o campo real), aplicada à altura do sensor.
     */
    public static float cameraZoom(TelescopeType t, float digitalZoom) {
        double fieldH = cameraFieldH(t) / digitalZoom;
        return (float) Math.max(1.5, Math.min(64.0, 70.0 / (12.1 * fieldH)));
    }

    /** Ganho extra de brilho do empilhamento ao vivo (as imagens vão clareando com o tempo). */
    public static float stackingBoost(double seconds) {
        return (float) Math.min(0.55, seconds / 90.0 * 0.55);
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
