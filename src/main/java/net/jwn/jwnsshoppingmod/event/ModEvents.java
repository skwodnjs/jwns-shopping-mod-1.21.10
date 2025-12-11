package net.jwn.jwnsshoppingmod.event;

import net.jwn.jwnsinvitationmod.item.ModItems;
import net.jwn.jwnsshoppingmod.JWNsMod;
import net.jwn.jwnsshoppingmod.profile.ProfileData;
import net.jwn.jwnsshoppingmod.profile.ProfileDataStorage;
import net.jwn.jwnsshoppingmod.shop.PlayerBlockTimerData;
import net.jwn.jwnsshoppingmod.shop.PlayerCoinData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = JWNsMod.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        ItemStack stack = new ItemStack(ModItems.INVITATION_CARD.get());

        if (!player.getInventory().add(stack)) {
            player.displayClientMessage(Component.literal("YOUR INVENTORY IS FULL"), false);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            ProfileData data = ProfileDataStorage.loadByPlayerName(serverPlayer.level().getServer(), event.getEntity().getDisplayName().getString());
            if (data == null) {
                ProfileData newData = new ProfileData(
                    player.getName().getString(),
                    1,
                    "No Alias",
                    0,
                        System.currentTimeMillis(),
                    "자기소개를 입력해주세요!"
                );
                ProfileDataStorage.saveByPlayerName(serverPlayer.level().getServer(), serverPlayer.getDisplayName().getString(), newData);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();

        if (player instanceof ServerPlayer serverPlayer) {
            ProfileData profileData = ProfileDataStorage.loadByPlayerName(serverPlayer.level().getServer(), event.getEntity().getDisplayName().getString());
            PlayerCoinData coinData = PlayerCoinData.get(serverPlayer.level());
            assert profileData != null;
            profileData.setTime(System.currentTimeMillis());
            profileData.setLevel(player.experienceLevel);
            profileData.setCoins(coinData.getCoins(serverPlayer));
            ProfileDataStorage.saveByPlayerName(serverPlayer.level().getServer(), serverPlayer.getDisplayName().getString(), profileData);
        }
    }

    @SubscribeEvent
    public static void onPlayerTickEvent(PlayerTickEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerBlockTimerData data = PlayerBlockTimerData.get(serverPlayer.level());
            if (data.getTimer(serverPlayer) == 0) data.resetBlocklist(serverPlayer);
            data.tickPlayer(serverPlayer);
        }
    }
}
