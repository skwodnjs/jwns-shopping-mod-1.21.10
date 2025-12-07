package net.jwn.jwnsshoppingmod.event;

import net.jwn.jwnsinvitationmod.item.ModItems;
import net.jwn.jwnsshoppingmod.JWNsMod;
import net.minecraft.network.chat.Component;
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
    }
}
