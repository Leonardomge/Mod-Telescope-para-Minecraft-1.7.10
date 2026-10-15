package com.modtelescopio.common;

// ARQUIVO GERADO por ferramentas/gerar_recursos.py - edite o catálogo lá e rode o script de novo.

/** Catálogo de telescópios do mod. Cada constante vira um bloco + item próprio. */
public enum TelescopeType {
    SKYWATCHER_250PDS_EQ6R("skywatcher_250pds_eq6r", "Sky-Watcher Explorer 250PDS", "EQ6-R Pro",
            OpticalDesign.NEWTONIAN, MountType.GEM_HEAVY, 254, 1200, 1.100f, 0.300f,
            0x1C1D21, 0xB8BCC2, 0xE9EAEC, 0xC3C7CC, true, "black",
            new String[]{"primary_mirror", "optical_tube", "eyepiece", "equatorial_mount", "dye_black", "iron_block"}),
    SKYWATCHER_FLEXTUBE_250P("skywatcher_flextube_250p", "Sky-Watcher Skyliner Flextube 250P", "Skyliner",
            OpticalDesign.NEWTONIAN_TRUSS, MountType.DOBSONIAN, 254, 1200, 1.200f, 0.300f,
            0x1C1D21, 0xB8BCC2, 0x2E2F33, 0x2E2F33, false, "black",
            new String[]{"primary_mirror", "optical_tube", "eyepiece", "dobsonian_base", "dye_black", "iron_bars"}),
    SKYWATCHER_FLEXTUBE_400P("skywatcher_flextube_400p", "Sky-Watcher Skyliner Flextube 400P SynScan", "SynScan GoTo",
            OpticalDesign.NEWTONIAN_TRUSS, MountType.DOBSONIAN, 406, 1800, 1.750f, 0.470f,
            0x1C1D21, 0xB8BCC2, 0x2E2F33, 0x2E2F33, true, "black",
            new String[]{"primary_mirror", "primary_mirror", "optical_tube", "eyepiece", "dobsonian_base", "dye_black", "iron_bars", "goto_controller", "diamond"}),
    ORION_SKYQUEST_XT8("orion_skyquest_xt8", "Orion SkyQuest XT8 Classic", "",
            OpticalDesign.NEWTONIAN, MountType.DOBSONIAN, 203, 1200, 1.170f, 0.230f,
            0x2B4C8C, 0x111214, 0x1E1F22, 0x1E1F22, false, "blue",
            new String[]{"primary_mirror", "optical_tube", "eyepiece", "dobsonian_base", "dye_blue"}),
    SKYWATCHER_EXPLORER_150P("skywatcher_explorer_150p", "Sky-Watcher Explorer 150P", "EQ3-2",
            OpticalDesign.NEWTONIAN, MountType.GEM_LIGHT, 150, 750, 0.700f, 0.180f,
            0xEDEEF0, 0x1C1D21, 0x2F3236, 0xA8ADB3, false, "white",
            new String[]{"primary_mirror", "optical_tube", "eyepiece", "equatorial_mount", "dye_white"}),
    CELESTRON_ASTROMASTER_130EQ("celestron_astromaster_130eq", "Celestron AstroMaster 130EQ", "CG-3",
            OpticalDesign.NEWTONIAN, MountType.GEM_LIGHT, 130, 650, 0.620f, 0.160f,
            0x26324A, 0xB8BCC2, 0x2F3236, 0xA8ADB3, false, "blue",
            new String[]{"primary_mirror", "optical_tube", "eyepiece", "equatorial_mount", "dye_blue"}),
    SKYWATCHER_EVOSTAR_80ED("skywatcher_evostar_80ed", "Sky-Watcher Evostar 80ED DS-Pro", "EQ5",
            OpticalDesign.REFRACTOR_APO, MountType.GEM_MEDIUM, 80, 600, 0.650f, 0.100f,
            0x161616, 0xC9A45C, 0xE9EAEC, 0xA8ADB3, false, "black",
            new String[]{"objective_lens", "optical_tube", "eyepiece", "equatorial_mount", "dye_black", "gold_ingot"}),
    SKYWATCHER_ESPRIT_120ED("skywatcher_esprit_120ed", "Sky-Watcher Esprit 120ED Triplet", "EQ6-R Pro",
            OpticalDesign.REFRACTOR_APO, MountType.GEM_HEAVY, 120, 840, 0.900f, 0.140f,
            0xEDEEF0, 0x2B2D31, 0xE9EAEC, 0xC3C7CC, true, "white",
            new String[]{"objective_lens", "objective_lens", "optical_tube", "eyepiece", "equatorial_mount", "dye_white", "gold_ingot", "iron_block"}),
    WILLIAM_OPTICS_REDCAT_51("william_optics_redcat_51", "William Optics RedCat 51", "Star Adventurer GTi",
            OpticalDesign.REFRACTOR_APO, MountType.GEM_LIGHT, 51, 250, 0.300f, 0.080f,
            0xB3262B, 0x2B2D31, 0x2F3136, 0x2F3136, true, "red",
            new String[]{"objective_lens", "optical_tube", "eyepiece", "equatorial_mount", "dye_red", "gold_ingot"}),
    TAKAHASHI_FSQ106EDX4("takahashi_fsq106edx4", "Takahashi FSQ-106EDX4", "EM-200 Temma",
            OpticalDesign.REFRACTOR_APO, MountType.GEM_HEAVY, 106, 530, 0.550f, 0.125f,
            0xDCE6D2, 0x6E9E78, 0xDCE6D2, 0xBFC4C9, true, "lime",
            new String[]{"objective_lens", "objective_lens", "optical_tube", "eyepiece", "equatorial_mount", "dye_lime", "gold_ingot", "iron_block"}),
    CELESTRON_ASTROMASTER_70AZ("celestron_astromaster_70az", "Celestron AstroMaster 70AZ", "",
            OpticalDesign.REFRACTOR_ACHROMAT, MountType.ALTAZ_TRIPOD, 70, 900, 0.800f, 0.085f,
            0x1C1D21, 0xB8BCC2, 0x2B2D31, 0xA8ADB3, false, "black",
            new String[]{"objective_lens", "optical_tube", "eyepiece", "tripod", "dye_black"}),
    MEADE_INFINITY_102("meade_infinity_102", "Meade Infinity 102", "",
            OpticalDesign.REFRACTOR_ACHROMAT, MountType.ALTAZ_TRIPOD, 102, 600, 0.620f, 0.120f,
            0x223A6B, 0x111214, 0x2B2D31, 0xA8ADB3, false, "blue",
            new String[]{"objective_lens", "optical_tube", "eyepiece", "tripod", "dye_blue"}),
    SKYWATCHER_EVOSTAR_150("skywatcher_evostar_150", "Sky-Watcher Evostar 150", "EQ5",
            OpticalDesign.REFRACTOR_ACHROMAT, MountType.GEM_MEDIUM, 150, 1200, 1.250f, 0.180f,
            0x1C1D21, 0xB8BCC2, 0xE9EAEC, 0xA8ADB3, false, "black",
            new String[]{"objective_lens", "objective_lens", "optical_tube", "eyepiece", "equatorial_mount", "dye_black"}),
    MEADE_ETX_125("meade_etx_125", "Meade ETX 125 Observer", "AudioStar",
            OpticalDesign.MAKSUTOV, MountType.FORK_DOUBLE_SMALL, 127, 1900, 0.360f, 0.150f,
            0x2E4F95, 0x1A1A1C, 0x1A1A1C, 0xA8ADB3, true, "blue",
            new String[]{"meniscus_corrector", "meniscus_corrector", "optical_tube", "eyepiece", "fork_mount", "dye_blue", "goto_controller"}),
    MEADE_ETX_90("meade_etx_90", "Meade ETX 90 Observer", "AudioStar",
            OpticalDesign.MAKSUTOV, MountType.FORK_DOUBLE_SMALL, 90, 1250, 0.280f, 0.110f,
            0x2E4F95, 0x1A1A1C, 0x1A1A1C, 0xA8ADB3, true, "blue",
            new String[]{"meniscus_corrector", "optical_tube", "eyepiece", "fork_mount", "dye_blue", "goto_controller"}),
    SKYWATCHER_SKYMAX_127("skywatcher_skymax_127", "Sky-Watcher Skymax 127", "AZ-GTi",
            OpticalDesign.MAKSUTOV, MountType.FORK_SINGLE, 127, 1500, 0.330f, 0.140f,
            0x2B2D33, 0xB8BCC2, 0x26282C, 0xA8ADB3, true, "gray",
            new String[]{"meniscus_corrector", "optical_tube", "eyepiece", "fork_mount", "dye_gray", "goto_controller"}),
    SKYWATCHER_SKYMAX_180_PRO("skywatcher_skymax_180_pro", "Sky-Watcher Skymax 180 PRO", "HEQ5 Pro",
            OpticalDesign.MAKSUTOV, MountType.GEM_MEDIUM, 180, 2700, 0.500f, 0.200f,
            0x1C1D21, 0xB8BCC2, 0xE9EAEC, 0xA8ADB3, true, "black",
            new String[]{"meniscus_corrector", "meniscus_corrector", "optical_tube", "eyepiece", "equatorial_mount", "dye_black", "goto_controller"}),
    CELESTRON_NEXSTAR_127SLT("celestron_nexstar_127slt", "Celestron NexStar 127SLT", "NexStar SLT",
            OpticalDesign.MAKSUTOV, MountType.FORK_SINGLE, 127, 1500, 0.330f, 0.140f,
            0x3A3F47, 0x111214, 0x26282C, 0xA8ADB3, true, "black",
            new String[]{"meniscus_corrector", "optical_tube", "eyepiece", "fork_mount", "dye_black", "goto_controller"}),
    CELESTRON_C14_CGXL("celestron_c14_cgxl", "Celestron C14 XLT", "CGX-L",
            OpticalDesign.SCHMIDT_CASSEGRAIN, MountType.GEM_HEAVY, 356, 3910, 0.790f, 0.400f,
            0x18191B, 0xB8BCC2, 0x2D2F33, 0x5A5E64, true, "black",
            new String[]{"schmidt_corrector", "schmidt_corrector", "optical_tube", "optical_tube", "eyepiece", "equatorial_mount", "dye_black", "iron_block", "diamond"}),
    CELESTRON_C11_CGX("celestron_c11_cgx", "Celestron C11 XLT", "CGX",
            OpticalDesign.SCHMIDT_CASSEGRAIN, MountType.GEM_HEAVY, 280, 2800, 0.610f, 0.310f,
            0x18191B, 0xB8BCC2, 0x2D2F33, 0x5A5E64, true, "black",
            new String[]{"schmidt_corrector", "schmidt_corrector", "optical_tube", "eyepiece", "equatorial_mount", "dye_black", "iron_block"}),
    CELESTRON_C925_AVX("celestron_c925_avx", "Celestron C9.25 XLT", "Advanced VX",
            OpticalDesign.SCHMIDT_CASSEGRAIN, MountType.GEM_MEDIUM, 235, 2350, 0.560f, 0.270f,
            0x18191B, 0xB8BCC2, 0x1E1F22, 0xB0B4BA, true, "black",
            new String[]{"schmidt_corrector", "optical_tube", "eyepiece", "equatorial_mount", "dye_black", "goto_controller"}),
    CELESTRON_NEXSTAR_8SE("celestron_nexstar_8se", "Celestron NexStar 8SE", "NexStar SE",
            OpticalDesign.SCHMIDT_CASSEGRAIN, MountType.FORK_SINGLE, 203, 2032, 0.430f, 0.230f,
            0xD9661F, 0x1A1A1C, 0x1F2023, 0xA8ADB3, true, "orange",
            new String[]{"schmidt_corrector", "schmidt_corrector", "optical_tube", "eyepiece", "fork_mount", "dye_orange", "goto_controller"}),
    CELESTRON_NEXSTAR_6SE("celestron_nexstar_6se", "Celestron NexStar 6SE", "NexStar SE",
            OpticalDesign.SCHMIDT_CASSEGRAIN, MountType.FORK_SINGLE, 150, 1500, 0.400f, 0.180f,
            0xD9661F, 0x1A1A1C, 0x1F2023, 0xA8ADB3, true, "orange",
            new String[]{"schmidt_corrector", "optical_tube", "eyepiece", "fork_mount", "dye_orange", "goto_controller"}),
    CELESTRON_CPC_1100("celestron_cpc_1100", "Celestron CPC 1100 GPS", "CPC",
            OpticalDesign.SCHMIDT_CASSEGRAIN, MountType.FORK_DOUBLE, 280, 2800, 0.610f, 0.310f,
            0x18191B, 0xB8BCC2, 0x5B6068, 0x5A5E64, true, "black",
            new String[]{"schmidt_corrector", "schmidt_corrector", "optical_tube", "eyepiece", "fork_mount", "dye_black", "goto_controller"}),
    MEADE_LX200_12_ACF("meade_lx200_12_acf", "Meade LX200-ACF 12\"", "LX200",
            OpticalDesign.SCHMIDT_CASSEGRAIN, MountType.FORK_DOUBLE, 305, 3048, 0.610f, 0.360f,
            0x1B2B57, 0x111214, 0x1D1E22, 0x5A5E64, true, "blue",
            new String[]{"schmidt_corrector", "schmidt_corrector", "optical_tube", "eyepiece", "fork_mount", "dye_blue", "goto_controller", "iron_block"}),
    MEADE_LX90_8_ACF("meade_lx90_8_acf", "Meade LX90 8\" ACF", "LX90",
            OpticalDesign.SCHMIDT_CASSEGRAIN, MountType.FORK_DOUBLE, 203, 2032, 0.430f, 0.230f,
            0x1B2B57, 0x111214, 0x1D1E22, 0x5A5E64, true, "blue",
            new String[]{"schmidt_corrector", "optical_tube", "eyepiece", "fork_mount", "dye_blue", "goto_controller"});

