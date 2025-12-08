package net.jwn.jwnsshoppingmod.event;

import net.jwn.jwnsinvitationmod.item.ModItems;
import net.jwn.jwnsshoppingmod.JWNsMod;
import net.jwn.jwnsshoppingmod.profile.ProfileData;
import net.jwn.jwnsshoppingmod.profile.ProfileDataStorage;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

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
            ProfileData data = new ProfileData(
                    player.getName().getString(),
                    1,
                    "No Alias",
                    0,
                    10,
                    true,
                    "첫 저장입니다."
            );

            ProfileDataStorage.saveByPlayerName(serverPlayer.level().getServer(), serverPlayer.getDisplayName().getString(), data);
        }
    }
}
