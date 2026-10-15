package com.modtelescopio.client;

import com.modtelescopio.block.BlockTelescope;
import com.modtelescopio.block.TileTelescope;
import com.modtelescopio.common.Optics;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

/** Desenha o telescópio colocado no mundo, com o tubo apontando para onde foi mirado. */
public class RenderTelescope extends TileEntitySpecialRenderer {
    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TileTelescope)) {
            return;
        }
        Block block = te.getBlockType();
        if (!(block instanceof BlockTelescope)) {
            return;
        }
        TileTelescope tile = (TileTelescope) te;
        float facing = BlockTelescope.facingYaw(te.getBlockMetadata());
        updateRenderAim(tile, partialTicks);

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y, z + 0.5);
        TessellatorSink.INSTANCE.render(((BlockTelescope) block).type, facing, tile.renderYaw, tile.renderAlt);
        GL11.glPopMatrix();
    }

    private static void updateRenderAim(TileTelescope tile, float partialTicks) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        float yaw, alt;
        if (tile.liveAim && player != null) {
            // o jogador local está olhando por este telescópio: o tubo segue a câmera sem atraso
            yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;
            alt = -(player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks);
            tile.renderYaw = yaw;
            tile.renderAlt = Optics.clampAltitude(alt);
            tile.renderInit = true;
            return;
        }
        yaw = tile.getAimYaw();
        alt = tile.getAimAlt();
        if (!tile.renderInit) {
            tile.renderYaw = yaw;
            tile.renderAlt = alt;
            tile.renderInit = true;
            return;
        }
        // outros jogadores mexendo: movimento suave até a nova posição
        tile.renderYaw += Optics.wrapDegrees(yaw - tile.renderYaw) * 0.2F;
        tile.renderAlt += (alt - tile.renderAlt) * 0.2F;
    }
}
