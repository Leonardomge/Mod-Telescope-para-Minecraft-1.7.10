package com.modtelescopio.common;

// ARQUIVO GERADO por ferramentas/gerar_recursos.py - edite a lista lá e rode o script de novo.

/** Peças de crafting usadas nas receitas dos telescópios. */
public enum ComponentType {
    OBJECTIVE_LENS("objective_lens", 1, new String[]{" P ", "PGP", " N "},
            new String[]{"P", "glass_pane", "G", "glass", "N", "gold_nugget"}),
    PRIMARY_MIRROR("primary_mirror", 1, new String[]{"GGG", "QIQ"},
            new String[]{"G", "glass", "Q", "quartz", "I", "iron_ingot"}),
    MENISCUS_CORRECTOR("meniscus_corrector", 1, new String[]{"GPG", " I "},
            new String[]{"G", "glass", "P", "glass_pane", "I", "iron_ingot"}),
    SCHMIDT_CORRECTOR("schmidt_corrector", 1, new String[]{"PPP", "PQP"},
            new String[]{"P", "glass_pane", "Q", "quartz"}),
    HYPERBOLIC_MIRROR("hyperbolic_mirror", 1, new String[]{"QGQ", "GIG"},
            new String[]{"Q", "quartz", "G", "glass", "I", "iron_ingot"}),
    EYEPIECE("eyepiece", 2, new String[]{"I", "P", "I"},
            new String[]{"I", "iron_ingot", "P", "glass_pane"}),
    ASTRO_CAMERA("astro_camera", 1, new String[]{"III", "RPR", "III"},
            new String[]{"I", "iron_ingot", "R", "redstone", "P", "glass_pane"}),
    OPTICAL_TUBE("optical_tube", 1, new String[]{"III", "   ", "III"},
            new String[]{"I", "iron_ingot"}),
    CARBON_TUBE("carbon_tube", 1, new String[]{"CCC", "   ", "CCC"},
            new String[]{"C", "coal"}),
    TRIPOD("tripod", 1, new String[]{" I ", "S S", "S S"},
            new String[]{"I", "iron_ingot", "S", "stick"}),
    EQUATORIAL_MOUNT("equatorial_mount", 1, new String[]{" C ", "IRI", " T "},
            new String[]{"C", "compass", "I", "iron_ingot", "R", "redstone", "T", "tripod"}),
    DOBSONIAN_BASE("dobsonian_base", 1, new String[]{"PIP", "PPP"},
            new String[]{"P", "planks", "I", "iron_ingot"}),
    FORK_MOUNT("fork_mount", 1, new String[]{"I I", "IRI", " T "},
            new String[]{"I", "iron_ingot", "R", "redstone", "T", "tripod"}),
    GOTO_CONTROLLER("goto_controller", 1, new String[]{"RQR", "ICI"},
            new String[]{"R", "redstone", "Q", "quartz", "I", "iron_ingot", "C", "clock"});

    public final String id;
    public final int count;
    /** Padrão da receita com forma. */
    public final String[] pattern;
    /** Pares (caractere, ingrediente). */
    public final String[] keys;

    ComponentType(String id, int count, String[] pattern, String[] keys) {
        this.id = id;
        this.count = count;
        this.pattern = pattern;
        this.keys = keys;
    }
}
