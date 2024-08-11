package net.plugins.main;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockState;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Container;

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
		case "annihilate": {
			if (args.length > 0) {
				return annihilateCmd(sender, args);
			} else {
				return false;
			}
		}
		default: 
			return false;
    	}
    }

    private boolean annihilateCmd(CommandSender sender, String[] args) {
    	Player player = (Player) sender;
    	org.bukkit.World buk_world = player.getWorld();
    	if (player.hasPermission("containerbutcher")) {
    		Region rg = BukkitAdapter.adapt(player).getSelection();
    		if (rg != null) {
    			Extent ext = rg.getWorld();
    			Thread th = new Thread(() -> {
    				player.sendMessage("§c§lStarting §call container emptying in selected area...");
    				for (BlockVector3 position : rg) {
    		            BlockState bl_we = ext.getBlock(position);
    		            if (bl_we.getMaterial().hasContainer()) {
    		            	Location lok = new Location(buk_world, position.x(), position.y(), position.z());
    		            	Block bl_buk = lok.getBlock();
    		            	if (bl_buk.getState() instanceof Container) {
    		                    Container con = (Container) bl_buk.getState();
    		                    con.getInventory().clear();
    		                }
    		            }
    		        }
    	        });
    			
    			th.start();
    		        try {
    		            th.join();
    		            player.sendMessage("§a§lSuccess!");
    		        } catch (InterruptedException e) {
    		            e.printStackTrace();
    		            player.sendMessage("§c§cERROR.");
    		        }
    		        return true;
    		}
    	}
		return false;
    }

    private boolean engraveCmd(CommandSender sender, String[] args) {
    	Player player = (Player) sender;
			if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
				ItemStack item = player.getInventory().getItemInMainHand();
				NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(item);
	    		if (args[1].equals("add") && Double.parseDouble(args[2]) != 0 && Integer.parseInt(args[3]) != 0 && Short.parseShort(args[4]) == 0 || Short.parseShort(args[4]) == 1) {
	    			compound.put("offhand_atk", Double.parseDouble(args[2]));
	    			compound.put("offhand_cd", Integer.parseInt(args[3]));
	    			compound.put("offhand_sweep", Short.parseShort(args[4]));
	    			NbtFactory.setItemTag(item, compound);
	        		player.sendMessage("§6§l[OffhandAttack] §e- A special engrave was applied to your item. Now it can be swinged using second hand. Offhand damage for this item: " + args[2] + ". Cooldown for this item (in ticks): " + args[3] );
	        		return true;
	    		} else if (args[1].equals("remove")) {
	    			if (compound.containsKey("offhand_atk") && compound.containsKey("offhand_cd")) {
	    				compound.remove("offhand_atk");
	    				compound.remove("offhand_cd");
	    				NbtFactory.setItemTag(item, compound);
	    				player.sendMessage("§6§l[OffhandAttack] §e- A special engrave was removed from your item.");
		        		return true;
	    			} else {
	    				player.sendMessage("§6§l[OffhandAttack] §e- This item isn't engraved!");
	    			}
	    		} else {
	    			player.sendMessage("§6§l[OffhandAttack] §e- Invalid argument provided: " + args[1] + ". Use \"add\" or \"remove\".");
	    			return false;
	    		}
	    	} else {
	    		player.sendMessage("§6§l[OffhandAttack] §e- You don't have item in your hand!");
	    		return false;
	    	}
			return false;
	}
}
