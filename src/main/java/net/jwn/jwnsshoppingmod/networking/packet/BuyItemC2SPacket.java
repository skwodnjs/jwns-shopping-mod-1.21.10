package net.jwn.jwnsshoppingmod.networking.packet;

import io.netty.buffer.ByteBuf;
import net.jwn.jwnsinvitationmod.JWNsMod;
import net.jwn.jwnsshoppingmod.shop.PlayerBlockTimerData;
import net.jwn.jwnsshoppingmod.shop.PlayerCoinData;
import net.jwn.jwnsshoppingmod.shop.ShopItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record BuyItemC2SPacket(ShopItem item, int count) implements CustomPacketPayload{
    public static final CustomPacketPayload.Type<BuyItemC2SPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(JWNsMod.MOD_ID, "buy_item_packet"));

    public static final StreamCodec<ByteBuf, BuyItemC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ShopItem.STREAM_CODEC, BuyItemC2SPacket::item,
            ByteBufCodecs.VAR_INT, BuyItemC2SPacket::count,
            BuyItemC2SPacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final BuyItemC2SPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) return;
            Inventory inv = serverPlayer.getInventory();
            int emptySlot = 0;

            for (int i = 0; i < 36; i++) if (inv.getItem(i).isEmpty()) emptySlot++;

            if (data.count > emptySlot) {
                serverPlayer.sendSystemMessage(Component.translatable("jwnsshoppingmod.networking.not_enough_slot"));
            } else {
                PlayerCoinData coinData = PlayerCoinData.get(serverPlayer.level());
                int cost = data.item().price() * data.count();
                coinData.addCoins(serverPlayer, -cost);
                PlayerBlockTimerData blockTimerData = PlayerBlockTimerData.get(serverPlayer.level());
                blockTimerData.consumeShopItem(serverPlayer, data.item().item(), data.count());
                serverPlayer.getInventory().add(new ItemStack(data.item().item(), data.item().bundleSize() * data.count()));
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("jwnsshoppingmod.networking.edit_profile_failed", e.getMessage()));
            return null;
        });
    }
}
