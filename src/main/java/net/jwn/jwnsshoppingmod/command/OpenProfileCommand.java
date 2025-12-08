package net.jwn.jwnsshoppingmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.jwn.jwnsshoppingmod.networking.packet.OpenProfileScreenS2CPacket;
import net.jwn.jwnsshoppingmod.profile.ProfileData;
import net.jwn.jwnsshoppingmod.profile.ProfileDataStorage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

public class OpenProfileCommand {
    public OpenProfileCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("profile")
            .then(Commands.argument("playerName", StringArgumentType.string())
            .executes(this::execute)));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        ProfileData data = ProfileDataStorage.loadByPlayerName(context.getSource().getServer(), StringArgumentType.getString(context, "playerName"));
        if (data == null) {
            if (context.getSource().getPlayer() != null) {
                context.getSource().getPlayer().displayClientMessage(Component.literal("프로필 검색 실패"), false);
            }
            return 1;
        }
        OpenProfileScreenS2CPacket packet = new OpenProfileScreenS2CPacket(
                data.getName(), data.getLevel(), data.getAlias(), data.getCoins(), data.getTime(), data.getIsMinute(), data.getComment()
        );
        PacketDistributor.sendToPlayer(Objects.requireNonNull(context.getSource().getPlayer()), packet);
        return 1;
    }
}