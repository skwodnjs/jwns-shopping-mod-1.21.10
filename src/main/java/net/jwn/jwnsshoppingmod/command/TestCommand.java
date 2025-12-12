package net.jwn.jwnsshoppingmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.jwn.jwnsshoppingmod.shop.PlayerBlockTimerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class TestCommand {
    public TestCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("test")
            .executes(this::execute));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        // BLOCK 가져오기
        ServerPlayer player = context.getSource().getPlayer();
        PlayerBlockTimerData data = PlayerBlockTimerData.get(player.level());
        for (int i = 0; i < data.getShopItems(player).toArray().length; i++) {
            System.out.println(data.getShopItems(player).get(i).item());
        }

        // COIN 저장하기
//        ServerPlayer player = context.getSource().getPlayer();
//        ServerLevel level = player.level();
//
//        PlayerCoinData data = PlayerCoinData.get(level);
//        data.addCoins(player, 30);
//        player.sendSystemMessage(Component.literal(data.getCoins(player) + "개"));
        return 1;
    }
}