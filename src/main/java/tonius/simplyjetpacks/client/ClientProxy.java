package tonius.simplyjetpacks.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import tonius.simplyjetpacks.CommonProxy;
import tonius.simplyjetpacks.ConfigReader;
import tonius.simplyjetpacks.util.Vector3;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {

    private static Minecraft mc = Minecraft.getMinecraft();
    private static Random rand = new Random();

    @Override
    public void registerHandlers() {
        super.registerHandlers();
        TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
        KeyBindingRegistry.registerKeyBinding(new SJKeyHandler());
    }

    @Override
    public void sendPacketToServer(int packetType, int int1) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(8);
        DataOutputStream data = new DataOutputStream(bytes);
        try {
            data.writeInt(packetType);
            data.writeInt(int1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "SmpJet";
        packet.data = bytes.toByteArray();
        packet.length = bytes.size();
        PacketDispatcher.sendPacketToServer(packet);
    }

    @Override
    public void sendPacketToServer(int packetType, boolean key1, boolean key2) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream(8);
        DataOutputStream data = new DataOutputStream(bytes);
        try {
            data.writeInt(packetType);
            data.writeBoolean(key1);
            data.writeBoolean(key2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "SmpJet";
        packet.data = bytes.toByteArray();
        packet.length = bytes.size();
        PacketDispatcher.sendPacketToServer(packet);
    }

    @Override
    public void showJetpackParticles(World world, String username, boolean hoverMode) {
        if (ConfigReader.enableJetpackParticles) {
            EntityPlayer player = world.getPlayerEntityByName(username);
            if (player != null) {
                Vector3 playerPos = new Vector3(player);

                if (!(player.equals(mc.thePlayer))) {
                    playerPos.translate(new Vector3(0, 1.90, 0));
                }

                Vector3 vLeft = new Vector3();
                vLeft.z -= 0.55;
                vLeft.x -= 0.35;
                vLeft.rotate(player.renderYawOffset);
                vLeft.y -= 0.80;

                Vector3 vRight = new Vector3();
                vRight.z -= 0.55;
                vRight.x += 0.35;
                vRight.rotate(player.renderYawOffset);
                vRight.y -= 0.80;

                Vector3 vCenter = new Vector3();
                vCenter.z -= 0.50;
                vCenter.x = (rand.nextFloat() - 0.5F) * 0.25F;
                vCenter.rotate(player.renderYawOffset);
                vCenter.y -= 0.85;

                vLeft = Vector3.translate(vLeft.clone(), new Vector3(-player.motionX, -player.motionY, -player.motionZ));
                vRight = Vector3.translate(vRight.clone(), new Vector3(-player.motionX, -player.motionY, -player.motionZ));
                vCenter = Vector3.translate(vCenter.clone(), new Vector3(-player.motionX, -player.motionY, -player.motionZ));

                Vector3 v = new Vector3(playerPos).translate(vLeft);
                if (!(hoverMode)) {
                    world.spawnParticle("flame", v.x, v.y, v.z, 0, -0.2, 0);
                }
                world.spawnParticle("smoke", v.x, v.y, v.z, 0, -0.3, 0);

                v = new Vector3(playerPos).translate(vRight);
                if (!(hoverMode)) {
                    world.spawnParticle("flame", v.x, v.y, v.z, 0, -0.2, 0);
                }
                world.spawnParticle("smoke", v.x, v.y, v.z, 0, -0.3, 0);

                v = new Vector3(playerPos).translate(vCenter);
                if (!(hoverMode)) {
                    world.spawnParticle("flame", v.x, v.y, v.z, 0, -0.2, 0);
                }
                world.spawnParticle("smoke", v.x, v.y, v.z, 0, -0.3, 0);
            }
        }
    }

}
