package io.github.eirikh1996.blockplacersandbreakers;

import io.github.eirikh1996.blockplacersandbreakers.objects.BlockBreaker;
import io.github.eirikh1996.blockplacersandbreakers.objects.BlockPlacer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.eirikh1996.blockplacersandbreakers.Messages.BPB_PREFIX;
import static io.github.eirikh1996.blockplacersandbreakers.Messages.ERROR;

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
        else if (strings[0].equalsIgnoreCase("info")){
            commandSender.sendMessage(ChatColor.DARK_AQUA + "==============================================================");
            commandSender.sendMessage(ChatColor.BLUE + "Name: " + bpb.getDescription().getName());
            commandSender.sendMessage(ChatColor.BLUE + "Author: " + bpb.getDescription().getAuthors());
            commandSender.sendMessage(ChatColor.BLUE + "Version: " + bpb.getDescription().getVersion());
            commandSender.sendMessage(ChatColor.BLUE + "Download page: " + ChatColor.RESET + "https://dev.bukkit.org/projects/blockplacersandbreakers");
            commandSender.sendMessage(ChatColor.DARK_AQUA + "==============================================================");
            return true;
        } else if (strings[0].equalsIgnoreCase("placers")){
            if (strings.length == 2 && !commandSender.hasPermission("blockplacersandbreakers.command.placers.other") && !commandSender.hasPermission("bpb.command.placers.other")){
                commandSender.sendMessage(BPB_PREFIX + ERROR + "You have no permission to check other's block placers");
                return true;
            } else if (strings.length == 2){
                OfflinePlayer otherOwner = Bukkit.getOfflinePlayer(strings[1]);
                for (BlockPlacer placer : BlockPlacersAndBreakers.getInstance().getBlockPlacers()){
                    if (!placer.getOwner().equals(otherOwner.getUniqueId())){
                        continue;
                    }
                    commandSender.sendMessage(placer.getLocation().getWorld().getName() + ": " + placer.getLocation().toVector().toString());
                }
                return true;
            } else {
                for (BlockPlacer placer : BlockPlacersAndBreakers.getInstance().getBlockPlacers()){
                    if (!placer.getOwner().equals(((Player) commandSender).getUniqueId())){
                        continue;
                    }
                    commandSender.sendMessage(placer.getLocation().getWorld().getName() + ": " + placer.getLocation().toVector().toString());
                }
            }
        } else if (strings[0].equalsIgnoreCase("breakers")){
            if (strings.length == 2 && !commandSender.hasPermission("blockplacersandbreakers.command.breakers.other") && !commandSender.hasPermission("bpb.command.breakers.other")){
                commandSender.sendMessage(BPB_PREFIX + ERROR + "You have no permission to check other's block breakers");
                return true;
            } else if (strings.length == 2){
                OfflinePlayer otherOwner = Bukkit.getOfflinePlayer(strings[1]);
                for (BlockBreaker breaker : BlockPlacersAndBreakers.getInstance().getBlockBreakers()){
                    if (!breaker.getOwner().equals(otherOwner.getUniqueId())){
                        continue;
                    }
                    commandSender.sendMessage(breaker.getLocation().getWorld().getName() + ": " + breaker.getLocation().toVector().toString());
                }
                return true;
            } else {
                for (BlockBreaker breaker : BlockPlacersAndBreakers.getInstance().getBlockBreakers()){
                    if (!breaker.getOwner().equals(((Player) commandSender).getUniqueId())){
                        continue;
                    }
                    commandSender.sendMessage(breaker.getLocation().getWorld().getName() + ": " + breaker.getLocation().toVector().toString());
                }
            }
        }

        return false;
    }
}
