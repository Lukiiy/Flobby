package me.lukiiy.flobby.cmd;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.lukiiy.flobby.Flobby;
import me.lukiiy.flow.FDefaults;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class Main {
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
            .executes(it -> {
                Flobby.getInstance().setBoostY(null);
                it.getSource().getSender().sendMessage(FDefaults.success(Component.text("Disabled Void Booster!")));

                return Command.SINGLE_SUCCESS;
            })
            .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                    .executes(it -> {
                        double value = DoubleArgumentType.getDouble(it, "value");

                        Flobby.getInstance().setBoostY(value);
                        it.getSource().getSender().sendMessage(FDefaults.success(Component.text("Booster Y set to " + value)));

                        return Command.SINGLE_SUCCESS;
                    }));

    private static final LiteralArgumentBuilder<CommandSourceStack> setCutOffRadius = Commands.literal("setcutoffradius")
            .executes(it -> {
                Flobby.getInstance().setCutOffRadius(null);
                it.getSource().getSender().sendMessage(FDefaults.success(Component.text("Disabled Cutoff Radius!")));

                return Command.SINGLE_SUCCESS;
            })
            .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                    .executes(it -> {
                        double value = DoubleArgumentType.getDouble(it, "value");

                        Flobby.getInstance().setCutOffRadius(value);
                        it.getSource().getSender().sendMessage(FDefaults.success(Component.text("Cutoff radius set to " + value)));

                        return Command.SINGLE_SUCCESS;
                    }));

    private static final LiteralArgumentBuilder<CommandSourceStack> transfer = Commands.literal("transfer")
            .then(Commands.argument("player", ArgumentTypes.player())
                    .executes(it -> {
                        Player target = it.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(it.getSource()).getFirst();

                        Flobby.getInstance().setLeader(target);

                        return Command.SINGLE_SUCCESS;
                    }));

    public static LiteralCommandNode<CommandSourceStack> register() {
        return main.then(setPos).then(setBoostY).then(setCutOffRadius).then(transfer).build();
    }
}