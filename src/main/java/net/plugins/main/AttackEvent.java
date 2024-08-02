package net.plugins.main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import org.bukkit.Sound;

public class AttackEvent implements Listener {

	@EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Entity entity = event.getRightClicked();
		Random random = new Random();
		Location ent_loc = entity.getLocation();
		LivingEntity ent_liv = (LivingEntity) entity;
		ItemStack item = player.getInventory().getItemInOffHand();
		if (player.getCooldown(Material.KNOWLEDGE_BOOK) == 0) {
			if (weaponCheck(item)) {
				animateOffHand(player);
				if (invulnerableCheck(entity)) {
					float pit = 0.9f + (1.1f - 0.9f) * random.nextFloat();
					ent_loc.getWorld().playSound(ent_loc, Sound.ENTITY_PLAYER_ATTACK_NODAMAGE, 1.0f, pit);
				} else {
					player.setCooldown(Material.KNOWLEDGE_BOOK, getCdOffhand(item));
					if (getDmgOffhand(item) != 0) {
						if (isSweep(player)) {
							ent_liv.damage(getDmgOffhand(item), player);
						}
						
					}
				}
			}
		}
	}

	public void animateOffHand(Player player) {
		 PacketContainer packet = RedginaldPlugin.protocolManager.createPacket(PacketType.Play.Server.ANIMATION);
		 packet.getIntegers().write(0, player.getEntityId());
		 packet.getIntegers().write(1, 3); // 3 is code for second hand swing animation.
		 for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			 RedginaldPlugin.protocolManager.sendServerPacket(onlinePlayer, packet);
			 }
	    }

	private boolean invulnerableCheck(Entity entity) {
		if (entity instanceof Player) {
			if (((Player) entity).getGameMode() == GameMode.CREATIVE && ((Player) entity).getGameMode() == GameMode.SPECTATOR) {
				return true;
			} else {
				return false;
			}
		} else if (entity.isInvulnerable()) {
			return true;
		} else {
			return false;
		}
	}
	private boolean weaponCheck(ItemStack item) {
		if (item.getType() != Material.AIR && item.hasItemMeta()) {
			NbtCompound compound = NbtFactory.asCompound(NbtFactory.fromItemTag(item));
			if (compound.containsKey("offhand_atk")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	private double getDmgOffhand(ItemStack item) {
		NbtCompound compound = NbtFactory.asCompound(NbtFactory.fromItemTag(item));
		if (compound.containsKey("offhand_atk")) {
			return compound.getDouble("offhand_atk");
		} else {
			return 0;	
		}
	}
		
	private boolean isCrit(Player player) {
		double loc1 = player.getLocation().getY();
		try {
			Thread.sleep(200);
		} catch (InterruptedException  e) {}
		double loc2 = player.getLocation().getY();
		if (loc1 > loc2) {
			return true;
		} else {
			return false;
		}
	}
	private boolean isSweep(Player player) {
		if (!player.isSprinting()) {
			if (player.getVelocity().getY() == 0) {
				//if ()
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	private int getCdOffhand(ItemStack item) {
		NbtCompound compound = NbtFactory.asCompound(NbtFactory.fromItemTag(item));
		if (compound.containsKey("offhand_cd")) {
			return compound.getInteger("offhand_cd");
		} else {
			return 0;	
		}
	}
	/*
	private int[] getGeneralYmlData() {
		OffhandAttack plugin = (OffhandAttack) Bukkit.getPluginManager().getPlugin("OffhandAttack");

		File def_config = new File(plugin.getDataFolder(), "default_config.yml");
		Yaml yml = new Yaml();
		int[] arr = new int[2];
		try (InputStream inputStream = new FileInputStream(def_config)) {
			 Map<String, Object> data = yml.load(inputStream);
			 Object a = data.get("Attack speed penalty (INTEGER)");
			 Object d = data.get("Damage penalty (INTEGER)");
			 Object def = data.get("Default for all players (1 for true, 0 for false)");
			 arr[0] = (int) a;
			 arr[1] = (int) d;
			 arr[2] = (int) def;
		} catch (Exception e) {
		}
		return arr;
	}
	private int[] getPlayerYmlData(Player player) {
		OffhandAttack plugin = (OffhandAttack) Bukkit.getPluginManager().getPlugin("OffhandAttack");
		
		File def_config = new File(plugin.getDataFolder(), "default_config.yml");
		Yaml yml = new Yaml();
		int[] arr = new int[2];
		try (InputStream inputStream = new FileInputStream(def_config)) {
			 Map<String, Object> data = yml.load(inputStream);
			 Object pen_s = data.get("Attack speed penalty (INTEGER)");
			 Object pen_d = data.get("Damage penalty (INTEGER)");
			 Object def = data.get("Default for all players (1 for true, 0 for false)");
			 arr[0] = (int) pen_s;
			 arr[1] = (int) pen_d;
			 arr[2] = (int) def;
		} catch (Exception e) {
		}
		return arr;
	}
	*/
}
