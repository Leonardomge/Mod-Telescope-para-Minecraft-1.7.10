package com.modtelescopio.client.model;

import com.modtelescopio.common.Optics;
import com.modtelescopio.common.TelescopeType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Caixa envolvente do modelo na pose padrão. Usada para encaixar o telescópio no
 * ícone do inventário e na mão do jogador, qualquer que seja o tamanho dele.
 */
public final class ModelBounds implements QuadSink {
    private static final Map<TelescopeType, ModelBounds> CACHE = new EnumMap<TelescopeType, ModelBounds>(TelescopeType.class);

    public float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
    public float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;

    public static synchronized ModelBounds of(TelescopeType type) {
        ModelBounds b = CACHE.get(type);
        if (b == null) {
            b = new ModelBounds();
            TelescopeModel.build(new ModelBuilder().reset(b), type, 0f, 0f, Optics.DEFAULT_ALTITUDE);
            CACHE.put(type, b);
        }
        return b;
    }

    @Override
    public void quad(float[] x, float[] y, float[] z, float[] nx, float[] ny, float[] nz, int rgb) {
        for (int i = 0; i < 4; i++) {
            minX = Math.min(minX, x[i]);
            minY = Math.min(minY, y[i]);
            minZ = Math.min(minZ, z[i]);
            maxX = Math.max(maxX, x[i]);
            maxY = Math.max(maxY, y[i]);
            maxZ = Math.max(maxZ, z[i]);
        }
    }

    public float centerX() {
        return (minX + maxX) * 0.5f;
    }

    public float centerZ() {
        return (minZ + maxZ) * 0.5f;
    }

    public float height() {
        return maxY - minY;
    }

    public float maxDimension() {
        return Math.max(maxY - minY, Math.max(maxX - minX, maxZ - minZ));
    }

    /** Escala para que a maior dimensão do modelo ocupe {@code size} blocos. */
    public float fitScale(float size) {
        float d = maxDimension();
        return d > 1e-4f ? size / d : 1f;
    }
}
