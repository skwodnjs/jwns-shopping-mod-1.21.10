package net.jwn.jwnsshoppingmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.jwn.jwnsshoppingmod.shop.PlayerCoinData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class TestCommand {
    public TestCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("test")
            .executes(this::execute));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        // BLOCKS 목록 가져오기
//        ServerPlayer player = context.getSource().getPlayer();
//        ServerLevel level = player.level();
//        PlayerBlockTimerData data = PlayerBlockTimerData.get(level);
//
//        List<Block> blocks = data.getBlocks(player);
//
//        MutableComponent msg = Component.literal("현재 블록 목록: ");
//
//        for (int i = 0; i < blocks.size(); i++) {
//            Block block = blocks.get(i);
//
//            String name = block.getName().getString();
//            msg.append(name);
//
//            if (i < blocks.size() - 1) {
//                msg.append(", ");
//            }
//        }
//
//        player.sendSystemMessage(msg);

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