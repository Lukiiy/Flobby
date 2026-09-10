package me.lukiiy.flobby.cmd;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.lukiiy.flow.BaseGameEntry;
import me.lukiiy.flow.FDefaults;
import me.lukiiy.flow.Flow;
import me.lukiiy.flow.GameEntry;
import me.lukiiy.flow.commands.Host;
import me.lukiiy.flow.setting.BooleanSetting;
import me.lukiiy.flow.setting.CycleSetting;
import me.lukiiy.flow.setting.DoubleSetting;
import me.lukiiy.flow.setting.Setting;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.Comparator;

public class MenuCUI {
    private static final String ROOT = "/cuimenu";

    public static LiteralCommandNode<CommandSourceStack> register() {
        return Commands.literal(ROOT.substring(1))
                .requires(source -> powerReq(source.getSender()))
                .executes(ctx -> {
                    home((Player) ctx.getSource().getSender());

                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("games").executes(ctx -> {
                    games((Player) ctx.getSource().getSender());

                    return Command.SINGLE_SUCCESS;
                }))
                .then(Commands.literal("select")
                        .then(Commands.argument("id", StringArgumentType.word()).executes(ctx -> {
                            Player player = (Player) ctx.getSource().getSender();
                            String id = StringArgumentType.getString(ctx, "id");

                            Flow.getInstance().getManager().setCurrent(id);
                            home(player);

                            return Command.SINGLE_SUCCESS;
                        })))
                .then(Commands.literal("start").executes(_ -> {
                    Flow.getInstance().getManager().startCurrent();

                    return Command.SINGLE_SUCCESS;
                }))
                .then(Commands.literal("stop").executes(_ -> {
                    Flow.getInstance().getManager().stopCurrent();

                    return Command.SINGLE_SUCCESS;
                }))
                .then(Commands.literal("settings").executes(ctx -> {
                    settings((Player) ctx.getSource().getSender());

                    return Command.SINGLE_SUCCESS;
                }))
                .then(Commands.literal("setting").then(Commands.argument("id", StringArgumentType.word())
                                .then(Commands.literal("toggle").executes(ctx -> {
                                    Player player = (Player) ctx.getSource().getSender();
                                    String id = StringArgumentType.getString(ctx, "id");

                                    GameEntry game = Flow.getInstance().getManager().getCurrent();
                                    if (game == null) return 0;

                                    Setting<?> setting = game.getSetting(id);

                                    if (setting instanceof BooleanSetting boolSet) {
                                        boolSet.setValue(!boolSet.getValue());

                                        player.sendMessage(FDefaults.success(Component.text(boolSet.getName() + ": " + boolSet.getValue())));
                                    }

                                    settings(player);
                                    return Command.SINGLE_SUCCESS;
                                }))
                                .then(Commands.literal("+").executes(ctx -> {
                                    Player player = (Player) ctx.getSource().getSender();
                                    String id = StringArgumentType.getString(ctx, "id");

                                    GameEntry game = Flow.getInstance().getManager().getCurrent();
                                    if (game == null) return 0;

                                    Setting<?> setting = game.getSetting(id);

                                    if (setting instanceof DoubleSetting doubleSet) {
                                        doubleSet.onInteract(false);

                                        player.sendMessage(FDefaults.success(Component.text(doubleSet.getName() + ": " + doubleSet.getValue())));
                                    }

                                    settings(player);
                                    return Command.SINGLE_SUCCESS;
                                }))
                                .then(Commands.literal("-").executes(ctx -> {
                                    Player player = (Player) ctx.getSource().getSender();
                                    String id = StringArgumentType.getString(ctx, "id");

                                    GameEntry game = Flow.getInstance().getManager().getCurrent();
                                    if (game == null) return 0;

                                    Setting<?> setting = game.getSetting(id);

                                    if (setting instanceof DoubleSetting doubleSet) {
                                        doubleSet.onInteract(true);

                                        player.sendMessage(FDefaults.success(Component.text(doubleSet.getName() + ": " + doubleSet.getValue())));
                                    }

                                    settings(player);
                                    return Command.SINGLE_SUCCESS;
                                }))
                                .then(Commands.literal("next").executes(ctx -> {
                                    Player player = (Player) ctx.getSource().getSender();
                                    String id = StringArgumentType.getString(ctx, "id");

                                    GameEntry game = Flow.getInstance().getManager().getCurrent();
                                    if (game == null) return 0;

                                    Setting<?> setting = game.getSetting(id);
                                    if (setting instanceof CycleSetting<?> cycleSetting) {
                                        cycleSetting.onInteract(false);
                                        player.sendMessage(FDefaults.success(Component.text(cycleSetting.getName() + ": " + cycleSetting.getValue())));
                                    }

                                    settings(player);
                                    return Command.SINGLE_SUCCESS;
                                }))
                                .then(Commands.literal("prev").executes(ctx -> {
                                    Player player = (Player) ctx.getSource().getSender();
                                    String id = StringArgumentType.getString(ctx, "id");

                                    GameEntry game = Flow.getInstance().getManager().getCurrent();
                                    if (game == null) return 0;

                                    Setting<?> setting = game.getSetting(id);

                                    if (setting instanceof CycleSetting<?> cycleSetting) {
                                        cycleSetting.onInteract(true);

                                        player.sendMessage(FDefaults.success(Component.text(cycleSetting.getName() + ": " + cycleSetting.getValue())));
                                    }

                                    settings(player);
                                    return Command.SINGLE_SUCCESS;
                                })))
                ).build();
    }

    public static void show(Player player) {
        home(player);
    }

    private static void home(Player player) {
        var manager = Flow.getInstance().getManager();
        var current = manager.getCurrent();

        if (current == null) {
            player.sendMessage(FDefaults.fail(asMini("No game registered.")));
            return;
        }

        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("Game Manager", FDefaults.PINK));
        player.sendMessage(Component.text("Current: ").color(FDefaults.WHITE).append(current.displayName()));

        if (manager.getEntries().size() > 1) player.sendMessage(button("Change Game", ROOT + " games", FDefaults.ORANGE));

        player.sendMessage(button("Settings", ROOT + " settings", FDefaults.YELLOW));

        if (manager.getCurrentRun() != null) {
            player.sendMessage(button("Stop", ROOT + " stop", FDefaults.RED));
        } else {
            player.sendMessage(button("Start", ROOT + " start", FDefaults.GREEN));
        }

        player.sendMessage(Component.empty());
    }

