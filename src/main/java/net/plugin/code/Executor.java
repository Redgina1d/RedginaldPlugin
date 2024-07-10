package net.plugin.code;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class Executor extends OffhandAttack  implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
        	switch (label.toLowerCase()) {
			case "ohatk": {
				if (args[0] == "engrave") {
					return engrPlayerHand((Player) sender, args[1]);
				}
			}
			default:
				return false;
			}
        }
        return false;
    }

    public ItemStack setEngrave(ItemStack item, short val) {
    	ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(instance, "offhand_atk");
    	if (val == 1) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "");
            item.setItemMeta(meta);
    	} else {
    		meta.getPersistentDataContainer().remove(key);
            item.setItemMeta(meta);
    	}
    	return item;
	}
    
    private boolean engrPlayerHand(Player player, String value) {
    	if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
    		if (value == "add") {
    			setEngrave(player.getInventory().getItemInMainHand(),(short) 1);
        		player.sendMessage("§6§l[OffhandAttack[ §e- A special engrave was applied to your item. Now it can be swinged using second hand.");
    		} else if (value == "remove") {
    			setEngrave(player.getInventory().getItemInMainHand(),(short) 0);
        		player.sendMessage("§6§l[OffhandAttack[ §e- A special engrave was removed from your item.");
    		} else {
    			player.sendMessage("§6§l[OffhandAttack[ §e- Invalid argument provided. Use \"add\" or \"remove\".");
    		}
    	} else {
    		player.sendMessage("§6§l[OffhandAttack[ §e- You don't have item in your hand!");
    	}
		return true;
    }
}
