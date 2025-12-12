package net.jwn.jwnsshoppingmod.shop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ShopItem {

    private final Item item;
    private final int bundleSize;
    private final int price;
    private final int maxPurchase;
    private int remaining;

    /* =========================
       Constructor
       ========================= */

    public ShopItem(Item item, int bundleSize, int price, int maxPurchase, int remaining) {
        this.item = item;
        this.bundleSize = bundleSize;
        this.price = price;
        this.maxPurchase = maxPurchase;
        this.remaining = remaining;
    }

    /* =========================
       Getters
       ========================= */

    public Item item() {
        return item;
    }

    public int bundleSize() {
        return bundleSize;
    }

    public int price() {
        return price;
    }

    public int maxPurchase() {
        return maxPurchase;
    }

    public int remaining() {
        return remaining;
    }

    /* =========================
       Mutators
       ========================= */

    public boolean consume(int count) {
        if (this.remaining < count) return false;
        this.remaining -= count;
        return true;
    }

    /* =========================
       Empty
       ========================= */

    public static ShopItem empty() {
        return new ShopItem(Items.AIR, 0, 0, 0, 0);
    }

    /* =========================
       StreamCodec (network)
       ========================= */

    private static final StreamCodec<ByteBuf, Item> ITEM_CODEC = new StreamCodec<>() {
        @Override
        public Item decode(ByteBuf buf) {
            ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buf);
            return BuiltInRegistries.ITEM.get(id)
                    .map(Holder.Reference::value)
                    .orElse(Items.AIR);
        }

        @Override
        public void encode(ByteBuf buf, Item value) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(value);
            ResourceLocation.STREAM_CODEC.encode(buf, id);
        }
    };

    public static final StreamCodec<ByteBuf, ShopItem> STREAM_CODEC =
            StreamCodec.composite(
                    ITEM_CODEC, ShopItem::item,
                    ByteBufCodecs.VAR_INT, ShopItem::bundleSize,
                    ByteBufCodecs.VAR_INT, ShopItem::price,
                    ByteBufCodecs.VAR_INT, ShopItem::maxPurchase,
                    ByteBufCodecs.VAR_INT, ShopItem::remaining,
                    ShopItem::new
            );

    /* =========================
       Codec (SavedData / JSON)
       ========================= */

    public static final Codec<ShopItem> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("item")
                            .forGetter(s -> BuiltInRegistries.ITEM.getKey(s.item)),
                    Codec.INT.fieldOf("bundleSize").forGetter(ShopItem::bundleSize),
                    Codec.INT.fieldOf("price").forGetter(ShopItem::price),
                    Codec.INT.fieldOf("maxPurchase").forGetter(ShopItem::maxPurchase),
                    Codec.INT.fieldOf("remaining").forGetter(ShopItem::remaining)
            ).apply(instance, (id, bundleSize, price, maxPurchase, remaining) ->
                    new ShopItem(
                            BuiltInRegistries.ITEM.get(id)
                                    .map(Holder.Reference::value)
                                    .orElse(Items.AIR),
                            bundleSize,
                            price,
                            maxPurchase,
                            remaining
                    )
            ));
}
