package net.jwn.jwnsshoppingmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.jwn.jwnsshoppingmod.shop.PlayerCoinData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class CoinCommand {
    public CoinCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("coin")
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.literal("set")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(this::executeSet)))
                                .then(Commands.literal("add")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(this::executeAdd)))
                                .then(Commands.literal("get")
                                        .executes(this::executeGet))
                        )
        );
    }

    private int executeSet(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = EntityArgument.getPlayer(context, "target");
        int amount = IntegerArgumentType.getInteger(context, "amount");

        PlayerCoinData data = PlayerCoinData.get(player.level());
        data.setCoins(player, amount);

        source.sendSuccess(() -> Component.translatable("jwnsshoppingmod.networking.set_coin", amount), false);
        return 1;
    }

    private int executeAdd(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = EntityArgument.getPlayer(context, "target");
        int amount = IntegerArgumentType.getInteger(context, "amount");

        PlayerCoinData data = PlayerCoinData.get(player.level());
        data.addCoins(player, amount);

        source.sendSuccess(() -> Component.translatable("jwnsshoppingmod.networking.add_coin", amount), false);
        return 1;
    }

    private int executeGet(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = EntityArgument.getPlayer(context, "target");
        PlayerCoinData data = PlayerCoinData.get(player.level());
        source.sendSuccess(() -> Component.translatable("jwnsshoppingmod.networking.get_coin", data.getCoins(player)), false);
        return 1;
    }
}