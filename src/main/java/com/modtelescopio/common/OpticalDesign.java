package com.modtelescopio.common;

/** Esquema óptico do tubo. Define a geometria do tubo e o nome mostrado no tooltip/HUD. */
public enum OpticalDesign {
    REFRACTOR_ACHROMAT("refractor_achromat"),
    REFRACTOR_APO("refractor_apo"),
    NEWTONIAN("newtonian"),
    NEWTONIAN_TRUSS("newtonian_truss"),
    MAKSUTOV("maksutov"),
    SCHMIDT_CASSEGRAIN("schmidt_cassegrain");

    public final String key;

    OpticalDesign(String key) {
        this.key = key;
    }

    public String langKey() {
        return "telescopio.design." + key;
    }

    public boolean isRefractor() {
        return this == REFRACTOR_ACHROMAT || this == REFRACTOR_APO;
    }
}
