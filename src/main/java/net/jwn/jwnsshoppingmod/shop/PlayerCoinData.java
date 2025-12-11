package net.jwn.jwnsshoppingmod.shop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerCoinData extends SavedData {
    public static final SavedDataType<PlayerCoinData> TYPE =
            new SavedDataType<>(
                    "playercoins",
                    PlayerCoinData::new,
                    RecordCodecBuilder.create(instance -> instance.group(
                            Codec.unboundedMap(
                                    UUIDUtil.STRING_CODEC,
                                    Codec.INT
                            ).fieldOf("coins").forGetter(sd -> sd.coins)
                    ).apply(instance, PlayerCoinData::new))
            );

    private final Map<UUID, Integer> coins;

    public PlayerCoinData() {
        this.coins = new HashMap<>();
    }

    public PlayerCoinData(Map<UUID, Integer> coins) {
        this.coins = new HashMap<>(coins);
    }

    private int getOrCreateEntry(ServerPlayer player) {
        return coins.computeIfAbsent(player.getUUID(), k -> 0);
    }

    public static PlayerCoinData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public int getCoins(ServerPlayer player) {
        return coins.getOrDefault(player.getUUID(), 0);
    }

    public void setCoins(ServerPlayer player, int amount) {
        coins.put(player.getUUID(), amount);
        setDirty();
    }

    public void addCoins(ServerPlayer player, int delta) {
        int current = getOrCreateEntry(player);
        coins.put(player.getUUID(), current + delta);
        setDirty();
    }
}