package com.modtelescopio.common;

/** Esquema óptico do tubo. Define a geometria do tubo e o nome mostrado no tooltip/HUD. */
public enum OpticalDesign {
    REFRACTOR_ACHROMAT("refractor_achromat"),
    REFRACTOR_APO("refractor_apo"),
    NEWTONIAN("newtonian"),
    NEWTONIAN_TRUSS("newtonian_truss"),
    MAKSUTOV("maksutov"),
    SCHMIDT_CASSEGRAIN("schmidt_cassegrain"),
    RITCHEY_CHRETIEN("ritchey_chretien"),
    RITCHEY_CHRETIEN_TRUSS("ritchey_chretien_truss"),
    CDK("cdk"),
    RASA("rasa"),
    SMART("smart");

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

    /** Telescópios sem ocular: a observação é pela "tela" da câmera (inteligentes e RASA). */
    public boolean usesCamera() {
        return this == SMART || this == RASA;
    }
}
