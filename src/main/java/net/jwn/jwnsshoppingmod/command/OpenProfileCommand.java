package net.jwn.jwnsshoppingmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.jwn.jwnsshoppingmod.networking.packet.OpenProfileScreenS2CPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

public class OpenProfileCommand {
    public OpenProfileCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("profile")
            .then(Commands.argument("playerName", StringArgumentType.string())
            .executes(this::execute)));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        OpenProfileScreenS2CPacket packet = new OpenProfileScreenS2CPacket(
                StringArgumentType.getString(context, "playerName"),
                10,
                "서버관리자",
                99998,
                23,
                false,
                "안녕하세요, 나재원입니다. 잘부탁드립니다..."
        );
        PacketDistributor.sendToPlayer(Objects.requireNonNull(context.getSource().getPlayer()), packet);
        return 1;
    }
}