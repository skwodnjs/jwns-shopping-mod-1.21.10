package net.jwn.jwnsshoppingmod.shop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.*;

public class PlayerBlockTimerData extends SavedData {
    public static final int RESET_TIMER = 20 * 60 * 20; // 24000

    private static final List<ShopItem> SHOP_ITEMS = List.of(
            // 고가 / 희귀 자원
            new ShopItem(Items.DIAMOND_BLOCK,   64, 50, 2, 2),
            new ShopItem(Items.EMERALD_BLOCK,   64, 50, 2, 2),
            new ShopItem(Items.AMETHYST_BLOCK,  64, 40, 3, 3),

            // 중상급 광물
            new ShopItem(Items.GOLD_BLOCK,      64, 40, 3, 3),
            new ShopItem(Items.IRON_BLOCK,      64, 30, 5, 5),
            new ShopItem(Items.COPPER_BLOCK,    64, 20, 7, 7),

            // 중급 자원
            new ShopItem(Items.LAPIS_BLOCK,     64, 30, 5, 5),
            new ShopItem(Items.REDSTONE_BLOCK,  64, 20, 7, 7),
            new ShopItem(Items.QUARTZ_BLOCK,    64, 20, 7, 7),

            // 연료 / 기본 자원
            new ShopItem(Items.COAL_BLOCK,      64, 10, 9, 9),
            new ShopItem(Items.STONE,           64, 10, 9, 9),

            // 목재류 (공급 많음)
            new ShopItem(Items.OAK_LOG,          64, 10, 9, 9),
            new ShopItem(Items.SPRUCE_LOG,       64, 10, 9, 9),
            new ShopItem(Items.BIRCH_LOG,        64, 10, 9, 9),
            new ShopItem(Items.JUNGLE_LOG,       64, 10, 9, 9),
            new ShopItem(Items.ACACIA_LOG,       64, 10, 9, 9),
            new ShopItem(Items.DARK_OAK_LOG,     64, 10, 9, 9),
            new ShopItem(Items.MANGROVE_LOG,     64, 20, 7, 7),
            new ShopItem(Items.CHERRY_LOG,       64, 20, 7, 7),

            // 특수 자원
            new ShopItem(Items.BAMBOO_BLOCK,    64, 20, 6, 6)
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
        private ShopItem item1;
        private ShopItem item2;
        private ShopItem item3;
        private ShopItem item4;
        private ShopItem item5;

        public static final Codec<PlayerEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("timer").forGetter(e -> e.timer),
                ShopItem.CODEC.fieldOf("item1").forGetter(e -> e.item1),
                ShopItem.CODEC.fieldOf("item2").forGetter(e -> e.item2),
                ShopItem.CODEC.fieldOf("item3").forGetter(e -> e.item3),
                ShopItem.CODEC.fieldOf("item4").forGetter(e -> e.item4),
                ShopItem.CODEC.fieldOf("item5").forGetter(e -> e.item5)
        ).apply(instance, PlayerEntry::new));

        public PlayerEntry(int timer, ShopItem item1, ShopItem item2, ShopItem item3, ShopItem item4, ShopItem item5) {
            this.timer = timer;
            this.item1 = item1;
            this.item2 = item2;
            this.item3 = item3;
            this.item4 = item4;
            this.item5 = item5;
        }

        public PlayerEntry() {
            this(0, ShopItem.empty(), ShopItem.empty(), ShopItem.empty(), ShopItem.empty(), ShopItem.empty());
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
            resetShopItems(player);
        }

        setDirty();
    }

    public int getTimer(ServerPlayer player) {
        return getOrCreateEntry(player).timer;
    }

    public void resetShopItems(ServerPlayer player) {
        PlayerEntry entry = getOrCreateEntry(player);

        List<ShopItem> pool = new ArrayList<>(SHOP_ITEMS);
        RandomSource random = player.getRandom();

        entry.item1 = pool.get(random.nextInt(pool.size()));
        entry.item2 = pool.get(random.nextInt(pool.size()));
        entry.item3 = pool.get(random.nextInt(pool.size()));
        entry.item4 = pool.get(random.nextInt(pool.size()));
        entry.item5 = pool.get(random.nextInt(pool.size()));

        setDirty();
    }

    public List<ShopItem> getShopItems(ServerPlayer player) {
        PlayerEntry e = getOrCreateEntry(player);
        return List.of(e.item1, e.item2, e.item3, e.item4, e.item5);
    }

    public void consumeShopItem(ServerPlayer player, Item targetItem, int amount) {
        PlayerEntry entry = getOrCreateEntry(player);
        List<ShopItem> items = List.of(entry.item1, entry.item2, entry.item3, entry.item4, entry.item5);
        for (ShopItem item : items) {
            if (item.item() == targetItem && item.remaining() >= amount) {
                item.consume(amount);
                setDirty();
                return;
            }
        }
    }
}
