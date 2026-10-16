package com.modtelescopio.item;

import com.modtelescopio.common.TelescopeType;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.List;
import java.util.Locale;

/** Linhas de tooltip comuns aos itens de telescópio e de tubo óptico. */
final class TooltipHelper {
    private TooltipHelper() {
    }

    static void optics(List<String> lines, TelescopeType t) {
        String design = StatCollector.translateToLocal(t.design.langKey());
        lines.add(EnumChatFormatting.AQUA + StatCollector.translateToLocalFormatted("telescopio.tooltip.optics",
                design, t.aperture, fmt1(t.focalRatio())));
        lines.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted("telescopio.tooltip.focal", t.focal));
    }

    static void extras(List<String> lines, TelescopeType t) {
        if (t.design.usesCamera()) {
            lines.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("telescopio.tooltip.camera"));
        }
        if (t.has(TelescopeType.FLAG_CONCEPT)) {
            lines.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("telescopio.tooltip.concept"));
        }
    }

    static void hint(List<String> lines, String key) {
        lines.add(EnumChatFormatting.DARK_GRAY + "" + EnumChatFormatting.ITALIC + StatCollector.translateToLocal(key));
    }

    static String fmt1(float v) {
        return String.format(Locale.ROOT, "%.1f", v);
    }
}