    private static void games(Player player) {
        var manager = Flow.getInstance().getManager();
        var current = manager.getCurrent();

        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("Game Manager: Select Game").color(FDefaults.PINK));

        manager.getEntries().stream().sorted(Comparator.comparing(BaseGameEntry::getName))
                .forEach(game -> {
                    Component line = Component.empty()
                            .append(game.equals(current) ? Component.text("» ").color(FDefaults.GREEN) : Component.empty())
                            .append(game.displayName())
                            .append(Component.text(" (" + game.getId() + ")").color(FDefaults.GRAY))
                            .clickEvent(ClickEvent.runCommand(ROOT + " select " + game.getId()))
                            .hoverEvent(Component.text("Select " + game.getName()).color(FDefaults.YELLOW));

                    player.sendMessage(line);
                });

        player.sendMessage(button("Back", ROOT, FDefaults.YELLOW));
        player.sendMessage(Component.empty());
    }

    private static void settings(Player player) {
        var game = Flow.getInstance().getManager().getCurrent();
        if (game == null) return;

        game.refreshSettings();

        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("Game Manager: " + game.getName() + " Settings").color(FDefaults.PINK));

        game.getSettings().stream().map(MenuCUI::settingLine).forEach(player::sendMessage);

        player.sendMessage(Component.empty());
        player.sendMessage(button("Back", ROOT, FDefaults.YELLOW));
        player.sendMessage(Component.empty());
    }

    private static Component settingLine(Setting<?> setting) {
        Component base = Component.empty().append(Component.text(setting.getName() + ": ", FDefaults.WHITE)).append(Component.text(String.valueOf(setting.getValue())).color(FDefaults.CYAN));

        return switch (setting) {
            case BooleanSetting _ -> base.appendSpace().append(button("Toggle", ROOT + " setting " + setting.getId() + " toggle", FDefaults.GREEN));
            case DoubleSetting _ -> base.appendSpace().append(button("-", ROOT + " setting " + setting.getId() + " -", FDefaults.RED)).appendSpace().append(button("+", ROOT + " setting " + setting.getId() + " +", FDefaults.GREEN));
            case CycleSetting<?> _ -> base.appendSpace().append(button("<", ROOT + " setting " + setting.getId() + " prev", FDefaults.ORANGE)).appendSpace().append(button(">", ROOT + " setting " + setting.getId() + " next", FDefaults.ORANGE));
            default -> base;
        };
    }

    private static Component button(String text, String command, TextColor color) {
        return Component.text("[ ", color).append(Component.text(text, color)).append(Component.text(" ]", color)).clickEvent(ClickEvent.runCommand(command)).hoverEvent(Component.text(command, FDefaults.GRAY));
    }

    private static Component asMini(String text) {
        return MiniMessage.miniMessage().deserialize(text);
    }

    private static boolean powerReq(org.bukkit.command.CommandSender sender) {
        return Host.INSTANCE.getPowerReq().invoke(sender);
    }
}
