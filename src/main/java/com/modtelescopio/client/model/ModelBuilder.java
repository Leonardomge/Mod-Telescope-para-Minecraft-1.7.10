package com.modtelescopio.client.model;

/**
 * Pilha de matrizes + primitivas geométricas (caixas, cilindros, tubos, discos, hastes).
 * Não depende de nenhuma classe do Minecraft: o mesmo código desenha o telescópio no
 * Forge 1.7.10 e no Forge 1.20.1. Ângulos sempre em graus.
 */
public final class ModelBuilder {
    private static final int MAX_DEPTH = 32;

    private final float[][] stack = new float[MAX_DEPTH][16];
    private int depth;
    private QuadSink sink;

    // vértices locais do quadrilátero em construção
    private final float[] lx = new float[4], ly = new float[4], lz = new float[4];
    private final float[] lnx = new float[4], lny = new float[4], lnz = new float[4];
    // vértices transformados
    private final float[] tx = new float[4], ty = new float[4], tz = new float[4];
    private final float[] tnx = new float[4], tny = new float[4], tnz = new float[4];

    public ModelBuilder() {
        reset(null);
    }

    public ModelBuilder reset(QuadSink sink) {
        this.sink = sink;
        this.depth = 0;
        float[] m = stack[0];
        for (int i = 0; i < 16; i++) {
            m[i] = (i % 5 == 0) ? 1f : 0f;
        }
        return this;
    }

    // ------------------------------------------------------------------ matrizes

    public void push() {
        if (depth + 1 >= MAX_DEPTH) {
            throw new IllegalStateException("ModelBuilder: pilha cheia");
        }
        System.arraycopy(stack[depth], 0, stack[depth + 1], 0, 16);
        depth++;
    }

    public void pop() {
        if (depth == 0) {
            throw new IllegalStateException("ModelBuilder: pop sem push");
        }
        depth--;
    }

    public void translate(float x, float y, float z) {
        float[] m = stack[depth];
        for (int r = 0; r < 3; r++) {
            int o = r * 4;
            m[o + 3] += m[o] * x + m[o + 1] * y + m[o + 2] * z;
        }
    }

    /** Rotação de mão direita em torno de X: +Y vai para +Z. */
    public void rotateX(float deg) {
        if (deg == 0f) {
            return;
        }
        double a = Math.toRadians(deg);
        float c = (float) Math.cos(a), s = (float) Math.sin(a);
        float[] m = stack[depth];
        for (int r = 0; r < 3; r++) {
            int o = r * 4;
            float m1 = m[o + 1], m2 = m[o + 2];
            m[o + 1] = c * m1 + s * m2;
            m[o + 2] = -s * m1 + c * m2;
        }
    }

    /** Rotação de mão direita em torno de Y: +Z vai para +X. */
    public void rotateY(float deg) {
        if (deg == 0f) {
            return;
        }
        double a = Math.toRadians(deg);
        float c = (float) Math.cos(a), s = (float) Math.sin(a);
        float[] m = stack[depth];
        for (int r = 0; r < 3; r++) {
            int o = r * 4;
            float m0 = m[o], m2 = m[o + 2];
            m[o] = c * m0 - s * m2;
            m[o + 2] = s * m0 + c * m2;
        }
    }

    /** Rotação de mão direita em torno de Z: +X vai para +Y. */
    public void rotateZ(float deg) {
        if (deg == 0f) {
            return;
        }
        double a = Math.toRadians(deg);
        float c = (float) Math.cos(a), s = (float) Math.sin(a);
        float[] m = stack[depth];
        for (int r = 0; r < 3; r++) {
            int o = r * 4;
            float m0 = m[o], m1 = m[o + 1];
            m[o] = c * m0 + s * m1;
            m[o + 1] = -s * m0 + c * m1;
        }
    }

    public void scale(float k) {
        float[] m = stack[depth];
        for (int r = 0; r < 3; r++) {
            int o = r * 4;
            m[o] *= k;
            m[o + 1] *= k;
            m[o + 2] *= k;
        }
    }

    // ------------------------------------------------------------------ emissão

    private void v(int i, float x, float y, float z, float nx, float ny, float nz) {
        lx[i] = x;
        ly[i] = y;
        lz[i] = z;
        lnx[i] = nx;
        lny[i] = ny;
        lnz[i] = nz;
    }

    /** Define o vértice i sem normal própria (a normal do plano é calculada em {@link #emitFlat}). */
    private void p(int i, float x, float y, float z) {
        lx[i] = x;
        ly[i] = y;
        lz[i] = z;
    }

