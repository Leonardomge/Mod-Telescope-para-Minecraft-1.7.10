package com.modtelescopio.common;

// ARQUIVO GERADO por ferramentas/gerar_recursos.py - edite o catálogo lá e rode o script de novo.

/** Catálogo de telescópios do mod. Cada constante vira um bloco + item próprio. */
public enum TelescopeType {
    SKYWATCHER_250PDS_EQ6R("skywatcher_250pds_eq6r", "Sky-Watcher Explorer 250PDS", "EQ6-R Pro",
            OpticalDesign.NEWTONIAN, MountType.GEM_HEAVY, 254, 1200, 1.100f, 0.3000f,
            0x1C1D21, 0xB8BCC2, 0xE9EAEC, 0xC3C7CC, true, "black",
            1.5311f, 11.5f, true, 0, 0.0f, 0.0f,
            new String[]{"primary_mirror", "optical_tube", "eyepiece", "dye_black", "iron_block"},
            new String[]{"equatorial_mount"}),
    NEWTON_CARBON_300_EQ8R("newton_carbon_300_eq8r", "Carbon Newtonian 300/1500 f/5", "EQ8-R Pro",
            OpticalDesign.NEWTONIAN, MountType.GEM_XHEAVY, 300, 1500, 1.420f, 0.3600f,
            0x2A2C31, 0xB8BCC2, 0xE9EAEC, 0xC3C7CC, true, "black",
            1.5804f, 15.0f, true, 1, 0.0f, 0.0f,
            new String[]{"primary_mirror", "primary_mirror", "carbon_tube", "eyepiece", "dye_black"},
            new String[]{"equatorial_mount", "goto_controller", "iron_block"}),
    SKYWATCHER_EXPLORER_150P("skywatcher_explorer_150p", "Sky-Watcher Explorer 150P", "EQ3-2",
            OpticalDesign.NEWTONIAN, MountType.GEM_LIGHT, 150, 750, 0.700f, 0.1800f,
            0xEDEEF0, 0x1C1D21, 0x2F3236, 0xA8ADB3, false, "white",
            1.4196f, 5.5f, true, 0, 0.0f, 0.0f,
            new String[]{"primary_mirror", "optical_tube", "eyepiece", "dye_white"},
            new String[]{"equatorial_mount"}),
    CELESTRON_ASTROMASTER_130EQ("celestron_astromaster_130eq", "Celestron AstroMaster 130EQ", "CG-3",
            OpticalDesign.NEWTONIAN, MountType.GEM_LIGHT, 130, 650, 0.620f, 0.1600f,
            0x26324A, 0xB8BCC2, 0x2F3236, 0xA8ADB3, false, "blue",
            1.3982f, 4.5f, true, 0, 0.0f, 0.0f,
            new String[]{"primary_mirror", "optical_tube", "eyepiece", "dye_blue"},
            new String[]{"equatorial_mount"}),
    ORION_SKYQUEST_XT8("orion_skyquest_xt8", "Orion SkyQuest XT8 Classic", "",
            OpticalDesign.NEWTONIAN, MountType.DOBSONIAN, 203, 1200, 1.170f, 0.2300f,
            0x2B4C8C, 0x111214, 0x1E1F22, 0x1E1F22, false, "blue",
            1.2697f, 0.0f, false, 0, 0.0f, 0.0f,
            new String[]{"primary_mirror", "optical_tube", "eyepiece", "dye_blue"},
            new String[]{"dobsonian_base"}),
    SKYWATCHER_FLEXTUBE_250P("skywatcher_flextube_250p", "Sky-Watcher Skyliner Flextube 250P", "Skyliner",
            OpticalDesign.NEWTONIAN_TRUSS, MountType.DOBSONIAN, 254, 1200, 1.200f, 0.3000f,
            0x1C1D21, 0xB8BCC2, 0x2E2F33, 0x2E2F33, false, "black",
            1.3167f, 0.0f, false, 0, 0.0f, 0.0f,
            new String[]{"primary_mirror", "optical_tube", "eyepiece", "dye_black", "iron_bars"},
            new String[]{"dobsonian_base"}),
    SKYWATCHER_FLEXTUBE_300P("skywatcher_flextube_300p", "Sky-Watcher Skyliner Flextube 300P SynScan", "SynScan GoTo",
            OpticalDesign.NEWTONIAN_TRUSS, MountType.DOBSONIAN, 305, 1500, 1.450f, 0.3700f,
            0x1C1D21, 0xB8BCC2, 0x2E2F33, 0x2E2F33, true, "black",
            1.3637f, 0.0f, false, 0, 0.0f, 0.0f,
            new String[]{"primary_mirror", "optical_tube", "optical_tube", "eyepiece", "dye_black", "iron_bars"},
            new String[]{"dobsonian_base", "goto_controller"}),
    SKYWATCHER_FLEXTUBE_400P("skywatcher_flextube_400p", "Sky-Watcher Skyliner Flextube 400P SynScan", "SynScan GoTo",
            OpticalDesign.NEWTONIAN_TRUSS, MountType.DOBSONIAN, 406, 1800, 1.750f, 0.4700f,
            0x1C1D21, 0xB8BCC2, 0x2E2F33, 0x2E2F33, true, "black",
            1.4545f, 0.0f, false, 0, 0.0f, 0.0f,
            new String[]{"primary_mirror", "primary_mirror", "optical_tube", "eyepiece", "dye_black", "iron_bars", "diamond"},
            new String[]{"dobsonian_base", "goto_controller"}),
    SKYWATCHER_STARGATE_500P("skywatcher_stargate_500p", "Sky-Watcher Stargate 500P SynScan", "SynScan GoTo",
            OpticalDesign.NEWTONIAN_TRUSS, MountType.DOBSONIAN, 508, 2000, 1.950f, 0.6000f,
            0x1C1D21, 0xB8BCC2, 0x2E2F33, 0x2E2F33, true, "black",
            1.5093f, 0.0f, false, 18, 0.0f, 0.0f,
            new String[]{"primary_mirror", "primary_mirror", "carbon_tube", "eyepiece", "dye_black", "iron_bars", "diamond"},
            new String[]{"dobsonian_base", "goto_controller"}),
    DOB_FLEXTUBE_600("dob_flextube_600", "Flextube Dobsonian 600 mm f/3.3", "",
            OpticalDesign.NEWTONIAN_TRUSS, MountType.DOBSONIAN, 600, 1980, 1.850f, 0.7200f,
            0x1C1D21, 0xB8BCC2, 0x2E2F33, 0x2E2F33, false, "black",
            1.5588f, 0.0f, false, 2, 0.0f, 0.0f,
            new String[]{"primary_mirror", "primary_mirror", "primary_mirror", "carbon_tube", "eyepiece", "iron_bars", "diamond"},
            new String[]{"dobsonian_base"}),
    DOB_FLEXTUBE_1000("dob_flextube_1000", "Flextube Dobsonian 1000 mm f/3.3", "",
            OpticalDesign.NEWTONIAN_TRUSS, MountType.DOBSONIAN, 1000, 3300, 3.050f, 1.1500f,
            0x1C1D21, 0xB8BCC2, 0x2E2F33, 0x2E2F33, true, "black",
            1.7737f, 0.0f, false, 2, 0.0f, 0.0f,
            new String[]{"primary_mirror", "primary_mirror", "primary_mirror", "primary_mirror", "carbon_tube", "eyepiece", "iron_bars", "diamond_block"},
            new String[]{"dobsonian_base"}),
    SKYWATCHER_EVOSTAR_80ED("skywatcher_evostar_80ed", "Sky-Watcher Evostar 80ED DS-Pro", "EQ5",
            OpticalDesign.REFRACTOR_APO, MountType.GEM_MEDIUM, 80, 600, 0.650f, 0.1000f,
            0x161616, 0xC9A45C, 0xE9EAEC, 0xA8ADB3, false, "black",
            1.3446f, 3.0f, true, 0, 0.0f, 0.0f,
            new String[]{"objective_lens", "optical_tube", "eyepiece", "dye_black", "gold_ingot"},
            new String[]{"equatorial_mount"}),
    SKYWATCHER_ESPRIT_120ED("skywatcher_esprit_120ed", "Sky-Watcher Esprit 120ED Triplet", "EQ6-R Pro",
            OpticalDesign.REFRACTOR_APO, MountType.GEM_HEAVY, 120, 840, 0.900f, 0.1400f,
            0xEDEEF0, 0x2B2D31, 0xE9EAEC, 0xC3C7CC, true, "white",
            1.3875f, 8.5f, true, 0, 0.0f, 0.0f,
            new String[]{"objective_lens", "objective_lens", "optical_tube", "eyepiece", "dye_white", "gold_ingot", "iron_block"},
            new String[]{"equatorial_mount"}),
    WILLIAM_OPTICS_REDCAT_51("william_optics_redcat_51", "William Optics RedCat 51", "Star Adventurer GTi",
            OpticalDesign.REFRACTOR_APO, MountType.GEM_LIGHT, 51, 250, 0.300f, 0.0800f,
            0xB3262B, 0x2B2D31, 0x2F3136, 0x2F3136, true, "red",
            1.3136f, 1.6f, true, 0, 0.0f, 0.0f,
            new String[]{"objective_lens", "optical_tube", "eyepiece", "dye_red", "gold_ingot"},
            new String[]{"equatorial_mount"}),
    TAKAHASHI_FSQ106EDX4("takahashi_fsq106edx4", "Takahashi FSQ-106EDX4", "EM-200 Temma",
            OpticalDesign.REFRACTOR_APO, MountType.GEM_HEAVY, 106, 530, 0.550f, 0.1250f,
            0xDCE6D2, 0x6E9E78, 0xDCE6D2, 0xBFC4C9, true, "lime",
            1.3725f, 6.5f, true, 0, 0.0f, 0.0f,
            new String[]{"objective_lens", "objective_lens", "optical_tube", "eyepiece", "dye_lime", "gold_ingot", "iron_block"},
            new String[]{"equatorial_mount"}),
    CELESTRON_ASTROMASTER_70AZ("celestron_astromaster_70az", "Celestron AstroMaster 70AZ", "",
            OpticalDesign.REFRACTOR_ACHROMAT, MountType.ALTAZ_TRIPOD, 70, 900, 0.800f, 0.0850f,
            0x1C1D21, 0xB8BCC2, 0x2B2D31, 0xA8ADB3, false, "black",
            1.3339f, 1.5f, true, 0, 0.0f, 0.0f,
            new String[]{"objective_lens", "optical_tube", "eyepiece", "dye_black"},
            new String[]{"tripod"}),
    MEADE_INFINITY_102("meade_infinity_102", "Meade Infinity 102", "",
            OpticalDesign.REFRACTOR_ACHROMAT, MountType.ALTAZ_TRIPOD, 102, 600, 0.620f, 0.1200f,
            0x223A6B, 0x111214, 0x2B2D31, 0xA8ADB3, false, "blue",
            1.3682f, 2.5f, true, 0, 0.0f, 0.0f,
            new String[]{"objective_lens", "optical_tube", "eyepiece", "dye_blue"},
            new String[]{"tripod"}),
    SKYWATCHER_EVOSTAR_150("skywatcher_evostar_150", "Sky-Watcher Evostar 150", "EQ5",
            OpticalDesign.REFRACTOR_ACHROMAT, MountType.GEM_MEDIUM, 150, 1200, 1.250f, 0.1800f,
            0x1C1D21, 0xB8BCC2, 0xE9EAEC, 0xA8ADB3, false, "black",
            1.4196f, 10.5f, true, 0, 0.0f, 0.0f,
            new String[]{"objective_lens", "objective_lens", "optical_tube", "eyepiece", "dye_black"},
            new String[]{"equatorial_mount"}),
    MEADE_ETX_125("meade_etx_125", "Meade ETX 125 Observer", "AudioStar",
            OpticalDesign.MAKSUTOV, MountType.FORK_DOUBLE_SMALL, 127, 1900, 0.360f, 0.1500f,
            0x2E4F95, 0x1A1A1C, 0x1A1A1C, 0xA8ADB3, true, "blue",
            1.3950f, 0.0f, false, 0, 0.0f, 0.0f,
            new String[]{"meniscus_corrector", "meniscus_corrector", "optical_tube", "eyepiece", "dye_blue"},
            new String[]{"fork_mount", "goto_controller"}),
    MEADE_ETX_90("meade_etx_90", "Meade ETX 90 Observer", "AudioStar",
            OpticalDesign.MAKSUTOV, MountType.FORK_DOUBLE_SMALL, 90, 1250, 0.280f, 0.1100f,
            0x2E4F95, 0x1A1A1C, 0x1A1A1C, 0xA8ADB3, true, "blue",
            1.3554f, 0.0f, false, 0, 0.0f, 0.0f,
            new String[]{"meniscus_corrector", "optical_tube", "eyepiece", "dye_blue"},
            new String[]{"fork_mount", "goto_controller"}),
    SKYWATCHER_SKYMAX_127("skywatcher_skymax_127", "Sky-Watcher Skymax 127", "AZ-GTi",
            OpticalDesign.MAKSUTOV, MountType.FORK_SINGLE, 127, 1500, 0.330f, 0.1400f,
            0x2B2D33, 0xB8BCC2, 0x26282C, 0xA8ADB3, true, "gray",
            1.3950f, 3.5f, true, 0, 0.0f, 0.0f,
            new String[]{"meniscus_corrector", "optical_tube", "eyepiece", "dye_gray"},
            new String[]{"fork_mount", "goto_controller"}),
    SKYWATCHER_SKYMAX_180_PRO("skywatcher_skymax_180_pro", "Sky-Watcher Skymax 180 PRO", "HEQ5 Pro",
            OpticalDesign.MAKSUTOV, MountType.GEM_MEDIUM, 180, 2700, 0.500f, 0.2000f,
            0x1C1D21, 0xB8BCC2, 0xE9EAEC, 0xA8ADB3, true, "black",
            1.4518f, 6.5f, true, 0, 0.0f, 0.0f,
            new String[]{"meniscus_corrector", "meniscus_corrector", "optical_tube", "eyepiece", "dye_black"},
            new String[]{"equatorial_mount", "goto_controller"}),
    CELESTRON_NEXSTAR_127SLT("celestron_nexstar_127slt", "Celestron NexStar 127SLT", "NexStar SLT",
            OpticalDesign.MAKSUTOV, MountType.FORK_SINGLE, 127, 1500, 0.330f, 0.1400f,
            0x3A3F47, 0x111214, 0x26282C, 0xA8ADB3, true, "black",
            1.3950f, 3.5f, true, 0, 0.0f, 0.0f,
            new String[]{"meniscus_corrector", "optical_tube", "eyepiece", "dye_black"},
            new String[]{"fork_mount", "goto_controller"}),
    CELESTRON_C14_CGXL("celestron_c14_cgxl", "Celestron C14 XLT", "CGX-L",
            OpticalDesign.SCHMIDT_CASSEGRAIN, MountType.GEM_HEAVY, 356, 3910, 0.790f, 0.4000f,
            0x18191B, 0xB8BCC2, 0x2D2F33, 0x5A5E64, true, "black",
            1.6404f, 20.5f, true, 0, 0.0f, 0.0f,
            new String[]{"schmidt_corrector", "schmidt_corrector", "optical_tube", "optical_tube", "eyepiece", "dye_black", "iron_block", "diamond"},
            new String[]{"equatorial_mount"}),
    CELESTRON_C11_CGX("celestron_c11_cgx", "Celestron C11 XLT", "CGX",
            OpticalDesign.SCHMIDT_CASSEGRAIN, MountType.GEM_HEAVY, 280, 2800, 0.610f, 0.3100f,
            0x18191B, 0xB8BCC2, 0x2D2F33, 0x5A5E64, true, "black",
            1.5589f, 12.5f, true, 0, 0.0f, 0.0f,
            new String[]{"schmidt_corrector", "schmidt_corrector", "optical_tube", "eyepiece", "dye_black", "iron_block"},
            new String[]{"equatorial_mount"}),
    CELESTRON_C925_AVX("celestron_c925_avx", "Celestron C9.25 XLT", "Advanced VX",
            OpticalDesign.SCHMIDT_CASSEGRAIN, MountType.GEM_MEDIUM, 235, 2350, 0.560f, 0.2700f,
            0x18191B, 0xB8BCC2, 0x1E1F22, 0xB0B4BA, true, "black",
            1.5107f, 9.5f, true, 0, 0.0f, 0.0f,
            new String[]{"schmidt_corrector", "optical_tube", "eyepiece", "dye_black"},
            new String[]{"equatorial_mount", "goto_controller"}),
    CELESTRON_NEXSTAR_8SE("celestron_nexstar_8se", "Celestron NexStar 8SE", "NexStar SE",
            OpticalDesign.SCHMIDT_CASSEGRAIN, MountType.FORK_SINGLE, 203, 2032, 0.430f, 0.2300f,
            0xD9661F, 0x1A1A1C, 0x1F2023, 0xA8ADB3, true, "orange",
            1.4764f, 5.7f, true, 0, 0.0f, 0.0f,
            new String[]{"schmidt_corrector", "schmidt_corrector", "optical_tube", "eyepiece", "dye_orange"},
            new String[]{"fork_mount", "goto_controller"}),
    CELESTRON_NEXSTAR_6SE("celestron_nexstar_6se", "Celestron NexStar 6SE", "NexStar SE",
            OpticalDesign.SCHMIDT_CASSEGRAIN, MountType.FORK_SINGLE, 150, 1500, 0.400f, 0.1800f,
            0xD9661F, 0x1A1A1C, 0x1F2023, 0xA8ADB3, true, "orange",
            1.4196f, 4.5f, true, 0, 0.0f, 0.0f,
            new String[]{"schmidt_corrector", "optical_tube", "eyepiece", "dye_orange"},
            new String[]{"fork_mount", "goto_controller"}),
    CELESTRON_CPC_1100("celestron_cpc_1100", "Celestron CPC 1100 GPS", "CPC",
            OpticalDesign.SCHMIDT_CASSEGRAIN, MountType.FORK_DOUBLE, 280, 2800, 0.610f, 0.3100f,
            0x18191B, 0xB8BCC2, 0x5B6068, 0x5A5E64, true, "black",
            1.5589f, 12.5f, true, 0, 0.0f, 0.0f,
            new String[]{"schmidt_corrector", "schmidt_corrector", "optical_tube", "eyepiece", "dye_black"},
            new String[]{"fork_mount", "goto_controller"}),
    MEADE_LX200_12_ACF("meade_lx200_12_acf", "Meade LX200-ACF 12\"", "LX200",
            OpticalDesign.SCHMIDT_CASSEGRAIN, MountType.FORK_DOUBLE, 305, 3048, 0.610f, 0.3600f,
            0x1B2B57, 0x111214, 0x1D1E22, 0x5A5E64, true, "blue",
            1.5857f, 20.0f, true, 0, 0.0f, 0.0f,
            new String[]{"schmidt_corrector", "schmidt_corrector", "optical_tube", "eyepiece", "dye_blue", "iron_block"},
            new String[]{"fork_mount", "goto_controller"}),
    MEADE_LX90_8_ACF("meade_lx90_8_acf", "Meade LX90 8\" ACF", "LX90",
            OpticalDesign.SCHMIDT_CASSEGRAIN, MountType.FORK_DOUBLE, 203, 2032, 0.430f, 0.2300f,
            0x1B2B57, 0x111214, 0x1D1E22, 0x5A5E64, true, "blue",
            1.4764f, 7.0f, true, 0, 0.0f, 0.0f,
            new String[]{"schmidt_corrector", "optical_tube", "eyepiece", "dye_blue"},
            new String[]{"fork_mount", "goto_controller"}),
    GSO_RC6("gso_rc6", "GSO Ritchey-Chrétien 6\" (152/1370)", "HEQ5 Pro",
            OpticalDesign.RITCHEY_CHRETIEN, MountType.GEM_MEDIUM, 152, 1370, 0.440f, 0.1900f,
            0xEDEEF0, 0x1C1D21, 0xE9EAEC, 0xA8ADB3, true, "white",
            1.4218f, 6.0f, true, 64, 0.0f, 0.0f,
            new String[]{"hyperbolic_mirror", "optical_tube", "eyepiece", "dye_white"},
            new String[]{"equatorial_mount", "goto_controller"}),
    GSO_RC8_CARBON("gso_rc8_carbon", "GSO Ritchey-Chrétien 8\" Carbon (203/1624)", "EQ6-R Pro",
            OpticalDesign.RITCHEY_CHRETIEN, MountType.GEM_HEAVY, 203, 1624, 0.580f, 0.2500f,
            0x2A2C31, 0xB8BCC2, 0xE9EAEC, 0xC3C7CC, true, "black",
            1.4764f, 7.5f, true, 65, 0.0f, 0.0f,
            new String[]{"hyperbolic_mirror", "carbon_tube", "eyepiece", "dye_black"},
            new String[]{"equatorial_mount", "goto_controller"}),
    GSO_RC10_TRUSS("gso_rc10_truss", "GSO Ritchey-Chrétien 10\" Truss (254/2032)", "CGX",
            OpticalDesign.RITCHEY_CHRETIEN_TRUSS, MountType.GEM_HEAVY, 254, 2032, 0.780f, 0.3300f,
            0xEDEEF0, 0x1C1D21, 0x2D2F33, 0x5A5E64, true, "white",
            1.5311f, 15.0f, true, 80, 0.0f, 0.0f,
            new String[]{"hyperbolic_mirror", "hyperbolic_mirror", "optical_tube", "eyepiece", "iron_bars"},
            new String[]{"equatorial_mount", "goto_controller"}),
    GSO_RC12_TRUSS("gso_rc12_truss", "GSO Ritchey-Chrétien 12\" Truss (305/2440)", "EQ8-R Pro",
            OpticalDesign.RITCHEY_CHRETIEN_TRUSS, MountType.GEM_XHEAVY, 305, 2440, 0.920f, 0.4000f,
            0xEDEEF0, 0x1C1D21, 0xE9EAEC, 0xC3C7CC, true, "white",
            1.5857f, 22.0f, true, 80, 0.0f, 0.0f,
            new String[]{"hyperbolic_mirror", "hyperbolic_mirror", "carbon_tube", "eyepiece", "iron_bars"},
            new String[]{"equatorial_mount", "goto_controller"}),
    GSO_RC16_TRUSS("gso_rc16_truss", "GSO Ritchey-Chrétien 16\" Truss (406/3250)", "PlaneWave L-350",
            OpticalDesign.RITCHEY_CHRETIEN_TRUSS, MountType.FORK_SINGLE_HEAVY, 406, 3250, 1.150f, 0.5200f,
            0xEDEEF0, 0x1C1D21, 0x1E1F23, 0x34373D, true, "white",
            1.6912f, 43.0f, true, 80, 0.0f, 0.0f,
            new String[]{"hyperbolic_mirror", "hyperbolic_mirror", "carbon_tube", "eyepiece", "iron_bars", "iron_block"},
            new String[]{"fork_mount", "goto_controller"}),
    PLANEWAVE_CDK125("planewave_cdk125", "PlaneWave CDK12.5", "L-350",
            OpticalDesign.CDK, MountType.FORK_SINGLE_HEAVY, 318, 2541, 0.980f, 0.4200f,
            0x1E1F23, 0xB8BCC2, 0x1E1F23, 0x34373D, true, "black",
            1.5996f, 21.0f, true, 64, 0.0f, 0.0f,
            new String[]{"hyperbolic_mirror", "objective_lens", "carbon_tube", "astro_camera", "dye_black"},
            new String[]{"fork_mount", "goto_controller"}),
    PLANEWAVE_CDK14("planewave_cdk14", "PlaneWave CDK14", "L-350",
            OpticalDesign.CDK, MountType.FORK_SINGLE_HEAVY, 356, 2563, 1.030f, 0.4600f,
            0x1E1F23, 0xB8BCC2, 0x1E1F23, 0x34373D, true, "black",
            1.6404f, 24.0f, true, 64, 0.0f, 0.0f,
            new String[]{"hyperbolic_mirror", "hyperbolic_mirror", "objective_lens", "carbon_tube", "astro_camera", "dye_black"},
            new String[]{"fork_mount", "goto_controller"}),
    PLANEWAVE_CDK17("planewave_cdk17", "PlaneWave CDK17", "L-350",
            OpticalDesign.CDK, MountType.FORK_SINGLE_HEAVY, 432, 2939, 1.220f, 0.5600f,
            0x1E1F23, 0xB8BCC2, 0x1E1F23, 0x34373D, true, "black",
            1.7075f, 41.0f, true, 64, 0.0f, 0.0f,
            new String[]{"hyperbolic_mirror", "hyperbolic_mirror", "objective_lens", "objective_lens", "carbon_tube", "astro_camera", "dye_black"},
            new String[]{"fork_mount", "goto_controller"}),
    PLANEWAVE_CDK20("planewave_cdk20", "PlaneWave CDK20", "L-500",
            OpticalDesign.CDK, MountType.FORK_SINGLE_HEAVY, 508, 3454, 1.420f, 0.6600f,
            0x1E1F23, 0xB8BCC2, 0x1E1F23, 0x34373D, true, "black",
            1.7550f, 54.0f, true, 64, 0.0f, 0.0f,
            new String[]{"hyperbolic_mirror", "hyperbolic_mirror", "objective_lens", "objective_lens", "carbon_tube", "carbon_tube", "astro_camera"},
            new String[]{"fork_mount", "goto_controller"}),
    PLANEWAVE_CDK24("planewave_cdk24", "PlaneWave CDK24", "L-600",
            OpticalDesign.CDK, MountType.FORK_SINGLE_HEAVY, 610, 3962, 1.620f, 0.8000f,
            0x1E1F23, 0xB8BCC2, 0x1E1F23, 0x34373D, true, "black",
            1.8188f, 109.0f, true, 64, 0.0f, 0.0f,
            new String[]{"hyperbolic_mirror", "hyperbolic_mirror", "hyperbolic_mirror", "objective_lens", "objective_lens", "carbon_tube", "astro_camera"},
            new String[]{"fork_mount", "goto_controller"}),
    PLANEWAVE_CDK700("planewave_cdk700", "PlaneWave CDK700", "Alt-Az Direct Drive",
            OpticalDesign.CDK, MountType.FORK_OBSERVATORY, 700, 4540, 1.750f, 0.9800f,
            0x1E1F23, 0xB8BCC2, 0xD7D9DC, 0x34373D, true, "black",
            1.8750f, 0.0f, false, 64, 0.0f, 0.0f,
            new String[]{"hyperbolic_mirror", "hyperbolic_mirror", "hyperbolic_mirror", "objective_lens", "objective_lens", "carbon_tube", "carbon_tube", "astro_camera"},
            new String[]{"fork_mount"}),
    PLANEWAVE_CDK1000("planewave_cdk1000", "PlaneWave CDK1000 (PW1000)", "Alt-Az Direct Drive",
            OpticalDesign.CDK, MountType.FORK_OBSERVATORY, 1000, 6000, 2.500f, 1.3800f,
            0x1E1F23, 0xB8BCC2, 0xD7D9DC, 0x34373D, true, "black",
            2.0625f, 0.0f, false, 64, 0.0f, 0.0f,
            new String[]{"hyperbolic_mirror", "hyperbolic_mirror", "hyperbolic_mirror", "objective_lens", "objective_lens", "carbon_tube", "carbon_tube", "diamond"},
            new String[]{"fork_mount"}),
    PLANEWAVE_CDK2000("planewave_cdk2000", "PlaneWave CDK2000 (2 m concept)", "Alt-Az Direct Drive",
            OpticalDesign.CDK, MountType.FORK_OBSERVATORY, 2000, 12000, 4.600f, 2.7000f,
            0x1E1F23, 0xB8BCC2, 0xD7D9DC, 0x34373D, true, "black",
            1.7800f, 0.0f, false, 72, 0.0f, 0.0f,
            new String[]{"hyperbolic_mirror", "hyperbolic_mirror", "hyperbolic_mirror", "objective_lens", "objective_lens", "carbon_tube", "nether_star", "diamond_block"},
            new String[]{"fork_mount"}),
    CELESTRON_RASA8("celestron_rasa8", "Celestron RASA 8", "Advanced VX",
            OpticalDesign.RASA, MountType.GEM_MEDIUM, 203, 400, 0.620f, 0.2500f,
            0x18191B, 0xB8BCC2, 0x1E1F22, 0xB0B4BA, true, "black",
            1.4764f, 7.5f, true, 0, 23.5f, 15.6f,
            new String[]{"schmidt_corrector", "optical_tube", "astro_camera", "dye_black"},
            new String[]{"equatorial_mount", "goto_controller"}),
    CELESTRON_RASA11("celestron_rasa11", "Celestron RASA 11 V2", "CGX",
            OpticalDesign.RASA, MountType.GEM_HEAVY, 279, 620, 0.880f, 0.3200f,
            0x18191B, 0xB8BCC2, 0x2D2F33, 0x5A5E64, true, "black",
            1.5579f, 18.0f, true, 0, 36.0f, 24.0f,
            new String[]{"schmidt_corrector", "schmidt_corrector", "optical_tube", "astro_camera", "dye_black"},
            new String[]{"equatorial_mount", "goto_controller"}),
    ZWO_SEESTAR_S50("zwo_seestar_s50", "ZWO Seestar S50", "",
            OpticalDesign.SMART, MountType.SMART, 50, 250, 0.257f, 0.1425f,
            0x202226, 0x8A8F96, 0x202226, 0x2A2C31, true, "black",
            1.3125f, 0.0f, false, 0, 5.6f, 3.2f,
            new String[]{"objective_lens", "astro_camera", "goto_controller", "dye_black"},
            new String[]{"tripod"}),
    ZWO_SEESTAR_S30("zwo_seestar_s30", "ZWO Seestar S30", "",
            OpticalDesign.SMART, MountType.SMART, 30, 150, 0.200f, 0.1100f,
            0xEEEFF1, 0x2A2B2F, 0xEEEFF1, 0x2A2C31, true, "white",
            1.3125f, 0.0f, false, 0, 5.6f, 3.2f,
            new String[]{"objective_lens", "astro_camera", "goto_controller", "dye_white"},
            new String[]{"tripod"}),
    DWARFLAB_DWARF3("dwarflab_dwarf3", "DwarfLab DWARF 3", "",
            OpticalDesign.SMART, MountType.SMART, 35, 150, 0.210f, 0.1200f,
            0x2C2E33, 0x0E0E10, 0x2C2E33, 0x2A2C31, true, "gray",
            1.3125f, 0.0f, false, 4, 7.7f, 4.3f,
            new String[]{"objective_lens", "objective_lens", "astro_camera", "goto_controller", "dye_gray"},
            new String[]{"tripod"}),
    DWARFLAB_DRACO("dwarflab_draco", "DwarfLab Draco", "",
            OpticalDesign.SMART, MountType.SMART, 90, 340, 0.386f, 0.1700f,
            0x26282C, 0xB9BDC3, 0x26282C, 0x2A2C31, true, "gray",
            1.3554f, 0.0f, false, 4, 10.0f, 5.6f,
            new String[]{"objective_lens", "objective_lens", "astro_camera", "astro_camera", "goto_controller", "dye_gray"},
            new String[]{"tripod"}),
    VAONIS_VESPERA2("vaonis_vespera2", "Vaonis Vespera II", "",
            OpticalDesign.SMART, MountType.SMART, 50, 250, 0.400f, 0.2000f,
            0xF2F3F5, 0x9EA3AA, 0xF2F3F5, 0x2A2C31, true, "white",
            1.3125f, 0.0f, false, 0, 11.2f, 6.3f,
            new String[]{"objective_lens", "astro_camera", "astro_camera", "goto_controller", "dye_white"},
            new String[]{"tripod"}),
    UNISTELLAR_EVSCOPE2("unistellar_evscope2", "Unistellar eVscope 2", "",
            OpticalDesign.SMART, MountType.SMART, 114, 450, 0.600f, 0.1700f,
            0x1B1C20, 0xB5B9BF, 0x1B1C20, 0x2A2C31, true, "black",
            1.3811f, 0.0f, false, 32, 7.4f, 5.6f,
            new String[]{"primary_mirror", "astro_camera", "goto_controller", "dye_black"},
            new String[]{"tripod"});

