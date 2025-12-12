package net.jwn.jwnsshoppingmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.jwn.jwnsshoppingmod.networking.packet.OpenShopScreenS2CPacket;
import net.jwn.jwnsshoppingmod.shop.PlayerCoinData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

public class OpenShopCommand {
    public OpenShopCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("shop")
                        .then(Commands.literal("open")
                                .executes(this::execute))
        );
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        PlayerCoinData data = PlayerCoinData.get(context.getSource().getLevel());
        OpenShopScreenS2CPacket packet = new OpenShopScreenS2CPacket(data.getCoins(Objects.requireNonNull(context.getSource().getPlayer())));
        PacketDistributor.sendToPlayer(Objects.requireNonNull(context.getSource().getPlayer()), packet);
        return 1;
    }
}
