package net.jwn.jwnsshoppingmod.networking.client;


import net.jwn.jwnsshoppingmod.JWNsMod;
import net.jwn.jwnsshoppingmod.networking.packet.OpenProfileScreenS2CPacket;
import net.jwn.jwnsshoppingmod.screen.ProfileScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber(modid = JWNsMod.MOD_ID, value = Dist.CLIENT)
public record OpenProfileScreenS2CPacketHandler(String name) {
    public static void handle(final OpenProfileScreenS2CPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft.getInstance().setScreen(new ProfileScreen(Component.literal(data.name())));

        }).exceptionally(e -> {
            context.disconnect(Component.translatable("jwnsshoppingmod.networking.open_profile_failed", e.getMessage()));
            return null;
        });
    }
}
