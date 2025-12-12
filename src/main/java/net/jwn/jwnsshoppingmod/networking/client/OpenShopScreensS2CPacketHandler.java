package net.jwn.jwnsshoppingmod.networking.client;

import net.jwn.jwnsshoppingmod.JWNsMod;
import net.jwn.jwnsshoppingmod.networking.packet.OpenShopScreenS2CPacket;
import net.jwn.jwnsshoppingmod.screen.ShopScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber(modid = JWNsMod.MOD_ID, value = Dist.CLIENT)
public class OpenShopScreensS2CPacketHandler {
    public static void handle(final OpenShopScreenS2CPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft.getInstance().setScreen(new ShopScreen(data.coin(), data.shopItems()));
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("jwnsshoppingmod.networking.open_shop_failed", e.getMessage()));
            return null;
        });
    }
}
