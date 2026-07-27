package me.lukiiy.flobby.cmd;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.lukiiy.flobby.Flobby;
import me.lukiiy.flow.FDefaults;
import me.lukiiy.flow.Flow;
import me.lukiiy.flow.FlowPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Leadership {
    private static final LiteralArgumentBuilder<CommandSourceStack> main = Commands.literal("leadership")
            .executes(it -> {
                FlowPlayer leader = Flow.getInstance().getLeader();
                if (leader == null) return 0;

                CommandSender sender = it.getSource().getSender();
                Player leaderP = leader.getPlayer();

                if (it.getSource().getSender() == leaderP) {
                    sender.sendMessage(Component.empty().append(Flobby.LEADER_PREFIX).append(Component.text("Specify a player to transfer leadership!").color(FDefaults.ORANGE)));

                    return Command.SINGLE_SUCCESS;
                }

                sender.sendMessage(Component.empty().append(Flobby.LEADER_PREFIX).append(Component.text("The leader is ").append(leaderP.displayName())));

                return Command.SINGLE_SUCCESS;
            })
            .then(Commands.argument("player", ArgumentTypes.player())
                    .requires(it -> it.getSender().hasPermission("flow.host") || Flow.getInstance().getLeader() != null && Flow.getInstance().getLeader().getPlayer() == it.getSender())
                    .executes(it -> {
                        Player target = it.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(it.getSource()).getFirst();

                        Flobby.getInstance().setLeader(target);

                        return Command.SINGLE_SUCCESS;
                    }));

    public static LiteralCommandNode<CommandSourceStack> register() {
        return main.build();
    }
}