    /** Nome de registro (bloco e item). */
    public final String id;
    /** Nome comercial, mostrado no HUD. */
    public final String displayName;
    /** Nome comercial da montagem (pode ser vazio). */
    public final String mountName;
    public final OpticalDesign design;
    public final MountType mount;
    /** Abertura em mm. */
    public final int aperture;
    /** Distância focal em mm. */
    public final int focal;
    /** Comprimento e diâmetro reais do tubo, em metros. */
    public final float tubeLengthM, tubeDiameterM;
    public final int tubeColor, accentColor, mountColor, legColor;
    public final boolean goTo;
    /** Cor principal (black, white, orange...): corante da receita e textura das partículas. */
    public final String colorName;
    /** Ingredientes da receita sem forma (componentes do mod ou itens vanilla). */
    public final String[] recipe;

    TelescopeType(String id, String displayName, String mountName, OpticalDesign design, MountType mount,
                  int aperture, int focal, float tubeLengthM, float tubeDiameterM,
                  int tubeColor, int accentColor, int mountColor, int legColor, boolean goTo, String colorName,
                  String[] recipe) {
        this.id = id;
        this.displayName = displayName;
        this.mountName = mountName;
        this.design = design;
        this.mount = mount;
        this.aperture = aperture;
        this.focal = focal;
        this.tubeLengthM = tubeLengthM;
        this.tubeDiameterM = tubeDiameterM;
        this.tubeColor = tubeColor;
        this.accentColor = accentColor;
        this.mountColor = mountColor;
        this.legColor = legColor;
        this.goTo = goTo;
        this.colorName = colorName;
        this.recipe = recipe;
    }

    public float focalRatio() {
        return (float) focal / aperture;
    }

    public static TelescopeType byId(String id) {
        for (TelescopeType t : values()) {
            if (t.id.equals(id)) {
                return t;
            }
        }
        return null;
    }
}