    // flags (valores iguais aos de FLAGS no gerador)
    public static final int FLAG_CARBON = 1;
    public static final int FLAG_LADDER = 2;
    public static final int FLAG_DUAL_LENS = 4;
    public static final int FLAG_CONCEPT = 8;
    public static final int FLAG_OPEN_TRUSS = 16;
    public static final int FLAG_TUBE_BODY = 32;
    public static final int FLAG_CAMERA = 64;

    /** Nome de registro (bloco e item). */
    public final String id;
    /** Nome comercial (o nome traduzido vem dos arquivos de idioma). */
    public final String displayName;
    /** Nome comercial da montagem (pode ser vazio). */
    public final String mountName;
    public final OpticalDesign design;
    public final MountType mount;
    /** Abertura em mm. */
    public final int aperture;
    /** Distância focal em mm. */
    public final int focal;
    /** Comprimento e diâmetro reais do tubo, em metros (nos inteligentes: medidas do corpo). */
    public final float tubeLengthM, tubeDiameterM;
    public final int tubeColor, accentColor, mountColor, legColor;
    public final boolean goTo;
    /** Cor principal (black, white, orange...): textura das partículas. */
    public final String colorName;
    /** Multiplicador de tamanho do modelo (cresce com a abertura). */
    public final float sizeScale;
    /** Peso do tubo óptico em kg (para o limite de carga das montagens). */
    public final float weightKg;
    /** Existe o item "tubo óptico" separado, que pode ir numa montagem-bloco. */
    public final boolean hasOta;
    public final int flags;
    /** Sensor da câmera em mm (0 quando o telescópio usa oculares). */
    public final float sensorW, sensorH;
    /** Ingredientes do tubo óptico e da montagem (receita completa = os dois juntos). */
    public final String[] opticsRecipe, mountRecipe;

