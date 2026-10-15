package com.modtelescopio.block;

import com.modtelescopio.common.Optics;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

/** Guarda para onde o tubo está apontando (sincronizado com todos os jogadores). */
public class TileTelescope extends TileEntity {
    private float aimYaw;
    private float aimAlt = Optics.DEFAULT_ALTITUDE;
    private boolean hasAim;

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
        return hasAim ? aimYaw : BlockTelescope.facingYaw(getBlockMetadata());
    }

    public float getAimAlt() {
        return aimAlt;
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        writeAim(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        readAim(tag);
    }

    private void writeAim(NBTTagCompound tag) {
        tag.setBoolean("HasAim", hasAim);
        tag.setFloat("AimYaw", aimYaw);
        tag.setFloat("AimAlt", aimAlt);
    }

    private void readAim(NBTTagCompound tag) {
        hasAim = tag.getBoolean("HasAim");
        aimYaw = tag.getFloat("AimYaw");
        aimAlt = tag.hasKey("AimAlt") ? tag.getFloat("AimAlt") : Optics.DEFAULT_ALTITUDE;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeAim(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readAim(pkt.func_148857_g());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord - 1, yCoord, zCoord - 1, xCoord + 2, yCoord + 2.5, zCoord + 2);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 96.0 * 96.0;
    }
}
