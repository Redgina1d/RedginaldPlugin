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

public class Executor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	switch (label.toLowerCase()) {
		case "ohatk": {
			if (args.length > 0) {
				return engraveCmd(sender, args);
			} else {
				return false;
			}
		}
		default: 
			return false;
    	}
    }
    private boolean engraveCmd(CommandSender sender, String[] args) {
    	Player player = (Player) sender;
			if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
				NamespacedKey key = new NamespacedKey(OffhandAttack.instance, "offhand_dmg");
				NamespacedKey key2 = new NamespacedKey(OffhandAttack.instance, "offhand_cd");
	    		if (args[1].equals("add") && Double.parseDouble(args[2]) != 0 && Integer.parseInt(args[3]) != 0) {
	    			player.getInventory().setItemInMainHand(setEngrave(player.getInventory().getItemInMainHand(),(short) 1));
	    			player.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, Double.parseDouble(args[2]));
	    			player.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().set(key2, PersistentDataType.INTEGER, Integer.parseInt(args[2]));
	        		player.sendMessage("§6§l[OffhandAttack] §e- A special engrave was applied to your item. Now it can be swinged using second hand. Offhand damage for this item: " + args[2] + ". Cooldown for this item (in ticks): " + args[3]);
	        		return true;
	    		} else if (args[1].equals("remove")) {
	    			player.getInventory().setItemInMainHand(setEngrave(player.getInventory().getItemInMainHand(),(short) 0));
	    			player.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().remove(key);
	    			player.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().remove(key2);
	        		player.sendMessage("§6§l[OffhandAttack] §e- A special engrave was removed from your item.");
	        		return true;
	    		} else {
	    			player.sendMessage("§6§l[OffhandAttack] §e- Invalid argument provided: " + args[1] + ". Use \"add\" or \"remove\".");
	    			return false;
	    		}
	    	} else {
	    		player.sendMessage("§6§l[OffhandAttack] §e- You don't have item in your hand!");
	    		return false;
	    	}
	}

    private ItemStack setEngrave(ItemStack item, short val) {
    	ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(OffhandAttack.instance, "offhand_atk");
    	if (val == 1) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "");
            item.setItemMeta(meta);
    	} else {
    		meta.getPersistentDataContainer().remove(key);
            item.setItemMeta(meta);
    	}
    	return item;
	}

}