    TelescopeType(String id, String displayName, String mountName, OpticalDesign design, MountType mount,
                  int aperture, int focal, float tubeLengthM, float tubeDiameterM,
                  int tubeColor, int accentColor, int mountColor, int legColor, boolean goTo, String colorName,
                  float sizeScale, float weightKg, boolean hasOta, int flags, float sensorW, float sensorH,
                  String[] opticsRecipe, String[] mountRecipe) {
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
        this.sizeScale = sizeScale;
        this.weightKg = weightKg;
        this.hasOta = hasOta;
        this.flags = flags;
        this.sensorW = sensorW;
        this.sensorH = sensorH;
        this.opticsRecipe = opticsRecipe;
        this.mountRecipe = mountRecipe;
    }

    public boolean has(int flag) {
        return (flags & flag) != 0;
    }

    public float focalRatio() {
        return (float) focal / aperture;
    }

    /** Nome de registro do item "tubo óptico". */
    public String otaId() {
        return "tubo_" + id;
    }

    /** Receita completa (tubo + montagem). */
    public String[] fullRecipe() {
        String[] out = new String[opticsRecipe.length + mountRecipe.length];
        System.arraycopy(opticsRecipe, 0, out, 0, opticsRecipe.length);
        System.arraycopy(mountRecipe, 0, out, opticsRecipe.length, mountRecipe.length);
        return out;
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
