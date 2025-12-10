package net.jwn.jwnsshoppingmod.command;

import net.jwn.jwnsshoppingmod.JWNsMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.server.command.ConfigCommand;

@EventBusSubscriber(modid = JWNsMod.MOD_ID)
public class ModCommands {
    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        new OpenProfileCommand(event.getDispatcher());
        new TestCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }
}
