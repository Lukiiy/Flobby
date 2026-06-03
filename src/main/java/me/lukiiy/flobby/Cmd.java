package me.lukiiy.flobby;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.lukiiy.flow.FDefaults;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class Cmd {
    private static final LiteralArgumentBuilder<CommandSourceStack> main = Commands.literal("lobby")
            .executes(it -> {
                if (!(it.getSource().getSender() instanceof Player player)) throw FDefaults.NON_PLAYER;

                Flobby.getInstance().sendToLobby(player);
                
                return Command.SINGLE_SUCCESS;
            });

    private static final LiteralArgumentBuilder<CommandSourceStack> setPos = Commands.literal("setpos")
            .executes(it -> {
                if (!(it.getSource().getSender() instanceof Player player)) throw FDefaults.NON_PLAYER;

                Flobby.getInstance().setMain(player.getLocation());
                player.sendMessage(FDefaults.success(Component.text("Lobby position set!")));

                return Command.SINGLE_SUCCESS;
            });

    private static final LiteralArgumentBuilder<CommandSourceStack> setBoostY = Commands.literal("setboosty")
            .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                    .executes(it -> {
                        double value = DoubleArgumentType.getDouble(it, "value");

                        Flobby.getInstance().setBoostY(value);
                        it.getSource().getSender().sendMessage(FDefaults.success(Component.text("Booster Y set to " + value)));

                        return Command.SINGLE_SUCCESS;
                    }));

    private static final LiteralArgumentBuilder<CommandSourceStack> setCutOffRadius = Commands.literal("setcutoffradius")
            .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                    .executes(it -> {
                        double value = DoubleArgumentType.getDouble(it, "value");

                        Flobby.getInstance().setCutOffRadius(value);
                        it.getSource().getSender().sendMessage(FDefaults.success(Component.text("Cutoff radius set to " + value)));

                        return Command.SINGLE_SUCCESS;
                    }));

    public static LiteralCommandNode<CommandSourceStack> register() {
        return main.then(setPos).then(setBoostY).then(setCutOffRadius).build();
    }
}