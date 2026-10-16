package com.modtelescopio.common;

// ARQUIVO GERADO por ferramentas/gerar_recursos.py - edite a lista lá e rode o script de novo.

/** Montagens em tripé/pilar que viram blocos: o jogador instala um tubo óptico em cima. */
public enum MountBlockType {
    MOUNT_AZ_GTI("mount_az_gti", "Sky-Watcher AZ-GTi Mount", MountType.FORK_SINGLE, 0x2C2E33, 0xA8ADB3, true, 5.0f, 1.3125f, "gray",
            new String[]{"fork_mount", "goto_controller", "iron_ingot"}),
    MOUNT_STAR_ADVENTURER_GTI("mount_star_adventurer_gti", "Sky-Watcher Star Adventurer GTi Mount", MountType.GEM_LIGHT, 0x2F3136, 0x2F3136, true, 5.0f, 1.3125f, "black",
            new String[]{"equatorial_mount", "goto_controller", "iron_ingot"}),
    MOUNT_EQ5("mount_eq5", "Sky-Watcher EQ5 Mount", MountType.GEM_MEDIUM, 0xE9EAEC, 0xA8ADB3, false, 9.0f, 1.3458f, "white",
            new String[]{"equatorial_mount", "iron_ingot", "iron_ingot"}),
    MOUNT_HEQ5_PRO("mount_heq5_pro", "Sky-Watcher HEQ5 Pro Mount", MountType.GEM_MEDIUM, 0xE9EAEC, 0xA8ADB3, true, 11.0f, 1.3625f, "white",
            new String[]{"equatorial_mount", "goto_controller", "iron_ingot", "iron_ingot"}),
    MOUNT_EQ6R_PRO("mount_eq6r_pro", "Sky-Watcher EQ6-R Pro Mount", MountType.GEM_HEAVY, 0xE9EAEC, 0xC3C7CC, true, 20.0f, 1.4375f, "white",
            new String[]{"equatorial_mount", "goto_controller", "iron_block"}),
    MOUNT_EQ8R_PRO("mount_eq8r_pro", "Sky-Watcher EQ8-R Pro Mount (pier tripod)", MountType.GEM_XHEAVY, 0xE9EAEC, 0xC3C7CC, true, 50.0f, 1.6875f, "white",
            new String[]{"equatorial_mount", "goto_controller", "iron_block", "iron_block"}),
    MOUNT_AVX("mount_avx", "Celestron Advanced VX Mount", MountType.GEM_MEDIUM, 0x1E1F22, 0xB0B4BA, true, 14.0f, 1.3875f, "black",
            new String[]{"equatorial_mount", "goto_controller", "dye_black", "iron_ingot"}),
    MOUNT_CGX("mount_cgx", "Celestron CGX Mount", MountType.GEM_HEAVY, 0x2D2F33, 0x5A5E64, true, 25.0f, 1.4792f, "gray",
            new String[]{"equatorial_mount", "goto_controller", "dye_gray", "iron_block"}),
    MOUNT_CGX_L("mount_cgx_l", "Celestron CGX-L Mount", MountType.GEM_HEAVY, 0x2D2F33, 0x5A5E64, true, 34.0f, 1.5542f, "gray",
            new String[]{"equatorial_mount", "goto_controller", "dye_gray", "iron_block", "iron_block"}),
    MOUNT_PLANEWAVE_L350("mount_planewave_l350", "PlaneWave L-350 Mount (pier)", MountType.FORK_SINGLE_HEAVY, 0x1E1F23, 0x34373D, true, 45.0f, 1.6458f, "black",
            new String[]{"fork_mount", "goto_controller", "iron_block"}),
    MOUNT_PLANEWAVE_L600("mount_planewave_l600", "PlaneWave L-600 Mount (pier)", MountType.FORK_SINGLE_HEAVY, 0x1E1F23, 0x34373D, true, 136.0f, 1.8069f, "black",
            new String[]{"fork_mount", "goto_controller", "iron_block", "diamond"});

    public final String id;
    public final String displayName;
    public final MountType preset;
    public final int mountColor, legColor;
    public final boolean goTo;
    /** Carga máxima em kg. */
    public final float capacityKg;
    /** Multiplicador de tamanho do modelo. */
    public final float sizeScale;
    public final String colorName;
    public final String[] recipe;

    MountBlockType(String id, String displayName, MountType preset, int mountColor, int legColor, boolean goTo,
                   float capacityKg, float sizeScale, String colorName, String[] recipe) {
        this.id = id;
        this.displayName = displayName;
        this.preset = preset;
        this.mountColor = mountColor;
        this.legColor = legColor;
        this.goTo = goTo;
        this.capacityKg = capacityKg;
        this.sizeScale = sizeScale;
        this.colorName = colorName;
        this.recipe = recipe;
    }

    public boolean canCarry(TelescopeType tube) {
        return tube.hasOta && tube.weightKg <= capacityKg;
    }

    public static MountBlockType byId(String id) {
        for (MountBlockType m : values()) {
            if (m.id.equals(id)) {
                return m;
            }
        }
        return null;
    }
}
