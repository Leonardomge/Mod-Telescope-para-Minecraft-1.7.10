package com.modtelescopio.client;

import com.modtelescopio.client.model.Assembly;
import com.modtelescopio.client.model.ModelBounds;
import com.modtelescopio.common.Optics;
import com.modtelescopio.item.ItemMountBlock;
import com.modtelescopio.item.ItemOta;
import com.modtelescopio.item.ItemTelescope;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/** Telescópios, montagens e tubos ópticos em 3D no inventário, na mão e caídos no chão. */
public class ItemRendererTelescope implements IItemRenderer {
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        switch (helper) {
            case ENTITY_ROTATION:
            case ENTITY_BOBBING:
            case EQUIPPED_BLOCK:
            case BLOCK_3D:
                return true;
            default:
                // no inventário montamos a projeção nós mesmos, para mostrar o telescópio de lado
                return false;
        }
    }

    static Assembly assemblyOf(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ItemTelescope) {
            return Assembly.of(((ItemTelescope) item).getType());
        }
        if (item instanceof ItemMountBlock) {
            return Assembly.of(((ItemMountBlock) item).getMount(), null);
        }
        if (item instanceof ItemOta) {
            return Assembly.tubeOnly(((ItemOta) item).type);
        }
        return null;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        Assembly a = assemblyOf(item);
        if (a == null) {
            return;
        }
        ModelBounds b = ModelBounds.of(a);
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glPushMatrix();
        switch (type) {
            case INVENTORY: {
                RenderHelper.enableGUIStandardItemLighting();
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glTranslatef(8.0F, 8.0F, 10.0F);
                GL11.glScalef(15.0F, -15.0F, 15.0F);
                GL11.glRotatef(25.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(60.0F, 0.0F, 1.0F, 0.0F);
                fit(b, 1.0F, true);
                break;
            }
            case ENTITY:
                fit(b, 2.0F, true);
                break;
            default:
                // EQUIPPED / EQUIPPED_FIRST_PERSON: espaço de bloco 0..1
                GL11.glTranslatef(0.5F, 0.0F, 0.5F);
                fit(b, 1.1F, false);
                break;
        }
        TessellatorSink.INSTANCE.render(a, 0.0F, 0.0F, Optics.DEFAULT_ALTITUDE);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    /** Escala o modelo para caber em {@code size} e centraliza (verticalmente se pedido). */
    private static void fit(ModelBounds b, float size, boolean centerVertically) {
        float s = b.fitScale(size);
        if (centerVertically) {
            GL11.glTranslatef(0.0F, -b.height() * s * 0.5F, 0.0F);
        }
        GL11.glScalef(s, s, s);
        GL11.glTranslatef(-b.centerX(), -b.minY, -b.centerZ());
    }
}
