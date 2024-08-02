package net.plugins.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OhatkTabComplete implements TabCompleter {
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (sender instanceof Player) {
    		if (args.length == 1) {
                completions.add("engrave");
                completions.add("manage");
    		}
    		if (args.length == 2) {
    			if (args[0].equals("engrave")) {
    				completions.add("add");
    				completions.add("remove");
    			}
    		}
    		if (args.length == 5) {
    			completions.add("true");
    			completions.add("false");
    		}
        }
		return completions;
    }
}