    private void emitFlat(int rgb) {
        float ax = lx[1] - lx[0], ay = ly[1] - ly[0], az = lz[1] - lz[0];
        float bx = lx[2] - lx[0], by = ly[2] - ly[0], bz = lz[2] - lz[0];
        float nx = ay * bz - az * by, ny = az * bx - ax * bz, nz = ax * by - ay * bx;
        for (int i = 0; i < 4; i++) {
            lnx[i] = nx;
            lny[i] = ny;
            lnz[i] = nz;
        }
        emit(rgb);
    }

    private void emit(int rgb) {
        if (sink == null) {
            return;
        }
        float[] m = stack[depth];
        for (int i = 0; i < 4; i++) {
            float x = lx[i], y = ly[i], z = lz[i];
            tx[i] = m[0] * x + m[1] * y + m[2] * z + m[3];
            ty[i] = m[4] * x + m[5] * y + m[6] * z + m[7];
            tz[i] = m[8] * x + m[9] * y + m[10] * z + m[11];
            float a = lnx[i], b = lny[i], c = lnz[i];
            float nx = m[0] * a + m[1] * b + m[2] * c;
            float ny = m[4] * a + m[5] * b + m[6] * c;
            float nz = m[8] * a + m[9] * b + m[10] * c;
            float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
            if (len > 1e-9f) {
                nx /= len;
                ny /= len;
                nz /= len;
            } else {
                nx = 0f;
                ny = 1f;
                nz = 0f;
            }
            tnx[i] = nx;
            tny[i] = ny;
            tnz[i] = nz;
        }
        sink.quad(tx, ty, tz, tnx, tny, tnz, rgb);
    }

    // ------------------------------------------------------------------ primitivas

    /** Caixa alinhada aos eixos locais. */
    public void box(float x0, float y0, float z0, float x1, float y1, float z1, int rgb) {
        // +Y
        p(0, x0, y1, z0); p(1, x0, y1, z1); p(2, x1, y1, z1); p(3, x1, y1, z0); emitFlat(rgb);
        // -Y
        p(0, x0, y0, z0); p(1, x1, y0, z0); p(2, x1, y0, z1); p(3, x0, y0, z1); emitFlat(rgb);
        // +Z
        p(0, x0, y0, z1); p(1, x1, y0, z1); p(2, x1, y1, z1); p(3, x0, y1, z1); emitFlat(rgb);
        // -Z
        p(0, x0, y0, z0); p(1, x0, y1, z0); p(2, x1, y1, z0); p(3, x1, y0, z0); emitFlat(rgb);
        // +X
        p(0, x1, y0, z0); p(1, x1, y1, z0); p(2, x1, y1, z1); p(3, x1, y0, z1); emitFlat(rgb);
        // -X
        p(0, x0, y0, z0); p(1, x0, y0, z1); p(2, x0, y1, z1); p(3, x0, y1, z0); emitFlat(rgb);
    }

    /** Cilindro sólido ao longo de Z, com normais suaves na lateral. */
    public void cylinderZ(float r, float z0, float z1, int sides, int rgb, boolean capBack, boolean capFront) {
        for (int i = 0; i < sides; i++) {
            double a0 = 2.0 * Math.PI * i / sides, a1 = 2.0 * Math.PI * (i + 1) / sides;
            float c0 = (float) Math.cos(a0), s0 = (float) Math.sin(a0);
            float c1 = (float) Math.cos(a1), s1 = (float) Math.sin(a1);
            v(0, r * c0, r * s0, z0, c0, s0, 0f);
            v(1, r * c1, r * s1, z0, c1, s1, 0f);
            v(2, r * c1, r * s1, z1, c1, s1, 0f);
            v(3, r * c0, r * s0, z1, c0, s0, 0f);
            emit(rgb);
        }
        if (capBack) {
            discZ(r, z0, sides, rgb, false);
        }
        if (capFront) {
            discZ(r, z1, sides, rgb, true);
        }
    }

    /** Disco no plano z; front=true faz a normal apontar para +Z. */
    public void discZ(float r, float z, int sides, int rgb, boolean front) {
        if ((sides & 1) == 1) {
            sides++;
        }
        for (int i = 0; i < sides; i += 2) {
            double a0 = 2.0 * Math.PI * i / sides;
            double a1 = 2.0 * Math.PI * (i + 1) / sides;
            double a2 = 2.0 * Math.PI * (i + 2) / sides;
            float x0 = r * (float) Math.cos(a0), y0 = r * (float) Math.sin(a0);
            float x1 = r * (float) Math.cos(a1), y1 = r * (float) Math.sin(a1);
            float x2 = r * (float) Math.cos(a2), y2 = r * (float) Math.sin(a2);
            if (front) {
                v(0, 0f, 0f, z, 0f, 0f, 1f);
                v(1, x0, y0, z, 0f, 0f, 1f);
                v(2, x1, y1, z, 0f, 0f, 1f);
                v(3, x2, y2, z, 0f, 0f, 1f);
            } else {
                v(0, 0f, 0f, z, 0f, 0f, -1f);
                v(1, x2, y2, z, 0f, 0f, -1f);
                v(2, x1, y1, z, 0f, 0f, -1f);
                v(3, x0, y0, z, 0f, 0f, -1f);
            }
            emit(rgb);
        }
    }

