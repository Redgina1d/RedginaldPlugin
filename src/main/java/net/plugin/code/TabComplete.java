package net.plugin.code;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (sender instanceof Player) {
    		if (args.length == 1) {
                completions.add("engrave");
                completions.add("manage");
    		}
            if (args.length == 2 && args[0] == "manage") {
            	completions.add("player");
            	completions.add("global");
            }
            if (args.length == 3) {
            	completions.add("set_speed_penalty");
            	completions.add("set_dmg_penalty");
            }
            return completions;
        }
		return null;
    }
}