package net.jwn.jwnsshoppingmod.networking.packet;


import io.netty.buffer.ByteBuf;
import net.jwn.jwnsshoppingmod.JWNsMod;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenProfileScreenS2CPacket(String name, int level, String alias, int coins, int time, Boolean isMinute, String comment) implements CustomPacketPayload {
    public static final Type<OpenProfileScreenS2CPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "open_profile_packet"));

    public static final StreamCodec<ByteBuf, OpenProfileScreenS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, OpenProfileScreenS2CPacket::name,
            ByteBufCodecs.VAR_INT, OpenProfileScreenS2CPacket::level,
            ByteBufCodecs.STRING_UTF8, OpenProfileScreenS2CPacket::alias,
            ByteBufCodecs.VAR_INT, OpenProfileScreenS2CPacket::coins,
            ByteBufCodecs.VAR_INT, OpenProfileScreenS2CPacket::time,
            ByteBufCodecs.BOOL, OpenProfileScreenS2CPacket::isMinute,
            ByteBufCodecs.STRING_UTF8, OpenProfileScreenS2CPacket::comment,
            OpenProfileScreenS2CPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final OpenProfileScreenS2CPacket data, final IPayloadContext context) {

    }
}
