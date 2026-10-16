package com.modtelescopio.common;

/**
 * Tipos de montagem. As medidas estão na escala base do modelo (0,8 bloco por metro); o
 * multiplicador de tamanho de cada telescópio/montagem é aplicado por cima.
 */
public enum MountType {
    //            tipo              suporte            altTripé abertura perna  rRA     lRA    rDEC    lDEC   haste  rHaste  rContra tContra nContra
    GEM_HEAVY(Kind.GEM, Support.TRIPOD,              0.62f,   0.46f,  0.042f, 0.062f, 0.25f, 0.056f, 0.20f, 0.30f, 0.012f, 0.072f, 0.055f, 2),
    GEM_XHEAVY(Kind.GEM, Support.PIER_TRIPOD,        0.78f,   0.42f,  0.050f, 0.085f, 0.33f, 0.075f, 0.27f, 0.40f, 0.016f, 0.095f, 0.070f, 2),
    GEM_MEDIUM(Kind.GEM, Support.TRIPOD,             0.57f,   0.40f,  0.034f, 0.050f, 0.20f, 0.046f, 0.16f, 0.25f, 0.010f, 0.058f, 0.050f, 2),
    GEM_LIGHT(Kind.GEM, Support.TRIPOD,              0.52f,   0.35f,  0.026f, 0.036f, 0.15f, 0.033f, 0.12f, 0.19f, 0.008f, 0.045f, 0.040f, 1),
    DOBSONIAN(Kind.DOBSONIAN, Support.NONE,          0f,      0f,     0f,     0f,     0f,    0f,     0f,    0f,    0f,     0f,     0f,     0),
    FORK_DOUBLE(Kind.FORK_DOUBLE, Support.TRIPOD,    0.55f,   0.42f,  0.040f, 0f,     0f,    0f,     0f,    0f,    0f,     0f,     0f,     0),
    FORK_DOUBLE_SMALL(Kind.FORK_DOUBLE, Support.TRIPOD, 0.50f, 0.34f, 0.026f, 0f,     0f,    0f,     0f,    0f,    0f,     0f,     0f,     0),
    FORK_SINGLE(Kind.FORK_SINGLE, Support.TRIPOD,    0.50f,   0.36f,  0.030f, 0f,     0f,    0f,     0f,    0f,    0f,     0f,     0f,     0),
    FORK_SINGLE_HEAVY(Kind.FORK_SINGLE, Support.PIER, 0.62f,  0.30f,  0.105f, 0f,     0f,    0f,     0f,    0f,    0f,     0f,     0f,     0),
    FORK_OBSERVATORY(Kind.OBSERVATORY, Support.PIER, 0f,      0f,     0f,     0f,     0f,    0f,     0f,    0f,    0f,     0f,     0f,     0),
    ALTAZ_TRIPOD(Kind.ALTAZ, Support.TRIPOD,         0.56f,   0.36f,  0.024f, 0f,     0f,    0f,     0f,    0f,    0f,     0f,     0f,     0),
    SMART(Kind.SMART, Support.MINI_TRIPOD,           0.30f,   0.20f,  0.016f, 0f,     0f,    0f,     0f,    0f,    0f,     0f,     0f,     0);

    public enum Kind {
        GEM("gem"), DOBSONIAN("dobsonian"), FORK_DOUBLE("fork_double"), FORK_SINGLE("fork_single"), ALTAZ("altaz"),
        OBSERVATORY("observatory"), SMART("smart");

        public final String key;

        Kind(String key) {
            this.key = key;
        }
    }

    /** Como a montagem fica apoiada no chão. */
    public enum Support {
        NONE, TRIPOD, PIER_TRIPOD, PIER, MINI_TRIPOD
    }

    public final Kind kind;
    public final Support support;
    public final float headY, spread, legT;
    public final float rRA, lRA, rDec, lDec;
    public final float shaft, shaftR, rCW, tCW;
    public final int nCW;

    MountType(Kind kind, Support support, float headY, float spread, float legT, float rRA, float lRA, float rDec,
              float lDec, float shaft, float shaftR, float rCW, float tCW, int nCW) {
        this.kind = kind;
        this.support = support;
        this.headY = headY;
        this.spread = spread;
        this.legT = legT;
        this.rRA = rRA;
        this.lRA = lRA;
        this.rDec = rDec;
        this.lDec = lDec;
        this.shaft = shaft;
        this.shaftR = shaftR;
        this.rCW = rCW;
        this.tCW = tCW;
        this.nCW = nCW;
    }

    public String langKey() {
        return "telescopio.mount." + kind.key;
    }
}
