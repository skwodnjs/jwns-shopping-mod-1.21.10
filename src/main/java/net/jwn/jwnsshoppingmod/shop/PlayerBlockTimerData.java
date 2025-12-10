package net.jwn.jwnsshoppingmod.shop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.*;

public class PlayerBlockTimerData extends SavedData {
    public static final int RESET_TIMER = 20 * 60 * 20; // 24000

    private static final Map<Block, Integer> SHOP_BLOCKS =
            Map.ofEntries(
                    Map.entry(Blocks.DIAMOND_BLOCK, 64),
                    Map.entry(Blocks.GOLD_BLOCK, 64),
                    Map.entry(Blocks.IRON_BLOCK, 64),
                    Map.entry(Blocks.COAL_BLOCK, 64),
                    Map.entry(Blocks.EMERALD_BLOCK, 64),
                    Map.entry(Blocks.LAPIS_BLOCK, 64),
                    Map.entry(Blocks.REDSTONE_BLOCK, 64),
                    Map.entry(Blocks.COPPER_BLOCK, 64),
                    Map.entry(Blocks.QUARTZ_BLOCK, 64),
                    Map.entry(Blocks.AMETHYST_BLOCK, 64),
                    Map.entry(Blocks.OAK_LOG, 64),
                    Map.entry(Blocks.SPRUCE_LOG, 64),
                    Map.entry(Blocks.BIRCH_LOG, 64),
                    Map.entry(Blocks.JUNGLE_LOG, 64),
                    Map.entry(Blocks.ACACIA_LOG, 64),
                    Map.entry(Blocks.DARK_OAK_LOG, 64),
                    Map.entry(Blocks.MANGROVE_LOG, 64),
                    Map.entry(Blocks.CHERRY_LOG, 64),
                    Map.entry(Blocks.BAMBOO_BLOCK, 64),
                    Map.entry(Blocks.STONE, 64)
            );

    public static final SavedDataType<PlayerBlockTimerData> TYPE =
        new SavedDataType<>(
            "blockandtimer",
            PlayerBlockTimerData::new,
            RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(
                    UUIDUtil.STRING_CODEC,
                    PlayerEntry.CODEC
                ).fieldOf("players").forGetter(sd -> sd.players)
            ).apply(instance, PlayerBlockTimerData::new))
        );

    private final Map<UUID, PlayerEntry> players;

    public PlayerBlockTimerData() {
        this.players = new HashMap<>();
    }

    public PlayerBlockTimerData(Map<UUID, PlayerEntry> players) {
        this.players = new HashMap<>(players);
    }
    public static class PlayerEntry {
        private int timer;
        private Block block1;
        private Block block2;
        private Block block3;
        private Block block4;
        private Block block5;

        public static final Codec<PlayerEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("timer").forGetter(e -> e.timer),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block1").forGetter(e -> e.block1),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block2").forGetter(e -> e.block2),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block3").forGetter(e -> e.block3),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block4").forGetter(e -> e.block4),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block5").forGetter(e -> e.block5)
        ).apply(instance, PlayerEntry::new));

        public PlayerEntry(int timer, Block block1, Block block2, Block block3, Block block4, Block block5) {
            this.timer = timer;
            this.block1 = block1;
            this.block2 = block2;
            this.block3 = block3;
            this.block4 = block4;
            this.block5 = block5;
        }

        public PlayerEntry() {
            this(0, Blocks.AIR, Blocks.AIR, Blocks.AIR, Blocks.AIR, Blocks.AIR);
        }
    }

    private PlayerEntry getOrCreateEntry(ServerPlayer player) {
        UUID key = player.getUUID();
        return players.computeIfAbsent(key, k -> new PlayerEntry());
    }

    public static PlayerBlockTimerData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public void tickPlayer(ServerPlayer player) {
        PlayerEntry entry = getOrCreateEntry(player);
        entry.timer++;

        if (entry.timer >= RESET_TIMER) {
            entry.timer = 0;
            resetBlocklist(player);
        }

        setDirty();
    }

    public int getTimer(ServerPlayer player) {
        return getOrCreateEntry(player).timer;
    }

    public void resetBlocklist(ServerPlayer player) {
        PlayerEntry entry = getOrCreateEntry(player);

        List<Block> pool = new ArrayList<>(SHOP_BLOCKS.keySet());
        RandomSource random = player.getRandom();

        entry.block1 = pool.get(random.nextInt(pool.size()));
        entry.block2 = pool.get(random.nextInt(pool.size()));
        entry.block3 = pool.get(random.nextInt(pool.size()));
        entry.block4 = pool.get(random.nextInt(pool.size()));
        entry.block5 = pool.get(random.nextInt(pool.size()));

        setDirty();
    }

    public List<Block> getBlocks(ServerPlayer player) {
        PlayerEntry e = getOrCreateEntry(player);
        return List.of(e.block1, e.block2, e.block3, e.block4, e.block5);
    }

}
