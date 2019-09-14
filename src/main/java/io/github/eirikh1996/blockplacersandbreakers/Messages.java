package io.github.eirikh1996.blockplacersandbreakers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Messages {
    public static final String TAB = "    ";
    public static final String BPB_PREFIX = ChatColor.BLUE + "[" + ChatColor.GREEN + "BlockPlacersAndBreakers" + ChatColor.BLUE + "] " + ChatColor.RESET;
    public static final String ERROR = ChatColor.RED + "Error: ";

    public static String[] updateMessage(double newVersion){
        String[] messages = new String[5];
        messages[0] = ChatColor.BLUE + "=========[" + ChatColor.GREEN + "BlockPlacersAndBreakers update" + ChatColor.BLUE + "]==============";
        messages[1] = "Version " + newVersion + " of BlockPlacersAndBreakers is now available";
        messages[2] = "You are currently on version " + BlockPlacersAndBreakers.getInstance().getDescription().getVersion();
        messages[3] = "Download your update at " + "https://dev.bukkit.org/projects/blockplacersandbreakers/files/latest";
        messages[4] = "=======================================================";
        return messages;
    }
}
