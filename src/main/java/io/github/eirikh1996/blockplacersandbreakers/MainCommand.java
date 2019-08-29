package io.github.eirikh1996.blockplacersandbreakers;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommand implements CommandExecutor {
    private final BlockPlacersAndBreakers bpb;

    public MainCommand(BlockPlacersAndBreakers bpb) {
        this.bpb = bpb;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!command.getName().equalsIgnoreCase("blockplacersandbreakers")){
            return true;
        }
        commandSender.sendMessage(ChatColor.DARK_AQUA + "==============================================================");
        commandSender.sendMessage(ChatColor.BLUE + "Name: " + bpb.getDescription().getName());
        commandSender.sendMessage(ChatColor.BLUE + "Author: " + bpb.getDescription().getAuthors());
        commandSender.sendMessage(ChatColor.BLUE + "Version: " + bpb.getDescription().getVersion());
        commandSender.sendMessage(ChatColor.BLUE + "Download page: " + ChatColor.RESET + "https://dev.bukkit.org/projects/blockplacersandbreakers");
        commandSender.sendMessage(ChatColor.DARK_AQUA + "==============================================================");
        return false;
    }
}
