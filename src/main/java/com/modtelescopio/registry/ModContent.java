package com.modtelescopio.registry;

import com.modtelescopio.TelescopioMod;
import com.modtelescopio.block.BlockMount;
import com.modtelescopio.block.BlockTelescope;
import com.modtelescopio.block.TileTelescope;
import com.modtelescopio.common.ComponentType;
import com.modtelescopio.common.MountBlockType;
import com.modtelescopio.common.TelescopeType;
import com.modtelescopio.item.ItemComponent;
import com.modtelescopio.item.ItemMountBlock;
import com.modtelescopio.item.ItemOta;
import com.modtelescopio.item.ItemTelescope;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Registro de blocos, itens, tile entity e receitas. */
public final class ModContent {
    public static final Map<TelescopeType, BlockTelescope> TELESCOPES =
            new EnumMap<TelescopeType, BlockTelescope>(TelescopeType.class);
    public static final Map<MountBlockType, BlockMount> MOUNTS =
            new EnumMap<MountBlockType, BlockMount>(MountBlockType.class);
    public static final Map<TelescopeType, ItemOta> OTAS = new EnumMap<TelescopeType, ItemOta>(TelescopeType.class);
    public static final Map<ComponentType, ItemComponent> COMPONENTS =
            new EnumMap<ComponentType, ItemComponent>(ComponentType.class);

    public static final CreativeTabs TAB = new CreativeTabs(TelescopioMod.MODID) {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return Item.getItemFromBlock(TELESCOPES.get(TelescopeType.SKYWATCHER_250PDS_EQ6R));
        }
    };

    public static final CreativeTabs TAB_PARTS = new CreativeTabs(TelescopioMod.MODID + "_pecas") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return Item.getItemFromBlock(MOUNTS.get(MountBlockType.MOUNT_EQ6R_PRO));
        }
    };

    private ModContent() {
    }

    public static void registerAll() {
        for (TelescopeType t : TelescopeType.values()) {
            BlockTelescope block = new BlockTelescope(t);
            GameRegistry.registerBlock(block, ItemTelescope.class, t.id);
            TELESCOPES.put(t, block);
        }
        for (MountBlockType m : MountBlockType.values()) {
            BlockMount block = new BlockMount(m);
            GameRegistry.registerBlock(block, ItemMountBlock.class, m.id);
            MOUNTS.put(m, block);
        }
        for (TelescopeType t : TelescopeType.values()) {
            if (t.hasOta) {
                ItemOta item = new ItemOta(t);
                GameRegistry.registerItem(item, t.otaId());
                OTAS.put(t, item);
            }
        }
        for (ComponentType c : ComponentType.values()) {
            ItemComponent item = new ItemComponent(c);
            GameRegistry.registerItem(item, c.id);
            COMPONENTS.put(c, item);
        }
        GameRegistry.registerTileEntity(TileTelescope.class, TelescopioMod.MODID + ":telescope");
    }

    public static void registerRecipes() {
        for (ComponentType c : ComponentType.values()) {
            List<Object> args = new ArrayList<Object>();
            for (String row : c.pattern) {
                args.add(row);
            }
            for (int i = 0; i < c.keys.length; i += 2) {
                args.add(c.keys[i].charAt(0));
                args.add(ingredient(c.keys[i + 1]));
            }
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(COMPONENTS.get(c), c.count), args.toArray()));
        }
        for (TelescopeType t : TelescopeType.values()) {
            ItemStack telescope = new ItemStack(TELESCOPES.get(t));
            GameRegistry.addRecipe(new ShapelessOreRecipe(telescope, ingredients(t.fullRecipe())));
            if (t.hasOta) {
                GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(OTAS.get(t)), ingredients(t.opticsRecipe)));
                String[] fromTube = new String[t.mountRecipe.length + 1];
                fromTube[0] = t.otaId();
                System.arraycopy(t.mountRecipe, 0, fromTube, 1, t.mountRecipe.length);
                GameRegistry.addRecipe(new ShapelessOreRecipe(telescope.copy(), ingredients(fromTube)));
            }
        }
        for (MountBlockType m : MountBlockType.values()) {
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(MOUNTS.get(m)), ingredients(m.recipe)));
        }
    }

    private static Object[] ingredients(String[] tokens) {
        Object[] out = new Object[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            out[i] = ingredient(tokens[i]);
        }
        return out;
    }

    /** Traduz os nomes de ingrediente do catálogo para itens/ore dictionary do 1.7.10. */
    private static Object ingredient(String token) {
        for (ComponentType c : ComponentType.values()) {
            if (c.id.equals(token)) {
                return COMPONENTS.get(c);
            }
        }
        for (TelescopeType t : TelescopeType.values()) {
            if (t.hasOta && t.otaId().equals(token)) {
                return OTAS.get(t);
            }
        }
        if (token.startsWith("dye_")) {
            String color = token.substring(4);
            return "dye" + Character.toUpperCase(color.charAt(0)) + color.substring(1);
        }
        if ("glass".equals(token)) {
            return new ItemStack(Blocks.glass);
        }
        if ("glass_pane".equals(token)) {
            return new ItemStack(Blocks.glass_pane);
        }
        if ("gold_nugget".equals(token)) {
            return "nuggetGold";
        }
        if ("quartz".equals(token)) {
            return "gemQuartz";
        }
        if ("iron_ingot".equals(token)) {
            return "ingotIron";
        }
        if ("gold_ingot".equals(token)) {
            return "ingotGold";
        }
        if ("diamond".equals(token)) {
            return "gemDiamond";
        }
        if ("diamond_block".equals(token)) {
            return "blockDiamond";
        }
        if ("iron_block".equals(token)) {
            return "blockIron";
        }
        if ("iron_bars".equals(token)) {
            return new ItemStack(Blocks.iron_bars);
        }
        if ("stick".equals(token)) {
            return "stickWood";
        }
        if ("planks".equals(token)) {
            return "plankWood";
        }
        if ("redstone".equals(token)) {
            return "dustRedstone";
        }
        if ("compass".equals(token)) {
            return new ItemStack(Items.compass);
        }
        if ("clock".equals(token)) {
            return new ItemStack(Items.clock);
        }
        if ("coal".equals(token)) {
            return new ItemStack(Items.coal, 1, 0);
        }
        if ("nether_star".equals(token)) {
            return new ItemStack(Items.nether_star);
        }
        throw new IllegalArgumentException("Ingrediente desconhecido no catálogo: " + token);
    }
}
