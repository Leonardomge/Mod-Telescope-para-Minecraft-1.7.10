package com.modtelescopio.client.model;

import com.modtelescopio.common.Optics;
import com.modtelescopio.common.TelescopeType;

import java.util.HashMap;
import java.util.Map;

/**
 * Caixa envolvente do modelo na pose padrão. Usada para encaixar o telescópio no
 * ícone do inventário, na mão do jogador e para a área de renderização no mundo.
 */
public final class ModelBounds implements QuadSink {
    private static final Map<String, ModelBounds> CACHE = new HashMap<String, ModelBounds>();

    public float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
    public float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;

    public static synchronized ModelBounds of(Assembly a) {
        ModelBounds b = CACHE.get(a.key);
        if (b == null) {
            b = new ModelBounds();
            TelescopeModel.build(new ModelBuilder().reset(b), a, 0f, 0f, Optics.DEFAULT_ALTITUDE);
            if (b.minX > b.maxX) {
                b.minX = b.minY = b.minZ = -0.5f;
                b.maxX = b.maxY = b.maxZ = 0.5f;
            }
            CACHE.put(a.key, b);
        }
        return b;
    }

    public static ModelBounds of(TelescopeType type) {
        return of(Assembly.of(type));
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

    public float width() {
        return Math.max(maxX - minX, maxZ - minZ);
    }

    public float maxDimension() {
        return Math.max(maxY - minY, Math.max(maxX - minX, maxZ - minZ));
    }

    /** Maior distância horizontal a partir do centro do bloco (o tubo gira, então vale para qualquer lado). */
    public float horizontalReach() {
        float r = Math.max(Math.max(Math.abs(minX), Math.abs(maxX)), Math.max(Math.abs(minZ), Math.abs(maxZ)));
        // com o tubo apontando para outro lado o alcance pode crescer até a altura do modelo
        return Math.max(r, height() * 0.75f);
    }

    /** Escala para que a maior dimensão do modelo ocupe {@code size} blocos. */
    public float fitScale(float size) {
        float d = maxDimension();
        return d > 1e-4f ? size / d : 1f;
    }
}
