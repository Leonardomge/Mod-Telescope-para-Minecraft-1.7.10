package com.modtelescopio.block;

import com.modtelescopio.client.model.Assembly;
import com.modtelescopio.client.model.ModelBounds;
import com.modtelescopio.common.Optics;
import com.modtelescopio.common.TelescopeType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

/**
 * Guarda para onde o tubo está apontando (sincronizado com todos os jogadores) e, nas
 * montagens-bloco, qual tubo óptico está instalado.
 */
public class TileTelescope extends TileEntity {
    private float aimYaw;
    private float aimAlt = Optics.DEFAULT_ALTITUDE;
    private boolean hasAim;
    private TelescopeType tube;

    // estado só do cliente: ângulo desenhado, suavizado
    public float renderYaw, renderAlt;
    public boolean renderInit;
    /** true enquanto o jogador local está observando por este telescópio. */
    public boolean liveAim;

    public void setAim(float yaw, float alt) {
        aimYaw = Optics.wrapDegrees(yaw);
        aimAlt = Optics.clampAltitude(alt);
        hasAim = true;
        markDirty();
    }

    public float getAimYaw() {
        return hasAim ? aimYaw : BlockTelescopeBase.facingYaw(getBlockMetadata());
    }

    public float getAimAlt() {
        return aimAlt;
    }

    /** Tubo instalado (só nas montagens-bloco). */
    public TelescopeType getTube() {
        return tube;
    }

    public void setTube(TelescopeType tube) {
        this.tube = tube;
        markDirty();
    }

    /** O que este bloco desenha, ou null se o bloco não for de telescópio. */
    public Assembly assembly() {
        Block b = getBlockType();
        if (b instanceof BlockTelescope) {
            return Assembly.of(((BlockTelescope) b).type);
        }
        if (b instanceof BlockMount) {
            return Assembly.of(((BlockMount) b).mount, tube);
        }
        return null;
    }

    /** Tubo usado na observação (o do telescópio completo ou o instalado na montagem). */
    public TelescopeType opticalTube() {
        Block b = getBlockType();
        if (b instanceof BlockTelescope) {
            return ((BlockTelescope) b).type;
        }
        return b instanceof BlockMount ? tube : null;
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        writeSync(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        readSync(tag);
    }

    private void writeSync(NBTTagCompound tag) {
        tag.setBoolean("HasAim", hasAim);
        tag.setFloat("AimYaw", aimYaw);
        tag.setFloat("AimAlt", aimAlt);
        if (tube != null) {
            tag.setString("Tube", tube.id);
        }
    }

    private void readSync(NBTTagCompound tag) {
        hasAim = tag.getBoolean("HasAim");
        aimYaw = tag.getFloat("AimYaw");
        aimAlt = tag.hasKey("AimAlt") ? tag.getFloat("AimAlt") : Optics.DEFAULT_ALTITUDE;
        tube = tag.hasKey("Tube") ? TelescopeType.byId(tag.getString("Tube")) : null;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeSync(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readSync(pkt.func_148857_g());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        Assembly a = worldObj != null ? assembly() : null;
        if (a == null) {
            return AxisAlignedBB.getBoundingBox(xCoord - 1, yCoord, zCoord - 1, xCoord + 2, yCoord + 2.5, zCoord + 2);
        }
        ModelBounds b = ModelBounds.of(a);
        double r = b.horizontalReach() + 0.5;
        return AxisAlignedBB.getBoundingBox(xCoord + 0.5 - r, yCoord + Math.min(0.0, b.minY), zCoord + 0.5 - r,
                xCoord + 0.5 + r, yCoord + b.maxY + b.height() * 0.3 + 0.5, zCoord + 0.5 + r);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        Assembly a = worldObj != null ? assembly() : null;
        double d = a == null ? 96.0 : Math.min(256.0, 80.0 + ModelBounds.of(a).height() * 12.0);
        return d * d;
    }
}