    /** Anel (coroa circular) no plano z. */
    public void ringZ(float rOut, float rIn, float z, int sides, int rgb, boolean front) {
        float n = front ? 1f : -1f;
        for (int i = 0; i < sides; i++) {
            double a0 = 2.0 * Math.PI * i / sides, a1 = 2.0 * Math.PI * (i + 1) / sides;
            float c0 = (float) Math.cos(a0), s0 = (float) Math.sin(a0);
            float c1 = (float) Math.cos(a1), s1 = (float) Math.sin(a1);
            if (front) {
                v(0, rIn * c0, rIn * s0, z, 0f, 0f, n);
                v(1, rOut * c0, rOut * s0, z, 0f, 0f, n);
                v(2, rOut * c1, rOut * s1, z, 0f, 0f, n);
                v(3, rIn * c1, rIn * s1, z, 0f, 0f, n);
            } else {
                v(0, rIn * c1, rIn * s1, z, 0f, 0f, n);
                v(1, rOut * c1, rOut * s1, z, 0f, 0f, n);
                v(2, rOut * c0, rOut * s0, z, 0f, 0f, n);
                v(3, rIn * c0, rIn * s0, z, 0f, 0f, n);
            }
            emit(rgb);
        }
    }

    /** Tubo oco ao longo de Z: parede externa, parede interna (cor própria) e bordas opcionais. */
    public void tubeZ(float rOut, float rIn, float z0, float z1, int sides, int outer, int inner,
                      boolean rimBack, boolean rimFront) {
        cylinderZ(rOut, z0, z1, sides, outer, false, false);
        for (int i = 0; i < sides; i++) {
            double a0 = 2.0 * Math.PI * i / sides, a1 = 2.0 * Math.PI * (i + 1) / sides;
            float c0 = (float) Math.cos(a0), s0 = (float) Math.sin(a0);
            float c1 = (float) Math.cos(a1), s1 = (float) Math.sin(a1);
            v(0, rIn * c1, rIn * s1, z0, -c1, -s1, 0f);
            v(1, rIn * c0, rIn * s0, z0, -c0, -s0, 0f);
            v(2, rIn * c0, rIn * s0, z1, -c0, -s0, 0f);
            v(3, rIn * c1, rIn * s1, z1, -c1, -s1, 0f);
            emit(inner);
        }
        if (rimBack) {
            ringZ(rOut, rIn, z0, sides, outer, false);
        }
        if (rimFront) {
            ringZ(rOut, rIn, z1, sides, outer, true);
        }
    }

    /** Cilindro vertical (eixo Y), com tampas. */
    public void cylinderY(float r, float y0, float y1, int sides, int rgb) {
        push();
        rotateX(-90f);
        cylinderZ(r, y0, y1, sides, rgb, true, true);
        pop();
    }

    /** Cilindro ao longo de X, com tampas. */
    public void cylinderX(float r, float x0, float x1, int sides, int rgb) {
        push();
        rotateY(90f);
        cylinderZ(r, x0, x1, sides, rgb, true, true);
        pop();
    }

    /** Haste de seção quadrada ligando dois pontos. */
    public void rod(float ax, float ay, float az, float bx, float by, float bz, float thickness, int rgb) {
        float len = alignTo(ax, ay, az, bx, by, bz);
        float h = thickness * 0.5f;
        box(-h, 0f, -h, h, len, h, rgb);
        pop();
    }

    /** Haste cilíndrica ligando dois pontos. */
    public void rodRound(float ax, float ay, float az, float bx, float by, float bz, float radius, int sides, int rgb) {
        float len = alignTo(ax, ay, az, bx, by, bz);
        rotateX(-90f);
        cylinderZ(radius, 0f, len, sides, rgb, true, true);
        pop();
    }

    /** Faz push e orienta o eixo +Y local de A para B. Devolve o comprimento. */
    private float alignTo(float ax, float ay, float az, float bx, float by, float bz) {
        float dx = bx - ax, dy = by - ay, dz = bz - az;
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        push();
        translate(ax, ay, az);
        if (len < 1e-6f) {
            return 0f;
        }
        float yaw = (float) Math.toDegrees(Math.atan2(dx, dz));
        float pitch = (float) Math.toDegrees(Math.acos(Math.max(-1f, Math.min(1f, dy / len))));
        rotateY(yaw);
        rotateX(pitch);
        return len;
    }
}
