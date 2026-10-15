package com.modtelescopio.common;

/**
 * Tipos de montagem. As medidas já estão em blocos (1 bloco = 1 metro, com o modelo
 * reduzido por {@link com.modtelescopio.client.model.TelescopeModel#SCALE}).
 */
public enum MountType {
    //            tipo              altTripé abertura perna  rRA     lRA    rDEC    lDEC   haste  rHaste  rContra tContra nContra
    GEM_HEAVY(Kind.GEM,             0.62f,   0.46f,  0.042f, 0.062f, 0.25f, 0.056f, 0.20f, 0.30f, 0.012f, 0.072f, 0.055f, 2),
    GEM_MEDIUM(Kind.GEM,            0.57f,   0.40f,  0.034f, 0.050f, 0.20f, 0.046f, 0.16f, 0.25f, 0.010f, 0.058f, 0.050f, 2),
    GEM_LIGHT(Kind.GEM,             0.52f,   0.35f,  0.026f, 0.036f, 0.15f, 0.033f, 0.12f, 0.19f, 0.008f, 0.045f, 0.040f, 1),
    DOBSONIAN(Kind.DOBSONIAN,       0f,      0f,     0f,     0f,     0f,    0f,     0f,    0f,    0f,     0f,     0f,     0),
    FORK_DOUBLE(Kind.FORK_DOUBLE,   0.55f,   0.42f,  0.040f, 0f,     0f,    0f,     0f,    0f,    0f,     0f,     0f,     0),
    FORK_DOUBLE_SMALL(Kind.FORK_DOUBLE, 0.50f, 0.34f, 0.026f, 0f,    0f,    0f,     0f,    0f,    0f,     0f,     0f,     0),
    FORK_SINGLE(Kind.FORK_SINGLE,   0.50f,   0.36f,  0.030f, 0f,     0f,    0f,     0f,    0f,    0f,     0f,     0f,     0),
    ALTAZ_TRIPOD(Kind.ALTAZ,        0.56f,   0.36f,  0.024f, 0f,     0f,    0f,     0f,    0f,    0f,     0f,     0f,     0);

    public enum Kind {
        GEM("gem"), DOBSONIAN("dobsonian"), FORK_DOUBLE("fork_double"), FORK_SINGLE("fork_single"), ALTAZ("altaz");

        public final String key;

        Kind(String key) {
            this.key = key;
        }
    }

    public final Kind kind;
    public final float headY, spread, legT;
    public final float rRA, lRA, rDec, lDec;
    public final float shaft, shaftR, rCW, tCW;
    public final int nCW;

    MountType(Kind kind, float headY, float spread, float legT, float rRA, float lRA, float rDec, float lDec,
              float shaft, float shaftR, float rCW, float tCW, int nCW) {
        this.kind = kind;
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
