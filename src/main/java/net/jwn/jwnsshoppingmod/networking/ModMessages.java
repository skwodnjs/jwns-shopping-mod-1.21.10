package net.jwn.jwnsshoppingmod.networking;


import net.jwn.jwnsshoppingmod.JWNsMod;
import net.jwn.jwnsshoppingmod.networking.client.OpenProfileScreenS2CPacketHandler;
import net.jwn.jwnsshoppingmod.networking.client.OpenShopScreensS2CPacketHandler;
import net.jwn.jwnsshoppingmod.networking.packet.BuyItemC2SPacket;
import net.jwn.jwnsshoppingmod.networking.packet.EditCommentC2SPacket;
import net.jwn.jwnsshoppingmod.networking.packet.OpenProfileScreenS2CPacket;
import net.jwn.jwnsshoppingmod.networking.packet.OpenShopScreenS2CPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = JWNsMod.MOD_ID)
public class ModMessages {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playBidirectional(
                OpenProfileScreenS2CPacket.TYPE,
                OpenProfileScreenS2CPacket.STREAM_CODEC,
                OpenProfileScreenS2CPacket::handle
        );
        registrar.playBidirectional(
                OpenShopScreenS2CPacket.TYPE,
                OpenShopScreenS2CPacket.STREAM_CODEC,
                OpenShopScreenS2CPacket::handle
        );
        registrar.playToServer(
                EditCommentC2SPacket.TYPE,
                EditCommentC2SPacket.STREAM_CODEC,
                EditCommentC2SPacket::handle
        );
        registrar.playToServer(
                BuyItemC2SPacket.TYPE,
                BuyItemC2SPacket.STREAM_CODEC,
                BuyItemC2SPacket::handle
        );
    }

    @SubscribeEvent
    public static void register(RegisterClientPayloadHandlersEvent event) {
        event.register(
                OpenProfileScreenS2CPacket.TYPE,
                OpenProfileScreenS2CPacketHandler::handle
        );
        event.register(
                OpenShopScreenS2CPacket.TYPE,
                OpenShopScreensS2CPacketHandler::handle
        );
    }
}
