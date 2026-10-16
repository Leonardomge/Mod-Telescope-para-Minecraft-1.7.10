package com.modtelescopio.client.model;

import com.modtelescopio.common.MountBlockType;
import com.modtelescopio.common.MountType;
import com.modtelescopio.common.TelescopeType;

/**
 * O que vai ser desenhado: uma montagem (com suas cores e tamanho) e, opcionalmente, um tubo
 * óptico. Serve para os telescópios completos, para as montagens-bloco (vazias ou com tubo)
 * e para o item "tubo óptico" sozinho.
 */
public final class Assembly {
    /** Montagem; null = só o tubo (ícone do item "tubo óptico"). */
    public final MountType preset;
    public final int mountColor, legColor;
    public final boolean goTo;
    /** Tubo óptico; null = montagem vazia. */
    public final TelescopeType tube;
    /** Multiplicador de tamanho. */
    public final float scale;
    /** Chave para cache (caixa envolvente etc.). */
    public final String key;

    private Assembly(MountType preset, int mountColor, int legColor, boolean goTo, TelescopeType tube, float scale,
                     String key) {
        this.preset = preset;
        this.mountColor = mountColor;
        this.legColor = legColor;
        this.goTo = goTo;
        this.tube = tube;
        this.scale = scale;
        this.key = key;
    }

    /** Telescópio completo, como vem de fábrica. */
    public static Assembly of(TelescopeType t) {
        return new Assembly(t.mount, t.mountColor, t.legColor, t.goTo, t, t.sizeScale, "T:" + t.id);
    }

    /** Montagem-bloco com (ou sem) um tubo instalado. */
    public static Assembly of(MountBlockType m, TelescopeType tubeOrNull) {
        return new Assembly(m.preset, m.mountColor, m.legColor, m.goTo, tubeOrNull, m.sizeScale,
                "M:" + m.id + (tubeOrNull == null ? "" : "+" + tubeOrNull.id));
    }

    /** Só o tubo óptico (item). */
    public static Assembly tubeOnly(TelescopeType t) {
        return new Assembly(null, 0, 0, false, t, t.sizeScale, "O:" + t.id);
    }
}
