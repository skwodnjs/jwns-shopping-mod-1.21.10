package net.jwn.jwnsshoppingmod.networking.packet;

import io.netty.buffer.ByteBuf;
import net.jwn.jwnsshoppingmod.JWNsMod;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenShopScreenS2CPacket(int coin) implements CustomPacketPayload {
    public static final Type<OpenShopScreenS2CPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "open_shop_packet"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, OpenShopScreenS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, OpenShopScreenS2CPacket::coin,
            OpenShopScreenS2CPacket::new
    );

    public static void handle(final OpenShopScreenS2CPacket data, final IPayloadContext context) {

    }
}
