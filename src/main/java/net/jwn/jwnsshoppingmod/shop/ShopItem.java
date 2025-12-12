package net.jwn.jwnsshoppingmod.shop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public record ShopItem(Item item, int bundleSize, int price, int maxPurchase) {

    private static final StreamCodec<ByteBuf, Item> ITEM_CODEC = new StreamCodec<>() {
        @Override
        public Item decode(ByteBuf buf) {
            ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buf);
            return BuiltInRegistries.ITEM.get(id)
                    .map(net.minecraft.core.Holder.Reference::value)
                    .orElse(net.minecraft.world.item.Items.AIR);
        }

        @Override
        public void encode(ByteBuf buf, Item value) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(value);
            ResourceLocation.STREAM_CODEC.encode(buf, id);
        }
    };

    public static final StreamCodec<ByteBuf, ShopItem> STREAM_CODEC = StreamCodec.composite(
            ITEM_CODEC, ShopItem::item,
            ByteBufCodecs.VAR_INT, ShopItem::bundleSize,
            ByteBufCodecs.VAR_INT, ShopItem::price,
            ByteBufCodecs.VAR_INT, ShopItem::maxPurchase,
            ShopItem::new
    );

    public static final Codec<ShopItem> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("item").forGetter(s -> BuiltInRegistries.ITEM.getKey(s.item())),
                    Codec.INT.fieldOf("bundleSize").forGetter(ShopItem::bundleSize),
                    Codec.INT.fieldOf("price").forGetter(ShopItem::price),
                    Codec.INT.fieldOf("maxPurchase").forGetter(ShopItem::maxPurchase)
            ).apply(instance, (id, bundleSize, price, maxPurchase) ->
                    new ShopItem(
                            BuiltInRegistries.ITEM.get(id)
                                    .map(net.minecraft.core.Holder.Reference::value)
                                    .orElse(Items.AIR),
                            bundleSize,
                            price,
                            maxPurchase
                    )
            ));

    public static ShopItem empty() {
        return new ShopItem(Items.AIR, 0, 0, 0);
    }
}
