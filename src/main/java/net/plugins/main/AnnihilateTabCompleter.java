package net.plugins.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AnnihilateTabCompleter implements TabCompleter {
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (sender instanceof Player) {
    		if (args.length == 1) {
                completions.add("Containers");
                completions.add("PackAnimals");
    		}
    		if (args.length == 2) {
    			if (args[0].equals("Containers") || args[0].equals("PackAnimals")) {
    				completions.add("__CONFIRM__");
    			}
    		}
        }
		return completions;
    }
}
