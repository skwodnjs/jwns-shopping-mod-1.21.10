package net.jwn.jwnsshoppingmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.jwn.jwnsshoppingmod.networking.packet.OpenProfileScreenS2CPacket;
import net.jwn.jwnsshoppingmod.profile.ProfileData;
import net.jwn.jwnsshoppingmod.profile.ProfileDataStorage;
import net.jwn.jwnsshoppingmod.shop.PlayerCoinData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Objects;

public class OpenProfileCommand {
    public OpenProfileCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("profile")
            .then(Commands.argument("playerName", StringArgumentType.string())
            .executes(this::execute)));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "playerName");
        MinecraftServer server = context.getSource().getServer();
        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        boolean isPlayerOnline = players.stream().anyMatch(sp -> sp.getDisplayName().getString().equals(name));

        ProfileData data = ProfileDataStorage.loadByPlayerName(context.getSource().getServer(), name);
        if (data == null) {
            if (context.getSource().getPlayer() != null) {
                context.getSource().getPlayer().displayClientMessage(Component.literal("프로필 검색 실패"), false);
            }
            return 1;
        }

        OpenProfileScreenS2CPacket packet;

        if (isPlayerOnline) {
            ServerPlayer player = server.getPlayerList().getPlayerByName(name);
            PlayerCoinData coinData = PlayerCoinData.get(player.level());
            assert player != null;
            packet = new OpenProfileScreenS2CPacket(
                    name, player.experienceLevel, data.getAlias(), coinData.getCoins(player), data.getTimeMillis(), true, data.getComment()
            );
        } else {
            packet = new OpenProfileScreenS2CPacket(
                    name, data.getLevel(), data.getAlias(), data.getCoins(), data.getTimeMillis(), false, data.getComment()
            );
        }

        PacketDistributor.sendToPlayer(Objects.requireNonNull(context.getSource().getPlayer()), packet);
        return 1;
    }
}