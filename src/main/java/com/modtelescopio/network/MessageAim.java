package com.modtelescopio.network;

import com.modtelescopio.block.TileTelescope;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.concurrent.ConcurrentLinkedQueue;

/** Cliente -> servidor: "estou observando por este telescópio, apontando para cá". */
public class MessageAim implements IMessage {
    public int x, y, z;
    public float yaw, alt;

    public MessageAim() {
    }

    public MessageAim(int x, int y, int z, float yaw, float alt) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.alt = alt;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        yaw = buf.readFloat();
        alt = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeFloat(yaw);
        buf.writeFloat(alt);
    }

    private static final class Pending {
        final EntityPlayerMP player;
        final MessageAim msg;

        Pending(EntityPlayerMP player, MessageAim msg) {
            this.player = player;
            this.msg = msg;
        }
    }

    private static final ConcurrentLinkedQueue<Pending> QUEUE = new ConcurrentLinkedQueue<Pending>();

    /** No 1.7.10 os pacotes chegam na thread da rede: guardamos e aplicamos no tick do servidor. */
    public static class Handler implements IMessageHandler<MessageAim, IMessage> {
        @Override
        public IMessage onMessage(MessageAim message, MessageContext ctx) {
            QUEUE.add(new Pending(ctx.getServerHandler().playerEntity, message));
            return null;
        }
    }

    public static class ServerQueue {
        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            Pending p;
            while ((p = QUEUE.poll()) != null) {
                apply(p.player, p.msg);
            }
        }
    }

    private static void apply(EntityPlayerMP player, MessageAim m) {
        World world = player.worldObj;
        if (!world.blockExists(m.x, m.y, m.z)) {
            return;
        }
        // os telescópios gigantes (até 10 blocos) podem ser observados de mais longe
        if (player.getDistanceSq(m.x + 0.5, m.y + 0.5, m.z + 0.5) > 32.0 * 32.0) {
            return;
        }
        if (Float.isNaN(m.yaw) || Float.isNaN(m.alt) || Float.isInfinite(m.yaw) || Float.isInfinite(m.alt)) {
            return;
        }
        TileEntity te = world.getTileEntity(m.x, m.y, m.z);
        if (te instanceof TileTelescope) {
            ((TileTelescope) te).setAim(m.yaw, m.alt);
            world.markBlockForUpdate(m.x, m.y, m.z);
        }
    }
}
