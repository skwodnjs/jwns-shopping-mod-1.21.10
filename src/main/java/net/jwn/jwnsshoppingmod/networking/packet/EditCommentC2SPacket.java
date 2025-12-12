package net.jwn.jwnsshoppingmod.networking.packet;

import io.netty.buffer.ByteBuf;
import net.jwn.jwnsinvitationmod.JWNsMod;
import net.jwn.jwnsshoppingmod.profile.ProfileData;
import net.jwn.jwnsshoppingmod.profile.ProfileDataStorage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EditCommentC2SPacket(String comment) implements CustomPacketPayload {
    public static final Type<EditCommentC2SPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "edit_comment_packet"));

    public static final StreamCodec<ByteBuf, EditCommentC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, EditCommentC2SPacket::comment,
            EditCommentC2SPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final EditCommentC2SPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) return;
            ProfileData profileData = ProfileDataStorage.loadByPlayerName(serverPlayer.level().getServer(), serverPlayer.getDisplayName().getString());
            assert profileData != null;
            profileData.setComment(data.comment());
            ProfileDataStorage.saveByPlayerName(serverPlayer.level().getServer(), serverPlayer.getDisplayName().getString(), profileData);
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("jwnsshoppingmod.networking.edit_profile_failed", e.getMessage()));
            return null;
        });
    }
}
