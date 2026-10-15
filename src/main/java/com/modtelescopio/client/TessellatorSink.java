package com.modtelescopio.client;

import com.modtelescopio.client.model.ModelBuilder;
import com.modtelescopio.client.model.QuadSink;
import com.modtelescopio.client.model.TelescopeModel;
import com.modtelescopio.common.TelescopeType;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/** Envia os quadriláteros do modelo procedural para o Tessellator do 1.7.10. */
public final class TessellatorSink implements QuadSink {
    public static final TessellatorSink INSTANCE = new TessellatorSink();

    private final ModelBuilder builder = new ModelBuilder();

    private TessellatorSink() {
    }

    /** Desenha o telescópio na matriz GL atual (origem = centro da base do bloco). */
    public void render(TelescopeType type, float facingYaw, float aimYaw, float aimAlt) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        TelescopeModel.build(builder.reset(this), type, facingYaw, aimYaw, aimAlt);
        tess.draw();
        GL11.glPopAttrib();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void quad(float[] x, float[] y, float[] z, float[] nx, float[] ny, float[] nz, int rgb) {
        Tessellator tess = Tessellator.instance;
        tess.setColorOpaque_I(rgb);
        for (int i = 0; i < 4; i++) {
            tess.setNormal(nx[i], ny[i], nz[i]);
            tess.addVertex(x[i], y[i], z[i]);
        }
    }
}
