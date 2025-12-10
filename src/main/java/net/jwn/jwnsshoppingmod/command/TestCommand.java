package net.jwn.jwnsshoppingmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.jwn.jwnsshoppingmod.shop.PlayerBlockTimerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class TestCommand {
    public TestCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("test")
            .executes(this::execute));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        ServerLevel level = player.level();
        PlayerBlockTimerData data = PlayerBlockTimerData.get(level);

        List<Block> blocks = data.getBlocks(player); // getBlocks(ServerPlayer) 있다고 가정

        MutableComponent msg = Component.literal("현재 블록 목록: ");

        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);

            String name = block.getName().getString();
            msg.append(name);

            if (i < blocks.size() - 1) {
                msg.append(", ");
            }
        }

        player.sendSystemMessage(msg);
        return 1;
    }
}