package net.ironingot.kanachat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class KanaChatCommand implements CommandExecutor {
    private final KanaChat plugin;
    private final String pluginName;
    private final String pluginVersion;

    public KanaChatCommand(KanaChat plugin) {
        this.plugin = plugin;
        this.pluginName = plugin.getName();
        this.pluginVersion = plugin.getPluginMeta().getVersion();
    }

    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String commandLabel, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("dict")) {
            return executeDictionaryCommand(sender, args);
        }

        String command;
        String option;

        command = (args.length >= 1) ? args[0].toLowerCase() : "get";
        option = (args.length >= 2) ? args[1].toLowerCase() : null;

        return executeCommand(sender, command, option);
    }

    private boolean executeDictionaryCommand(CommandSender sender, String[] args) {
        if (args.length >= 2 && args[1].equalsIgnoreCase("list")) {
            Map<String, String> entries = plugin.getDictionary().getEntries();
            if (entries.isEmpty()) {
                sender.sendMessage(Component.text("KanaChat dictionary is empty.", NamedTextColor.GOLD));
            } else {
                for (Map.Entry<String, String> entry : entries.entrySet()) {
                    sender.sendMessage(Component.text(entry.getKey() + " <- " + entry.getValue(), NamedTextColor.GOLD));
                }
            }
            return true;
        }

        if (!sender.isOp()) {
            sender.sendMessage(Component.text("Only operators can modify the dictionary.", NamedTextColor.RED));
            return true;
        }

        if (args.length >= 3 && args[1].equalsIgnoreCase("remove")) {
            boolean removed = plugin.getDictionary().remove(args[2]);
            sender.sendMessage(Component.text(
                    (removed ? "Removed dictionary entry: " : "Dictionary entry not found: ") + args[2],
                    removed ? NamedTextColor.GOLD : NamedTextColor.RED
            ));
            return true;
        }

        if (args.length >= 4
                && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("edit"))) {
            String[] readings = new String[args.length - 3];
            System.arraycopy(args, 3, readings, 0, readings.length);
            plugin.getDictionary().set(args[2], readings);
            sender.sendMessage(Component.text(
                    (args[1].equalsIgnoreCase("add") ? "Added" : "Updated") +
                            " dictionary entry: " + args[2],
                    NamedTextColor.GOLD
            ));
            return true;
        }

        sender.sendMessage(Component.text(
                "Usage: /kanachat dict list | add <word> <alphabet...> | edit <word> <alphabet...> | remove <word>",
                NamedTextColor.GOLD
        ));
        return true;
    }

    private boolean executeCommand(CommandSender sender, String command, String option) {
        if (command != null && command.equals("version")) {
            sender.sendMessage(Component.text(this.pluginName + "-" + this.pluginVersion, NamedTextColor.GOLD));
            return true;
        }

        if (command != null && command.equals("kanji")) {
            if (option != null && (option.equals("on") || option.equals("true"))) {
                plugin.getConfiguration().setKanjiEnabled(sender.getName(), true);
            }
            if (option != null && (option.equals("off") || option.equals("false"))) {
                plugin.getConfiguration().setKanjiEnabled(sender.getName(), false);
            }

            if (plugin.getConfiguration().isKanjiEnabled(sender.getName())) {
                sender.sendMessage(Component.text(pluginName + " Kanji conversion is enabled.", NamedTextColor.GOLD));
            } else {
                sender.sendMessage(Component.text(pluginName + " Kanji conversion is disabled.", NamedTextColor.GOLD));
            }
            return true;
        }

        if (command != null) {
            if (command.equals("on") || command.equals("true")) {
                plugin.getConfiguration().setKanaEnabled(sender.getName(), true);
            }
            if (command.equals("off") || command.equals("false")) {
                plugin.getConfiguration().setKanaEnabled(sender.getName(), false);
            }
        }

        if (plugin.getConfiguration().isKanaEnabled(sender.getName())) {
            sender.sendMessage(Component.text(pluginName + " is enabled.", NamedTextColor.GOLD));
        } else {
            sender.sendMessage(Component.text(pluginName + " is disabled.", NamedTextColor.GOLD));
        }
        return true;
    }

}